package project.lab_management_syst;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/receiver")
public class WebhookReceiver extends HttpServlet {
    private static final Logger logger = LogManager.getLogger();

    @Override
    protected  void doGet(HttpServletRequest request,
                          HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/WEB-INF/servlet_test.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response) throws IOException{
//        System.out.println("Called with method" + request.getMethod());

        System.out.println("Log Statement");
        logger.info("Called with method" + request.getMethod());

        String line;
        try(BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null)
                System.out.println(line);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        this.getServletContext().log("Test");

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.append("Received the request\n");
    }
}
