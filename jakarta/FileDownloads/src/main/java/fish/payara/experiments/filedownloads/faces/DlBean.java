package fish.payara.experiments.filedownloads.faces;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Petr Aubrecht <aubrecht@asoftware.cz>
 */
@Named(value = "dlBean")
@RequestScoped
public class DlBean {
    public void downloadFile() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        try (InputStream inStream = cl.getResourceAsStream("payara-logo.png"); OutputStream out = response.getOutputStream()) {

            response.setHeader("Content-Disposition",
                    "attachment; filename=\"payara-logo.png\"");
            inStream.transferTo(out);
            facesContext.responseComplete();
        } catch (IOException e) {
            // FIXME: Handle properly
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error loading logo.");
        }
    }
    
    public StreamedContent getFacesFile() {
        InputStream inStream = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("payara-logo.png");
        return DefaultStreamedContent.builder()
                .contentType("image/png")
                .name("payara-logo.png")
                .stream(() -> inStream)
                .build();
    }

    public void pageRefresh() throws IOException {
        System.out.println("doing some extra work");
        downloadFile();
    }
}
