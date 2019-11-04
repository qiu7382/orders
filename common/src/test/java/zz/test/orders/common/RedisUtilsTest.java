package zz.test.orders.common;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import zz.test.orders.common.utils.RedisUtils;
import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

// 声明运行环境为spring
@RunWith(SpringRunner.class)
// 扫描spring环境并构建完整的SpringContext
@SpringBootTest
public class RedisUtilsTest {

    @Resource
    RedisUtils redisUtils;
    // 产品数量
    int productCount = 10;
    // 线程数
    int threadCount = 100;

    @Test
    public void setVal(){
        redisUtils.set("ok","123456");
        Object val = redisUtils.get("ok");
        Assert.assertThat("123456", Is.is(val.toString()));
    }

    /**
     * 内部线程
     */
    class NxThread extends Thread {
        boolean run = true;
        int index  = 0;
        public NxThread(int index){
            this.index = index;
        }
        public void run() {
            try {
                // 尝试获得锁
                while (!redisUtils.lock("1")) {
                    TimeUnit.MILLISECONDS.sleep(1);
                }
                if(productCount - 1 < 0){
                    // 设置信息
                    redisUtils.set("orders_" + index
                            + "_0_"
                            + System.currentTimeMillis(), "1");
                }else{
                    productCount--;
                    // 设置信息
                    redisUtils.set("orders_" + index
                            + "_" + productCount + "_"
                            + System.currentTimeMillis(), "1");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisUtils.unlock("1");
                run = false;
            }
        }
    }
    @Test
    public void nxTest() throws InterruptedException {
        // 线程数组
        NxThread []nts = new NxThread[threadCount];
        for(int i = 1;i <= threadCount; i++){
            nts[i-1] = new NxThread(i);
            nts[i-1].start();
        }
        // 等待线程结束
        int count = 0;
        while(true){
            for(int i = 0;i < threadCount;i++){
                count += nts[i].run ? 0 : 1;
            }
            if(count >= threadCount) break;
            else TimeUnit.SECONDS.sleep(1);
            count = 0;
        }
    }

}
