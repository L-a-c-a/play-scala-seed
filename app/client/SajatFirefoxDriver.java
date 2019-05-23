package client;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.firefox.XpiDriverService;

public class SajatFirefoxDriver extends FirefoxDriver implements SajatDriver  //csak az eclipse jelez hibát, a play-nek jó
{
	//final static String playhome = "/home/laca/play/"; //- a pici főrendszerén (egyelőre csak ide kell, aztál lehet, hogy majd globálisabb helyen kell definiálni
	// de inkább: (Java.io.File) play.Environment.rootPath()
	//final static String projhome = playhome+"play-java-seed/";
	// play-specifikus (ott van ./public):
	final static String driverHelye = "./public/geckodriver";  //ugyanis oda tettem (állítólag relatív is lehet, csak ./ -'el kezdődjön)
	//static FirefoxOptions options = new FirefoxOptions();
	private static FirefoxOptions driverInic() //super, mint első utasítás probléma megkerülése (static-nak kell lenni)
	{
		FirefoxOptions ffo = new FirefoxOptions();
		ffo.setHeadless(true);
/*
		Proxy proxy = new Proxy(); 
		proxy.setHttpProxy("szusza:8118")
				.setSslProxy("szusza:8118")
				.setNoProxy("localhost, 127.0.0.1, szusza, pici, laca.no-ip.hu, 192.168.0.0/16"); 
		ffo.setProxy(proxy);
*/
		ffo.setProxy(SajatDriver$.MODULE$.sajatProxy()); //így eszi csak meg: $.MODULE$. és () (társobjektumból egy val)
		System.setProperty("webdriver.gecko.driver", driverHelye);
		return ffo;
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
