package zz.test.orders.user;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@ComponentScan(basePackages = {"zz.test.orders.common.utils", "zz.test.orders.user.service.impl","zz.test.orders.user.controller"})
@DubboComponentScan("zz.test.orders.user.service.impl")
public class UserConsumerLauncher {
	public static void main(String[] args) {
		run(UserConsumerLauncher.class, args);
	}
}
