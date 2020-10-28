/*
 * MIT License
 *
 * Copyright (c) 2020 Roman Srom
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cz.raptor22fa.dbchecko.db;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.hsqldb.cmdline.SqlFile;
import org.hsqldb.cmdline.SqlToolError;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * @author Roman Srom
 */
public class DbCheckoTest {

    private static final Logger LOG = Logger.getLogger(DbCheckoTest.class);

    private static DbConnection dbConnection;

    @BeforeClass
    public static void beforeClass() throws Exception {
        dbConnection = createDbConnection();
        Class.forName(dbConnection.getDriverClassName());
    }

    private static DbConnection createDbConnection() {
        Properties properties = new Properties();
        properties.setProperty(DbConnection.URL_PROPERTY, "jdbc:hsqldb:mem:users");
        properties.setProperty(DbConnection.USER_PROPERTY, "sa");
        properties.setProperty(DbConnection.PASSWORD_PROPERTY, "");
        properties.setProperty(DbConnection.DRIVER_CLASS_NAME_PROPERTY, "org.hsqldb.jdbc.JDBCDriver");
        return DbConnection.fromProperties(properties);
    }

    @Before
    public void setUp() throws Exception {
        try (Connection connection = getConnection()) {
            List<String> sqlScripts = Arrays.asList("/sql/postgresql-compatibility.sql", "/sql/init-db.sql");
            sqlScripts.forEach(sqlScript -> executeScript(sqlScript, connection));
        }
    }

    @After
    public void tearDown() throws Exception {
        try (Connection connection = getConnection()) {
            executeScript("/sql/clean-db.sql", connection);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                dbConnection.getUrl(), dbConnection.getUser(), dbConnection.getPassword());
    }

    private void executeScript(String sqlScript, Connection connection) {
        try(InputStream inputStream = getClass().getResourceAsStream(sqlScript)) {
            SqlFile sqlFile = new SqlFile(
                    new InputStreamReader(inputStream),sqlScript, System.out, "UTF-8", false, new File("."));
            sqlFile.setConnection(connection);
            sqlFile.execute();
        } catch (IOException | SqlToolError | SQLException e) {
            LogMF.error(LOG, "Error running script: {0}", new Object[] {sqlScript});
        }
    }

    @Test
    public void check_existingDb() {
        // Given
        DbChecko dbChecko = new DbChecko(dbConnection);

        // When
        final boolean connected = dbChecko.check();

        // Then
        assertThat(connected).isTrue();
    }

    @Test
    public void check_nonExistingDb() {
        // Given
        Properties properties = new Properties();
        properties.setProperty(DbConnection.URL_PROPERTY, "jdbc:postgresql://non-existing-hostname:5432/liferay");
        properties.setProperty(DbConnection.USER_PROPERTY, "sa");
        properties.setProperty(DbConnection.PASSWORD_PROPERTY, "");
        properties.setProperty(DbConnection.DRIVER_CLASS_NAME_PROPERTY, "org.postgresql.Driver");
        DbChecko dbChecko = new DbChecko(DbConnection.fromProperties(properties));

        // When
        final boolean connected = dbChecko.check();

        // Then
        assertThat(connected).isFalse();
    }

    @Test
    public void executeSelect() {
        // Given
        DbChecko dbChecko = new DbChecko(dbConnection);
        ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();

        // When
        try (PrintStream output = new PrintStream(testOutputStream)) {
            dbChecko.executeSelect("SELECT id, name FROM user_", output);

            // Then
            final String outputString = testOutputStream.toString();
            assertThat(outputString).containsSubsequence(
                    "ID         || NAME      ",
                    "================================================================================",
                    "1          || Raptor    ",
                    "2          || Peter     ",
                    "3          || John      "
            );
        }
    }

    @Test
    public void executeUpdate() throws SQLException {
        // Given
        DbChecko dbChecko = new DbChecko(dbConnection);

        // When
        dbChecko.executeUpdate("UPDATE user_ SET name='SuperRaptor' WHERE id=1");

        // Then
        final String expName = "SuperRaptor";
        try (Connection connection = getConnection();
                Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT name FROM user_ WHERE id=1")) {
                if (resultSet.next()) {
                    final String name = resultSet.getString("name");
                    assertThat(name).isEqualTo(expName);
                }
            }
        }
    }
}
