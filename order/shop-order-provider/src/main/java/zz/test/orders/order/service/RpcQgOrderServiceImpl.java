package zz.test.orders.order.service;
import com.alibaba.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import zz.test.orders.common.bean.QgOrder;
import zz.test.orders.common.dao.QgOrderMapper;
import zz.test.orders.common.dto.QgGoodsMessageVo;
import zz.test.orders.common.service.RpcQgOrderService;
import zz.test.orders.common.utils.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

@Component
@Service(interfaceClass= RpcQgOrderService.class)
@EnableTransactionManagement
public class RpcQgOrderServiceImpl implements RpcQgOrderService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(RpcQgOrderServiceImpl.class);

    @Resource
    private QgOrderMapper qgOrderMapper;


    @Value("${order.deviceNo}")
    private String orderDeviceNo;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public Dto createOrder(QgGoodsMessageVo qgGoodsMessage) {
        QgOrder qgOrder = new QgOrder();
        try {
            qgOrder.setId(IdWorker.getId());
            qgOrder.setNum(1);
            qgOrder.setAmount(qgGoodsMessage.getAmount());
            qgOrder.setOrderNo(createOrderNo(qgOrder));
            qgOrder.setGoodsId(qgGoodsMessage.getGoodsId());
            qgOrder.setUserId(qgGoodsMessage.getUserId());
            qgOrder.setStockId(qgGoodsMessage.getStockId());
            qgOrder.setStatus(Constants.OrderStatus.TOPAY);
            qgOrder.setCreatedTime(new Date());
            setQgStatus(qgGoodsMessage);//修改抢购状态为获得订单待支付状态
            qgOrderMapper.insertQgOrder(qgOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("系统异常", "0002");
        }
        return DtoUtil.returnSuccess();
    }

    private String createOrderNo(QgOrder qgOrder) {
        StringBuilder md5String = new StringBuilder();
        md5String.append(qgOrder.getGoodsId());
        md5String.append(qgOrder.getUserId());
        md5String.append(System.currentTimeMillis());
        md5String.append(Math.random() * 1000000);
        String md5 = MD5.getMd5(md5String.toString(), 6);
        //生成订单编号
        StringBuilder orderNo = new StringBuilder();
        orderNo.append(orderDeviceNo);
        try {
            orderNo.append(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        orderNo.append(md5);
        return orderNo.toString();
    }

    /***
     * 设置抢购状态
     * @param QgGoodsMessage
     */
    public void setQgStatus(QgGoodsMessageVo QgGoodsMessage) {
        String key = QgGoodsMessage.getUserId() + ":" + QgGoodsMessage.getGoodsId();
        redisUtils.set(key, Constants.QgStatus.getOrder);
    }

    public QgOrder getQgOrderById(String id) throws Exception {
        return qgOrderMapper.getQgOrderById(id);
    }

    public List<QgOrder> getQgOrderListByMap(Map<String, Object> param) throws Exception {
        return qgOrderMapper.getQgOrderListByMap(param);
    }

    public Integer getQgOrderCountByMap(Map<String, Object> param) throws Exception {
        return qgOrderMapper.getQgOrderCountByMap(param);
    }

    @Override
    public QgOrder loadQgOrderByOrderNo(String orderNo) throws Exception {
        logger.debug("加载订单：" + orderNo);
        Map<String, Object> param = new HashMap();
        param.put("orderNo", orderNo);
        List<QgOrder> orders = qgOrderMapper.getQgOrderListByMap(param);
        if (orders.size() == 1) {
            return orders.get(0);
        } else
            return null;
    }

    @Override
    public boolean processed(String orderNo) throws Exception {
        QgOrder qgOrder = this.loadQgOrderByOrderNo(orderNo);
        return qgOrder.getStatus().equals(2) && !EmptyUtils.isEmpty(qgOrder.getAliTradeNo());
    }

    @Override
    public void updateOrder(String orderNo, String tradeNo) throws Exception {
        logger.debug("订单支付成功：" + orderNo);
        QgOrder qgOrder = this.loadQgOrderByOrderNo(orderNo);
        qgOrder.setStatus(Constants.OrderStatus.SUCCESS);//支付成功
        qgOrder.setAliTradeNo(tradeNo);//交易号（如支付宝交易号）
        qgOrderMapper.updateQgOrder(qgOrder);
    }

    public boolean flushOrderStatus() throws Exception {
        Integer flag;
        flag = qgOrderMapper.flushCancelOrderStatus();
        return flag > 0 ? true : false;
    }

    public List<QgOrder> getFlushCancelOrderList() throws Exception {
        return qgOrderMapper.getFlushCancelOrderList();
    }
}