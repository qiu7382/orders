$(function() {
    var orderNo = GetQueryString("orderNo");
    var goodsId = GetQueryString("goodsId");
    var count = GetQueryString("count");
    var payAmount = GetQueryString("payAmount");
    $("#bankvalue").html(payAmount);
    var html = "<table id='table-7'  cellspacing='100px' style='width: 90%;margin-bottom:20px;'>" +
        "<tr>" +
        "<td>订单编号：</td>" +
        "<td style='text-align: left'>" + orderNo+
        "<input type='hidden' name='WIDout_trade_no' value='" + orderNo + "'>" +
        "<input type='hidden' name='WIDsubject' value='" + goodsId + "'>" +
        "<input type='hidden' name='WIDtotal_amount' value='" + payAmount + "'>" +
        "</td>" +
        "</tr>" +
    // "<hr class='one_line'>" +
    "<tr><td>订单金额：</td>" +
    "<td style='text-align: left'>" + payAmount+
    "</td></tr>" +
    // "<hr class='one_line'>" +
    "</table>"
    // "<input type='submit' id='btn' style='width:150px' value='确认'>";
    $("#body").html(html);

    $("#wxzf").click(function(){
        $(this).css("border","1px solid #fbb161");
        $("#zfbzf").css("border","1px solid #dedddd");
        //点击微信支付时设置状态为1，用于立即支付时的跳转
        $("#zflag").html("1");
    });

    $("#zfbzf").click(function(){
        $(this).css("border","1px solid #fbb161");
        $("#wxzf").css("border","1px solid #dedddd");
        //点击支付宝支付时设置状态为2，用于立即支付时的跳转
        $("#zflag").html("2");
    });

    //立即支付点击事件
    $("#submitBtnBank").click(function(){
        var flag = $("#zflag").html();
        var urlVal;
        if(0 == flag){
            alert({"title": "提示", "content": "请选择支付方式！"});
        }else if(1 == flag){
            $.ajax({
                url: ReqUrl.ToWxPayUrl() + "wxpay/createqccode/" + orderNo,
                method: "get",
                success: function (data) {
                    $("#orderNoDiv").html(orderNo);
                    ShowDiv("showdiv","bgdiv");
                    //先清空qrcodediv中的内容，否则会叠加内容
                    $("#qrcode").html("");
                    new QRCode(document.getElementById('qrcode'),data.data.codeUrl);
                }
            });
            function queryOrder(){
                $.ajax({
                    url: ReqUrl.ToWxPayUrl() + "wxpay/queryorderstatus/" + orderNo,
                    method: "get",
                    success: function (result) {
                        if(result.success == 'true'){
                            var orderStatus = result.data.status;
                            if(orderStatus == 1){
                                window.location.href = "success.html?orderNo=" + orderNo;
                            }
                        }
                    }
                });
            }
            setInterval(queryOrder, 5000);
        }else if(2 == flag){
            //提交form表单
            $("#body").submit();
        }
    });
})

//支付成功事件
function paySuccess(){
    var orderNo =  $("#orderNoDiv").html();
    window.location.href = "/index.html";
}

//弹出窗口关闭
function closeWindow(){
    CloseDiv("showdiv","bgdiv");
}