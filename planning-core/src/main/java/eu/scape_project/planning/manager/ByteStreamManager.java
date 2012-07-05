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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.utils.FileUtils;
import eu.scape_project.planning.utils.OS;

/**
 * A handler for loading and storing bytestreams of a (read: one) plan, it: -
 * uses a {@link IByteStreamStorage } for persisting the bytestreams permanently,
 * - and also takes care of caching bytestreams. Note: - It does NOT take any
 * measures to prevent concurrency issues,ensure thread savety. But this is ok,
 * because : - A plan cannot be loaded and accessed more than once at a time,
 * the ByteStreamManager shares the same (conversation) scope. - Beside
 * different planning-conversations we do not read/store bytestreams with
 * multiple threads.
 * 
 * @author Michael Kraxner
 * 
 */
// FIXME: use cache!
public class ByteStreamManager implements Serializable, IByteStreamManager {

    private static final long serialVersionUID = 7205715730617180554L;

    @Inject
    private Logger log;

    @Inject 
    private IByteStreamStorage storage;
    //private FileStorage storage;
    
    private Map<String, File> tempDigitalObjects = new HashMap<String, File>();

    private File tempDir = null;

    public ByteStreamManager() {
    }
  
    /**
     * 
     * @see IByteStreamManager#store(String, byte[])
     */
    public String store(String pid, byte[] bytestream) throws StorageException {
        if ("".equals(pid)) {
            pid = null;
        }
        pid = storage.store(pid, bytestream);
        // we have also to update the cache!
        try {
            cacheObject(pid, bytestream);
        } catch (IOException e) {
            throw new StorageException("failed to cache object", e);
        }
        return pid;
    }

    public byte[] load(String pid) throws StorageException {
        //try to load it from the cache
    	byte[] data = loadFromCache(pid);
        
    	// if it is not in the cache load it from the storage and cache it 
    	if (data == null) { 
        	data = storage.load(pid);
        	try {
        		cacheObject(pid, data);
        	} catch (IOException e) {
        		throw new StorageException("failed to cache object", e);
        	}
        }
        return data;
    }

    /**
     * 
     * @see IByteStreamManager#getTempFile(String)
     */
    public File getTempFile(String pid) {
        File tmp = tempDigitalObjects.get(pid);
        if (tmp == null) {
            try {
                load(pid);
                tmp = tempDigitalObjects.get(pid);
            } catch (StorageException e) {
                log.error("failed to retrieve object: " + pid, e);
                return null;
            }
        }
        return tmp;
    }

    public void delete(String pid) throws StorageException {
        if ((pid == null) || (pid.isEmpty())) {
            return;
        }
        File f = tempDigitalObjects.remove(pid);
        if (f != null) {
            f.delete();
        }
        storage.delete(pid);
    }

    /**
     * Stores the given bytestream to a tempfile and keeps the handle for later
     * use.
     * 
     * @param pid
     * @param bitstream
     */
    private void cacheObject(String pid, byte[] bitstream) throws IOException {
        String filename = pid;
        String fileExtension = "";
        int bodyEnd = filename.lastIndexOf(".");
        if (bodyEnd >= 0) {
            fileExtension = filename.substring(bodyEnd);
        }

        String tempFileName = tempDir.getAbsolutePath() + System.nanoTime() + fileExtension;
        File tempFile = new File(tempFileName);

        OutputStream fileStream;

        fileStream = new BufferedOutputStream(new FileOutputStream(tempFile));
        fileStream.write(bitstream);
        fileStream.close();

        // queue file for deletion, in case the clean up fails
        tempFile.deleteOnExit();

        // put the temp file in the map
        tempDigitalObjects.put(pid, tempFile);
    }

    /**
     * Loads the bytestream from the cache. 
     * 
     * @param pid
     * @return
     * @throws StorageException 
     */
    private byte[] loadFromCache(String pid) throws StorageException {
    	File tmp = tempDigitalObjects.get(pid);
    	if (tmp==null) {
    		return null;
    	}
    	try {
			return FileUtils.inputStreamToBytes(new FileInputStream(tmp));
		} catch (IOException e) {
			throw new StorageException("failed to load data for persistent identifier: " + pid);
		}
    }
    /**
     * Creates a new temp directory for this ByteStreamManager
     */
    @PostConstruct
    public void init() {
        tempDir = new File(OS.getTmpPath() + "digitalobjects" + System.nanoTime() + File.separator);
        tempDir.mkdir();
        tempDir.deleteOnExit();
    }

    /**
     * Cleanup of tempfiles, and handles to loaded digital objects
     */
    @PreDestroy
    public void destroy() {
        OS.deleteDirectory(tempDir);
        tempDigitalObjects.clear();
    }

}
