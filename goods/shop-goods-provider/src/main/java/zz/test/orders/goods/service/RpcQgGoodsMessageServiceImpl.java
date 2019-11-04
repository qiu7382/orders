package zz.test.orders.goods.service;


import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import zz.test.orders.common.bean.QgGoodsMessage;
import zz.test.orders.common.dao.QgGoodsMessageMapper;
import zz.test.orders.common.service.RpcQgGoodsMessageService;
import zz.test.orders.common.utils.IdWorker;

import javax.annotation.Resource;

@Component
@Service(interfaceClass = RpcQgGoodsMessageService.class)
public class RpcQgGoodsMessageServiceImpl implements RpcQgGoodsMessageService{

    @Resource
    private QgGoodsMessageMapper qgGoodsMessageMapper;

    @Override
    public int addQgGoodsMessage(QgGoodsMessage qgGoodsMessage) throws Exception{
        qgGoodsMessage.setId(IdWorker.getId());
        return qgGoodsMessageMapper.insertQgGoodsMessage(qgGoodsMessage);
    }
}
