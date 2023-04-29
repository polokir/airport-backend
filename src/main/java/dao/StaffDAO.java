package dao;

import dbconnection.ConnectionPool;
import lombok.SneakyThrows;
import models.Flight;
import models.Staff;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    public static void insert(Staff staff){

        ConnectionPool cd = ConnectionPool.getConnectionPool();
        try(Connection connection = cd.getConnection()) {
            String sql = "INSERT INTO public.staff (speciality_id,name,salary) "
                    + "VALUES (?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1,staff.getSpeciality_id());
            ps.setString(2,staff.getName());
            ps.setFloat(3,staff.getSalary());
            ps.executeUpdate();
            ps.close();
            cd.releaseConnection(connection);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void update(Staff staff){
        ConnectionPool cp = ConnectionPool.getConnectionPool();
        try(Connection connection = cp.getConnection();) {
            String sql = "UPDATE public.staff SET speciality_id=?,name=?,slary=? WHERE id=?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, staff.getSpeciality_id());
            st.setString(2, staff.getName());
            st.setFloat(3, staff.getSalary());
            st.setInt(4,staff.getId());
            st.executeUpdate();
            st.close();
            cp.releaseConnection(connection);

        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void delete(int _id){
        ConnectionPool cp = ConnectionPool.getConnectionPool();
        try(Connection connection = cp.getConnection();) {
            String sql ="DELETE FROM public.ёstaff WHERE id = ?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, _id);
            st.executeUpdate();
            st.close();
            cp.releaseConnection(connection);
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public static List<Staff> getAll() {
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        Connection connection = cd.getConnection();
        String sql = "SELECT * FROM public.staff";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        List<Staff> staffs = new ArrayList<>();
        while (result.next()) {
            Staff staff = new Staff();
            staff.setId(result.getInt("id"));
            staff.setSpeciality_id(result.getInt("speciality_id"));
            staff.setName(result.getString("name"));
            staff.setSalary(result.getFloat("salary"));
            staffs.add(staff);
        }
        return staffs;
    }

    @SneakyThrows
    public static Staff getById(int id)  {
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        Connection connection = cd.getConnection();
        String sql = "SELECT * FROM public.staff WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            Staff staff = new Staff();
            staff.setId(result.getInt("id"));
            staff.setSpeciality_id(result.getInt("speciality_id"));
            staff.setName(result.getString("name"));
            staff.setSalary(result.getFloat("salary"));
            return staff;
        } else {
            return null;
        }
    }

}

