# CoSpi

CoSpi is a lightweight tool for visualizing datasets in a spiral form.

## Getting Started

Before you procceed, make sure that your machine has the following requirements:
* [Java](https://www.java.com/en/) (>=8) - Core Programming Language
* [Maven](https://maven.apache.org/) - Dependency Management 

## Datasets and Configuration
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
