package eu.scape_project.pw.planning.plato;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.Stateful;
import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DetailedExperimentInfo;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Experiment;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PreservationActionDefinition;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.beans.MigrationResult;
import eu.scape_project.planning.model.interfaces.actions.IMigrationAction;
import eu.scape_project.planning.model.interfaces.actions.IPreservationAction;
import eu.scape_project.pw.planning.manager.DigitalObjectManager;
import eu.scape_project.pw.planning.manager.StorageException;
import eu.scape_project.pw.planning.plato.bean.ExperimentStatus;
import eu.scape_project.pw.planning.services.preservationaction.PreservationActionServiceFactory;

@Stateful
public class ExperimentRunner implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject private Logger log;
	
	@Inject private DigitalObjectManager digitalObjectManager;
	
	private Plan plan;
	private ExperimentStatus experimentStatus;
	
    /**
     * runs all experiments scheduled in experimentStatus
     */
	@Asynchronous
    public void startExperiments(Plan plan, ExperimentStatus experimentStatus) {
		this.plan = plan;
		this.experimentStatus = experimentStatus;
		
        Alternative alt = experimentStatus.getNextAlternative(); 
        while ((alt != null)&&(! experimentStatus.isCanceled())) {
            runSingle(alt);
            alt = experimentStatus.getNextAlternative();
        }
        System.gc();
    }
	
	
    private void runSingle(Alternative a) {
        if (!a.isExecutable()) {
            // this alternative has to be evaluated manually, nothing to do here
            return;
        }

        IPreservationAction action =
            PreservationActionServiceFactory.getPreservationAction(a.getAction());
        // if the action is null the service isn't accessible (anymore)
        // we have to set an error message for each sample record
        if (action == null) {
            String msg = String.format("Preservation action %s - %s is not registered or accessible and cant be executed. (Please check the registry.)",
                    a.getAction().getShortname(), a.getAction().getInfo());

            setProgramOutputForAlternative(a, msg, false);
        } 
        // das sieht verdächtig aus - pad wird auch geschrieben. 
        // ABER die settings, die in pad eingetragen werden, brauchen wir nicht zum persistieren, sondern nur zum 
        // ausführen der action - stehen ja in experiment.settings. daher ist es ok, dass wir die merged instance verändern.
        PreservationActionDefinition pad = a.getAction();
        pad.setExecute(a.getAction().isExecute());

        String settings = a.getExperiment().getSettings();
        pad.setParamByName("settings", settings);

        if (action instanceof IMigrationAction) {
            IMigrationAction migrationAction = (IMigrationAction) action;
            SampleObject record = experimentStatus.getNextSample();
            while (record != null) {
                if (record.isDataExistent()) {
                    DigitalObject migrationResultObject = null;
                    DigitalObject experimentResultObject = null;
                    MigrationResult migrationResult = null;

                    try {
                    	// retrieve the data of the digital object
						DigitalObject objectToMigrate = digitalObjectManager.getCopyOfDataFilledDigitalObject(record);

						try {
						    // ACTION HAPPENS HERE:
						    migrationResult =  migrationAction.migrate(pad, objectToMigrate);
						} catch (NullPointerException npe) {
						    log.error("Caught nullpointer exception when running a migration tool. ### WRONG CONFIGURATION? ###",npe);
						} catch (Throwable t) {
						    log.error("Caught unchecked exception when running a migration tool: "+t.getMessage(),t);
						}
					} catch (StorageException e1) {
						log.error("Failed to load sample object: ", e1);
					}

                    if (migrationResult != null) {
                        // set detailed infos depending on migration result
					    // we have to this now, because we remove the binary data after storing the digital object  
                        extractDetailedInfos(a.getExperiment(), record, migrationResult);

                        try {
							if (migrationResult.isSuccessful() && migrationResult.getMigratedObject() != null) {
							    
							    migrationResultObject = migrationResult.getMigratedObject();
							    
							    experimentResultObject = a.getExperiment().getResults().get(record);
							    experimentResultObject.setContentType(migrationResultObject.getContentType());
							    experimentResultObject.getFormatInfo().assignValues(migrationResultObject.getFormatInfo());
							    experimentResultObject.setData(migrationResultObject.getData());
							    experimentResultObject.setFullname(migrationResultObject.getFullname());
							    
							    digitalObjectManager.moveDataToStorage(experimentResultObject);
							    //addedBytestreams.add(experimentResultObject.getPid());

							    //characteriseFits(experimentResultObject);
							    // experimentResultObject.setJhoveXMLString(jHoveAdaptor.describe(tempFiles.get(experimentResultObject)));
							}
						} catch (StorageException e) {
							log.error("Could not store result: ", e);
							migrationResult.setSuccessful(false);
							migrationResult.setReport("Migration failed - could not store result.");
						}


                    } else {
                        DetailedExperimentInfo info = a.getExperiment().getDetailedInfo().get(record);
                        if (info == null) {
                            info = new DetailedExperimentInfo();
                            a.getExperiment().getDetailedInfo().put(record,info);
                        }
                        info.setProgramOutput(
                                String.format("Applying action %s to sample %s failed.",
                                        a.getAction().getShortname(), 
                                        record.getFullname()));
                    }
                } 
                record = experimentStatus.getNextSample(); 
            }
        }
    }
    
    
    /**
     * for the given alternative the program output of all experiment infos is set to <param>msg</param>.
     *  
     */
    private void setProgramOutputForAlternative(Alternative a, String msg, boolean successful) {
        List<SampleObject> sampleObjects = plan.getSampleRecordsDefinition().getRecords();
        for (SampleObject o : sampleObjects) {
            DetailedExperimentInfo info = a.getExperiment().getDetailedInfo().get(o);
            
            if (info == null) {
                info = new DetailedExperimentInfo();
                a.getExperiment().getDetailedInfo().put(o, info);
            }
            
            info.setProgramOutput(msg);
            info.setSuccessful(successful);
        }
    }	
    
    /**
     * stores {@link MigrationResult migration results} for the given sample object in {@link DetailedExperimentInfo experiment info} of experiment <param>e</param>. 
     */
    private void extractDetailedInfos(Experiment e, SampleObject sample, MigrationResult migrationResult) {
        DetailedExperimentInfo info = e.getDetailedInfo().get(sample);
        // remove previous results
        if (info != null) {
            info.getMeasurements().clear();
        } else {
            info = new DetailedExperimentInfo();
            e.getDetailedInfo().put(sample, info);
        }
        if (migrationResult == null) {
            // nothing to add
            return;
        }
        // write info of migration result to experiment's detailedInfo
        info.getMeasurements().putAll(migrationResult.getMeasurements());
        info.setSuccessful(migrationResult.isSuccessful());
        
        if (migrationResult.getReport() == null) {
            info.setProgramOutput("The tool didn't provide any output.");
        } else {
            info.setProgramOutput(migrationResult.getReport());
        }
        
        // if the executing programme claims to have migrated the object, but the result file has size 0 than something must have
        // gone wrong. so we set the migration result to 'false' and add some text to the program output.
        long sizeMigratedObject = (migrationResult.getMigratedObject() == null)? 0 : migrationResult.getMigratedObject().getData().getSize(); 
        
        if (migrationResult.isSuccessful() && sizeMigratedObject == 0) {
           info.setSuccessful(false);
           String programOutput = info.getProgramOutput();
           
           programOutput += "\nSomething went wrong during migration. No result file has been generated.";
           
           info.setProgramOutput(programOutput);
        }
    }        

}
