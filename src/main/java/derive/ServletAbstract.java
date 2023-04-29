package derive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServletAbstract extends HttpServlet {
    private Boolean callback_colled = false;

    @SneakyThrows
    protected void getMapping(String regex, HttpServletRequest req, Callback callback) throws IOException, SQLException, InterruptedException {
        String pathInfo = Objects.toString(req.getPathInfo(), "");


        if (Pattern.matches(regex, pathInfo)) {

            List<String> m = new ArrayList<String>();
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(pathInfo);

            while(matcher.find())
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    m.add(matcher.group(i));
                }

            BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null)
                sb.append(line);

            String requestBody = sb.toString();

            Gson gson = new Gson();
            JsonObject r = gson.fromJson(requestBody, JsonObject.class);

            this.callback_colled = true;
            callback.onResult(m, r);
        }
    }

    protected void responseError(HttpServletResponse res) throws ServletException, IOException {
        if(!callback_colled)
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    protected interface Callback {
        void onResult(List<String> m, JsonObject r) throws SQLException, InterruptedException, IOException, ServletException;
    }
}
