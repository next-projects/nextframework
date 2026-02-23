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
import java.util.List;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

/**
 * @author rogelgarcia
 */
public class MemoryJavaOutputFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

	private List<MemoryJavaOutputFileObject> outputs = new ArrayList<>();

	private List<JavaFileObject> compiledFiles;

	public List<MemoryJavaOutputFileObject> getOutputs() {
		return outputs;
	}

	public MemoryJavaOutputFileManager(StandardJavaFileManager delegate, List<JavaFileObject> compiledFiles) {
		super(delegate);
		this.compiledFiles = compiledFiles;
	}

	@Override
	public void flush() throws IOException {
		for (MemoryJavaOutputFileObject output : outputs) {
			output.flush();
		}
		super.flush();
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
		MemoryJavaOutputFileObject memoryJavaOutputFileObject = new MemoryJavaOutputFileObject(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), kind, className);
		outputs.add(memoryJavaOutputFileObject);
		return memoryJavaOutputFileObject;
	}

	@Override
	public String inferBinaryName(Location location, JavaFileObject file) {
		if (file instanceof MemoryJavaOutputFileObject) {
			return ((MemoryJavaOutputFileObject) file).getClassName();
		}
		return super.inferBinaryName(location, file);
	}

	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
		ArrayList<JavaFileObject> list = new ArrayList<JavaFileObject>();
		Iterable<JavaFileObject> result = super.list(location, packageName, kinds, recurse);
		for (JavaFileObject javaFileObject : result) {
			list.add(javaFileObject);
		}
		for (JavaFileObject javaFileObject : compiledFiles) {
			if (((MemoryJavaOutputFileObject) javaFileObject).getClassName().startsWith(packageName)) {
				list.add(javaFileObject);
			}
		}
		return list;
	}

}
