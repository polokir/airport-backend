package dbconnection;




import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionPool {
    private static ConnectionPool cp = new ConnectionPool();

    private final String URL = "jdbc:postgresql://localhost:5432/airport";
    private final String user = "postgres";
    private final String password = "1";
    private final int MAX_CONNECT = 4;

    private BlockingQueue<Connection> connections;

    private ConnectionPool(){
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found");
            e.printStackTrace();
        }

        connections = new LinkedBlockingQueue<Connection>(MAX_CONNECT);

        try {
            for(int i = 0; i < MAX_CONNECT; ++i) {
                connections.put(DriverManager.getConnection(URL,user,password));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static ConnectionPool getConnectionPool() {
        return cp;
    }

    public Connection getConnection() throws InterruptedException, SQLException {
        Connection c = connections.take();
        if (c.isClosed()) {
            c = DriverManager.getConnection(URL,user,password);
        }
        return c;
    }

    public void releaseConnection(Connection c) throws InterruptedException {
        connections.put(c);
    }
}
