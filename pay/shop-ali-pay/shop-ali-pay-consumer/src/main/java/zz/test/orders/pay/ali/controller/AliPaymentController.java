package zz.test.orders.pay.ali.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import zz.test.orders.common.bean.QgOrder;
import zz.test.orders.common.utils.Dto;
import zz.test.orders.common.utils.DtoUtil;
import zz.test.orders.common.utils.EmptyUtils;
import zz.test.orders.pay.ali.config.AlipayConfig;
import zz.test.orders.pay.ali.service.QgAlipayService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 支付处理控制器
 *
 * @author hduser
 *
 */
@Controller
@RequestMapping("/api")
public class AliPaymentController {

	private Logger logger=Logger.getLogger(AliPaymentController.class);

	@Resource
	private AlipayConfig alipayConfig;

	@Resource
    private QgAlipayService qgAlipayService;

	/**
	 * 确认订单信息
	 *
	 * @param orderNo
	 *            订单ID
	 * @return
	 */
	@RequestMapping(value = "/prepay/{orderNo}", method = RequestMethod.GET)
    @ResponseBody
	public Dto prePay(@PathVariable String orderNo, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			QgOrder order = qgAlipayService.loadQgOrderByOrderNo(orderNo);
			if (!EmptyUtils.isEmpty(order)) {
				Map<String,Object> result=new HashMap<String, Object>();
				result.put("orderNo", orderNo);
				result.put("goodsId", order.getGoodsId());
				result.put("count", order.getNum());
				result.put("payAmount", order.getAmount());
				return DtoUtil.returnSuccess("获取订单信息成功", result);
			}else
				return DtoUtil.returnFail("订单不存在","fail");
		} catch (Exception e) {
			e.printStackTrace();
			return DtoUtil.returnFail("获取订单信息失败","fail");
		}
	}

	/**
	 * 客户端提交订单支付请求，对该API的返回结果不用处理，浏览器将自动跳转至支付宝。
	 *
	 * @param WIDout_trade_no
	 *            商户订单号，商户网站订单系统中唯一订单号，必填
	 * @param WIDsubject
	 *            订单名称，必填
	 * @param WIDtotal_amount
	 *            付款金额，必填
	 */
	@RequestMapping(value = "/pay", method = RequestMethod.POST)
	public void pay(
			@RequestParam String WIDout_trade_no,
			@RequestParam String WIDsubject,
			@RequestParam String WIDtotal_amount, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
		String product_code = "FAST_INSTANT_TRADE_PAY";
		// SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
		// 调用RSA签名方式
		AlipayClient client = new DefaultAlipayClient(alipayConfig.getUrl(),
				alipayConfig.getAppID(), alipayConfig.getRsaPrivateKey(),
				alipayConfig.getFormat(), alipayConfig.getCharset(),
				alipayConfig.getAlipayPublicKey(), alipayConfig.getSignType());
        AlipayTradePagePayRequest  alipay_request = new AlipayTradePagePayRequest();

		// 封装请求支付信息
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
		model.setOutTradeNo(WIDout_trade_no);
		model.setSubject(WIDsubject);
		model.setTotalAmount(WIDtotal_amount);
		model.setProductCode(product_code);
		alipay_request.setBizModel(model);
		// 设置异步通知地址
		alipay_request.setNotifyUrl(alipayConfig.getNotifyUrl());
		// 设置同步地址
		alipay_request.setReturnUrl(alipayConfig.getReturnUrl());
		// form表单生产
		String form = "";
		try {
			// 调用SDK生成表单
			form = client.pageExecute(alipay_request).getBody();
			response.setContentType("text/html;charset="
					+ alipayConfig.getCharset());
			response.getWriter().write(form);// 直接将完整的表单html输出到页面
			response.getWriter().flush();
			response.getWriter().close();
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    /**
     * 导步通知，跟踪支付状态变更
     * @param request
     * @param response
     */
	@RequestMapping(value = "/notify", method = RequestMethod.POST)
	public void trackPaymentStatus(HttpServletRequest request,
			HttpServletResponse response) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
			// 获取支付宝POST过来反馈信息
			Map requestParams = request.getParameterMap();
            request.setCharacterEncoding("UTF-8");
			// 商户订单号
			String out_trade_no = request.getParameter("out_trade_no");
			// 支付宝交易号
			String trade_no = request.getParameter("trade_no");
			// 交易状态
			String trade_status = request.getParameter("trade_status");
            boolean verify_result = qgAlipayService.asyncVerifyResult(requestParams);
			if (verify_result) {// 验证成功
				response.getWriter().println("success"); // 请不要修改或删除
			} else {// 验证失败
				response.getWriter().println("fail");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());			
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
	}

	/**
	 * 支付宝页面跳转同步通知页面
	 */
	@RequestMapping(value = "/return", method = RequestMethod.GET)
	public void callback(HttpServletRequest request,
			HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			//获取支付宝GET过来反馈信息
			Map<String,String> params = new HashMap<String,String>();
            request.setCharacterEncoding("UTF-8");
			Map requestParams = request.getParameterMap();
			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			//商户订单号
			String out_trade_no = request.getParameter("out_trade_no");
			//支付宝交易号
			String trade_no = request.getParameter("trade_no");
			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
			//计算得出通知验证结果
			//boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
			boolean verify_result = qgAlipayService.syncVerifyResult(requestParams);
			if(verify_result){//验证成功
                if(!qgAlipayService.processed(out_trade_no)){
                    qgAlipayService.insertTrade(out_trade_no, trade_no);
                }
				String id=qgAlipayService.loadQgOrderByOrderNo(out_trade_no).getId().toString();
				//提示支付成功
				response.sendRedirect(String.format(alipayConfig.getPaymentSuccessUrl(), out_trade_no,id));
			}else{				
				//提示支付失败
				response.sendRedirect(alipayConfig.getPaymentFailureUrl());
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

    /**
     * 获取订单信息，用于支付完成之后的订单信息显示
     */
    @RequestMapping(value = "/getOrderInfo", method = RequestMethod.GET)
    @ResponseBody
    public Dto getOrderInfo(@RequestParam String orderNo,HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            //获取支付宝GET过来反馈信息
            QgOrder qgOrder = qgAlipayService.loadQgOrderByOrderNo(orderNo);
            return DtoUtil.returnSuccess("获取订单信息成功", qgOrder);
        } catch (Exception e){
            e.printStackTrace();
            return DtoUtil.returnFail("获取订单信息失败","fail");
        }
    }
}
