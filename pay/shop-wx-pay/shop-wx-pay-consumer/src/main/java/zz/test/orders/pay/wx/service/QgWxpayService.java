package zz.test.orders.pay.wx.service;


import zz.test.orders.common.bean.QgOrder;

import java.util.Map;

/**
 * Created by Administrator on 2018/1/8.
 */
public interface QgWxpayService {
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
}
