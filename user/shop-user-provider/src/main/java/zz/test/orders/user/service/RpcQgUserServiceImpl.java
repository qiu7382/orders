package zz.test.orders.user.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import zz.test.orders.common.bean.QgUser;
import zz.test.orders.common.dao.QgUserMapper;
import zz.test.orders.common.service.RpcQgUserService;
import zz.test.orders.common.utils.RedisUtils;

import javax.annotation.Resource;
import java.util.*;

@Component
@Service(interfaceClass= RpcQgUserService.class)
public class RpcQgUserServiceImpl implements RpcQgUserService {

    @Resource
    private QgUserMapper qgUserMapper;

    @Resource
    private RedisUtils redisUtils;


    public QgUser getQgUserById(String id) throws Exception{
        return qgUserMapper.getQgUserById(id);
    }

    @Override
    public QgUser findByPhone(String phone) throws Exception {
        return qgUserMapper.findByPhone(phone);
    }

    @Override
    public QgUser login(String phone, String password) throws Exception {
        QgUser user=this.findByPhone(phone);
        if(null!=user&&user.getPassword().equals(password)){
            return user;
        }else{
            return null;
        }
    }

    public QgUser getCurrentUser(String tokenString) throws Exception {
        QgUser qgUser = null;
        try{
            if(null == tokenString || "".equals(tokenString)){
                return null;
            }
            String userInfoJson = redisUtils.get(tokenString).toString();
            qgUser = JSONObject.parseObject(userInfoJson,QgUser.class);
        }catch(Exception e){
            e.printStackTrace();
            return qgUser;
        }
        return qgUser;
    }

    @Override
    public Integer createQgUser(String wxUserId, String id) throws Exception {
        QgUser qgUser = new QgUser();
        qgUser.setId(id);
        qgUser.setWxUserId(wxUserId);
        qgUser.setCreatedTime(new Date());
        return qgUserMapper.insertQgUser(qgUser);
    }

    public QgUser getQgUserByWxUserId(String id) throws Exception{
        return qgUserMapper.getQgUserByWxUserId(id);
    }
}
