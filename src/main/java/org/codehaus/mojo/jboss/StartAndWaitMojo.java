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
     * Maximum number of retries to get JBoss JMX MBean connection.
     * 
     * @parameter default-value="3" expression="${jboss.retry}"
     */
    protected int retry;

    /**
     * Wait in ms before each retry of the JBoss JMX MBean connection.
     * 
     * @parameter default-value="5000" expression="${jboss.retryWait}"
     */
    protected int retryWait;
        
    /**
     * Time in ms to start the application server (once JMX MBean connection has been reached).
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
        MBeanServerConnection server = null;
        NamingException ne = null;
        for ( int i = 0; i < retry; ++i )
        {
            try
            {
                Thread.sleep( retryWait );
                server = (MBeanServerConnection) ctx.lookup( "jmx/invoker/RMIAdaptor" );
                break;
            }
            catch ( NamingException e )
            {
                ne = e;
                getLog().info( "Retry to retrieve JBoss jmx MBean connection... " );
            }
            catch ( InterruptedException e )
            {
                getLog().warn( "Thread interrupted while waiting for MBean connection: " +  e.getMessage() );
                e.printStackTrace();
            }
        }
        
        if ( server == null )
        {
            throw new MojoExecutionException( "Unable to get JBoss jmx MBean connection: " + ne.getMessage(), ne );
        }
        getLog().info( "JBoss JMX MBean connection successful!" );
        
        // Wait until server startup is complete
        boolean isStarted = false;
        long startTime = System.currentTimeMillis();
        while ( !isStarted && System.currentTimeMillis() - startTime < timeout )
        {
            try
            {
                Thread.sleep( ONE_SECOND );
                isStarted = isStarted( server );
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
     * Check if the server has finished startup.  Will throw one of several
     * exceptions if the server connection fails.
     * 
     * @param s
     * @return
     * @throws Exception 
     */
    protected boolean isStarted( MBeanServerConnection server )
        throws Exception
    {
        ObjectName serverMBeanName = new ObjectName( "jboss.system:type=Server" );
        return ( (Boolean) server.getAttribute( serverMBeanName, "Started" ) ).booleanValue();
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
