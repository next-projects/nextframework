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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

/**
 * Main class for compiling source code
 * 
 * @author rogelgarcia
 */
public class JavaSourceCompiler {
	
	private static List<JavaFileObject> compiledFiles = new ArrayList<JavaFileObject>();

	/**
	 * Compiles a source code, load and return the compiled class. <BR>
	 * The class is compiled in memory, no disk IO is done.<BR>
	 * The class will be loaded by the class loader passed as parameter.<BR> 
	 * If the class loader already contains a class with the same name, the existing class will be returned.
	 * 
	 */
	  public static synchronized Class<?> compileClass(ClassLoader classLoader, String className, byte[] source) throws InstantiationException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		try {
			//verificar se a classe já está compilada e carregada
			Class<?> class1 = classLoader.loadClass(className);
			return class1;
		} catch (Exception e) {}
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null);

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		JavaFileObject file = new JavaSourceFromString(className, new String(source));

		List<JavaFileObject> compilationUnits = new ArrayList<JavaFileObject>(Arrays.asList(file));
		
		MemoryJavaOutputFileManager memoryManager = new MemoryJavaOutputFileManager(standardFileManager, compiledFiles);
		
		List<String> options = new ArrayList<String>();
		options.add("-classpath");
		StringBuilder sb = new StringBuilder();
		URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
		for (URL url : urlClassLoader.getURLs()){
			sb.append(url.getFile().replace("%20", " ")).append(File.pathSeparator);
		}
		options.add(sb.toString());
		
		CompilationTask task = compiler.getTask(null, memoryManager, diagnostics, options, null, compilationUnits);

		if(!task.call()){
			String errorMessage = "";
			errorMessage += "\n\nCould not compile "+className+"\n";
			for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
				errorMessage += diagnostic.getMessage(null)+"\n\n";
				//System.err.println(diagnostic.getCode());
				//System.err.println(diagnostic.getPosition());
				//System.err.println(diagnostic.getStartPosition());
				//System.err.println(diagnostic.getEndPosition());
				if(diagnostic.getSource() != null){
					String charContent = (String) ((JavaSourceFromString)diagnostic.getSource()).getCharContent(false);
					int begin = charContent.substring(0, (int) diagnostic.getStartPosition()).lastIndexOf('\n') +1;
					int end = charContent.indexOf('\n', (int) diagnostic.getEndPosition());
					if(end < 0){
						end = charContent.length();
					}
					CharSequence surroundCode = charContent.subSequence(Math.max(0, begin), end);
					errorMessage += diagnostic.getKind()+ " in line: "+surroundCode+"\n";
					int position = (int) (diagnostic.getStartPosition() - begin) + "Error in line: ".length();
					for (int i = 0; i < position; i++) {
						errorMessage +=" ";
					}
					errorMessage += "^\n";
				} else {
					errorMessage += "\n"+diagnostic;
				}
			}
			System.out.println(new String(source));
			throw new RuntimeException(errorMessage);
		}
		try {
			for (MemoryJavaOutputFileObject javaFileObject : memoryManager.getOutputs()) {
				compiledFiles.add(javaFileObject);
				byte[] byteArray = javaFileObject.toByteArray();

				Method defineClass;
				defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
				defineClass.setAccessible(true);
				defineClass.invoke(classLoader, javaFileObject.getClassName(), byteArray, 0, byteArray.length);
			}
			return classLoader.loadClass(className);
		} catch (SecurityException e) {
			throw e;
		} catch (NoSuchMethodException e) {
			throw e;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw e;
		}
	}

	static class JavaSourceFromString extends SimpleJavaFileObject {
		final String code;

		JavaSourceFromString(String name, String code) {
			super(URI.create(name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return code;
		}
	}
}
