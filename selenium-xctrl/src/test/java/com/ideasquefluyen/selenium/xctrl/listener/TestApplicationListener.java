package com.ideasquefluyen.selenium.xctrl.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.ideasquefluyen.selenium.xctrl.database.ApplicationRepository;
import com.ideasquefluyen.selenium.xctrl.database.ApplicationRepositorySQLImpl;
import com.ideasquefluyen.selenium.xctrl.database.SQLServerDatasourceProvider;
import com.ideasquefluyen.selenium.xctrl.testng.CachedModuleFactory;
import com.ideasquefluyen.selenium.xctrl.testng.listener.CurrentSuiteContext;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

/**
 * @author dmarafetti
 *
 */
public class TestApplicationListener implements ITestListener {

    private static final String APPLICATION_ID_FILE = "Application.log";

    @Inject
    protected ApplicationRepository applicationRepository;

    @Inject @Named("selenium.proofsDir")
    protected String proofsDir;


    /**
     * Class constructor. Must inject member because of
     * <a href="https://github.com/cbeust/testng/issues/279"> https://github.com
     * /cbeust/testng/issues/279</a>
     */
    public TestApplicationListener() {}


    /**
     * Has the application token setted?
     *
     * @return
     */
    private boolean hasApplicationToken() {

        return CurrentSuiteContext.hasGlobalParameter("applicationToken");
    }



    /* (non-Javadoc)
     * @see org.testng.ITestListener#onStart(org.testng.ITestContext)
     */
    public void onStart(ITestContext context) {

        final String connectionString = context.getCurrentXmlTest().getParameter("connectionString");
        final Injector injector = CachedModuleFactory.getInjector();

        injector.createChildInjector(new Module() {

            public void configure(Binder binder) {

                binder.bind(String.class).annotatedWith(Names.named("sql.cs")).toInstance(connectionString);
                binder.bind(DataSource.class).toProvider(SQLServerDatasourceProvider.class).in(Scopes.SINGLETON);
                binder.bind(ApplicationRepository.class).to(ApplicationRepositorySQLImpl.class);
            }

        }).injectMembers(this);
    }



    /* (non-Javadoc)
     * @see org.testng.ITestListener#onFinish(org.testng.ITestContext)
     */
    public void onFinish(ITestContext context) {

        if(hasApplicationToken()) {

            String token = (String) CurrentSuiteContext.getGlobalParameter("applicationToken");

            List<String> list = applicationRepository.getApplicationIdByToken(token);

            try {

                FileWriter writer = new FileWriter(new File(this.proofsDir, APPLICATION_ID_FILE));

                list.forEach((e) -> {

                    try {

                        writer.write(e);

                    } catch (IOException ex) {}

                });

                writer.flush();
                writer.close();

            } catch (Exception e) {

                throw new RuntimeException("File with application Ids could not be written", e);
            }
        }
    }


    /* (non-Javadoc)
     * @see org.testng.ITestListener#onTestStart(org.testng.ITestResult)
     */
    public void onTestStart(ITestResult result) {}

    /* (non-Javadoc)
     * @see org.testng.ITestListener#onTestSuccess(org.testng.ITestResult)
     */
    public void onTestSuccess(ITestResult result) {}

    /* (non-Javadoc)
     * @see org.testng.ITestListener#onTestFailure(org.testng.ITestResult)
     */
    public void onTestFailure(ITestResult result) {}

    /* (non-Javadoc)
     * @see org.testng.ITestListener#onTestSkipped(org.testng.ITestResult)
     */
    public void onTestSkipped(ITestResult result) {}

    /* (non-Javadoc)
     * @see org.testng.ITestListener#onTestFailedButWithinSuccessPercentage(org.testng.ITestResult)
     */
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}
}
