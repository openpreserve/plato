/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
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
package eu.scape_project.planning.plato;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.Stateful;
import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.manager.ByteStreamManager;
import eu.scape_project.planning.manager.DigitalObjectManager;
import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DetailedExperimentInfo;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.beans.MigrationResult;
import eu.scape_project.planning.model.interfaces.actions.IMigrationAction;
import eu.scape_project.planning.model.interfaces.actions.IPreservationAction;
import eu.scape_project.planning.model.measurement.Measurement;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.values.Value;
import eu.scape_project.planning.plato.bean.ExperimentStatus;
import eu.scape_project.planning.services.pa.PreservationActionServiceFactory;

/**
 * Experiment runner to asynchronously execute experiments.
 */
@Stateful
public class ExperimentRunner implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;

    @Inject
    private DigitalObjectManager digitalObjectManager;

    @Inject
    private ByteStreamManager byteStreamManager;

    private Plan plan;

    private ExperimentStatus experimentStatus;

    /**
     * Runs all experiments scheduled in experimentStatus.
     * 
     * @param plan
     *            the plan of the experiments
     * @param experimentStatus
     *            the experiment status to run
     */
    @Asynchronous
    public void startExperiments(Plan plan, ExperimentStatus experimentStatus) {
        this.plan = plan;
        this.experimentStatus = experimentStatus;

        Alternative alt = experimentStatus.getNextAlternative();
        while (alt != null && !experimentStatus.isCanceled()) {
            if (alt.isExecutable()) {
                runPreservationAction(alt);
            }

            alt = experimentStatus.getNextAlternative();
        }
        System.gc();
    }

    /**
     * Runs preservation actions for the provided alternative.
     * 
     * @param a
     *            the alternative to run
     */
    private void runPreservationAction(Alternative a) {
        if (!a.isExecutable()) {
            return;
        }

        IPreservationAction action = PreservationActionServiceFactory.getPreservationAction(a.getAction());

        if (action == null) {
            String msg = String
                .format(
                    "Preservation action %s - %s is not registered or accessible and cant be executed. (Please check the registry.)",
                    a.getAction().getShortname(), a.getAction().getInfo());

            setProgramOutputForAlternative(a, msg, false);
        }

        if (action instanceof IMigrationAction) {
            IMigrationAction migrationAction = (IMigrationAction) action;
            SampleObject record = experimentStatus.getNextSample();
            while (record != null) {
                if (record.isDataExistent()) {
                    MigrationResult migrationResult = null;

                    try {
                        DigitalObject workflow = a.getExperiment().getWorkflow();
                        if (workflow != null) {
                            byte[] workflowData = byteStreamManager.load(workflow.getPid());
                            workflow.getData().setData(workflowData);
                        }
                        DigitalObject objectToMigrate = digitalObjectManager.getCopyOfDataFilledDigitalObject(record);
                        migrationResult = migrationAction.migrate(a, objectToMigrate);
                    } catch (StorageException e) {
                        log.error("Failed to load sample object", e);
                    } catch (NullPointerException e) {
                        log.error(
                            "Caught nullpointer exception when running a migration tool. ### WRONG CONFIGURATION? ###",
                            e);
                    } catch (Throwable t) {
                        log.error("Caught unchecked exception when running a migration tool: " + t.getMessage(), t);
                    }

                    // Set detailed info before moving data to storage
                    extractDetailedInfos(a, record, migrationResult);

                    if (migrationResult != null) {
                        try {
                            if (migrationResult.isSuccessful()) {
                                DigitalObject experimentResultObject = a.getExperiment().getResults().get(record);
                                experimentResultObject.assignValues(migrationResult.getMigratedObject());
                                digitalObjectManager.moveDataToStorage(experimentResultObject);

                                addCriteria(a, record, migrationResult);
                            }
                        } catch (StorageException e) {
                            log.error("Could not store migration result", e);
                            migrationResult.setSuccessful(false);
                            migrationResult.setReport("Migration failed - could not store result.");
                        }
                    }
                }
                record = experimentStatus.getNextSample();
            }
        }
    }

    /**
     * Adds values from the experiment results to the tree.
     * 
     * @param alternative
     *            the alternative of the experiment run.
     * @param sample
     *            the sample object
     * @param migrationResult
     *            experiment results
     */
    private void addCriteria(Alternative alternative, SampleObject sample, MigrationResult migrationResult) {
        Map<String, Measurement> measurements = migrationResult.getMeasurements();

        List<SampleObject> samples = plan.getSampleRecordsDefinition().getRecords();

        List<Leaf> leaves = plan.getTree().getRoot().getAllLeaves();
        for (Leaf leaf : leaves) {
            if (leaf.isMapped()) {
                Measurement measurement = measurements.get(leaf.getMeasure().getUri());
                if (measurement != null && measurement.getValue() != null) {
                    Value value = leaf.getScale().createValue();
                    value.setComment(measurement.getValue().getComment());
                    try {
                        value.parse(measurement.getValue().toString());
                    } catch (Exception e) {
                        // Catch parsing exceptions
                        log.debug("Error parsing measure value", e);
                    }
                    int i = samples.indexOf(sample);
                    leaf.getValues(alternative.getName()).setValue(i, value);
                }
            }

        }
    }

    /**
     * For the given alternative the program output of all experiment infos is
     * set to {@code msg}.
     * 
     * @param a
     *            the alternative to update
     * @param msg
     *            the message to set
     * @param successful
     *            successful flag to set
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
     * Stores {@link MigrationResult migration results} for the given sample
     * object in {@link DetailedExperimentInfo experiment info} of the
     * alternative {@code a}.
     * 
     * @param a
     *            the alternative
     * @param sample
     *            sample object of the experiment
     * @param migrationResult
     *            migration result of the experiment
     */
    private void extractDetailedInfos(Alternative a, SampleObject sample, MigrationResult migrationResult) {
        DetailedExperimentInfo info = a.getExperiment().getDetailedInfo().get(sample);
        if (info == null) {
            info = new DetailedExperimentInfo();
            a.getExperiment().getDetailedInfo().put(sample, info);
        }

        info.clear();

        if (migrationResult == null) {
            info.setProgramOutput(String.format("Applying action %s to sample %s failed.",
                a.getAction().getShortname(), sample.getFullname()));
        } else {
            info.getMeasurements().putAll(migrationResult.getMeasurements());
            info.setSuccessful(migrationResult.isSuccessful());

            if (migrationResult.getReport() == null) {
                info.setProgramOutput("The tool didn't provide any output.");
            } else {
                info.setProgramOutput(migrationResult.getReport());
            }

            // Execution claimed to be successful but size = 0
            DigitalObject migratedObject = migrationResult.getMigratedObject();
            if (migrationResult.isSuccessful() && (migratedObject == null || migratedObject.getData().getSize() == 0)) {
                info.setSuccessful(false);
                info.setProgramOutput(info.getProgramOutput()
                    + "\nSomething went wrong during migration. No result file has been generated.");
            }
        }
    }

}
