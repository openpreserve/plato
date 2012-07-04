package eu.scape_project.pw.planning.manager;

import java.io.File;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.scape_project.planning.manager.FileStorage;
import eu.scape_project.planning.manager.StorageException;

public class FileStorageTest {

	private static FileStorage fs; 
	
	private static String storagePath; 
	
	@BeforeClass 
	public static void init() {
		fs = new FileStorage();
		fs.init();
		storagePath = fs.getStoragePath();
		
	}
	
	@AfterClass
	public static void destroy() {
		File f = new File(storagePath);
		File[] files = f.listFiles();
		for (File file : files) {
			file.delete();
		}
		f.delete();
	}
	
	@Test 
	public void testInit() {
		String sp = "/home/kresimir/Projects/test";
		
		Assert.assertTrue(storagePath.startsWith(sp));	
		
		File spf = new File(storagePath);
		
		Assert.assertTrue(spf.exists());
		Assert.assertTrue(spf.isDirectory());
		
	}
	
	@Test 
	public void testStore() {
		byte[] array = {0, 1, 2, 3};
		try {
			fs.store("plato:testStore", array);
			File t = new File(storagePath,"testStore");
			Assert.assertTrue(t.exists());
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLoad() {
		byte[] array = {0, 1, 2, 3, 9};
		try {
			fs.store("plato:testLoad", array);
			File t = new File(storagePath,"testLoad");
			Assert.assertTrue(t.exists());
			byte [] tmp = fs.load("plato:testLoad");
			Assert.assertTrue(tmp.length==array.length);
			for (int i=0; i<tmp.length; i++) {
				Assert.assertTrue(tmp[i]==array[i]);
			}
		} catch (StorageException e) {
			e.printStackTrace();
		} 
	}
	
	@Test
	public void testDelete() {
		byte[] array = {0, 1, 2, 3};
		try {
			fs.store("plato:testDelete", array);
			File t1 = new File(storagePath,"testDelete");
			Assert.assertTrue(t1.exists());
			fs.delete("plato:testDelete");
			File t2 = new File(storagePath,"testDelete");
			Assert.assertFalse(t2.exists());
		} catch (StorageException e) {
			e.printStackTrace();
		}

	}
}
