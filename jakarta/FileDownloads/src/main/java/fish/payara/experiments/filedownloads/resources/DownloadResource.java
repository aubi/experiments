package fish.payara.experiments.filedownloads.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;

/**
 * Sample JAX-RS for file download
 *
 * @author Petr Aubrecht <aubrecht@asoftware.cz>
 */
@Path("download")
public class DownloadResource {
    @GET
    @Produces("image/png")
    public Response download() {
        InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("payara-logo.png");
        return Response.ok(inStream, "image/png")
                .header("Content-Disposition", "attachment; filename=\"payara-logo.png\"") // recommended, to specify the filename
                .build();
    }
}
