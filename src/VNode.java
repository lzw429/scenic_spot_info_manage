public class VNode { // 定义顶点信息
    private int number; // 景点编号
    private String name; // 景点名称
    private String intro; // 景点简介
    private boolean hasRestingArea; // 有无休息区
    private boolean hasLatrine; // 有无公厕

    VNode(int number) {
        this.number = number;
        this.name = Util.spotNumberToName(number);
    }

    VNode(int number, String name) {
        this.number = number;
        this.name = name;
    }

    VNode(String name) {
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
}
