package org.nextframework.view.components;

import java.io.IOException;

import org.nextframework.controller.ServletRequestDataBinderNext;
import org.nextframework.controller.TempFileTokenSupport;
import org.nextframework.exception.NextException;
import org.nextframework.types.File;
import org.nextframework.util.Util;
import org.nextframework.view.DownloadFileServlet;
import org.nextframework.view.TagUtils;
import org.nextframework.view.template.PropertyConfigTag;
import org.nextframework.web.WebUtils;

public class InputTagFileComponent extends InputTagComponent {

	private static final String VAZIO = "-";

	private static final String EXCLUDE_FIELD_SUFIX = ServletRequestDataBinderNext.EXCLUDE_FIELD_SUFIX;
	private static final String TEMP_FILE_TOKEN_SUFFIX = ServletRequestDataBinderNext.TEMP_FILE_TOKEN_SUFFIX;
	private static final String FILE_OBJECT_SUFFIX = ServletRequestDataBinderNext.FILE_OBJECT_SUFFIX;
	private static final String FILE_CLASS_NAME_SUFFIX = "_fileClassName";

	private String removerLabel;
	private String tempFileTokenValue;

	@Override
	public void prepare() {

		removerLabel = getDefaultViewLabel("remover", "Remover");
		tempFileTokenValue = null;

		super.prepare();

		boolean disabled = configureDisabled();
		PropertyConfigTag propertyConfig = inputTag.findParent(PropertyConfigTag.class);
		if (disabled || propertyConfig != null && Boolean.TRUE.equals(propertyConfig.getDisabled())) {
			inputTag.setShowDeleteButton(false);
		}

		if (inputTag.getValue() instanceof File && isNotTransient()) {
			String fileName = getFileName();
			if (!VAZIO.equals(fileName)) {
				File file = (File) inputTag.getValue();
				if (!isPersisted(file)) {
					try {
						tempFileTokenValue = getReusableTempFileToken();
						if (Util.strings.isEmpty(tempFileTokenValue)) {
							tempFileTokenValue = TempFileTokenSupport.createToken();
						}
						TempFileTokenSupport.persist(file, tempFileTokenValue);
					} catch (IOException e) {
						throw new NextException(e);
					}
				}
			}
		}

		if (isAjaxUpload() && Util.strings.isEmpty(getFileClassNameValue())) {
			throw new NextException("Não foi possível determinar a classe concreta do arquivo para o input '" + inputTag.getName() + "'.");
		}

	}

	private boolean isNotTransient() {
		return (inputTag.getTransientFile() == null || !inputTag.getTransientFile());
	}

	private boolean isTransient() {
		return (inputTag.getTransientFile() != null && inputTag.getTransientFile());
	}

	public boolean isAjaxUpload() {
		return Boolean.TRUE.equals(inputTag.getAjaxUpload());
	}

	public String getFileLink() {

		String fileName;
		try {
			fileName = getFileName();
		} catch (Exception e) {
			return "<span style=\"color: red\"><B>Erro ao adquirir nome do arquivo.</B> " + e.getMessage() + "</span>";
		}

		if (isTransient()) {
			return "<span id=\"" + inputTag.getName() + "_div\">" + fileName + "</span>";
		}

		if (fileName.equals(VAZIO)) {
			return "<span id=\"" + inputTag.getName() + "_div\">" + fileName + "</span>";
		} else {

			Long cdfile;
			try {
				cdfile = ((File) inputTag.getValue()).getCdfile();
			} catch (Exception e) {
				return "<span id=\"" + inputTag.getName() + "_div\" style=\"color: red\"><B>Ocorreu um erro ao adquirir o código do arquivo.</B> " + e.getMessage() + "</span>";
			}

			String link;
			if (isPersisted((File) inputTag.getValue())) {
				DownloadFileServlet.addCdfile(inputTag.getRequest().getSession(), cdfile);
				link = inputTag.getRequest().getContextPath() + DownloadFileServlet.DOWNLOAD_FILE_PATH + "/" + cdfile;
			} else if (Util.strings.isNotEmpty(tempFileTokenValue)) {
				DownloadFileServlet.addTempFileToken(inputTag.getRequest().getSession(), tempFileTokenValue);
				link = inputTag.getRequest().getContextPath() + DownloadFileServlet.DOWNLOAD_FILE_PATH + "/" + tempFileTokenValue;
			} else {
				return "<span id=\"" + inputTag.getName() + "_div\">" + fileName + "</span>";
			}

			//Verifica URL Sufix
			link = WebUtils.rewriteUrl(link); //tinha um segundo parametro '&' aqui

			return "<a href=\"" + link + "\">" + "<span id=\"" + inputTag.getName() + "_div\">" + fileName + "</span>" + "</a>";
		}

	}

