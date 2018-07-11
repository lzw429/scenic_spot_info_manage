package servlet;

import Model.ManageSystem;
import Model.VNode;
import service.GraphService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

@WebServlet("/spotsort")
public class SpotSortServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        GraphService graphService = new GraphService();
        HttpSession session = request.getSession();

        VNode[] vNodes = new VNode[ManageSystem.getSpots().size()];
        int i = 0;
        for (Map.Entry<String, VNode> entry : ManageSystem.getSpots().entrySet()) {
            vNodes[i++] = entry.getValue();
        }
        graphService.quickSort(vNodes);
        StringBuilder res = new StringBuilder();
        Random random = new Random();
        for (i = 0; i < vNodes.length; i++) {
            res.append("<li>\n" + "<div class=\"collapsible-header\">")
                    .append(vNodes[i].getName())
                    .append("</div>\n")
                    .append("<div class=\"collapsible-body\">\n");
            if (vNodes[i].getIntro() == null)
                res.append("<p>该景点暂无简介</p>");
            else
                res.append("<p>").append(vNodes[i].getIntro()).append("</p>");
            int pathNum; // 度数
            if (ManageSystem.getArcs().get(vNodes[i].getName()) != null) {
                pathNum = ManageSystem.getArcs().get(vNodes[i].getName()).size();
                res.append("<p>岔路数：").append(pathNum).append("</p>");
            }
            res.append("<p>参观人数：")
                    .append(random.nextInt(20000))
                    .append("</p>\n")
                    .append("<p>卫生间：有</p>\n")
                    .append("<p>休息区：有</p>\n")
                    .append("</div>\n")
                    .append("</li>");
        }
        response.getWriter().write(res.toString());
    }
}
