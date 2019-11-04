package zz.test.orders.pay.wx;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.SpringApplication.*;

@SpringBootApplication
@DubboComponentScan("zz.test.orders.pay.wx.service.impl")
public class WxpayConsumerLauncher
{
    public static void main( String[] args )
    {
        run(WxpayConsumerLauncher.class, args);
        synchronized (WxpayConsumerLauncher.class) {
            while (true) {
                try {
                    WxpayConsumerLauncher.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
