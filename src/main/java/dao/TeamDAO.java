package dao;

import dbconnection.ConnectionPool;
import lombok.SneakyThrows;
import models.Flight;
import models.Team;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamDAO {

    /*private int id;
    private int first_pilot_id;
    private int second_pilot_id;
    private int first_stuarad_id;
    private int second_stuarad_id;
    private int radio_spec_id;
    private int shturman_id;*/



    @SneakyThrows
    public static List<Team> getAll(){
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        Connection connection = cd.getConnection();
        String sql = "SELECT * FROM public.team";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        List<Team> teams = new ArrayList<>();
        while (result.next()) {
            Team team = new Team();
            team.setId(result.getInt("id"));
            team.setFirst_pilot_id(result.getInt("first_pilot_id"));
            team.setSecond_pilot_id(result.getInt("second_pilot_id"));
            team.setFirst_stuard_id(result.getInt("first_stuard_id"));
            team.setSecond_stuard_id(result.getInt("second_stuard_id"));
            team.setRadio_spec_id(result.getInt("radio_spec_id"));
            team.setShturman_id(result.getInt("shturman_id"));
            teams.add(team);
        }
        cd.releaseConnection(connection);
        return teams;
    }

    @SneakyThrows
    public static Team getTeamById(int _id){
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        Connection connection = cd.getConnection();
        String sql = "SELECT * FROM public.team WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, _id);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            Team team = new Team();
            team.setId(result.getInt("id"));
            team.setFirst_pilot_id(result.getInt("first_pilot_id"));
            team.setSecond_pilot_id(result.getInt("second_pilot_id"));
            team.setFirst_stuard_id(result.getInt("first_stuard_id"));
            team.setSecond_stuard_id(result.getInt("second_stuard_id"));
            team.setRadio_spec_id(result.getInt("radio_spec_id"));
            team.setShturman_id(result.getInt("shturman_id"));
            return team;
        } else {
            return null;
        }
    }

    public  static void insert(Team team){

        ConnectionPool cd = ConnectionPool.getConnectionPool();
        try(Connection connection = cd.getConnection()) {
            String sql = "INSERT INTO public.team (first_pilot_id,second_pilot_id,first_stuard_id,second_stuard_id,radio_spec_id,shturman_id) "
                    + "VALUES (?,?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1,team.getFirst_pilot_id());
            ps.setInt(2, team.getSecond_pilot_id());
            ps.setInt(3, team.getFirst_stuard_id());
            ps.setInt(4, team.getSecond_stuard_id());
            ps.setInt(5, team.getRadio_spec_id());
            ps.setInt(6, team.getShturman_id());
            ps.executeUpdate();
            ps.close();
            cd.releaseConnection(connection);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void update(Team team){

        ConnectionPool cp = ConnectionPool.getConnectionPool();
        try(Connection connection = cp.getConnection();) {
            String sql = "UPDATE team SET first_pilot_id=?,second_pilot_id=?,first_stuard_id=?,second_stuard_id=?,radio_spec_id=?,shturman_id=? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1,team.getFirst_pilot_id());
            ps.setInt(2, team.getSecond_pilot_id());
            ps.setInt(3, team.getFirst_stuard_id());
            ps.setInt(4, team.getSecond_stuard_id());
            ps.setInt(5, team.getRadio_spec_id());
            ps.setInt(6, team.getShturman_id());
            ps.setInt(7,team.getId());
            ps.executeUpdate();
            ps.close();
            cp.releaseConnection(connection);
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static boolean delete(int _id) {
        int count=0;
        ConnectionPool cp = ConnectionPool.getConnectionPool();
        try(Connection connection = cp.getConnection();) {
            String sql =" DELETE FROM public.team WHERE id = ?";
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

