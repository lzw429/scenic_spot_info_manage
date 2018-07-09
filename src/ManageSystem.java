import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

public class ManageSystem {
    private HashMap<String, List<ArcNode>> arcs;
    private HashMap<String, VNode> spots;
    private HashMap<Integer, Car> cars;
    private Stack<Car> parkingLot;
    private Stack<Car> tempParking;
    private Queue<Car> pavement;
    private final int parkingNum = 5; // 停车场大小
    private Comparator<VNode> distanceComparator = (o1, o2) -> Integer.compare(o1.getTotalDist() - o2.getTotalDist(), 0); // 距离比较
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int shortestTourLen = Integer.MAX_VALUE;

    private ManageSystem() {
        arcs = new HashMap<>();
        spots = new HashMap<>();
        cars = new HashMap<>();
        parkingLot = new Stack<>();
        tempParking = new Stack<>();
        pavement = new LinkedList<>();
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

    private void CreateGraph() {
        String line;
        BufferedReader reader;
        try {
            String filePath = "C:\\Users\\舒意恒\\Documents\\GitHub\\scenic_spot_info_manage\\data\\graph.txt";
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
            for (String spotName : spotNames) {
                int distance = getDistance(spotNames[i], spotName);
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

    /**
     * @param VNodeName1 景点1
     * @param VNodeName2 景点2
     * @return 景点1到景点2的弧
     */
    private ArcNode getArc(String VNodeName1, String VNodeName2) {
        if (arcs.get(VNodeName1).isEmpty())
            return null;
        for (ArcNode arcNode : arcs.get(VNodeName1)) {
            if (arcNode.getTo().equals(VNodeName2))
                return arcNode;
        }
        return null;
    }

    private void deleteArc(String VNodeName1, String VNodeName2) {
        if (arcs.get(VNodeName1).isEmpty()) return;
        arcs.get(VNodeName1).removeIf(arcNode -> arcNode.getTo().equals(VNodeName2));
        if (arcs.get(VNodeName2).isEmpty()) return;
        arcs.get(VNodeName2).removeIf(arcNode -> arcNode.getTo().equals(VNodeName1));
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

        this.addSpot(getMaxSpotNum() + 1, name);
    }

    private int getMaxSpotNum() {
        int res = Integer.MIN_VALUE;
        for (Map.Entry<String, VNode> entry : spots.entrySet()) {
            if (entry.getValue().getNumber() > res)
                res = entry.getValue().getNumber();
        }
        return res;
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
        int shortestDist = this.MiniDistance_Dijkstra(arcStr[0], arcStr[1], shortestPath_Dij);
        System.out.println("Dijkstra 算法：");
        System.out.println("最短距离：" + shortestDist);
        System.out.println("最短路径：");
        printShortestPath(shortestPath_Dij);

        shortestDist = this.MiniDistance_FloydWarshall(arcStr[0], arcStr[1], shortestPath_Floyd);
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
    private int MiniDistance_Dijkstra(String v1, String v2, Stack<String> path) {


        PriorityBlockingQueue<VNode> candidate = new PriorityBlockingQueue<>(12, distanceComparator);
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
    private int MiniDistance_FloydWarshall(String v1, String v2, Stack<String> path) {
        int[][] dist = new int[spots.size()][spots.size()];
        int[][] pre = new int[spots.size()][spots.size()];
        // pre[i][j] = p 表示i到j的最短路径为 i->...->p->j
        String[] VNodes = new String[spots.size()];
        int i = 0;
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
        i = j = 0;
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
        Stack<String> tourPath = new Stack<>();
        this.createTourSortGraph(root, tourPath);
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
    public void createTourSortGraph(String root, Stack<String> tourPath) {
      /*  if (spots.get(root) == null)
            return;
        Set<TreeNode<String>> tree = new HashSet<>();
        TreeNode<String> rootNode = new TreeNode<>(root);
        tree.add(rootNode);

        PriorityBlockingQueue<ArcNode> candidate = new PriorityBlockingQueue<>(12, distanceComparator);
        while (tree.size() != spots.size()) { // 如果还有结点需要添加到树中
            for (TreeNode<String> treeNode : tree) { // 对于树中每个结点
                if (arcs.get(treeNode.value) == null)
                    continue;
                for (ArcNode arcNode : arcs.get(treeNode.value)) { // 对于结点的每条弧
                    if (!tree.contains(arcNode.getTo())) {
                        candidate.add(arcNode);
                    }
                }
            }

        }*/
        String[] hamCycle = new String[spots.size()];
        hamCycle[0] = root;
        int i = 1;
        for (Map.Entry<String, VNode> entry : spots.entrySet()) {
            String curSpot = entry.getKey();
            if (!curSpot.equals(root))
                hamCycle[i++] = curSpot;
        }
        shortestTourLen = Integer.MAX_VALUE;
        hamCyclePermutation(hamCycle, 0, spots.size());
        System.out.println("最短环游长度：" + shortestTourLen);
        for (i = 0; i < spots.size(); i++)
            System.out.print(" " + hamCycle[i] + " ");
    }


    public void hamCyclePermutation(String[] hamCycle, int pos, int len) {
        int minLenOfAll = Integer.MAX_VALUE;
        if (pos == len - 1) {
            int minLen = 0;
            hamCycleUtil(1, hamCycle, minLen);
            if (minLen < shortestTourLen)
                shortestTourLen = minLen;
        } else {
            for (int i = pos; i < len; i++) {
                Util.swap(hamCycle, pos, i);
                hamCyclePermutation(hamCycle, pos + 1, len);
                Util.swap(hamCycle, pos, i);
            }
        }
    }

    /**
     * 检查顶点curSpot是否可在hamCycle[pos]处添加
     *
     * @param curSpot  当前顶点
     * @param hamCycle 正在生成的Hamiltonian图
     * @param pos      当前hamCycle数组的索引
     * @return 可添加true，否则false
     */
    private boolean isSafe(String curSpot, String[] hamCycle, int pos) {
        // 判断该顶点是否前一顶点的邻接顶点
        if (getArc(hamCycle[pos - 1], curSpot) == null)
            return false;
        // 检查顶点是否被包含在hamCycle中
        for (int i = 0; i < pos; i++)
            if (hamCycle[i].equals(curSpot))
                return false;
        return true;
    }

    private int backTrackingCount = 0;

    /**
     * 用于 Hamiltonian 环的递归函数
     *
     * @param hamCycle 正在生成的Hamiltonian图
     * @param pos      当前hamCycle数组的索引
     * @return 找到解返回true，否则false
     */
    private boolean hamCycleUtil(int pos, String[] hamCycle, int minLen) {
        // 如果所有顶点都被添加到Hamilton环
        if (pos == spots.size()) {
            // 如果最后一个顶点与首个顶点间有边
            ArcNode arc = getArc(hamCycle[pos - 1], hamCycle[0]);
            if (arc != null) {
                minLen += arc.getDistance();
                System.out.println("最短环游长度：" + minLen + " " + (backTrackingCount++));
                return true;
            }
            return false;
        }
        // 在Hamilton环中尝试不同的候选顶点，跳过0因为开始时已添加
        for (Map.Entry<String, VNode> entry : spots.entrySet()) {
            String curSpot = entry.getKey();
            if (isSafe(curSpot, hamCycle, pos)) {
                hamCycle[pos] = curSpot;
                int dist = getArc(curSpot, hamCycle[pos - 1]).getDistance();
                minLen += dist;
                // 递归构建剩余路径
                if (hamCycleUtil(pos + 1, hamCycle, minLen))
                    return true;
                // 如果添加顶点curSpot找不到解，去除
                minLen -= dist;
                hamCycle[pos] = null;
            }
        }
        // 如果没有顶点能被添加到现有的环中，返回false
        return false;
    }


}
