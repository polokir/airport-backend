package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.FlightDAO;
import dao.StaffDAO;
import dao.TeamDAO;
import derive.ServletAbstract;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import models.Flight;
import models.Staff;
import models.Team;
import org.json.JSONObject;
import utils.TokenManager;

import java.io.PrintWriter;
import java.util.List;


@WebServlet({"/staff" ,"/staff/*"})
public class StaffServlet extends ServletAbstract {
    @Override
    @SneakyThrows
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        getMapping("",req,(m, r) ->{
            PrintWriter writer = resp.getWriter();
            JSONObject params = TokenManager.Authorization(req);
            if(params==null){
                writer.println("Authorization ERROR");
            }
            if(params.getString("role").equals("DISPATCHER") || params.getString("role").equals("ADMIN")){
                List<Staff> staffList = StaffDAO.getAll();
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(staffList);
                writer.println(json);
            }else responseError(resp);
        });

        getMapping("/(\\d+)",req,(m, r) -> {
            PrintWriter out = resp.getWriter();
            JSONObject params = TokenManager.Authorization(req);
            if(params==null){
                out.println("Access denied");
                return;
            }
            if(params.getString("role").equals("DISPATCHER")){
                Staff staff = StaffDAO.getById(Integer.parseInt(m.get(0)));
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(staff);
                if ((staff == null)) {
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

            if(params.getString("role").equals("DISPATCHER")|| params.getString("role").equals("ADMIN")){
                String requestJSON = r.toString();
                ObjectMapper mapper = new ObjectMapper();
                Staff staff = mapper.readValue(requestJSON,Staff.class);
                StaffDAO.insert(staff);
                writer.println(new JSONObject(staff));
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
            if(params.getString("role").equals("DISPATCHER")|| params.getString("role").equals("ADMIN")){
                String requestJSON = r.toString();
                System.out.println("TeamServlet.java: " + requestJSON);
                ObjectMapper mapper = new ObjectMapper();
                Staff staff = mapper.readValue(requestJSON,Staff.class);
                StaffDAO.update(staff);
                writer.println(new JSONObject(staff));
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
            if(m.get(0)!=null && params.getString("role").equals("DISPATCHER")|| params.getString("role").equals("ADMIN")){
                int requestID = Integer.parseInt(m.get(0));
                StaffDAO.delete(requestID);
                writer.println("DELETED");
            }else responseError(resp);
        });
    }
}
