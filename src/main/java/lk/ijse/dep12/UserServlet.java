package lk.ijse.dep12;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lk.ijse.dep12.to.ErrorResponse;
import lk.ijse.dep12.to.User;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Set;

@WebServlet(name = "user-servlet", urlPatterns = "/users/*")
@MultipartConfig(location = "/tmp", maxFileSize = 5 * 1024 * 1024)
public class UserServlet extends HttpServlet {

    @Resource(lookup = "java:comp/env/jdbc/to-do-app-db")
    private DataSource pool;

    private final ObjectMapper mapper = new ObjectMapper();
    //Validator and ValidatorFactory are thread safe
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Override
    public void destroy() {
        //close validator factory when servlet is destroyed
        validatorFactory.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        System.out.printf("name=%s, email=%s, password=%s\n", name, email, password);
        Part picture = req.getPart("picture");

        //Create user object
        User user = new User(null, name, email, password, picture);

        //Validate created user
        //try (ValidatorFactory vf = Validation.buildDefaultValidatorFactory()) {
        // no point of creating a validator factory for each request
        // Validator validator = vf.getValidator();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        if (!constraintViolations.isEmpty()) {
//                //put all constraint violations to a map
//                HashMap<String, String> errors = new HashMap<>();
//                constraintViolations.forEach(violation -> errors.put(violation.getPropertyPath().toString(),
//                        violation.getMessage().toString()));
//
//                //create HashMap object to write into response
//                HashMap<String, Object> errorMap = new HashMap<>();
//                errorMap.put("code", HttpServletResponse.SC_BAD_REQUEST);
//                errorMap.put("status", "Bad Request");
//                errorMap.put("message", "Validation failed");
//                errorMap.put("errors", errors);

            /* To replace above complex code, we can use a custom Error Response Object*/

            resp.setContentType("application/json"); // to get response as a json
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //set status of response
            mapper.writeValue(resp.getWriter(),
                    new ErrorResponse(req.getRequestURI(), 400, "Bad Request",
                            "Validation failed", constraintViolations)); //write the ErrorResponse Object into response object
            return;
        }
        //}

        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("""
                    INSERT INTO "user" (email,password,name,picture) VALUES (?,?,?,?)
                    """, Statement.RETURN_GENERATED_KEYS);

            stm.setString(1, email);
            stm.setString(2, password);
            stm.setString(3, name);
            stm.setBinaryStream(4, picture == null ? null/*we can post without picture*/ : picture.getInputStream());
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            rs.next();
            int newUserId = rs.getInt("id");
            user.setId(newUserId);

            /*set content type and headers before writing content to the body, so that
             * in case if it flushes, headers would not be deleted*/
            resp.setContentType("application/json");
            /* set the status code to created */
            resp.setStatus(HttpServletResponse.SC_CREATED);

            mapper.writeValue(resp.getWriter(), user);

//            resp.getWriter().println("""
//                    {
//                    "id":%s,
//                    "email":"%s",
//                    "name":"%s"
//                    }
//                    """.formatted(newUserId, email, name));
//            resp.getWriter().flush();

        } catch (SQLException e) {
            resp.getWriter().println("<h1>Failed to save user : %s</h1>".formatted(e.getMessage()));

            throw new RuntimeException(e);
        }

//        /*save file in desktop*/
//        picture.write(System.getProperty("user.home") + "/Desktop/" + picture.getSubmittedFileName());

//        resp.getWriter().println("<h1>POST USER</h1>");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("<h1>GET USER</h1>");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("<h1>DELETE USER</h1>");

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("<h1>PATCH USER</h1>");
    }

}
