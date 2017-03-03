
/**
 * 
 */
package org.stanford;

import org.junit.Before;
import org.junit.Test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.junit.Assert.assertTrue;

/**
 * @author darren.weber@stanford.edu
 * 
 */
public class Log4jTest {

	Logger log = null;

    @Before
    public void setUp() {
        log = LogManager.getLogger(Log4jTest.class.getName());
    }

	/**
	 * test messages to log4j.
	 */
	@Test
	public final void logTest() {
		log.trace("Trace message.");
		log.debug("Debug message.");
		log.info("Info message.");
		log.warn("Warn message.");
		log.error("Error message.");
		log.fatal("Fatal message.");
		assertTrue(true);
	}

}

