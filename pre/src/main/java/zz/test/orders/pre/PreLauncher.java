package zz.test.orders.pre;
import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.SpringApplication.*;

@SpringBootApplication
@DubboComponentScan("zz.test.orders.common.utils")
public class PreLauncher {
    public static void main(String[] args) {
        run(PreLauncher.class, args);
    }
}