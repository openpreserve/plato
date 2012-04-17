/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.model.util;

import java.io.Serializable;

/**
 * A lightweight helper class for Criterion, which allows easier parsing of URIs
 * It holds information on what is measured (scheme and path of the URI) 
 * and for derived measurements the metric which is used (in the fragment part of the URI)  
 * 
 */
public class CriterionUri implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String uri;
    
//    /**
//     * pattern for matching an URI string
//     * scheme://[pathpart[/pathpart]*[#fragment]
//     */
//    private static final Pattern URI_PATTERN = Pattern.compile("^(?:(\\w+)://(?:(\\w+(?:/\\w+)*)(?:#((\\w)+))?)?)$");
    
//    /**
//     * defines what is measured, action or outcome criterion
//     */
//    private String scheme;
//    
//    /**
//     * the key of a specific property 
//     */
//    private String path;
//    
//    /**
//     * information about the metric, if it represents a derived measurement
//     */
//    private String fragment;
    
    public CriterionUri() {
    }
    
    public CriterionUri(String uri) {
        setAsURI(uri);
    }
    
    /**
     * returns the string representation of the URI
     *   
     * @return null, if parts do not correspond to a valid URI 
     */
    public String getAsURI() {
    	return uri;
//        if (scheme == null) {
//            return null;
//        }
//        String uri = scheme + "://";
//        
//        if (path != null) {
//            uri = uri + path;
//
//            // no metric without a measurement 
//            if (fragment != null) {
//                uri = uri + "#" + fragment;
//            }
//        }
//        return uri;
    }

    /**
     * sets the measurement info parts according to the given uri.
     * - the uri must satisfy <scheme>://[<path>#[<fragment>]]
     *   so at least a scheme is required.
     * - if null or an empty string is passed to this method, all measurement information is reset.
     * 
     * @throws InvalidArgumentException  if the given uri is invalid - then its values remain unchanged. 
     * @param uri
     */
    public void setAsURI(String uri) throws IllegalArgumentException{
    	this.uri = uri; 
//    	if (uri == null || "".equals(uri)) {
//    	    this.scheme = null;
//    	    this.path = null;
//    	    this.fragment = null;
//    	    return;
//    	}
//    	
//    	Matcher m = URI_PATTERN.matcher(uri);
//    	if (!m.matches()) {
//    	    throw new IllegalArgumentException(uri + " is not a valid measurement info URI");
//    	}
//    	// if a group had no match, it is set to null - this is exactly what we want
//        scheme = m.group(1);
//        path = m.group(2);
//        fragment = m.group(3);

    }
//    public void assign(CriterionUri m) {
//        scheme = m.getScheme();
//        path = m.getPath();
//        fragment = m.getFragment();
//    }
    
//    public String getScheme() {
//        return scheme;
//    }
//
//    public void setScheme(String scheme) {
//        this.scheme = scheme;
//    }
//
//    public String getPath() {
//        return path;
//    }
//
//    public void setPath(String path) {
//        this.path = path;
//    }
//
//    public String getFragment() {
//        return fragment;
//    }
//
//    public void setFragment(String fragment) {
//        this.fragment = fragment;
//    }
}
