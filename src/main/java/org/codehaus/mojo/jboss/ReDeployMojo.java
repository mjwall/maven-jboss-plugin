package org.codehaus.mojo.jboss;

import org.apache.maven.plugin.MojoExecutionException;

/**
 *  Deploys a directory or file to JBoss via JMX
 *
 * @author <a href="mailto:jgenender@apache.org">Jeff Genender</a>
 * @goal redeploy
 * @description Maven 2 JBoss plugin
 */
public class ReDeployMojo extends AbstractDeployerMojo {

    /**
     * The redeployment URL
     *
     * @parameter expression="/jmx-console/HtmlAdaptor?action=invokeOpByName&name=jboss.system:service%3DMainDeployer&methodName=redeploy&argType=java.net.URL&arg0="
     * @required
     */
    protected String redeployUrlPath;

    public void execute() throws MojoExecutionException {

        //Fix the ejb packaging to a jar
        String fixedFile = null;
        if (fileName.toLowerCase().endsWith("ejb")){
            fixedFile = fileName.substring(0, fileName.length() - 3) + "jar";
        } else {
            fixedFile = fileName;
        }

        getLog().info("Reploying " + fixedFile + " to JBoss.");
        String url = "http://" + hostName + ":" + port + redeployUrlPath + fixedFile;
        doURL(url);
    }
}
