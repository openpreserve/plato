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
package eu.scape_project.planning.manager;

import java.io.Serializable;
import java.security.InvalidParameterException;

import javax.inject.Inject;

import eu.scape_project.planning.model.ByteStream;
import eu.scape_project.planning.model.DigitalObject;

/**
 * Class offering services for moving DigitalObject data to the
 * {@link IByteStreamStorage} and vice versa. ByteStreamManager is used to
 * handle the ByteStream.
 * 
 * @author Markus Hamm
 */
public class DigitalObjectManager implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ByteStreamManager byteStreamManager;

    /**
     * Method responsible for moving DigitalObject data to IByteStreamStorage.
     * The INPUT object will be MODIFIED the following way: - No data is set any
     * more - Instead the field pid is filled which represents the identifier to
     * fetch data from a storage again. - sizeInMB is set corresponding to the
     * stored data size.
     * 
     * @param object
     *            DigitalObject with data set, which will be moved.
     * @throws StorageException
     *             If any error occurs at storing the data.
     */
    public void moveDataToStorage(DigitalObject object) throws StorageException {
        String pid = byteStreamManager.store(null, object.getData().getData());

        // remove the file data - and set the pid and filesize instead
        object.setPid(pid);
        object.setSizeInMB(object.getData().getDataInMB());
        // we must not use Bytestream.setData directly, as it also resets
        // ByteStream.size
        object.getData().getRealByteStream().setData(null);
    }

    /**
     * Method responsible for retrieving a copy of the DigitalObject filled with
     * data (fetched from a storage). (A copy of the DigitalObject is filled
     * with the data instead of the original one because usually the passed
     * DigitalObject is part of an objective tree which is stored over a long
     * time period. To avoid high memory usage it is better to charge an
     * independent object with this usually big amount of data.)
     * 
     * @param object
     *            DigitalObject with pid set, where data if of interest.
     * @return A copy of the DigitalObject filled with data.
     * @throws StorageException
     *             If any error occurs at retrieving the data from a storage.
     */
    public DigitalObject getCopyOfDataFilledDigitalObject(DigitalObject object) throws StorageException {
        // parameter check
        if ((object.getPid() == null) || (object.getPid().equals(""))) {
            throw new InvalidParameterException("DigitalObject must have a pid set to be retrievable from a storage");
        }

        byte[] digitalObjectBytes = byteStreamManager.load(object.getPid());
        ByteStream digitalObjectByteStream = new ByteStream();
        digitalObjectByteStream.setData(digitalObjectBytes);

        // return a new digital object instance - because otherwise the now
        // retrieved bytestream would be put into the current object tree
        // (originating from plan)
        // which would fill up the memory. Because we do not want this - we
        // create a standalone DigitalObject which is cleaned up by garbage
        // collector after its usage.
        DigitalObject copyObject = object.clone();
        copyObject.setData(digitalObjectByteStream);
        return copyObject;
    }

    // ---------------- getter / setter ----------------

    public ByteStreamManager getByteStreamManager() {
        return byteStreamManager;
    }

    public void setByteStreamManager(ByteStreamManager byteStreamManager) {
        this.byteStreamManager = byteStreamManager;
    }
}
