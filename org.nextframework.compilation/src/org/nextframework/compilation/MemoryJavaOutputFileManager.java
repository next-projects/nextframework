/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2012 the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * You may obtain a copy of the license at
 * 
 *     http://www.gnu.org/copyleft/lesser.html
 * 
 */
package org.nextframework.compilation;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

/**
 * @author rogelgarcia
 */
public class MemoryJavaOutputFileManager implements JavaFileManager {

	JavaFileManager delegate;
	
	List<MemoryJavaOutputFileObject> outputs = new ArrayList<MemoryJavaOutputFileObject>();

	private List<JavaFileObject> compiledFiles;
	
	public List<MemoryJavaOutputFileObject> getOutputs() {
		return outputs;
	}
	
	public MemoryJavaOutputFileManager(JavaFileManager delegate, List<JavaFileObject> compiledFiles) {
		this.delegate = delegate;
		this.compiledFiles = compiledFiles;
	}
	
	public void close() throws IOException {
		delegate.close();
	}

	public void flush() throws IOException {
		for (MemoryJavaOutputFileObject output : outputs) {
			output.flush();
		}
		delegate.flush();
	}

	public ClassLoader getClassLoader(Location location) {
		return delegate.getClassLoader(location);
	}

	public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
		return delegate.getFileForInput(location, packageName, relativeName);
	}

	public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
		return delegate.getFileForOutput(location, packageName, relativeName, sibling);
	}

	public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
		return delegate.getJavaFileForInput(location, className, kind);
	}

	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
		//System.out.println("MemoryJavaOutputFileManager.getJavaFileForOutput("+location+", "+className+", "+kind+", "+sibling+")");
		MemoryJavaOutputFileObject memoryJavaOutputFileObject = new MemoryJavaOutputFileObject(URI.create("string:///" + className.replace('.','/') + Kind.SOURCE.extension),kind, className);
		outputs.add(memoryJavaOutputFileObject);
		return memoryJavaOutputFileObject;
	}

	public boolean handleOption(String current, Iterator<String> remaining) {
		return delegate.handleOption(current, remaining);
	}

	public boolean hasLocation(Location location) {
		return delegate.hasLocation(location);
	}

	public String inferBinaryName(Location location, JavaFileObject file) {
		if(file instanceof MemoryJavaOutputFileObject){
			return ((MemoryJavaOutputFileObject) file).getClassName();
		}
		return delegate.inferBinaryName(location, file);
	}

	public boolean isSameFile(FileObject a, FileObject b) {
		return delegate.isSameFile(a, b);
	}

	public int isSupportedOption(String option) {
		return delegate.isSupportedOption(option);
	}

	public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
		Iterable<JavaFileObject> result = delegate.list(location, packageName, kinds, recurse);
		ArrayList<JavaFileObject> list = new ArrayList<JavaFileObject>();
		for (JavaFileObject javaFileObject : result) {
			list.add(javaFileObject);
		}
		for (JavaFileObject javaFileObject : compiledFiles) {
			if(((MemoryJavaOutputFileObject)javaFileObject).getClassName().startsWith(packageName)){
				list.add(javaFileObject);
			}
		}
		return list;
	}
	
}
