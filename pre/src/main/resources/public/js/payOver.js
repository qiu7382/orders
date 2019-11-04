$(function () {
    debugger;
    var flag = $("#flag").val();
    var orderNoVal = GetQueryString("orderNo");
    if(flag == "true"){
        $.ajax({
            url: ReqUrl.ToAliPayReqUrl()+"getOrderInfo?orderNo="+orderNoVal,
            type: "get",
            dataType: "json",
            success: function (response, status, xhr) {
                debugger;
                var flag = response.success;
                var data = response.data;
                if (!flag) {
                    alert({"title": "提示", "content": message});
                } else {
                    $("#orderAmount").html(data.amount);
                    $("#orderNoArea").html(orderNoVal);
                }
            }
        });
    }else{
        $("#orderNoArea").html(orderNoVal);
    }
});