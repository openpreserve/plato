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

import java.io.InputStream;
import java.io.OutputStream;


/**
 * Defines the interface of a bytestream-storage.
 * Note:
 *  - a.t.m. it uses byte arrays to pass the data on 
 *  - we might want to add interfaces using {@link InputStream} {@link OutputStream} so it can also handle large objects
 *      
 * @author Michael Kraxner
 */
public interface IByteStreamStorage {
	
	/**
	 * Stores the bytestream of a DigitalObject
	 * - if <param>pid</param> is undefined, a new pid is generated
	 * - if the pid is already known to the storage, then the binary data is updated(replaced) 
	 * 
	 * @param pid
	 * @param bytestream
	 * 
	 * @return pid of the stored bytestream
	 *  
	 * @throws StorageException 
	 */
	public String store(String pid, byte[] bytestream) throws StorageException;
	
	/**
	 * loads the bytestream for the given pid
	 * 
	 * @param pid
	 * @return
	 * @throws StorageException if pid null or object cannot be found
	 */
	public byte[] load(String pid) throws StorageException;
	
	/**
	 * removes the object with <param>pid</param>
	 * 
	 * @param pid
	 * @throws StorageException if pid null or object cannot be found
	 */
	public void delete(String pid) throws StorageException;
}
