var timer1,timer2;
$(function () {
    loadGoods();
});
function loadGoods() {
    $.ajax({
        url:ReqUrl.GoodsReqUrl()+'queryGoodsById',
        type: 'post',
        dataType: 'json',
        data: {
            id:"1",
            token:getCookie("token")
        },
        success: function(result){
            if(result.success="true"){
                var goods=result.data;
                $("#goodsImg").attr('src',goods.goodsImg);
                $("#goodsName").html(goods.goodsName);
                $("#price").html("<h4>￥"+goods.price+"<span id=\"submit\" onclick=\"qgGoods(\'"+goods.id+"');\">抢[剩余"+(goods.currentStock)+"]</span></h4>");
            }else{
                alert({"title":"提示","content":"加载失败"});
            }
        },
        error: function(){
            alert({"title":"提示","content":"系统异常"});
        }
    })
};
//使用方法名字执行方法
timer1=window.setInterval(loadGoods,3000);
function qgGoods(id) {
    $.ajax({
        url:ReqUrl.GoodsReqUrl()+'sendQgGoodsMessage',
        type: 'post',
        dataType: 'json',
        data: {
            goodsId:id,
            token:getCookie("token")
        },
        success: function(result){
            if(result.success=="true"){
                loading({"title":"抢购提示","content":"排队成功，正在抢购中......"});
                timer2=window.setInterval(function(){
                    flushGoodsIsGet(id)
                },1000);
            }else{
                if(result.errorCode=="0002"){
                    alert({"title":"提示","content":"抢购失败"});
                }else if(result.errorCode=="3001"){
                    alert({"title":"提示","content":"用户已抢购，请勿重复抢购"});
                }else if(result.errorCode=="0001"){
                    window.location.href="login.html";
                }else if(result.errorCode=="3003"){
                    alert({"title":"提示","content":result.msg});
                }
            }
        },
        error: function(){
            alert({"title":"提示","content":"系统异常"});
        }
    })
}
function flushGoodsIsGet(id){
    $.ajax({
        url:ReqUrl.OrdeReqUrl()+'flushIsGet',
        type: 'post',
        dataType: 'json',
        data: {
            goodsId:id,
            token:getCookie("token")
        },
        success: function(result){
            if(result.success=="true"){
                var data=result.data;
                if(data=="-1"){ //重复抢购
                    window.clearInterval(timer2);
                    alert({"title":"提示","content":"用户已抢购，请勿重复抢购",ok:function () {
                        closeAll();
                    }});
                }else if(data=="2"){
                    window.clearInterval(timer2);
                    alert({"title":"提示","content":"已抢到商品,请前往订单页支付",ok:function () {
                        closeAll();
                        window.location.href="toPay.html";
                    }});
                }
            }else{
                window.clearInterval(timer2);
                if(result.errorCode!="0002"){
                    closeAll();
                    alert({"title":"提示","content":"抢购失败"});
                }else if(result.errorCode=="0001"){
                    window.location.href="login.html";
                }
            }
        },
        error: function(){
            alert({"title":"提示","content":"系统异常"});
        }
    })
}