package dao;

import Model.ArcNode;
import Model.ManageSystem;
import Util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GraphDao {
    private Connection conn = null;

    public void createGraph() {
        PreparedStatement stm = null;
        try {
            conn = DBUtil.connectDB();
            stm = conn.prepareStatement("SELECT * FROM scenic_spot.spots");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                String spotName = rs.getString("name");
                String intro = rs.getString("intro");
                ManageSystem.addSpot(spotName, intro);
            }
            rs.close();
            DBUtil.safeClose(stm);

            stm = conn.prepareStatement("SELECT * FROM scenic_spot.arcs");
            rs = stm.executeQuery();
            while (rs.next()) {
                String fromSpot = rs.getString("fromSpot");
                String toSpot = rs.getString("toSpot");
                int distance = rs.getInt("distance");
                ManageSystem.addArc(fromSpot, new ArcNode(toSpot, distance));
                ManageSystem.addArc(toSpot, new ArcNode(fromSpot, distance));
            }
            rs.close();
            DBUtil.safeClose(stm);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("GraphDao: 建图失败");
        } finally {
            DBUtil.safeClose(stm);
            DBUtil.safeClose(conn);
        }
    }

    public void addPath(String fromSpot, String toSpot, int distance) {
        PreparedStatement stm = null;
        try {
            conn = DBUtil.connectDB();
            stm = conn.prepareStatement("INSERT INTO scenic_spot.arcs(fromSpot, toSpot, distance) VALUE (?,?,?)");
            stm.setString(1, fromSpot);
            stm.setString(2, toSpot);
            stm.setInt(3, distance);
            stm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("GraphDao: 添加路线失败");
        } finally {
            DBUtil.safeClose(stm);
            DBUtil.safeClose(conn);
        }
    }

    public void addSpot(String name, String intro) {
        PreparedStatement stm = null;
        try {
            conn = DBUtil.connectDB();
            stm = conn.prepareStatement("INSERT INTO scenic_spot.spots(name, intro) VALUE (?,?)");
            stm.setString(1, name);
            stm.setString(2, intro);
            stm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("GraphDao: 添加景点失败");
        } finally {
            DBUtil.safeClose(stm);
            DBUtil.safeClose(conn);
        }
    }

    public void deleteArc(String spot1, String spot2) {
        PreparedStatement stm = null;
        try {
            conn = DBUtil.connectDB();
            stm = conn.prepareStatement("DELETE FROM scenic_spot.arcs WHERE (fromSpot=? AND toSpot = ?) OR (fromSpot = ? AND toSpot = ?)");
            stm.setString(1, spot1);
            stm.setString(2, spot2);
            stm.setString(3, spot2);
            stm.setString(4, spot1);
            stm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("GraphDao: 删除路线失败");
        } finally {
            DBUtil.safeClose(stm);
            DBUtil.safeClose(conn);
        }
    }

    public void deleteSpot(String spot) {
        PreparedStatement stm = null;
        try {
            conn = DBUtil.connectDB();
            stm = conn.prepareStatement("DELETE FROM scenic_spot.spots WHERE name=?");
            stm.setString(1, spot);
            stm.executeUpdate();
            DBUtil.safeClose(stm);

            stm = conn.prepareStatement("DELETE FROM scenic_spot.arcs WHERE (fromSpot = ? OR toSpot = ?)");
            stm.setString(1, spot);
            stm.setString(2, spot);
            stm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("GraphDao: 删除景点失败");
        } finally {
            DBUtil.safeClose(stm);
            DBUtil.safeClose(conn);
        }
    }
}
