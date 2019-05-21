package client;

import org.openqa.selenium.Capabilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.webautomation.ScreenCaptureHtmlUnitDriver;

public class SajatHtmlUnitDriver extends ScreenCaptureHtmlUnitDriver implements SajatDriver
{

	public SajatHtmlUnitDriver()
	{
		super(true);
		//this.setHTTPProxy("szusza", 8118, new java.util.ArrayList<String>(List.of("localhost", "127.0.0.1", "szusza", "pici", "laca.no-ip.hu", "192.168.0.0/16")));
		//this.setProxy("szusza", 8118);
		this.setProxySettings(sajatProxy);
	}

	public SajatHtmlUnitDriver(boolean enableJavascript)
	{
		super(enableJavascript);
		// TODO Auto-generated constructor stub
	}

	public SajatHtmlUnitDriver(Capabilities capabilities)
	{
		super(capabilities);
		// TODO Auto-generated constructor stub
	}

	public SajatHtmlUnitDriver(BrowserVersion version)
	{
		super(version);
		// TODO Auto-generated constructor stub
	}

}
