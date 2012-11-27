package eu.scape_project.planning.application;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;

@Stereotype
@Target( {ElementType.TYPE, ElementType.METHOD} )
@Retention(RetentionPolicy.RUNTIME)
@Alternative
public @interface Mock {

}
