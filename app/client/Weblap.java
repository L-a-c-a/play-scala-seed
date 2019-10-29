package client;

import java.util.Map;
import java.time.Instant;

public class Weblap
{
  String url = "";
  public String getURL() { return url; }

  String docHtml = ""; //html forrás

  String s = "";
  public String getS() { return s; }

  Instant inicPill = Instant.ofEpochMilli(0L);
  public void setInicPill(Instant pill) { inicPill = pill; }
  public Instant getInicPill() { return inicPill; }

  public String getInicEredm() { return ""; }   //majd a leszármazottban csinál valamit

  public Weblap(String pUrl, String pS)
  {
    url = pUrl;
    s = pS;
  }

  public Weblap()   //legyen egy ilyen is, hogy a modellnek ne kelljen nullal inicializálni
  {
    url = "";
    s = "";
  }

  public Weblap(Map<String, String[]> wParams)
  {
    if (wParams.containsKey("url")) url = wParams.get("url")[0];
    if (wParams.containsKey("s")) s = wParams.get("s")[0];
  }

  public String feldolg(String... pp)
  {
    return "Feldolgozás";
  }

  public String urlEgyebAttr = "";   //további attr. az url form-input-mezőhöz, pl. readonly
  public String sEgyebAttr = "";

  public void close() {}    //itt üres, de ide is kell, mert hátha a kontroller meghívja

  public String statusz() { return String.format("%s %s %s", java.time.format.DateTimeFormatter.ISO_INSTANT.format(inicPill), url, s); }

  //public String katt() { return ""; }   //majd a leszármazottban csinál valamit
  //public String katt(String par) { return ""; }   //majd a leszármazottban csinál valamit

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
    case "SeEro": return new WeblapSeEro(wParams, SajatDriver.meghajtoNyit(SajatDriver.aktMeghajtoTipus()));
    }
  }

  public static Map<String, String[]> wParamUjra (String pUrl, String pS) { return wParamUjra(pUrl, pS, Instant.now()); }

  public static Map<String, String[]> wParamUjra (String pUrl, String pS, Instant pPill)
  {
    Map<String, String[]> wp = new java.util.HashMap<>();
    wp.put("url", new String[]{ pUrl });
    wp.put("s", new String[]{ pS });
    wp.put("pill", new String[]{ pPill.toEpochMilli()+"" });
    return wp;
  }
}
