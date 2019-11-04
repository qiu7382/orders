package zz.test.orders.pay.ali.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.alibaba.dubbo.config.annotation.Reference;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.alipay.api.internal.util.AlipaySignature;
import zz.test.orders.common.bean.QgOrder;
import zz.test.orders.common.dto.QgGoodsMessageVo;
import zz.test.orders.common.service.RpcQgAlipayService;
import zz.test.orders.common.service.RpcQgGoodsService;
import zz.test.orders.common.service.RpcQgOrderService;
import zz.test.orders.pay.ali.config.AlipayConfig;
import zz.test.orders.pay.ali.service.QgAlipayService;

/**
 * Created by Administrator on 2018/1/8.
 */
@Service
public class QgAlipayServiceImpl implements QgAlipayService {

    private static final Logger logger = LoggerFactory.getLogger(QgAlipayServiceImpl.class);

    @Reference
    private RpcQgOrderService rpcQgOrderService;
    @Reference
    private RpcQgGoodsService rpcQgGoodsService;
    @Reference
    private RpcQgAlipayService rpcQgTradeService;
    @Resource
    private AlipayConfig alipayConfig;

    @Override
    public Integer insertTrade(String orderNo, String tradeNo) throws Exception {
        QgOrder qgOrder = rpcQgOrderService.loadQgOrderByOrderNo(orderNo);
        QgGoodsMessageVo qgGoodsMessageVo = new QgGoodsMessageVo();
        qgGoodsMessageVo.setTradeNo(tradeNo);
        qgGoodsMessageVo.setOrderNo(orderNo);
        qgGoodsMessageVo.setGoodsId(qgOrder.getGoodsId());
        qgGoodsMessageVo.setUserId(qgOrder.getUserId());
        qgGoodsMessageVo.setAmount(qgOrder.getAmount());
        qgGoodsMessageVo.setNum(qgOrder.getNum());
        return rpcQgTradeService.insertTrade(qgGoodsMessageVo);
    }

    @Override
    public QgOrder loadQgOrderByOrderNo(String orderNo) throws Exception {
        return rpcQgOrderService.loadQgOrderByOrderNo(orderNo);
    }

    @Override
    public boolean processed(String orderNo) throws Exception {
        return rpcQgOrderService.processed(orderNo);
    }

    @Override
    public boolean asyncVerifyResult(Map<String, Object> params) throws Exception {
        Map<String, String> verifyParams = new HashMap<String, String>();
        Map<String, String[]> requestParams = (Map<String, String[]>)params.get("requestParams");
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            verifyParams.put(name, valueStr);
        }
        // 计算得出通知验证结果
        boolean verify_result = AlipaySignature.rsaCheckV1(verifyParams,alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), "RSA2");
        return verify_result;
    }
    public boolean syncVerifyResult(Map<String, String[]> requestParams) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        // 计算得出通知验证结果
        boolean verify_result = AlipaySignature.rsaCheckV1(params,
                alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), "RSA2");
        return verify_result;
    }
}
