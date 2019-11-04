var environment=0; //0表示测试环境 1:线上环境
var ReqUrl={
    GoodsReqUrl:function () {
        return (environment==0?"http://127.0.0.1:8083/api/":"http://goods.local.com/api/");
    },
    OrdeReqUrl:function () {
        return (environment==0?"http://127.0.0.1:8084/api/":"http://order.local.com/api/");
    },
    LoginReqUrl:function() {
        return (environment==0?"http://localhost:8082/api/":"http://user.local.com/api/");
    },
    ToAliPayReqUrl:function () {
        return (environment==0?"http://localhost:8087/api/":"http://aliPay.local.com/api/");
    },
    ToWxPayUrl:function () {
        return (environment==0?"http://localhost:8088/api/":"http://wxPay.local.com/api/");
    }
};
window.alert=function (json) {
    var title=(json.title==null)?"温馨提示":json.title;
    layer.alert(json.content,{icon:1,title:title},function (index) {
        layer.close(index);
        if(json.ok!=null){
            json.ok();
        }
    });
};
window.confirm=function (json) {
    layer.confirm(json.content, {
        btn: ['确认','取消'] //按钮
    }, function(index){
        layer.close(index);
        if(json.ok!=null){
            json.ok();
        }
    }, function(index){
        layer.close(index);
        if(json.cancel!=null){
            json.cancel();
        }
    });
};
window.dialog=function(json){
    layer.open({
        type: 1,
        area: [json.width, json.height],
        shadeClose: true,
        content: '<div style="padding:20px;">'+json.content+'</div>'
    });
};
window.ajaxDialog=function (json) {
    $.ajax({
        type: 'POST',
        url: json.url,
        async:true,
        success: function (result) {
            layer.open({
                title:json.title,
                type: 1,
                area: [json.width, json.height],
                shadeClose: true,
                content: result
            });
        }
    });
};
window.loading=function (json) {
    var title=(json.title==null)?"温馨提示":json.title;
    layer.alert(json.content,{
        title:title,
        closeBtn: 0,
        btn:[]
    });
};
window.closeAll=function () {
    layer.closeAll();
};
//设置cookie
window.setCookie=function(c_name,c_value,expiredays){
    var exdate=new Date()
    exdate.setDate(exdate.getDate()+expiredays)
    document.cookie=c_name+ "=" +escape(c_value)+((expiredays==null) ? "" : ";expires="+exdate.toGMTString());
};
//读取cookie
window.getCookie=function (c_name) {
    if (document.cookie.length>0){
        c_start=document.cookie.indexOf(c_name + "=")
        if (c_start!=-1){
            c_start=c_start + c_name.length+1
            c_end=document.cookie.indexOf(";",c_start)
            if (c_end==-1) c_end=document.cookie.length
            return unescape(document.cookie.substring(c_start,c_end))
        }
    }
    return ""
};
//获取url请求中的参数
window.GetQueryString=function(name)
{
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null)return  unescape(r[2]); return null;
}