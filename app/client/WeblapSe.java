package client;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

//import org.apache.commons.io.FileUtils;

public class WeblapSe extends Weblap
{

	//final static String playhome = "/home/laca/play/"; //- a pici főrendszerén (egyelőre csak ide kell, aztál lehet, hogy majd globálisabb helyen kell definiálni
	// de inkább: (Java.io.File) play.Environment.rootPath()
	//final static String projhome = playhome+"play-java-seed/";
	// play-specifikus (ott van ./public):
	final static String driverHelye = "./public/geckodriver";  //ugyanis oda tettem (állítólag relatív is lehet, csak ./ -'el kezdődjön)
	final static String tmpDir = "./public/tmp/"; // http-ben ez /assets/tmp/ (mármint play alatt, ld. routes)
	
	java.io.PrintStream ki = System.out;
	
	String inicEredm = ""; // mert esetleg a konstruktor is feldolgoz, és majd a feldolg jeleníti meg (lehet, hogy feljebb kell)
	
	//de ha a driver mező, akkor mehet a feldolgozás a feldolg-ban
	FirefoxOptions options = new FirefoxOptions();
	
	WebDriver driver ;//= (WebDriver) new FirefoxDriver(options);		mivel az options-t metódusban kell beállítani, ezért ezt is ott kell inicializálni
	
	/* vagy a konstruktorból, vagy a seInic-ből
	 * 
	 */
	void driverInic()
	{
		System.setProperty("webdriver.gecko.driver", driverHelye);
	//Set Firefox Headless mode as TRUE
		options.setHeadless(true);
	//Instantiate Web Driver
		driver = (WebDriver) new FirefoxDriver(options);		//eclipse kedvéért cast-olva, de a play-nek anélkül is jó
	}
	
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

	public WeblapSe(Map<String, String[]> wParams)
	{
		super(wParams);
		
		driverInic();
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
		driver.close();
	}

}
