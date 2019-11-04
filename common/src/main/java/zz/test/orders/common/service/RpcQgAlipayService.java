package zz.test.orders.common.service;

import zz.test.orders.common.dto.QgGoodsMessageVo;

/**
* Created by shang-pc on 2015/11/7.
*/
public interface RpcQgAlipayService {
    /**
     * 添加一条交易记录
     * @param qgGoodsMessageVo
     * @return
     * @throws Exception
     */
    public Integer insertTrade(QgGoodsMessageVo qgGoodsMessageVo) throws Exception;
}
