/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import cospi.CoSpi;
import cospi.VisConfig;
import cospi.params.Axes;
import cospi.params.Direction;
import cospi.params.DrawStyle;
import cospi.params.ExpandStyle;
import cospi.params.ShapeGaps;
import java.awt.Color;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author manos
 */
public class CoSpiExamples {

    public static void classicCospiExamples() throws IOException {
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
        conf.setAxes(Axes.AxisX);
        conf.setRectColor(Color.red);
        conf.setEnableInfo(true);
        conf.setLabelParams(true, true, true, Color.black, 4);

        new CoSpi("./datasets/DemoDatasets/cities.csv", false).visualizeClassic(conf, 1, 0, false);
    }

    public static void pieChartCospiExamples() throws IOException, CloneNotSupportedException {
        VisConfig conf = new VisConfig();

        conf.setDrawStyle(DrawStyle.Filled);
        conf.setDirection(Direction.Expand);
        conf.setExpandStyle(ExpandStyle.Spiral);
        conf.setShapeGaps(ShapeGaps.Minimum);
        conf.setN(1000);
        conf.setMax(90);
        conf.setMin(40);
        conf.setRoadSize(4);
        conf.setAxes(Axes.AxisX);
        conf.setEnableInfo(false);
        conf.setLabelParams(true, true, true, Color.black, 4);

        new CoSpi("./datasets/DemoDatasets/companies.csv", false).visualizePieChart(conf, 2, 0, 1, false);
    }

    public static void main(String[] args) {
        try {
            //classicCospiExamples();
            pieChartCospiExamples();
        } catch (Exception ex) {
            Logger.getLogger(CoSpiExamples.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
