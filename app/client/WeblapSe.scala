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
  driver.get(url);
  /***/ println(s"${Console.CYAN}driver.get kész: ${Instant.now}${Console.RESET}")
  driver.executeScript(s"history.replaceState({lap: ${inicPill.toEpochMilli}}, '');")
  /***/ println(s"${Console.CYAN}driver.executeScript kész: ${Instant.now}${Console.RESET}")
  docHtml = driver.getPageSource();
  /***/ println(s"${Console.CYAN}driver.getPageSource kész: ${Instant.now}${Console.RESET}")

  //linkek = linkek(); WeblapSeJ::linkek() baromi lassú
  linkek = """majd egyszer... ("eventuálisan!")"""
  var linkekList = driver.findElements(By.cssSelector("a[href], [onclick]")).asScala
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

  inicEredm = "¤ajaxfeldolg¤" + WeblapSe.feldolgHtml + "¤lapcim¤" + <pre>Lapcím: {lapcim}</pre>

//////// KONSTRUKTOR idáig; innentől csak def-ek  vannak

  def elembolHtml(elem: WebElement): String =
  {
    val absHref = Option(elem.getAttribute("href")) getOrElse "[nincs href]"
    val relHref = Option(driver.executeScript("return arguments[0].getAttribute('href');", elem).asInstanceOf[String]) getOrElse "[nincs href]"
    var absHrefHa = ""
    if (relHref.replaceAll("/", "") != absHref.replaceAll("/", "")) absHrefHa = s"($absHref)"
    var gombHa = s"""<button class=alacsonygomb onclick='inicajaxhivas("weblapajaxinic?url=$absHref&s=$s")'>nyomjad</button>"""
    if (relHref == "[nincs href]") gombHa = "<button class=alacsonygomb disabled>ne nyomjad</button>"
    val onclickTmp = s"""feldolgajaxhivas(${inicPill.toEpochMilli}, "&muvelet=lapReszl&par=${linkekList.indexOf(elem)}"); document.querySelector("#checkbox15").click()"""
    val reszlGomb = s"<button class=alacsonygomb onclick='$onclickTmp'>több</button>"
    s"<div>$relHref $absHrefHa $gombHa $reszlGomb</div>"
  }

  var kattintanivalok = kattintaniValok
    //**/ println(s"konstruáláskor kattintanivalok=$kattintanivalok...?")
  // seInic-be való lenne a def., de ott korai - itt rend akkor lesz, ha az egész átjön scalába

  def kattintaniValok() =
    "kattintanivalók"

  override def seInic = {}  // ezt a baromságot kiveszem innét
  /*{
    //super.seInic
    //println("WeblapSe.scala seInic meghívva")  //oké, ezt hívja meg a "konstruktor"

    //előlről
    /**/ println("driver.get hívás előtt url="+url)
    //**/ println("feldolgHtml="+WeblapSe.feldolgHtml)  //null, ha az osztályban van (és nem "static")
    //driver.ujAblakba  //ez itt piszok rossz helyen van --> végtelen ciklus ...hacsak másképp nem csinálom
    //if (url!="about:blank") driver.ujAblakba  //de ezt meg kéne tisztábban csinálni! TERV!
    driver.get(url);
    docHtml = driver.getPageSource();
    linkek = linkek(); // ezt inickor kell
    //kattintanivalok = kattintaniValok()   itt korai, mert előbb hajtódik végre, mint a kattintanivalok definiálása
    //**/ println(s"kattintanivalok=$kattintanivalok...mé?")

    inicEredm = "¤ajaxfeldolg¤" + WeblapSe.feldolgHtml + "¤lapcim¤" + <pre>Lapcím: {driver.getTitle}</pre>

  }*/

/*
 * Másodlagos konstruktor duplikáláshoz
 */
  def this (w1: WeblapSe)
  {
    this(WeblapSe.wParamUjra(w1.url, w1.s), w1.driver)
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
  { // ha lesz többféle művelet, akkor ide match
    s"kattintanivalok¤${elembolHtmlReszl(par.toInt)}"
  }

  def elembolHtmlReszl (elemindex: Int) =
  {
    val elem = linkekList(elemindex)
    s"$elemindex. elem meg van kattintva"
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
/*"a hülye atom miatt*/

  def reszletesStatusz =
    <div>inicPill={inicPill}</div>
    <div>lapcím={lapcim}</div>
    <div>url={url}</div>
    <div>drURL={drURL}</div>
    <div>ablak={ablak}</div>
    <div>histSorsz={histSorsz}</div>

/*
  override //üres ős a Weblap-ban
  def katt =
  {

    inicPill + " meg van kattintva"
  }

  override //üres ős a Weblap-ban
  def katt (xpath: String) =
  {
    //new WeblapSe(this)
    //driver.findElement(org.openqa.selenium.By.xpath(xpath)).click
    inicPill + " meg van kattintva, xpath=" + xpath
  }
*/
}

object WeblapSe
{
  val feldolgHtml/*: scala.xml.Elem*/ =  //ez megy inic-kor az ajaxfeldolg-ba, és a feldolg ennek részeibe ír
<div>
  <pre id="lapcim"></pre>
    <div class="tabs">
    <div class="tab">
      <input name="checkbox-tabs-group" type="radio" id="checkbox10" class="checkboxtab" checked=""></input>
      <label for="checkbox10">Linkek</label>
      <div id="linkek" class="content" style="overflow-y: auto;">
      </div>
    </div>

    <div class="tab">
      <input name="checkbox-tabs-group" type="radio" id="checkbox15" class="checkboxtab"></input>
      <label for="checkbox15">Kattintanivalók</label>
      <div id="kattintanivalok" class="content">
      </div>
    </div>

    <div class="tab">
      <input name="checkbox-tabs-group" type="radio" id="checkbox20" class="checkboxtab"></input>
      <label for="checkbox20">Kép</label>
      <div id="kep" class="content">
      </div>
    </div>

    <div class="tab">
      <input name="checkbox-tabs-group" type="radio" id="checkbox30" class="checkboxtab"></input>
      <label for="checkbox30">Forrás</label>
      <div id="forras" class="content">
      </div>
    </div>
  </div>
</div>

  def wParamUjra (pUrl: String, pS: String, pPill: Instant = Instant.now): java.util.Map[String, Array[String]] =
  {
    var wp: java.util.Map[String, Array[String]] = new java.util.HashMap()
    wp.put("url", Array(pUrl))
    wp.put("s", Array(pS))
    wp.put("pill", Array(pPill.toEpochMilli.toString))
    wp
  }
}
