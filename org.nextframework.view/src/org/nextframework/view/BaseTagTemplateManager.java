package org.nextframework.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.ServletContext;

import org.nextframework.exception.NextException;

public class BaseTagTemplateManager {
	
	static Set<String> templates = new HashSet<String>();

	static Map<String, String[]> templateCache = new HashMap<String, String[]>();	

	public byte[] getTemplate(ServletContext servletContext, String template, String nextJarPath) throws IOException {
		byte[] bytesTemplate = null;
		
		//se tiver o jar do next, procurar o arquivo lá dentro
		if(nextJarPath != null){
			String root = servletContext.getRealPath("/");
			//InputStream streamNext = getServletContext().getResourceAsStream(nextJarPath);
			//ZipInputStream zip = new ZipInputStream(streamNext);
			
			try {
				ZipFile zipFile = new ZipFile(root+nextJarPath);
				ZipEntry entry = zipFile.getEntry(template);
				if(entry == null){
					return null;
				}
				InputStream inputStreamTemplate = zipFile.getInputStream(entry);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStreamTemplate);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((int) entry.getSize());
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
				int data = -1;
				while((data = bufferedInputStream.read()) != -1){
					bufferedOutputStream.write(data);
				}
				bufferedInputStream.close();
				bufferedOutputStream.flush();
				bytesTemplate = byteArrayOutputStream.toByteArray();
				bufferedOutputStream.close();
			} catch (IOException e) {
				throw new IOException("Não foi possível copiar o template ("+nextJarPath+") do jar do next para a aplicacao. " + e.getMessage());
			}
		}
		return bytesTemplate;
	}

	protected void writeTemplate(ServletContext servletContext, String template, byte[] bytesTemplate) {
		if(bytesTemplate != null){
			String realPath = servletContext.getRealPath("/WEB-INF/classes");
			int lastSeparator = Math.max(template.lastIndexOf('\\'), template.lastIndexOf('/'));
			String templatePath = template.substring(0, lastSeparator);
			
			File dir = new File(realPath + File.separator + templatePath);
			if(!dir.exists()){
				dir.mkdirs();
			}
			
			File arquivo = new File(realPath + File.separator + template);
			try {
				FileOutputStream outputStream = new FileOutputStream(arquivo);
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
				for (int i = 0; i < bytesTemplate.length; i++) {
					bufferedOutputStream.write(bytesTemplate[i]);
				}
				bufferedOutputStream.flush();
				bufferedOutputStream.close();
			} catch (FileNotFoundException e) {
				BaseTag.log.warn("Arquivo não encontrado "+realPath + File.separator + template+"  "+ e.getMessage());
			} catch (IOException e) {
				BaseTag.log.warn("Não foi possível escrever o arquivo "+realPath + File.separator + template+"  "+ e.getMessage());
			}
		}
	}

	protected List<String> getTagJarPaths(BaseTag baseTag) {
		List<String> nextJarPaths = new ArrayList<String>();
		Set<?> resourcePaths = baseTag.getServletContext().getResourcePaths("/WEB-INF/lib");
		for (Object object : resourcePaths) {
			String resourcePath = object.toString();
			if(resourcePath.endsWith(".jar") && baseTag.isTagFromJar(resourcePath)){
				nextJarPaths.add(resourcePath);
			}
		}
		return nextJarPaths;
	}

	protected void checkTemplate(BaseTag baseTag, String templateName, String suffix) {
		String template = null;
		if(suffix == null){
			template = templateName+".jsp";
		} else {
			template = templateName+"-"+suffix+".jsp";
		}
		if(templates.contains(template)){
			return;
		}
		String url = "/WEB-INF/classes/"+template;
		try {
			ServletContext servletContext = baseTag.getServletContext();
			URL resource = servletContext.getResource(url);
			if(resource == null){
				//copiar do jar do next
				
				
				//verificar kit de compatibilidade de templates NEO
	//				if(writeNeoTckTemplate(template)){
	//					return;
	//				}
				
				//achar o jar do next
				boolean templateFound = false;
				List<String> tagJarPaths = getTagJarPaths(baseTag);
				for (String path : tagJarPaths) {
					byte[] bytesTemplate = getTemplate(servletContext, template, path);
					if(bytesTemplate != null){
						writeTemplate(servletContext, template, bytesTemplate);
						templateFound = true;
						break;
					}
				}
				if(!templateFound){
					throw new NextException("Template for "+template+" not found in paths "+tagJarPaths);
				}
			} else {
				synchronized (templates) {
					templates.add(template);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] getTextFromTemplate(BaseTag baseTag, String template) throws IOException {
		String[] text = templateCache.get(template);
		//cancela o cache
		text = null;
		if(text == null){
			InputStream resourceAsStream = baseTag.getServletContext().getResourceAsStream(template);
			if(resourceAsStream == null){
				throw new IllegalArgumentException("Template "+template+" não encontrado!");
			}
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
			String line = null;
			String[] text2 = {"",""};
			boolean first = true;
			int index = 0;
			while((line = bufferedReader.readLine()) != null){
				if(line.contains("<dobody/>")) {
					String[] split = line.split("<dobody/>");
					if(split.length>0){
						text2[index] += split[0];	
					}
					if(split.length>1){
						text2[index+1] += split[1];	
					}
					index++;
					first = true;
					continue;
				}
				if(!first){
					text2[index] = text2[index] + "\n";
				} else {
					first = false;
				}
				text2[index] = text2[index] + line;
			}
			text = text2;
			templateCache.put(template, text);
		}
		return text;
	}
	
}
