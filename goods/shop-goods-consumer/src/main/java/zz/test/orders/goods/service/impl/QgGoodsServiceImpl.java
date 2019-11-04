package zz.test.orders.goods.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import zz.test.orders.common.bean.QgGoodsMessage;
import zz.test.orders.common.bean.QgUser;
import zz.test.orders.common.dto.QgGoodsMessageVo;
import zz.test.orders.common.dto.QgGoodsVo;
import zz.test.orders.common.service.RpcQgGoodsMessageService;
import zz.test.orders.common.service.RpcQgGoodsService;
import zz.test.orders.common.service.RpcQgUserService;
import zz.test.orders.common.utils.*;
import zz.test.orders.goods.service.QgGoodsService;

import javax.jms.Message;
import javax.jms.ObjectMessage;

@Component
public class QgGoodsServiceImpl implements QgGoodsService {

    @Reference
    private RpcQgUserService rpcQgUserService;

    @Reference
    private RpcQgGoodsService rpcQgGoodsService;

    @Reference
    private RpcQgGoodsMessageService rpcQgGoodsMessageService;

    @Autowired
    private MqUtils mqUtils;

    //日志记录
    Logger logger=Logger.getLogger(QgGoodsServiceImpl.class);
    /***
     * 根据id查询商品信息
     * @param goodsId
     * @return
     */
    @Override
    public Dto<QgGoodsVo> queryQgGoodsById(String goodsId) {
        QgGoodsVo qgGoodsVo= null;
        Dto<QgGoodsVo> dto=null;
        try {
            qgGoodsVo = rpcQgGoodsService.getQgGoodsById(goodsId);
            dto= EmptyUtils.isEmpty(qgGoodsVo)? DtoUtil.returnFail("",""):DtoUtil.returnDataSuccess(qgGoodsVo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }
    /***
     * 发送商品抢购消息
     * @param goodsId
     * @param token
     * @return
     */
    public Dto sendQgGoodsMessage(String goodsId, String token) throws Exception{
        QgUser qgUser=rpcQgUserService.getCurrentUser(token);
        if(EmptyUtils.isEmpty(qgUser)){
            return DtoUtil.returnFail("用户未登录","0001");
        }
        QgGoodsMessageVo qgGoodsMessage=new QgGoodsMessageVo();
        qgGoodsMessage.setUserId(qgUser.getId());
        qgGoodsMessage.setGoodsId(goodsId);
        return rpcQgGoodsService.sendQgGoodsMessage(qgGoodsMessage);
    }
    /***
     * 收到抢购消息，如果抢购成功则发送订单消息
     * @param message
     */
    @JmsListener(destination = Constants.QueueName.TO_QG_QUEUE)
    public void receiveOrderQueue(Message message) throws Exception{
        int flag= 0;
        logger.info("Consumer收到的报文为:" + message);
        ObjectMessage objMsg = (ObjectMessage) message;
        QgGoodsMessageVo qgGoodsMessage= (QgGoodsMessageVo) objMsg.getObject();
        flag = rpcQgGoodsService.queryAndMinusStock(qgGoodsMessage);
        //如果抢到商品
        if(flag==1){
            logger.info("");
        }else if(flag==3){
            throw new Exception();
        }
    }
    /***
     * 支付成功后 修改商品库存
     * @param message
     */
    @JmsListener(destination = Constants.QueueName.TO_UPDATED_GOODS_QUQUE)
    public void receiveGoodsQueue(Message message) throws Exception {
        logger.info("Goods收到的报文为:" + message);
        ObjectMessage objMsg = (ObjectMessage) message;
        QgGoodsMessageVo qgGoodsMessage= (QgGoodsMessageVo) objMsg.getObject();
        rpcQgGoodsService.updateStockStatus(qgGoodsMessage.getStockId(),Constants.StockStatus.success);
    }
    /***
     * 处理死信 保存未处理信息
     * @param message
     * @throws Exception
     */
    @JmsListener(destination = "ActiveMQ.DLQ")
    public void receiveDeadQueue(Message message) throws Exception {
        logger.info("Consumer收到的报文为:" + message);
        ObjectMessage objMsg = (ObjectMessage) message;
        QgGoodsMessageVo qgGoodsMessageVo= (QgGoodsMessageVo) objMsg.getObject();
        QgGoodsMessage qgGoodsMessage=new QgGoodsMessage();
        BeanUtils.copyProperties(qgGoodsMessageVo,qgGoodsMessage);
        rpcQgGoodsMessageService.addQgGoodsMessage(qgGoodsMessage);
    }

    public void sendMessage(){
        mqUtils.sendMessage("shang","test");
    }

    @JmsListener(destination = "shang")
    public void reivceMessage() throws Exception {
        mqUtils.sendMessage("shang","test");
        throw new Exception();
    }

}

