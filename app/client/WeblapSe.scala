package client

import java.time.Instant

import ru.yandex.qatools.ashot.AShot
import ru.yandex.qatools.ashot.Screenshot
import ru.yandex.qatools.ashot.shooting.ShootingStrategies
import java.io.File
import javax.imageio.ImageIO
import java.io.PrintWriter
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import collection.JavaConverters._

import models.WeblapModell

import Weblap._  // wParamUjra miatt (enélkül csak Weblap.wParamUjra -ként lehet hivatkozni rá)

class WeblapSe (wParams: java.util.Map[String, Array[String]], dr: SajatDriver) extends WeblapSeJ(wParams, dr)
{
  //super(wParams, dr) nem kell, az extends-be kell írni
  
  //Weblap (super.super): wParams-ból helyretette url-t és s-t
  //WeblapSeJ (super): dr-t és wParams-ból pill-t (inicPill-be) (ha volt, különben 0L marad) helyretette;
  //  semmi mást, mert a seInic-et itt üresre írom felül (meg még a konzolra kiír, de azt jól teszi)
  //de WeblapModell.inicAjax mindig tesz pill-t wParams-ba

  //Ha van kopp= paraméter a wParams-ban: (akkor egy meglévő WeblapSe pill-je van benne):
  //  akkor meg kell duplikálni a böngészőablakot, és url-be annak a lapnak az url-jét kell tenni
  //  (innentől a klon meg a másodkonstruktor felesleges?)

//////// KONSTRUKTOR //////////////////

  ( //(pontosvessző-okoskodás elleni zárójel)
    Option(wParams.get("kopp")).foreach
      (pill =>
        {
          driver.ujAblakba(None)
          url = WeblapModell.tartosWeblapok(pill(0).toLong).url
        }
      )
  )

  /***/ println(s"${Console.CYAN}driver.get kezd: ${Instant.now}${Console.RESET}")
  if (url.length>0)  //ha meg üres, a lap már be van töltve a böngészőbe, és ahhoz kell új példány (ld. másodlagos konstruktor(dr))
  {
    driver.get(url);  //állítólag így kell; enélkül InvalidCookieDomainException: Document is cookie-averse
    kukikAlk
    driver.get(url);
  }
  /***/ println(s"${Console.CYAN}driver.get kész: ${Instant.now}${Console.RESET}")
  driver.executeScript(s"history.replaceState({lap: ${inicPill.toEpochMilli}}, '');")
  /***/ println(s"${Console.CYAN}driver.executeScript kész: ${Instant.now}${Console.RESET}")
  docHtml = driver.getPageSource();
  /***/ println(s"${Console.CYAN}driver.getPageSource kész: ${Instant.now}${Console.RESET}")

  //linkek = linkek(); WeblapSeJ::linkek() baromi lassú
  linkek = """majd egyszer... ("eventuálisan!")"""
  var linkekList = linkedListFn //driver.findElements(By.cssSelector("a[href], [onclick]")).asScala
  linkek = linkekList.map(elembolHtml).foldLeft("")(_+_)
  /***/ println(s"${Console.CYAN}linkekList kész: ${linkekList.size} db elem ${Instant.now}${Console.RESET}")

  var drURL = driver.getCurrentUrl  //a Weblap-ban már van egy url, a wParams-ban kapott
  /***/ println(s"${Console.CYAN}driver.getCurrentUrl kész: $drURL ${Instant.now}${Console.RESET}")
  var lapcim = driver.getTitle
  /***/ println(s"${Console.CYAN}driver.getTitle kész: $lapcim ${Instant.now}${Console.RESET}")
  var ablak = driver.getWindowHandle
  /***/ println(s"${Console.CYAN}driver.getWindowHandle kész: $ablak ${Instant.now}${Console.RESET}")
  //***/ println(s"${Console.CYAN}drURL=$drURL lapcim=$lapcim ablak=$ablak ${Instant.now}${Console.RESET}")
  driver.ablakok += (ablak -> this)  //szintén a seInic-ből szorult ki
  var histSorsz = driver.histHossz;  //hányadik az ablak históriájában

  var kukik = driver.manage.getCookies.asScala

  inicEredm = inicEredmFn  //függvénybe, ami felülírható

//////// KONSTRUKTOR idáig; innentől csak def-ek  vannak

  // paraméterben jött kukik alkalmazása - nem függvény, procedúra; ez teszi bele, mert csak egyenként lehet
  def kukikAlk =
  {
    //var q = wParams.get("kuki") //Array[String]   [ "PHPSESSID"]  vagy null, mert javás Map
    var q = Option(wParams.get("kuki")).getOrElse(Array())
    /**/ println(s"wParams:$wParams kukik:$q")
    (
      q.foreach
      { nev =>
        {
          var ert = wParams.get(nev)(0)
          /**/ println(s"név=$nev érték=$ert")
          driver.manage.deleteCookieNamed(nev)  //különben két olyan kuki lesz, és az eddigi lehet érvényes továbbra is
          driver.manage.addCookie(new org.openqa.selenium.Cookie(nev, ert))
        }
      }
    )
  }

