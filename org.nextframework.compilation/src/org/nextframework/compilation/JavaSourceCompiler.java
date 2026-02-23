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
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

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
	public static synchronized Class<?> compileClass(ClassLoader classLoader, String className, byte[] source) throws ClassNotFoundException, InstantiationException {

		try {
			//verificar se a classe já está compilada e carregada
			Class<?> class1 = classLoader.loadClass(className);
			return class1;
		} catch (Exception e) {
		}

		MemoryJavaOutputFileManager memoryManager = null;

		try {

			JavaFileObject file = new JavaSourceFromString(className, new String(source));
			List<JavaFileObject> compilationUnits = new ArrayList<JavaFileObject>(Arrays.asList(file));

			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null);
			memoryManager = new MemoryJavaOutputFileManager(standardFileManager, compiledFiles);

			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

			List<String> options = getCompilerOptions();

			CompilationTask task = compiler.getTask(null, memoryManager, diagnostics, options, null, compilationUnits);

			if (!task.call()) {
				String errorMessage = getErrorMessage(className, diagnostics);
				throw new RuntimeException(errorMessage);
			}

			MemoryClassLoader memoryClassLoader = new MemoryClassLoader(classLoader);
			for (MemoryJavaOutputFileObject javaFileObject : memoryManager.getOutputs()) {
				compiledFiles.add(javaFileObject);
				byte[] byteArray = javaFileObject.toByteArray();
				memoryClassLoader.addClass(javaFileObject.getClassName(), byteArray);
			}

			return memoryClassLoader.loadClass(className);

		} finally {
			if (memoryManager != null) {
				try {
					memoryManager.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

	}

	/**
	 * ClassLoader that can define classes from in-memory bytecode.
	 * Used instead of reflection on ClassLoader.defineClass (blocked since JDK 16+).
	 */
	private static class MemoryClassLoader extends ClassLoader {

		MemoryClassLoader(ClassLoader parent) {
			super(parent);
		}

		Class<?> addClass(String name, byte[] bytes) {
			return defineClass(name, bytes, 0, bytes.length);
		}

	}

	private static List<String> getCompilerOptions() {
		List<String> options = new ArrayList<>();
		options.add("-classpath");
		options.add(System.getProperty("java.class.path"));
		return options;
	}

	private static String getErrorMessage(String className, DiagnosticCollector<JavaFileObject> diagnostics) {
		String errorMessage = "";
		errorMessage += "\n\nCould not compile " + className + "\n";
		for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
			errorMessage += diagnostic.getMessage(null) + "\n\n";
			if (diagnostic.getSource() != null && diagnostic.getStartPosition() > -1) {
				String charContent = (String) ((JavaSourceFromString) diagnostic.getSource()).getCharContent(false);
				int begin = charContent.substring(0, (int) diagnostic.getStartPosition()).lastIndexOf('\n') + 1;
				int end = charContent.indexOf('\n', (int) diagnostic.getEndPosition());
				if (end < 0) {
					end = charContent.length();
				}
				CharSequence surroundCode = charContent.subSequence(Math.max(0, begin), end);
				errorMessage += diagnostic.getKind() + " in line: " + surroundCode + "\n";
				int position = (int) (diagnostic.getStartPosition() - begin) + "Error in line: ".length();
				for (int i = 0; i < position; i++) {
					errorMessage += " ";
				}
				errorMessage += "^\n";
			} else {
				errorMessage += "\n" + diagnostic;
			}
		}
		return errorMessage;
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
