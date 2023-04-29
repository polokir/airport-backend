package servlets;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.AirportDAO;
import dao.UserDAO;
import derive.ServletAbstract;

import derive.UserBase;
import lombok.SneakyThrows;
import models.Airport;
import models.User;
import org.json.JSONObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.TokenManager;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;




@WebServlet({"/users", "/users/*"})
public class UserServlet extends ServletAbstract {

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        PrintWriter out = resp.getWriter();


        getMapping("", req, (m, r) -> { // /users
            List<User> users = UserDAO.getAllUsers();
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(users);
            out.println(json);
        });

        getMapping("/(\\d+)", req, (m, r) -> { // /users/{id}
            User user = UserDAO.getUserById(Integer.valueOf(m.get(0)));
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(user);
            out.println(json);
        });


        responseError(resp);
    }

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {

        getMapping("/register", req, (m, r) -> {
            //users/register
            PrintWriter out =  resp.getWriter();
            String json = r.toString();
            ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(json, User.class);
            System.out.println(user.getEmail());
            user.setRole("NEW_USER");
            UserDAO.addUser(user);
            String accessToken = TokenManager.generateAccessToken(user.getId(), user.getRole());
            String refreshToken = TokenManager.generateRefreshToken(user.getId(), user.getRole());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("accessToken",accessToken);
            jsonObject.put("refreshToken",refreshToken);
            out.println(jsonObject);
        });

        getMapping("/login",req,(m,r) ->{
            PrintWriter out =  resp.getWriter();
            String json =r.toString();
            System.out.println(json);
            ObjectMapper objectMapper = new ObjectMapper();
            UserBase userBase = objectMapper.readValue(json,UserBase.class);
            User userInDB = UserDAO.getUserByEmail(userBase.getEmail());
            if(userInDB == null){
                out.println("User not found please register");
            }else{
                String reqPassword = userBase.getPassword();
                String dbPassword = userInDB.getPassword();
                if(reqPassword.equals(dbPassword)){
                    String tokenAccess = TokenManager.generateAccessToken(userInDB.getId(), userInDB.getRole());
                    String tokenRefresh = TokenManager.generateRefreshToken(userInDB.getId(), userInDB.getRole());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("accessToken",tokenAccess);
                    jsonObject.put("refreshToken",tokenRefresh);
                    out.println(jsonObject);
                }else{
                    resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                }
            }


        });

        responseError(resp);
    }

    @SneakyThrows
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp){
        PrintWriter writer = resp.getWriter();
        getMapping("",req,(m, r) -> {
            JSONObject params = TokenManager.Authorization(req);
            if(params==null){
                writer.println("Authorization ERROR");
            }

            if(params.getString("role").equals("ADMIN")){
                String requestJSON = r.toString();
                System.out.println(requestJSON);
                ObjectMapper mapper = new ObjectMapper();
                User user = mapper.readValue(requestJSON,User.class);
                UserDAO.update(user);
                writer.println(new JSONObject(user));
            }
            else responseError(resp);

        });
    }

    @Override
    @SneakyThrows
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp){
        getMapping("/(\\d+)",req,(m, r) -> {
            System.out.println("UserServlet.java: index -> "+m.get(0));
            PrintWriter writer = resp.getWriter();
            JSONObject params = TokenManager.Authorization(req);
            if(params==null){
                writer.println("Authorization ERROR");
            }

            if(m.get(0)!=null && params.getString("role").equals("ADMIN")){
                int requestID = Integer.parseInt(m.get(0));
                System.out.println("deleted "+requestID);
                UserDAO.delete(requestID);
                writer.println("DELETED");
            }
            else responseError(resp);

        });
    }


}
