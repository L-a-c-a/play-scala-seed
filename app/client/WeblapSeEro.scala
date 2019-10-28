package client

import WeblapSeEro._
class WeblapSeEro (wParams: java.util.Map[String, Array[String]], dr: SajatDriver) extends WeblapSe(wParams, dr)
{
  override def inicEredmFn = "¤ajaxfeldolg¤" + feldolgHtml + "¤lapcim¤" + <pre>Lapcím: {lapcim}</pre> + "¤ero¤" + eroFeldolg

  def eroFeldolg = //ez megy az Ero fülre
  {
    "eróóóóó"
  }
}

//import WeblapSe._  //öröklés objektumok között - nem jó, ha ugyanúgy hívják, rekurzió
object WeblapSeEro
{
  def fulElem (inputID: String, divID: String, szoveg: String, pipa: Boolean = false) = WeblapSe.fulElem (inputID, divID, szoveg, pipa)
  // a paraméterlista kell, mert anélkül nem tudja a default értéket és nem ismeri a par. nevét (nem lehet pipa=true-val hívni)
  // a másik oldalon meg nem műxenek a _-ok, mert úgy (...) => xml.Elem lesz és nem (...)xml.Elem (ld. függvény és metódus közti különbség)
  //  ...szívás volt ezeket objektumba rakni

  /*override*/ val feldolgHtml/*: scala.xml.Elem*/ =  //ez megy inic-kor az ajaxfeldolg-ba, és a feldolg ennek részeibe ír
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