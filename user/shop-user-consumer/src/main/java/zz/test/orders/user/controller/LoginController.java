package zz.test.orders.user.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import zz.test.orders.common.bean.QgUser;
import zz.test.orders.common.dto.TokenVO;
import zz.test.orders.common.utils.*;
import zz.test.orders.user.service.QgLoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;

/**
 * 用户登录控制器
 *
 * @author hduser
 */
@Controller
@RequestMapping(value = "/api")
@CrossOrigin(origins = "*")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Resource
    private QgLoginService qgLoginService;

    @RequestMapping(value = "/dologin", method = RequestMethod.POST)
    @ResponseBody
    public Dto dologin(String phone, String password, String token, HttpServletResponse response, HttpServletRequest request) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        if (!EmptyUtils.isEmpty(phone) && !EmptyUtils.isEmpty(password)) {
            QgUser user = null;
            try {
                user = qgLoginService.login(phone.trim(), password);
            } catch (UserLoginFailedException e) {
                return DtoUtil.returnFail(e.getMessage(), Constants.User_AUTH.AUTH_AUTHENTICATION_FAILED);
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.returnFail(e.getMessage(), Constants.User_AUTH.AUTH_UNKNOWN);
            }
            if (EmptyUtils.isNotEmpty(user)) {
                //删除已有的token
                if (EmptyUtils.isNotEmpty(token)) {
                    qgLoginService.delete(token);
                }
                String newToken = qgLoginService.generateToken(user);
                qgLoginService.save(newToken, user);
                //返回ItripTokenVO
                TokenVO tokenVO = new TokenVO(newToken,
                        Calendar.getInstance().getTimeInMillis() + Constants.Redis_Expire.SESSION_TIMEOUT * 1000,//2h有效期
                        Calendar.getInstance().getTimeInMillis());
                return DtoUtil.returnDataSuccess(tokenVO);
            } else {
                return DtoUtil.returnFail("用户名密码错误", Constants.User_AUTH.AUTH_AUTHENTICATION_FAILED);
            }
        } else {
            return DtoUtil.returnFail("参数错误！检查提交的参数名称是否正确。", Constants.User_AUTH.AUTH_PARAMETER_ERROR);
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET, produces = "application/json", headers = "token")
    @ResponseBody
    public Dto logout(HttpServletRequest request) {
        //验证token
        String token = request.getHeader("token");
        if (!qgLoginService.validate(token))
            return DtoUtil.returnFail("token无效", Constants.User_AUTH.AUTH_TOKEN_INVALID);
        try {
            //删除token和信息
            qgLoginService.delete(token);
            return DtoUtil.returnSuccess("注销成功");
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("注销失败", Constants.User_AUTH.AUTH_UNKNOWN);
        }
    }
}
