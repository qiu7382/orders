package zz.test.orders.pay.wx;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.springframework.boot.SpringApplication.*;

@SpringBootApplication
@DubboComponentScan("zz.test.orders.pay.wx.service")
@EnableTransactionManagement
@MapperScan("zz.test.orders.common.dao")
public class WxpayProviderLauncher
{
    public static void main( String[] args )
    {
        run(WxpayProviderLauncher.class, args);
        synchronized (WxpayProviderLauncher.class) {
            while (true) {
                try {
                    WxpayProviderLauncher.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