  def elembolHtml(elem: WebElement): String =
  {
    val absHref = Option(elem.getAttribute("href")) getOrElse "[nincs href]"
    val relHref = Option(driver.executeScript("return arguments[0].getAttribute('href');", elem).asInstanceOf[String]) getOrElse "[nincs href]"
    var absHrefHa = ""
    if (relHref.replaceAll("/", "") != absHref.replaceAll("/", "")) absHrefHa = s"($absHref)"
    var gombHa = s"""<button class=alacsonygomb onclick='inicajaxhivas("weblapajaxinic?url=$absHref&s=$s")'>nyomjad</button>"""
    if (relHref == "[nincs href]") gombHa = "<button class=alacsonygomb disabled>ne nyomjad</button>"
    val onclickTmp = s"""feldolgajaxhivas(${inicPill.toEpochMilli}, "&muvelet=lapReszl&par=${linkekList.indexOf(elem)}"); document.querySelector("#kattbox").click()"""
    val reszlGomb = s"<button class=alacsonygomb onclick='$onclickTmp'>több</button>"
    s"<div>$relHref $absHrefHa $gombHa $reszlGomb</div>"
  }

  var kattintanivalok = kattintaniValok
    //**/ println(s"konstruáláskor kattintanivalok=$kattintanivalok...?")
  // seInic-be való lenne a def., de ott korai - itt rend akkor lesz, ha az egész átjön scalába

  def kattintaniValok() =
    "kattintanivalók"

  def linkedListFn = driver.findElements(By.cssSelector("a[href], [onclick]")).asScala

  def linkedListTarol = linkekList = linkedListFn

  def inicEredmFn = "¤ajaxfeldolg¤" + feldolgHtml + "¤lapcim¤" + <pre>Lapcím: {lapcim}</pre>

  override def seInic = {}  // ezt a baromságot kiveszem innét

/*
 * Másodlagos konstruktor duplikáláshoz
 */
  def this (w1: WeblapSe)
  {
    this(wParamUjra(w1.url, w1.s), w1.driver)
  }
/*
 * Másodlagos konstruktor a már böngészőbe betöltött laphoz
 */
  def this (dr: SajatDriver)
  {
    this(wParamUjra("", "Se"), dr)
  }

  def klon = // duplikátum új böngészőablakba, új WeblapModell.tartosWeblapok elembe
  {
    driver.ujAblakba(Some(this))  //új böngészőablak
    var lap = new WeblapSe(this)  //duplikátum, ami be is teszi az új ablakot ablakok-ba
    var most = Instant.now
    lap.setInicPill(most)
    //WeblapModell.tartosWeblapok.put(most.toEpochMilli, lap)        vagy: (elvégre nem a nyomorék java-ban vagyunk)
    WeblapModell.tartosWeblapok += most.toEpochMilli -> lap
  }

  override def feldolg(pp: String*) =
  {
    pp.length match
    {
      case 2 => feldolg2par(pp(0), pp(1))
      case _ => simaFeldolg
    }
  }

  def simaFeldolg =
  {
    feldolgPill = Instant.now
    val pill = feldolgPill.toEpochMilli
    /* */ println("WeblapSe.feldolg() meghívva, feldolgPill = " + pill + " " + feldolgPill)

    var kepfajl = "screenshot" + inicPill + ".png"
    var htmlfajl = "drpagesource" + inicPill + ".html"

    if (tmpKepFajl.isEmpty())
    {
      var fpScreenshot: Screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver)
      ImageIO.write(fpScreenshot.getImage(),"PNG",new File(WeblapSeJ.tmpDir + kepfajl))
      tmpKepFajl = kepfajl
    }else
      kepfajl = tmpKepFajl + "#" + pill  //ua. a fájl friss címmel, hogy ne a cache-ből tudjisten mit vegyen elő

    if (tmpHtmlFajl.isEmpty())
    {
      val writer = new PrintWriter(new File(WeblapSeJ.tmpDir + htmlfajl))
      writer.write(docHtml)
      writer.close()
      tmpHtmlFajl = htmlfajl
    }else
      htmlfajl = tmpHtmlFajl + "#" + pill

