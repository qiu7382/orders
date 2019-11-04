$(function(){
    var orderNo = GetQueryString("orderNo");
    $.ajax({
        url: ReqUrl.ToWxPayUrl() + "wxpay/createqccode/" + orderNo,
        method: "get",
        success: function (data) {
            debugger;
            new QRCode(document.getElementById('qrcode'),data.data.codeUrl);
        }
    });
    }
)