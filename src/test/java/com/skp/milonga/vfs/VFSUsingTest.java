/*
 * Copyright 2014 K.M. Kim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skp.milonga.vfs;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.junit.Test;

public class VFSUsingTest {
	
	@Test
	public void monitoringFile() throws Exception {
		String fileLocation = VFSUsingTest.class.getResource(".").getPath(); 
		
		FileSystemManager fsManager = VFS.getManager();
		FileObject listenDir = fsManager.resolveFile(fileLocation);
		
		DefaultFileMonitor fileMonitor = new DefaultFileMonitor(new FileListener() {
			
			@Override
			public void fileDeleted(FileChangeEvent event) throws Exception {
				System.out.println(event.getFile().getName() + " is deleted.");
			}
			
			@Override
			public void fileCreated(FileChangeEvent event) throws Exception {
				System.out.println(event.getFile().getName() + " is created.");
			}
			
			@Override
			public void fileChanged(FileChangeEvent event) throws Exception {
				System.out.println(event.getFile().getName() + " is changed.");
			}
		});
		
		fileMonitor.setRecursive(true);
		fileMonitor.addFile(listenDir);
		fileMonitor.start();
		
		FileWriter writer = new FileWriter(fileLocation + "/test.txt");
		writer.write("abc");
		writer.flush();
		writer.close();
		
		Thread.sleep(10000);
		
		File fileToWrite = new File(fileLocation + "/test.txt");
		fileToWrite.delete();
	}

}
