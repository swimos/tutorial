# Swim Tutorial

Swim is a completely integrated solution for building scalable, end-to-end streaming applications. Instead of requiring separate message brokers, app servers, and databases, Swim applications consist of just two pieces:

- A **Swim server** with *built-in* persistence, messaging, scheduling, clustering, replication, introspection, and security

- A **user interface** that uses Swim's streaming UI frameworks to visualize data from Swim servers in real-time

*Read this in other languages: [简体中文](README.zh-cn.md)*


## Run

* [Install JDK 11+](https://www.oracle.com/technetwork/java/javase/downloads/index.html).
  * Ensure that your `JAVA_HOME` environment variable is pointed to your Java installation location.
  * Ensure that your `PATH` includes `$JAVA_HOME`.

* From a console pointed to the `server` directory, run `./gradlew run` (`.\gradlew.bat run` on Windows) to run the Swim server.

* Then, open `ui/chart.html`, `ui/gauge.html`, and `ui/pie.html` in your browser to see the different real-time UI components.

## How It Works

Visit [server](https://github.com/swimos/tutorial/tree/master/server) to learn how to stand up your data in a Swim server.

Then, visit [ui](https://github.com/swimos/tutorial/tree/master/ui) to learn to visualize this data like never before.
