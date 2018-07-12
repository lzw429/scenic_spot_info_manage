package Model;

import dao.GraphDao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

public class ManageSystem {
    private static HashMap<String, List<ArcNode>> arcs = new HashMap<>();
    private static HashMap<String, VNode> spots = new HashMap<>();
    private static HashMap<Integer, Car> cars = new HashMap<>();
    private static Stack<Car> parkingLot = new Stack<>();
    private static Stack<Car> tempParking = new Stack<>();
    private static Queue<Car> pavement = new LinkedList<>();
    private static final int parkingNum = 5; // 停车场大小
    private static Comparator<VNode> spotDistanceComparator = (o1, o2) -> Integer.compare(o1.getTotalDist() - o2.getTotalDist(), 0); // 距离比较
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static int shortestTourLen = Integer.MAX_VALUE;

    public static HashMap<String, List<ArcNode>> getArcs() {
        return arcs;
    }

    public static HashMap<String, VNode> getSpots() {
        return spots;
    }

    public static HashMap<Integer, Car> getCars() {
        return cars;
    }

    public static Stack<Car> getParkingLot() {
        return parkingLot;
    }

    public static Stack<Car> getTempParking() {
        return tempParking;
    }

    public static Queue<Car> getPavement() {
        return pavement;
    }

    private ManageSystem() { // 构造方法
    }

    public static void main(String[] args) throws ParseException {
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
                case 7:
                    manageSystem.ShortestPath();
                    break;
                case 8:
                    manageSystem.CreateTourSortGraph();
                    break;
                case 9:
                    manageSystem.ParkingLotManage();
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
        System.out.println("7.两景点最短路径与最短距离"); // Dijkstra 算法和 FloydWarshall 算法
        System.out.println("8.导游路线图");
        System.out.println("9.停车场管理");
        System.out.println("=============================");
        System.out.println("请选择：");
    }

    public static void dbCreateGraph() {
        GraphDao graphDao = new GraphDao();
        graphDao.createGraph();
    }

