package client;

import org.openqa.selenium.Capabilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.webautomation.ScreenCaptureHtmlUnitDriver;

public class SajatHtmlUnitDriver extends ScreenCaptureHtmlUnitDriver implements SajatDriver
{

	public SajatHtmlUnitDriver()
	{
		super(true);
		this.setProxySettings(SajatDriver$.MODULE$.sajatProxy());
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

  public scala.collection.mutable.Map<String, WeblapSe> ablakok = SajatDriver$.MODULE$.uresAblakok();
  @Override
  public void ablakok_$eq(scala.collection.mutable.Map<String, WeblapSe> s) { ablakok = s;}
  @Override
  public scala.collection.mutable.Map<String, WeblapSe> ablakok() { return ablakok;}
}
