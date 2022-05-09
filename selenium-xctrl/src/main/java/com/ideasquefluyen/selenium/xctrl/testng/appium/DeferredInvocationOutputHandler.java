package com.ideasquefluyen.selenium.xctrl.testng.appium;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.apache.maven.shared.invoker.InvocationOutputHandler;


/**
 * Print stream is created when first line is going to be written.
 *
 * @author dmarafetti
 * @since 1.0.1
 *
 */
public final class DeferredInvocationOutputHandler implements InvocationOutputHandler {

    /**
     * The print stream to write to.
     */
    private PrintStream ps = null;

    /**
     * Directory where log should be written.
     *
     */
    private String outputDir;

    /**
     * A flag whether the print stream should be flushed after each line.
     */
    private boolean alwaysFlush = true;

    /**
     * Class constructor
     *
     * @param outputDir
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public DeferredInvocationOutputHandler(String outputDir) throws FileNotFoundException, UnsupportedEncodingException {

        this.outputDir = outputDir;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.plexus.util.cli.StreamConsumer#consumeLine(java.lang.String)
     */
    public void consumeLine(String line) {

        if(ps == null) {

            try {

                System.out.println("Writting debug file: " + outputDir);

                File file = new File(outputDir);

                if(!file.exists()) {

                    file.createNewFile();
                }

                ps = new PrintStream(new FileOutputStream(file), alwaysFlush, "UTF-8");

            } catch (Exception e) {

                ps = System.out;
            }
        }

        if ( line == null ) {

            ps.println();

        } else {

            ps.println( line );
        }
    }
}