    public static void CreateGraph() {
        String line;
        try {
            String filePath = "C:\\Users\\舒意恒\\Documents\\GitHub\\scenic_spot_info_manage\\data\\graph.txt";
            FileInputStream fileInputStream = new FileInputStream(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            while ((line = reader.readLine()) != null) {
                String[] edge = line.split("——");
                // edge[0]、edge[1]是顶点，edge[2]是距离权值
                String name1 = edge[0];
                String name2 = edge[1];
                int distance = Integer.parseInt(edge[2]);
                addArc(name1, new ArcNode(name2, distance));
                addArc(name2, new ArcNode(name1, distance));
                addSpot(name1);
                addSpot(name2);
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
            for (String spotName : spotNames) {
                int distance = getDistance(spotNames[i], spotName);
                String distance_out = String.format("%5d", distance);
                System.out.print(distance_out + "\t");
            }
            System.out.println();
        }
    }

    public static void addArc(String VNodeName, ArcNode arc) {
        if (arcs.containsKey(VNodeName)) {
            arcs.get(VNodeName).add(arc);
        } else {
            ArrayList<ArcNode> list = new ArrayList<>();
            list.add(arc);
            arcs.put(VNodeName, list);
        }
    }

    /**
     * @param VNodeName1 景点1
     * @param VNodeName2 景点2
     * @return 景点1到景点2的弧
     */
    public static ArcNode getArc(String VNodeName1, String VNodeName2) {
        if (!arcs.containsKey(VNodeName1) || arcs.get(VNodeName1).isEmpty())
            return null;
        for (ArcNode arcNode : arcs.get(VNodeName1)) {
            if (arcNode.getTo().equals(VNodeName2))
                return arcNode;
        }
        return null;
    }

    public static void deleteArc(String VNodeName1, String VNodeName2) {
        if (arcs.get(VNodeName1).isEmpty()) return;
        arcs.get(VNodeName1).removeIf(arcNode -> arcNode.getTo().equals(VNodeName2));
        if (arcs.get(VNodeName2).isEmpty()) return;
        arcs.get(VNodeName2).removeIf(arcNode -> arcNode.getTo().equals(VNodeName1));
    }

    private static void addSpot(String VNodeName) {
        if (!spots.containsKey(VNodeName))
            spots.put(VNodeName, new VNode(VNodeName));
    }

    public static void addSpot(String VNodeName, String intro) {
        if (!spots.containsKey(VNodeName)) {
            spots.put(VNodeName, new VNode(VNodeName, intro));
        }
    }

    public static void deleteSpot(String VNodeName) {
        if (spots.containsKey(VNodeName)) {
            spots.remove(VNodeName);
            arcs.remove(VNodeName);
            for (Map.Entry<String, List<ArcNode>> entry : arcs.entrySet()) {
                for (ArcNode arcNode : entry.getValue()) {
                    if (arcNode.getTo().equals(VNodeName))
                        entry.getValue().remove(arcNode);
                }
            }
        } else System.out.println("[信息]该景点不存在");
    }

    private static int getDistance(String name1, String name2) {
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
        addSpot(name);
    }

    private void DeleteSpot() { // 删除景点
        System.out.println("请输入删除景点名称：");
        Scanner sc = new Scanner(System.in);
        String name = sc.next();
        deleteSpot(name);
    }

    private void AddArc() { // 添加路线
        System.out.println("请输入路线信息：景点1名称 景点2名称 距离");
        System.out.println("例如：北门 狮子山 9");
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        String[] arcStr = line.split(" ");
        addArc(arcStr[0], new ArcNode(arcStr[1], Integer.parseInt(arcStr[2])));
        addArc(arcStr[1], new ArcNode(arcStr[0], Integer.parseInt(arcStr[2])));
    }

    private void DeleteArc() { // 添加路线
        System.out.println("请输入路线信息：景点1名称 景点2名称");
        System.out.println("例如：北门 狮子山");
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        String[] arcStr = line.split(" ");
        deleteArc(arcStr[0], arcStr[1]);
    }

    private void ShortestPath() {
        System.out.println("请输入出发景点与到达景点：景点1名称 景点2名称");
        System.out.println("例如：仙云石 飞流瀑");
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        String[] arcStr = line.split(" ");
        Stack<String> shortestPath_Dij = new Stack<>();
        Stack<String> shortestPath_Floyd = new Stack<>();
        if (arcStr[0].equals(arcStr[1])) {
            System.out.println("[信息]您输入了同一景点");
            return;
        }
        if (spots.get(arcStr[0]) == null || spots.get(arcStr[1]) == null) {
            throw new IllegalArgumentException("[错误]未找到景点");
        }
        int shortestDist = MiniDistance_Dijkstra(arcStr[0], arcStr[1], shortestPath_Dij);
        System.out.println("Dijkstra 算法：");
        System.out.println("最短距离：" + shortestDist);
        System.out.println("最短路径：");
        printShortestPath(shortestPath_Dij);

        shortestDist = MiniDistance_FloydWarshall(arcStr[0], arcStr[1], shortestPath_Floyd);
        System.out.println("FloydWarshall 算法：");
        System.out.println("最短距离：" + shortestDist);
        System.out.println("最短路径：");
        printShortestPath(shortestPath_Floyd);
    }

    private void printShortestPath(Stack<String> shortestPath) {
        int cnt = 1;
        System.out.print("1." + shortestPath.pop());
        while (shortestPath.size() > 1) {
            String cur = shortestPath.pop();
            System.out.println(" -> " + cur);
            System.out.print((++cnt) + "." + cur);
        }
        System.out.println(" -> " + shortestPath.pop());
    }

    /**
     * 使用堆优化的Dijkstra算法，求两景点间最短路径和最短距离
     *
     * @param v1   起始景点名称
     * @param v2   终止景点名称
     * @param path ArcNode逆序入栈，顺序出栈即得到最短路径
     * @return 最短距离
     */
    public static int MiniDistance_Dijkstra(String v1, String v2, Stack<String> path) {
        PriorityBlockingQueue<VNode> candidate = new PriorityBlockingQueue<>(12, spotDistanceComparator);
        for (Map.Entry<String, VNode> entry : spots.entrySet()) { // 每次查询前的初始化
            entry.getValue().setVisited(false);
            entry.getValue().setTotalDist(Integer.MAX_VALUE);
            entry.getValue().setFromSpot(null);
        }
        VNode curSpot = spots.get(v1);
        curSpot.setTotalDist(0); // 初始值为INT_MAX_VALUE
        candidate.add(curSpot);
        do {
            curSpot = candidate.peek();
            assert curSpot != null;
            if (!curSpot.isVisited()) {
                for (ArcNode arcNode : arcs.get(curSpot.getName())) {
                    VNode toSpot = spots.get(arcNode.getTo());
                    int curDist = curSpot.getTotalDist() + arcNode.getDistance();
                    if (curDist < toSpot.getTotalDist()) {
                        // 更新距离
                        toSpot.setTotalDist(curDist);
                        toSpot.setFromSpot(curSpot.getName());
                        if (!candidate.contains(toSpot))
                            candidate.add(toSpot);
                        else { // 重新建堆
                            candidate.remove(toSpot);
                            candidate.add(toSpot);
                        }
                    }
                }
            }
            // 遍历完邻接结点
            curSpot.setVisited(true);
            candidate.poll();
        } while (candidate.size() > 0);

        VNode destSpot = spots.get(v2);
        if (destSpot.isVisited()) { // 如果找到最短路径
            path.push(destSpot.getName());
            String fromSpot = destSpot.getFromSpot(); // 终点的上一个结点
            while (fromSpot != null) {
                path.push(fromSpot); // 入栈
                fromSpot = spots.get(fromSpot).getFromSpot();
            }
            return spots.get(v2).getTotalDist();
        } else // 如果未找到路径
            return Integer.MAX_VALUE;
    }

    /**
     * 使用FloydWarshall算法，求两景点间最短路径和最短距离
     *
     * @param v1   起始景点名称
     * @param v2   终止景点名称
     * @param path ArcNode逆序入栈，顺序出栈即得到最短路径
     * @return 最短距离
     */
    public static int MiniDistance_FloydWarshall(String v1, String v2, Stack<String> path) {
        int[][] dist = new int[spots.size()][spots.size()];
        int[][] pre = new int[spots.size()][spots.size()];
        // pre[i][j] = p 表示i到j的最短路径为 i->...->p->j
        String[] VNodes = new String[spots.size()];
        int i;
        int j;
        for (i = 0; i < spots.size(); i++) {
            for (j = 0; j < spots.size(); j++) {
                pre[i][j] = i;
            }
        }
        int v1Index = 0;
        int v2Index = 0;
        i = 0;
        for (Map.Entry<String, VNode> entry : spots.entrySet()) {
            if (entry.getKey().equals(v1)) v1Index = i;
            if (entry.getKey().equals(v2)) v2Index = i;
            VNodes[i] = entry.getKey();
            i++;
        }
        for (i = 0; i < spots.size(); i++) {
            for (j = 0; j < spots.size(); j++) {
                dist[i][j] = getDistance(VNodes[i], VNodes[j]);
            }
        }
        for (int k = 0; k < spots.size(); k++) {
            for (i = 0; i < spots.size(); i++) {
                for (j = 0; j < spots.size(); j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        pre[i][j] = pre[k][j];
                    }
                }
            }
        }
        if (dist[v1Index][v2Index] < 32767) {
            int t = v2Index;
            while (t != v1Index) {
                path.add(VNodes[t]);
                t = pre[v1Index][t];
            }
            path.add(VNodes[v1Index]);
            return dist[v1Index][v2Index];
        } else return Integer.MAX_VALUE;
    }

    private void CreateTourSortGraph() {
        String root;
        System.out.println("请输入游览起始景点：");
        Scanner sc = new Scanner(System.in);
        root = sc.next();
        List<String> tourPath = new ArrayList<>();
        createTourSortGraph(root, tourPath);
        for (String string : tourPath) {
            System.out.print(string + "->");
        }
        System.out.println(root);
    }

    private void ParkingLotManage() throws ParseException {
        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.println("1.汽车进车场");
            System.out.println("2.汽车出车场");
            System.out.println("3.返回");
            int command = sc.nextInt();
            if (command == 3)
                return;
            System.out.println("车牌号：");
            int carNumber = sc.nextInt();
            Scanner stringScanner = new Scanner(System.in);
            String time;
            switch (command) {
                case 1: {
                    if (cars.containsKey(carNumber))
                        System.out.println("[信息]该车已进入停车场或在便道中");
                    else {
                        System.out.println("进场时间：");
                        time = stringScanner.nextLine();
                        addCar(carNumber, time);
                    }
                    break;
                }
                case 2: {
                    if (!cars.containsKey(carNumber))
                        System.out.println("[错误]该车尚未进入停车场或在便道中");
                    else {
                        System.out.println("离场时间：");
                        time = stringScanner.nextLine();
                        removeCar(carNumber, time);
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    private void addCar(int carNumber, String time) {
        if (cars.get(carNumber) != null) {
            System.out.println("[信息]该车已进入停车场或在便道中");
            return;
        }
        Car car = new Car(carNumber);
        cars.put(carNumber, car);
        if (parkingLot.size() == parkingNum) { // 停车库满
            pavement.add(car); // 停在便道，不计费
            System.out.println("#" + carNumber + " 停放在便道 " + pavement.size() + " 号位");
        } else { // 停车库有空位
            try {
                car.setAr_time(dateFormat.parse(time));
            } catch (ParseException e) {
                e.printStackTrace();
                System.out.println("[异常]时间格式有误");
            }
            parkingLot.add(car); // 进入停车场
            System.out.println("进场时间：" + time);
            System.out.println("#" + carNumber + " 已进入停车场 " + parkingLot.size() + " 号车道");
        }
    }

    private void removeCar(int carNumber, String time) throws ParseException {
        if (parkingLot.size() == 0) {
            System.out.println("[错误]停车场尚无车辆");
            return;
        }
        if (!cars.containsKey(carNumber)) {
            System.out.println("[错误]该车尚未进入停车场或在便道中");
            return;
        }
        while (parkingLot.size() > 0 && parkingLot.peek().getNumber() != carNumber) {
            tempParking.add(parkingLot.pop());
        }
        if (parkingLot.size() == 0) {
            System.out.println("[错误]该车仍在便道中");
        } else { // 车在停车场中
            Car car = parkingLot.pop();

            System.out.println("退场时间：" + time);
            Date quitTime = dateFormat.parse(time);
            Date enterTime = car.getAr_time();
            long diff = quitTime.getTime() - enterTime.getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            System.out.println("停车时长：" + days + " 天 " + hours + " 小时 " + minutes + " 分");
            cars.remove(carNumber);
        }
        while (tempParking.size() > 0) {
            parkingLot.add(tempParking.pop());
        }
        // 便道等候的车进入停车场
        if (parkingLot.size() < parkingNum && pavement.size() != 0) {
            Car pavementFront = pavement.poll();
            parkingLot.add(pavementFront);
            Date curTime = new Date();
            pavementFront.setAr_time(curTime);
            System.out.println("#" + pavementFront.getNumber() + " 已从便道进入停车场 " + parkingLot.size() + " 号车道；进场时间：" + dateFormat.format(curTime));
        }
    }

    /**
     * 最小Hamilton回路作为导游路线图
     * 1.最小生成树算法找出带权图的一颗以root为根的最小生成树T
     * 2.前序遍历树T得到顶点表L
     * 3.将root添加到顶点表L的末尾，按表L中顶点次序组成回路H，作为计算结果
     *
     * @param root 最小生成树的根结点
     */
    public static void createTourSortGraph(String root, List<String> path) {
        if (spots.get(root) == null) // 根结点为空
            return;
        Set<String> tree = new HashSet<>();
        Map<String, TreeNode<String>> treeNodeMap = new HashMap<>();

        TreeNode<String> rootNode = new TreeNode<>(root);
        tree.add(root);
        treeNodeMap.put(root, rootNode);
        Comparator<ArcNode> arcDistanceComparator = (o1, o2) -> Integer.compare(o1.getDistance() - o2.getDistance(), 0);

        while (tree.size() != spots.size()) { // 如果还有顶点需要添加到树中
            PriorityBlockingQueue<ArcNode> candidate = new PriorityBlockingQueue<>(12, arcDistanceComparator);
            for (String spot : tree) { // 对于树中每个顶点
                if (arcs.get(spot) == null) // 当前顶点无边
                    continue;
                for (ArcNode arcNode : arcs.get(spot)) { // 对于顶点的每条弧
                    if (!tree.contains(arcNode.getTo())) { // 如果弧指向的顶点尚未添加到树中
                        arcNode.setFrom(spot);
                        candidate.add(arcNode);
                    }
                }
            }
            if (candidate.peek() == null) {
                System.out.println("[错误]优先级队列为空");
                return;
            }
            String curSpot = candidate.peek().getTo();
            TreeNode<String> curNode = new TreeNode<>(curSpot);
            treeNodeMap.get(candidate.peek().getFrom()).insert(curNode); // 添加为子结点
            tree.add(curSpot);
            treeNodeMap.put(curSpot, curNode);
        }

        List<String> traversal = rootNode.preorderTraversal();
        path.addAll(traversal);
    }
}
