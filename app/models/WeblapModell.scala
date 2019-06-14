package models

//import java.util.HashMap
import java.util.Map	//ha azt mondtam a Controllerben, hogy java.util.Map, akkor java.util.Map (a scala.collection.immutable.Map más)
import scala.collection.JavaConverters.mutableMapAsJavaMap

import client._

import SajatDriver._  // a client._ nem elég pl. a meghajtoNyit-ra való hivatkozáshoz

object WeblapModell
{
	//var lap: Weblap = null //de aszongyák, kerüljük a nullt
	//var lap = new Weblap(mutableMapAsJavaMap(collection.mutable.Map("" -> Array(""))))  //hát nesztek :)
	var lap = new Weblap()
	var tartosWeblapok = new scala.collection.mutable.HashMap[Long, Weblap]()	//ajaxoshoz
	
	def tartosWeblapokStatusz: String =
	{
/*
		//def tartosWeblapbolHtml(sor:Tuple2[Long,Weblap]) =
		def tartosWeblapbolHtml(sor:(Long,Weblap)) =
		{
			val pill=sor._1
			val lap=sor._2
			s"""$pill ${java.time.Instant.ofEpochMilli(pill)}
					|<button onclick="feldolgajaxhivas($pill, '')">Újrafeldolg</button>
					|<button onclick="feldolgajaxhivas($pill, '&muvelet=csuk')">Csuk</button>
					|""".stripMargin
		}
*/
		tartosWeblapok
		//.map(tartosWeblapbolHtml)  na, ehelyett van a case		https://twitter.github.io/scala_school/collections.html#vsMap
		.map{ case (pill, lap) =>		//még csak zárójelbe se kell tenni a {...}-t
					s"""$pill ${java.time.Instant.ofEpochMilli(pill)} ${lap.getS} ${lap.getURL}
							|<button onclick="feldolgajaxhivas($pill, '')">Újrafeldolg</button>
							|<button onclick="feldolgajaxhivas($pill, '&muvelet=csuk')">Csuk</button>
							|""".stripMargin
				}
		.foldLeft("<br>")(_ + "<br>" + _)
	}

	def inic (wParams: Map[String, Array[String]]): String =	//ezt kell hívni elsőnek a .scala.html lapról
	{
		var s = ""
		//try { s = wParams.get("s")(0) } catch {case e:NullPointerException => }   vagy: (miért nem mindig így csináltam...)
		if (wParams.containsKey("s")) s = wParams.get("s")(0)		//vagy miért nem így: var s = (Option(wParams.get("s")) getOrElse Array(""))(0)

		lap = Weblap.apply(wParams, s)		// Weblap(wParams, s) nem műx: class client.Weblap is not a value
		lap.getClass + " példányosítva"
	}

	def inicAjax (wParams: Map[String, Array[String]]): String =	//ezt kell hívni elsőnek az ajaxos .scala.html lapról
	{
		var ret = "ajaxinic¤" + inic(wParams)  //ettől lesz egy weblap a lap-ban
		var pillInstant = java.time.Instant.now
		var pill = pillInstant.toEpochMilli
    lap.setInicPill(pillInstant)  //igazából a konstruktorban lenne a helye
    //**/ println("inicEredm="+lap.getInicEredm)
		tartosWeblapok.put(pill, lap)
		ret += s"<div><span id=idoLong>$pill</span> <span id=idoISO>$pillInstant</span></div>"
		ret += "<pre>" + lap.getInicEredm + "</pre>"
		ret
	}
	
	def feldolg /*(wParams: Map[String, Array[String]]): String*/ = lap.feldolg
	// de valsz. nem kell neki a paraméter, mert már az inic-kor eltette magának
	
	def feldolg(pill: Long, muvelet: String): String =
	{
		muvelet match
		{
			case "csuk" => csuk(pill)
			case "statusz" => tartosWeblapokStatusz
			case _ => feldolg(pill)
		}
	}

	def feldolg(pill: Long): String =
	{
		//var ret = if (tartosWeblapok.contains(pill)) tartosWeblapok.apply(pill).feldolg else s"nincs már $pill kulcsú objektum a listában" - ez nem jó, ha több utasítás kell a then-ágba
		var ret = s"nincs már $pill kulcsú objektum a listában" + tartosWeblapokStatusz
		if (!tartosWeblapok.contains(pill)) return ret	// ehhez kell a : String is felül
		//és innen már van tartosWeblapok[pill]
		 println (s"modell.feldolg($pill) hívás");
		lap = tartosWeblapok.apply(pill)
		ret = lap.feldolg()
		//lap.close()
		//lehet, hogy scalátlan, de világos
		
		ret //+ tartosWeblapokStatusz
	}

	def csuk(pill: Long) =
	{
		//ezt lehet, külön tagba kéne tenni, amit az ajaxos .scala.html lap explicite hív (onunload-ból?):
		tartosWeblapok(pill).close()
		tartosWeblapok -= pill
		println(s"${tartosWeblapok.size} eleme van még a listának")
		( s"$pill csukva" 
		+ tartosWeblapokStatusz
		)
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

}
