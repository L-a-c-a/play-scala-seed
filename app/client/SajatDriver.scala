package client;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.Proxy

import collection.JavaConverters._

import models.WeblapModell._

trait SajatDriver extends WebDriver with JavascriptExecutor with TakesScreenshot
{
	//def sajatProxy = SajatDriver.sajatProxyVal //ezt static kontextusból nem lehet hívni - marad a társobjektumban
  //var ablakok: collection.mutable.Set[String] //= collection.mutable.Set()  //getWindowHandle(s) adta azonosítóknak
  var ablakok: collection.mutable.Map[String, WeblapSe]
  //ettől csak egy ablakok() meg egy ablakok_$eq(Set) lesz felülírnivaló, ablakok változó nem

  var histIndex: Integer  //ennyivel vagyunk visszább a históriában 

  def ablakStatusz(abl: String): scala.xml.Elem = // hátha így kell hívni valahonnan... de akkor mért nem inkább default-os második paraméter? ...mert abban nem lehet az első par.-re hivatkozni
  {
    //var lap = ablakok(abl) 
    ablakStatusz(abl, ablakok(abl))
  }

  def ablakStatusz(abl: String, lap: WeblapSe) = // história hossza, előre, vissza, csukógomb...
  {
    switchTo.window(abl)
    <div>
    {abl}: {lap.driver.getCurrentUrl}
    | história hossza: {histHossz}
    | akt. históriaelem-státusz: {aktHistStat}
    <!-- | akt. históriaelem-státusz.lap: {aktHistStatLap} -->
    | ennyivel visszább a históriában: {histIndex}
    <button type="button" onclick={s"feldolgajaxhivas($abl, '&muvelet=ablakVissza&par=$abl')"} disabled={vaneVissza}>&lt;Vissza</button>
    <button type="button" onclick={s"feldolgajaxhivas($abl, '&muvelet=ablakFrissit')"}>Frissít</button>
    <button type="button" onclick={s"feldolgajaxhivas($abl, '&muvelet=ablakElore&par=$abl')"} disabled={vaneElore}>Előre&gt;</button>
    <button type="button" onclick={s"feldolgajaxhivas($abl, '&muvelet=ablakCsuk&par=$abl')"} disabled={vanemegAblak}>Csuk</button>
    <button type="button" onclick={s"feldolgajaxhivas($abl, '&muvelet=ablakUjLap&par=$abl')"}>Új lap</button>
    </div>
    // feldolggomb hülyeség volt, de így meg a par felesleges (Frissit új, annál már nincs)
  }

  /* ablakStatusz alfunkciók
   * 
   */
  def histHossz = executeScript("return history.length;").asInstanceOf[Long]

  def aktHistStat = executeScript("return history.state;")  // a WeblapSe konstruáláskor rögtön beleír egy {lap: $Pill} alakú objektumot

  def aktHistStatLap = executeScript("return history.state.lap;").asInstanceOf[Long]

  def vaneVissza: Option[xml.Text] = //mert egész attribútumot nem tehetek {}-be a literálban, de ha None-t vagy Null-t kap, eltünteti az attribútumot
  {
    /**/ println (s"histHossz=$histHossz  histIndex=$histIndex  különbség=${histHossz - histIndex}")
    if (histHossz - histIndex > 1) None  //mert akkor van hova visszalépni
    else  Some(xml.Text("disabled"))
  }

  def vaneElore: Option[xml.Text] = 
    if (histIndex == 0) Some(xml.Text("disabled"))
    else None

  def vanemegAblak: Option[xml.Text] = 
    if (getWindowHandles.size > 1) None
    else  Some(xml.Text("disabled"))

  def ablakMuv (pill: Long, muv: String, par: String): String =
  {
    var ret = s"QQQQQQQQQQ ablakMuv pill=$pill muv=$muv par=$par"
    println(ret)
    muv match
    {
      case "Vissza" => ablakVissza(par)
      case "Frissit" => ablakFrissit(pill)
      case "Elore" => ablakElore(par)
      case "Csuk" => ablakCsuk(par)
      case "UjLap" => ablakUjLap(par)
      case _ => "uzen¤" + ret
    }
  }

