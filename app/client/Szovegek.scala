package client
// amit java-ban nehézkes lenne definiálni

object Szovegek 
{
	def cimForm(act: String, url:String, urlAttr:String, s:String, sAttr:String, pluszelem:String):String = s"""
<form id=inicform action=$act>
	<br>url<input name="url" value="$url" size="100" $urlAttr>
	<!-- kukik? -->
	<br>s<input name="s" value="$s" $sAttr>
	$pluszelem
</form>
"""
//így, hogy id=konstans, hülyeség
//amúgy is scalából hülyeség idehivatkozni
}