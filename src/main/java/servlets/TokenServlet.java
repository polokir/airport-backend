package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import dao.UserDAO;
import derive.ServletAbstract;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import models.User;
import org.json.JSONObject;
import utils.TokenManager;

import java.io.PrintWriter;

@WebServlet({"/refresh"})
public class TokenServlet extends ServletAbstract {

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        PrintWriter out = response.getWriter();

        getMapping("",request,(m, r) -> {
            String headersToken = request.getHeader("Authorization");
            if(headersToken != null && headersToken.startsWith("Bearer ")) {
                String accessToken = headersToken.replace("Bearer ", "");
                System.out.println(TokenManager.getParam(accessToken));
                if (TokenManager.verifyToken(accessToken)) {
                    JSONObject roleObj = TokenManager.getParam(accessToken);
                    System.out.println(roleObj.toString());
                    out.println(roleObj.get("role"));
                }
            }else{
                out.println(HttpServletResponse.SC_UNAUTHORIZED);
            }

        });
    }


    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        PrintWriter out = response.getWriter();
        JsonObject message = new JsonObject();
        getMapping("", request, (m, r) -> {
            String headersToken = request.getHeader("Authorization");
            if(headersToken != null && headersToken.startsWith("Bearer ")){
                String refreshToken = headersToken.replace("Bearer ","");
                if(TokenManager.verifyToken(refreshToken)) {
                    int idToken = Integer.parseInt((String)TokenManager.getParam(refreshToken).get("id"));
                    String roleToken =(String)TokenManager.getParam(refreshToken).get("role");
                    String newAccessToken = TokenManager.generateAccessToken(idToken,roleToken);
                    String newRefreshToken = TokenManager.generateRefreshToken(idToken,roleToken);
                    JSONObject newTokenObj = new JSONObject();
                    newTokenObj.put("AccessToken",newAccessToken);
                    newTokenObj.put("RefreshToken",newRefreshToken);
                    out.println(newTokenObj);
                }else{
                    out.println(m);
                }

            }
        });
    }

}
