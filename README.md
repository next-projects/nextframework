# Next Framework
Next Framework is a full-stack web framework that embraces existing technologies and enhances them to provide extended productivity and lighter learning curve. In its core there are frameworks like Spring and Hibernate, therefore it is familiar to most Java enterprise developers. 

## Quick Start

Go to [Next Framework Site][] and download latest version zip.

Extract `build-install.xml` from the zip file.

Create a web project in your IDE and copy both the zip and install files.

Execute `build-install.xml` as an ANT script.

Your project is configured with next! No tools, no command lines, no plugins are necessary.
Refresh your project to see the new files.

## Next Build

Next comes with ANT scripts ready to use. A `build.xml` is placed at the root of the project. It contains commons tasks. 
If you want to use this script file to deploy your application, configure in `build.properties` file, the path where the project should be deployed.

## Hello World - MVC

To create a controller in next, you should extend `org.nextframework.controller.MultiActionController`, and anotate it with `@Controller` annotation. Methods in controllers (that follows certain convensions) are actions. To define a standard action, use `@DefaultAction` annotation in a method.

	package com.foo.bar;
	
	import org.nextframework.controller.*;
	
	@Controller(path="/public/helloworld")
	public class IndexController extends MultiActionController {
	
		@DefaultAction
		public String index(){
			return "helloPage";
		}
	}

Every controller must be registered in its own `module`. The `module` is the first part of the path. `/public` in the example above.

The `index` method returns a String representing the name of the JSP to dispatch the request. The JSPs must be placed at `WEB-INF/jsp/[controllermodule]`. 

In the `WEB-INF/jsp/public` create a `helloPage.jsp` file with the contents:

	<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>
	
	<t:view title="Hello World Page">
	Hello World!!
	</t:view>

Ask `/public/helloworld` in your web browser, and you will see the `Hello World!!` message.

## Dependency Injection

Next is built on top of Spring Framework. Any Spring configuration will work. Next already pre-configures spring to use annotations. So, you don't have to configure Spring by yourself. `@Service` and `@Autowired` annotations work out of the box. All controllers are Spring beans.

## IVY and library management

Next commes with ivy preconfigured. You don't have to install ivy. Next will detect its absence and automatically download if necessary. 

File `/ivy.xml` created on install, already contain some examples of libraries to download. 

Place your ivy dependent modules and execute ant task `Retrieve Ivy Dependencies` from the build.xml file.

## Database Connection and Hibernate

The quickest way to configure a database is to uncomment the HSQLDB dependency on `/ivy.xml` file, and run `Retrieve Ivy dependencies`. Next automatically detects HSQLDB on classpath and configures a datasource automatically for it.

To define custom datasource, use connection.properties file. Define driver, url, username and password properties. 

When there is a datasource avaiable, next configures hibernate. Hibernate annotations can be used. No configuration required.

## More

Next yet comes with features like, crud operations support, gereneric DAOs, java to javscript convertion, a collections of tags, reports and charts api and more. Access [Next Framework Site][] for more information.



[Next Framework Site]: http://www.nextframework.org
