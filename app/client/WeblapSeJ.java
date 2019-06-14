package client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.Instant;
import java.util.Map;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

/*
 * WeblapSe kezdeménye, még java-ban - amaz ennek a leszármazottja (most még?) 2019-06
 */
public class WeblapSeJ extends Weblap
{

	
	java.io.PrintStream ki = System.out;

	final static String tmpDir = "./public/tmp/"; // http-ben ez /assets/tmp/ (mármint play alatt, ld. routes)

	String inicEredm = ""; // mert esetleg a konstruktor is feldolgoz, és majd a feldolg jeleníti meg (lehet, hogy feljebb kell)
	
	SajatDriver driver; // = new SajatDriverC(); nem kell Weblap* példányonként egy böngésző
	
	//2 ideiglenes fájl
  String tmpKepFajl = "";
  String tmpHtmlFajl = "";
  
  String linkek = "";
	
	@Override
	public String getInicEredm() { return inicEredm; }
	
	Instant feldolgPill;

	public void seInic() //output az inicEredm-ben; konstruktor hívja, azért void
	{
		java.io.ByteArrayOutputStream rOS = new java.io.ByteArrayOutputStream();
		ki = new java.io.PrintStream(rOS);

		/**/ System.out.println("driver.get hívás előtt url="+url);
		driver.get(url);
		ki.println("lapcím: " + driver.getTitle());

		docHtml = driver.getPageSource();
		
		linkek = linkek(); // ezt inickor kell

		try { inicEredm = rOS.size() == 0 ? "rOS üres" : rOS.toString("UTF-8"); }catch (java.io.UnsupportedEncodingException e) {}
	}

	public WeblapSeJ(Map<String, String[]> wParams)	//ellenjavallt (dekrepált), tessék kívülről driver-t adni
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
			return "<div>"
			    + elem.getAttribute("href")
					+  " "
					+  elem.getText()
					+ "<button class=alacsonygomb onclick='inicajaxhivas(\"weblapajaxinic?url=" + elem.getAttribute("href") + "&s=" + s + "\")'>nyomjad</button>"
					+ "</div>";
		};
		return driver.findElements(By.cssSelector("a[href]"))
				.stream()
				.map(elembolHtmlMap)
				.reduce("", (a,b)->a+b);
	}

	public WeblapSeJ(Map<String, String[]> wParams, SajatDriver dr)
	{
		super(wParams);
		driver = dr;
		seInic();
	}
	
	@Override
	public String feldolg()
	{
	  feldolgPill = Instant.now();
	  Long pill = feldolgPill.toEpochMilli();
		/**/ System.out.println("WeblapSe.feldolg() meghívva " + pill + " " + feldolgPill);
		//de nem ezt használjuk az ideigl. fájlok nevéhez, mert az a koncepció, hogy azok addig élnek, ameddig a példány
		
		String kepfajl = "screenshot" + inicPill + ".png";
		String htmlfajl = "drpagesource" + inicPill + ".html";
		
		if (tmpKepFajl.isEmpty())
		{
	    //AShot:
	    Screenshot fpScreenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
	    try { ImageIO.write(fpScreenshot.getImage(),"PNG",new File(tmpDir + kepfajl)); }catch(IOException e1) { e1.printStackTrace(); }
	    tmpKepFajl = kepfajl;
		}else
		  kepfajl = tmpKepFajl + "#" + pill;  //ua. a fájl friss címmel, hogy ne a cache-ből tudjisten mit vegyen elő

    java.util.function.BiConsumer<String, String> fajlbair =
        (szoveg, fajl) ->
        {
          try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fajl), "utf-8")))
          {
            writer.write(szoveg);
          } catch (IOException e) { e.printStackTrace(); }
        };
/*
    //HTML forrás egyikféleképpen
    docHtml = driver.getPageSource();
    fajlbair.accept(docHtml, tmpDir + "drpagesource.html");
    //HTML forrás másikféleképpen
    docHtml = (String)driver.executeScript("return document.documentElement.outerHTML;");
    fajlbair.accept(docHtml, tmpDir + "drexejs.html");
    //a kettő ugyanaz még nagyon js-súlyos lapoknál is
*/
    if (tmpHtmlFajl.isEmpty())
    {
      fajlbair.accept(docHtml, tmpDir + htmlfajl);
      tmpHtmlFajl = htmlfajl;
    }else
      htmlfajl = tmpHtmlFajl + "#" + pill;
    

    return s + " Feldolgozás"
        + "<pre>" + inicEredm + "</pre>"
        + "<div class=korlatozottmagassag1000>" + linkek + "</div>"
        //+ "<img src=assets/tmp/screenshot" + pill + ".png>" helyett, http://qnimate.com/tabbed-area-using-html-and-css-only/ -ból smart insert módban idemásolva:
            + "<div class=\"tabs\">\n" + 
            "    <div class=\"tab\">\n" + 
            "      <input name=\"checkbox-tabs-group\" type=\"radio\" id=\"checkbox1\" class=\"checkboxtab\" checked>\n" + 
            "      <label for=\"checkbox1\">Kép</label>\n" + 
            "      <div class=\"content\">\n" + 
            "        <img src=assets/tmp/" + kepfajl + ">\n" + 
            "      </div>\n" + 
            "    </div>\n" + 
            "    \n" + 
            "    <div class=\"tab\">\n" + 
            "      <input name=\"checkbox-tabs-group\" type=\"radio\" id=\"checkbox2\" class=\"checkboxtab\">\n" + 
            "      <label for=\"checkbox2\">Forrás</label>\n" + 
            "      <div class=\"content\">\n" + 
            "        <iframe src=assets/tmp/" + htmlfajl + " style=\"flex-grow: 1; width: 100%;\"></iframe>\n" + 
            "      </div>\n" + 
            "    </div>\n" + 
            "    \n" + 
            "  </div>\n";
  }
	
	@Override
	public void close()
	{
		//driver.close();
		//ideigl. fájlokat törölni?
    tmpKepFajl = "";
    tmpHtmlFajl = "";
	}

}
