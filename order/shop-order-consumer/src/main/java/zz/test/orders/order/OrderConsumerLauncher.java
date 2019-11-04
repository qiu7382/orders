package zz.test.orders.order;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import static org.springframework.boot.SpringApplication.*;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {"zz.test.orders.common.utils","zz.test.orders.order.service.impl","zz.test.orders.order.controller"})
@DubboComponentScan(basePackages = {"zz.test.orders.order.service.impl"})
public class OrderConsumerLauncher {

    public static void main(String[] args) {
        run(OrderConsumerLauncher.class, args);
        synchronized (OrderConsumerLauncher.class) {
            while (true) {
                try {
                    OrderConsumerLauncher.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
