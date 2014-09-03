package eu.scape_project.planning.services;

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.UnauthorizedException;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.utils.ConfigurationLoader;

@Provider
@ServerInterceptor
public class RestSecurityInterceptor implements PreProcessInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestSecurityInterceptor.class);
    @Override
    public ServerResponse preProcess(HttpRequest request, ResourceMethod resourceMethod) throws Failure, WebApplicationException {
        // Then get the HTTP-Authorization header and base64 decode it
        List<String> authHeader = request.getHttpHeaders().getRequestHeader("Authorization");
        if (authHeader != null) {
            
            
            for (String auth : authHeader) {
                if (auth.startsWith("Basic ")) {
                    byte encoded[] = Base64.decodeBase64(auth.substring(6));
                    String encodedStr = new String(encoded);
                    String userPwd[] = encodedStr.split(":");
                    if ((userPwd != null) && 
                        (userPwd.length == 2) &&
                        StringUtils.isNotEmpty(userPwd[0]) &&
                        StringUtils.isNotEmpty(userPwd[1])) {
                        ConfigurationLoader configurationLoader = new ConfigurationLoader();
                        Configuration config = configurationLoader.load();
                        String path = request.getPreprocessedPath().replaceAll("/", ".").substring(1);
                        
                        String user = config.getString(path + ".rest.user", "");
                        String passwd = config.getString(path + ".rest.pass", "");
                        
                        if (user.equals(userPwd[0]) && passwd.equals(userPwd[1])) {
                            return null;
                        }
                    }
                }
            }
        }
        throw new UnauthorizedException("Username/Password does not match");        
    }

}
