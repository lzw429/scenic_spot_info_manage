import java.util.Scanner;

public class ManageSystem {
    public static void main(String[] args) {
        while (true) {
            printMenu();
            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();
            switch (choice) {
                case 1: // 创建景区景点分布图
                    CreateGraph();
                    break;
                case 2: // 输出景区景点分布图
                    break;
                case 0: // 退出系统
                    break;
                default:
                    System.out.println("【错误】请重新选择");
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
    }

    private static void CreateGraph() {

    }
}
