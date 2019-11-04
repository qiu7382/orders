package zz.test.orders.common.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import zz.test.orders.common.bean.QgUser;

@Mapper
public interface QgUserMapper {

    /**
     * 根据主键id查询用户记录
     * @param id
     * @return
     * @throws Exception
     */
	public QgUser getQgUserById(@Param(value = "id") String id)throws Exception;

	/**
     * 根据wxUserId查询用户记录
     * @param wxUserId
     * @return
     * @throws Exception
     */
	public QgUser getQgUserByWxUserId(@Param(value = "wxUserId") String wxUserId)throws Exception;

    /**
     * 根据电话查询用户
     * @param phone
     * @return
     * @throws Exception
     */
    public QgUser findByPhone(@Param(value = "phone") String phone) throws Exception;

    /**
     * 添加用户记录
     * @param qgUser
     * @return
     * @throws Exception
     */
    public Integer insertQgUser(QgUser qgUser)throws Exception;
}