	public String getShowRemoverBtn() {
		if (inputTag.getValue() instanceof File) {
			String name2;
			try {
				name2 = ((File) inputTag.getValue()).getName();
			} catch (Exception e) {
				return "style=\"color: red\"";
			}
			if (Util.strings.isEmpty(name2)) {
				return "style=\"display: none\"";
			} else {
				return "";
			}
		}
		return "style=\"display: none\"";
	}

	// utilizado em file
	public String getFileName() {
		if (inputTag.getValue() instanceof File) {
			String name2 = ((File) inputTag.getValue()).getName();
			if (Util.strings.isEmpty(name2)) {
				return VAZIO;
			} else {
				return TagUtils.escape(name2);
			}
		}
		return VAZIO;
	}

	public String getFileOnChange() {
		String complemento = "";
		if (inputTag.isShowDeleteButton()) {
			complemento = "document.getElementById('" + inputTag.getName() + "_removerbtn').style.display = '';";
		}
		String onchangestring = "document.getElementById('" + getExcludeFieldFieldId() + "').value='false'; document.getElementById('" + inputTag.getName() + "_div').style.textDecoration = 'line-through'; " + complemento + " ";
		onchangestring += "try{document.getElementById('" + getTempFileTokenFieldId() + "').value='';}catch(e){} ";
		onchangestring += "try{document.getElementById('" + getFileObjectFieldId() + "').value='';}catch(e){} ";
		if (isAjaxUpload()) {
			onchangestring += "try{resetAjaxUploadProgress(document.getElementById('" + inputTag.getId() + "'));}catch(e){} ";
		}
		String daOnChange = (String) inputTag.getDAAtribute("onChange", true);
		if (daOnChange != null) {
			onchangestring = daOnChange + ";" + onchangestring;
		}
		return onchangestring;
	}

	private boolean isPersisted(File file) {
		return file != null && file.getCdfile() != null && file.getCdfile().longValue() > 0L;
	}

	private String getReusableTempFileToken() {
		String tempFileToken = inputTag.getRequest().getParameter(getTempFileTokenFieldName());
		return TempFileTokenSupport.isValidToken(tempFileToken) ? tempFileToken : null;
	}

	public String getRemoverLabel() {
		return removerLabel;
	}

	public String getTempFileTokenValue() {
		return tempFileTokenValue;
	}

	public String getExcludeFieldFieldId() {
		return inputTag.getId() + EXCLUDE_FIELD_SUFIX;
	}

	public String getExcludeFieldFieldName() {
		return inputTag.getName() + EXCLUDE_FIELD_SUFIX;
	}

	public String getTempFileTokenFieldId() {
		return inputTag.getId() + TEMP_FILE_TOKEN_SUFFIX;
	}

	public String getTempFileTokenFieldName() {
		return inputTag.getName() + TEMP_FILE_TOKEN_SUFFIX;
	}

	public String getFileObjectFieldId() {
		return inputTag.getId() + FILE_OBJECT_SUFFIX;
	}

	public String getFileObjectFieldName() {
		return inputTag.getName() + FILE_OBJECT_SUFFIX;
	}

	public String getFileClassNameFieldId() {
		return inputTag.getId() + FILE_CLASS_NAME_SUFFIX;
	}

	public String getFileClassNameFieldName() {
		return inputTag.getName() + FILE_CLASS_NAME_SUFFIX;
	}

	public String getFileClassNameValue() {
		Class<?> fileClass = resolveFileClass();
		return fileClass != null ? fileClass.getName() : "";
	}

	private Class<?> resolveFileClass() {
		Class<?> fileClass = resolveFileClass(inputTag.getType());
		if (fileClass != null) {
			return fileClass;
		}
		fileClass = resolveFileClass(inputTag.getAutowiredType());
		if (fileClass != null) {
			return fileClass;
		}
		if (inputTag.getValue() instanceof File) {
			return inputTag.getValue().getClass();
		}
		return null;
	}

	private Class<?> resolveFileClass(Object type) {
		if (!(type instanceof Class<?>)) {
			return null;
		}
		Class<?> clazz = (Class<?>) type;
		return File.class.isAssignableFrom(clazz) ? clazz : null;
	}

}
