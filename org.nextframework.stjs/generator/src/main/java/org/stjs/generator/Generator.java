/**
 *  Copyright 2011 Alexandru Craciun, Eyal Kaspi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.stjs.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.stjs.generator.scope.CompilationUnitScope;
import org.stjs.generator.scope.ScopeBuilder;
import org.stjs.generator.type.ClassLoaderWrapper;
import org.stjs.generator.type.ClassWrapper;
import org.stjs.generator.utils.ClassUtils;
import org.stjs.generator.visitor.SetParentVisitor;
import org.stjs.generator.writer.JavascriptWriterVisitor;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

/**
 * This class parses a Java source file, launches several visitors and finally generate the corresponding Javascript.
 * 
 * @author acraciun
 * 
 */
public class Generator {

	private static final String STJS_FILE = "stjs.js";

	public File getOutputFile(File generationFolder, String className) {
		File output = new File(generationFolder, className.replace('.', File.separatorChar) + ".js");
		output.getParentFile().mkdirs();
		return output;
	}

	public File getInputFile(File sourceFolder, String className) {
		return new File(sourceFolder, className.replace('.', File.separatorChar) + ".java");
	}

	/**
	 * 
	 * @param builtProjectClassLoader
	 * @param inputFile
	 * @param outputFile
	 * @param configuration
	 * @return the list of imports needed by the generated class
	 */
	public ClassWithJavascript generateJavascript(ClassLoader builtProjectClassLoader, String className,
			File sourceFolder, File generationFolder, File targetFolder, GeneratorConfiguration configuration)
			throws JavascriptGenerationException {

		ClassLoaderWrapper classLoaderWrapper = new ClassLoaderWrapper(builtProjectClassLoader,
				configuration.getAllowedPackages(), configuration.getAllowedJavaLangClasses());
		DependencyResolver dependencyResolver = new GeneratorDependencyResolver(builtProjectClassLoader, sourceFolder,
				generationFolder, targetFolder, configuration);

		ClassWrapper clazz = classLoaderWrapper.loadClass(className).getOrThrow();
		if (ClassUtils.isBridge(clazz.getClazz())) {
			return new BridgeClass(dependencyResolver, clazz.getClazz());
		}

		File inputFile = getInputFile(sourceFolder, className);
		File outputFile = getOutputFile(generationFolder, className);
		GenerationContext context = new GenerationContext(inputFile, configuration);

		CompilationUnit cu = parseAndResolve(classLoaderWrapper, inputFile, context);

		Writer writer = null;

		try {

			// generate the javascript code
			JavascriptWriterVisitor generatorVisitor = new JavascriptWriterVisitor();
			generatorVisitor.visit(cu, context);

			//writer = new FileWriter(outputFile);
			writer = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8);
			writer.write("\uFEFF"); //BOM
			writer.write(generatorVisitor.getGeneratedSource());
			writer.flush();

		} catch (IOException e1) {
			throw new RuntimeException("Could not open output file " + outputFile + ":" + e1, e1);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		// write properties
		STJSClass stjsClass = new STJSClass(dependencyResolver, targetFolder, className);
		stjsClass.setDependencies(classLoaderWrapper.getResolvedClasses());
		stjsClass.setGeneratedJavascriptFile(relative(generationFolder, className));
		stjsClass.store();

		return stjsClass;
	}

