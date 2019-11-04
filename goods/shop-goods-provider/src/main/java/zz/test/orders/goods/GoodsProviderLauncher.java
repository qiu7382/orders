package zz.test.orders.goods;
import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@ComponentScan(basePackages = {"zz.test.orders.common.utils","zz.test.orders.goods.service","zz.test.orders.goods.config"})
@MapperScan(basePackages = {"zz.test.orders.common.dao"})
@DubboComponentScan("zz.test.orders.goods.service")
public class GoodsProviderLauncher {
    public static void main(String[] args) {
        run(GoodsProviderLauncher.class, args);
        synchronized (GoodsProviderLauncher.class) {
            while (true) {
                try {
                    GoodsProviderLauncher.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}