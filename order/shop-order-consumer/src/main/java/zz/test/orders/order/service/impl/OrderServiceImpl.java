package zz.test.orders.order.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import com.alibaba.dubbo.config.annotation.Reference;
import zz.test.orders.common.bean.QgOrder;
import zz.test.orders.common.bean.QgUser;
import zz.test.orders.common.dto.QgGoodsMessageVo;
import zz.test.orders.common.dto.QgGoodsVo;
import zz.test.orders.common.dto.QgOrderVo;
import zz.test.orders.common.service.RpcQgGoodsService;
import zz.test.orders.common.service.RpcQgOrderService;
import zz.test.orders.common.service.RpcQgUserService;
import zz.test.orders.common.utils.*;
import zz.test.orders.order.service.OrderService;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.util.*;


@Component
public class OrderServiceImpl implements OrderService {

    @Reference(timeout=100000)
    private RpcQgOrderService rpcQgOrderService;

    @Reference(timeout=100000)
    private RpcQgUserService rpcQgUserService;

    @Reference(timeout=100000)
    private RpcQgGoodsService rpcQgGoodsService;

    @Resource
    private RedisUtils redisUtils;

    //日志处理
    Logger logger=Logger.getLogger(OrderServiceImpl.class);
    /***
     * 接收生成订单的消息
     * @param message
     */
    @JmsListener(destination = Constants.QueueName.TO_CREATE_ORDER)
    public void receiveOrderQueue(Message message)throws Exception {
        logger.info("Consumer收到的报文为:" + message);
        ObjectMessage objMsg = (ObjectMessage) message;
        try {
            //1.拿到消息
            QgGoodsMessageVo qgGoodsMessage=(QgGoodsMessageVo) objMsg.getObject();
            //2.开始生成订单 TODO判断异常进行回滚
            rpcQgOrderService.createOrder(qgGoodsMessage);//判断成功失败
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /***
     * 接收修改订单状态的消息
     * @param message
     */
    @JmsListener(destination = Constants.QueueName.TO_UPDATED_ORDER_QUEUE)
    public void receiveUpdateOrderQueue(Message message)throws Exception {
        logger.info("Consumer收到的报文为:" + message);
        ObjectMessage objMsg = (ObjectMessage) message;
        try {
            //1.拿到消息
            QgGoodsMessageVo qgGoodsMessageVo=(QgGoodsMessageVo) objMsg.getObject();
            //2.开始修改订单 TODO判断异常进行循环
            rpcQgOrderService.updateOrder(qgGoodsMessageVo.getOrderNo(),qgGoodsMessageVo.getTradeNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /****
     * 查询订单列表
     * @param token
     * @return
     */
    public Dto<List<QgOrderVo>> queryOrderList(String token) throws Exception {
        List<QgOrder> qgOrderList=null;
        List<QgOrderVo> qgOrderVoList=new ArrayList<QgOrderVo>();
        //获取用户
        QgUser qgUser=rpcQgUserService.getCurrentUser(token);
        if(EmptyUtils.isEmpty(qgUser)){
            return DtoUtil.returnFail("用户未登录","0001");
        }
        Map<String,Object> param=new HashMap<String, Object>();
        param.put("userId",qgUser.getId());
        try {
            qgOrderList=rpcQgOrderService.getQgOrderListByMap(param);
            for (QgOrder qgOrder:qgOrderList){
                QgOrderVo qgOrderVo=new QgOrderVo();
                QgGoodsVo qgGoods=rpcQgGoodsService.getQgGoodsById(qgOrder.getGoodsId());
                BeanUtils.copyProperties(qgOrder,qgOrderVo);
                BeanUtils.copyProperties(qgGoods,qgOrderVo);
                qgOrderVoList.add(qgOrderVo);
            }
            return DtoUtil.returnDataSuccess(qgOrderVoList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("0002","系统异常");
        }
    }
    /***
     * 刷新是否抢购
     * @param token
     * @param goodsId
     * @return
     * @throws Exception
     */
    @Override
    public Dto flushIsGet(String token, String goodsId) throws Exception {
        //判断用户
        QgUser qgUser=rpcQgUserService.getCurrentUser(token);
        if(EmptyUtils.isEmpty(qgUser)){
            return DtoUtil.returnFail("用户未登录","0001");
        }
        //得到步骤
        String step=getStep(qgUser.getId(),goodsId);
        return DtoUtil.returnDataSuccess(step);
    }

    public String getStep(String userId,String goodsId) {
        String key=userId+":"+goodsId;
        return  (String) redisUtils.get(key);
    }

    @Override
    public boolean cancelGoodsAndOrder() throws Exception {
        //刷新商品的库存
        boolean flag = false;
        try{
            List<QgOrder> qgOrderList = rpcQgOrderService.getFlushCancelOrderList();
            //刷新订单状态
            flag = rpcQgOrderService.flushOrderStatus();
            for(QgOrder qgOrder : qgOrderList){
                rpcQgGoodsService.flushAddGoods(qgOrder.getStockId());
            }
            //清除redis记录
            for(QgOrder qgOrder : qgOrderList){
                redisUtils.delete(qgOrder.getUserId()+":"+qgOrder.getGoodsId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return flag;
        }
    }
}
