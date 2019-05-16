package models

//import java.util.HashMap
import java.util.Map	//ha azt mondtam a Controllerben, hogy java.util.Map, akkor java.util.Map (a scala.collection.immutable.Map más)
import scala.collection.JavaConverters.mutableMapAsJavaMap

import client._

object WeblapModell
{
	//var lap: Weblap = null //de aszongyák, kerüljük a nullt
	//var lap = new Weblap(mutableMapAsJavaMap(collection.mutable.Map("" -> Array(""))))  //hát nesztek :)
	var lap = new Weblap()
	var tartosWeblapok = new scala.collection.mutable.HashMap[Long, Weblap]()	//ajaxoshoz
	
	def inic (wParams: Map[String, Array[String]]): String =	//ezt kell hívni elsőnek a .scala.html lapról
	{
		var s = ""
		//s = wParams.get("s")(0)  //kiváltódik? - ki.
		//try { s = wParams.get("s")(0) } catch {case e:NullPointerException => }   vagy: (miért nem mindig így csináltam...)
		if (wParams.containsKey("s")) s = wParams.get("s")(0)
		if (s=="Se")
		{
			lap = new WeblapSe(wParams)
			return "WeblapSe példányosítva" //+ "<br>" + lap.getInicEredm
		}
		if (s=="SeCP")
		{
			wParams.put("url", Array("https://www.scribd.com/document/397870947/Gramatica-Quechua-Junin-Huanca-Rodolfo-Cerron-Palomino"))
			lap = new WeblapSeCP(wParams)
			return "WeblapSeCP példányosítva"
		}
		lap = new Weblap(wParams)
		//if (wParams.get("s")(0) == "qqq") return " qqq lap példányosítva"  //beszarás, műx! - így lehet s-től függő típusú weblapot példányosítani
			//de ha nincs s paraméter, akkor NullPointerException
		return "lap példányosítva"
	}

	def inicAjax (wParams: Map[String, Array[String]]): String =	//ezt kell hívni elsőnek az ajaxos .scala.html lapról
	{
		var ret = inic(wParams)  //ettől lesz egy weblap a lap-ban
		//var pill = //111111L  //helyett majd lesz a mostani időpillanat
			//java.time.Instant.now.toEpochMilli
		var pillInstant = java.time.Instant.now
		var pill = pillInstant.toEpochMilli
		tartosWeblapok.put(pill, lap)
		ret + s"<div><span id=idoLong>$pill</span> <span id=idoISO>$pillInstant</span></div>"
	}
	
	def feldolg /*(wParams: Map[String, Array[String]]): String*/ = lap.feldolg
	// de valsz. nem kell neki a paraméter, mert már az inic-kor eltette magának
	
	def feldolg(pill: Long): String =
	{
		//var ret = if (tartosWeblapok.contains(pill)) tartosWeblapok.apply(pill).feldolg else s"nincs már $pill kulcsú objektum a listában" - ez nem jó, ha több utasítás kell a then-ágba
		var ret = s"nincs már $pill kulcsú objektum a listában"
		if (!tartosWeblapok.contains(pill)) return ret	// ehhez kell a : String is felül
		//és innen már van tartosWeblapok[pill]
		 println (s"modell.feldolg($pill) hívás");
		lap = tartosWeblapok.apply(pill)
		ret = lap.feldolg()
		lap.close()
		//lehet, hogy scalátlan, de világos
		
		//ezt lehet, külön tagba kéne tenni, amit az ajaxos .scala.html lap explicite hív (onunload-ból?):
		tartosWeblapok -= pill
		println(s"${tartosWeblapok.size} eleme van még a listának")
		ret
	}
	
	def getURL = lap.getURL
	def getS = lap.getS
	def urlEgyebAttr = lap.urlEgyebAttr
	def sEgyebAttr = lap.sEgyebAttr
	
	def cimForm(formAttr: String, url:String, urlAttr:String, s:String, sAttr:String, pluszelem:String):String
	= s"""
<form $formAttr>
	<br>url<input name="url" value="$url" size="100" $urlAttr>
	<!-- kukik? -->
	<br>s<input name="s" value="$s" $sAttr>
	$pluszelem
</form>
"""

	def ajaxForm (wParams: Map[String, Array[String]], formAttr: String, pluszelem: String): String =
    cimForm( formAttr  //pl. id=... action=... target=...
           , (Option(wParams.get("url")) getOrElse Array(""))(0)
           , "" //további attribútum(ok) az url-hoz, pl. readonly
           , (Option(wParams.get("s")) getOrElse Array(""))(0)
           , ""
           , pluszelem //további inputok (pl. kuki), nyomógomb...
           )

	def ajaxForm (wParams: Map[String, Array[String]]): String = ajaxForm(wParams, "id=inicform action=weblapajaxinic", "")
	def ajaxAlapraForm (wParams: Map[String, Array[String]]): String = ajaxForm(wParams, "action=weblapajax", """<br><input type="submit" name="mehet" value="alapra">""")
/*
	def ajaxForm (wParams: Map[String, Array[String]], act: String, pluszelem: String): String =
    Szovegek.cimForm( act  //action= a formba, lehet utána más is, pl. target=
                    , if (wParams.containsKey("url")) wParams.get("url")(0) else ""
                    , "" //további attribútum(ok) az url-hoz, pl. readonly
                    , if (wParams.containsKey("s")) wParams.get("s")(0) else ""
                    , ""
                    , pluszelem //további inputok (pl. kuki), nyomógomb...
                    )
	def ajaxForm (wParams: Map[String, Array[String]]): String = ajaxForm(wParams, "weblapajaxinic", "")
	def ajaxAlapraForm (wParams: Map[String, Array[String]]): String = ajaxForm(wParams, "weblapajax", """<br><input type="submit" name="mehet" value="alapra">""")
 */
}
