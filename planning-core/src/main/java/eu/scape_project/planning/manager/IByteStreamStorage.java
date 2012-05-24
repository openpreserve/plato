package eu.scape_project.planning.manager;

import java.io.InputStream;


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
