package client;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.Proxy

trait SajatDriver extends WebDriver with JavascriptExecutor with TakesScreenshot
{
	//def sajatProxy = SajatDriver.sajatProxyVal //ezt static kontextusból nem lehet hívni - marad a társobjektumban
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
		tip match  //nem úszom meg (vagy nem érem föl ésszel, hogy úszom meg)
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
	def apply = meghajtoNyit(_)   //innentől SajatDriver(tip) -'el lehet példányosítani

	val aktMeghajtoTipus = FFDR  // <---- itt kell meghajtótípust változtatni!

}