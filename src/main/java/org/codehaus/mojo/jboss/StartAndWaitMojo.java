package org.codehaus.mojo.jboss;

import java.rmi.RMISecurityManager;
import java.util.Properties;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Starts JBoss and waits until the server is started.
 * 
 * @author <a href="mailto:jc7442@yahoo.fr">J-C</a>
 * @author <a href="mailto:fuzail@fingerprintsoft.org">Fuzail Sarang</a>
 * @goal startAndWait
 * @requiresProject false
 */
public class StartAndWaitMojo
    extends StartMojo
{
    
    /**
     * One second in millis.
     */
    public final static long ONE_SECOND = 1000;
    
    /**
     * Maximum number of retries to JBoss JMX MBean connection.
     * 
     * @parameter default-value="3" expression="${jboss.retry}"
     */
    protected int retry;

    /**
     * Wait in ms before retrying JBoss JMX MBean connection.
     * 
     * @parameter default-value="5000" expression="${jboss.retryWait}"
     */
    protected int retryWait;
        
    /**
     * Timeout in ms to start the application server (once JMX MBean connection has been reached).
     * 
     * @parameter default-value="20000" expression="${jboss.timeout}"
     */
    protected int timeout;

    /**
     * The port for the naming service.
     * 
     * @parameter default-value="1099" expression="${jboss.namingPort}"
     */
    protected String namingPort;

    /**
     * The host JBoss is running on.
     * 
     * @parameter default-value="localhost" expression="${jboss.hostname}"
     */
    protected String hostName;

    public void execute()
        throws MojoExecutionException
    {
        // Start JBoss
        super.execute();

        InitialContext ctx = getInitialContext();
        
        // Try to get JBoss jmx MBean connection
        MBeanServerConnection s = null;
        int i = 0;
        while ( true )
        {
            try
            {
                Thread.sleep( retryWait );
                s = (MBeanServerConnection) ctx.lookup( "jmx/invoker/RMIAdaptor" );
                break;
            }
            catch ( NamingException e )
            {
                i++;
                if ( i > retry )
                {
                    throw new MojoExecutionException( "Unable to get JBoss jmx MBean connection: " + e.getMessage(), e );
                }
                getLog().info( "Retry to retrieve JBoss jmx MBean connection..." );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
        getLog().info( "JBoss JMX MBean connection successful!" );
        // Wait server is started
        boolean isStarted = false;
        long startTime = System.currentTimeMillis();
        while ( !isStarted && System.currentTimeMillis() - startTime < timeout )
        {
            try
            {
                Thread.sleep( ONE_SECOND );
                isStarted = isStarted( s );
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( "Unable to wait: " + e.getMessage(), e );
            }
        }
        if ( !isStarted )
        {
            throw new MojoExecutionException( "JBoss AS is not stared before timeout has expired! " );
        }
        getLog().info( "JBoss server started!" );
    }

    /**
     * Check if the server has finished startup. 
     * 
     * @param s
     * @return
     * @throws Exception
     */
    protected boolean isStarted( MBeanServerConnection s )
        throws Exception
    {
        ObjectName serverMBeanName = new ObjectName( "jboss.system:type=Server" );
        return ( (Boolean) s.getAttribute( serverMBeanName, "Started" ) ).booleanValue();
    }

    /**
     * Set up the context information for connecting the the jboss server.
     * @return
     * @throws MojoExecutionException
     */
    protected InitialContext getInitialContext()
        throws MojoExecutionException
    {
        Properties env = new Properties();
        try
        {
            env.put( "java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory" );
            env.put( "java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces" );
            env.put( "java.naming.provider.url", hostName + ":" + namingPort );
            return new InitialContext( env );
        }
        catch ( NamingException e )
        {
            throw new MojoExecutionException( "Unable to instantiate naming context: " + e.getMessage(), e );
        }
    }
}
