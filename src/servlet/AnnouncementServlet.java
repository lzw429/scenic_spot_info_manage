package servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/announcement")
public class AnnouncementServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        ServletContext application = this.getServletContext();
        try {
            String announcement = request.getParameter("announcement");
            application.setAttribute("announcement", announcement);
            System.out.println("AnnouncementServlet :" + announcement);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(403);
        }
    }
}

