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

import java.io.File;

import eu.scape_project.planning.model.DigitalObject;

/**
 * Interface for bytestream managers.
 * 
 * A ByteStreamManager allows to:
 *  - CRUD bytestreams - of digital objects or standalone - according to their persistent identifier (pid). 
 *  - retrieve tempfiles for stored bytestreams.  
 * 
 * @author Michael Kraxner
 *
 */
//FIXME: remove methods with DigitalObject
public interface IByteStreamManager{
	
	/**
	 * Stores the bytestream with the given pid.
	 * - If no pid is provided, a new one is created
	 * 
	 * @param pid
	 * @param bytestream
	 * @return The pid identifying the stored bytestream 
	 * @throws StorageException
	 */
	public String store(String pid, byte[] bytestream) throws StorageException;
	
	/**
	 * Loads the bit stream for the given pid
	 * 
	 * @param pid
	 * @return
	 * @throws StorageException TODO
	 */
	public byte[] load(String pid) throws StorageException;
	

	/**
	 * Deletes the bytestream with the given pid
	 * 
	 * @param pid
	 * @throws StorageException
	 */
	public void delete(String pid) throws StorageException;	
	
	/**
	 * Returns a temp-file containing the data of the bytestream with the given pid.
	 * Note:
	 *  - ByteStreamManagers have to take care of deleting the temp files
	 *  - You cannot directly update the data in the store via this file, but have to call {@link #store(String, byte[])} instead.  
	 * @param pid
	 * @return
	 */
	public File getTempFile(String pid);
	
	

}
