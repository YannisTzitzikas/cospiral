# CoSpi

[CoSpi](http://users.ics.forth.gr/tzitzik/demos/cospi/) is a lightweight tool for visualizing datasets in a spiral form.

## Getting Started

Before you procceed, make sure that your machine has the following requirements:
* [Java](https://www.java.com/en/) (>=8) - Core Programming Language
* [Maven](https://maven.apache.org/) - Dependency Management 

## Input Dataset

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

#### Classic Visualization
``` java
int namesColumn = 0;
int valuesColumn = 1;
boolean createSVG = true;

cospi.visualizePieChart(conf, valuesColumn, namesColumn, createSVG);
```

#### Pie Chart Visualization
``` java
int namesColumn = 0;
int valuesColumn = 1;
int groupbyColumn = 2;
boolean createSVG = true;

cospi.visualizePieChart(conf, valuesColumn, namesColumn, groupbyColumn, createSVG);
```

### Buildin Examples
``` java
cospi.clusteredLOD();
cospi.clusteredLODUniform();
```

We provide plenty of examples and use cases in the corresponding example class.

## CoSpi GUI Application
We have developed a GUI environment to ease the use of CoSpi, which is included on this repository. To use it, run the CoSpiGUI java file.

