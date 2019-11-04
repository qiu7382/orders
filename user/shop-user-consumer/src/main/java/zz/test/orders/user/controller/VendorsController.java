package zz.test.orders.user.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import zz.test.orders.common.bean.QgUser;
import zz.test.orders.common.utils.Dto;
import zz.test.orders.common.utils.DtoUtil;
import zz.test.orders.common.utils.IdWorker;
import zz.test.orders.common.utils.UrlUtils;
import zz.test.orders.user.service.QgLoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 第三方登录控制器
 * @author hduser
 *
 */
@Controller
@RequestMapping(value = "/vendors")
public class VendorsController {

	@Resource
	private QgLoginService qgLoginService;

	/**
	 * 微信登录——第一步：获取code
	 * @param response
	 */
	@RequestMapping(value = "/wechat/login")
	public void wechatLogin(HttpServletResponse response){
		String qrconnect="https://open.weixin.qq.com/connect/qrconnect?appid=wx9168f76f000a0d4c&redirect_uri=http%3a%2f%2fj19h691179.iok.la%3a27105%2fvendors%2fwechat%2fcallback&response_type=code&scope=snsapi_login&state=STATE#wechat_redirect";
		try {
			response.sendRedirect(qrconnect);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 微信登录——第二步：通过code换取access_token
	 * @param code
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/wechat/callback")
	public void wechatCallback(@RequestParam String code, HttpServletRequest request, HttpServletResponse response) throws IOException{
		String accessTokenUrl="https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx9168f76f000a0d4c&secret=8ba69d5639242c3bd3a69dffe84336c1&code="+
				code+"&grant_type=authorization_code";	
		response.setContentType("text/html;charset=utf-8");
		String json= UrlUtils.loadURL(accessTokenUrl);
		Map<String,Object> wechatToken=JSON.parseObject(json, Map.class);
		
		try {
			//验证本地库是否存在该用户
			QgUser user=qgLoginService.findByWxUserId(wechatToken.get("openid").toString());
			if(user==null){//如果不存在则添加用户
                user = new QgUser();
                user.setWxUserId(wechatToken.get("openid").toString());
                String id = IdWorker.getId();
                user.setId(id);
                qgLoginService.createQgUser(wechatToken.get("openid").toString(), id);
			}
			String token = qgLoginService.generateToken(user);
            qgLoginService.save(token, user);
			//返回前端处理
			StringBuilder loginPage=new StringBuilder();
			loginPage.append("http://j19h691179.iok.la:15614/index.html");
			loginPage.append("?user_type=1&token="+token);
			loginPage.append("&access_token="+wechatToken.get("access_token").toString());
			loginPage.append("&expires_in="+wechatToken.get("expires_in").toString());
			loginPage.append("&refresh_token="+wechatToken.get("refresh_token").toString());
			loginPage.append("&openid="+wechatToken.get("openid").toString());
			response.sendRedirect(loginPage.toString());
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
	}
	/**
	 * 获取微信用户信息
	 * @param accessToken 微信会话凭据
	 * @param openid 微信用户唯一标识
	 * @return
	 */
	@RequestMapping(value = "/wechat/user/info", method = RequestMethod.GET,produces= "application/json")
	public @ResponseBody
	Dto wechatUserInfo(
			@RequestParam String accessToken,
			@RequestParam String openid){
		try {
			//加载用户信息
			String userInfoJson=UrlUtils.loadURL("https://api.weixin.qq.com/sns/userinfo?access_token="
					+accessToken
					+"&openid="+ openid
					);
			Map<String,Object> userInfo=JSON.parseObject(userInfoJson, Map.class);
			return DtoUtil.returnDataSuccess(userInfo);
		} catch (Exception e) {			
			e.printStackTrace();
			return DtoUtil.returnFail(e.getMessage(), "授权失败");
		}
	}
	/**
	 * 会话保持
	 * 1、保持和itrip的会话
	 * 2、保持和wechat的会话
	 * @return
	 */
//	@RequestMapping(value = "/wechat/token/refresh", method = RequestMethod.POST,produces= "application/json")
//	public @ResponseBody
//    Dto wechatRefreshToken(HttpServletRequest request, HttpServletResponse response){
//
//		String agent=request.getHeader("user-agent");
//		String token=request.getHeader("token");
//		String refreshToken=request.getHeader("refreshtoken");//此处header中的名称使用“_”不能获取
//
//		try {
//			//2、保持和wechat的会话
//			String refreshTokenUrl="https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=wx9168f76f000a0d4c&grant_type=refresh_token&refresh_token="+refreshToken.trim();
//
//			String json=UrlUtils.loadURL(refreshTokenUrl);
//			Map<String,Object> wechatToken=JSON.parseObject(json, Map.class);
//			if(null!=wechatToken.get("errcode")){
//				return DtoUtil.returnFail(wechatToken.get("errmsg").toString(), "置换token失败");
//			}
//
//			//返回ItripWechatTokenVO (整合了本地会话与微信会话)
//			ItripWechatTokenVO wechatTokenVO=new ItripWechatTokenVO(token,
//					Calendar.getInstance().getTimeInMillis()+TokenService.SESSION_TIMEOUT*1000,//2h有效期
//					Calendar.getInstance().getTimeInMillis());
//			wechatTokenVO.setAccessToken(wechatToken.get("access_token").toString());
//			wechatTokenVO.setExpiresIn(wechatToken.get("expires_in").toString());
//			wechatTokenVO.setRefreshToken(wechatToken.get("refresh_token").toString());
//			wechatTokenVO.setOpenid(wechatToken.get("openid").toString());
//			return DtoUtil.returnDataSuccess(wechatTokenVO);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_REPLACEMENT_FAILED);
//		}
//
//	}
}

