/**
 *
 */
package com.ideasquefluyen.selenium.xctrl.database;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import com.google.inject.Provider;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;




/**
 * SQL Server datasource provider
 *
 * @author dmarafetti
 * @since 1.0.1
 *
 */
public class SQLServerDatasourceProvider implements Provider<DataSource> {

    private final String url;


    /**
     * Class constructor
     */
    @Inject
    public SQLServerDatasourceProvider(@Named("sql.cs") String cs) {

        this.url = cs;
    }



    /* (non-Javadoc)
     * @see com.google.inject.Provider#get()
     */
    public DataSource get() {

        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setURL(this.url);
        return ds;
    }

}
