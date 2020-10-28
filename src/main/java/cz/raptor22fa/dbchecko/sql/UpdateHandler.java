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
package cz.raptor22fa.dbchecko.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import cz.raptor22fa.dbchecko.exception.DbCheckoException;
import org.apache.log4j.Logger;

/**
 * Runs an update sql command and prints count of updated rows.
 *
 * @author Roman Srom
 */
public class UpdateHandler {

    private static final Logger LOG = Logger.getLogger(UpdateHandler.class);

    private final Connection connection;
    private final String query;

    public UpdateHandler(Connection connection, String query) {
        this.connection = connection;
        this.query = query;
    }

    public void execute() {
        try (Statement statement = connection.createStatement()) {
            final int updatedRowsCount = statement.executeUpdate(query);

            System.out.println("Updated rows count: " + updatedRowsCount);
        } catch (SQLException e) {
            throw new DbCheckoException("Update command failed", e);
        }
    }
}
