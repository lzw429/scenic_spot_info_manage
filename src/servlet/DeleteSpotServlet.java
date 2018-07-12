package servlet;

import Model.ManageSystem;
import dao.GraphDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/deletespot")
public class DeleteSpotServlet extends BaseServlet {
    private GraphDao graphDao = new GraphDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        HttpSession session = request.getSession();
        if (session.getAttribute("init") == null) {
            System.out.println("DeleteSpotServlet: 初始化错误");
        }
        String spotName = request.getParameter("spotName");
        if (!ManageSystem.getSpots().containsKey(spotName)) { // 景点不存在
            response.sendError(403);
        } else {
            ManageSystem.deleteSpot(spotName);
            graphDao.deleteSpot(spotName);
            System.out.println("DeleteSpotServlet: 删除景点成功");
        }
    }
}
