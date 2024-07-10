package lk.ijse.dep12;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

@WebServlet(name = "item-servlet", urlPatterns = "/users/me/items/*")
public class ItemServlet extends HttpServlet {

}
