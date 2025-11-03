package org.nextframework.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nextframework.classmanager.ClassManagerFactory;
import org.nextframework.controller.resource.Resource;
import org.nextframework.core.standard.Next;
import org.nextframework.persistence.FileDAO;
import org.nextframework.persistence.PersistenceConfiguration;
import org.nextframework.types.File;
import org.nextframework.util.Util;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class DownloadFileProvider {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Long cdfile;
		try {
			cdfile = extractCdfile(request);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// Checa se há permissão
		if (!checkCdfile(request, cdfile)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		// Obtém o conteúdo
		Resource resource = getResource(request, cdfile);
		if (resource == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		response.setContentType(resource.getContentType());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + resource.getFileName() + "\";");
		//response.setHeader("Last-Modified", );
		if (resource.getSize() >= 0) {
			response.setContentLength((int) resource.getSize());
		}

		response.getOutputStream().write(resource.getContents());
		response.flushBuffer();

	}

	protected boolean checkCdfile(HttpServletRequest request, Long cdfile) {
		return DownloadFileServlet.checkCdfile(request.getSession(), cdfile);
	}

	protected long getLastModified(HttpServletRequest request) {
		try {
			return getLastModified(request, extractCdfile(request));
		} catch (Exception e) {
			return 0;
		}
	}

	protected Long extractCdfile(HttpServletRequest request) throws Exception {
		String requestURI = request.getRequestURI();
		Pattern pattern = Pattern.compile(".+?/(-?[0-9]+)");
		Matcher matcher = pattern.matcher(requestURI);
		if (matcher.find()) {
			return new Long(matcher.group(1));
		} else {
			throw new Exception("URL inválida");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Resource getResource(HttpServletRequest request, Long cdfile) {

		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext()).getAutowireCapableBeanFactory();

		Map beansOfType;
		do {
			beansOfType = defaultListableBeanFactory.getBeansOfType(FileDAO.class);
			if (beansOfType.size() == 0) {
				defaultListableBeanFactory = (DefaultListableBeanFactory) defaultListableBeanFactory.getParentBeanFactory();
			} else {
				break;
			}
		} while (defaultListableBeanFactory != null);

		if (beansOfType.size() == 0) {
			//se nao tem FileDAO.. criar um default se só tiver um tipo de arquivo
			Class<?>[] classes = Util.objects.removeInterfaces(ClassManagerFactory.getClassManager().getAllClassesOfType(File.class));
			if (classes.length == 1) {
				//TODO REFATORAR ESSE CÓDIGO, ELE SE REPETE NO GENERICDAO
				FileDAO<?> fileDAO = new FileDAO(classes[0], true);
				fileDAO.setHibernateTemplate(Next.getObject(HibernateTemplate.class));
//				fileDAO.setJdbcTemplate(Next.getObject(JdbcTemplate.class));
//				fileDAO.setSessionFactory(Next.getObject(SessionFactory.class));
				fileDAO.setPersistenceContext(PersistenceConfiguration.getConfig().getPersistenceContext());
				fileDAO.setTransactionTemplate(Next.getObject(TransactionTemplate.class));
				beansOfType.put("fileDAO", fileDAO);//satisfazemos um FileDAO default
			}
		}

		if (cdfile < 0) {
			File file = load(cdfile);
			return returnFile(file);
		} else if (beansOfType.size() == 1) {
			FileDAO<?> fileDAO = (FileDAO<?>) beansOfType.values().iterator().next();
			Class<?>[] allClassesOfTypeFile = Util.objects.removeInterfaces(ClassManagerFactory.getClassManager().getAllClassesOfType(File.class));
			if (allClassesOfTypeFile.length == 1) {
				File file;
				try {
					file = (File) allClassesOfTypeFile[0].newInstance();
					file.setCdfile(cdfile);
					file = fileDAO.loadWithContents(file);
					return returnFile(file);
				} catch (InstantiationException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}

		throw new RuntimeException("Estenda a classe DownloadFileServlet e sobrescreva o método getResource, ou crie um DAO que extenda FileDAO");
	}

	private Resource returnFile(File file) {
		Resource resource = new Resource(file.getContenttype(), file.getName(), file.getContent());
		Long size = file.getSize();
		if (size != null) {
			resource.setSize(size.intValue());
		}
		return resource;
	}

	protected long getLastModified(HttpServletRequest request, Long cdfile) {
		return -1;
	}

	public static File load(long tempFileId) {
		java.io.File tempFile = new java.io.File(System.getProperty("java.io.tmpdir"), Next.getApplicationName() + "_tempFileObject" + tempFileId + ".next");
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(tempFile));
			Object obj = in.readObject();
			in.close();
			return (File) obj;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
