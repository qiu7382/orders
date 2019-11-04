package zz.test.orders.common.service;

import zz.test.orders.common.dto.QgGoodsMessageVo;
import zz.test.orders.common.dto.QgGoodsVo;
import zz.test.orders.common.utils.Dto;

/***
 * 商品库存业务类
 */
public interface RpcQgGoodsService {
    //1、查询库存
    public int getGoodsStockByGoodsId(String goodsId)throws Exception;
    //2、减库存 锁
    public int queryAndMinusStock(QgGoodsMessageVo qgGoodsMessageVo)throws Exception;
    //3、查询商品 不锁
    public QgGoodsVo getQgGoodsById(String id) throws Exception;
    //4、更新商品状态 不锁
    public void updateUserQgStatus(String goodsId, String userId, String status)throws Exception;
    //5、更新库存状态
    public int updateStockStatus(String stockId, Integer status)throws Exception;
    //6、发送抢购商品消息
    public Dto sendQgGoodsMessage(QgGoodsMessageVo qgGoodsMessage) throws Exception;
    //7、发送生成订单消息
    public Dto sendOrderMessage(QgGoodsMessageVo qgGoodsMessage) throws Exception;
    //8、刷新增加库存
    public void flushAddGoods(String stockId) throws Exception;
}
