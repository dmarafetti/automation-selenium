package com.ideasquefluyen.selenium.xctrl.testng.appium;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.SystemOutHandler;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * Multi test launcher.
 *
 *
 * @author dmarafetti
 * @since 1.0.0
 *
 */
public class AppiumLauncher {


    /**
     * Usage: --properties /xctrl/test.properties
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // Display usage
        if(args.length < 2) {

            System.out.printf("\nUsage:\n--properties /xctrl/test.properties\n");

            return;
        }

        SystemOutHandler handler = new SystemOutHandler();



        Commandline cmd = new Commandline();
        cmd.setExecutable("sh");
        cmd.createArg().setValue("-c");
        cmd.createArg().setValue("ps -fea | pgrep -f 'appium' | awk '{print $1}' | xargs kill -9");

        System.out.println("Killing previous appium instance");
        System.out.println(cmd.toString());
        int ret = CommandLineUtils.executeCommandLine(cmd,
                                     null,
                                     (StreamConsumer) handler,
                                     (StreamConsumer) handler,
                                     0);
        System.out.println("Return code: " + ret);


        Properties props = new Properties();
        props.load(new FileReader(new File(args[1])));
        Integer count = Integer.valueOf(props.getProperty("appium.test.count", "0"));

        IntStream.range(0, count).forEachOrdered(n -> {

            Runnable r = () -> {

                try {

                    final String host   = props.getProperty("appium.test." + n + ".host");
                    final String port   = props.getProperty("appium.test." + n + ".port");
                    final String bPort  = props.getProperty("appium.test." + n + ".bport");
                    final String cdPort = props.getProperty("appium.test." + n + ".cdport");
                    final String device = props.getProperty("appium.test." + n + ".device");
                    final String apk    = props.getProperty("appium.test." + n + ".apk");
                    final String ver    = props.getProperty("appium.test." + n + ".version");
                    final String pack   = props.getProperty("appium.test." + n + ".package");
                    final String suite  = props.getProperty("appium.test." + n + ".suite");
                    final String outputDir = "target_" + n;

                    System.out.printf("Starting thread for device %s\n", device);

                    Properties properties = new Properties();
                    properties.setProperty("selenium.browser", "hybrid");
                    properties.setProperty("selenium.hybridEngine", "Appium");
                    properties.setProperty("selenium.suiteFile", suite);
                    properties.setProperty("selenium.hybridHost", host);
                    properties.setProperty("selenium.hybridPort", port);
                    properties.setProperty("selenium.hybridBootstrapPort", bPort);
                    properties.setProperty("selenium.hybridChromedriverPort", cdPort);
                    properties.setProperty("selenium.hybridDeviceName", device);
                    properties.setProperty("selenium.hybridApk", apk);
                    properties.setProperty("selenium.hybridAndroidVersion", ver);
                    properties.setProperty("selenium.hybridPackage", pack);
                    properties.setProperty("buildDirectory", outputDir);

                    DefaultInvocationRequest request = new DefaultInvocationRequest();
                    request.setPomFile(new File("pom.xml"));
                    request.setGoals(Arrays.asList("-T 4", "clean", "test"));

                    List<String> profiles = new ArrayList<>();
                    profiles.add("otherOutputDir"); // target output per maven instance
                    //profiles.add("android-wakeup-device"); // wake up blocked device
                    profiles.add("appium-server");         // start appium per instance

                    request.setProfiles(profiles);
                    request.setDebug(false);
                    request.setOutputHandler(new DeferredInvocationOutputHandler("debug_" + n + ".log"));
                    request.setProperties(properties);

                    Invoker invoker = new DefaultInvoker();
                    invoker.execute(request);

                } catch (Exception e) {

                    e.printStackTrace();
                }
            };

            try {

                Thread.sleep(1000);
                Thread thread = new Thread(r);
                thread.start();

            } catch (Exception e) {}

        });

    }

}
