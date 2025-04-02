package fish.payara.experiments.filedownloads.servlet;

import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Petr Aubrecht <aubrecht@asoftware.cz>
 */
@WebServlet("/download")
public class DownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream inStream = cl.getResourceAsStream("payara-logo.png"); OutputStream outStream = response.getOutputStream()) {

            if (inStream == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.setContentType("image/png");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"payara-logo.png\"");

            inStream.transferTo(outStream);
        }
    }
}
