package zz.test.orders.common.service;

import zz.test.orders.common.dto.QgGoodsMessageVo;

/***
 * 发送消息的接口
 */
public interface RpcMessageService {
    /***
     * 记录抢购消息
     */
    public void recordQgMessageLog(QgGoodsMessageVo QgGoodsMessage) throws Exception;
}
