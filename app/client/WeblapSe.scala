package client

import java.time.Instant

import ru.yandex.qatools.ashot.AShot
import ru.yandex.qatools.ashot.Screenshot
import ru.yandex.qatools.ashot.shooting.ShootingStrategies
import java.io.File
import javax.imageio.ImageIO
import java.io.PrintWriter

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
  ( //(pontosvessző-okoskodás elleni zárójel)
    Option(wParams.get("kopp")).foreach
      (pill =>
        {
          driver.ujAblakba(None)
          url = WeblapModell.tartosWeblapok(pill(0).toLong).url
        }
      )
  )

  driver.get(url);
  docHtml = driver.getPageSource();
  linkek = linkek();

  var kattintanivalok = kattintaniValok
    //**/ println(s"konstruáláskor kattintanivalok=$kattintanivalok...?")
  // seInic-be való lenne a def., de ott korai - itt rend akkor lesz, ha az egész átjön scalába

  def kattintaniValok() =
    "kattintanivalók"

  var drURL = driver.getCurrentUrl  //a Weblap-ban már van egy url, a wParams-ban kapott
  var lapcim = driver.getTitle
  var ablak = driver.getWindowHandle
  driver.ablakok += (ablak -> this)  //szintén a seInic-ből szorult ki

  inicEredm = "¤ajaxfeldolg¤" + WeblapSe.feldolgHtml + "¤lapcim¤" + <pre>Lapcím: {lapcim}</pre>

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

  override def feldolg =
  {
    feldolgPill = Instant.now
    val pill = feldolgPill.toEpochMilli
    /* */ println("WeblapSe.feldolg() meghívva " + pill + " " + feldolgPill)

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
