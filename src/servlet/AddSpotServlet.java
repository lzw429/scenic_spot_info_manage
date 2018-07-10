package servlet;

import Model.ManageSystem;
import Model.VNode;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/addspot")
public class AddSpotServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        HttpSession session = request.getSession();
        assert session.getAttribute("init") != null;
        String spotName = request.getParameter("spotName");
        String spotIntro = request.getParameter("spotIntro");
        if (ManageSystem.getSpots().containsKey(spotName)) {
            response.sendError(403);
        } else {
            VNode spot = new VNode(spotName);
            spot.setIntro(spotIntro);
            ManageSystem.getSpots().put(spotName, spot);
            System.out.println("AddSpotServlet: 添加景点成功");
        }
    }
}
