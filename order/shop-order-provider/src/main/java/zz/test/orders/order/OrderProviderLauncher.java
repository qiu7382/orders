package zz.test.orders.order;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import static org.springframework.boot.SpringApplication.*;

/**QgMqMessage
 * Created by Administrator on 2018/1/3.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"zz.test.orders.common.utils","zz.test.orders.order.service"})
@MapperScan("zz.test.orders.common.dao")
@DubboComponentScan("zz.test.orders.order.service")
public class OrderProviderLauncher {
    public static void main(String[] args) {
        run(OrderProviderLauncher.class, args);
    }
}
