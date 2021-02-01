package APP;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import layoutAlgs.CoSpi;
import layoutAlgs.VisConfig;
import layoutAlgs.params.Axes;
import layoutAlgs.params.Direction;
import layoutAlgs.params.DrawStyle;
import layoutAlgs.params.ExpandStyle;
import layoutAlgs.params.ShapeGaps;
import utils.CSVReader;
import utils.SVGGenerator;
import utils.SyntheticDataGenerator;

/**
 * This class contains examples to create CoSpi layouts. Status: Ongoing just
 * for testing
 * 
 * @author Yannis Tzitzikas (tzitzik@ics.forth.gr)
 * @author Manos Chatzakis (additions - chatzakis@ics.forth.gr)
 *
 */
public class CoSpiExamples {

	/**
	 * Visualize trivial examples
	 * 
	 * @throws InterruptedException
	 */
	public static void indicativeExamples() throws InterruptedException {
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

		/*
		 * SYNTHETIC DATA VISUALIZATIONS EXAMPLES
		 */
		CoSpi.visualize(SyntheticDataGenerator.DecreasingByPercentage(1500, 80, 1, 20), conf, false, true, false, false,
				false);
		TimeUnit.SECONDS.sleep(1);
		CoSpi.clearOldData();

		CoSpi.visualize(SyntheticDataGenerator.PowerLaw(1000, 150, 1.2, 1), conf, false, true, false, false, false);
		TimeUnit.SECONDS.sleep(1);
		CoSpi.clearOldData();

		/*
		 * DATASET VISUALIZATION EXAMPLES
		 */
		// Classic Spiral
		CoSpi.loadDataAndRun("Resources/cities.csv", 1, 0, 3, 40, conf, false, false);
		TimeUnit.SECONDS.sleep(1);
		CoSpi.clearOldData();

		CoSpi.loadDataAndRun("Resources/word_frequency_shakespeare.csv", 1, 0, 1, 50, conf, false, false);
		TimeUnit.SECONDS.sleep(1);
		CoSpi.clearOldData();

		// Pie Chart Spiral
		CoSpi.loadDataAndRunPieChart("Resources/citiesContinents.csv", 1, 2, 0, 10, 40, conf, false, false);
		TimeUnit.SECONDS.sleep(1);
		CoSpi.clearOldData();

		CoSpi.loadDataAndRunPieChart("Resources/companies.csv", 1, 2, 0, 10, 40, conf, false, false);
		TimeUnit.SECONDS.sleep(1);
		CoSpi.clearOldData();
	}

	/**
	 * Clustered LOData visualization examples
	 * 
	 * @throws InterruptedException
	 */
	public static void demoExamples() throws InterruptedException {
		/*
		 * DEMO EXAMPLES
		 */
		CoSpi.clusteredLODUniform();
		TimeUnit.SECONDS.sleep(1);
		CoSpi.clearOldData();

		CoSpi.clusteredLOD();
		TimeUnit.SECONDS.sleep(1);
		CoSpi.clearOldData();
	}

	/**
	 * Visualizations of millions of objects with time measurements
	 * 
	 * @throws InterruptedException
	 */
	public static void veryBigExamples() throws InterruptedException {
		// Example of creating a config object
		VisConfig conf = new VisConfig();
		conf.setDrawStyle(DrawStyle.Filled); // Filled vs Outline
		conf.setDirection(Direction.Expand); // Expand vs Shink
		conf.setExpandStyle(ExpandStyle.Spiral); // vs Spiral v Ring
		conf.setShapeGaps(ShapeGaps.Normal); // Normal vs Minium
		conf.setRoadSize(1);
		conf.setAngleMin(0);
		conf.setAngleMax(2 * Math.PI); // 2*Math.PI
		conf.setAxes(Axes.NoAxes); // AxesXY
		conf.setLabelParams(false, false, false, Color.black, 1);
		conf.setEnableInfo(true);

		// SOS: First make the Canvas Very Big
		// Set manually: CoSpi.N = 8000;

		// Time start
		long startTime = System.nanoTime();

		// Production of synthetic values (not normalized)
		ArrayList<Integer> syntheticValues = SyntheticDataGenerator.DecreasingByPercentage(10 * 1000 * 1000 + 10, 4000,
				10, 20); // size, maxsize, minsize, decreasePercentage // to be 1 million

		CoSpi.originalValues = syntheticValues; // for keeping the original values (needed for the axes)

		ArrayList<Integer> syntheticValuesNormalized = new ArrayList<>(syntheticValues); // copying the value for
																							// normalize them
		CSVReader.Normalize(syntheticValuesNormalized, 0, 50); // Normalize : min, max

		System.out.println("Normalized Values> Max: " + syntheticValuesNormalized.get(0));
		System.out.println(
				"Normalized Values> Min: " + syntheticValuesNormalized.get(syntheticValuesNormalized.size() - 1));

		/*
		 * CoSpi.visualize(syntheticValuesNormalized ,conf,
		 * false,true,false,false,false); TimeUnit.SECONDS.sleep(1);
		 * CoSpi.clearOldData();
		 */

		CoSpi.visualize(SyntheticDataGenerator.DecreasingByPercentage(1000 * 1000, 4, 0, 30), conf, false, true, false,
				false, false);
		TimeUnit.SECONDS.sleep(1);
		CoSpi.clearOldData();

		// Time end
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		System.out.println("Execution time in nanoseconds: " + timeElapsed);
		System.out.println("Execution time in milliseconds: " + timeElapsed / (1000 * 1000));
		System.out.println("Execution time in seconds: " + timeElapsed / (1000 * 1000 * 1000));

		TimeUnit.SECONDS.sleep(1);
	}

