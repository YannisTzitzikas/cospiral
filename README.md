# CoSpi

[CoSpi](http://users.ics.forth.gr/tzitzik/demos/cospi/) is a lightweight tool for visualizing datasets in a spiral form.

## Roadmap
The contents of this readme file are listed below:
* [Getting Started](https://github.com/YannisTzitzikas/cospiral/blob/master/README.md#getting-started)
* [Dependencies](https://github.com/YannisTzitzikas/cospiral/blob/master/README.md#dependencies)
* [Input Dataset](https://github.com/YannisTzitzikas/cospiral/blob/master/README.md#input-dataset)
* [Configuration](https://github.com/YannisTzitzikas/cospiral/blob/master/README.md#configuration)
* [Usage](https://github.com/YannisTzitzikas/cospiral/blob/master/README.md#usage)
  * aa
  * bb
  * cc
  * dd
* [Cospi GUI application](https://github.com/YannisTzitzikas/cospiral/blob/master/README.md#cospi-gui-application)
* [Involved People](https://github.com/YannisTzitzikas/cospiral/blob/master/README.md#involved-people)


## Getting Started

Before you procceed, make sure that your machine has the following requirements:
* [Java](https://www.java.com/en/) (>=8) - Core Programming Language
* [Maven](https://maven.apache.org/) - Dependency Management 


## Dependencies

Apart from the libraries used by the exploitation of [Maven](https://maven.apache.org/), shown in pom file, CoSpi uses [CSVEditor](https://github.com/MChatzakis/CSVEditor) to sort (and generally manipulate) the input dataset. 

Note: In case of build errors, you may download and add as dependency the project from the aforementioned repository.


## Input Dataset

By default, CoSpi supports CSV datasets as input. The tool is capaple of sorting the files before the visualization begins, using out [CSVEditor](https://github.com/MChatzakis/CSVEditor) library. The CSV dataset can also have header line, which can be ignored. We provide numerous examples of supported datasets in the corresponding folder.

## Configuration

CoSpi API supports CSV datasets (aka Comma Separated Values) and has many configurable options using VisConfig class:

``` java
VisConfig conf = new VisConfig();

conf.setDrawStyle(DrawStyle.Filled);
conf.setDirection(Direction.Expand);
conf.setExpandStyle(ExpandStyle.Spiral);
conf.setShapeGaps(ShapeGaps.Minimum);
conf.setN(1000);
conf.setMax(50);
conf.setMin(1);
conf.setAngleMin(0);
conf.setAngleMax(2 * Math.PI);
conf.setRoadSize(4);
conf.setAxes(Axes.NoAxes);
conf.setRectColor(Color.red);
conf.setEnableInfo(true);
conf.setLabelParams(true, true, true, Color.black, 4);
```

## Usage
To proceed after the configuration is set, you need to instanciate a CoSpi object:
``` java
String filepath = "path/to/dataset";
boolean hasHeader = false; /*Whether or not input file has header on the first line*/

CoSpi cospi = new CoSpi(filepath, hasHeader);
```

### Classic Visualization
Classic Visualization places the values in the classic CoSpi form.
``` java
int namesColumn = 0;
int valuesColumn = 1;
boolean createSVG = true;

cospi.visualizePieChart(conf, valuesColumn, namesColumn, createSVG);
```

### Pie Chart Visualization
Pie Chart Visualization creates a pie chart, using the CoSpi form separately for every pie.
``` java
int namesColumn = 0;
int valuesColumn = 1;
int groupbyColumn = 2;
boolean createSVG = true;

cospi.visualizePieChart(conf, valuesColumn, namesColumn, groupbyColumn, createSVG);
```

### File Comparison Visualization
File Comparison Visualization creates a visualization based on the values from different files.
``` java
String[] filenames = {"filepath1", "filepath2"};
int[] valueColumns = {1, 1};
int[] nameColumns = {0, 0};
boolean createSVG = false;

cospi.compareFrequencies(filenames, valueColumns, nameColumns, conf, createSVG);
```

### Buildin Visualization
Demo visualization creates a visualization for clustered LOD (Linked Open Data)
``` java
cospi.clusteredLOD();
cospi.clusteredLODUniform();
```

We provide plenty of examples and use cases in the corresponding example class.

## CoSpi GUI Application
We have developed a GUI environment to ease the use of CoSpi, which is included on this repository. To use it, run the CoSpiGUI java file.


## Involved People
- [Yannis Tzitzikas](tzitzik@ics.forth.gr)
- [Manos Chatzakis](chatzakis@ics.forth.gr)


Copyright 2020 FOUNDATION FOR RESEARCH & TECHNOLOGY - HELLAS, All rights reserved. 
