Milonga 
=========

What is Milonga?
-----------------

**Javascript dancing with Spring MVC**

Milonga is library for helping developers writing javascript code to make Spring MVC controllers.

To take advantage of Javascript features such as dynamic typing, closures and JSON expressions, you can write Spring MVC controllers simple and easy.

Milonga is based on Rhino, which is an open-source implementation of Javascript written entirely in Java. Milonga controllers are compiled to Java bytecode and executed on JVM where Spring MVC application is executed. Therefore Milonga controllers can be used with existing Spring MVC controllers on the same JVM. For example, you can write Javascript code for some Spring MVC controllers.

![alt text](.wiki/images/simple_architecture.png "Milonga Overview")

Milonga is Simple
-----------------

Milonga Controller Example

	Atmos.handler('/user/info', function() {
		var userInfo = {
						'id' : 'abd@sk.com',
						'firstName' : 'John',
						'lastName' : 'Milonga'};
		return userInfo;
	});

You can define Spring MVC REST application by coding Javascript **easy and fast** way.

Easy to Setup
-------------

Using Maven
    	
	<dependency>
		<groupId>com.skp</groupId>
		<artifactId>milonga</artifactId>
		<version>milonga_version</version>
	</dependency>

Ready to use Milonga by adding dependency to to your build manager.

Check the installation section for more detail. 

Features
--------

* Based on Spring MVC
* Writing Spring MVC handler in Javascript code
* RESTful request dispatching
* Using Rhino internally

Furthermore
-----------

* [Quickstart](./wiki/Quickstart)
* [API](./wiki/API)
