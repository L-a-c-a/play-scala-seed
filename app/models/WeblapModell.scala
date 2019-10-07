package models

//import java.util.HashMap
import java.util.Map  //ha azt mondtam a Controllerben, hogy java.util.Map, akkor java.util.Map (a scala.collection.immutable.Map más)
import scala.collection.JavaConverters.mutableMapAsJavaMap
import java.time.Instant

import client._

import SajatDriver._  // a client._ nem elég pl. a meghajtoNyit-ra való hivatkozáshoz

object WeblapModell
{
  //var lap: Weblap = null //de aszongyák, kerüljük a nullt
  //var lap = new Weblap(mutableMapAsJavaMap(collection.mutable.Map("" -> Array(""))))  //hát nesztek :)
  var lap = new Weblap()
  var tartosWeblapok = new scala.collection.mutable.HashMap[Long, Weblap]()  //ajaxoshoz

  def tartosWeblapokStatusz: String =
  {
/*
    //def tartosWeblapbolHtml(sor:Tuple2[Long,Weblap]) =
    def tartosWeblapbolHtml(sor:(Long,Weblap)) =
    {
      val pill=sor._1
      val lap=sor._2
      // állítólag e kettő helyett jó lett volna: val (pill, lap) = sor
      s"""$pill ${java.time.Instant.ofEpochMilli(pill)}
          |<button onclick="feldolgajaxhivas($pill, '')">Újrafeldolg</button>
          |<button onclick="feldolgajaxhivas($pill, '&muvelet=csuk')">Csuk</button>
          |""".stripMargin
    }
*/
    (    //az egész zárójelben a ;-okoskodás ellen (a + miatt)  (...de úgy meg "scrutinee is incompatible" hiba :( ...mindegy, a concat paraméterblokkja miatt is)
      tartosWeblapok
      //.map(tartosWeblapbolHtml)  na, ehelyett van a case    https://twitter.github.io/scala_school/collections.html#vsMap
      .map{ case (pill, lap) =>    //még csak zárójelbe se kell tenni a {...}-t
            //s"""$pill ${java.time.Instant.ofEpochMilli(pill)} ${lap.statusz}
            s"""$pill: ${lap.statusz}
                |<button onclick="feldolgajaxhivas($pill, '')">Újrafeldolg</button>
                |<button onclick="feldolgajaxhivas($pill, '&muvelet=csuk')">Csuk</button>
                |<button onclick="inicajaxhivas('weblapajaxinic?kopp=$pill&s='+document.forms.inicform.elements.s.value)">Kopp</button>
                |""".stripMargin
          }
      .foldLeft("<div>")(_ + "</div>\n<div>" + _) + "</div>" 
      //.concat (lap.asInstanceOf[WeblapSe].getDriver.statusz.toString)  //TERv: gatyábaráznivaló  --na, erre való a típus szerinti case
      .concat
      ( lap match    // a .concat()-ot lehet új sorba írni; a +-t nem (kivéve, ha zárójelben van az egész)
        {
          case lapu:WeblapSe => lapu.getDriver.statusz.toString
          case _ => ""
        }
      )
    )
  }

  def inic (wParams: Map[String, Array[String]]): String =  //ezt kell hívni elsőnek a .scala.html lapról
  {
    /***/ println(s"${Console.BLUE}${Console.BOLD}inic kezd: ${Instant.now}; ${WeblapSeJ.wParamsStr(wParams)}${Console.RESET}")
    var s = ""
    //try { s = wParams.get("s")(0) } catch {case e:NullPointerException => }   vagy: (miért nem mindig így csináltam...)
    if (wParams.containsKey("s")) s = wParams.get("s")(0)    //vagy miért nem így: var s = (Option(wParams.get("s")) getOrElse Array(""))(0)

    lap = Weblap.apply(wParams, s)    // Weblap(wParams, s) nem műx: class client.Weblap is not a value
    /***/ println(s"${Console.BLUE}${Console.BOLD}inic kész: ${Instant.now}; ${lap.getClass} példányosítva (${lap.getInicPill})${Console.RESET}")
    lap.getClass + " példányosítva"
    //.concat (lap.asInstanceOf[WeblapSe].getDriver.statusz.toString)  //TERv: gatyábaráznivaló 
  }

  var folyamatban: Option[Instant] = None // inicAjax valamiért újra és újra meghívódik

  def inicAjax (wParams: Map[String, Array[String]]): String =  //ezt kell hívni elsőnek az ajaxos .scala.html lapról
  {
    folyamatban match
    {
      case Some(p) =>
      {
        println (s"haggyámá, már csinálom $p ${p.toEpochMilli}")
        return s"ajaxinic¤folyamatban <span id=idoLong>${p.toEpochMilli}</span> <span id=idoISO>$p</span>"
      }
      case None =>
    }  //még mindig utálom a hosszú "else" és "case _" ágakat, ezért return
    val pillInstant = java.time.Instant.now
    folyamatban = Some(pillInstant)
    /***/ println(s"${Console.GREEN}inicAjax kezd: $pillInstant; ${WeblapSeJ.wParamsStr(wParams)}${Console.RESET}")
    val pill = pillInstant.toEpochMilli
    wParams.put("pill", Array(pill.toString))
    var ret = "ajaxinic¤" + inic(wParams)  //ettől lesz egy weblap a lap-ban
    //lap.setInicPill(pillInstant)  //igazából a konstruktorban lenne a helye
    //**/ println("inicEredm="+lap.getInicEredm)
    tartosWeblapok.put(pill, lap)
    ret += s"<div><span id=idoLong>$pill</span> <span id=idoISO>$pillInstant</span></div>"
    ret += /*"<pre>" +*/ lap.getInicEredm //+ "</pre>"
    //***/ println(s"${Console.GREEN}inicAjax kész: ${Instant.now}; ${ret take 50}...${ret takeRight 50}${Console.RESET}")
    /***/ println(s"${Console.GREEN}inicAjax kész: ${Instant.now}; ${ret.replaceFirst("tva<div>.*¤lapcim", "tva...\n...¤lapcim")}${Console.RESET}")
    folyamatban = None
    ret
  }

  def feldolg /*(wParams: Map[String, Array[String]]): String*/ = lap.feldolg()
  // de valsz. nem kell neki a paraméter, mert már az inic-kor eltette magának

  def feldolg(pill: Long, muvelet: String, par: String): String =   //ezt hívja weblapajaxfeldolg.scala.html
  {
    /***/ println(s"""${Console.YELLOW}${Console.BOLD}feldolg($pill, "$muvelet", "$par") hívva${Console.RESET}""")
    val ablakMuvRegex = "ablak(.*)".r  // pl. ablakCsuk --> case ablakMuvRegex(muv) => fn(muv)  --> fn("Csuk")
    muvelet match
    {
      case "csuk" => csuk(pill)
      case "statusz" => tartosWeblapokStatusz
      case "katt" => "lapcim¤" + tartosWeblapok(pill).katt(par)  //pill + " meg van kattintva"
      case ablakMuvRegex(muv) => meghajto.ablakMuv(pill, muv, par)
      case _ => feldolg(pill)
    }
  }

  def feldolg(pill: Long) =
  {
    /***/ println (s"modell.feldolg($pill) hívás");
    if (!tartosWeblapok.contains(pill)) s"nincs már $pill kulcsú objektum a listában" //+ tartosWeblapokStatusz ??
    else
    {
      lap = tartosWeblapok(pill)
      lap.feldolg()
      //lap.close()   ha ez nincs, kell-e a lap-ba betenni? kell-e egyáltalán a lap?
    }
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
