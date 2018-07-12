package servlet;

import Model.ArcNode;
import Model.ManageSystem;
import Model.Transaction;
import Model.VNode;
import dao.GraphDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

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
            try {
                VNode deleted = ManageSystem.getSpots().get(spotName);
                VNode vNode = new VNode(deleted.getName(), deleted.getIntro());
                List<ArcNode> arcNodeList = new CopyOnWriteArrayList<>();
                for (Map.Entry<String, List<ArcNode>> entry : ManageSystem.getArcs().entrySet()) {
                    for (ArcNode arcNode : entry.getValue()) {
                        if (arcNode.getTo().equals(spotName) || entry.getKey().equals(spotName)) {
                            ArcNode arcNode1 = new ArcNode(arcNode.getTo(), arcNode.getDistance());
                            if (arcNode.getTo().equals(spotName))
                                arcNode1.setFrom(entry.getKey());
                            else
                                arcNode1.setFrom(spotName);
                            arcNodeList.add(arcNode1);
                        }
                    }
                }
                Transaction transaction = new Transaction(vNode, arcNodeList, Transaction.DELETE_SPOT);
                ManageSystem.getTransactions().add(transaction);
                ManageSystem.deleteSpot(spotName);
                graphDao.deleteSpot(spotName);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("DeleteSpotServlet: 删除景点失败");
            }

            System.out.println("DeleteSpotServlet: 删除景点成功");
        }
    }
}
