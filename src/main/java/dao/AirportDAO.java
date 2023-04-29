package dao;

import com.google.gson.JsonObject;
import dbconnection.ConnectionPool;
import models.Airport;
import models.Flight;
import models.User;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AirportDAO {
    public static void insert(Airport airport){
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        try(Connection connection = cd.getConnection()) {
            String sql = "INSERT INTO public.airport (flight_id,name,index) "
                    + "VALUES (?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1,airport.getFlight_id());
            ps.setString(2, airport.getName());
            ps.setString(3,airport.getIndex());
            ps.executeUpdate();
            ps.close();
            cd.releaseConnection(connection);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getNameById(int _id){
        String resultName = null;
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        try(Connection connection = cd.getConnection()) {
            String sql = "SELECT name"
                    + "FROM airport"
                    + "WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1,_id);
            ResultSet set = ps.executeQuery();
            if(set.next()){
                resultName = set.getString(1);
            }
            ps.close();
            cd.releaseConnection(connection);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return (new Boolean(resultName)) ? resultName : "Not found";
    }

    public static List<Flight> getFlightsByID(int id) {
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        List<Flight> flights = new ArrayList<>();
        try (Connection connection = cd.getConnection()) {
            String sql = "SELECT flight.id, flight.team_id, flight.departure, flight.destination, flight.passengers_number, flight.plane\n" +
                    "FROM public.flight\n" +
                    "INNER JOIN public.airport\n" +
                    "ON flight.id = airport.flight_id\n" +
                    "WHERE airport.flight_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
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
            statement.close();
            cd.releaseConnection(connection);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        JSONObject resJson = new JSONObject();
        resJson.put("flight_id", flights);
        return flights;
    }

    public static boolean update(Airport airport){
        int count=0;
        ConnectionPool cp = ConnectionPool.getConnectionPool();
        try(Connection connection = cp.getConnection();) {
            String sql = "UPDATE public.airport SET flight_id = ?, name = ?, index = ?  WHERE id = ?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, airport.getFlight_id());
            st.setString(2, airport.getName());
            st.setString(3, airport.getIndex());
            st.setInt(4,airport.getId());
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

    public static void delete (int _id){
        ConnectionPool cp = ConnectionPool.getConnectionPool();
        try(Connection connection = cp.getConnection();) {
            String sql =" DELETE FROM public.airport WHERE id = ?";
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

    public static Airport getAirportById(int id) throws SQLException, InterruptedException {
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        Connection connection = cd.getConnection();
        String sql = "SELECT * FROM public.airport WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            Airport airport = new Airport();
            airport.setId(result.getInt("id"));
            airport.setFlight_id(result.getInt("flight_id"));
            airport.setName(result.getString("name"));
            airport.setIndex(result.getString("index"));
            return airport;
        } else {
            return null;
        }
    }
    public static List<Airport> getAllAirports() throws SQLException, InterruptedException {
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        Connection connection = cd.getConnection();
        String sql = "SELECT * FROM public.airport";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        List<Airport> airports = new ArrayList<>();
        while (result.next()) {
            Airport airport = new Airport();
            airport.setId(result.getInt("id"));
            airport.setFlight_id(result.getInt("flight_id"));
            airport.setName(result.getString("name"));
            airport.setIndex(result.getString("index"));
            airports.add(airport);
        }

        cd.releaseConnection(connection);
        return airports;
    }
}


