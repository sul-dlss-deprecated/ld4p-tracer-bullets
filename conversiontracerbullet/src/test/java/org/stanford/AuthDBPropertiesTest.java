package org.stanford;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Properties;

import static java.nio.file.Files.createTempDirectory;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


//@RunWith(PowerMockRunner.class)
//@PrepareForTest(AuthDBProperties.class)
//@PrepareForTest({AuthDBProperties.class, Class.class})
public class AuthDBPropertiesTest {

    /*
    Unit test config:      src/test/resources/server.conf
    Packaged code config:  src/main/resources/server.conf
     */

    private AuthDBProperties authProps;
    private Logger log = LogManager.getLogger(AuthDBProperties.class.getName());
    private Properties serverConf;
//    private String propertyFile;
//    private String propertyResource;
    private String serverConfResourceName = "/server.conf";
    private File serverConfFile;
    private File tmpDir;

    @Before
    public void setUp() throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        tmpDir = createTempDirectory("AuthDBPropertiesTest_").toFile();
        authProps = new AuthDBProperties();

//        // Use private method to get the server.conf file path
//
//        Field f;
//        f = AuthDBProperties.class.getDeclaredField("propertyResourceFile");
//        f.setAccessible(true);
//        propertyFile = (String) f.get(authProps);
//        serverConfFile = new File(propertyFile);
//
//        f = AuthDBProperties.class.getDeclaredField("PROPERTY_RESOURCE");
//        f.setAccessible(true);
//        propertyResource = (String) f.get(authProps);
//
        Method m = AuthDBProperties.class.getDeclaredMethod("loadPropertyResource");
        m.setAccessible(true);
        serverConf = (Properties) m.invoke(authProps);
    }

    public void setServerConfFile() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Use private method to get the server.conf properties file
        Method m = AuthDBProperties.class.getDeclaredMethod("propertyResourceFile");
        m.setAccessible(true);
        String serverConfFileName = (String) m.invoke(authProps);
        serverConfFile = new File(serverConfFileName);
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(tmpDir);
    }

    @Test
    public void testConstructor() {
        assertEquals(serverConf.getProperty("USER"), authProps.getUserName());
        assertEquals(serverConf.getProperty("PASS"), authProps.getUserPass());
        assertEquals(serverConf.getProperty("SERVER"), authProps.getServer());
        assertEquals(serverConf.getProperty("SERVICE_NAME"), authProps.getService());
    }

    @Test
    public void testConstructorWithString() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        setServerConfFile();
        FileUtils.copyFileToDirectory(serverConfFile, tmpDir);
        String customConfigFile = Paths.get(tmpDir.toString(), serverConfFile.getName()).toString();
        AuthDBProperties customProps = new AuthDBProperties(customConfigFile);
        assertEquals(serverConf.getProperty("USER"), customProps.getUserName());
        assertEquals(serverConf.getProperty("PASS"), customProps.getUserPass());
        assertEquals(serverConf.getProperty("SERVER"), customProps.getServer());
        assertEquals(serverConf.getProperty("SERVICE_NAME"), customProps.getService());
    }

    @Test
    public void testLoadDataSourceProperties() {
        assertNotNull(serverConf.getProperty("USER"));
        assertNotNull(serverConf.getProperty("PASS"));
        assertNotNull(serverConf.getProperty("SERVER"));
        assertNotNull(serverConf.getProperty("SERVICE_NAME"));
        assertNull(serverConf.getProperty("MISSING_PROPERTY"));
    }

    @Test
    public void testServer() throws Exception {
        // Same code is used to test setter/getter
        String server = "test.server.org";
        authProps.setServer(server);
        assertEquals(server, authProps.getServer());
    }

    @Test
    public void testService() throws Exception {
        // Same code is used to test setter/getter
        String service = "service_name";
        authProps.setService(service);
        assertEquals(service, authProps.getService());
    }

    @Test
    public void testUserName() throws Exception {
        // Same code is used to test setter/getter
        String userName = "user_name";
        authProps.setUserName(userName);
        assertEquals(userName, authProps.getUserName());
    }

    @Test
    public void testUserPass() throws Exception {
        // Same code is used to test setter/getter
        String userPass = "user_pass";
        authProps.setUserPass(userPass);
        assertEquals(userPass, authProps.getUserPass());
    }

    @Test
    public void testURL() throws Exception {
        String url = authProps.getURL();
        assertNotNull(url);
        assertThat(url, containsString("jdbc:oracle:thin:@"));
        assertThat(url, containsString(":1521:"));
        assertThat(url, containsString(authProps.getServer()));
        assertThat(url, containsString(authProps.getService()));
    }

    @Test (expected = FileNotFoundException.class)
    public void testFailureLoadingProperties() throws IOException {
        String customConfigFile = Paths.get(tmpDir.toString(), "missing.properties").toString();
        new AuthDBProperties(customConfigFile);
    }

    @Ignore("This is not working as expected")
    @Test
    public void testMissingDefaultProperties() {
        Class authClass = AuthDBProperties.class;
//        Object authClassSpy = spy(authClass);
        String exception = "Cannot find /server.conf";

        PowerMockito.mockStatic(AuthDBProperties.class);
        PowerMockito.mockStatic(Class.class);
        PowerMockito.when(authClass.getResourceAsStream(serverConfResourceName)).thenThrow(new IOException(exception));

        boolean thrown = false;
        try {
            AuthDBProperties props = new AuthDBProperties();
//            verify(log.fatal("Failed to load resource file"));
        } catch (Exception e) {
            thrown = true;
        }
        assertTrue(thrown);
    }
}

