package main;

import java.io.*;
import java.util.Properties;

class PropGet
{
    static Properties getProps() throws NullPointerException, IOException {
        Properties defaultProps = new Properties();
        try 
        {
            FileInputStream in = new FileInputStream("main/resources/server.conf");
            defaultProps.load(in);
            in.close();
        }
        catch (FileNotFoundException e)
        {
            System.err.println(e.getMessage());
        }

        return defaultProps;
    }
}
