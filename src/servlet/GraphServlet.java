package servlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GraphServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject echartsGraph = new JsonObject();

        JsonObject title = new JsonObject();
        title.addProperty("text", "景区示意图");
        echartsGraph.addProperty("title", title.toString());

        echartsGraph.addProperty("animationDurationUpdate", 1500);
        echartsGraph.addProperty("animationEasingUpdate", "quinticInOut");

        JsonArray series = new JsonArray();
        JsonObject seriesContent = new JsonObject();
        seriesContent.addProperty("type", "graph");
        seriesContent.addProperty("layout", "force");
        seriesContent.addProperty("symbolSize", 50);
        seriesContent.addProperty("roam", true);
        JsonObject label = new JsonObject();
        JsonObject normal = new JsonObject();
        normal.addProperty("show", true);
        label.addProperty("normal", normal.toString());
        seriesContent.addProperty("label", label.toString());
        JsonArray edgeSymbol = new JsonArray();
        edgeSymbol.add("circle");
        edgeSymbol.add("line");
        seriesContent.addProperty("edgeSymbol", edgeSymbol.toString());
        JsonArray edgeSymbolSize = new JsonArray();
        edgeSymbolSize.add(4);
        edgeSymbolSize.add(10);
        seriesContent.addProperty("edgeSymbolSize", edgeSymbolSize.toString());
        JsonObject edgeLabel = new JsonObject();
        JsonObject normal1 = new JsonObject();
        JsonObject textStyle = new JsonObject();
        textStyle.addProperty("fontSize", 20);
        normal1.addProperty("textStyle", textStyle.toString());
        edgeLabel.addProperty("normal", normal1.toString());
        seriesContent.addProperty("edgeLabel", edgeLabel.toString());
        JsonArray data = new JsonArray();
        // TODO 从图获取顶点信息
        seriesContent.addProperty("data", data.toString());
        JsonArray links = new JsonArray();
        // TODO 从图获取弧的信息
        seriesContent.addProperty("links", links.toString());
        JsonObject lineStyle = new JsonObject();
        
        series.add(seriesContent);

        System.out.println(echartsGraph.toString());
    }
}

