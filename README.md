# Reference_Stream_Reasoning_2016

This repository contains the references contents in the paper "On measuring performances of C-SPARQL and CQELS" of stream reasoning 2016.

To performe the test, you need:

- The Apache Maven.

- To execute the test of C-SPARQL, you need to install the csparqlengine-0.9.jar in your local maven directory 
(mvn install:install-file -Dfile=<path-to-file> -DgroupId=<group-id> -DartifactId=<artifact-id> -Dversion=<version> -Dpackaging=<packaging>). The  The csparqlengine-0.9.jar can be found in the lib directory. 
 
- The evaluation of CQELS contains the source code of CQELS itself, since I inject some codes into CQELS to obtain the precise execution time.

- To obtain the memory consumption, we recommand to use VisualVM to trace the GC activity.
