package client

import java.time.Instant

import ru.yandex.qatools.ashot.AShot
import ru.yandex.qatools.ashot.Screenshot
import ru.yandex.qatools.ashot.shooting.ShootingStrategies
import java.io.File
import javax.imageio.ImageIO
import java.io.PrintWriter

class WeblapSe (wParams: java.util.Map[String, Array[String]], dr: SajatDriver) extends WeblapSeJ(wParams, dr)
{
  //super(wParams, dr) nem kell, az extends-be kell írni

  override def seInic =
  {
    //super.seInic
    //println("WeblapSe.scala seInic meghívva")  //oké, ezt hívja meg a "konstruktor"
    
    //előlről
    /**/ println("driver.get hívás előtt url="+url)
    /**/ println("feldolgHtml="+WeblapSe.feldolgHtml)  //null, ha az osztályban van (és nem "static")
    driver.get(url);
    docHtml = driver.getPageSource();
    linkek = linkek(); // ezt inickor kell
    
    inicEredm = "¤ajaxfeldolg¤" + WeblapSe.feldolgHtml + "¤lapcim¤" + <pre>Lapcím: {driver.getTitle}</pre>

  }
  
  override def feldolg = 
  {
    feldolgPill = Instant.now
    val pill = feldolgPill.toEpochMilli
    /**/ println("WeblapSe.feldolg() meghívva " + pill + " " + feldolgPill)

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

    var ret = "linkek¤" + linkek
    ret += "¤kep¤" + s"""<img src=assets/tmp/$kepfajl></img>"""
    ret += "¤forras¤" + s"""<iframe src=assets/tmp/$htmlfajl style="flex-grow: 1; width: 100%;"></iframe>"""  //hát még ha attribútum írására is fel lenne készülve a js oldal
    ret
  }
}

object WeblapSe
{
  val feldolgHtml/*: scala.xml.Elem*/ =  //ez megy inic-kor az ajaxfeldolg-ba, és a feldolg ennek részeibe ír
<div>
  <pre id="lapcim"></pre>
    <div class="tabs">
    <div class="tab">
      <input name="checkbox-tabs-group" type="radio" id="checkbox1" class="checkboxtab" checked=""></input>
      <label for="checkbox1">Linkek</label>
      <div id="linkek" class="content" style="overflow-y: auto;">
      </div>
    </div>
    
    <div class="tab">
      <input name="checkbox-tabs-group" type="radio" id="checkbox2" class="checkboxtab"></input>
      <label for="checkbox2">Kép</label>
      <div id="kep" class="content">
      </div>
    </div>
    
    <div class="tab">
      <input name="checkbox-tabs-group" type="radio" id="checkbox3" class="checkboxtab"></input>
      <label for="checkbox3">Forrás</label>
      <div id="forras" class="content">
      </div>
    </div>
  </div>
</div>

}

