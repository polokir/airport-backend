package dao;

import dbconnection.ConnectionPool;
import models.Specialities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SpecialityDAO {
    public static boolean insert(Specialities specialities){
        int count;
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        try(Connection connection = cd.getConnection()) {
            String sql = "INSERT INTO specialities (id,name) "
                    + "VALUES (?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1,specialities.getId());
            ps.setString(2,specialities.getName());
            count = ps.executeUpdate();
            ps.close();
            cd.releaseConnection(connection);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return (count>0) ? true : false;
    }

    public static boolean update (Specialities specialities){
        int count=0;
        ConnectionPool cp = ConnectionPool.getConnectionPool();
        try(Connection connection = cp.getConnection();) {
            String sql = "UPDATE specialities SET name = ? WHERE id = ?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, specialities.getName());
            st.setInt(2, specialities.getId());
            count = st.executeUpdate();
            st.close();
            cp.releaseConnection(connection);
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (count>0) ? true : false;
    }

    public static boolean delete (int _id){
        int count=0;
        ConnectionPool cp = ConnectionPool.getConnectionPool();
        try(Connection connection = cp.getConnection();) {
            String sql =" DELETE FROM specialities WHERE id = ?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, _id);
            count=st.executeUpdate();
            st.close();
            cp.releaseConnection(connection);
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (count>0) ? true : false;
    }

}
