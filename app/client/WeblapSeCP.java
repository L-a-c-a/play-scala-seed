package client;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class WeblapSeCP extends WeblapSe
{
/*
 * a Cerrón-Palomino-könyvnek a scribd-en
 * a div.image_layer alatti képek letöltésére
 * (más scribd-könyveknek nincsenek olyanjai)
 */
	public void seCPInic()
	{
		java.io.ByteArrayOutputStream rOS = new java.io.ByteArrayOutputStream();
		ki = new java.io.PrintStream(rOS);

		/*kiszervezve SajatFirefoxDriver-be
		System.setProperty("webdriver.gecko.driver", driverHelye);
		
		//Set Firefox Headless mode as TRUE
		FirefoxOptions options = new FirefoxOptions();
		options.setHeadless(true);
		
		//Instantiate Web Driver
		WebDriver driver = (WebDriver) new FirefoxDriver(options);		//eclipse kedvéért cast-olva, de a play-nek anélkül is jó
		*/
		WebDriver driver = (WebDriver) new SajatFirefoxDriver(); //ez az egy meghajtó-típus-függés maradt itt - ez is lehet, hogy megy egy közbülső SajatDriver osztályba
		JavascriptExecutor driverJS = (JavascriptExecutor)driver;

		driver.get(url);

		//List<WebElement> lapok =driver.findElements(By.cssSelector("div.document_container div.outer_page"));
		List<WebElement> lapok = driver.findElements(By.cssSelector("div.newpage"));
		//**/ ki.println("lapok: " + lapok.size());   //294 = 288 + ... az előző módszerrel
		/**/ System.out.println("lapok: " + lapok.size());   //így 288
		List<WebElement> szkriptek =driver.findElements(By.cssSelector("div.document_container script")); //minden laphoz van egy megjelenítő js
		/**/ ki.println("szkriptek: " + szkriptek.size());   //290 , kettővel több, mint kéne
		/**/ System.out.println("szkriptek: " + szkriptek.size());   //290 , kettővel több, mint kéne

		Consumer <WebElement> szkriptenkent =
				elem ->
		{
			String js = (String) driverJS.executeScript("return arguments[0].innerHTML;", elem);
			//System.out.println(js);
			/*
    (function() {
        var pageParams = {"origHeight": 1400, "origWidth": 901, "fonts": [9, 23], "pageNum": 1};

        pageParams.containerElem = document.getElementById("outer_page_1");
          pageParams.contentUrl = "https://html2-f.scribdassets.com/9c27d1x88w6qg2fc/pages/1-e16e89b670.jsonp";
          pageParams.blur = false
        var page = docManager.addPage(pageParams);
      })();
			akár ebből is lehetne dolgozni, mert ami kell nekünk, az contentUrl.replace('html2','html1').replace('pages', 'images').replace('.jsonp', '.jpg')
			de megpróbáljuk végrehajtani
			*/
			if (js.contains("var pageParams")) //driverJS.executeScript(js); de nem megy, valami belül megakadályozza
				//Caused by: org.openqa.selenium.JavascriptException: Container Elem is already bound to a page.  We shouldn't get here
			{
				String cim = "https://" + js.split("https://")[1].split(".jsonp")[0].replace("html2", "html1").replace("pages", "images") + ".jpg";
				System.out.println(cim);
				String lapszam = js.split("\"pageNum\": ")[1].split("};")[0];
				System.out.println(lapszam);
				//ki.println("<a href=" + cim + ">" + lapszam + ". lap</a><br>");
				ki.println("curl --output pagina" + lapszam + ".jpg " + cim);
			}

		};
		szkriptek.forEach(szkriptenkent);


		System.out.println("most akarjuk lezárni");
		driver.close();
		System.out.println("lezártuk - most jön a rOS.toString");
		
		try { inicEredm = rOS.size() == 0 ? "rOS üres" : rOS.toString("UTF-8"); }catch (java.io.UnsupportedEncodingException e) {}
		System.out.println("állítólag át is konvertáltuk"); //át, a System.out-ra ki is jött, csak a böngészőre nem
		System.out.println(inicEredm);	//addig, míg ez a két println is it nem lett
	}

	@Override
	public void seInic() { seCPInic(); }
	
	public WeblapSeCP(Map<String, String[]> wParams)
	{
		super(wParams);  //super.super nem megy, muszáj az seInic-et leszármaztatni
		
		//url = "https://www.scribd.com/document/397870947/Gramatica-Quechua-Junin-Huanca-Rodolfo-Cerron-Palomino"; WeblapModell-ben megtörtént
		urlEgyebAttr = "readonly";
		sEgyebAttr = "readonly";
		
		//seCPInic();
	}

	@Override
	public String feldolg()
	{
		return s + " Feldolgozás"
				+ "<pre>" + inicEredm + "</pre>";
	}
}
