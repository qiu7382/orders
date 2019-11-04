package zz.test.orders.goods;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@ComponentScan(basePackages = {"zz.test.orders.common.utils","zz.test.orders.goods.service.impl","zz.test.orders.goods.controller"})
@DubboComponentScan("zz.test.orders.goods.service.impl")
public class GoodsConsumerLauncher {

	public static void main(String[] args) {
		run(GoodsConsumerLauncher.class, args);
	}
}
