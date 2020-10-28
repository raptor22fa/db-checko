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

import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import cz.raptor22fa.dbchecko.exception.DbCheckoException;
import cz.raptor22fa.dbchecko.exception.MissingParameterDbCheckoException;
import cz.raptor22fa.dbchecko.sql.SelectHandler;
import cz.raptor22fa.dbchecko.sql.UpdateHandler;
import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;

/**
 * Does the business logic of the application. It connects to the database and executes sql commands.
 *
 * @author Roman Srom
 */
public class DbChecko {

    private static final Logger LOG = Logger.getLogger(DbChecko.class);

    private DbConnection dbConnection;

    public DbChecko(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public static DbChecko getInstance(File propertiesFile, File contextFile) {
        if (propertiesFile != null) {
            return new DbChecko(DbConnection.fromPropertiesFile(propertiesFile));
        } else if (contextFile != null) {
            return new DbChecko(DbConnection.fromContextFile(contextFile));
        } else {
            throw new MissingParameterDbCheckoException("Missing properties file or context file");
        }
    }

    private Connection getConnection() throws SQLException {
        LogMF.info(LOG, "Getting connection to {0}", dbConnection.getUrl());

        try {
            Class.forName(dbConnection.getDriverClassName());
        } catch (ClassNotFoundException e) {
            LogMF.error(LOG, e, "Where is your JDBC Driver {0}?", new Object[] {dbConnection.getDriverClassName()});
            throw new DbCheckoException("JDBC Driver not found", e);
        }

        LOG.info("JDBC Driver Registered!");

        Connection connection =
                DriverManager.getConnection(dbConnection.getUrl(), dbConnection.getUser(), dbConnection.getPassword());
        return connection;
    }

    public boolean check() {
        try (Connection connection = getConnection()) {
            LogMF.info(LOG, "Connected to {0}!", dbConnection.getUrl());
            return true;
        } catch (SQLException e) {
            LogMF.warn(LOG, "Failed to make connection to {0}!", dbConnection.getUrl());
            return false;
        }
    }

    public void executeSelect(String query) {
        executeSelect(query, System.out);
    }

    public void executeSelect(String query, PrintStream output) {
        try (Connection connection = getConnection()) {
            SelectHandler selectHandler = new SelectHandler(connection, query, output);
            selectHandler.execute();
        } catch (SQLException e) {
            LogMF.warn(LOG, "Failed to make connection to {0}!", dbConnection.getUrl());
        }
    }

    public void executeUpdate(String query) {
        try (Connection connection = getConnection()) {
            UpdateHandler updateHandler = new UpdateHandler(connection, query);
            updateHandler.execute();
        } catch (SQLException e) {
            LogMF.warn(LOG, "Failed to make connection to {0}!", dbConnection.getUrl());
        }
    }
}
