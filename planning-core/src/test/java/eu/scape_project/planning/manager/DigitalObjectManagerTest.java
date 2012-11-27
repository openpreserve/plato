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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.InvalidParameterException;
import java.util.Arrays;

import org.junit.Test;

import eu.scape_project.planning.manager.ByteStreamManager;
import eu.scape_project.planning.manager.DigitalObjectManager;
import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.model.ByteStream;
import eu.scape_project.planning.model.DigitalObject;

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
        assertTrue(object.getSizeInBytes() > 0);
    }

    @Test(expected = InvalidParameterException.class)
    public void getCopyOfDataFilledDigitalObject_digitalObjectWithoutPidCausesInvalidParameterException()
        throws StorageException {
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
        object.setSizeInBytes(10.5 * 1024 * 1024);

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
