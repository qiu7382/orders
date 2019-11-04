package zz.test.orders.user;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@ComponentScan(basePackages = {"zz.test.orders.common.utils","zz.test.orders.user.service"})
@MapperScan("zz.test.orders.common.dao")
@DubboComponentScan("zz.test.orders.user.service")
public class UserProviderLauncher {
    public static void main(String[] args) {
        run(UserProviderLauncher.class, args);
    }
}

