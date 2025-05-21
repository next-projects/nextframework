package org.nextframework.resource;

import static org.nextframework.js.NextGlobalJs.next;
import static org.stjs.javascript.Global.exception;
import static org.stjs.javascript.Global.window;
import static org.stjs.javascript.JSCollections.$array;

import org.nextframework.js.ajax.AjaxRequest;
import org.stjs.javascript.Array;
import org.stjs.javascript.Global;
import org.stjs.javascript.JSCollections;
import org.stjs.javascript.JSStringAdapter;
import org.stjs.javascript.dom.DOMEvent;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.dom.Input;
import org.stjs.javascript.dom.Option;
import org.stjs.javascript.dom.Select;
import org.stjs.javascript.functions.Callback1;
import org.stjs.javascript.functions.Function1;

public class NextReload {

	private static class FunctionCall {
		
		String call;
		String beanName;
		String methodName;
		Array<FunctionCallParameter> params;

		public FunctionCall(String call){
			this.call = call;
			this.params = JSCollections.$array();
			parse();
		}

		private void parse() {
			if(call == null){
				throw exception("null call string");
			}
			int sepPoint = call.indexOf('.');
			int sepParenthesis = call.indexOf('(', sepPoint);
			int sepParenthesisClose = call.lastIndexOf(')');
			
			this.beanName = call.substring(0, sepPoint);
			this.methodName = call.substring(sepPoint + 1, sepParenthesis);
			String paramsStr = call.substring(sepParenthesis +1, sepParenthesisClose);
			Array<String> params = JSStringAdapter.split(paramsStr, ",");
			for (String i : params) {
				FunctionCallParameter cp = new FunctionCallParameter(params.$get(i).trim());
				this.params.push(cp);
			}
		}
		
	}
	private static class FunctionCallParameter {
		
		String param;
		
		public FunctionCallParameter(String param) {
			this.param = param;
		}
		
	}
	
	private static class OptionsLoader {
		
		private Array<ParameterReference> parameterReferences;
		private FunctionCall functionCall;
		private Select propertyEl;
		
		public OptionsLoader(String property, FunctionCall functionCall){
			this.propertyEl = (Select) window.document.getElementsByName(property).$get(0);
			this.functionCall = functionCall;
			this.parameterReferences = JSCollections.$array();
			for(String i :functionCall.params){
				parameterReferences.push(new ParameterReference(this, functionCall.params.$get(i)));
			}
		}
		
		public OptionsLoader setParameterOptional(String parameterName, boolean optional){
			for (String i : parameterReferences) {
				if(parameterReferences.$get(i).callParameter.param == parameterName){
					parameterReferences.$get(i).setOptional(optional);
					return this;
				}
			}
			return this;
		}
		
		public void refOnChange(ParameterReference parameterReference) {
			checkOptions();
		}

		public void checkOptions() {
			for (String i : parameterReferences) {
				if(parameterReferences.$get(i).getParameterValue() == null
						&& !parameterReferences.$get(i).isOptional()){
					cleanOptions(true);
					return;
				}
			}
			loadOptions();
		}
		
		private void loadOptions() {
			final Array<String> selectedValues = getSelectedValues();
			
			String sParam = "";
			for (String i : parameterReferences) {
				String parameterValue = parameterReferences.$get(i).getParameterValue();
				if(parameterValue == null){
					sParam += "<null>";
				} else {
					sParam += parameterValue;
				}
				sParam += "#";
			}
			
			String sTypes = "";
			for (String i : parameterReferences) {
				String parameterType = parameterReferences.$get(i).getParameterType();
				if(parameterType == null){
					sTypes += "java.lang.Void"; //represents an unknown type
				} else {
					sTypes += parameterType;
				}
				sTypes += ";";
			}

//			'parentValue='+parentValue + '&' +
//			'label='+label + '&' +
//			'type='+type + '&' +
//			'loadFunction='+loadfunction + '&' +
//			'listaParametros='+listaParametros + '&' +
//			'listaClasses='+listaClasses
			final OptionsLoader bigThis = this;
			AjaxRequest req = next.ajax.newRequest()
					.setUrl("/ajax/combo")
					.setParameter("loadFunction", functionCall.call)
					.setParameter("parameterList", sParam)
					.setParameter("classesList", sTypes);
			req.setOnComplete(new Callback1<String>() {
				@Override
				public void $invoke(String p1) {
					Array<Array<String>> lista = null;
					Global.eval(p1);
					bigThis.setOptions(lista, selectedValues);
				}
			});
			req.send();
		}

