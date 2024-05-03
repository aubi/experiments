package ee.omnifish.glassfish.vt;

import static java.lang.System.Logger.Level.WARNING;
import static java.nio.file.Path.of;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;
import org.glassfish.embeddable.archive.ScatteredArchive;

public class EmbeddedGlassfishApp {

    public static void main(String[] args) throws GlassFishException, IOException {
        new EmbeddedGlassfishApp().run();
    }

    public void run() throws GlassFishException, IOException {
        final GlassFishProperties gfProperties = new GlassFishProperties(copyOfSystemProperties());
        loadPropertiesFromFile(gfProperties);
        setPortFromSimplifiedPropertyOrDefault(gfProperties, "http.port", 8080);

        GlassFish glassfish = GlassFishRuntime.bootstrap().newGlassFish(gfProperties);
        glassfish.start();

        ScatteredArchive archive = new ScatteredArchive("simpleapp", ScatteredArchive.Type.WAR);
        archive.addCurrentClassPath();

        final Deployer deployer = glassfish.getDeployer();
        final String appName = deployer.deploy(archive.toURI(), "--contextroot=/");
        cleanUpAtEnd(deployer, appName, glassfish);
        System.out.println("Application deployed!");
    }

    private void loadPropertiesFromFile(final GlassFishProperties gfProperties) {
        final Path gfPropertiesPath = of("gf.properties");
        try (Reader inProperties = Files.newBufferedReader(gfPropertiesPath)) {
            gfProperties.getProperties().load(inProperties);
//        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            System.getLogger(this.getClass().getName()).log(WARNING, "Cannot open file \"" + gfPropertiesPath.toAbsolutePath() + "\". Reason: " + e.getMessage());
        }
    }

    private static void setPortFromSimplifiedPropertyOrDefault(final GlassFishProperties gfProperties, String simplifiedPropertyname, int defaultPort) {
        if (-1 == gfProperties.getPort("http-listener")) {
            gfProperties.setPort("http-listener", Optional.ofNullable(gfProperties.getProperties().getProperty(simplifiedPropertyname))
                    .map(EmbeddedGlassfishApp::stringToIntOrNull)
                    .filter(Objects::nonNull)
                    .orElse(defaultPort));
        }
    }

    private static Integer stringToIntOrNull(String s) {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Properties copyOfSystemProperties() {
        return new Properties(System.getProperties());
    }

    private static void cleanUpAtEnd(final Deployer deployer, final String appName, GlassFish glassfish) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public synchronized void start() {
                try {
                    deployer.undeploy(appName);
                    glassfish.stop();
                    glassfish.dispose();
                } catch (GlassFishException ex) {
                }
            }

        });
    }
}
