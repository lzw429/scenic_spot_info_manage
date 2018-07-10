package servlet;

import Model.ManageSystem;

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
        assert session.getAttribute("init") != null;
        String startSpot = request.getParameter("startSpot");
        String endSpot = request.getParameter("endSpot");
        if (!ManageSystem.getSpots().containsKey(startSpot) || !ManageSystem.getSpots().containsKey(endSpot)) {
            response.sendError(403);
        } else {

        }
    }

    private String getShortestPath(String startSpot, String endSpot) {
        Stack<String> path = new Stack<>();
        ManageSystem.MiniDistance_Dijkstra(startSpot, endSpot, path);
    }
}
