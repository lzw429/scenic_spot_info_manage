package Util;

public class Util {
    public static void swap(String[] str, int i, int j) {
        String temp = str[i];
        str[i] = str[j];
        str[j] = temp;
    }


    // 通过全排列和回溯可搜索非完全图的最短哈密尔顿回路，但效率较低；
    // 对于12个顶点的无向带权图，需要39916880次回溯才能找到指定起点的最短哈密尔顿回路
//    /**
//     * @param str 被全排列的字符串
//     * @param pos 当前位置
//     * @param len 排列范围是[pos, pos + len)
//     */
//    public void nextPermutation(String[] str, int pos, int len) {
//        if (pos == len - 1) {
//
//        } else {
//            for (int i = pos; i < len; i++) {
//                swap(str, pos, i);
//                nextPermutation(str, pos + 1, len);
//                swap(str, pos, i);
//            }
//        }
//    }
//
//    private int backTrackingCount = 0;
//
//    /**
//     * 用于 Hamiltonian 环的递归函数
//     *
//     * @param hamCycle 正在生成的Hamiltonian图
//     * @param pos      当前hamCycle数组的索引
//     * @return 找到解返回true，否则false
//     */
//    private boolean hamCycleUtil(int pos, String[] hamCycle, int minLen) {
//        // 如果所有顶点都被添加到Hamilton环
//        if (pos == spots.size()) {
//            // 如果最后一个顶点与首个顶点间有边
//            Model.ArcNode arc = getArc(hamCycle[pos - 1], hamCycle[0]);
//            if (arc != null) {
//                minLen += arc.getDistance();
//                System.out.println("最短环游长度：" + minLen + " " + (backTrackingCount++));
//                return true;
//            }
//            return false;
//        }
//        // 在Hamilton环中尝试不同的候选顶点，跳过0因为开始时已添加
//        for (Map.Entry<String, Model.VNode> entry : spots.entrySet()) {
//            String curSpot = entry.getKey();
//            if (isSafe(curSpot, hamCycle, pos)) {
//                hamCycle[pos] = curSpot;
//                int dist = getArc(curSpot, hamCycle[pos - 1]).getDistance();
//                minLen += dist;
//                // 递归构建剩余路径
//                if (hamCycleUtil(pos + 1, hamCycle, minLen))
//                    return true;
//                // 如果添加顶点curSpot找不到解，去除
//                minLen -= dist;
//                hamCycle[pos] = null;
//            }
//        }
//        // 如果没有顶点能被添加到现有的环中，返回false
//        return false;
//    }
//    public void hamCyclePermutation(String[] hamCycle, int pos, int len) {
//        int minLenOfAll = Integer.MAX_VALUE;
//        if (pos == len - 1) {
//            int minLen = 0;
//            hamCycleUtil(1, hamCycle, minLen);
//            if (minLen < shortestTourLen)
//                shortestTourLen = minLen;
//        } else {
//            for (int i = pos; i < len; i++) {
//                Util.Util.swap(hamCycle, pos, i);
//                hamCyclePermutation(hamCycle, pos + 1, len);
//                Util.Util.swap(hamCycle, pos, i);
//            }
//        }
//    }
//
//    /**
//     * 检查顶点curSpot是否可在hamCycle[pos]处添加
//     *
//     * @param curSpot  当前顶点
//     * @param hamCycle 正在生成的Hamiltonian图
//     * @param pos      当前hamCycle数组的索引
//     * @return 可添加true，否则false
//     */
//    private boolean isSafe(String curSpot, String[] hamCycle, int pos) {
//        // 判断该顶点是否前一顶点的邻接顶点
//        if (getArc(hamCycle[pos - 1], curSpot) == null)
//            return false;
//        // 检查顶点是否被包含在hamCycle中
//        for (int i = 0; i < pos; i++)
//            if (hamCycle[i].equals(curSpot))
//                return false;
//        return true;
//    }
}