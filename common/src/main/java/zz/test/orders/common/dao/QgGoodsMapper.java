package zz.test.orders.common.dao;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import zz.test.orders.common.bean.QgGoods;

@Mapper
public interface QgGoodsMapper {

	public QgGoods getQgGoodsById(@Param(value = "id") String id)throws Exception;

	public List<QgGoods>	getQgGoodsListByMap(Map<String, Object> param)throws Exception;

	public Integer getQgGoodsCountByMap(Map<String, Object> param)throws Exception;

	public Integer insertQgGoods(QgGoods qgGoods)throws Exception;

	public Integer updateQgGoods(QgGoods qgGoods)throws Exception;

	public Integer deleteQgGoodsById(@Param(value = "id") String id)throws Exception;

	public Integer batchDeleteQgGoods(Map<String, List<String>> params);

	public Integer lockStore(Map<String, Object> param) throws Exception;

	public Integer flushCancelLockGoodsStock(Map<String, Object> param) throws Exception;
}
