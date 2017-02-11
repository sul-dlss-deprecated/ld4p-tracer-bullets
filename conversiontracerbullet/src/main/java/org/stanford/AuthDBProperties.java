package org.stanford;

import oracle.jdbc.pool.OracleDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Stanford University Libraries, DLSS
 */
class AuthDBProperties {

    private static Logger log = LogManager.getLogger(AuthDBProperties.class.getName());

    private static final String PROPERTY_RESOURCE = "/server.conf";

    private String server = null;
    private String service = null;
    private String userName = null;
    private String userPass = null;
    private Properties properties = null;

    public AuthDBProperties() throws IOException {
        // initialize using the default property file resource
        properties = loadPropertyResource();
        initDataSourceProperties();
    }

    public AuthDBProperties(String propertyFile) throws IOException {
        properties = loadPropertyFile(propertyFile);
        initDataSourceProperties();
    }

    public String getURL() {
        return "jdbc:oracle:thin:@" + this.server + ":1521:" + this.service;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    private void initDataSourceProperties() {
        this.server = properties.getProperty("SERVER");
        this.service = properties.getProperty("SERVICE_NAME");
        this.userName = properties.getProperty("USER");
        this.userPass = properties.getProperty("PASS");
    }

    private Properties loadPropertyFile(String propertyFile) throws IOException {
        try {
            FileInputStream iStream = new FileInputStream(propertyFile);
            Properties props = new Properties();
            props.load(iStream);
            iStream.close();
            log.debug( props.toString() );
            return props;
        } catch (IOException e) {
            log.fatal("Failed to initialize AuthDBProperties", e);
            throw e;
        }
    }

    private Properties loadPropertyResource() {
        Class cls = AuthDBConnection.class;
        InputStream in = null;
        Properties props = new Properties();
        try {
            in = cls.getResourceAsStream(PROPERTY_RESOURCE);
            props.load(in);
            log.debug( props.toString() );
        } catch (IOException e) {
            log.fatal("Failed to load resource file", e);
            propertyResourceFile();
        } finally {
            try {
                if( in != null )
                    in.close();
            } catch (IOException e) {
                log.fatal("Failed to close resource file stream", e);
            }
        }
        return props;
    }

    private String propertyResourceFile() {
        Class cls = AuthDBProperties.class;
        URL path = cls.getResource(PROPERTY_RESOURCE);
        if (path == null) {
            log.debug("Failed to cls.getResource()");
            path = cls.getClassLoader().getResource(PROPERTY_RESOURCE);
            if (path == null)
                log.debug("Failed to cls.getClassLoader().getResource()");
        }
        if (path != null) {
            log.debug(path.getFile());
            return path.getFile();
        } else {
            log.fatal("Failed to find default server.conf file resource");
            return null;
        }
    }

}