	/**
	 * Value comparison examples
	 * 
	 * @throws InterruptedException
	 */
	public static void comparisonExamples() throws InterruptedException {
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
		conf.setEnableInfo(true);

		// Comparing Distributions from 2 different files
		String[] filenames = { "Resources/earthquakesCrete.csv", "Resources/cities.csv" };
		int[] columns = { 1, 1 };
		CoSpi.loadDataFromFilesAndCompare(filenames, columns, 1, 5, conf, false, false);
		TimeUnit.SECONDS.sleep(1);
		CoSpi.clearOldData();

		// Comparing Numeric Attribute Distributions form the same data file
		String[] fileNumericData = { "Resources/numericdata.csv", "Resources/numericdata.csv",
				"Resources/numericdata.csv", "Resources/numericdata.csv", "Resources/numericdata.csv" };
		int[] columnsPerson = { 0, 1, 2, 3, 4 };
		CoSpi.loadDataFromFilesAndCompare(fileNumericData, columnsPerson, 10, 50, conf, false, false);
		TimeUnit.SECONDS.sleep(1);
		CoSpi.clearOldData();

		// Computing the frequencies and compares them as a piechart: Feb 4, 2020
		int[] columnsPersonData = { 0, 1, 2, 3, 4, 5 };
		CoSpi.loadDataFromSingleFileAndCompareFrequencies("Resources/persons.csv", columnsPersonData, 10, 50, conf,
				false, false);
		TimeUnit.SECONDS.sleep(1);
		CoSpi.clearOldData();
	}

	/**
	 * Examples to create SVG and open them easily
	 */
	public static void runAndOpenSVGExamples() {
		VisConfig conf = new VisConfig();
		conf.setDrawStyle(DrawStyle.Filled);
		conf.setDirection(Direction.Expand);
		conf.setExpandStyle(ExpandStyle.Spiral);
		conf.setShapeGaps(ShapeGaps.Minimum);
		conf.setAngleMin(0);
		conf.setAngleMax(2 * Math.PI);
		conf.setRoadSize(5);
		conf.setAxes(Axes.NoAxes);
		conf.setLabelParams(false, true, true, Color.black, 5);
		CoSpi.loadDataAndRun("Resources/cities.csv", 1, 0, 1, 40, conf, false, true);

		try {
			SVGGenerator.createSVG(conf, CoSpi.rectangles, 5000, CoSpi.pic.image, "C:\\Users\\manos\\Desktop",
					"SVGNew.html");
			Desktop dt = Desktop.getDesktop();
			dt.open(new File("SVG.html"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * Zero min examples
	 * 
	 * @throws InterruptedException
	 */
	public static void zeroMinExamples() throws InterruptedException {
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
		conf.setEnableInfo(true);
		int repetitions = 11;
		int minSize = 1;
		for (int i = 0; i < repetitions; i++) {
			CoSpi.loadDataAndRun("Resources/word_frequency_shakespeare.csv", 1, 0, minSize, 100, conf, false, false);
			TimeUnit.SECONDS.sleep(1);
			minSize = (minSize == 1) ? 0 : 1;
		}
	}

	/**
	 * Select the examples and run
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			indicativeExamples();
			demoExamples();
			// veryBigExamples(); //Canvas size should be changed here.
			// comparisonExamples();
			// runAndOpenSVGExamples(); //filepath in generator should be changed.
			// zeroMinExamples();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
