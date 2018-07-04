import java.io.*;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

public class ManageSystem {
    private HashMap<String, List<ArcNode>> arcs;
    private HashMap<String, VNode> spots;

    private ManageSystem() {
        arcs = new HashMap<>();
        spots = new HashMap<>();
    }

    public static void main(String[] args) {
        ManageSystem manageSystem = new ManageSystem();
        manageSystem.CreateGraph();
        Scanner sc;
        while (true) {
            printMenu();
            sc = new Scanner(System.in);
            int choice = sc.nextInt();
            if (choice == 0) {
                System.out.println("[信息]结束服务");
                sc.close(); // 会关闭 System.in
                return;
            }
            switch (choice) {
                case 1: // 创建景区景点分布图
                    manageSystem.CreateGraph();
                    break;
                case 2: // 输出景区景点分布图
                    manageSystem.OutputGraph();
                    break;
                case 3:
                    manageSystem.AddSpot();
                    break;
                case 4:
                    manageSystem.DeleteSpot();
                    break;
                case 5:
                    manageSystem.AddArc();
                    break;
                case 6:
                    manageSystem.DeleteArc();
                    break;
                default:
                    System.out.println("[错误]请重新选择");
                    break;
            }
        }
    }

    private static void printMenu() {
        System.out.println("=============================");
        System.out.println("     欢迎使用景区信息管理系统");
        System.out.println("=============================");
        System.out.println("1.创建景区景点分布图");
        System.out.println("2.输出景区景点分布图");
        System.out.println("3.新增景点");
        System.out.println("4.删除景点");
        System.out.println("5.新增路线");
        System.out.println("6.删除路线");
        System.out.println("=============================");
        System.out.println("请选择：");
    }

