package zz.test.orders.goods.config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import zz.test.orders.common.bean.QgGoods;
import zz.test.orders.common.dao.QgGoodsMapper;
import zz.test.orders.common.dao.QgGoodsTempStockMapper;
import zz.test.orders.common.dto.QgGoodsVo;
import zz.test.orders.common.utils.RedisUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class BeforeStartup implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private QgGoodsMapper qgGoodsMapper;

    @Autowired
    private QgGoodsTempStockMapper qgGoodsTempStockMapper;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            List<QgGoods> qgGoodsList=qgGoodsMapper.getQgGoodsListByMap(null);
            for (QgGoods qgGoods:qgGoodsList){
                QgGoodsVo qgGoodsVo=new QgGoodsVo();
                BeanUtils.copyProperties(qgGoods,qgGoodsVo);
                String key = "goods_" + qgGoods.getId();
                qgGoods = qgGoodsMapper.getQgGoodsById(qgGoods.getId());
                Map<String,Object> param=new HashMap<String,Object>();
                param.put("goodsId",qgGoods.getId());
                param.put("ok",1);
                Integer count=qgGoodsTempStockMapper.getQgGoodsTempStockCountByMap(param);
                qgGoodsVo.setCurrentStock(qgGoods.getStock()-count);
                redisUtils.set(key, JSONObject.toJSONString(qgGoodsVo));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
