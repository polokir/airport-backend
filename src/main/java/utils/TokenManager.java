package utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

public class TokenManager {
    private static KeyPairGenerator kpg;
    private static final long TIME_ACCESS_TOKEN = 5*60L*1000L;//1 min
    private static final long TIME_REFRESH_TOKEN = 10*24*60*60*1000;

    static {
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static KeyPair keyPair = kpg.generateKeyPair();
    private static Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());

    public static String generateAccessToken(int id, String role){
        return JWT.create().withIssuer("secret111")
                .withClaim("role", role)
                .withClaim("id",id)
                .withExpiresAt(new Date(TIME_ACCESS_TOKEN + System.currentTimeMillis()))
                .sign(algorithm);
    }

    public static String generateRefreshToken(int id, String role){
        return JWT.create().withIssuer("secret111")
                .withClaim("role",role)
                .withClaim("id",id)
                .withExpiresAt(new Date(TIME_REFRESH_TOKEN + System.currentTimeMillis()))
                .sign(algorithm);
    }

    public static boolean verifyToken(String token){
        try{
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public static JSONObject Authorization (HttpServletRequest req){
        String token = req.getHeader("Authorization");
        if(token != null && token.startsWith("Bearer ")){
            String accessToken = token.replace("Bearer ","");
            System.out.println("(TokenManager.java: token "+accessToken);
            JSONObject params = getParam(accessToken);
            return (verifyToken(token)) ? null : params;
        }
        return null;
    }

    @SneakyThrows
    public static JSONObject getParam(String token) {
        JSONObject jsonObject = new JSONObject();
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);
        System.out.println("getParams --> " + jwt.getClaim("id").asInt()+" " + jwt.getClaim("role").asString());
        int id = jwt.getClaim("id").asInt();
        String role = jwt.getClaim("role").asString();
        jsonObject.put("id",id);
        jsonObject.put("role",role);
        return jsonObject;
    }

    @SneakyThrows
    public TokenManager()  {
    }


}
