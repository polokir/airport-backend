package dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import dbconnection.ConnectionPool;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import models.Airport;
import models.Flight;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
class baseTeam{
    String name;
    String speciality;
}

public class FlightDAO {
    public static void insert(Flight flight){
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        try(Connection connection = cd.getConnection()) {
            String sql = "INSERT INTO public.flight (team_id,departure,destination,passengers_number,plane) "
                    + "VALUES (?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1,flight.getTeam_id());
            ps.setString(2, flight.getDeparture());
            ps.setString(3,flight.getDestination());
            ps.setInt(4,flight.getPassengers_number());
            ps.setString(5, flight.getPlane());
           ps.executeUpdate();
            ps.close();
            cd.releaseConnection(connection);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void update(Flight flight){
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        try(Connection connection = cd.getConnection()) {
            String sql = "UPDATE public.flight SET team_id = ?, departure = ?, destination = ?, passengers_number = ?, plane = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1,flight.getTeam_id());
            ps.setString(2,flight.getDeparture());
            ps.setString(3, flight.getDestination());
            ps.setInt(4,flight.getPassengers_number());
            ps.setString(5,flight.getPlane());
            ps.setInt(6, flight.getId());
            ps.executeUpdate();
            ps.close();
            cd.releaseConnection(connection);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(int id){
        ConnectionPool cp = ConnectionPool.getConnectionPool();
        try(Connection connection = cp.getConnection();) {
            String sql =" DELETE FROM public.flight WHERE id = ?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, id);
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
    public static List<Flight> getAll() {
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        Connection connection = cd.getConnection();
        String sql = "SELECT * FROM public.flight";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        List<Flight> flights = new ArrayList<>();
        while (result.next()) {
            Flight flight = new Flight();
            flight.setId(result.getInt("id"));
            flight.setTeam_id(result.getInt("team_id"));
            flight.setDeparture(result.getString("departure"));
            flight.setDestination(result.getString("destination"));
            flight.setPassengers_number(result.getInt("passengers_number"));
            flight.setPlane(result.getString("plane"));
            flights.add(flight);
        }
        cd.releaseConnection(connection);
        return flights;
    }

    @SneakyThrows
    public static Flight getFlightById(int id)  {
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        Connection connection = cd.getConnection();
        String sql = "SELECT * FROM public.flight WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            Flight flight = new Flight();
            flight.setId(result.getInt("id"));
            flight.setTeam_id(result.getInt("team_id"));
            flight.setDeparture(result.getString("departure"));
            flight.setDestination(result.getString("destination"));
            flight.setPassengers_number(result.getInt("passengers_number"));
            flight.setPlane(result.getString("plane"));
            return flight;
        } else {
            return null;
        }
    }
    @SneakyThrows
     public static String getTeam(int id){
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        Connection connection = cd.getConnection();
        String sql = "SELECT staff.name, specialities.name\n" +
                "FROM ((staff FULL JOIN team ON team.first_pilot_id=staff.id OR team.second_pilot_id=staff.id OR team.first_stuard_id = staff.id OR team.second_stuard_id = staff.id OR team.radio_spec_id = staff.id OR team.shturman_id = staff.id ) INNER JOIN specialities ON specialities.id = staff.speciality_id)\n" +
                "WHERE team.id=?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        List<baseTeam> teams= new ArrayList<>();
        while (result.next()){
            baseTeam team= new baseTeam();
            team.setName(result.getString(1));
            team.setSpeciality(result.getString(2));
            teams.add(team);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String json= objectMapper.writeValueAsString(teams);
        cd.releaseConnection(connection);
        return  json;
    }
}
