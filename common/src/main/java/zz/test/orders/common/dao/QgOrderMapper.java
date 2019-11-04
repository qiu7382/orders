package zz.test.orders.common.dao;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import zz.test.orders.common.bean.QgOrder;
// zz.test.orders.common.dao.QgOrderMapper.getQgOrderListByMap
@Mapper
public interface QgOrderMapper {

	public QgOrder getQgOrderById(@Param(value = "id") String id)throws Exception;

	public List<QgOrder> getQgOrderListByMap(Map<String, Object> param)throws Exception;

	public Integer getQgOrderCountByMap(Map<String, Object> param)throws Exception;

	public Integer insertQgOrder(QgOrder qgOrder)throws Exception;

	public Integer updateQgOrder(QgOrder qgOrder)throws Exception;

	public Integer deleteQgOrderById(@Param(value = "id") String id)throws Exception;

	public Integer batchDeleteQgOrder(Map<String, List<String>> params);

    /***
     * 刷新取消订单状态(用于定时程序)
     * @return
     * @throws Exception
     */
    public Integer flushCancelOrderStatus()throws Exception;

    /**
     * 获取刷新取消的订单的列表，用于定时任务更新商品的库存
     * @return
     * @throws Exception
     */
    public List<QgOrder> getFlushCancelOrderList() throws Exception;
}