  def ablakVissza (abl: String) = // history.back, histIndex+=1, history.state.lap indexű lapot kivenni tartosWeblapok-ból és feldolg
    //s"uzen¤$abl vissza eggyel"
  {
    executeScript("history.back();")
    histIndex+=1
    if (tartosWeblapok.contains(aktHistStatLap)) tartosWeblapok(aktHistStatLap).feldolg() + s"¤uzen¤$abl vissza eggyel"
    else s"uzen¤nincs $aktHistStatLap a listában"
  }

  def ablakFrissit(abl: Long) =
  {
    executeScript("history.go();")
    if (tartosWeblapok.contains(aktHistStatLap))
    {
      val lap = tartosWeblapok(aktHistStatLap).asInstanceOf[WeblapSe]
      lap.linkedListTarol
      lap.feldolg() + s"¤uzen¤$abl frissítve (go() és linkedListTarol)"
    }
    else s"uzen¤nincs $aktHistStatLap a listában"
  }

  def ablakElore (abl: String) =
  {
    executeScript("history.forward();")
    histIndex += -1
    //tartosWeblapok.applyOrElse(aktHistStatLap, default).feldolg()
    if (tartosWeblapok.contains(aktHistStatLap)) tartosWeblapok(aktHistStatLap).feldolg() + s"¤uzen¤$abl előre eggyel"
    else s"uzen¤nincs $aktHistStatLap a listában"
  }

  def ablakCsuk (abl: String) /* bőngészőablakot bezárni, és a tartosWeblapok-ból kitörölni (vagy elárvítani: ablak=""), aminek ez az ablak-a */ =
  {
    /***/println("ablakCsuk getWindowHandles.size="+ getWindowHandles.size)
    /***/println("ablakCsuk getWindowHandle="+ getWindowHandle)
    close
    //...egy másik "ablakot" kell aktuálissá tenni, ha még van
    //ja, és a saját listából meg kiszedni!
    ablakok -= abl
    /***/println("ablakCsuk getWindowHandles.size="+ getWindowHandles.size)
    switchTo.window(getWindowHandles.asScala.head)
    /***/println("ablakCsuk getWindowHandle="+ getWindowHandle)
    //...
    s"uzen¤$abl csukva"  // ajaxstatusz¤ nem jó, mert felülíródik
  }

  def ablakUjLap (abl: String) = 
  {
    lap = new WeblapSe(this)
    tartosWeblapok += lap.getInicPill.toEpochMilli -> lap
    lap.feldolg() + s"¤uzen¤$abl: új lap: ${lap.getInicPill.toEpochMilli}"
  }

  def statusz: scala.xml.Elem    //ezeket a Sajat*Driver osztályokban kell implementálni... vagy itt lehet egy default implementáció
  =
    <div>
    <h6>Aktuális ablak</h6>
    <span>{getWindowHandle + ": " + getCurrentUrl}</span>
    <h6>Többi ablak (getWindowHandles)</h6>
    {
      getWindowHandles.asScala.map(abl => <div>{abl}</div>)//.foldLeft("")(_+_)
    }
    <h6>Többi ablak (ablakok)</h6>
    {
      var ablMent = getWindowHandle          //menteni és visszaállítani az ablakot! mert ablakStatusz elállítja!
      var ret = ablakok.map { case (abl, lap) => ablakStatusz(abl, lap) }
      // vagy: ablakok.keys.map(ablakStatusz(_))
      switchTo.window(ablMent)
      ret
    }
    <h6>Többi meghajtó (statikus)</h6>
    { SajatDriver.statikusStatusz }
    </div>
    //há'szen elérhető itt a példány (annak a getWindowHandle-je, getCurrentUrl-ja)! mi a probléma?

/*
  def inic: Unit =
		{
			println ("*********** SajatDriver inicializálva")
		}
*/

/*
 * keres egy használatlan (ablakok-ban meg nem levő) ablak-azonosítót (az elején biztos van olyan)
 * - ha nem talál, csinál -,
 * beleteszi ablakok-ba,
 * és átkapcsol arra az ablakra (további get-ek stb. arra vonatkoznak)
 * ..................nem jó, ketté kell választani, (ha nem háromfelé) 1. ablakok-ba be (de ez könnyebb a lap konstruálásakor), 2. új üres ablak (3. lap duplikálás új ablakba)
 */
  def ujAblakba (oLap: Option[WeblapSe] = None) =
  {
    var htlanAbl = hasznalatlanAblak
    /**/ println("AAAAA htlanAbl=" + htlanAbl)
    if (htlanAbl.isEmpty) //most jön Jégtörő Mátyás
    {
      //executeScript("window.open('', '');")  //nyit egy új ablakot (vagy fület?) about:blank-kal
      //executeScript("window.open(window.location, '');")  //...vagy megduplikálja a mostanit
      var jsWinPar = if (oLap.isEmpty) "''" else "window.location"  //nem mintha nem lenne mindegy
      executeScript(s"window.open($jsWinPar, '');")
      htlanAbl = hasznalatlanAblak   //a most nyitott ablak
      /**/ println("AAAAB htlanAbl=" + htlanAbl)
    }
    /* áááállj... ezt a konstruktor is megcsinálja!
    ablakok += (htlanAbl -> { if(oLap.isEmpty) 
                                new WeblapSe( Map( "url" -> Array("about:blank")
                                                 , "s" -> Array("Se")
                                                 ).asJava
                                            , this
                                            )
                              else
                                new WeblapSe(oLap.get)
                            }
               )
    */
    switchTo.window(htlanAbl)
    //ezután kell konstruálni
  }

