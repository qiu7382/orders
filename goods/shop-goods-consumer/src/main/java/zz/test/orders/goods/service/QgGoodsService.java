package zz.test.orders.goods.service;

import zz.test.orders.common.dto.QgGoodsVo;
import zz.test.orders.common.utils.Dto;

public interface QgGoodsService {

    public Dto<QgGoodsVo> queryQgGoodsById(String goodsId);

    public Dto sendQgGoodsMessage(String goodsId, String token) throws Exception;

    public void sendMessage();
}
