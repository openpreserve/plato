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
package eu.scape_project.planning.plato.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.SampleObject;

public class ExperimentStatus implements Serializable {

    private static final long serialVersionUID = -793940683965238929L;

    // private static Logger log =
    // LoggerFactory.getLogger(ExperimentStatus.class);

    private List<Alternative> alternatives = new ArrayList<Alternative>();
    private List<SampleObject> samples = new ArrayList<SampleObject>();

    public void setSamples(List<SampleObject> samples) {
        this.samples = samples;
    }

    public void setAlternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    private int altIndex;
    private int sampleIndex;

    private boolean started;
    private boolean finished;
    private boolean canceled;

    private int ticks;

    public boolean isStarted() {
        return started;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(final boolean canceled) {
        this.canceled = canceled;
    }

    public void cancel() {
        canceled = true;
    }

    public void experimentSetup(List<Alternative> alternatives, List<SampleObject> samples) {
        clear();
        this.alternatives.addAll(alternatives);
        this.samples.addAll(samples);
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void clear() {
        alternatives.clear();
        samples.clear();
        altIndex = -1;
        sampleIndex = -1;
        finished = false;
        canceled = false;
        started = false;
        ticks = 0;
    }

    public int getNextSampleIndex() {
        return ++sampleIndex;
    }

    public SampleObject getNextSample() {
        if (!canceled) {
            if (sampleIndex < samples.size() - 1) {
                sampleIndex++;
                return samples.get(sampleIndex);
            }
        }
        return null;
    }

    public Alternative getNextAlternative() {
        started = true;
        if (!canceled) {
            if (altIndex < alternatives.size() - 1) {
                // start with first sample
                sampleIndex = -1;
                altIndex++;
                return alternatives.get(altIndex);
            }
        }
        // last alternative reached - finished
        finished = true;
        return null;
    }

    public int getAltIndex() {
        return altIndex;
    }

    public int getAltTotal() {
        return alternatives.size();
    }

    public int getSampleIndex() {
        return sampleIndex;
    }

    public int getSamplesTotal() {
        return samples.size();
    }

    public SampleObject getCurrentSample() {
        if ((sampleIndex < 0) || (sampleIndex >= samples.size())) {
            return null;
        }
        return samples.get(sampleIndex);
    }

    public Alternative getCurrentAlternative() {
        if ((altIndex < 0) || (altIndex >= alternatives.size())) {
            return null;
        }
        return alternatives.get(altIndex);
    }

    public void keepAlive() {
        ticks++;
        // log.error("current ticks: " + ticks);
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}
