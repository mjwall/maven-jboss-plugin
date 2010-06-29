package org.codehaus.mojo.jboss;

/*
 * Copyright 2005 Jeff Genender.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Stops JBoss. By default the plugin will return immediately after calling "shutdown" command. The @see #stopWait
 * parameter can be used to force the plugin to wait for a specified time before returning control.
 * 
 * @author <a href="mailto:jgenender@apache.org">Jeff Genender</a>
 * @goal stop
 * @requiresProject false
 */
public class StopMojo
    extends AbstractJBossServerMojo
{

    /**
     * The command to shutdown JBoss.
     */
    public static final String SHUTDOWN_COMMAND = "shutdown";

    /**
     * The set of options to pass to the JBoss "shutdown" command.
     * 
     * @parameter default-value="" expression="${jboss.options}"
     */
    protected String options;

    /**
     * Wait in ms for server to shutdown before the plugin returns.
     * 
     * @since 1.4.1
     * @parameter expression="${jboss.stopWait}"
     */
    protected int stopWait;

    /**
     * Main plugin execution.
     * 
     * @throws MojoExecutionException
     */
    public void execute()
        throws MojoExecutionException
    {
        String credentials = "";
        
        if ( getUsername() != null )
        {
            credentials = " -u " + getUsername() + " -p " + getPassword();
        }
        
        launch( SHUTDOWN_COMMAND, options + credentials );

        if ( stopWait > 0 )
        {
            try
            {
                Thread.sleep( stopWait );
            }
            catch ( InterruptedException e )
            {
                getLog().warn( "Thread interrupted while waiting for JBoss to stop: " + e.getMessage() );
                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( "Thread interrupted while waiting for JBoss to stop: ", e );
                }
            }
        }
    }

}
