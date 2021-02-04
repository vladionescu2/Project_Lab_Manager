package project.lab_management_syst;

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
    @Override
    protected  void doGet(HttpServletRequest request,
                          HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/WEB-INF/servlet_test.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response) throws IOException{
        System.out.println("Called with method" + request.getMethod());

        String line;
        try(BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null)
                System.out.println(line);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.append("Received the request\n");
    }
}
