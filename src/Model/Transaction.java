package Model;

import dao.GraphDao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Transaction {
    private String spot1;
    private String spot2;
    private List<ArcNode> arcNodeList;
    private VNode vNode;

    private int operation;
    public static final int ADD_ARC = 0;
    public static final int DELETE_ARC = 1;
    public static final int ADD_SPOT = 2;
    public static final int DELETE_SPOT = 3;


    public List<ArcNode> getArcNodeList() {
        return arcNodeList;
    }

    public void setArcNodeList(List<ArcNode> arcNodeList) {
        this.arcNodeList = arcNodeList;
    }


    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public String getSpot1() {
        return spot1;
    }

    public void setSpot1(String spot1) {
        this.spot1 = spot1;
    }

    public String getSpot2() {
        return spot2;
    }

    public void setSpot2(String spot2) {
        this.spot2 = spot2;
    }

    public VNode getvNode() {
        return vNode;
    }

    public void setvNode(VNode vNode) {
        this.vNode = vNode;
    }

    // 构造方法
    public Transaction(String spot1, String spot2, int operation) {
        this.spot1 = spot1;
        this.spot2 = spot2;
        this.operation = operation;
    }

    public Transaction(List<ArcNode> arcNodeList, int operation) {
        this.arcNodeList = arcNodeList;
        this.operation = operation;
    }

    public Transaction(String spot, int operation) {
        this.spot1 = spot;
        this.operation = operation;
    }

    public Transaction(VNode vNode, List<ArcNode> arcNodeList, int operation) {
        this.vNode = vNode;
        this.arcNodeList = arcNodeList;
        this.operation = operation;
    }

    public void revert() {
        GraphDao graphDao = new GraphDao();
        switch (this.operation) {
            case ADD_ARC: { // 删掉新增的路线
                ManageSystem.deleteArc(spot1, spot2);
                graphDao.deleteArc(spot1, spot2);
                break;
            }
            case DELETE_ARC: { // 新增删掉的路线
                int dist = 0;
                for (ArcNode arcNode : arcNodeList) {
                    ManageSystem.addArc(arcNode.getFrom(), arcNode);
                    dist = arcNode.getDistance();
                }
                graphDao.addPath(spot1, spot2, dist);
                break;
            }
            case ADD_SPOT: { // 删掉新增的景点
                ManageSystem.deleteSpot(spot1);
                graphDao.deleteSpot(spot1);
                break;
            }
            case DELETE_SPOT: { // 新增删掉的景点及其相关的边
                ManageSystem.addSpot(vNode.getName(), vNode.getIntro());
                for (ArcNode arcNode : arcNodeList) {
                    ManageSystem.addArc(arcNode.getFrom(), arcNode);
                }
                // 数据库加顶点
                graphDao.addSpot(vNode.getName(), vNode.getIntro());
                // 数据库加边
                Set<String> toSet = new HashSet<String>();
                for (ArcNode arcNode : arcNodeList) {
                    if (!arcNode.getTo().equals(vNode.getName())) {
                        graphDao.addPath(vNode.getName(), arcNode.getTo(), arcNode.getDistance());
                    }
                }
                break;
            }
            default:
                break;
        }
    }
}
