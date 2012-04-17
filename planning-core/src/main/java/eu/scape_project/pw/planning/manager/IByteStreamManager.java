package eu.scape_project.pw.planning.manager;

import java.io.File;

import eu.planets_project.pp.plato.model.DigitalObject;

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
     * Stores the bytestream of the given digital object, with the pid 
     * - If the pid is not set, a new pid is generated and set to the digital object.
     * - else the pid is used to update the bytestream in the storage
     *    
     * @param o
     * @return pid of the stored bytestream
     * @throws StorageException
     */
    public String store(DigitalObject o) throws StorageException;	
	
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
	 * Loads the bytestream of the given digital object.
	 * 
	 * Note: sets the bytestream in the digital object, and returns it too
	 * 
	 * @param o
	 * @return the loaded bytestream
	 * @throws StorageException
	 */
	public byte[] load(DigitalObject o) throws StorageException; 
	
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
