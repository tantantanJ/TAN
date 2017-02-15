$(function(){
	$("#head_included").load("head.html")
})

//取URL后的参数 function GetQueryString(name)
//URL的参数&参数名1=XXXX&参数名2=XXXX&参数名3=XXXX  
//alert(GetQueryString("参数名2"));
function GetQueryString(name) {
var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
var r = window.location.search.substr(1).match(reg);
if (r != null)
    return unescape(r[2]);
return null;
}