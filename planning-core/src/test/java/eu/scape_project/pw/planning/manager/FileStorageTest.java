package eu.scape_project.pw.planning.manager;

import java.io.File;

import org.junit.Test;

import eu.scape_project.planning.manager.FileStorage;

public class FileStorageTest {

	
	@Test 
	public void testInit() {
		FileStorage fs = new FileStorage();
		fs.init();
		System.out.println(fs.getStoragePath());
	}
}
