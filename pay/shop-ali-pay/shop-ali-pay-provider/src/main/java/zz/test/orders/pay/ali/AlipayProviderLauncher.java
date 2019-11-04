package zz.test.orders.pay.ali;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.SpringApplication.*;

@SpringBootApplication
@DubboComponentScan("zz.test.orders.pay.ali.service")
@MapperScan("zz.test.orders.common.dao")
public class AlipayProviderLauncher {
    public static void main(String[] args) {
        run(AlipayProviderLauncher.class, args);
        synchronized (AlipayProviderLauncher.class) {
            while (true) {
                try {
                    AlipayProviderLauncher.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
