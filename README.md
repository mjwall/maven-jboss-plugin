This is my fork of the jboss maven plugin, from the svn repo at
http://svn.codehaus.org/mojo/trunk/mojo/jboss-maven-plugin.

I forked it during the process of making a jboss sample project, found at 
https://github.com/mjwall/jboss-sample.  The current version is 1.5.0, and
there is no skip option when using hard-deploy and hard-undeploy.  That makes
using it in multi module maven project like jboss-sample tough if not every
module needs to be deployed.  Deploy and undeploy seem to work, but they do not
survive jboss restarts.

I submitted a ticket at http://jira.codehaus.org/browse/MJBOSS-63 and a patch.
If the team accepts the patch or adds skip in another way, you should use that
version.

If you want to use this plugin, the best way is to clone this repo, make
sure you are in the "mjwall" branch and then run the following

`mvn clean install`

You can then define this version in your pom like so:

<blockquote>
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>jboss-maven-plugin</artifactId>
  <version>1.5.1-mjwall-1</version>
  <configuration>
    ...etc...
  </configuration>
</plugin>
</blockquote>

For more information about the other options in the jboss-maven-plugin, see
http://mojo.codehaus.org/jboss-maven-plugin/
