package client

import zz.app.models._
// SZIPU!! abban a .jar-ban is van views.html.weblapajaxfeldolg[$].class, meg emitt is! Ütközik! -> át kellett nevezni weblapajaxfeldolg.scala.html-t
import Weblap._

class WeblapSeEro (wParams: java.util.Map[String, Array[String]], dr: SajatDriver) extends WeblapSe(wParams, dr)
{
  override def inicEredmFn = "¤ajaxfeldolg¤" + feldolgHtml + "¤lapcim¤" + <pre>Lapcím: {lapcim}</pre> + "¤ero¤" + eroFeldolg
    //már csak az erofeldolg miatt override, a feldolgHtml-től még jó lenne a WeblapSe inicEredmFn-je

  def eroFeldolg = //ez megy az Ero fülre
  {
    //"eróóóóó" 
    EroZZ.bodyKiirTitle(wParamUjra("en", "SeEro"))
  }

  override def feldolgHtml/*: scala.xml.Elem*/ =  //ez megy inic-kor az ajaxfeldolg-ba, és a feldolg ennek részeibe ír
<div>
  <pre id="lapcim"></pre>
  <div class="tabs">
    {fulElem("linkbox", "linkek", "Linkek", pipa=true)}
    {fulElem("kattbox", "kattintanivalok", "Kattintanivalók")}
    {fulElem("erobox", "ero", "Ero")}
    {fulElem("kepbox", "kep", "Kép")}
    {fulElem("forrbox", "forras", "Forrás")}
  </div>
</div>

}