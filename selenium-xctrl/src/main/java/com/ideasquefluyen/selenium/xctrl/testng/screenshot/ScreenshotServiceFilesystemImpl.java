package com.ideasquefluyen.selenium.xctrl.testng.screenshot;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.ideasquefluyen.selenium.xctrl.selenium.WebdriverLocal;
import com.ideasquefluyen.selenium.xctrl.testng.listener.CurrentTestContext;
import com.ideasquefluyen.selenium.xctrl.utils.StreamUtils;

/**
 * Filesystem implementation of {@link ScreenshotService}
 *
 * @author dmarafetti
 * @since 1.0.0
 *
 */
public class ScreenshotServiceFilesystemImpl implements ScreenshotService {

    private static final Logger LOGGER = Logger.getLogger(ScreenshotServiceFilesystemImpl.class.getName());


    /**
     * Default storage directory
     *
     */
    private File contentDir;


    /**
     * Class constructor
     *
     * @param storeDir
     */
    @Inject
    public ScreenshotServiceFilesystemImpl(@Named("selenium.proofsDir") String storeDir) {

        LOGGER.config("Screenshots path: " + storeDir);

        contentDir = new File(storeDir);

        if(!contentDir.exists()) {

            contentDir.mkdir();
        }

    }

    /**
     * Take a snapshot
     *
     */
    public void snapshot() {

        LOGGER.config("Taking a snapshot!");

        CurrentTestContext.runOn(c -> {

            TakesScreenshot driver = (TakesScreenshot) WebdriverLocal.getDriver();

            try {

                String timestamp  = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
                String testName   = c.getTestClass().getXmlTest().getName();
                String methodName = c.getMethodName();

                // build name
                StringBuilder imageName = new StringBuilder(methodName);
                imageName.append("-").append(timestamp);
                imageName.append(".png");

                byte[] rawImage = driver.getScreenshotAs(OutputType.BYTES);

                // save image
                this.save(testName,
                          methodName,
                          imageName.toString(),
                          new ByteArrayInputStream(rawImage));

            } catch (Exception ex) {

                LOGGER.warning("Couldn't generate screenshot.");
            }
        });
    }



    /**
     * Save file in filesystem
     *
     * @param testName
     * @param methodName
     * @param imageName
     * @param fileStream
     */
    private void save(String testName,
                     String methodName,
                     String imageName,
                     InputStream fileStream) {

        try {

            // mkdir for test name
            File testDir = new File(contentDir, testName);

            if(!testDir.exists()) {
                testDir.mkdirs();
            }

            // mkdir for method name
            File methodDir = new File(testDir, methodName);

            if(!methodDir.exists()) {
                methodDir.mkdirs();
            }

            FileOutputStream outputStream = new FileOutputStream(new File(methodDir, imageName));
            StreamUtils.copy(fileStream, outputStream);

        } catch (Exception ex) {

            throw new RuntimeException(ex);
        }
    }

}
