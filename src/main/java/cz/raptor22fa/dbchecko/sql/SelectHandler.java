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

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import cz.raptor22fa.dbchecko.exception.DbCheckoException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Runs a select sql command and prints output of the command.
 *
 * @author Roman Srom
 */
public class SelectHandler {

    private static final Logger LOG = Logger.getLogger(SelectHandler.class);

    public static final char PAD_CHARACTER = ' ';
    public static final int MIN_COLUMN_NAME_LENGTH = 10;

    private final Connection connection;
    private final String query;
    private final PrintStream output;
    private Integer[] columnNameLengths;

    public SelectHandler(Connection connection, String query, PrintStream output) {
        this.connection = connection;
        this.query = query;
        this.output = output;
    }

    public void execute() {
        try (Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query)) {
            final ResultSetMetaData metaData = rs.getMetaData();

            columnNameLengths = getColumnNamesLength(metaData);

            printHeader(metaData);
            printRows(rs, metaData);
        } catch (SQLException e) {
            throw new DbCheckoException("Select command failed", e);
        }
    }

    private void printRows(ResultSet rs, ResultSetMetaData metaData) throws SQLException {
        final int columnCount = metaData.getColumnCount();
        while (rs.next()) {
            printRow(rs, columnCount);
        }
    }

    private void printRow(ResultSet rs, int columnCount) throws SQLException {
        for (int i = 1; i <= columnCount; i++) {
            final String columnValue = rs.getString(i);
            int columnNameLength = columnNameLengths[i-1];
            final boolean lastColumn = (i == columnCount);
            final String normalizedColumnValue = normalizeColumnValue(columnValue, columnNameLength, !lastColumn);
            output.print(normalizedColumnValue);
            if (!lastColumn) {
                output.print(" || ");
            }
        }
        output.println();
    }

    private String normalizeColumnValue(String columnValue, int columnNameLength, boolean abbreviate) {
        if (columnValue == null) {
            return StringUtils.repeat(PAD_CHARACTER, columnNameLength);
        }

        final int length = columnValue.length();
        if (abbreviate && length > columnNameLength) {
            return StringUtils.abbreviate(columnValue, columnNameLength);
        }
        if (length < columnNameLength) {
            return StringUtils.rightPad(columnValue, columnNameLength, PAD_CHARACTER);
        }
        return columnValue;
    }

    private void printHeader(ResultSetMetaData metaData) throws SQLException {
        final int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            final String columnName = metaData.getColumnName(i);
            output.print(normalizeColumnName(columnName));
            if (i != columnCount) {
                output.print(" || ");
            }
        }
        output.println();
        output.println(StringUtils.repeat('=', 80));
    }

    private String normalizeColumnName(String columnName) {
        final int length = columnName.length();
        if (length < MIN_COLUMN_NAME_LENGTH) {
            return StringUtils.rightPad(columnName, MIN_COLUMN_NAME_LENGTH, PAD_CHARACTER);
        }
        return columnName;
    }

    private Integer[] getColumnNamesLength(ResultSetMetaData metaData) throws SQLException {
        final int columnCount = metaData.getColumnCount();
        Integer[] columnNameLengths = new Integer[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            final int columnNameLength = metaData.getColumnName(i).length();
            columnNameLengths[i-1] = Math.max(columnNameLength, MIN_COLUMN_NAME_LENGTH);
        }
        return columnNameLengths;
    }
}
