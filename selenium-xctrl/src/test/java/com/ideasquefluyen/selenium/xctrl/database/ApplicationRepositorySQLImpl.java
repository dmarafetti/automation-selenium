/**
 *
 */
package com.ideasquefluyen.selenium.xctrl.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 *
 *
 * @author dmarafetti
 * @since 1.0.1
 *
 */
public class ApplicationRepositorySQLImpl implements ApplicationRepository {

    @Inject
    protected DataSource datasource;


    /* (non-Javadoc)
     * @see com.ideasquefluyen.selenium.xctrl.testng.database.ApplicationRepository#getApplicationIdByToken(java.lang.String)
     */
    public List<String> getApplicationIdByToken(String token) {

        Connection con = null;
        PreparedStatement pstmt;
        List<String> list = new ArrayList<String>();

        try {

            con = datasource.getConnection();
            con.setAutoCommit(false);

            pstmt = con.prepareStatement(

                        "SELECT ApplicationId " +
                        "FROM trx.Application " +
                        "WHERE ApplicationToken = '" + token + "'"
                    );

            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {

                list.add(resultSet.getString("ApplicationId"));
            }

            con.commit();
            pstmt.close();

        } catch (Exception ex) {

            ex.printStackTrace();

        } finally {

            try {if (con != null) con.close();} catch (Exception ex) {}
        }

        return list;
    }

}
