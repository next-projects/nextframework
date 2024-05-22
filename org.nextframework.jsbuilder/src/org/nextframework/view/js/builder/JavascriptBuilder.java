package org.nextframework.view.js.builder;

import org.nextframework.view.js.api.Core;
import org.nextframework.view.js.api.Google;
import org.nextframework.view.js.api.GoogleVisualization;
import org.nextframework.view.js.api.Html;

public abstract class JavascriptBuilder implements Core {

	public abstract void build();

	@Override
	public String toString() {
		JavascriptBuilderContext.pushNewContext();
		build();
		String result = JavascriptBuilderContext.getContext().toString();
		JavascriptBuilderContext.popContext();
		return result;
	}

	protected Array array(Object... objs) {
		return new Array(objs);
	}

	protected Map map(Object... props) {
		return new Map(props);
	}

	public static void main(String[] args) {

		abstract class MyBuilder extends JavascriptBuilder implements Google, Html, GoogleVisualization {
		}

		JavascriptBuilder b = new MyBuilder() {

			public void build() {

				google.load("visualization", "1", map("packages", array("corechart")));
				//google.setOnLoadCallback(function);

				Function function = new Function(new JavascriptBuilder() {

					public void build() {

						//Reference data = create("data", "google.visualization.DataTable");
						//final Obj data = new Obj("data", "google.visualization.DataTable");
						final DataTable data = new DataTable();
						data.addColumn("string", "Year");
						data.addColumn("number", "Sales");
						data.addColumn("number", "Expenses");
						data.addRows(new Object[][] {
								{ "2004", 1000, 400 },
								{ "2005", 1170, 460 },
								{ "2006", 660, 1120 },
								{ "2007", 1030, 540 },
						});

						// new Ref("document.getElementById('chart_div')")

						//Obj chart = new Obj("chart", "google.visualization.AreaChart", document.getElementById("chart_div"));
						Chart chart = new AreaChart(document.getElementById("chart_div"));
						chart.draw(data, map(
								"width", 400,
								"height", 200,
								"title", "Company Performance",
								"hAxis", map("title", "Year",
										"titleTextStyle", map("color", "#FF0000"))));
						/*chart.call("draw", map(
								"width", 400, 
								"height", 200, 
								"title", "Company Performance", 
								"hAxis", map(	"title", "Year", 
										"titleTextStyle", map("color", "#FF0000"))));*/
					}

				});

				google.setOnLoadCallback(function);

			}

		};
		System.out.println(b);
	}

	/*
	private List<Object> script = new ArrayList<Object>();
	
	public List<Object> getScript() {
		return script;
	}
	
	
	public class Reference extends JavascriptObjectReference {
	
		public Reference(String variable, JavascriptInstance instance) {
			super(variable, instance);
		}
	
		public Reference(String variable, String className, Object... parameters) {
			super(variable, className, parameters);
		}
	
		public Reference(String variable) {
			super(variable);
		}
		
		public String call(String function, Object... parameters) {
			String call = super.call(function, parameters);
			getScript().add(call);
			return call;
		}
		
	}
	
	protected class Map extends JavascriptMap {
	
		public Map() {
			super();
		}
	
		public Map(java.util.Map<String, Object> map) {
			super(map);
		}
	
		public Map(Object... props) {
			super(props);
		}
		
	}
	protected class Array extends JavascriptArray {
	
		public Array(boolean inline, Object... objects) {
			super(inline, objects);
		}
	
		public Array(Object... objects) {
			super(objects);
		}
		
	}
	
	protected class Function extends JavascriptFunctionReference {
		public Function(String name, JavascriptBuilder builder) {
			super(name, new JavascriptCode(builder.toString()));
		}
	}
	
	
	protected class DocumentReference extends Reference {
	
		public DocumentReference() {
			super("document");
		}
	
		public JavascriptObjectReference getElementById(String id){
			JavascriptObjectReference code = new JavascriptObjectReference("document.getElementById(\""+id+"\")");
			return code;
		}
	}
	
	protected DocumentReference document = new DocumentReference();
	
	protected Map map(Object... props) {
		return new Map(props);
	}
	
	protected Reference reference(String var) {
		return new Reference(var);
	}
	
	protected Reference create(String var, String className) {
		Reference reference = new Reference(var, className);
		getScript().add(reference);
		return reference;
	}
	protected Reference create(String var, String className, JavascriptInstance instance) {
		Reference reference = new Reference(var, className, instance);
		getScript().add(reference);
		return reference;
	}
	protected Reference create(String var, String className, Object...parameters) {
		Reference reference = new Reference(var, className, parameters);
		getScript().add(reference);
		return reference;
	}
	
	protected Array array(Object... objs) {
		return new Array(objs);
	}
	
	protected void function(String string, JavascriptBuilder javascriptBuilder) {
		JavascriptCode code = new JavascriptCode();
		code.append(javascriptBuilder.toString().replace("\n", "\n    "));
		JavascriptFunctionReference ref = new JavascriptFunctionReference(string, code);
		getScript().add(ref);
	}
	@Override
	public String toString() {
		build();
		return Util.collections.join(script, "\n");
	}
	
	public static void main(String[] args) {
		JavascriptBuilder script = new JavascriptBuilder() {
			public void build() {
				Reference google = reference("google");
				
				google.call("load", "visualization", "1", map("packages", array("corechart")));
				
				function("drawChart", new JavascriptBuilder(){ public void build() {
					Reference data = create("data", "google.visualization.DataTable");
					data.call("addColumn", "string", "Year");
					data.call("addColumn", "number", "Sales");
					data.call("addColumn", "number", "Expenses");
					data.call("addRows", array(
							array("2004", 1000, 400),
							array("2005", 1170, 460),
							array("2005", 660, 1120),
							array("2005", 1030, 540)
					));
					//var chart = new google.visualization.AreaChart(document.getElementById('chart_div'));
					Reference chart = create("chart", "google.visualization.AreaChart", reference("document.getElementById('chart_div')"));
					chart.call("draw", map(
							"width", 400, 
							"height", 200, 
							"title", "Company Performance", 
							"hAxis", map(	"title", "Year", 
											"titleTextStyle", map("color", "#FF0000"))));
				}});
			}
		};
		System.out.println(script);
	}
	
	*/
}
