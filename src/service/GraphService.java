package service;

import Model.ArcNode;
import Model.ManageSystem;
import Model.VNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;

public class GraphService {
    public String getPathJson(Stack<String> path, String mode) {
        Set<String> spotSet = new HashSet<>(path);
        Gson gson = new Gson();

        JsonObject echartsGraph = new JsonObject();

        JsonObject title = new JsonObject();
        title.addProperty("text", "景区示意图");
        echartsGraph.add("title", title);
        echartsGraph.addProperty("tooltip", "item");
        echartsGraph.addProperty("animationDurationUpdate", 1500);
        echartsGraph.addProperty("animationEasingUpdate", "quinticInOut");

        JsonArray series = new JsonArray();
        JsonObject seriesContent = new JsonObject();
        seriesContent.addProperty("type", "graph");
        seriesContent.addProperty("layout", "force");
        JsonObject force = new JsonObject();
        force.addProperty("repulsion", 2000); // 顶点间斥力
        force.addProperty("gravity", 0.8); // 顶点受到的向中心的引力
        seriesContent.add("force", force);
        seriesContent.addProperty("symbolSize", 50); // 顶点大小
        seriesContent.addProperty("roam", true);
        JsonObject label = new JsonObject();
        JsonObject normal = new JsonObject();
        normal.addProperty("show", true);
        label.add("normal", normal);
        seriesContent.add("label", label);
        JsonArray edgeSymbol = new JsonArray();
        edgeSymbol.add("circle");
        edgeSymbol.add("line");
        seriesContent.add("edgeSymbol", edgeSymbol);
        JsonArray edgeSymbolSize = new JsonArray();
        edgeSymbolSize.add(4);
        edgeSymbolSize.add(10);
        seriesContent.add("edgeSymbolSize", edgeSymbolSize);
        JsonObject edgeLabel = new JsonObject();
        JsonObject normal1 = new JsonObject();
        JsonObject textStyle = new JsonObject();
        textStyle.addProperty("fontSize", 20);
        normal1.add("textStyle", textStyle);
        edgeLabel.add("normal", normal1);
        seriesContent.add("edgeLabel", edgeLabel);

        JsonArray links = new JsonArray();
        // 从图获取弧的信息
        Set<ArcNode> arcNodeSet = new HashSet<>(); // 由于是无向图，用集合记录已经输出的边
        String rootSpot = path.peek(); // 用于最后一点与根结点连接
        String pre = path.pop();
        String cur;
        boolean stackEmpty = false; // 用于与root连接
        while (!path.isEmpty()) {
            cur = path.pop();
            if (mode.equals("tour") && path.size() == 0) { // 这是最后一个顶点
                if (!stackEmpty) {
                    path.add(rootSpot); // 根结点再入栈
                    stackEmpty = true;
                }
            }
            ArcNode arc = ManageSystem.getArc(pre, cur);
            assert arc != null;
            JsonObject arcJsonObject = new JsonObject();
            arcJsonObject.addProperty("source", pre);
            arcJsonObject.addProperty("target", cur);
            if (mode.equals("tour") && arc == null) // 导游路线图补边
                arcJsonObject.addProperty("value", 8);
            else arcJsonObject.addProperty("value", arc.getDistance());
            JsonObject lineStyle = new JsonObject();
            lineStyle.addProperty("color", "#ff9800"); // 路径指定颜色
            lineStyle.addProperty("width", 5); // 路径指定线宽
            arcJsonObject.add("lineStyle", lineStyle);
            links.add(arcJsonObject);
            arcNodeSet.add(arc);
            pre = cur;
        }
        for (Map.Entry<String, List<ArcNode>> entry : ManageSystem.getArcs().entrySet()) {
            String fromSpot = entry.getKey();
            List<ArcNode> arcList = entry.getValue();
            for (ArcNode arc : arcList) {
                if (!arcNodeSet.contains(ManageSystem.getArc(arc.getTo(), fromSpot)) && !arcNodeSet.contains(ManageSystem.getArc(fromSpot, arc.getTo()))) {
                    JsonObject arcJsonObject = new JsonObject();
                    arcJsonObject.addProperty("source", fromSpot);
                    arcJsonObject.addProperty("target", arc.getTo());
                    arcJsonObject.addProperty("value", arc.getDistance());
                    arcNodeSet.add(ManageSystem.getArc(fromSpot, arc.getTo()));
                    arcNodeSet.add(ManageSystem.getArc(arc.getTo(), fromSpot));
                    links.add(arcJsonObject);
                }
            }
        }
        seriesContent.add("links", links);

        JsonArray data = new JsonArray();
        // 从图获取顶点信息
        for (Map.Entry<String, VNode> entry : ManageSystem.getSpots().entrySet()) {
            JsonObject spot = new JsonObject();
            spot.addProperty("name", entry.getKey());
            if (spotSet.contains(entry.getKey())) {
                JsonObject itemStyle = new JsonObject();
                itemStyle.addProperty("color", "#9c27b0");
                spot.add("itemStyle", itemStyle);
            }
            data.add(spot);
        }
        seriesContent.add("data", data);

        JsonObject lineStyle = new JsonObject();
        JsonObject normal2 = new JsonObject();
        normal2.addProperty("opacity", 0.9);
        normal2.addProperty("width", 2);
        normal2.addProperty("curveness", 0.1);
        lineStyle.add("normal", normal2);
        seriesContent.add("lineStyle", lineStyle);
        series.add(seriesContent);
        echartsGraph.add("series", series);
        return gson.toJson(echartsGraph);
    }

    public boolean arcLessThan(VNode v1, VNode v2) {
        int size1 = 0;
        int size2 = 0;
        if (ManageSystem.getArcs().get(v1.getName()) != null)
            size1 = ManageSystem.getArcs().get(v1.getName()).size();
        if (ManageSystem.getArcs().get(v2.getName()) != null)
            size2 = ManageSystem.getArcs().get(v2.getName()).size();
        return (size1 - size2) < 0;
    }

    private boolean arcMoreThan(VNode v1, VNode v2) {
        return !arcLessThan(v1, v2);
    }

    public void quickSort(VNode[] arr) {
        qsort(arr, 0, arr.length - 1);
    }

    private void qsort(VNode[] arr, int low, int high) {
        if (low < high) {
            int pivot = partition(arr, low, high); // 将数组分为两部分
            qsort(arr, low, pivot - 1);       // 递归排序左子数组
            qsort(arr, pivot + 1, high);       // 递归排序右子数组
        }
    }

    private int partition(VNode[] arr, int low, int high) {
        VNode pivot = arr[low];
        while (low < high) {
            while (low < high && !arcMoreThan(arr[high], pivot))
                --high;
            arr[low] = arr[high];
            while (low < high && !arcLessThan(arr[low], pivot))
                ++low;
            arr[high] = arr[low];
        }
        arr[low] = pivot;
        return low;
    }
}
