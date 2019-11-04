package zz.test.orders.goods.service;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import zz.test.orders.common.bean.QgGoodsTempStock;
import zz.test.orders.common.dao.QgGoodsTempStockMapper;
import zz.test.orders.common.dto.QgGoodsMessageVo;
import zz.test.orders.common.dto.QgGoodsVo;
import zz.test.orders.common.service.RpcQgGoodsService;
import zz.test.orders.common.utils.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;
@Component
@Service(interfaceClass = RpcQgGoodsService.class)
public class RpcQgGoodsServiceImpl implements RpcQgGoodsService {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private MqUtils mqUtils;

    @Resource
    private QgGoodsTempStockMapper qgGoodsTempStockMapper;

    @Override
    public int getGoodsStockByGoodsId(String goodsId) throws Exception {
        QgGoodsVo qgGoodsVo=getQgGoodsById(goodsId);
        return qgGoodsVo.getCurrentStock();
    }
    /****
     * 0：库存不足 1：抢购成功 2：已抢购 3：异常
     * @return
     */
    @Override
    public int queryAndMinusStock(QgGoodsMessageVo qgGoodsMessage) {
        String goodsId=qgGoodsMessage.getGoodsId();
        String userId=qgGoodsMessage.getUserId();
        int flag=0;
        try{
            //检测锁
            while (!redisUtils.lock(qgGoodsMessage.getGoodsId())){
                TimeUnit.SECONDS.sleep(3); //没抢到锁，继续等待
            }
            //检测用户是否已抢购
            if(checkUserIsQg(goodsId,userId)){
                flag=2;
            }else{
                QgGoodsVo qgGoods=getQgGoodsById(goodsId);
                if(qgGoods.getCurrentStock()>0){
                    //刷新商品库存 锁的关键
                    String stockId=minGoods(qgGoods,userId);
                    qgGoodsMessage.setStockId(stockId);
                    qgGoodsMessage.setAmount(qgGoods.getPrice());
                    //更新用户抢购状态
                    updateUserQgStatus(goodsId,userId, Constants.QgStatus.getGoods);
                    // 发送创建订单消息
                    sendOrderMessage(qgGoodsMessage);
                    flag=1;
                }else{
                    updateUserQgStatus(goodsId,userId, Constants.QgStatus.fail);
                }
            }
        }catch (Exception e){
            flag=3;
        }finally {
            redisUtils.unlock(goodsId);
            return flag;
        }
    }
    /****
     * 发送商品抢购消息
     * @param qgGoodsMessage
     * @return
     * @throws Exception
     */
    public Dto sendQgGoodsMessage(QgGoodsMessageVo qgGoodsMessage) throws Exception {
        QgGoodsVo qgGoodsVo = null;
        String key = null;
        try {
            qgGoodsVo = getQgGoodsById(qgGoodsMessage.getGoodsId());
            if (EmptyUtils.isEmpty(qgGoodsVo)) {
                return DtoUtil.returnFail("商品不存在", "3002");
            }
            //加入排队队列前检查是否已经抢购
            if(checkUserIsQg(qgGoodsMessage.getGoodsId(),qgGoodsMessage.getUserId())){
                return DtoUtil.returnFail("您已抢购该商品", "3003");
            }
            //判断商品库存是否充足
            if (getGoodsStockByGoodsId(qgGoodsVo.getId())==0) {
                redisUtils.set(getUserGoodsKey(qgGoodsMessage.getGoodsId(),qgGoodsMessage.getUserId()),Constants.QgStatus.fail);
                return DtoUtil.returnFail("您手慢了，抢购未成功", "3003");
            }
            //如果未抢购则加入消息队列，等待处理
            //判断成功发送抢购消息 进行排队
            qgGoodsMessage.setAmount(qgGoodsVo.getPrice());
            mqUtils.sendMessage(Constants.QueueName.TO_QG_QUEUE, qgGoodsMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("排队失败", "0002");
        }
        return DtoUtil.returnSuccess();
    }
    /***
     * 更新库存状态
     * @param stockId
     * @return
     * @throws Exception
     */
    public int updateStockStatus(String stockId,Integer status)throws Exception{
        QgGoodsTempStock qgGoodsTempStock=qgGoodsTempStockMapper.getQgGoodsTempStockById(stockId);
        qgGoodsTempStock.setStatus(status);
        qgGoodsTempStock.setUpdatedTime(new Date());
        return qgGoodsTempStockMapper.updateQgGoodsTempStock(qgGoodsTempStock);
    }
    /***
     * 发送生成订单消息
     * @param qgGoodsMessage
     * @return
     * @throws Exception
     */
    public Dto sendOrderMessage(QgGoodsMessageVo qgGoodsMessage) throws Exception {
        mqUtils.sendMessage(Constants.QueueName.TO_CREATE_ORDER, qgGoodsMessage);
        return DtoUtil.returnSuccess();
    }

    public QgGoodsVo getQgGoodsById(String id) throws Exception {
        String key = "goods_" + id;
        String goodsJson = (String) redisUtils.get(key);
        return JSONObject.parseObject(goodsJson, QgGoodsVo.class);
    }

    public boolean checkUserIsQg(String goodsId,String userId)throws Exception{
        String key = getUserGoodsKey(goodsId,userId);
        String status = (String) redisUtils.get(key);
        if (EmptyUtils.isNotEmpty(status) && Integer.parseInt(status) > 0) {
            return true;
        }
        return false;
    }

    public String getUserGoodsKey(String goodsId,String userId) {
        return userId + ":" + goodsId;
    }

    /**
     * 减缓存中的库存数量，增加商品临时库
     * @param qgGoods
     * @param userId
     * @return
     * @throws Exception
     */
    public String minGoods(QgGoodsVo qgGoods,String userId) throws Exception {
        QgGoodsTempStock qgGoodsTempStock=new QgGoodsTempStock();
        qgGoodsTempStock.setId(IdWorker.getId());
        qgGoodsTempStock.setStatus(0);
        qgGoodsTempStock.setGoodsId(qgGoods.getId());
        qgGoodsTempStock.setUserId(userId);
        qgGoodsTempStock.setCreatedTime(new Date());
        qgGoodsTempStockMapper.insertQgGoodsTempStock(qgGoodsTempStock);
        String key = "goods_" + qgGoods.getId();
        qgGoods.setCurrentStock(qgGoods.getCurrentStock()-1);
        redisUtils.set(key, JSONObject.toJSONString(qgGoods));
        return qgGoodsTempStock.getId();
    }

    public void flushAddGoods(String stockId) throws Exception {
        QgGoodsTempStock qgGoodsTempStock=null;
        try{
            //库存锁
            while (!redisUtils.lock(stockId)){
                TimeUnit.SECONDS.sleep(3); //没抢到锁，继续等待
            }
            qgGoodsTempStock=qgGoodsTempStockMapper.getQgGoodsTempStockById(stockId);
            if(qgGoodsTempStock.getStatus().equals(Constants.StockStatus.cancel)){
                return;
            }else{
                //刷新库存
                updateStockStatus(stockId,Constants.StockStatus.cancel);
            }
            //商品锁
            while (!redisUtils.lock(qgGoodsTempStock.getGoodsId())){
                TimeUnit.SECONDS.sleep(3); //没抢到锁，继续等待
            }
            QgGoodsVo qgGoodsVo=getQgGoodsById(qgGoodsTempStock.getGoodsId());
            String key = "goods_" + qgGoodsTempStock.getGoodsId();
            qgGoodsVo.setCurrentStock(qgGoodsVo.getCurrentStock()+1);
            redisUtils.set(key, JSONObject.toJSONString(qgGoodsVo));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(!EmptyUtils.isEmpty(qgGoodsTempStock)){
                redisUtils.unlock(qgGoodsTempStock.getGoodsId());
            }
            redisUtils.unlock(stockId);
        }
    }

    /***
     * 更新用户抢购状态
     * @param goodsId
     * @param userId
     * @param status
     * @throws Exception
     */
    public void updateUserQgStatus(String goodsId,String userId,String status)throws Exception{
        String key = getUserGoodsKey(goodsId,userId);
        redisUtils.set(key, status);
    }
}
