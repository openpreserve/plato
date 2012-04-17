package eu.scape_project.pw.planning.utils;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;

/**
 * From {@link http://docs.jboss.org/weld/reference/1.1.0.Final/en-US/html/injection.html#d0e1624}
 *  
 * Creates a logger for the class where it should be injected.
 * This way we don not have specify the log category (class) every time - eases refactoring and configuration.
 *   
 * usage: @Inject Logger log;
 * 
 * @author Michael Kraxner 
 * 
 */
public class LoggerFactory {
	@Produces
	public Logger createLogger(InjectionPoint injectionPoint) {
		return org.slf4j.LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass());
	}
}
