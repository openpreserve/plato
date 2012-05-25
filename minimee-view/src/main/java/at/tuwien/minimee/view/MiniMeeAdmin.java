/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
//package at.tuwien.minimee.view;
//
//import java.io.Serializable;
//
//import javax.enterprise.context.SessionScoped;
//import javax.inject.Inject;
//import javax.inject.Named;
//
//import at.tuwien.minimee.controller.MiniMeeAdminAction;
//import eu.scape_project.planning.utils.FacesMessages;
//
//public class MiniMeeAdmin implements Serializable{
//	private static final long serialVersionUID = 1L;
//	
//	@Inject private MiniMeeAdminAction miniMeeAdmin;
//
//	@Inject FacesMessages facesMessages;
//	
//
//	
//	private String localPath;
//	
//	public MiniMeeAdmin(){
//	}
//	
//    public void verifySetup() {
//    	miniMeeAdmin.verifySetup();
//    }	
//    
//    public void reloadRegistry() {
//    	miniMeeAdmin.reloadRegistry();
//    }
//    
//    public void benchmark(){
//    	double score = miniMeeAdmin.benchmark();
//        facesMessages.addInfo("Ladies and gentlemen, this registry has a score of " + score);
//    }
//    
//    public void reloadRegistryFromPath() {
//        if (localPath == null || "".equals(localPath)) {
//        	facesMessages.addError("Please provide a local path name to the XML file");
//        } else {
//        	miniMeeAdmin.reloadRegistryFromPath(localPath);
//        }
//    }
//
//	public String getLocalPath() {
//		return localPath;
//	}
//
//	public void setLocalPath(String localPath) {
//		this.localPath = localPath;
//	}
//
//}
