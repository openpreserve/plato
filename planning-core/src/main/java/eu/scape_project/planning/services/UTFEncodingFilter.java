package eu.scape_project.planning.services;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
 
public class UTFEncodingFilter implements Filter {
 
    public void init(FilterConfig config) throws ServletException {
 
    }
 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(request.getCharacterEncoding()==null){
            request.setCharacterEncoding("UTF-8");
        }
        if(response.getCharacterEncoding() == null) {
            response.setCharacterEncoding("UTF-8");
        }
        if (response.getContentType() == null) {
            response.setContentType("text/html; charset=UTF-8");
        }
//        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }
 
    public void destroy() {
    }
}