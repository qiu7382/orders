package zz.test.orders.order.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import zz.test.orders.common.dto.QgOrderVo;
import zz.test.orders.common.utils.Dto;
import zz.test.orders.common.utils.DtoUtil;
import zz.test.orders.order.service.OrderService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("api")
@CrossOrigin("*")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Resource
    private OrderService orderService;

    @RequestMapping("sendTest")
    public Dto sendTest(){
        return DtoUtil.returnSuccess();
    }

    @RequestMapping("flushIsGet")
    @ResponseBody
    public Dto<Integer> flushIsGet(String token, String goodsId, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        Dto<Integer> dto=null;
        try {
            dto=orderService.flushIsGet(token,goodsId);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("系统异常","0002");
        }
        return dto;
    }

    @RequestMapping("queryOrderList")
    @ResponseBody
    public Dto<List<QgOrderVo>> queryOrderList(String token, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        Dto<List<QgOrderVo>> listDto=null;
        try {
            listDto=orderService.queryOrderList(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listDto;
    }
    /***
     * 30分钟执行一次 刷新订单的状态 不对外公布
     */
    //@Scheduled(cron = "0/10 * * * * ?")
    public void flushCancelOrderStatus() {
        try {
            //刷新商品的库存和订单状态
            logger.info("刷单程序开始执行.......");
            orderService.cancelGoodsAndOrder();
            logger.info("刷单程序执行完毕.......");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
