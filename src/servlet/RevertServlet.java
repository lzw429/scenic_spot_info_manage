package servlet;

import Model.ManageSystem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/revert")
public class RevertServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (ManageSystem.getTransactions().size() == 0)
            response.sendError(403);
        else {
            ManageSystem.getTransactions().pop().revert();
            System.out.println("RevertServlet：撤销操作成功");
        }
    }
}
