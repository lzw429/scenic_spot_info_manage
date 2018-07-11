package servlet;

import Model.ArcNode;
import Model.ManageSystem;
import Model.VNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet("/graph")
public class GraphServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        HttpSession session = request.getSession();
        if (session.getAttribute("init") == null) {
            ManageSystem.CreateGraph();
            session.setAttribute("init", true);
        }
        String graphJson = getGraphJson();
        // 输出
        System.out.println("GraphServlet: " + graphJson);
        response.setContentType("text/plain");
        response.getWriter().write(graphJson);
    }

    private String getGraphJson() {
        Gson gson = new Gson();

        JsonObject echartsGraph = new JsonObject();

        JsonObject title = new JsonObject();
        title.addProperty("text", "景区示意图");
        echartsGraph.add("title", title);
        JsonObject tooltip = new JsonObject();
        tooltip.addProperty("trigger", "item");
        echartsGraph.add("tooltip", tooltip);
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
        JsonArray data = new JsonArray();
        // 从图获取顶点信息
        for (Map.Entry<String, VNode> entry : ManageSystem.getSpots().entrySet()) {
            JsonObject spot = new JsonObject();
            spot.addProperty("name", entry.getKey());
            data.add(spot);
        }
        seriesContent.add("data", data);
        JsonArray links = new JsonArray();
        // 从图获取弧的信息
        Set<ArcNode> arcNodeSet = new HashSet<>(); // 由于是无向图，用集合记录已经输出的边
        for (Map.Entry<String, List<ArcNode>> entry : ManageSystem.getArcs().entrySet()) {
            String fromSpot = entry.getKey();
            List<ArcNode> arcList = entry.getValue();
            for (ArcNode arc : arcList) {
                if (!arcNodeSet.contains(ManageSystem.getArc(arc.getTo(), fromSpot))) {
                    JsonObject arcJsonObject = new JsonObject();
                    arcJsonObject.addProperty("source", fromSpot);
                    arcJsonObject.addProperty("target", arc.getTo());
                    arcJsonObject.addProperty("value", arc.getDistance());
                    arcNodeSet.add(ManageSystem.getArc(fromSpot, arc.getTo()));
                    links.add(arcJsonObject);
                }
            }
        }
        seriesContent.add("links", links);
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
}


