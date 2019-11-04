package zz.test.orders.user.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import zz.test.orders.common.bean.QgUser;
import zz.test.orders.common.service.RpcQgTokenService;
import zz.test.orders.common.service.RpcQgUserService;
import zz.test.orders.common.utils.Constants;
import zz.test.orders.common.utils.RedisUtils;
import zz.test.orders.user.service.QgLoginService;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2018/1/8.
 */
@Service
public class QgLoginServiceImpl implements QgLoginService {
    private Logger logger = LoggerFactory.getLogger(QgLoginServiceImpl.class);

    @Reference
    private RpcQgUserService rpcQgUserService;
    @Reference
    private RpcQgTokenService rpcQgTokenService;
    @Resource
    private RedisUtils redisUtils;

    @Override
    public QgUser login(String phone, String password) throws Exception {
        return rpcQgUserService.login(phone, password);
    }

    @Override
    public String generateToken(QgUser user) {
        return rpcQgTokenService.generateToken(user);
    }

    @Override
    public void save(String token, QgUser user) {
        redisUtils.set(token, Constants.Redis_Expire.SESSION_TIMEOUT, JSON.toJSONString(user));
    }

    @Override
    public void delete(String token) {
        if (redisUtils.exist(token))
            redisUtils.delete(token);
    }

    @Override
    public boolean validate(String token) {
        return redisUtils.validate(token);
    }

    @Override
    public QgUser findByWxUserId(String wxUserId) throws Exception {
        return rpcQgUserService.getQgUserByWxUserId(wxUserId);
    }

    @Override
    public Integer createQgUser(String wxUserId, String id) throws Exception {
        return rpcQgUserService.createQgUser(wxUserId, id);
    }
}
