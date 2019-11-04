package zz.test.orders.goods.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import zz.test.orders.common.bean.QgGoods;
import zz.test.orders.common.utils.Dto;
import zz.test.orders.common.utils.DtoUtil;
import zz.test.orders.goods.service.QgGoodsService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api")
@CrossOrigin("*")
public class GoodsController {

    @Resource
    private QgGoodsService qgGoodsService;
    /***
     * 根据id 查询商品信息
     * @param id
     * @param response
     * @return
     */
    @RequestMapping("queryGoodsById")
    @ResponseBody
    public Dto<QgGoods> queryGoodsById(String id, HttpServletResponse response){
        Dto dto=null;
        try{
            response.setHeader("Access-Control-Allow-Origin", "*");
            dto=qgGoodsService.queryQgGoodsById(id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return dto;
    }
    /****
     * 发送抢购商品消息
     * @param goodsId
     * @param token
     * @return
     */
    @RequestMapping("sendQgGoodsMessage")
    @ResponseBody
    public Dto sendQgGoodsMessage(String goodsId,String token, HttpServletResponse response){
        Dto dto=null;
        try {
            response.setHeader("Access-Control-Allow-Origin", "*");
            dto=qgGoodsService.sendQgGoodsMessage(goodsId,token);
        }catch (Exception e){
            e.printStackTrace();
            return DtoUtil.returnFail("抢购失败","0002");
        }
        return dto;
    }

    @RequestMapping("testSend")
    @ResponseBody
    public int testSend(){
        qgGoodsService.sendMessage();
        return 1;
    }
}
