package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/UserServlet")
public class UserServlet extends BaseServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String certCode_input = request.getParameter("checkcode");
        String certCode_ans = (String) session.getAttribute("certCode");
        if (!certCode_ans.equals(certCode_input)) {
            response.setContentType("text/plain");
            response.getWriter().write("verification failed");
            return;
        }
        if (username.equals("admin") && password.equals("admin")) {
            final int maxAge = 7 * 24 * 60 * 60;
            Cookie c_username = new Cookie("username", username);
            c_username.setMaxAge(maxAge);
            c_username.setPath("/");
            response.addCookie(c_username);
            // 首次登录成功后，将用户名保存到 session 中
            final int maxInactiveInterval = 7 * 24 * 60 * 60;
            session.setMaxInactiveInterval(maxInactiveInterval);
            session.setAttribute("username", username);
            // 将 JSESSIONID 持久化
            Cookie c_JSESSIONID = new Cookie("JSESSIONID", session.getId());
            c_JSESSIONID.setMaxAge(maxInactiveInterval);
            c_JSESSIONID.setPath("/");
            response.addCookie(c_JSESSIONID);
            // 登录成功后，跳转到管理员页
            response.setContentType("text/plain");
            response.getWriter().write("/admin");
        } else {
            response.sendError(403);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
