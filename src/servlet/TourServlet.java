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

@WebServlet("/tour")
public class TourServlet extends BaseServlet {
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

        String rootSpot = request.getParameter("rootSpotName");

        if (!ManageSystem.getSpots().containsKey(rootSpot)) {
            System.out.println("TourServlet: 地点不存在");
            response.sendError(403);
        } else {
            Stack<String> path = new Stack<>();
            ManageSystem.createTourSortGraph(rootSpot, path);
            String tourPathJson = graphService.getPathJson(path, "tour", 115);
            System.out.println("TourServlet: " + tourPathJson);
            response.getWriter().write(tourPathJson);
        }
    }


}
