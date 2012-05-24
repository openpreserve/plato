package eu.scape_project.pw.planning.manager;

import java.io.Serializable;
import java.security.InvalidParameterException;

import javax.inject.Inject;

import eu.scape_project.planning.model.ByteStream;
import eu.scape_project.planning.model.DigitalObject;

/**
 * Class offering services for moving DigitalObject data to the file system and vice versa.
 * 
 * @author Markus Hamm
 */
public class DigitalObjectManager implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ByteStreamManager byteStreamManager;
	
	/**
	 * Method responsible for moving DigitalObject data to file system.
	 * The INPUT object will be MODIFIED the following way: 
	 * - No data is set any more
	 * - Instead the field pid is filled which represents the identifier to fetch data from file system again.
	 * - sizeInMB is set corresponding to the stored data size.
	 * 
	 * @param object DigitalObject with data set, which will be moved to file system.
	 * @throws StorageException If any error occurs at storing the data to file system.  
	 */
	public void moveDataToStorage(DigitalObject object) throws StorageException {
		String pid = byteStreamManager.store(null, object.getData().getData());
		
		// remove the file data - and set the pid and filesize instead
		object.setPid(pid);
		object.setSizeInMB(object.getData().getDataInMB());
		// we must not use Bytestream.setData directly, as it also resets ByteStream.size   
		object.getData().getRealByteStream().setData(null);
	}
	
	/**
	 * Method responsible for retrieving a copy of the DigitalObject filled with data (fetched from file system).
	 * (A copy of the DigitalObject is filled with the data instead of the original one because usually the passed DigitalObject is part
	 * of an objective tree which is stored over a long time period in file system. To avoid high memory usage it is better to
	 * charge an independent object with this usually big amount of data.)
	 * 
	 * @param object DigitalObject with pid set, where data if of interest.
	 * @return A copy of the DigitalObject filled with data.
	 * @throws StorageException If any error occurs at retrieving the data from file system. 
	 */
	// FIXME rename
	public DigitalObject getCopyOfDataFilledDigitalObject(DigitalObject object) throws StorageException {
		// parameter check
		if ((object.getPid() == null) || (object.getPid().equals(""))) {
			throw new InvalidParameterException("DigitalObject must have a pid set to be retrievable from storage");
		}
		
		byte[] digitalObjectBytes = byteStreamManager.load(object.getPid());
		ByteStream digitalObjectByteStream = new ByteStream();
		digitalObjectByteStream.setData(digitalObjectBytes);

		
		// return a new digital object instance - because otherwise the now retrieved bytestream would be put into the current object tree (originating from plan)
		// which would fill up the memory. Because we do not want this - we create a standalone DigitalObject which is cleaned up by garbage collector after its usage.
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
