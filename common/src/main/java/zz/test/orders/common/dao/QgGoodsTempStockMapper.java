package zz.test.orders.common.dao;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import zz.test.orders.common.bean.QgGoodsTempStock;

@Mapper
public interface QgGoodsTempStockMapper {

	public QgGoodsTempStock getQgGoodsTempStockById(@Param(value = "id") String id)throws Exception;

	public List<QgGoodsTempStock>	getQgGoodsTempStockListByMap(Map<String, Object> param)throws Exception;

	public Integer getQgGoodsTempStockCountByMap(Map<String, Object> param)throws Exception;

	public Integer insertQgGoodsTempStock(QgGoodsTempStock qgGoodsTempStock)throws Exception;

	public Integer updateQgGoodsTempStock(QgGoodsTempStock qgGoodsTempStock)throws Exception;

	public Integer deleteQgGoodsTempStockById(@Param(value = "id") String id)throws Exception;

	public Integer batchDeleteQgGoodsTempStock(Map<String, List<String>> params);

}
