package zz.test.orders.common.dao;

import org.apache.ibatis.annotations.Mapper;
import zz.test.orders.common.bean.QgTrade;

@Mapper
public interface QgTradeMapper {

    /**
     *
     * @param qgTrade
     * @return
     * @throws Exception
     */
	public Integer insertQgTrade(QgTrade qgTrade)throws Exception;
}
