package zz.test.orders.common.service;


import zz.test.orders.common.bean.QgUser;

public interface RpcQgRedisService {

    public boolean set(String key, String value);

    public boolean set(String key, long seconds, String value);

    public boolean exist(String key);

    public Object get(String key);

    public void delete(String key);

    public boolean lock(String key);

    public void unlock(String key);

    public Object getValueNx(String key);

    public boolean validate(String agent, String token);

    public QgUser getCurrentUser(String agent, String tokenString);

    public QgUser getCurrentUser(String tokenString);
}
