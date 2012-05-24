package eu.scape_project.pw.planning.plans;

import java.io.Serializable;

import javax.inject.Inject;

import org.slf4j.Logger;

import eu.planets_project.pp.plato.bean.PrepareChangesForPersist;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.User;
import eu.scape_project.pw.planning.LoadedPlan;
import eu.scape_project.pw.planning.manager.ByteStreamManager;
import eu.scape_project.pw.planning.manager.DigitalObjectManager;
import eu.scape_project.pw.planning.manager.PlanManager;
import eu.scape_project.pw.planning.manager.StorageException;

public class PlanSettings implements Serializable {
	private static final long serialVersionUID = 1697444685695759020L;

	@Inject private Logger log;

	@Inject private PlanManager planManager;
	
	@Inject private ByteStreamManager byteStreamManager;
	
	@Inject private DigitalObjectManager digitalObjectManager;

	@Inject	@LoadedPlan	private Plan plan;
	
	private final static int pwCode = -3425080;
	
	/**
	 * Method responsible to check if a user is allowed to modify the settings of a given plan.
	 * 
	 * @param user User who wants to modify plan settings.
	 * @param plan Plan to check for allowance.
	 * @return true is user is allowed to modify plan settings of the given plan. False otherwise.
	 */
	public boolean isUserAllowedToModifyPlanSettings(User user, Plan plan) {
		return (user != null) && (user.isAdmin() || user.getUsername().equals(plan.getPlanProperties().getOwner()));
	}

	/**
	 * Method responsible for deleting a plan from database.
	 * 
	 * @param plan Plan to delete.
	 */
	public void deletePlan(Plan plan) {
		planManager.deletePlan(plan);
	}

	/**
	 * Method responsible for action execution activation for a given plan.
	 * 
	 * @param plan Plan to activate action execution for.
	 * @param password Activation-password.
	 */
	public boolean activateActionExecutionForPlan(Plan plan, String password) {
		if (password != null && password.hashCode() == pwCode) {
			int count = 0;
			for (Alternative alt : plan.getAlternativesDefinition().getAlternatives()) {
				if (alt.getAction() != null && alt.getAction().getActionIdentifier().toLowerCase().contains("minimee")) {
					alt.getAction().setExecute(true);
					count++;
				}
			}
			
			log.debug("Activated execution of " + count + " actions in plan with id " + plan.getId());
			return true;
		}
		
		return false;
	}
	
    /**
     * Method responsible for adding a final report to the plan.
     * 
     * @param report The final report to add.
     * @throws StorageException If any kind of error occurs at storing the report in the file-system.
     */
	public void uploadReport(DigitalObject report) throws StorageException {
		digitalObjectManager.moveDataToStorage(report);

		plan.getPlanProperties().setReportUpload(report);
	}
	
    /**
     * Method responsible for removing the final report from the plan.
     * 
     * @throws StorageException If any kind of error occurs at deleting the report from file-system.
     */
	public void removeReport() throws StorageException {
		DigitalObject report = plan.getPlanProperties().getReportUpload();
		byteStreamManager.delete(report.getPid());
		
		plan.getPlanProperties().setReportUpload(new DigitalObject());
	}
	
	/**
	 * Method responsible for fetching the currently present plan report.
	 * 
	 * @return A copy of the present plan report.
	 * @throws StorageException If any kind of error occurs at fetching the report from file-system.
	 */
	public DigitalObject fetchReport() throws StorageException {
		DigitalObject digitalObject = plan.getPlanProperties().getReportUpload();
		
		return digitalObjectManager.getCopyOfDataFilledDigitalObject(digitalObject);
	}
	
	/**
	 * Method responsible for persisting the changes.
	 */
	public void save(Plan plan, User user) {
		PrepareChangesForPersist prepChanges = new PrepareChangesForPersist(user.getUsername());
		prepChanges.prepare(plan);
		
		planManager.saveForPlanSettings(plan.getPlanProperties(), plan.getAlternativesDefinition());
	}
	
	
}
