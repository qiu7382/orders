package zz.test.orders.pay.wx.controller;

import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import zz.test.orders.common.bean.QgOrder;
import zz.test.orders.common.utils.Constants;
import zz.test.orders.common.utils.Dto;
import zz.test.orders.common.utils.DtoUtil;
import zz.test.orders.pay.wx.config.WXPayConfig;
import zz.test.orders.pay.wx.service.QgWxpayService;
import zz.test.orders.pay.wx.util.WXPayRequest;
import zz.test.orders.pay.wx.util.WXPayUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zezhong.shang on 17-8-11.
 */
@Controller
@RequestMapping("/api/wxpay/")
public class WxPaymentController {
    //**根据订单号生成二维码**//
    private Logger logger = Logger.getLogger(WxPaymentController.class);

    @Resource
    private QgWxpayService qgWxpayService;

    @Resource
    private WXPayConfig wxPayConfig;

    /**
     * 订单微信支付
     * @param orderNo
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value = "/createqccode/{orderNo}", method = RequestMethod.GET)
    @ResponseBody
    public Dto createQcCode(@PathVariable String orderNo, HttpServletResponse response, HttpServletRequest request) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        QgOrder order = null;
        //定义map类型的变量data，其中存放进行请求统一下单所需要的参数
        HashMap<String, String> data = new HashMap<String, String>();
        HashMap<String, Object> result = new HashMap<String, Object>();
        WXPayRequest wxPayRequest = new WXPayRequest(this.wxPayConfig);
        try {
            order = qgWxpayService.loadQgOrderByOrderNo(orderNo);
            if (order == null || order.getStatus() != 0) {
                return DtoUtil.returnFail("订单状态异常", Constants.WxpayStatus.DDZTYC);
            }
            data.put("body", "爱旅行项目订单支付");
            data.put("out_trade_no", orderNo);
            data.put("device_info", "");
            data.put("total_fee", "1");
            data.put("spbill_create_ip", "169.254.193.209");
            data.put("notify_url", "http://j19h691179.iok.la:15614/api/wxpay/notify");
            Map<String, String> r = wxPayRequest.unifiedorder(data);
            String resultCode = r.get("result_code");
            if (resultCode.equals("SUCCESS")) {
                result.put("goodsId", order.getGoodsId());
                result.put("num", order.getNum());
                result.put("amount", order.getAmount());
                result.put("codeUrl", r.get("code_url"));
                return DtoUtil.returnDataSuccess(result);
            } else {
                logger.info(r.get("return_msg"));
                return DtoUtil.returnFail("订单支付异常", Constants.WxpayStatus.DDZFYC);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("订单运行异常", Constants.WxpayStatus.DDYXYC);
        }
    }
    /***
     * 
     * 微信支付轮询订单，查看订单是否支付成功
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/queryorderstatus/{orderNo}", method = RequestMethod.GET)
    @ResponseBody
    public Dto<QgOrder> queryOrderIsSuccess(@PathVariable String orderNo, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        QgOrder order = null;
        try {
            order = qgWxpayService.loadQgOrderByOrderNo(orderNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DtoUtil.returnDataSuccess(order);
    }

    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    @ResponseBody
    public String paymentCallBack(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        WXPayRequest wxPayRequest = new WXPayRequest(this.wxPayConfig);
        Map<String, String> result = new HashMap<String, String>();
        Map<String, String> params = null;
        String returnxml = "";
        try {
            InputStream inputStream;
            StringBuffer sb = new StringBuffer();
            //以字节流的形式读取request中的数据
            inputStream = request.getInputStream();
            String s;
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((s = in.readLine()) != null) {
                sb.append(s);
            }
            in.close();
            inputStream.close();
            params = WXPayUtil.xmlToMap(sb.toString());
            logger.info("1.notify-params>>>>>>>>>>>:" + params);
            //判断签名是否正确
            boolean flag = wxPayRequest.isResponseSignatureValid(params);
            logger.info("2.notify-flag:" + flag);
            if (flag) {
                String returnCode = params.get("return_code");
                logger.info("3.returnCode:" + returnCode);
                if (returnCode.equals("SUCCESS")) {
                    //获取微信订单号
                    String transactionId = params.get("transaction_id");
                    //获取商户订单号
                    String outTradeNo = params.get("out_trade_no");
                    if (!qgWxpayService.processed(outTradeNo)) {
                        qgWxpayService.insertTrade(outTradeNo, transactionId);
                        logger.info("修改订单状态====================================");
                    }
                    logger.info("4.订单：" + outTradeNo + " 交易完成" + ">>>" + transactionId);
                } else {
                    result.put("return_code", "FAIL");
                    result.put("return_msg", "支付失败");
                    logger.info("");
                }
            } else {
                result.put("return_code", "FAIL");
                result.put("return_msg", "签名失败");
                logger.info("签名验证失败>>>>>>>>>>>>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            returnxml = WXPayUtil.mapToXml(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnxml;
    }
}