		protected void setOptions(Array<Array<String>> list, Array<String> selectedValues) {
			cleanOptions(false);
			int selectCount = 0;
			for (String i : list) {
				Array<String> item = list.$get(i);
				Option option = new Option(item.$get(1), item.$get(0));
				propertyEl.add(option);
				if(selectedValues.indexOf(item.$get(0)) >= 0){
					selectCount++;
					option.selected = true;
				}
			}
			if(selectCount != selectedValues.$length()){
				next.events.dispatchEvent(propertyEl, "change");
			}
		}

		public Array<String> getSelectedValues() {
			Array<String> selectedValues = $array();
			for(int i = 0; i < propertyEl.options.length; i++){
				Option op = propertyEl.options.$get(i);
				if(op.selected){
					selectedValues.push(op.value);
				}
			}
			return selectedValues;
		}

		private void cleanOptions(boolean refreshOnValueChange) {
			boolean refresh = false;
			if(getSelectedValues().$length() > 0){
				refresh = true;
			}
			while(propertyEl.options.length > 0){
				propertyEl.options.remove(propertyEl.options.length -1);
			}
			String includeBlank = propertyEl.getAttribute("data-includeblank");
			if(includeBlank == null || includeBlank.equals("true")){
				String blankLabel = propertyEl.getAttribute("data-blanklabel");
				if(blankLabel == null){
					blankLabel = "";
				}
				propertyEl.add(new Option(blankLabel, "<null>"));
				propertyEl.selectedIndex = 0;
			}
			if(refresh && refreshOnValueChange){
				next.events.dispatchEvent(propertyEl, "change");
			}
		}

		private static class ParameterReference {
			
			private FunctionCallParameter callParameter;
			private OptionsLoader loader;
			private Element domElementRef;
			private Function1<?, ?> attachedEvent;
			private boolean optional;

			public ParameterReference(OptionsLoader loader, FunctionCallParameter callParameter){
				this.optional = false;
				this.callParameter = callParameter;
				this.loader = loader;
				this.domElementRef = getDomElementRef();
				if(this.domElementRef != null){
					final ParameterReference bigThis = this;
					this.attachedEvent = next.events.attachEvent(domElementRef, "change", new Callback1<DOMEvent>() {
						public void $invoke(DOMEvent p1) {
							bigThis.refOnChange();
						}
					});
				}
			}
			
			public void setOptional(boolean optional) {
				this.optional = optional;
			}
			
			public boolean isOptional() {
				return optional;
			}

			public String getParameterType() {
				if(domElementRef != null){
					return domElementRef.getAttribute("data-rawType");
				}
				return null;
			}

			public String getParameterValue(){
				String value = null;
				if(domElementRef.tagName.toLowerCase().equals("select")){
					Select select = (Select) domElementRef;
					if(select.selectedIndex >= 0){
						value = select.options.$get(select.selectedIndex).value;
					}
				} else {
					Input input = (Input) domElementRef;
					value = input.value;
				}
				if(value != null && (value.trim().equals("") || value.equals("<null>"))){
					value = null;
				}
				return value;
			}
			
			protected void detach(){
				next.events.detachEvent(domElementRef, "change", attachedEvent);
			}
			
			protected void refOnChange() {
				loader.refOnChange(this);
			}

			private Element getDomElementRef(){
				//TODO verify indexed property
//				int i = loader.property.lastIndexOf(']');
//				String baseProperty = "";
//				if(i > 0){
//					//is indexed
//					baseProperty = loader.property.substring(0, i + 1);
//				}
				String paramName = callParameter.param;
				return window.document.getElementsByName(paramName).$get(0);
			}
		}
	}
	
	public OptionsLoader configure(String property, String callbackServerString){
		Select propertyInput = next.dom.toElement(property);
		if(propertyInput == null || !propertyInput.tagName.toLowerCase().equals("select")){
			throw exception("Property "+property+" not found or is not a SELECT");
		}
		OptionsLoader loader = new OptionsLoader(property, new FunctionCall(callbackServerString));
		loader.checkOptions();
		return loader;
	}
}
