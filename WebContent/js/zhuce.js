window.onload = function() {
	var isXiuGai = false;
	if (GetQueryString("action") == "xiugai") {isXiuGai = true;}
	$.formValidator.initConfig({
		autotip : true,
		formID : "regForm",
	    onError:function(msg,obj,errorlist){
	    	$("#errorlist").empty();
	    	$("#errorlist").map(function(msg){
	    		$("#errorlist").append("<li>"+msg+"</li>")
	    	})
	    }	
    })
    
    if (isXiuGai){
    	$('#MiMaRow').hide();
    	$('#repasswordRow').hide();
    	document.getElementById('regForm').action = "register?action=updateAcct";
    	$('#miscFunc').text('确认修改');
    	$('#ZhuCeYouXiang').val(getCookieContent('UserCode'));
    	$('#XingMing').val(getCookieContent('UserName'));
		$("#ZhuCeYouXiang").attr('disabled', 'disabled');
		$("#XingMing").attr('disabled', 'disabled');
		$.post('register','action=getAcct',function(data){
			if($.trim(data) != 'notLogIn') {
				var params = data.split('^');
				for (var i = 0;i < params.length;i++) {
					para = params[i].split('~');
					$('[name="'+para[0]+'"]').val(para[1])
				}
			} else {
				alert("还没登录");
			}
		})
    	$("#msg_Title").html('修改资料');
		$("#login-Tip").hide();
    } else {
    	 $("#ZhuCeYouXiang").formValidator({
    	    	onShow : "必填",
    	    	onCorrect : "&nbsp;"
    	    }).regexValidator({
    	    	regExp : "^([\\w-.]+)@(([[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.)|(([\\w-]+.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(]?)$",
    	    	onError : function(){
    	    		$("ZhuCeYouXiang").addClass("");
    	    		return "邮箱格式不正确"
    	    	} 
    	    }).ajaxValidator({
    	    	dataType : "json",
    	    	async : true,
    	    	url : 'register',
    	    	type : 'POST',
    	    	cache : false,
    	    	data : 'action=verifyEmail',
    	    	success : function(data){
    	    		if( data == "0") {
    	    			return true;
    	    			return "此邮箱已登记";
    	    		};
    	    	}
    	    });
    		
    		$("#MiMa").formValidator({
    			onShow : "必填",
    			onCorrect : "&nbsp;",
    		}).inputValidator({
    			min:6,max:45,onError:"输入6~25位密码"
    		}).regexValidator({
    			regExp:"^(?!(?:123456|654321)$)",
    			onError:"不能为简单密码"
    		});
    		
    		$("#repassword").formValidator({
    			onShow : "必填",
    			onCorrect : "&nbsp;",
    		}).compareValidator({
    			desID:"MiMa",
    			operateor:"=",
    			onError:"两次密码不一致"
    		});
    		
    		$("#XingMing").formValidator({
    			onShow : "必填",
    			onCorrect : "&nbsp;",
    		}).inputValidator({
    			min : 5,
    			max : 12,
    			onError : "5~12位字符"
    		}).ajaxValidator({
    			dataType : "json",
    			async : true,
    			url : 'register',
    			type : 'POST',
    			cache : false,
    			data : 'action=verifyName',
    			success : function(data){
    				if( data == "0" ) return true;
    				return "此名称已登记，请更换";
    			},
    		})
    		
    	  	$("#Mobile").formValidator({
    	  		onShow : "必填",
    	  		onCorrect : "&nbsp;"
    	  	}).inputValidator({
    	  		min : 11,
    	  		max : 11,
    	  		onError : "手机号码不正确"
    	  	}).regexValidator({
    	  		regExp : "mobile",
    	  		dataType : "enum",
    	  		onError : "你输入的手机号码格式不正确"
    	  	});

    		$("#GongSiMingCheng").formValidator({
    			onShow : "必填",
    			onCorrect : "&nbsp;"
    		}).inputValidator({
    			min : 2,
    			onError : "请告诉我们贵公司的宝号"
    		});
    }
}