	private URI relative(File generationFolder, String className) {
		// FIXME temporary have to remove the full path from the file name.
		// it should be different depending on whether the final artifact is a war or a jar.
		String path = generationFolder.getPath();
		int pos = path.lastIndexOf("target");
		try {
			if (pos >= 0) {
				File file = getOutputFile(new File(path.substring(pos)), className);
				return new URI("file", null, "/" + file.getPath().replace('\\', '/'), null);
			}
			return getOutputFile(generationFolder, className).toURI();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private CompilationUnit parseAndResolve(ClassLoaderWrapper builtProjectClassLoader, File inputFile, GenerationContext context) {

		CompilationUnitScope unitScope = new CompilationUnitScope(builtProjectClassLoader, context);
		CompilationUnit cu = null;
		InputStream in = null;

		try {

			try {
				in = new FileInputStream(inputFile);
			} catch (FileNotFoundException e) {
				throw new JavascriptGenerationException(inputFile, null, e);
			}

			// parse the file
			cu = JavaParser.parse(in);

			// set the parent of each node
			cu.accept(new SetParentVisitor(), context);

			// ASTUtils.dumpXML(cu);

			// 1. read the scope of all declared variables and methods
			ScopeBuilder scopes = new ScopeBuilder(builtProjectClassLoader, context);
			scopes.visit(cu, unitScope);
			// rootScope.dump(" ");

		} catch (ParseException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				// silent
			}

		}

		return cu;
	}

	/**
	 * This method copies the Javascript support file (stjs.js currently) to the desired folder. This method should be
	 * called after the processing of all the files.
	 * 
	 * @param folder
	 */
	public void copyJavascriptSupport(File folder) {
		final InputStream stjs = Thread.currentThread().getContextClassLoader().getResourceAsStream(STJS_FILE);
		if (stjs == null) {
			throw new RuntimeException(STJS_FILE + " is missing from the Generator's classpath");
		}
		File outputFile = new File(folder, STJS_FILE);
		try {
			Files.copy(new InputSupplier<InputStream>() {

				@Override
				public InputStream getInput() throws IOException {
					return stjs;
				}

			}, outputFile);
		} catch (IOException e) {
			throw new RuntimeException("Could not copy the " + STJS_FILE + " file to the folder " + folder, e);
		} finally {
			try {
				stjs.close();
			} catch (IOException e) {
				// silent
			}
		}
	}

	/**
	 * 
	 * this class lazily generates the dependencies
	 * 
	 */
	private class GeneratorDependencyResolver implements DependencyResolver {

		private final ClassLoader builtProjectClassLoader;
		private final File sourceFolder;
		private final File generationFolder;
		private final File targetFolder;
		private final GeneratorConfiguration configuration;

		public GeneratorDependencyResolver(ClassLoader builtProjectClassLoader, File sourceFolder,
				File generationFolder, File targetFolder, GeneratorConfiguration configuration) {
			this.builtProjectClassLoader = builtProjectClassLoader;
			this.sourceFolder = sourceFolder;
			this.targetFolder = targetFolder;
			this.generationFolder = generationFolder;
			this.configuration = configuration;
		}

		@Override
		public ClassWithJavascript resolve(String className) {

			String parentClassName = className;
			int pos = parentClassName.indexOf('$');
			if (pos > 0) {
				parentClassName = parentClassName.substring(0, pos);
			}

			// try first if to see if it's a bridge class
			Class<?> clazz;
			try {
				clazz = builtProjectClassLoader.loadClass(parentClassName);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
			if (ClassUtils.isBridge(clazz)) {
				return new BridgeClass(this, clazz);
			}

			// check if it has already generated
			STJSClass stjsClass = new STJSClass(this, builtProjectClassLoader, parentClassName);
			if (stjsClass.getJavascriptFiles().isEmpty()) {
				if ((generationFolder == null) || (sourceFolder == null) || (targetFolder == null)) {
					throw new IllegalStateException("This resolver assumed that the javascript for the class ["
							+ parentClassName + "] was already generated");
				}
				stjsClass = (STJSClass) generateJavascript(builtProjectClassLoader, parentClassName, sourceFolder,
						generationFolder, targetFolder, configuration);
			}

			return stjsClass;
		}

	}

	/**
	 * This method assumes the javascript code for the given class was already generated
	 * 
	 * @param testClass
	 */
	public ClassWithJavascript getExistingStjsClass(Class<?> testClass) {
		return new GeneratorDependencyResolver(testClass.getClassLoader(), null, null, null, null).resolve(testClass
				.getName());
	}

	public static void main(String[] args) throws URISyntaxException {
		System.out.println(Arrays.toString(args));
		if (args.length == 0) {
			System.out.println("Usage: Generator <class.name> [<allow package>] [<allow package>] .. ");
			return;
		}
		GeneratorConfigurationBuilder builder = new GeneratorConfigurationBuilder();
		for (int i = 1; i < args.length; ++i) {
			builder.allowedPackage(args[i]);
		}
		new Generator().generateJavascript(Thread.currentThread().getContextClassLoader(), args[0], new File(
				"js-lib"), new File("target", "generate-js"), new File("target"), builder.build());
	}

}
