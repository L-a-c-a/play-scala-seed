package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import collection.JavaConverters._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

//innentől én

  def q() = Action
  { implicit request =>
    //Ok("Got request [" + request + "]")  // Got request [GET /q]
    //Ok("req. body: [" + request.body + "]") // req. body: [AnyContentAsEmpty]  akkor is, ha paraméterekkel hívom - mondjuk érthető, ha GETtel hívtam
    //Ok("req.queryString: [" + request.queryString + "]") // req.queryString: [Map(a -> List(b), c -> List(d))]   mert  queryString: Map[String, Seq[String]]
    //Ok("req.rawQueryString: [" + request.rawQueryString + "]") // req.rawQueryString: [a=b&c=d]
    //további ilyenek: req
    var wParams = HomeController.lapParam(request)
    Ok(s"""<pre>
      |c0=${wParams.get("c")(0)}
      |c1=${wParams.get("c")(1)}
      |host=${wParams.get("host")(0)}
      |uri=${wParams.get("uri")(0)}
      |</pre>""".stripMargin)
  }

  def req() = Action
  { implicit request =>
    Ok(views.html.req(request))
  }

  def map() = Action
  { implicit request =>
    Ok(views.html.map(HomeController.lapParam(request)))
  }

  def weblap() = Action
  { implicit request =>
    Ok(views.html.weblap(HomeController.lapParam(request)))
  }

  def weblapAjax() = Action
  { implicit request =>
    Ok(views.html.weblapajax(HomeController.lapParam(request)))
  }

  def weblapAjaxInic() = Action
  { implicit request =>
    Ok(views.html.weblapajaxinic(HomeController.lapParam(request)))
  }

  def weblapAjaxFeldolg(pill: String, muvelet: String, par: String) = Action
  { implicit request =>
    Ok(views.html.weblapajaxfeldolgoz(pill, muvelet, par))
  }

}

object HomeController
{
  /**
   * ez lesz a .scala.html paramétere; uniform, akár jsp-ből, akár play-ből (java/scala)
   *
   * a hívásparaméterek (get: queryString, post: body().asFormUrlEncoded()),
   * hozzácsapva egy host= paraméter, hogy honnan hívták
   * ÉS MÉG hozzácsapva egy uri= paraméter, ha explicit form action kell
   *
   * jsp-ben (javax.servletizé) megfelel:{{{
   *<%! Map<String, String[]> frmbody; %>
   *<% frmbody = new HashMap<>(request.getParameterMap()); %>
   *<% frmbody.put("host", new String[]{request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()}); %> <%-- pl. http://szusza:8080 --%>
   *<% frmbody.put("uri", new String[]{request.getRequestURI()}); %> <%-- pl. /zzjsp/weblap.jsp --%>
   * }}}
   */
  def lapParam(req: Request[AnyContent]): java.util.Map[String, Array[String]] =
  {
    //var parMap: Map[String, Seq[String]] = if (req.method=="GET") req.queryString else req.body.asFormUrlEncoded.getOrElse(Map(""->Seq("")))
    var parMap = req.body.asFormUrlEncoded getOrElse req.queryString
    /***/ println (s"${Console.BOLD}HomeController.lapParam: parMap=$parMap req.path=${req.path} req.host= ${req.host}${Console.RESET}")
    var ret = new java.util.HashMap[String, Array[String]] (parMap.map { case (k,v) => (k, v.toArray) }.asJava)
    ret.put("host", Array((if (req.secure) "https://" else "http://") + req.host))
    ret.put("uri", Array(req.path))
    ret
  }
}
