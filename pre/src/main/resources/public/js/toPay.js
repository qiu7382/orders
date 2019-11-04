$(function () {
    loadOrders();
});
function loadOrders() {
    $.ajax({
        async: false,
        url: ReqUrl.OrdeReqUrl() + 'queryOrderList',
        type: 'post',
        dataType: 'json',
        data: {
            id: "1",
            token: getCookie("token")
        },
        success: function (result) {
            if (result.success == "true") {
                var list = result.data;
                var trs = $("#order-list-body");
                for (var i = 0; i < list.length; i++) {
                    var $tr = $("<tr></tr>");
                    var $orderNotd = $("<td class='orderNo'></td>");
                    var $goodsImgtd = $("<td></td>");
                    var $amounttd = $("<td></td>");
                    var $statustd = $("<td></td>");
                    var $operatortd = $("<td></td>");
                    $orderNotd.html(list[i].orderNo);
                    $goodsImgtd.html("<img src='" + list[i].goodsImg + "' width='100px;' height='100px'>");
                    $amounttd.html(list[i].amount);
                    $statustd.html(list[i].status == 1 ? "支付成功" : (list[i].status == 0?"待支付":"支付失败"));
                    $operatortd.html("<button id='" + list[i].orderNo +
                                    (list[i].status == 0 ?"' class='default-btn'":"' class='disable-btn' disabled")
                                    +">去支付</button>");
                    $tr.append($orderNotd);
                    $tr.append($goodsImgtd);
                    $tr.append($amounttd);
                    $tr.append($statustd);
                    $tr.append($operatortd);
                    trs.append($tr);
                }

            } else {
                if (result.errorCode == "0002") {
                    alert({"title": "提示", "content": "加载失败"});
                } else if (result.errorCode == "0001") {
                    window.location.href = "login.html";
                }
            }
            $('.default-btn').bind('click', toPay);
        },
        error: function () {
            alert({"title": "提示", "content": "系统异常"});
        }
    })
};

function toPay(e) {
    var orderNoval = this.id;
    $.ajax({
        url: ReqUrl.ToAliPayReqUrl()+"prepay/"+orderNoval,
        type: "get",
        dataType: "json",
        success: function (response, status, xhr) {
            var flag = response.success;
            var data = response.data;
            if (!flag) {
                alert({"title": "提示", "content": message});
            } else {
                window.location.href = "/choosePay.html?orderNo="
                    + data.orderNo + "&goodsId="
                    + data.goodsId + "&count="
                    + data.count + "&payAmount="
                    + data.payAmount;
            }
        }
    });
}

