package client;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
//import org.openqa.selenium.firefox.GeckoDriverService;
//import org.openqa.selenium.firefox.XpiDriverService;

public class SajatFirefoxDriver extends FirefoxDriver implements SajatDriver
{
	final static String driverHelye = "./public/geckodriver";  //ugyanis oda tettem (állítólag relatív is lehet, csak ./ -'el kezdődjön)
	//static FirefoxOptions options = new FirefoxOptions();
	private static FirefoxOptions driverInic() //super, mint első utasítás probléma megkerülése (static-nak kell lenni)
	{
		FirefoxOptions ffo = new FirefoxOptions();
		ffo.setHeadless(true);
		//ffo.setProxy(SajatDriver$.MODULE$.sajatProxy()); //így eszi csak meg: $.MODULE$. és () (társobjektumból egy val)
		System.setProperty("webdriver.gecko.driver", driverHelye);
		return ffo;
	}

	public SajatFirefoxDriver()
	{
		//az eredeti üres volt, ami implicit super() hívást jelent - vagyis van neki (mármint FirefoxDriver-nek () konstruktora)
		super(driverInic());  //így megeszi
		//inic(this);  //a SajatDriver-ből
		//ablakok = (Set<String>) scala.collection.generic.GenericSetTemplate.empty();
	}

  public scala.collection.mutable.Map<String, WeblapSe> ablakok = SajatDriver$.MODULE$.uresAblakok();//= (Set<String>) scala.collection.mutable.Set.empty();  //??ezt itt nem lehet, mert static content
  @Override
  public void ablakok_$eq(scala.collection.mutable.Map<String, WeblapSe> s) { ablakok = s;}
  @Override
  public scala.collection.mutable.Map<String, WeblapSe> ablakok() { return ablakok;}
  
  public int histIndex = 0;
  @Override
  public void histIndex_$eq(Integer i) { histIndex = i; }
  @Override
  public Integer histIndex() { return histIndex; }
/*
	@Override
	public String statusz()
	{
		return "";
	}

	@Override
	public scala.collection.mutable.Set ablakok ;//= scala.collection.generic.GenericSetTemplate.empty();  ezt itt nem lehet, mert static content
	@Override
  public void ablakok_$eq(scala.collection.mutable.Set s) { ffAblakok = s;}
	@Override
	public scala.collection.mutable.Set ablakok() { return ffAblakok;}
*/

}
