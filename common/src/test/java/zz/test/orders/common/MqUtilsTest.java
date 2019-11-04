package zz.test.orders.common;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.test.context.junit4.SpringRunner;
import zz.test.orders.common.dto.MQTestMessage;
import zz.test.orders.common.utils.MqUtils;
import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MqUtilsTest {

    Logger logger=Logger.getLogger(MqUtilsTest.class);

    @Resource
    MqUtils mqUtils;
    static final String QUEUE_NAME = "mq_queue_test";
    boolean receive = false;

    @Test
    public void sendMessage() throws InterruptedException {
        MQTestMessage msg = new MQTestMessage();
        msg.setTitle("测试信息");
        msg.setInfo("这是一大段测试信息！！！！！！！");
        mqUtils.sendMessage(QUEUE_NAME, msg);
        while(!receive){
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /***
     * 收到抢购消息，如果抢购成功则发送订单消息
     * @param message
     */
    @JmsListener(destination = QUEUE_NAME)
    public void receiveOrderQueue(Message message) throws Exception{
        logger.info("Consumer收到的报文为:" + message);
        ObjectMessage objMsg = (ObjectMessage) message;
        MQTestMessage msg = (MQTestMessage) objMsg.getObject();
        logger.info(msg);
        receive = true;
    }

}
