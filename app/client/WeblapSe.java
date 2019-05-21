package client;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

//import org.apache.commons.io.FileUtils;

public class WeblapSe extends Weblap
{

	
	java.io.PrintStream ki = System.out;

	final static String tmpDir = "./public/tmp/"; // http-ben ez /assets/tmp/ (mármint play alatt, ld. routes)
	// ha lesz SajatDriver a WebDriver és a Sajat{Firefox|...}Driver között, akkor ez oda kell, az AShot-os hívással együtt

	String inicEredm = ""; // mert esetleg a konstruktor is feldolgoz, és majd a feldolg jeleníti meg (lehet, hogy feljebb kell)
	
	//WebDriver driver ;//= (WebDriver) new FirefoxDriver(options);		mivel az options-t metódusban kell beállítani, ezért ezt is ott kell inicializálni
	
	/* vagy a konstruktorból, vagy a seInic-ből
egyikből sem; kiszervezzük sajád osztályba, aztán new
	void driverInic()
	{
		System.setProperty("webdriver.gecko.driver", driverHelye);
	//Set Firefox Headless mode as TRUE
		options.setHeadless(true);
	//Instantiate Web Driver
		driver = (WebDriver) new FirefoxDriver(options);		//eclipse kedvéért cast-olva, de a play-nek anélkül is jó
	}
	 */
	//WebDriver driver = (WebDriver) new SajatFirefoxDriver(); //ez az egy meghajtó-típus-függés maradt itt - ez is lehet, hogy megy egy közbülső SajatDriver osztályba
	SajatDriver driver; // = new SajatDriverC(); nem kell Weblap* példányonként egy böngésző
	
	@Override
	public String getInicEredm() { return inicEredm; }
	
	public void seInic() //output az inicEredm-ben; konstruktor hívja, azért void
	{
		java.io.ByteArrayOutputStream rOS = new java.io.ByteArrayOutputStream();
		ki = new java.io.PrintStream(rOS);
		
							/*s
							play.Environment envD = new play.Environment(play.Mode.DEV);
							play.Environment envT = new play.Environment(play.Mode.TEST);
							play.Environment envP = new play.Environment(play.Mode.PROD);
							ki.println("DEV gyökér=" + envD.rootPath());
							ki.println("TEST gyökér=" + envT.rootPath());
							ki.println("PROD gyökér=" + envP.rootPath());
							// mind a három "."
							ki.println("DEV gyökér absz.=" + envD.rootPath().getAbsolutePath());
							// /home/laca/play/play-java-seed/.
							*/

		// ld. driverInic
		//System.setProperty("webdriver.gecko.driver", driverHelye);
		//......további szívások leírása a jdk8_home docker-volumen alatti példányban
		// ... de a legbiztosabb nem dockerben futtatni - csak sbt-t kellett installálni és megy
		
		//Set Firefox Headless mode as TRUE
		//FirefoxOptions options = new FirefoxOptions();
		//options.setHeadless(true);
		
		//Instantiate Web Driver
		///*WebDriver*/ driver = (WebDriver) new FirefoxDriver(options);		//eclipse kedvéért cast-olva, de a play-nek anélkül is jó
		/**/ System.out.println("driver.get hívás előtt url="+url);
		driver.get(url);
		ki.println("lapcím: " + driver.getTitle());
		
		//driver.save_screenshot("q.png");
		//driver.manage().window().maximize();  //nem igazán nagyítja ki
		//File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		//try { FileUtils.copyFile(scrFile, new File(tmpDir + "screenshot.png")); } catch (IOException e1) { e1.printStackTrace(); }
		//AShot:  átkerül a helyére, a feldolg()-ba

		//driver.close(); nem itt!
		
		try { inicEredm = rOS.size() == 0 ? "rOS üres" : rOS.toString("UTF-8"); }catch (java.io.UnsupportedEncodingException e) {}
	}

	public WeblapSe(Map<String, String[]> wParams)	//ellenjavallt (dekrepált), tessék kívülről driver-t adni
	{
		super(wParams);
		
		//driverInic(); helyett new SajatFirefoxDriver(); a driver inicializálásában
		seInic();
	}

	public WeblapSe(Map<String, String[]> wParams, SajatDriver dr)
	{
		super(wParams);
		driver = dr;
		seInic();
	}
	
	@Override
	public String feldolg()
	{
		/**/ System.out.println("WeblapSe.feldolg()meghívva");
		//AShot:
		Screenshot fpScreenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
		try { ImageIO.write(fpScreenshot.getImage(),"PNG",new File(tmpDir + "screenshot.png")); }catch(IOException e1) { e1.printStackTrace(); }

		return s + " Feldolgozás"
				+ "<pre>" + inicEredm + "</pre>"
				+ "<img src=assets/tmp/screenshot.png>";
	}
	
	@Override
	public void close()
	{
		//driver.close();
	}

}
