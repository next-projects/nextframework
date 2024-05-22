package org.stjs.generator;

import java.io.File;

public class QuickGenerator {

	public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
		
//		System.out.println(args[3]);
//		System.out.println("=====================");
//		Thread.sleep(5000);
		//check libraries
		try {
			Class.forName("japa.parser.ParseException");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Error\nRequired libraries (STJS) not found in classpath. \n"
					+ "To download STJS libraries, uncomment stjs block at ivy.xml and execute Ant task 'Retrieve Ivy Dependencies' ");
		}

		String customPackages = args[2];
		String sourceDir = args[0];
		String destDir = args[1];
		String className = extractClassName(sourceDir, args[args.length - 1]);
		System.out.println("Generating... " + className);
		className = className.substring(0, className.length() - ".java".length());
		GeneratorConfigurationBuilder builder = new GeneratorConfigurationBuilder();
		configureAllowedPackages(builder, sourceDir, new File(sourceDir));
		builder.allowedPackage("org.nextframework.resource");
		String[] split = customPackages.substring(1, customPackages.length() - 1).split("[;:]");
		for (String pack : split) {
			builder.allowedPackage(pack);
		}

		Class.forName(className);
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		new Generator().generateJavascript(contextClassLoader, className,
				new File(sourceDir),
				new File(destDir),
				new File("temp"),
				builder.build());
	}

	private static String extractClassName(String sourceDir, String className) {
		className = className.substring(sourceDir.length() + 1).replace(File.separatorChar, '.');
		return className;
	}

	private static void configureAllowedPackages(GeneratorConfigurationBuilder builder, String sourceDir, File file) {
		File[] listFiles = file.listFiles();
		for (File dir : listFiles) {
			if (dir.isDirectory()) {
				String path = dir.getAbsolutePath();
				String packageName = path.substring(sourceDir.length() + 1).replace(File.separatorChar, '.');
				builder.allowedPackage(packageName);
				configureAllowedPackages(builder, sourceDir, dir);
			}
		}
	}

}