    private void CreateGraph() {
        String line;
        BufferedReader reader;
        try {
            String filePath = "C:\\Users\\舒意恒\\Documents\\GitHub\\scenic_spot_info_manage\\data\\graph.txt";
            File graphFile = new File(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(graphFile));
            reader = new BufferedReader(new FileReader(filePath));
            while ((line = reader.readLine()) != null) {
                String[] edge = line.split("——");
                // edge[0]、edge[1]是顶点，edge[2]是距离权值
                int number1 = Integer.parseInt(edge[0]);
                int number2 = Integer.parseInt(edge[1]);
                int distance = Integer.parseInt(edge[2]);
                String name1 = Util.spotNumberToName(number1);
                String name2 = Util.spotNumberToName(number2);
                ArcNode arc1 = new ArcNode(name2, distance);
                ArcNode arc2 = new ArcNode(name1, distance);
                addArc(name1, arc1);
                addArc(name2, arc2);
                addSpot(number1, name1);
                addSpot(number1, name2);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void OutputGraph() {
        String[] spotNames = new String[spots.size()];
        int i = 0;
        for (Map.Entry<String, VNode> entry : spots.entrySet()) {
            spotNames[i++] = entry.getKey();
        }
        System.out.print("\t\t"); // 第一行第一列空出
        for (i = 0; i < spotNames.length; i++) {
            printName(spotNames[i]); // 表头
        }
        System.out.println();
        for (i = 0; i < spotNames.length; i++) {
            printName(spotNames[i]);
            for (int j = 0; j < spotNames.length; j++) {
                int distance = getDistance(spotNames[i], spotNames[j]);
                String distance_out = String.format("%5d", distance);
                System.out.print(distance_out + "\t");
            }
            System.out.println();
        }
    }

    private void addArc(String VNodeName, ArcNode arc) {
        if (arcs.containsKey(VNodeName)) {
            arcs.get(VNodeName).add(arc);
        } else {
            ArrayList<ArcNode> list = new ArrayList<>();
            list.add(arc);
            arcs.put(VNodeName, list);
        }
    }

    private void deleteArc(String VNodeName1, String VNodeName2) {
        if (arcs.get(VNodeName1).isEmpty()) return;
        arcs.get(VNodeName1).removeIf(arcNode -> arcNode.to.equals(VNodeName2));
        if (arcs.get(VNodeName2).isEmpty()) return;
        arcs.get(VNodeName2).removeIf(arcNode -> arcNode.to.equals(VNodeName1));
    }

    private void addSpot(int number, String VNodeName) {
        if (!spots.containsKey(VNodeName))
            spots.put(VNodeName, new VNode(number, VNodeName));
    }

    private void deleteSpot(String VNodeName) {
        if (spots.containsKey(VNodeName))
            spots.remove(VNodeName);
        else System.out.println("[信息]该景点不存在");
    }

    private int getDistance(String name1, String name2) {
        int res = 32767; // 没有距离数据时，输出32767
        if (name1.equals(name2))
            res = 0;
        else {
            if (arcs.get(name1) != null) {
                for (ArcNode arc : arcs.get(name1)) {
                    if (arc.getTo().equals(name2))
                        res = arc.getDistance();
                }
            }
        }
        return res;
    }

    private void printName(String name) {
        if (name.length() < 3)
            System.out.print(name + "\t\t");
        else
            System.out.print(name + "\t");
    }

    private void AddSpot() { // 添加景点
        System.out.println("请输入新增景点名称：");
        Scanner sc = new Scanner(System.in);
        String name = sc.next();
        this.addSpot(spots.size() + 1, name);
    }

    private void DeleteSpot() { // 删除景点
        System.out.println("请输入删除景点名称：");
        Scanner sc = new Scanner(System.in);
        String name = sc.next();
        this.deleteSpot(name);
    }

    private void AddArc() { // 添加路线
        System.out.println("请输入路线信息：景点1名称 景点2名称 距离");
        System.out.println("例如：北门 狮子山 9");
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        String[] arcStr = line.split(" ");
        this.addArc(arcStr[0], new ArcNode(arcStr[1], Integer.parseInt(arcStr[2])));
        this.addArc(arcStr[1], new ArcNode(arcStr[0], Integer.parseInt(arcStr[2])));
    }

    private void DeleteArc() { // 添加路线
        System.out.println("请输入路线信息：景点1名称 景点2名称");
        System.out.println("例如：北门 狮子山");
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        String[] arcStr = line.split(" ");
        this.deleteArc(arcStr[0], arcStr[1]);
    }

    /**
     * 使用堆优化的Dijkstra算法，求两景点间最短路径和最短距离
     *
     * @param v1   起始景点名称
     * @param v2   终止景点名称
     * @param path ArcNode逆序入栈，顺序出栈即得到最短路径
     * @return 最短距离
     */
    private int MiniDistance_Dijkstra(String v1, String v2, Stack<ArcNode> path) {
        if (spots.get(v1) == null || spots.get(v2) == null) {
            throw new IllegalArgumentException("[错误]未找到景点");
        }

        Comparator<VNode> distanceComparator = (o1, o2) -> Integer.compare(o1.getTotalDist() - o2.getTotalDist(), 0); // 距离比较
        PriorityBlockingQueue<VNode> candidate = new PriorityBlockingQueue<>(12, distanceComparator);

        VNode curSpot = spots.get(v1);
        curSpot.setTotalDist(0); // 初始值为INT_MAX_VALUE
        candidate.add(curSpot);
        do {
            curSpot = candidate.peek();
            assert curSpot != null;
            if (!curSpot.isVisited()) {
                for (ArcNode arcNode : arcs.get(curSpot.getName())) {
                    VNode destSpot = spots.get(arcNode.to);
                    int curDist = curSpot.getTotalDist() + arcNode.getDistance();
                    if (curDist < destSpot.getTotalDist()) {
                        // 更新距离
                        destSpot.setTotalDist(curDist);
                        destSpot.setFromSpot(curSpot.getName());
                        if (!candidate.contains(destSpot))
                            candidate.add(destSpot);
                        else { // 重新建堆
                            candidate.remove(destSpot);
                            candidate.add(destSpot);
                        }
                    }
                }
            }
            // 遍历完邻接结点
            curSpot.setVisited(true);
            candidate.poll();
        } while (candidate.size() > 0);

        if (spots.get(v2).isVisited()) { // 如果找到最短路径
            path.push();
            return spots.get(v2).getTotalDist();
        } else // 如果未找到路径
            return Integer.MAX_VALUE;
    }
}
