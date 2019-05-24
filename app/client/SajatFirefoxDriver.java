package client;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.firefox.XpiDriverService;

public class SajatFirefoxDriver extends FirefoxDriver implements SajatDriver  //csak az eclipse jelez hibát, a play-nek jó
{
	final static String driverHelye = "./public/geckodriver";  //ugyanis oda tettem (állítólag relatív is lehet, csak ./ -'el kezdődjön)
	//static FirefoxOptions options = new FirefoxOptions();
	private static FirefoxOptions driverInic() //super, mint első utasítás probléma megkerülése (static-nak kell lenni)
	{
		FirefoxOptions ffo = new FirefoxOptions();
		ffo.setHeadless(true);
		ffo.setProxy(SajatDriver$.MODULE$.sajatProxy()); //így eszi csak meg: $.MODULE$. és () (társobjektumból egy val)
		System.setProperty("webdriver.gecko.driver", driverHelye);
		return ffo;
	}

	public SajatFirefoxDriver()
	{
		//az eredeti üres volt, ami implicit super() hívást jelent - vagyis van neki (mármint FirefoxDriver-nek () konstruktora)
		super(driverInic());  //így megeszi
	}

}
