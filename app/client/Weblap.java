package client;

import java.util.Map;

public class Weblap
{
	String url = "";
	public String getURL() { return url; }

	String docHtml = ""; //html forrás
	
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
	
	//hátha javában is működik
	public static Weblap apply(Map<String, String[]> wParams, String s)
	{
		switch(s)
		{
		case "Se": return new WeblapSe(wParams, SajatDriver.meghajtoNyit(SajatDriver.aktMeghajtoTipus()));
		case "SeCP": 
			wParams.put("url", new String[] {"https://www.scribd.com/document/397870947/Gramatica-Quechua-Junin-Huanca-Rodolfo-Cerron-Palomino"});
			return new WeblapSeCP(wParams, SajatDriver.meghajtoNyit(SajatDriver.aktMeghajtoTipus()));
		default: return new Weblap(wParams);
		}
	}

}
