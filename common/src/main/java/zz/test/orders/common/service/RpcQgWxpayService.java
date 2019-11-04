package zz.test.orders.common.service;
import zz.test.orders.common.dto.QgGoodsMessageVo;

/**
 * Created by Administrator on 2018/1/22.
 */
public interface RpcQgWxpayService {
    public Integer insertTrade(QgGoodsMessageVo qgGoodsMessageVo) throws Exception;
}

