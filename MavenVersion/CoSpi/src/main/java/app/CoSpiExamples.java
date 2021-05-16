/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import commonUtils.SyntheticDataGenerator;
import cospi.CoSpi;
import cospi.VisConfig;
import cospi.params.Axes;
import cospi.params.Direction;
import cospi.params.DrawStyle;
import cospi.params.ExpandStyle;
import cospi.params.ShapeGaps;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author manos
 */
public class CoSpiExamples {

    public static void classicCoSpiExamples() throws IOException, InterruptedException {
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

        new CoSpi("./datasets/DemoDatasets/cities.csv", false).visualizeClassic(conf, 1, 0, false);
        TimeUnit.SECONDS.sleep(1);

        conf.setMax(70);
        conf.setMin(1);

        new CoSpi("./datasets/DemoDatasets/word_frequencies_shakespeare.csv", false).visualizeClassic(conf, 1, 0, false);
        TimeUnit.SECONDS.sleep(1);

    }

    public static void pieChartCoSpiExamples() throws IOException, CloneNotSupportedException, InterruptedException {
        VisConfig conf = new VisConfig();

        conf.setDrawStyle(DrawStyle.Filled);
        conf.setDirection(Direction.Expand);
        conf.setExpandStyle(ExpandStyle.Spiral);
        conf.setShapeGaps(ShapeGaps.Minimum);

        conf.setN(1000);
        conf.setMax(90);
        conf.setMin(40);

        conf.setRoadSize(4);

        conf.setAxes(Axes.NoAxes);
        conf.setEnableInfo(false);
        conf.setLabelParams(true, true, true, Color.black, 4);

        new CoSpi("./datasets/DemoDatasets/companies.csv", false).visualizePieChart(conf, 2, 0, 1, false);
        TimeUnit.SECONDS.sleep(1);

        conf.setMax(50);
        conf.setMin(20);

        new CoSpi("./datasets/DemoDatasets/citiesContinents-forPieChart.csv", false).visualizePieChart(conf, 2, 0, 1, false);
        TimeUnit.SECONDS.sleep(1);
    }

    public static void demoCoSpiExamples() throws InterruptedException {
        new CoSpi().clusteredLOD();
        TimeUnit.SECONDS.sleep(1);
        new CoSpi().clusteredLODUniform();
        TimeUnit.SECONDS.sleep(1);
    }

    public static void zeroMinExamples() throws InterruptedException, IOException {
        // Example of creating a config object
        VisConfig conf = new VisConfig();
        conf.setDrawStyle(DrawStyle.Filled); // Filled vs Outline
        conf.setDirection(Direction.Expand); // Expand vs Shink
        conf.setExpandStyle(ExpandStyle.Spiral); // vs Spiral v Ring
        conf.setShapeGaps(ShapeGaps.Normal); // Normal vs Minium
        conf.setRoadSize(5);
        conf.setAngleMin(0);
        conf.setAngleMax(2 * Math.PI); // 2*Math.PI
        conf.setAxes(Axes.NoAxes); // AxesXY
        conf.setLabelParams(false, false, false, Color.black, 1);
        conf.setEnableInfo(false);
        conf.setMin(1);
        conf.setMax(100);
        int repetitions = 11;
        CoSpi cospi = new CoSpi("./datasets/DemoDatasets/word_frequencies_shakespeare.csv", false);
        for (int i = 0; i < repetitions; i++) {
            cospi.visualizeClassic(conf, 1, 0, false);
            TimeUnit.SECONDS.sleep(1);
            conf.setMin((conf.getMin() == 1) ? 0 : 1);
        }

    }

    public static void syntheticCoSpiExamples() throws InterruptedException {
        // Example of creating a config object
        VisConfig conf = new VisConfig();
        conf.setDrawStyle(DrawStyle.Filled); // Filled vs Outline
        conf.setDirection(Direction.Expand); // Expand vs Shink
        conf.setExpandStyle(ExpandStyle.Spiral); // vs Spiral v Ring
        conf.setShapeGaps(ShapeGaps.Normal); // Normal vs Minium
        conf.setRoadSize(5);
        conf.setAngleMin(0);
        conf.setAngleMax(2 * Math.PI); // 2*Math.PI
        conf.setAxes(Axes.NoAxes); // AxesXY
        conf.setLabelParams(false, false, false, Color.black, 4);
        conf.setEnableInfo(false);

        new CoSpi().visualizeClassic(SyntheticDataGenerator.DecreasingByPercentage(1500, 80, 1, 20), conf, true);
        TimeUnit.SECONDS.sleep(1);

        new CoSpi().visualizeClassic(SyntheticDataGenerator.PowerLaw(1000, 150, 1.2, 1), conf, false);
        TimeUnit.SECONDS.sleep(1);
    }

    public static void veryBigCoSpiExamples() throws InterruptedException {
        // Example of creating a config object

        VisConfig conf = new VisConfig();
        conf.setDrawStyle(DrawStyle.Filled); // Filled vs Outline
        conf.setDirection(Direction.Expand); // Expand vs Shink
        conf.setExpandStyle(ExpandStyle.Spiral); // vs Spiral v Ring
        conf.setShapeGaps(ShapeGaps.Normal); // Normal vs Minium

        conf.setRoadSize(1);
        conf.setAngleMin(0);

        conf.setN(8000);

        conf.setAngleMax(2 * Math.PI); // 2*Math.PI
        conf.setAxes(Axes.NoAxes); // AxesXY

        conf.setLabelParams(false, false, false, Color.black, 1);
        conf.setEnableInfo(false);

        // Time start
        long startTime = System.nanoTime();

        // Production of synthetic values (not normalized)
        ArrayList<Integer> syntheticValues = SyntheticDataGenerator.DecreasingByPercentage(10 * 1000 * 1000 + 10, 4000, 10, 20);

        //CoSpi.originalValues = syntheticValues; // for keeping the original values (needed for the axes)
        ArrayList<Integer> syntheticValuesNormalized = new ArrayList<>(syntheticValues); // copying the value for normalize them

        System.out.println("Normalized Values> Max: " + syntheticValuesNormalized.get(0));
        System.out.println("Normalized Values> Min: " + syntheticValuesNormalized.get(syntheticValuesNormalized.size() - 1));

        new CoSpi().visualizeClassic(SyntheticDataGenerator.DecreasingByPercentage(1000 * 1000, 4, 0, 30), conf, false);
        TimeUnit.SECONDS.sleep(1);

        // Time end
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        System.out.println("Execution time in nanoseconds: " + timeElapsed);
        System.out.println("Execution time in milliseconds: " + timeElapsed / (1000 * 1000));
        System.out.println("Execution time in seconds: " + timeElapsed / (1000 * 1000 * 1000));

    }

    public static void main(String[] args) {
        try {
            syntheticCoSpiExamples();
            classicCoSpiExamples();
            pieChartCoSpiExamples();
            demoCoSpiExamples();
            zeroMinExamples();
            //veryBigCoSpiExamples();
        } catch (Exception ex) {
            Logger.getLogger(CoSpiExamples.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
