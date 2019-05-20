package client;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

//import org.openqa.selenium.htmlunit.HtmlUnitDriver;
/* ezt a play valamiért nem találja az ivy2-ben, pedig ott van
	package org.openqa.selenium.htmlunit does not exist
	átmásolom a lib alá:
	.ivy2/cache/org.seleniumhq.selenium/htmlunit-driver/jars/htmlunit-driver-2.33.3.jar
	.ivy2/cache/net.sourceforge.htmlunit/htmlunit/jars/htmlunit-2.33.jar
	.ivy2/cache/net.sourceforge.htmlunit/htmlunit-cssparser/jars/htmlunit-cssparser-1.2.0.jar
	......nem csinálom tovább, az isten tudja, meddig tart a függőségi lánc
		java.lang.NoClassDefFoundError: org/apache/http/entity/mime/content/ContentBody
 */

public class SajatDriver implements WebDriver, JavascriptExecutor, TakesScreenshot
{
	WebDriver sajatDriver = new SajatFirefoxDriver();
	//WebDriver sajatDriver = new /*Sajat*/HtmlUnitDriver(true); //de ez nem tud TakesScreenshot-ot
	//WebDriver sajatDriver = new ScreenCaptureHtmlUnitDriver(true);
	// nem ússzuk meg a SajatHtmlUnitDriver-t, mert a proxy-t be kell állítani, és azt minden driver-nél máshogy kell
	//WebDriver sajatDriver = new SajatHtmlUnitDriver();
	//na itt lehet típust változtatni

/*
	public SajatDriver()
	{
		// TODO Auto-generated constructor stub
	}
ott az inicializálás
*/
	public static org.openqa.selenium.Proxy sajatProxy = new org.openqa.selenium.Proxy()
			.setHttpProxy("szusza:8118")
			.setSslProxy("szusza:8118")
			.setNoProxy("localhost, 127.0.0.1, szusza, pici, laca.no-ip.hu, 192.168.0.0/16");
	// a Proxy "univerzális", csak a driver-hez rendelés driver-függő

	@Override
	public Object executeAsyncScript(String arg0, Object... arg1)
	{
		return ((JavascriptExecutor)sajatDriver).executeAsyncScript(arg0, arg1);
	}

	@Override
	public Object executeScript(String arg0, Object... arg1)
	{
		return ((JavascriptExecutor)sajatDriver).executeScript(arg0, arg1);
	}

	@Override
	public void close()
	{
		sajatDriver.close();
	}

	@Override
	public WebElement findElement(By arg0)
	{
		return sajatDriver.findElement(arg0);
	}

	@Override
	public List<WebElement> findElements(By arg0)
	{
		return sajatDriver.findElements(arg0);
	}

	@Override
	public void get(String arg0)
	{
		sajatDriver.get(arg0);
	}

	@Override
	public String getCurrentUrl()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPageSource()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle()
	{
		return sajatDriver.getTitle();
	}

	@Override
	public String getWindowHandle()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getWindowHandles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Options manage()
	{
		return sajatDriver.manage();
	}

	@Override
	public Navigation navigate()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void quit()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public TargetLocator switchTo()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> arg0) throws WebDriverException
	{
		return ((TakesScreenshot)sajatDriver).getScreenshotAs(arg0);
		//- ezt nem lehet HtmlUnitDriverrel megcsinálni:
		//[ClassCastException: class org.openqa.selenium.htmlunit.HtmlUnitDriver cannot be cast to class org.openqa.selenium.TakesScreenshot (org.openqa.selenium.htmlunit.HtmlUnitDriver and org.openqa.selenium.TakesScreenshot are in unnamed module of loader play.runsupport.NamedURLClassLoader @61635847)]
	}

}
