import java.io.*;
import java.util.*;

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

    private void addSpot(int number, String VNodeName) {
        if (!spots.containsKey(VNodeName))
            spots.put(VNodeName, new VNode(number, VNodeName));
    }

    private int getDistance(String name1, String name2) {
        int res = 32767;
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

    private void DeleteSpot() {
        System.out.println("");
    }
}
