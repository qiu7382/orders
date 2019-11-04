package zz.test.orders.pay.ali;
import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.springframework.boot.SpringApplication.*;

/**
 * Created by Administrator on 2018/1/2.
 */
@SpringBootApplication
@DubboComponentScan("cn.bdqn.service.impl")
@EnableTransactionManagement
@MapperScan("cn.bdqn.mapper")
public class AlipayConsumerLauncher {
    public static void main(String args[]){
        run(AlipayConsumerLauncher.class, args);
    }
}