  def hasznalatlanAblak = (getWindowHandles.asScala diff ablakok.keySet).headOption getOrElse ""
    //a két halmaz különbségének az "első" eleme (hogy melyik a max. 1 közül...)
  
  //def ablakokKoze (lap: WeblapSe) = // ha még getWindowHandle nincs az ablakok között, felvenni oda lap-ot ............kell??


}

object SajatDriver
{
	val sajatProxy = new Proxy()
			.setHttpProxy("szusza:8118")
			.setSslProxy("szusza:8118")
			.setNoProxy("localhost, 127.0.0.1, szusza, pici, laca.no-ip.hu, 192.168.0.0/16");
	// a Proxy "univerzális", csak a driver-hez rendelés driver-függő

	object DrTip extends Enumeration
	{
		type DrTip = Value  //ez meg az import kell a meghajtok Map deklarálásához
		val FFDR = Value
		val HUDR = Value
		//... stb, PHDR (Phantom), CHRDR, ...
	}
	import DrTip._
	var meghajtok: scala.collection.mutable.Map[DrTip, SajatDriver] = scala.collection.mutable.Map.empty
	//var meghajtoTipusok: scala.collection.mutable.Map[DrTip, SajatDriver.type] =
	
	def meghajto = meghajtok(aktMeghajtoTipus)

	def ujMeghajtoTipusSzerint(tip: DrTip): SajatDriver =
	{
		tip match
		{
			case FFDR => new SajatFirefoxDriver
			case HUDR => new SajatHtmlUnitDriver
			//...
		}
	}
	//éppen ezt tanítják apply címszó alatt: http://allaboutscala.com/tutorials/chapter-5-traits/scala-traits-companion-object-factory-pattern/
	//def apply(tip: DrTip): SajatDriver = ujMeghajtoTipusSzerint(tip)
	//def apply = ujMeghajtoTipusSzerint(_)  de itt ne ez legyen

	def meghajtoNyit(tip: DrTip): SajatDriver =
	{
		if (!meghajtok.contains(tip)) meghajtok += (tip -> ujMeghajtoTipusSzerint(tip))
		meghajtok(tip)
	}
	// ...hanem:
	//def apply = meghajtoNyit(_)   //innentől SajatDriver(tip) -'el lehet példányosítani - elvileg
	def apply(tip: DrTip): SajatDriver = meghajtoNyit(tip) //még innentől is csak elvileg, de így legalább .apply-'al lehet hívni

	val aktMeghajtoTipus = FFDR  // <---- itt kell meghajtótípust változtatni!
/*
  def inic(peldany: SajatDriver): Unit =
    {
      
    }
*/
  val uresAblakok: collection.mutable.Map[String, WeblapSe] = collection.mutable.Map()

  def statikusStatusz = <div>{meghajtok.map{case(tip, dr) => <div>{tip}: {dr.getWindowHandle}</div>}}</div>
}
