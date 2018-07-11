package servlet;

import Model.ManageSystem;
import service.GraphService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Stack;

@WebServlet("/shortestpath")
public class ShortestPathServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        HttpSession session = request.getSession();
        GraphService graphService = new GraphService();
        assert session.getAttribute("init") != null;

        String startSpot = request.getParameter("startSpotName");
        String endSpot = request.getParameter("endSpotName");
        if (!ManageSystem.getSpots().containsKey(startSpot) || !ManageSystem.getSpots().containsKey(endSpot)) {
            System.out.println("ShortestPathServlet: 地点不存在");
            response.sendError(403);
        } else {
            Stack<String> path = new Stack<>();
            ManageSystem.MiniDistance_Dijkstra(startSpot, endSpot, path); // 最短路算法
            String shortestPathJson = graphService.getPathJson(path,"shortest");
            System.out.println("ShortestPathServlet: " + shortestPathJson);
            response.getWriter().write(shortestPathJson);
        }
    }
}
