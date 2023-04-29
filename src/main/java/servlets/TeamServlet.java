package servlets;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import dao.FlightDAO;
import dao.TeamDAO;
import derive.ServletAbstract;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import models.Flight;
import models.Team;
import org.json.JSONObject;
import utils.TokenManager;

import java.io.PrintWriter;
import java.util.List;

@WebServlet({"/teams","/teams/*"})
public class TeamServlet extends ServletAbstract {


    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){

        getMapping("",req,(m, r) ->{
            PrintWriter out = resp.getWriter();
            JSONObject params = TokenManager.Authorization(req);
            if(params==null){
                out.println("Access denied");
                return;
            }
            if(params.getString("role").equals("DISPATCHER") || params.getString("role").equals("ADMIN")){
                List<Team> teams = TeamDAO.getAll();
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(teams);
                if ((teams.size() == 0)) {
                    out.println("EROR 404");
                } else {
                    out.println(json);
                }
            }

        });

        getMapping("/(\\d+)",req,(m, r) -> {
            PrintWriter out = resp.getWriter();
            JSONObject params = TokenManager.Authorization(req);
            if(params==null){
                out.println("Access denied");
                return;
            }
            if(params.getString("role").equals("DISPATCHER")){
                Team team = TeamDAO.getTeamById(Integer.parseInt(m.get(0)));
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(team);
                if ((team == null)) {
                    out.println("EROR 404");
                } else {
                    out.println(json);
                }
            }

        });
    }

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {
        PrintWriter writer = resp.getWriter();
        getMapping("",req,(m, r) -> {
            JSONObject params = TokenManager.Authorization(req);
            if(params==null){
                writer.println("Authorization ERROR");
            }

            if(params.getString("role").equals("DISPATCHER") || params.getString("role").equals("ADMIN")){
                String requestJSON = r.toString();
                ObjectMapper mapper = new ObjectMapper();
                Team team = mapper.readValue(requestJSON,Team.class);
                TeamDAO.insert(team);
                writer.println(new JSONObject(team));
            }
            else responseError(resp);

        });
    }

    @SneakyThrows
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp){
        getMapping("",req,(m, r) -> {
            PrintWriter writer = resp.getWriter();
            JSONObject params = TokenManager.Authorization(req);
            if(params==null){
                writer.println("Authorization ERROR");
            }
            if(params.getString("role").equals("DISPATCHER") || params.getString("role").equals("ADMIN")){
                String requestJSON = r.toString();
                System.out.println("TeamServlet.java: " + requestJSON);
                ObjectMapper mapper = new ObjectMapper();
                Team team = mapper.readValue(requestJSON,Team.class);
                TeamDAO.update(team);
                writer.println(new JSONObject(team));
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
            if(m.get(0)!=null && params.getString("role").equals("DISPATCHER") || params.getString("role").equals("ADMIN")){
                int requestID = Integer.parseInt(m.get(0));
                TeamDAO.delete(requestID);
                writer.println("DELETED");
            }else responseError(resp);
        });
    }
}
