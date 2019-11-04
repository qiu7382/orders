package zz.test.orders.pay.wx.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import zz.test.orders.common.bean.QgOrder;
import zz.test.orders.common.dto.QgGoodsMessageVo;
import zz.test.orders.common.service.RpcQgGoodsService;
import zz.test.orders.common.service.RpcQgOrderService;
import zz.test.orders.common.service.RpcQgWxpayService;
import zz.test.orders.pay.wx.service.QgWxpayService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/22.
 */
@Service
public class QgWxpayServiceImpl implements QgWxpayService {
    private static final Logger logger = LoggerFactory.getLogger(QgWxpayServiceImpl.class);
    @Reference
    private RpcQgOrderService rpcQgOrderService;
    @Reference
    private RpcQgGoodsService rpcQgGoodsService;
    @Reference
    private RpcQgWxpayService rpcQgWxpayService;

    @Override
    public Integer insertTrade(String orderNo, String tradeNo) throws Exception {
        QgOrder qgOrder = rpcQgOrderService.loadQgOrderByOrderNo(orderNo);
        //如果订单已经支付则终止后续业务的执行
        if(1 == qgOrder.getStatus()){
            return 0;
        }
        QgGoodsMessageVo qgGoodsMessageVo = new QgGoodsMessageVo();
        qgGoodsMessageVo.setTradeNo(tradeNo);
        qgGoodsMessageVo.setOrderNo(orderNo);
        qgGoodsMessageVo.setGoodsId(qgOrder.getGoodsId());
        qgGoodsMessageVo.setUserId(qgOrder.getUserId());
        qgGoodsMessageVo.setAmount(qgOrder.getAmount());
        qgGoodsMessageVo.setNum(qgOrder.getNum());
        return rpcQgWxpayService.insertTrade(qgGoodsMessageVo);
    }

    @Override
    public QgOrder loadQgOrderByOrderNo(String orderNo) throws Exception {
        return rpcQgOrderService.loadQgOrderByOrderNo(orderNo);
    }

    @Override
    public boolean processed(String orderNo) throws Exception {
        return rpcQgOrderService.processed(orderNo);
    }
}
