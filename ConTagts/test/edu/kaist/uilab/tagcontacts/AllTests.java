package edu.kaist.uilab.tagcontacts;

import junit.framework.Test;
import junit.framework.TestSuite;
import android.test.suitebuilder.TestSuiteBuilder;

/**
 * A test suite containing all tests for the app.
 */
public class AllTests extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(
        		AllTests.class).includeAllPackagesUnderHere().build();
    }
}
