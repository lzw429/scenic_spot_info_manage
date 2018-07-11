package servlet;

import Model.ManageSystem;
import Model.VNode;
import service.GraphService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet("/spotsort")
public class SpotSortServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        GraphService graphService = new GraphService();
        HttpSession session = request.getSession();

        VNode[] vNodes = new VNode[ManageSystem.getSpots().size()];
        int i = 0;
        for (Map.Entry<String, VNode> entry : ManageSystem.getSpots().entrySet()) {
            vNodes[i++] = entry.getValue();
        }
        graphService.quickSort(vNodes);

    }
}
