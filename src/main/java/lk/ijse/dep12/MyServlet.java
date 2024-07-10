package lk.ijse.dep12;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "my-servlet", urlPatterns = "/hello", loadOnStartup = 0)
public class MyServlet extends HttpServlet {

    /* Resource Injection*/
    @Resource(lookup = "java:comp/env/jdbc/to-do-app-db")
//    @Resource(lookup = "jdbc/to-do-app-db")
    private DataSource dataSource;

    public MyServlet() {
        System.out.println("Constructor: Data Source :" + dataSource);
    }

    @Override
    public void init() throws ServletException {
//        try {
//            InitialContext context=new InitialContext();
//            dataSource = (DataSource) context.lookup("java:comp/env/jdbc/to-do-app-db");
//        } catch (Exception e) {
//            System.out.println("init: Error in DataSource :" + e);
//        }
        System.out.println("init: Data Source :" + dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Connection connection = dataSource.getConnection();
            resp.getWriter().printf("<h1>New Connection: %s</h1> %n", connection);
            connection.close();/*releasing connection*/
        } catch (SQLException e) {
            resp.getWriter().printf("<h1>Failed to obtain a connection, Reason: %s</h1>", e.getMessage());
            e.printStackTrace();
        }
    }
}
