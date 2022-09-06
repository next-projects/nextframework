package org.nextframework.view.components;

import java.io.IOException;

import org.nextframework.core.config.ViewConfig;
import org.nextframework.exception.NextException;
import org.nextframework.service.ServiceFactory;
import org.nextframework.types.File;
import org.nextframework.util.Util;
import org.nextframework.view.DownloadFileServlet;
import org.nextframework.view.TagUtils;
import org.nextframework.view.template.PropertyConfigTag;
import org.nextframework.web.WebUtils;

public class InputTagFileComponent extends InputTagComponent {

	private static final String VAZIO = "-";

	private String removerLabel;

	@Override
	public void prepare() {

		super.prepare();

		removerLabel = getDefaultViewLabel("remover", "Remover");

		boolean disabled = configureDisabled();
		PropertyConfigTag propertyConfig = inputTag.findParent(PropertyConfigTag.class);
		if (disabled || propertyConfig != null && Boolean.TRUE.equals(propertyConfig.getDisabled())) {
			inputTag.setShowDeleteButton(false);
		}

		if (inputTag.getValue() instanceof File && isNotTransient()) {
			String fileName = getFileName();
			if (!VAZIO.equals(fileName)) {
				Long cdfile = ((File) inputTag.getValue()).getCdfile();
				if (cdfile == null) {//temos um arquivo sem ID com conteúdo
					if (ServiceFactory.getService(ViewConfig.class).isPersistTemporaryFiles()) {
						long tempFileId = DownloadFileServlet.getNewTempFileId();
						try {
							DownloadFileServlet.persist((File) inputTag.getValue(), tempFileId);
							((File) inputTag.getValue()).setCdfile(tempFileId);
						} catch (IOException e) {
							throw new NextException(e);
						}
					}
				}
			}
		}

	}

	private boolean isNotTransient() {
		return (inputTag.getTransientFile() == null || !inputTag.getTransientFile());
	}

	private boolean isTransient() {
		return (inputTag.getTransientFile() != null && inputTag.getTransientFile());
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

			// dar autorizacao para fazer o download do arquivo
			DownloadFileServlet.addCdfile(inputTag.getRequest().getSession(), cdfile);
			String link = inputTag.getRequest().getContextPath() + "/DOWNLOADFILE/" + cdfile;

			//Verifica URL Sufix
			link = WebUtils.rewriteUrl(link); //tinha um segundo parametro '&' aqui

			return cdfile == null ? "[Escolha o arquivo novamente]" : "<a href=\"" + link + "\">" + "<span id=\"" + inputTag.getName() + "_div\">" + fileName + "</span>" + "</a>";
		}

	}

	public String getFileValue() {
		if (inputTag.getValue() instanceof File) {
			String name2 = ((File) inputTag.getValue()).getName();
			Long cdfile = ((File) inputTag.getValue()).getCdfile();
			if (Util.strings.isEmpty(name2) || cdfile != null) {
				return "";
			} else {
				return TagUtils.escape(name2);
			}
		}
		return "";
	}

	public String getShowRemoverBtn() {
		if (inputTag.getValue() instanceof File) {
			String name2;
			Long cdfile = ((File) inputTag.getValue()).getCdfile();
			try {
				name2 = ((File) inputTag.getValue()).getName();
			} catch (Exception e) {
				return "style=\"color: red\"";
			}
			if (Util.strings.isEmpty(name2) || cdfile == null) {
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
		// modificado por pedro em 31/07/07, pois quando seta como false o
		// removerbutton da erro de javascript
		String complemento = "";
		if (inputTag.isShowDeleteButton()) {
			complemento = "document.getElementById('" + inputTag.getName() + "_removerbtn').style.dysplay = '';";
		}
		String onchangestring = "document.getElementById('" + inputTag.getName() + "_excludeField').value='false'; document.getElementById('" + inputTag.getName() + "_div').style.textDecoration = 'line-through'; " + complemento + " ";
		String daOnChange = (String) inputTag.getDAAtribute("onChange", true);
		if (daOnChange != null) {
			onchangestring = daOnChange + ";" + onchangestring;
		}
		return onchangestring;
	}

	public String getRemoverLabel() {
		return removerLabel;
	}

}