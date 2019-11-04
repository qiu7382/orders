package zz.test.orders.common.bean;
import java.io.Serializable;
import java.util.Date;
/***
* 商品持久化对象，用于表示数据库的一条商品记录
*/
public class QgGoods implements Serializable {
    //主键
    private String id;
    //商品图片
    private String goodsImg;
    //商品名称
    private String goodsName;
    //商品单价
    private Double price;
    //原始库存
    private Integer stock;
    // 创建时间
    private Date createdTime;
    // 更新时间
    private Date updatedTime;
    //get set 方法
    public void setId (String  id){
        this.id=id;
    }
    public  String getId(){
        return this.id;
    }
    public void setGoodsImg (String  goodsImg){
        this.goodsImg=goodsImg;
    }
    public  String getGoodsImg(){
        return this.goodsImg;
    }
    public void setGoodsName (String  goodsName){
        this.goodsName=goodsName;
    }
    public  String getGoodsName(){
        return this.goodsName;
    }
    public void setPrice (Double  price){
        this.price=price;
    }
    public  Double getPrice(){
        return this.price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void setCreatedTime (Date  createdTime){
        this.createdTime=createdTime;
    }
    public  Date getCreatedTime(){
        return this.createdTime;
    }
    public void setUpdatedTime (Date  updatedTime){
        this.updatedTime=updatedTime;
    }
    public  Date getUpdatedTime(){
        return this.updatedTime;
    }
}
