$(function(){
	if (GetQueryString("result") == "error") {
		$("#login_hint").html('账号或密码错误');
	} else if (GetQueryString("result") == "noactive") {
		$("#login_hint").html('账号未激活。请登录邮箱激活账号。')
	} else {
		$("login_hint").html('&nbsp')
	};
})

function resendActiveMail(){
    $('#miscFunc').attr('disabled','disabled');
    $('#loginForm [name = "password"]').attr('disabled','disabled');
    if(!$([name = "YouXiang"]).val){
    	var userCode = getCookieContent('userCode');
    	if (userCode) {
    		$('[name = "YouXiang"]').val(userCode);
    		$('#login_hint').html('请确认这是您注册使用的邮箱');
    		$('[name = "YouXiang"]').css('background','#e4f1fd').focus();
    	} else {
    		$("#login_hint").html('请输入注册邮箱');
    		$('[name = "YouXiang"]').css('background','#e4f1fd').focus();
    	}
    }
    $.post('register','action=resendRegisterMail&YouXiang=' + $('[name="YouXiang"]').val(),function(data){
        if($.trim(data) != 'ok') {
        	alert(data);
        } else {
        	$("#login_hint").html('激活邮件已经发送到您的邮箱。请登录邮箱，激活账号。');
        }
    })
    $('#miscFunc').attr('disabled','');
    $('#loginForm [name = "password"]').attr('disabled','');
    
}