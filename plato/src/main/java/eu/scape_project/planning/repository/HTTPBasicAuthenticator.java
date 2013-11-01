package eu.scape_project.planning.repository;

import org.apache.commons.codec.binary.Base64;

public class HTTPBasicAuthenticator {
    public static final String AUTH_HEADER = "Authorization";
    public static final String AUTH_TYPE = "Basic";

    private String authorizationHeader;

    public HTTPBasicAuthenticator(final String user, final String password) {
        this.authorizationHeader = AUTH_TYPE + " " + Base64.encodeBase64String((user+":" + password).getBytes());
    }
    
//    public void addAuthorization(ClientRequest request) {
//        request.header(AUTH_HEADER, this.authorizationHeader);
//    }
}
