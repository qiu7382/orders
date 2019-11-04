package zz.test.orders.pay.wx.service;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import zz.test.orders.common.bean.QgTrade;
import zz.test.orders.common.dao.QgTradeMapper;
import zz.test.orders.common.dto.QgGoodsMessageVo;
import zz.test.orders.common.service.RpcQgWxpayService;
import zz.test.orders.common.utils.Constants;
import zz.test.orders.common.utils.IdWorker;
import zz.test.orders.common.utils.MqUtils;

import javax.annotation.Resource;
import java.util.Calendar;

/**
 * Created by Administrator on 2018/1/22.
 */
@Component
@Service(interfaceClass= RpcQgWxpayService.class)
public class RpcQgWxpayServiceImpl implements RpcQgWxpayService{

    @Resource
    private QgTradeMapper qgTradeMapper;
    @Resource
    private MqUtils mqUtils;


    @Override
    @Transactional
    public Integer insertTrade(QgGoodsMessageVo qgGoodsMessageVo) throws Exception {
        QgTrade qgTrade = new QgTrade();
        qgTrade.setId(IdWorker.getId());
        qgTrade.setAmount(qgGoodsMessageVo.getAmount());
        qgTrade.setCreatedTime(Calendar.getInstance().getTime());
        qgTrade.setOrderNo(qgGoodsMessageVo.getOrderNo());
        qgTrade.setPayMethod(1);
        qgTrade.setTradeNo(qgGoodsMessageVo.getTradeNo());
        //发消息去修改商品库存
        qgGoodsMessageVo.setOrderNo(qgGoodsMessageVo.getOrderNo());
        qgGoodsMessageVo.setTradeNo(qgGoodsMessageVo.getTradeNo());
        mqUtils.sendMessage(Constants.QueueName.TO_UPDATED_GOODS_QUQUE, qgGoodsMessageVo);
        return qgTradeMapper.insertQgTrade(qgTrade);
    }

}