    //**/ println(s"feldolgkor kattintanivalok=$kattintanivalok...mé?")
    var ret = "linkek¤" + linkek
    ret += "¤kattintanivalok¤" + kattintanivalok
    ret += "¤kep¤" + s"""<img src=assets/tmp/$kepfajl></img>"""
    ret += "¤forras¤" + s"""<iframe src=assets/tmp/$htmlfajl style="flex-grow: 1; width: 100%;"></iframe>"""  //hát még ha attribútum írására is fel lenne készülve a js oldal
    ret
  }

  def feldolg2par(muv:String, par:String) =
  {
    muv match
    {
      case "Reszl" => s"kattintanivalok¤${elembolHtmlReszl(par.toInt)}"
      case "Katt" => katt(par.toInt)
    }
  }

  def elembolHtmlReszl (elemindex: Int) =
  {
    val elem = linkekList(elemindex)
    val jsMiEz = xml.Utility.escape(driver.executeScript("return arguments[0].outerHTML", elem).asInstanceOf[String])
    val onclickKattTmp = s"""feldolgajaxhivas(${inicPill.toEpochMilli}, "&muvelet=lapKatt&par=$elemindex")"""
    val kattGomb = <button class='alacsonygomb' onclick={onclickKattTmp}>katt</button>

    val elembolHtmlReszlLink =
    {
      Option(elem.getAttribute("href")) match
      {
        case None => ""
        case Some(absHref) => 
        {
          val relHref = driver.executeScript("return arguments[0].getAttribute('href');", elem).asInstanceOf[String]   //felt.: ha van absHref, akkor relHref is van, nem kell opciózni
          val absHrefHa = if (relHref.replaceAll("/", "") != absHref.replaceAll("/", "")) s"($absHref)" else ""
          val onclickTmp = s"""inicajaxhivas("weblapajaxinic?url=${absHref}&s=${s}")"""
          val gomb = <button onclick={onclickTmp}>nyomjad</button>
          s"$relHref $absHrefHa $gomb"
        }
      }
    }

    s"$elemindex. elem: $jsMiEz $kattGomb $elembolHtmlReszlLink"
  }

  def katt(elemindex: Int) =
  {
    linkekList(elemindex).click  //továbbra sem javasolt linknél; betölti az új lapot, de WeblapSe objektumot nem konstruál
    val feldolgEredm =
      if (drURL == driver.getCurrentUrl)  //maradtunk a lapon, de újra"feldolg"ozni nem árt, mert tudjisten mi történt a DOM-ban (lehet, hogy csak onclick esetben kellene)
      {
        /* if (stale) */
        {
          linkedListTarol
          driver.executeScript(s"history.replaceState({lap: ${inicPill.toEpochMilli}}, '');")  //ugyanis ez is eltűnik ilyenkor
        } 
        simaFeldolg
      }else //akkor meg új példány kell
      {
        val lap = new WeblapSe(driver)
        WeblapModell.tartosWeblapok += lap.getInicPill.toEpochMilli -> lap
        lap.simaFeldolg
      }
    s"$feldolgEredm¤uzen¤$elemindex. elem meg van kattintva"
  }

  override // Weblap-é az ős
  def statusz = s"$s $feldobosStatusz $lapcim"

  def feldobosStatusz =
    <div class="feldobos">
      <button class="alacsonygomb">+</button>
      <div class="feldobosbavalo">
        {reszletesStatusz}
      </div>
    </div>

  def reszletesStatusz =
    <div>inicPill={inicPill}</div>
    <div>lapcím={lapcim}</div>
    <div>url={url}</div>
    <div>drURL={drURL}</div>
    <div>ablak={ablak}</div>
    <div>histSorsz={histSorsz}</div>
    <div>{kukik.map(k => <div>{k.toString}</div>)}</div>

  def fulElem (inputID: String, divID: String, szoveg: String, pipa: Boolean = false)/*: scala.xml.Elem*/ =
    <div class="tab">
      <input name="checkbox-tabs-group" type="radio" id={inputID} class="checkboxtab" checked={if (pipa) Some(xml.Text("pipa")) else None}></input>
      <label for={inputID}>{szoveg}</label>
      <div id={divID} class="content" style="overflow-y: auto;">
      </div>
    </div>
     // a null-kerülési kényszer miatt ilyen a checked ...meg amiatt, hogy az egész xml.Elem, ahelyett, hogy String lenne
    // ha tényleg csak az első/pipázott fülnek kell "overflow-y: auto"-nak lennie (mint ahogy a fulElem függvényesítés előtt volt), akkor:
    //      <div id={divID} class="content" style={if (pipa) Some(xml.Text("overflow-y: auto;")) else None}>

  def feldolgHtml/*: scala.xml.Elem*/ =  //ez megy inic-kor az ajaxfeldolg-ba, és a feldolg ennek részeibe ír
<div>
  <pre id="lapcim"></pre>
  <div class="tabs">
    {fulElem("linkbox", "linkek", "Linkek", pipa=true)}
    {fulElem("kattbox", "kattintanivalok", "Kattintanivalók")}
    {fulElem("kepbox", "kep", "Kép")}
    {fulElem("forrbox", "forras", "Forrás")}
  </div>
</div>
}
