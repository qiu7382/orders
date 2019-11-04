package zz.test.orders.user.service;


import com.alibaba.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import zz.test.orders.common.bean.QgUser;
import zz.test.orders.common.service.RpcQgTokenService;
import zz.test.orders.common.utils.EmptyUtils;
import zz.test.orders.common.utils.MD5;
import zz.test.orders.common.utils.RedisUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Token管理接口相关业务服务实现
 * 
 * @author hduser
 *
 */
@Component
@Service(interfaceClass= RpcQgTokenService.class)
public class RpcQgTokenServiceImpl implements RpcQgTokenService {

	private Logger logger = LoggerFactory.getLogger(RpcQgTokenServiceImpl.class);
	@Resource
    private RedisUtils redisUtils;

	private String tokenPrefix = "token:";//统一加入 token前缀标识
	/***
	 * @param user 用户信息
	 * @return Token格式<br/>
	 * 	PC：“前缀PC-USERCODE-USERID-CREATIONDATE-RONDEM[6位]” 
	 *  <br/>
	 *  Android：“前缀ANDROID-USERCODE-USERID-CREATIONDATE-RONDEM[6位]”
	 */
	@Override
	public String generateToken(QgUser user) {
        StringBuilder sb = new StringBuilder();
        sb.append(tokenPrefix);//统一前缀
        sb.append(MD5.getMd5((EmptyUtils.isEmpty(user.getPhone())?user.getWxUserId():user.getPhone()),32) + "-");//加密用户手机号
        sb.append(user.getId() + "-");
        sb.append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        return sb.toString();
	}

//    @Override
//    public String replaceToken(String agent, String token)
//            throws TokenValidationFailedException {
//
//        // 验证旧token是否有效
//        if (!redisUtils.exist(token)) {// token不存在
//            throw new TokenValidationFailedException("未知的token或 token已过期");// 终止置换
//        }
//        Date TokenGenTime;// token生成时间
//        try {
//            String[] tokenDetails = token.split("-");
//            TokenGenTime = DateUtil.parse(tokenDetails[3], "yyyyMMddHHmmss");
//        } catch (ParseException e) {
//            // TODO Auto-generated catch block
//            throw new TokenValidationFailedException("token格式错误:" + token);
//        }
//
//        long passed = Calendar.getInstance().getTimeInMillis()
//                - TokenGenTime.getTime();// token已产生时间
//        if (passed < REPLACEMENT_PROTECTION_TIMEOUT * 1000) {// 置换保护期内
//            throw new TokenValidationFailedException("token处于置换保护期内，剩余"
//                    + (REPLACEMENT_PROTECTION_TIMEOUT * 1000 - passed) / 1000
//                    + "(s),禁止置换");
//        }
//        // 置换token
//        String newToken = "";
//        ItripUser user = this.load(token);
//        long ttl = redisAPI.ttl(token);// token有效期（剩余秒数 ）
//        if (ttl > 0 || ttl == -1) {// 兼容手机与PC端的token在有效期
//            newToken = this.generateToken(agent, user);
//            this.save(newToken, user);// 缓存新token
//            redisAPI.set(token, this.REPLACEMENT_DELAY,
//                    JSON.toJSONString(user));// 2分钟后旧token过期，注意手机端由永久有效变为2分钟（REPLACEMENT_DELAY默认值）后失效
//        } else {// 其它未考虑情况，不予置换
//            throw new TokenValidationFailedException("当前token的过期时间异常,禁止置换");
//        }
//        return newToken;
//    }
}
