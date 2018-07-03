public class Util {
    public static String spotNumberToName(int number) {
        String res = "";
        switch (number) {
            case 1:
                res = "北门";
                break;
            case 2:
                res = "狮子山";
                break;
            case 3:
                res = "仙云石";
                break;
            case 4:
                res = "一线天";
                break;
            case 5:
                res = "飞流瀑";
                break;
            case 6:
                res = "仙武湖";
                break;
            case 7:
                res = "九曲桥";
                break;
            case 8:
                res = "观云台";
                break;
            case 9:
                res = "碧水潭";
                break;
            case 10:
                res = "花卉园";
                break;
            case 11:
                res = "红叶亭";
                break;
            case 12:
                res = "朝日峰";
                break;
        }
        return res;
    }
}
