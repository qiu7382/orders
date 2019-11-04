package zz.test.orders.common.service;


import zz.test.orders.common.bean.QgOrder;
import zz.test.orders.common.dto.QgGoodsMessageVo;
import zz.test.orders.common.utils.Dto;

import java.util.List;
import java.util.Map;

/**
 * Created by shang-pc on 2015/11/7.
 */
public interface RpcQgOrderService {
    /***
     * 生成订单
     * @return
     */
    public Dto createOrder(QgGoodsMessageVo qgGoodsMessageVo)throws Exception;
    /**
     * 根据订单编号加载订单信息
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
     * @param tradeNo 支付平台返回的交易码
     * @throws Exception
     */
    public void updateOrder(String orderNo, String tradeNo) throws Exception;
    /***
     * 根据订单id查询订单
     * @param id
     * @return
     * @throws Exception
     */
    public QgOrder getQgOrderById(String id)throws Exception;
    /****
     * 查询订单列表
     * @param param
     * @return
     * @throws Exception
     */
    public List<QgOrder>	getQgOrderListByMap(Map<String, Object> param)throws Exception;
    /***
     * 查询订单数目
     * @param param
     * @return
     * @throws Exception
     */
    public Integer getQgOrderCountByMap(Map<String, Object> param)throws Exception;

    /**
     * 刷新订单的状态
     * @return
     * @throws Exception
     */
    public boolean flushOrderStatus() throws Exception;
    /**
     * 获取被定时刷新的订单的列表
     * @return
     * @throws Exception
     */
    public List<QgOrder> getFlushCancelOrderList() throws Exception;
}
