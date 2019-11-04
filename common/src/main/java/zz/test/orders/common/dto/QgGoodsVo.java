package zz.test.orders.common.dto;

import java.io.Serializable;

/**
 * 商品值对象，用于在业务层传递数据
 */
public class QgGoodsVo implements Serializable{
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
    //实际库存
    private Integer currentStock;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }
}
