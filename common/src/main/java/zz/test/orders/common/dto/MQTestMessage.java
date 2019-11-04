package zz.test.orders.common.dto;

import java.io.Serializable;

public class MQTestMessage implements Serializable {
    private String title;
    private String info;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }
    @Override
    public String toString() {
        return "MQTestMessage{" +
                "title='" + title + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
