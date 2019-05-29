package client;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import java.util.Map;

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

	public String linkek()
	{
		//String[] ret = {""};
		Function<WebElement, String> elembolHtmlMap =
				elem ->
		{
			return elem.getAttribute("href")
					+  " "
					+  elem.getText()
					+ "<button class=alacsonygomb onclick='inicajaxhivas(\"weblapajaxinic?url=" + elem.getAttribute("href") + "&s=" + s + "\")'>nyomjad</button>"
					+ "<br>";
		};
		return driver.findElements(By.cssSelector("a[href]"))
				.stream()
				.map(elembolHtmlMap)
				.reduce("", (a,b)->a+b);
/*
		driver.findElements(By.cssSelector("a[href]")).forEach
		(
			elem ->
			{
.........................
			}
		);
		return ret[0];
*/
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
				+ "<div class=korlatozottmagassag1000>" + linkek() + "</div>"
				+ "<img src=assets/tmp/screenshot.png#" + java.time.Instant.now().toEpochMilli() + ">";
	}
	
	@Override
	public void close()
	{
		//driver.close();
	}

}
