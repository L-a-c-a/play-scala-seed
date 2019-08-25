package client;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.Proxy

import collection.JavaConverters._

trait SajatDriver extends WebDriver with JavascriptExecutor with TakesScreenshot
{
	//def sajatProxy = SajatDriver.sajatProxyVal //ezt static kontextusból nem lehet hívni - marad a társobjektumban
  //var ablakok: collection.mutable.Set[String] //= collection.mutable.Set()  //getWindowHandle(s) adta azonosítóknak
  var ablakok: collection.mutable.Map[String, WeblapSe]
  //ettől csak egy ablakok() meg egy ablakok_$eq(Set) lesz felülírnivaló, ablakok változó nem
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
      ablakok.map { case (abl, lap) => <div>{abl}: {lap.driver.getCurrentUrl}</div> }
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
