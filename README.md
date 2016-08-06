# Next Framework
Next Framework is a full-stack web framework that embraces and enhances existing technologies to provide extended productivity and lighter learning curve. Spring and Hibernate are on Next core, therefore it is familiar to most Java enterprise developers. 

Reference documentation on https://github.com/next-projects/nextframework/wiki

## Quick Start - (3 min)

Go to [Next Framework Site][] and download latest version zip.

Extract `build-install.xml` from the zip file.

Create a web project in your IDE and copy both the zip and install files.

Execute `build-install.xml` as an ANT script.

Your project is configured with next! No tools, no command lines, no plugins are necessary.
Refresh your project to see the new files.

## Next Build

Next comes with ANT scripts ready to use. A `build.xml` is placed at the root of the project. It contains common tasks. 
If you want to use this script file to deploy your application, configure in `build.properties` file. The property `server.deploy` sets where the files must be deployed. 

## Hello World - MVC

To create a controller in next, you should extend `org.nextframework.controller.MultiActionController`, and annotate it with `@Controller` annotation. Methods in controllers (that follows certain conventions) are actions. To define a standard action, use `@DefaultAction` annotation in a method.

	package com.foo.bar;
	
	import org.nextframework.controller.*;
	
	@Controller(path="/public/helloworld")
	public class HelloWorldController extends MultiActionController {
	
		@DefaultAction
		public String index(){
			return "helloPage";
		}
	}

Each controller must be registered in one `module`. The `module` is the first part of the path. `/public` in the example above.

The `index` method returns a String representing the name of the JSP to dispatch the request. The JSPs must be placed at `WEB-INF/jsp/[controllermodule]`. 

In the `WEB-INF/jsp/public` create a `helloPage.jsp` file with the contents:

	<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>
	
	<t:view title="Hello World Page">
	Hello World!!
	</t:view>

Ask `/public/helloworld` in your web browser, and you will see the `Hello World!!` message.

## IVY and library management

Next comes with ivy pre-configured. You don't have to install ivy. Next will detect its absence and automatically download it if necessary. 

The file `/ivy.xml` created on install, already contain some examples of libraries to download. 

Configure your ivy  modules and execute ant task `Retrieve Ivy Dependencies` from the `build.xml` file.

## Dependency Injection and Spring

Next is built on top of Spring Framework. Any Spring configuration will work. Next already pre-configures spring to use annotations. So, you don't have to configure Spring by yourself. `@Service` and `@Autowired` annotations work out of the box. All Next controllers are Spring beans.

## Database Connection and Hibernate

The quickest way to configure a database is to uncomment the HSQLDB dependency on `/ivy.xml` file, and run `Retrieve Ivy dependencies` in `build.xml`. Next automatically detects HSQLDB on the classpath and configures a data source automatically for it.

To define a custom data source, use connection.properties file. Define driver, url, username and password properties. 

When there is a data source available, next configures hibernate. Hibernate annotations can be used. No extra configuration required.

## More

Next yet comes with features like, crud operations support, generic DAOs, java to javascript conversion, a collection of tags, reports and charts API and more. Access [Next Framework Site][] for more information. Or go to [wiki documentation][].



[Next Framework Site]: http://www.nextframework.org
[wiki documentation]: https://github.com/next-projects/nextframework/wiki
