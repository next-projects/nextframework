package org.nextframework.core.config;

import java.util.HashMap;
import java.util.Map;

import org.nextframework.view.InputTagType;

public class DefaultViewConfig implements ViewConfig {

	HashMap<Class<?>, InputTagType> map = new HashMap<Class<?>, InputTagType>();
	
	protected UrlRewriter urlRewriter;
	
	protected boolean persistTemporaryFiles = true;

	public Map<Class<?>, InputTagType> getCustomInputTypes() {
		return map;
	}

	public String getRequiredMarkString() {
		return "&#0149;";
	}

	public boolean isDefaultShowCalendar() {
		return true;
	}

	public boolean isDefaultResizeDatagridColumns() {
		return false;
	}

	@Override
	public boolean isAutoLoadOnView() {
		return true;
	}

	@Override
	public UrlRewriter getUrlRewriter() {
		if(urlRewriter == null){
			urlRewriter = new UrlRewriter() {
				
				public String rewriteUrl(String url) {
					return url;
				}
			};
		}
		return urlRewriter;
	}
	
	@Override
	public boolean isPersistTemporaryFiles() {
		return persistTemporaryFiles;
	}

	public void setPersistTemporaryFiles(boolean persistTemporaryFiles) {
		this.persistTemporaryFiles = persistTemporaryFiles;
	}

	@Override
	public boolean isDefaultHideDatagridWhileLoading() {
		return false;
	}
}
