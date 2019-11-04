package zz.test.orders.user.service;


import zz.test.orders.common.bean.QgUser;

/**
 * Created by Administrator on 2018/1/18.
 */
public interface QgLoginService {
    /**
     * 用户登录
     * @param phone
     * @param password
     * @return
     * @throws Exception
     */
    public QgUser login(String phone, String password) throws Exception;

    /**
     * 生成token
     * @param user 用户信息
     * @return Token格式<br/>
     * 		PC：“前缀PC-USERCODE-USERID-CREATIONDATE-RONDEM[6位]”
     *  	<BR/>
     *  	Android：“前缀ANDROID-USERCODE-USERID-CREATIONDATE-RONDEM[6位]”
     */
    public String generateToken(QgUser user);

    /**
     *保存token到redis中
     * @param token
     * @param user
     */
    public void save(String token, QgUser user);

    /**
     * 删除token
     * @param token
     */
    public void delete(String token);

    /**
     * 验证token的正确性
     * @param token
     * @return
     */
    public boolean validate(String token);

    /**
     * 根据wxUserId查询用户
     * @param wxUserId
     * @return
     * @throws Exception
     */
    public QgUser findByWxUserId(String wxUserId) throws Exception;

    /**
     * 创建微信登录用户信息
     * @param wxUserId
     * @return
     * @throws Exception
     */
    public Integer createQgUser(String wxUserId, String id) throws Exception;
}
