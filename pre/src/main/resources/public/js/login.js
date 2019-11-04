$(function(){
    $("#btn").click(function(){
    var phoneVal = $("#phone").val();
    var passwordVal = $("#password").val();
    var token = getCookie("token");
        $.ajax({
            url : ReqUrl.LoginReqUrl()+"dologin",
            type : "post",
            dataType : "json",
            data : {
                phone : phoneVal,
                password : passwordVal,
                token : token
            },
            success: function(response, status, xhr){
                message = response.msg;
                if(message!=null){
                    alert({"title":"提示","content":message});
                }else{
                    setCookie("token",response.data.token);
                    window.location.href="index.html";
                }
            }
        });
    });
});