package zz.test.orders.goods.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import zz.test.orders.common.bean.QgUser;
import zz.test.orders.common.dao.QgUserMapper;
import zz.test.orders.common.utils.RedisUtils;


@Controller("/test")
public class TestController {

    @Autowired
    private MessageSendingOperations jmsMessagingTemplate;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private QgUserMapper qgUserMapper;

    @RequestMapping("send")
    public void testSend(){
        String message="我是测试";
        jmsMessagingTemplate.convertAndSend("test.queue",message);

    }

    @RequestMapping("/tokenCreate")
    public void tokenCreate() throws Exception{
        for (int i=2;i<10002;i++){
            QgUser qgUser=new QgUser();
            qgUser.setId(i+"");
            qgUser.setPhone("13366966561");
//          qgUserMapper.insertQgUser(qgUser);
            String token="token:"+i;
            redisUtils.set(token, JSONObject.toJSONString(qgUser));
        }
    }
}
