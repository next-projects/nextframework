package org.nextframework.view.ajax;

import java.io.Serializable;

import org.nextframework.core.web.NextWeb;

public abstract class Callback implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public String getScript(String... properties){
		String extraProperties = "";
		if(properties != null){
			for (int i = 0; i < properties.length; i++) {
				if(properties[i] != null){
					extraProperties += ", "+properties[i];
				}
			}
		}
		int index = register();
		
		return "next.ajax.send({" +
					"url:'" +getServerUrl() +"', " +
					"params: 'serverId="+index+"', " +
					"evalResponse:true, " +
					"appendContext:false " +
					extraProperties+"});";
	}

	public String getServerUrl() {
		String app = NextWeb.getRequestContext().getContextPath();
		return app+"/ajax/callbacksupport";
	}

	public int register() {
		return AjaxCallbackSupport.registerAjax(this);
	}
	
	public int registerSingleton() {
		return AjaxCallbackSupport.registerSingletonAjax(this);
	}

	public abstract String doAjax() throws Exception;
	
	public void unregister(){
		AjaxCallbackSupport.unregister();
	}
}
