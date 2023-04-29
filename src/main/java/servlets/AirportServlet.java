package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.AirportDAO;
import derive.ServletAbstract;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import models.Airport;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.annotation.WebServlet;
import models.Flight;
import models.User;
import org.json.JSONObject;
import utils.TokenManager;


@WebServlet({"/airports","/airports/*"})
public class AirportServlet extends ServletAbstract {

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        PrintWriter out = resp.getWriter();
        getMapping("",req,(m, r) ->{
            List<Airport> airports = AirportDAO.getAllAirports();
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(airports);
            if ((airports.size() == 0)) {
                out.println("EROR 404");
            } else {
                out.println(json);
            }
        });

        getMapping("/(\\d+)",req,(m, r) -> {
            Airport airport = AirportDAO.getAirportById(Integer.valueOf(m.get(0)));
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(airport);
            if ((airport == null)) {
                out.println("EROR 404");
            } else {
                out.println(json);
            }
        });
        getMapping("/flightById/(\\d+)",req,(m, r) ->{
          List<Flight> flights = AirportDAO.getFlightsByID(Integer.parseInt(m.get(0)));
            System.out.println(m.get(0));
          ObjectMapper objectMapper = new ObjectMapper();
          String json = objectMapper.writeValueAsString(flights);
            System.out.println(json);
            if (json==null) {
                out.println("EROR 404");
            } else {
                out.println(json);
            }
        });

    }

    @Override
    @SneakyThrows
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
                   Airport airport = mapper.readValue(requestJSON,Airport.class);
                   AirportDAO.insert(airport);
                   writer.println(new JSONObject(airport));
               }
               else responseError(resp);
           }
        });
    }

    @Override
    @SneakyThrows
    protected void doPut(HttpServletRequest req, HttpServletResponse resp){
        String pathInfo = req.getPathInfo();
        PrintWriter writer = resp.getWriter();
        getMapping("",req,(m, r) -> {
            JSONObject params = TokenManager.Authorization(req);
            if(params==null){
                writer.println("Authorization ERROR");
            }
            if(pathInfo == null||pathInfo.equals("/")){
                if(params.getString("role").equals("ADMIN")){
                    String requestJSON = r.toString();
                    ObjectMapper mapper = new ObjectMapper();
                    Airport airport = mapper.readValue(requestJSON,Airport.class);
                    AirportDAO.update(airport);
                    writer.println(new JSONObject(airport));
                }
                else responseError(resp);
            }
        });
    }

    @Override
    @SneakyThrows
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp){
        getMapping("/(\\d+)",req,(m, r) -> {
            System.out.println("AirportServlet.java: index -> "+m.get(0));
            PrintWriter writer = resp.getWriter();
            JSONObject params = TokenManager.Authorization(req);
            if(params==null){
                writer.println("Authorization ERROR");
            }

            if(m.get(0)!=null && params.getString("role").equals("ADMIN")){
                int requestID = Integer.parseInt(m.get(0));
                System.out.println("deleted "+requestID);
                AirportDAO.delete(requestID);
                writer.println("DELETED");
            }
            else responseError(resp);

        });
    }
}

