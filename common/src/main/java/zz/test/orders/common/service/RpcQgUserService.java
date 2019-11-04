package zz.test.orders.common.service;
import zz.test.orders.common.bean.QgUser;

/**
 * Created by shang-pc on 2015/11/7.
 */
public interface RpcQgUserService {

    public QgUser getQgUserById(String id)throws Exception;

    public QgUser getQgUserByWxUserId(String wxUserId)throws Exception;

    public QgUser findByPhone(String phone) throws Exception;

    public QgUser login(String name, String password) throws Exception;

    public QgUser getCurrentUser(String tokenString) throws Exception;

    public Integer createQgUser(String wxUserId, String id) throws  Exception;
}
