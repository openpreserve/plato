/**
 * 
 */
package eu.scape_project.planning.plato.fte;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.xml.sax.SAXException;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.tree.TemplateTree;
import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.DefineBasis;
import eu.scape_project.planning.plato.wf.DefineSampleObjects;
import eu.scape_project.planning.plato.wf.IdentifyRequirements;
import eu.scape_project.planning.plato.wf.beans.FastTrackTemplate;
import eu.scape_project.planning.plato.wf.beans.FastTrackTemplates;
import eu.scape_project.planning.xml.ProjectImporter;

/**
 * @author Markus Hamm, Michael Kraxner
 */
@Stateful
@ConversationScoped
public class FTDefineRequirements extends AbstractWorkflowStep {
	private static final long serialVersionUID = 1L;
	
	@Inject private FastTrackTemplates fastTrackTemplates;
	
	@Inject private ProjectImporter projectImporter;
	
	@Inject private Logger log;
	
	// BL of full workflow steps
	@Inject private DefineBasis defineBasis;
	@Inject private DefineSampleObjects defineSampleObjects;
	@Inject private IdentifyRequirements identifyRequirements;
	
	
	/**
	 * Nodes to delete after accepting the performed changes (before save).
	 */
	private List<TreeNode> nodesToDelete;
	
	
	public FTDefineRequirements(){
		this.requiredPlanState = PlanState.FTE_INITIALISED;
		this.correspondingPlanState = PlanState.FTE_REQUIREMENTS_DEFINED;
		nodesToDelete = new ArrayList<TreeNode>();
	}

	@Override
	public void init(Plan p) {
		super.init(p);
		
		// Init correspoding wf-step BL beans (this is mandatory to be able to call save later)
		defineBasis.init(p);
		defineSampleObjects.init(p);
		identifyRequirements.init(p);
		
		// init/fetch template list
		fastTrackTemplates.init();
	}

	@Override
	protected void saveStepSpecific() {
		// re-use BL of full workflow steps.
		defineBasis.saveWithoutModifyingPlanState();
		defineSampleObjects.saveWithoutModifyingPlanState();
		identifyRequirements.saveWithoutModifyingPlanState();
		
		deleteNodesToDelete();
	}
	
	@Override
	public void discard() throws PlanningException {
		super.discard();
		// We have to extend the standard discard function by the mandatory clean-up of "nodes to delete"
		nodesToDelete.clear();
		
		// We also have to call the discard function from all re-used BL wf-steps where we have to clean-up bytesterams
		// (because this is done in the discard function)
		defineSampleObjects.discard();
	}
	
	/**
	 * Method responsible for removing a given sample object.
	 * 
	 * @param sample Sample object to remove.
	 */
	public void removeSample(SampleObject sample) {
		defineSampleObjects.removeSample(sample);
	}
	
	/**
	 * Method responsible for adding a sample object.
	 * 
	 * @param digitalObject Sample object to add.
	 * @throws PlanningException if any error at storing the sample occured.
	 */
	public void addSample(DigitalObject digitalObject) throws PlanningException {
		defineSampleObjects.addSample(
				digitalObject.getFullname(), digitalObject.getContentType(), digitalObject.getData().getData());
	}
	
	/**
	 * Method responsible for returning all available fast-track templates.
	 * 
	 * @return All available fast-track templates.
	 */
	public List<FastTrackTemplate> getAvailableFTTemplates() {
		return fastTrackTemplates.getTemplateList();
	}
				
	/**
	 * Method responsible for using/applying the the given fast-track template.
	 * 
	 * @param template Fast-track template to use/apply.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws SAXException 
	 * @throws PlanningException 
	 */
	public void useFastTrackTemplate(FastTrackTemplate template) throws PlanningException {
		if (template == null) {
			log.info("No valid ft-template passed");
			return;
		}

		List<TemplateTree> templateTrees = null;
		try {
			byte[] templateData = fetchFileData(template.getAbsolutePath());
			
			templateTrees = projectImporter.importTemplates(templateData);
		} catch (Exception e) {
			throw new PlanningException("Unable to load template.", e);
		} 
		
        if ((templateTrees == null) || (templateTrees.size() != 1)) {
        	throw new PlanningException("Unable to load template");
        }
                
        TreeNode newRoot = ((TreeNode)templateTrees.get(0).getRoot()).clone();

        newRoot.touchAll(user.getUsername());

        //newtree.adjustScalesToMeasurements(MiniRED.getInstance().getMeasurementsDescriptor());
        newRoot.initWeights();
        
        nodesToDelete.add(plan.getTree().getRoot());
        
        plan.getTree().setRoot(newRoot);
        
        // setWeightsInitialized must be called so that initWeights does its work
        plan.getTree().setWeightsInitialized(false);
        // initWeights *must* be called because it amongst other things
        // sets the weight of the root node to 1.0. if that doesn't happed
        // the whole evaluation doesn't work.
        plan.getTree().initWeights();
	}
	
	/**
	 * Method responsible for retrieving the data of a file.
	 * 
	 * @param absolutePath Absolute path of the file
	 * @return The file data as array of bytes.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private byte[] fetchFileData(String absolutePath) throws FileNotFoundException, IOException {
		File file = new File(absolutePath);	
		FileInputStream fis = new FileInputStream(file);

        byte[] data = new byte[(int)file.length()];

        fis.read(data);
        fis.close();
        
        return data;
	}
	
    /**
     * Method responsible for deleting the nodes deleted in this step from database.
     */
    private void deleteNodesToDelete() {
    	for (TreeNode n : nodesToDelete) {
    		if (n.getId() != 0) {
    			removeEntity(n);
    		}
    	}
    	
    	nodesToDelete.clear();
    }

	// --------------- getter/setter ---------------
	
	public DefineSampleObjects getDefineSampleObjects() {
		return defineSampleObjects;
	}

	public void setDefineSampleObjects(DefineSampleObjects defineSampleObjects) {
		this.defineSampleObjects = defineSampleObjects;
	}

	public FastTrackTemplates getFastTrackTemplates() {
		return fastTrackTemplates;
	}

	public void setFastTrackTemplates(FastTrackTemplates fastTrackTemplates) {
		this.fastTrackTemplates = fastTrackTemplates;
	}
}
