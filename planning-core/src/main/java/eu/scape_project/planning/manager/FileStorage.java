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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import eu.scape_project.planning.utils.ConfigurationLoader;
import eu.scape_project.planning.utils.FileUtils;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;

/**
 * A {@link IByteStreamStorage} which stores the data in the file system. The
 * location of the files is defined by {@link #storagePath}
 * 
 * Note: - atm this storage handler shares the same scope as the plan being
 * worked on - it is not defined what happens if multiple threads store data for
 * the same pid This is not be a problem, as a bytestream is always related to
 * one plan(which can only be accessed once at a time) and we do not have
 * multiple threads altering the same bytestream
 * 
 * @author Michael Kraxner
 */
// Do not use @ConversationScoped because the ExperimentRunner is called
// asynchronous and no ConversationScope is available then.
@Stateful
@ApplicationScoped
public class FileStorage implements Serializable, IByteStreamStorage {
    private static final long serialVersionUID = -2406172386311143101L;

    /**
     * Name of the configuration.
     */
    private static final String CONFIG_NAME = "filestorage.properties";

    @Inject
    private Logger log;

    @Inject
    private ConfigurationLoader configurationLoader;

    /**
     * The storage path.
     */
    private String storagePath = null;

    /**
     * File handle to storagePath.
     */
    private File storagePathFile;

    /**
     * will be used as namespace for persistent identifiers, according to
     * {@link https://wiki.duraspace.org/display/FEDORA35/Fedora+Identifiers}.
     */
    private String repositoryName;

    /**
     * Default constructor.
     */
    public FileStorage() {
    }

    /**
     * Initializes class.
     */
    @PostConstruct
    public void init() {

        Configuration config = configurationLoader.load(CONFIG_NAME);
        storagePath = config.getString("filestorage.path");

        if (storagePath != null) {
            storagePathFile = new File(storagePath);
            if (!storagePathFile.exists()) {
                if (storagePathFile.mkdirs()) {
                    log.info("Storage path created and set to {}.", storagePathFile.getAbsoluteFile());
                } else {
                    log.error("Storage path could not be created.");
                }
            } else {
                log.info("Storage path set to {}.", storagePathFile.getAbsoluteFile());
            }
        } else {
            log.error("Storage path not set.");
        }

        repositoryName = config.getString("filestorage.repository.name");
        if (repositoryName == null) {
            log.error("Repository name not set.");
        }
    }

    @Override
    public String store(String pid, byte[] bytestream) throws StorageException {
        String objectId;
        if (pid == null) {
            // a new object
            objectId = UUID.randomUUID().toString();
            pid = repositoryName + ":" + objectId;
        } else {
            // we ignore the object's namespace
            objectId = pid.substring(pid.indexOf(':') + 1);
        }
        // we try to rename the file, if it already exists
        File file = new File(storagePathFile, objectId);
        File backup = null;
        if (file.exists()) {
            try {
                backup = File.createTempFile(file.getName(), "backup", storagePathFile);
                file.renameTo(backup);
            } catch (IOException e) {
                throw new StorageException("failed to create backup for: " + pid, e);
            }
        }
        try {
            // write data to filesystem
            FileUtils.writeToFile(new ByteArrayInputStream(bytestream), new FileOutputStream(file));
            // data was stored successfully, backup is not needed any more
            if (backup != null) {
                backup.delete();
            }
        } catch (IOException e) {
            // try to restore old file
            if (backup != null) {
                if (backup.renameTo(file)) {
                    backup = null;
                } else {
                    throw new StorageException("failed to store digital object: " + pid
                        + " and failed to restore backup!");
                }
            }
            throw new StorageException("failed to store digital object: " + pid, e);
        }
        return pid;

    }

    @Override
    public byte[] load(String pid) throws StorageException {
        File file = getFile(pid);
        try {
            return FileUtils.inputStreamToBytes(new FileInputStream(file));
        } catch (IOException e) {
            throw new StorageException("failed to load data for persistent identifier: " + pid);
        }
    }

    @Override
    public void delete(String pid) throws StorageException {
        File file = getFile(pid);
        if (!file.delete()) {
            log.error("failed to delete object: " + pid);
        }
    }

    /**
     * Returns a file for the provided pid.
     * 
     * @param pid
     *            the pid of the object
     * @return the file
     * @throws StorageException
     *             if the pid is empty or the object could not be found
     */
    private File getFile(String pid) throws StorageException {
        if ((pid == null) || (pid.isEmpty())) {
            throw new StorageException("provided persistent identifier is empty");
        }
        String objectId = pid.substring(pid.indexOf(':') + 1);
        File file = new File(storagePathFile, objectId);
        if (file.exists()) {
            return file;
        } else {
            throw new StorageException("no object found for persistent identifier: " + pid);
        }
    }

    // --------------- getter/setter ---------------
    public String getStoragePath() {
        return storagePath;
    }
}
