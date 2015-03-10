GreenDroid
==========

A tool to detect energy leaks in Android applications

# What the tool does

**GreenDroid** tool can be used to determine the methods of an application that are must likely associated with anomalous energy consumptions. It does that by running a set of tests (typical **Android** tests, written using the **Android** test framework) and collecting the information about the methods invoked, the time each test spent and an estimate of the energy consumed per test. 

Its workflow is the following:

1. Receives the path to the application project and to the test project;
2. Intruments the code of both the projects in order to enable tracing and power consumption measuring;
3. Uses [ant](http://ant.apache.org/) to build the .apk files of both projects;
4. Installs them on the connected device;
5. Runs the tests of the application;
6. When tests are done, it pulls the information out off the device;
7. Runs an algorithm to detect methods associated with anomalous energy consumption;
8. Classify each method analyzed, and generate the result to a JSON file;

The JSON file contains data to be displayed in a sunburst diagram. That diagram can be found [here](http://bl.ocks.org/mbostock/4348373), and a version edited by us can be found [here](https://github.com/greensoftwarelab/GreenDroid/tree/master/auxiliar/sunburst/)

# Current status

The **GreenDroid** tool is currently developed in a way that the input needed (application and test project) are set inside the application code.
We intend to soon include a property/configuration file, where are the variables can be previously set.


