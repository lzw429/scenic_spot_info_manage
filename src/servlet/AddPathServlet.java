package servlet;

import Model.ArcNode;
import Model.ManageSystem;
import dao.GraphDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/addpath")
public class AddPathServlet extends BaseServlet {
    GraphDao graphDao = new GraphDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        HttpSession session = request.getSession();
        assert session.getAttribute("init") != null;
        String spot1 = request.getParameter("spot1name");
        String spot2 = request.getParameter("spot2name");
        int distance = Integer.parseInt(request.getParameter("distance"));
        if (ManageSystem.getArc(spot1, spot2) != null) { // 路径已经存在
            response.sendError(403);
        } else { // 路径不存在，添加
            ManageSystem.addArc(spot1, new ArcNode(spot2, distance));
            ManageSystem.addArc(spot2, new ArcNode(spot1, distance));
            graphDao.addPath(spot1, spot2, distance);
            System.out.println("AddPathServlet: 添加成功");
        }
    }
}
