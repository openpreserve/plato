//package at.tuwien.minimee.view;
//
//import java.io.Serializable;
//
//import javax.enterprise.context.SessionScoped;
//import javax.inject.Inject;
//import javax.inject.Named;
//
//import at.tuwien.minimee.controller.MiniMeeAdminAction;
//import eu.scape_project.pw.planning.utils.FacesMessages;
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
