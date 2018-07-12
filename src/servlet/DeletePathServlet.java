package servlet;

import Model.ManageSystem;
import dao.GraphDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/deletepath")
public class DeletePathServlet extends BaseServlet {
    private GraphDao graphDao = new GraphDao();

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
        if (ManageSystem.getArc(spot1, spot2) == null) { // 路径不存在
            response.sendError(403);
        } else { // 路径存在，删除
            ManageSystem.deleteArc(spot1, spot2);
            graphDao.deleteArc(spot1, spot2);
            System.out.println("DeletePathServlet: 删除路线成功");
        }
    }
}
