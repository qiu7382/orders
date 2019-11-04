package zz.test.orders.common.service;

import zz.test.orders.common.bean.QgGoodsMessage;

/***
 * 抢购消息业务接口
 */
public interface RpcQgGoodsMessageService {

    public int addQgGoodsMessage(QgGoodsMessage qgGoodsMessage)throws Exception;
}
