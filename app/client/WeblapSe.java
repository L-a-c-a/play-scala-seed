package client;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

public class WeblapSe extends Weblap
{

	
	java.io.PrintStream ki = System.out;

	final static String tmpDir = "./public/tmp/"; // http-ben ez /assets/tmp/ (mármint play alatt, ld. routes)

	String inicEredm = ""; // mert esetleg a konstruktor is feldolgoz, és majd a feldolg jeleníti meg (lehet, hogy feljebb kell)
	
	SajatDriver driver; // = new SajatDriverC(); nem kell Weblap* példányonként egy böngésző
	
	@Override
	public String getInicEredm() { return inicEredm; }
	
	public void seInic() //output az inicEredm-ben; konstruktor hívja, azért void
	{
		java.io.ByteArrayOutputStream rOS = new java.io.ByteArrayOutputStream();
		ki = new java.io.PrintStream(rOS);

		/**/ System.out.println("driver.get hívás előtt url="+url);
		driver.get(url);
		ki.println("lapcím: " + driver.getTitle());

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
