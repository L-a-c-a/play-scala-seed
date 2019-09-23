package client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
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
	/* */ public SajatDriver getDriver() { return driver; }
	
	//2 ideiglenes fájl
  String tmpKepFajl = "";
  String tmpHtmlFajl = "";
  
  String linkek = "";
	
	@Override
	public String getInicEredm() { return inicEredm; }
	
	Instant feldolgPill = Instant.ofEpochMilli(0L);

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
		{ String absHref = Optional.ofNullable(elem.getAttribute("href")).orElse("[nincs href]"); // getAttribute beleokoskodik és abszolút címet ad!
		  String relHref = Optional.ofNullable((String) driver.executeScript("return arguments[0].getAttribute('href');", elem)).orElse("[nincs href]");
      //**/ System.out.println ("|"+absHref+"|"); //getAttribute még /-t is tesz a szerver végére
      //**/ System.out.println ("|"+relHref+"|");
      String fahOnclickVaz = "feldolgajaxhivas('" + inicPill.toEpochMilli() + "', '&muvelet=katt&par=%s');";  //%s-'el sprintf-hez... helyett String.format-hoz
      String fahOnclick = "";
      try
      { fahOnclick= String.format(fahOnclickVaz, java.net.URLEncoder.encode(generateXPATH(elem, ""), "UTF-8"));
      }catch (java.io.UnsupportedEncodingException e) { /*anyád*/ }
      String fahOnclickEmberi = String.format(fahOnclickVaz, generateXPATH(elem, ""));
      //**/ System.out.println ("fahOnclick=" + fahOnclick);
			return "<div>"
			    + relHref 
			    + (relHref.replace("/", "").equals(absHref.replace("/", "")) ? " " : " (" + absHref +  ") ")  //replaceAll-t akartam, de így is jó
			    + Optional.ofNullable(elem.getAttribute("onclick")).orElse("")
					+ elem.getText()
          //+ "|" + elem.getAttribute("xpath") + "|" -> |null|
          //+ "|" + generateXPATH(elem, "") + "|"
          //+ "|" + fahOnclick + "|"
					+ ( "[nincs href]".equals(relHref)
					  ? "<button class=alacsonygomb disabled>ne nyomjad</button>"
            : "<button class=alacsonygomb onclick='inicajaxhivas(\"weblapajaxinic?url=" + elem.getAttribute("href") + "&s=" + s + "\");'>nyomjad</button>"
            )
          + "<button class=alacsonygomb onclick=\"" + fahOnclick + "\" title=\"" + fahOnclickEmberi + "\">katt</button>"
          //+ "<button class=alacsonygomb onclick='this.click();'>katt(JS)</button>" ez így egy baromság; azonosítani kéne a célelemet (hogy a tökbe?), és azt kattintani
					+ "</div>";
		};
		return driver.findElements(By.cssSelector("a[href], [onclick]"))  //esetleg :is(..., ...)
				.stream()
				.map(elembolHtmlMap)
				.reduce("", (a,b)->a+b);
	}

	public WeblapSeJ(Map<String, String[]> wParams, SajatDriver dr)
	{
		super(wParams);
		driver = dr;
		/* */ System.out.println(wParamsStr(wParams));
		if (wParams.containsKey("pill")) inicPill = Instant.ofEpochMilli(Long.parseLong(wParams.get("pill")[0]));
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

  //innen: https://stackoverflow.com/questions/50578809/how-can-i-get-the-xpath-from-a-webelement
  public static String generateXPATH(WebElement childElement, String current) {
        String childTag = childElement.getTagName();
        //én:
        String childId = childElement.getAttribute("id");
        if (childId != null)
          if (childId.length() > 0)
            return "//*[@id='" + childId + "']" + current;  //de ez így nagyon nem jó urlencode nélkül
        //if(childTag.equals("body")) //abból is csak egy van
          //return "body" + current;    okoskodás, nem műx
        //idáig én
        if(childTag.equals("html")) {
            return "/html[1]"+current;
        }
        WebElement parentElement = childElement.findElement(By.xpath("..")); 
        java.util.List<WebElement> childrenElements = parentElement.findElements(By.xpath("*"));
        int count = 0;
        for(int i=0;i<childrenElements.size(); i++) {
            WebElement childrenElement = childrenElements.get(i);
            String childrenElementTag = childrenElement.getTagName();
            if(childTag.equals(childrenElementTag)) {
                count++;
            }
            if(childElement.equals(childrenElement)) {
                return generateXPATH(parentElement, "/" + childTag + "[" + count + "]"+current);
            }
        }
        return null;
    }

  public static String wParamsStr(Map<String, String[]> wParams)
  {
    return "wParams={host="+((wParams.containsKey("host")) ? wParams.get("host")[0] : "")
                  +",s="+((wParams.containsKey("s")) ? wParams.get("s")[0] : "")
                  +",uri="+((wParams.containsKey("uri")) ? wParams.get("uri")[0] : "")
                  +",pill="+((wParams.containsKey("pill")) ? wParams.get("pill")[0] : "")
                  +",url="+((wParams.containsKey("url")) ? wParams.get("url")[0] : "")
                  +"}"
                  ;
  }
}
