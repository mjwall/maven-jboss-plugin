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

package org.codehaus.mojo.jboss;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: jeffgenender
 * Date: Oct 1, 2005
 * Time: 1:36:05 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDeployerMojo extends AbstractMojo {
    /**
     * The port jboss is running on
     *
     * @parameter default-value="8080"
     * @required
     */
    protected int port;

    /**
     * The host jboss is running on
     *
     * @parameter default-value="localhost"
     * @required
     */
    protected String hostName;

    /**
     * The name of the file or directory to deploy or undeploy.
     *
     * @parameter expression="${project.build.directory}/${project.build.finalName}.${project.packaging}"
     * @required
     */
    protected String fileName;

        protected void doURL(String url) throws MojoExecutionException {
        try{
            HttpURLConnection connection = (HttpURLConnection) new URL( url ).openConnection();
            connection.setInstanceFollowRedirects(false);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            reader.readLine();
            reader.close();
        } catch ( Exception e ){
            throw new MojoExecutionException( "Mojo error occurred: " + e.getMessage(), e );
        }
    }
}
