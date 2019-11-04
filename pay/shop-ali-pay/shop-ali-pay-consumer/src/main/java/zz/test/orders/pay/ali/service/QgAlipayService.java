package zz.test.orders.pay.ali.service;

import zz.test.orders.common.bean.QgOrder;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/8.
 */
public interface QgAlipayService {
    public Integer insertTrade(String orderNo, String tradeNo) throws Exception;
    /**
     * 根据订单编号加载订单信息
     * @param orderNo
     * @return
     * @throws Exception
     */
    public QgOrder loadQgOrderByOrderNo(String orderNo) throws Exception;

    /**
     * 判断该订单是否已被处理过（被更新为已支付状态）
     * @param orderNo
     * @return
     * @throws Exception
     */
    public boolean processed(String orderNo) throws Exception;
    /**
     * 支付成功
     * @param orderNo 订单编号
     * @param payType 支付方式:1:支付宝 2:微信
     * @param tradeNo 支付平台返回的交易码
     * @throws Exception
     */
//    public void payFailed(String orderNo, int payType, String tradeNo) throws Exception;
//    /**
//     * 修改商品库存
//     * @param orderNo
//     * @throws Exception
//     */
//    public Integer updateGoods(String orderNo) throws Exception;
//
//    /**
//     * 支付成功
//     * @param orderNo 订单编号
//     * @param tradeNo 支付平台返回的交易码
//     * @throws Exception
//     */
//    public void updateOrder(String orderNo, String tradeNo) throws Exception;

    /**
     * 支付成功之后支付宝异步返回参数给商户
     * @param params
     * @return
     * @throws Exception
     */
    public boolean asyncVerifyResult(Map<String, Object> params) throws Exception;

    /**
     * 支付成功之后支付宝同步返回参数给商户
     * @param params
     * @return
     * @throws Exception
     */
    public boolean syncVerifyResult(Map<String, String[]> params) throws Exception;
}
