package eu.scape_project.pw.planning.manager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.security.InvalidParameterException;
import java.util.Arrays;

import javax.persistence.Temporal;

import org.junit.Test;

import eu.planets_project.pp.plato.model.ByteStream;
import eu.planets_project.pp.plato.model.DigitalObject;

public class DigitalObjectManagerTest {

	@Test
	public void moveDataToFileSystem_worksAsExpected() throws StorageException {
		// input object
		DigitalObject object = new DigitalObject();
		String content = "This is a test content";
		ByteStream contentByteStream = new ByteStream();
		contentByteStream.setData(content.getBytes());
		object.setData(contentByteStream);
		
		// mock ByteStreamManager
		ByteStreamManager byteStreamManager = mock(ByteStreamManager.class);
		when(byteStreamManager.store(null, content.getBytes())).thenReturn("myPid");
		
		// execute test
		DigitalObjectManager digitalObjectManager = new DigitalObjectManager();
		digitalObjectManager.setByteStreamManager(byteStreamManager);
		digitalObjectManager.moveDataToStorage(object);
		
		// verify results
		assertNull(object.getData().getData());
		assertEquals("myPid", object.getPid());
		assertTrue(object.getSizeInMB() > 0);
	}
	
	@Test(expected=InvalidParameterException.class)
	public void getCopyOfDataFilledDigitalObject_digitalObjectWithoutPidCausesInvalidParameterException() throws StorageException {
		// input object
		DigitalObject object = new DigitalObject();
		object.setPid("");
		
		// execute test
		DigitalObjectManager digitalObjectManager = new DigitalObjectManager();
		digitalObjectManager.getCopyOfDataFilledDigitalObject(object);
	}
	
	@Test
	public void getCopyOfDataFilledDigitalObject_worksAsExpected() throws StorageException {
		// input object
		DigitalObject object = new DigitalObject();
		object.setPid("pid");
		object.setFullname("test.txt");
		object.setContentType("text");
		object.setSizeInMB(10.5);
		
		// mock ByteStreamManager
		String content = "This is a test content";
		ByteStreamManager byteStreamManager = mock(ByteStreamManager.class);
		when(byteStreamManager.load("pid")).thenReturn(content.getBytes());
		
		// execute test
		DigitalObjectManager digitalObjectManager = new DigitalObjectManager();
		digitalObjectManager.setByteStreamManager(byteStreamManager);
		DigitalObject resultObject = digitalObjectManager.getCopyOfDataFilledDigitalObject(object);
		
		// verify results
		assertFalse(object == resultObject);
		assertTrue(Arrays.equals(content.getBytes(), resultObject.getData().getData()));
		assertEquals("pid", resultObject.getPid());
		assertEquals("test.txt", resultObject.getFullname());
		assertEquals("text", resultObject.getContentType());
		assertTrue(resultObject.getSizeInMB() == 10.5);
	}
}
