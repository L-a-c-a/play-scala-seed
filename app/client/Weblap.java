package client;

import java.util.Map;

public class Weblap
{
	String url = "";
	public String getURL() { return url; }
	
	String s = "";
	public String getS() { return s; }
	
	public String getInicEredm() { return ""; }   //majd a leszármazottban csinál valamit
	
	public Weblap() 	//legyen egy ilyen is, hogy a modellnek ne kelljen nullal inicializálni
	{
		url = "";
		s = "";
	}
	
	public Weblap(Map<String, String[]> wParams)
	{
		if (wParams.containsKey("url")) url = wParams.get("url")[0];
		if (wParams.containsKey("s")) s = wParams.get("s")[0];
	}
	
	public String feldolg()
	{
		return "Feldolgozás";
	}
	
	public String urlEgyebAttr = "";   //további attr. az url form-input-mezőhöz, pl. readonly
	public String sEgyebAttr = "";
	
	public void close() {}		//itt üres, de ide is kell, mert hátha a kontroller meghívja

}
