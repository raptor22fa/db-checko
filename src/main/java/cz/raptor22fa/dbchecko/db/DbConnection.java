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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Contains properties of database connection.
 *
 * @author Roman Srom
 */
public class DbConnection {

    private static final Logger LOG = Logger.getLogger(DbConnection.class);

    public static final String URL_PROPERTY = "jdbc.default.url";
    public static final String USER_PROPERTY = "jdbc.default.username";
    public static final String PASSWORD_PROPERTY = "jdbc.default.password";
    public static final String DRIVER_CLASS_NAME_PROPERTY = "jdbc.default.driverClassName";
    public static final String LIFERAY_POOL_RESOURCE_NAME = "jdbc/LiferayPool";

    private String url;
    private String user;
    private String password;
    private String driverClassName;

    public DbConnection(String url, String user, String password, String driverClassName) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.driverClassName = driverClassName;
    }

    /**
     * Creates DbConnection from *.properties file. It has to contain these properties:
     * <ul>
     *     <li>{@code jdbc.default.driverClassName}</li>
     *     <li>{@code jdbc.default.url}</li>
     *     <li>{@code jdbc.default.username}</li>
     *     <li>{@code jdbc.default.password}</li>
     * </ul>
     *
     * @param file properties file
     * @return DbConnection
     */
    public static DbConnection fromPropertiesFile(File file) {
        DbConnection dbConnection = null;
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            Properties properties = new Properties();
            properties.load(reader);

            dbConnection = fromProperties(properties);
        } catch (IOException e) {
            LOG.error("Error reading properties file", e);
        }
        return dbConnection;
    }

    /**
     * Creates DbConnection from java {@link Properties} object. It has to contain these properties:
     * <ul>
     *     <li>{@code jdbc.default.driverClassName}</li>
     *     <li>{@code jdbc.default.url}</li>
     *     <li>{@code jdbc.default.username}</li>
     *     <li>{@code jdbc.default.password}</li>
     * </ul>
     *
     * @param properties Properties object
     * @return DbConnection
     */
    public static DbConnection fromProperties(Properties properties) {
        final String url = properties.getProperty(URL_PROPERTY);
        final String user = properties.getProperty(USER_PROPERTY);
        final String password = properties.getProperty(PASSWORD_PROPERTY);
        final String driverClassName = properties.getProperty(DRIVER_CLASS_NAME_PROPERTY);

        return new DbConnection(url, user, password, driverClassName);
    }

    /**
     * Creates DbConnection from Resource in Tomcat context file. Resource's name has to be {@code jdbc/LiferayPool}.
     *
     * @param file Tomcat context file
     * @return DbConnection
     */
    public static DbConnection fromContextFile(File file) {
        Element liferayPoolElement = getLiferayPoolElement(file);
        final String url = liferayPoolElement.getAttribute("url");
        final String user = liferayPoolElement.getAttribute("username");
        final String password = liferayPoolElement.getAttribute("password");
        final String driverClassName = liferayPoolElement.getAttribute("driverClassName");

        return new DbConnection(url, user, password, driverClassName);
    }

    private static Element getLiferayPoolElement(File file) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            document.getDocumentElement().normalize();

            LogMF.debug(LOG,"Root element {0}", document.getDocumentElement().getNodeName());
            NodeList nodeList = document.getElementsByTagName("Resource");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                LogMF.debug(LOG, "Current Element {0}", node.getNodeName());

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element resourceElement = (Element) node;
                    if (LIFERAY_POOL_RESOURCE_NAME.equals(resourceElement.getAttribute("name"))) {
                        LogMF.debug(LOG, "Returning Element with name {0}", LIFERAY_POOL_RESOURCE_NAME);
                        return resourceElement;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error getting liferayPoolElement", e);
        }
        LOG.error("liferayPoolElement not found");
        throw new IllegalArgumentException("liferayPoolElement not found");
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }
}
