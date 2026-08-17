package org.nextframework.view;

import java.io.IOException;
import java.util.Map;

import org.nextframework.classmanager.ClassManagerFactory;
import org.nextframework.controller.TempFileTokenSupport;
import org.nextframework.controller.resource.Resource;
import org.nextframework.core.standard.Next;
import org.nextframework.exception.NextException;
import org.nextframework.persistence.FileDAO;
import org.nextframework.persistence.PersistenceConfiguration;
import org.nextframework.types.File;
import org.nextframework.util.Util;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DownloadFileProvider {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String fileReference;
		try {
			fileReference = extractFileReference(request);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		if (TempFileTokenSupport.isValidToken(fileReference)) {

			// Verifica se há permissão
			if (!checkTempFileToken(request, fileReference)) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}

			// Obtém o conteúdo
			Resource resource = getTempFileTokenResource(request, fileReference);
			if (resource == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			writeResource(response, resource);

			return;
		}

		Long cdfile;
		try {
			cdfile = Long.valueOf(fileReference);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// Verifica se há permissão
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

		writeResource(response, resource);

	}

	protected String extractFileReference(HttpServletRequest request) throws Exception {
		String requestURI = request.getRequestURI();
		int lastSlash = requestURI.lastIndexOf('/');
		if (lastSlash < 0 || lastSlash == requestURI.length() - 1) {
			throw new Exception("URL inválida");
		}
		String fileReference = requestURI.substring(lastSlash + 1);
		int sessionSeparatorIndex = fileReference.indexOf(';');
		if (sessionSeparatorIndex >= 0) {
			fileReference = fileReference.substring(0, sessionSeparatorIndex);
		}
		if (fileReference.length() == 0) {
			throw new Exception("URL inválida");
		}
		return fileReference;
	}

	protected boolean checkTempFileToken(HttpServletRequest request, String tempFileToken) {
		return DownloadFileServlet.checkTempFileToken(request.getSession(), tempFileToken);
	}

	protected Resource getTempFileTokenResource(HttpServletRequest request, String tempFileToken) {
		try {
			File file = TempFileTokenSupport.load(tempFileToken);
			return file != null ? returnFile(file) : null;
		} catch (NextException e) {
			return null;
		}
	}

	protected boolean checkCdfile(HttpServletRequest request, Long cdfile) {
		return DownloadFileServlet.checkCdfile(request.getSession(), cdfile);
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
				FileDAO<?> fileDAO = new FileDAO(classes[0], true);
				fileDAO.setHibernateTemplate(Next.getObject(HibernateTemplate.class));
				fileDAO.setPersistenceContext(PersistenceConfiguration.getConfig().getPersistenceContext());
				fileDAO.setTransactionTemplate(Next.getObject(TransactionTemplate.class));
				beansOfType.put("fileDAO", fileDAO);//satisfazemos um FileDAO default
			}
		}

		if (beansOfType.size() == 1) {
			FileDAO<?> fileDAO = (FileDAO<?>) beansOfType.values().iterator().next();
			Class<?>[] allClassesOfTypeFile = Util.objects.removeInterfaces(ClassManagerFactory.getClassManager().getAllClassesOfType(File.class));
			if (allClassesOfTypeFile.length == 1) {
				File file;
				try {
					file = (File) allClassesOfTypeFile[0].getConstructor().newInstance();
					file.setCdfile(cdfile);
					file = fileDAO.loadWithContents(file);
					return returnFile(file);
				} catch (Exception e) {
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

	private void writeResource(HttpServletResponse response, Resource resource) throws IOException {
		response.setContentType(resource.getContentType());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + resource.getFileName() + "\";");
		//response.setHeader("Last-Modified", );
		if (resource.getSize() >= 0) {
			response.setContentLength((int) resource.getSize());
		}
		response.getOutputStream().write(resource.getContents());
		response.flushBuffer();
	}

	protected long getLastModified(HttpServletRequest request) {
		try {
			String fileReference = extractFileReference(request);
			if (TempFileTokenSupport.isValidToken(fileReference)) {
				return getLastModified(request, fileReference);
			}
			return getLastModified(request, Long.valueOf(fileReference));
		} catch (Exception e) {
			return 0;
		}
	}

	protected Long extractCdfile(HttpServletRequest request) throws Exception {
		return Long.valueOf(extractFileReference(request));
	}

	protected long getLastModified(HttpServletRequest request, Long cdfile) {
		return -1;
	}

	protected long getLastModified(HttpServletRequest request, String tempFileToken) {
		return -1;
	}

}
