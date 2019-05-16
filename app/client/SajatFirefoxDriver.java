package client;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.firefox.XpiDriverService;

public class SajatFirefoxDriver extends FirefoxDriver
{
	//final static String playhome = "/home/laca/play/"; //- a pici főrendszerén (egyelőre csak ide kell, aztál lehet, hogy majd globálisabb helyen kell definiálni
	// de inkább: (Java.io.File) play.Environment.rootPath()
	//final static String projhome = playhome+"play-java-seed/";
	// play-specifikus (ott van ./public):
	final static String driverHelye = "./public/geckodriver";  //ugyanis oda tettem (állítólag relatív is lehet, csak ./ -'el kezdődjön)
	//static FirefoxOptions options = new FirefoxOptions();
	private static FirefoxOptions driverInic() //super, mint első utasítás probléma megkerülése (static-nak kell lenni)
	{
		FirefoxOptions ret = new FirefoxOptions();
		ret.setHeadless(true);
		System.setProperty("webdriver.gecko.driver", driverHelye);
		return ret;
	}

	public SajatFirefoxDriver()
	{
		//az eredeti üres volt, ami implicit super() hívást jelent - vagyis van neki (mármint FirefoxDriver-nek () konstruktora)
		super(driverInic());  //így megeszi
	}
/* többi nem kell
	public SajatFirefoxDriver(FirefoxOptions options)
	{
		super(options);
		// TODO Auto-generated constructor stub
	}

	public SajatFirefoxDriver(GeckoDriverService service)
	{
		super(service);
		// TODO Auto-generated constructor stub
	}

	public SajatFirefoxDriver(XpiDriverService service)
	{
		super(service);
		// TODO Auto-generated constructor stub
	}

	public SajatFirefoxDriver(GeckoDriverService service, FirefoxOptions options)
	{
		super(service, options);
		// TODO Auto-generated constructor stub
	}

	public SajatFirefoxDriver(XpiDriverService service, FirefoxOptions options)
	{
		super(service, options);
		// TODO Auto-generated constructor stub
	}
*/
}
