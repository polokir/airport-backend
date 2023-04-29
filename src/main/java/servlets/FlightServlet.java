package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.AirportDAO;
import dao.FlightDAO;
import derive.ServletAbstract;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import models.Flight;
import org.json.JSONObject;
import utils.TokenManager;

import java.io.PrintWriter;
import java.util.List;

@WebServlet ({"/flights","/flights/*"})
public class FlightServlet extends ServletAbstract {

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        PrintWriter out = resp.getWriter();
        getMapping("",req,(m, r) ->{
            List<Flight> flights = FlightDAO.getAll();
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(flights);
            if ((flights.size() == 0)) {
                out.println("EROR 404");
            } else {
                out.println(json);
            }
        });

        getMapping("/(\\d+)",req,(m, r) -> {
            Flight flight = FlightDAO.getFlightById(Integer.parseInt(m.get(0)));
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(flight);
            if ((flight == null)) {
                out.println("EROR 404");
            } else {
                out.println(json);
            }
        });
        getMapping("/team/(\\d+)",req,(m, r) -> {
            String team = FlightDAO.getTeam(Integer.parseInt(m.get(0)));
            if ((team == null)) {
                out.println("EROR 404");
            } else {
                out.println(team);
            }
        });
    }

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {
        PrintWriter writer = resp.getWriter();
        getMapping("",req,(m, r) -> {
            String pathInfo = req.getPathInfo();
            JSONObject params = TokenManager.Authorization(req);
            if(params==null){
                writer.println("Authorization ERROR");
            }
            if(pathInfo == null||pathInfo.equals("/")){
                if(params.getString("role").equals("ADMIN")){
                    String requestJSON = r.toString();
                    ObjectMapper mapper = new ObjectMapper();
                    Flight flight = mapper.readValue(requestJSON,Flight.class);
                    FlightDAO.insert(flight);
                    writer.println(new JSONObject(flight));
                }
                else responseError(resp);
            }
        });
    }

    @Override
    @SneakyThrows
    protected void doPut(HttpServletRequest req, HttpServletResponse resp){
        getMapping("",req,(m, r) -> {
            PrintWriter writer = resp.getWriter();
            JSONObject params = TokenManager.Authorization(req);
            if(params==null){
                writer.println("Authorization ERROR");
            }
            if(params.getString("role").equals("ADMIN")){
                String requestJSON = r.toString();
                System.out.println("FlightServlet.java: " + requestJSON);
                ObjectMapper mapper = new ObjectMapper();
                Flight flight = mapper.readValue(requestJSON,Flight.class);
                FlightDAO.update(flight);
                writer.println(new JSONObject(flight));
            }else responseError(resp);
        });
    }

    @SneakyThrows
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp){
        getMapping("/(\\d+)",req,(m, r) -> {
            PrintWriter writer = resp.getWriter();
            JSONObject params = TokenManager.Authorization(req);
            if(params==null){
                writer.println("Authorization ERROR");
            }
            if(m.get(0)!=null && params.getString("role").equals("ADMIN")){
                int requestID = Integer.parseInt(m.get(0));
                FlightDAO.delete(requestID);
                writer.println("DELETED");
            }else responseError(resp);
        });
    }
}


