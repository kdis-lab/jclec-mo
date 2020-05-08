# JCLEC-MO

JCLEC-MO is a Java suite (v8+) that adapts and extends [JCLEC](http://jclec.sourceforge.net/) (Java Class Library for Evolutionary Computation) to solve multi-objective and many-objective optimization problems. It currently implements evolutionary algorithms and particle swarm optimization.

Documentation, examples and other resources are available at the website: **https://www.uco.es/kdis/jclec-mo/**

## Download

The source code included in this repository is provided as an Eclipse project. After downloading it, you only need to import the project and check that the classpath includes the following dependencies:

* JCLEC base (v4.0+)
* datapro4j library core (v1.0+)], the additional module to access to R and JRI (only for some "reporters" and "handlers", please see the documentation)
* Apache Commons libraries: collections (v3.2.2), configuration (v1.10+), lang (v2.6), logging (v1.2+)
* JUnit (v4.12+) and harmcrest-core (v1.3+) (only for test classes)

This libraries are included as jar files in the folder *lib*, which is already set in the classpath. A compiled version is also available for download at the website (see instructions about how to link the dependencies).

## Execution

JCLEC-MO requires a configuration file in XML format, which specifies the problem to be solved, algorithm to be applied, and its parameter values. Several examples can be found in the folder *cfg*. The file should be specified as the program parameter of the main JCLEC class, *RunExperiment*. You can also create an customized experiment able to run one or more configuration files using the class *MOExperimentRunner*. If you are using the binary jar file and want to execute a configuration file, e.g. NSGA2-DTLZ1, just open a console and type:

```
java -jar jclec-mo.jar NSGA2-DTLZ1.xml
```

## Documentation

* [API](http://www.uco.es/grupos/kdis/jclec-mo/v1/api/)
* [User guide and tutorial](http://www.uco.es/grupos/kdis/jclec-mo/v1/docs/jclec-mo-1.0-user-guide.pdf)
* [Design specification](http://www.uco.es/grupos/kdis/jclec-mo/v1/docs/jclec-mo-1.0-design-specification.pdf)

## Licence

Copyright (c) 2018 The authors.

This software was developed by members of the [Knowledge Discovery and Intelligent Systems](http://www.uco.es/kdis/) Research Group at the University of Córdoba, Spain.

THE SOFTWARE IS PROVIDED “AS IS”, WHITOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED.

The source code can be redistributed or modified under the terms of the GNU Lesser General Public License (GPL3) as published by the Free Software Foundation.

## Citation

If you use JCLEC-MO, please cite the following paper:

A. Ramírez, J.R. Romero, C. García-Martínez, S. Ventura. *JCLEC-MO: a Java suite for solving many-objective optimization engineering problems*. Engineering Applications of Artificial Intelligence, vol. 81, pp. 14-28. 2019. DOI: [10.1016/j.engappai.2019.02.003](https://doi.org/10.1016/j.engappai.2019.02.003)
