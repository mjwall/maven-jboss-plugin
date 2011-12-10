package org.codehaus.mojo.jboss;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * HardUnDeployMojoTest
 *
 * @author Michael Wall
 */
public class HardUnDeployMojoTest extends TestCase {
    
    public HardUnDeployMojoTest(String test) {
        super(test);
    }

    private HardUnDeployMojo mojo;

    private String doURLResult;

    public void setUp() throws Exception {
        // This obviously won't work for multiple files--it is only for this test.
        mojo = new HardUnDeployMojo() {
            protected void doURL(String url) throws MojoExecutionException {
                doURLResult = url;
            }
        };
    }

    public void tearDown() throws Exception {
        mojo = null;
        doURLResult = null;
    }

    public void testSkipWorks() throws Exception {
        String testFile = "C:\\Documents and Settings\\maven\\project\\somefile.jar";
        mojo.fileName = new File(testFile);
        mojo.skip = true;
        try {
          mojo.execute();
          assert(true);
        } catch (MojoExecutionException e) {
            fail("Skip set, hard deploy should not do anything.");
        }
    }
}
