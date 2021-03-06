package Model;

import java.io.Serializable;

public class ArcNode implements Serializable { // 定义边信息
    private String to; // 指向的顶点
    private String from; // 用于Prim算法
    private int distance = 32767; // 景点间距离
    private int time; // 需要的时间

    public ArcNode(String to, int distance) {
        this.to = to;
        this.distance = distance;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
