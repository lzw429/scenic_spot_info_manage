import java.io.Serializable;

public class VNode implements Serializable { // 定义顶点信息
    private int number;                 // 景点编号
    private String name;                // 景点名称
    private String intro;               // 景点简介
    private boolean hasRestingArea;   // 有无休息区
    private boolean hasLatrine;        // 有无公厕
    // 用于Dijkstra算法的成员变量
    private int totalDist = Integer.MAX_VALUE;
    private boolean visited = false;
    private String fromSpot = null;

    VNode(int number) {
        this.number = number;
        this.name = Util.spotNumberToName(number);
    }

    VNode(String name) {
        this.name = name;
    }

    VNode(int number, String name) {
        this.number = number;
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public boolean isHasRestingArea() {
        return hasRestingArea;
    }

    public void setHasRestingArea(boolean hasRestingArea) {
        this.hasRestingArea = hasRestingArea;
    }

    public boolean isHasLatrine() {
        return hasLatrine;
    }

    public void setHasLatrine(boolean hasLatrine) {
        this.hasLatrine = hasLatrine;
    }

    public int getTotalDist() {
        return totalDist;
    }

    public void setTotalDist(int totalDist) {
        this.totalDist = totalDist;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public String getFromSpot() {
        return fromSpot;
    }

    public void setFromSpot(String fromSpot) {
        this.fromSpot = fromSpot;
    }
}
