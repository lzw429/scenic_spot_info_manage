package servlet;

import Model.ArcNode;
import Model.ManageSystem;
import Model.Transaction;
import dao.GraphDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
            List<ArcNode> arcNodeList = new CopyOnWriteArrayList<>();
            addToArcNodeList(spot1, spot2, arcNodeList);
            addToArcNodeList(spot2, spot1, arcNodeList);
            Transaction transaction = new Transaction(arcNodeList, Transaction.DELETE_ARC);
            transaction.setSpot1(spot1);
            transaction.setSpot2(spot2);
            ManageSystem.getTransactions().add(transaction);
            ManageSystem.deleteArc(spot1, spot2);
            graphDao.deleteArc(spot1, spot2);
            System.out.println("DeletePathServlet: 删除路线成功");
        }
    }

    private void addToArcNodeList(String spot1, String spot2, List<ArcNode> arcNodeList) {
        if (!ManageSystem.getArcs().get(spot1).isEmpty()) {
            for (ArcNode arcNode : ManageSystem.getArcs().get(spot1)) {
                if (arcNode.getTo().equals(spot2)) {
                    ArcNode arc = new ArcNode(spot2, arcNode.getDistance());
                    arc.setFrom(spot1);
                    arcNodeList.add(arc);
                }
            }
        }
    }
}
