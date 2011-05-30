What is Application Markup Language?
====================================

Application Markup Language, or AML, is a simple XML-based language that allows you to easily build a clean, functional application for your mobile device. The AML library implementation is open-source, and doesn’t require any imports from your package to work. AML doesn't require you to know the details of how the mobile device builds its views. It doesn't depend on some extra plugin installed on your device, and it doesn’t run your app code through another service.

AML uses markup from files statically bundled with your application code or dynamically pulled from a web service for both the data and the application design and behavior, allowing rapid design changes and the ability to use the mobile device as a thin client while still using the native UI.

Currently, AML is only partially implemented on the Android platform, but I plan to port it to the iPhone as well as Windows 7 Phone as soon as possible. Yes, this is an ambitious goal.

What is it good for?
====================

AML is not the best solution for every kind of application. It will probably never be good for visually stunning games. However, it is perfect for data-driven mobile implementations of web applications. If you need a mobile version of your product, but you want a real native implementation and not just a mobile website, AML could be the answer.

AML builds each app view dynamically straight from the markup. On Android, this means there is no more need for /res/layout files.

AML builds views on-the-fly as they are needed in your app. It can parse the markup from any string that is valid AML. This means you can request a view, for example, from a web service based on login credentials, and show a view that relies entirely on web application logic—not anything built into your app.

AML will enable your application to evolve at the same speed as your web application. Since it can cache and parse views that are provided remotely by your main web app, you can very easily build new AML-based features right into that code and just let the AML library in your mobile app work with the new information.

Even if you want a regular, mobile-only app, AML is much simpler and cleaner than the equivalent native code required. It isn't possible to do absolutely everything with AML that you can do natively, but it is quite likely that it can do everything you need it do.
Check out the [Examples](http://www.amlcode.com/examples) page for a few snippets (or amlets, as I call them) and screenshots.

How do I use it?
================

The best place to go for information on how to implement AML in your project is the [Examples](http://www.amlcode.com/examples) page and the [Documentation](http://www.amlcode.com/documentation) page on the main website.

The main official AML project website is here: http://www.amlcode.com

AML is definitely still in its infancy, so any input or feedback is welcome! Fork the project and go nuts if you want!
