package layoutAlgs;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import layoutAlgs.params.Axes;
import layoutAlgs.params.Direction;
import layoutAlgs.params.DrawStyle;
import layoutAlgs.params.ExpandStyle;
import layoutAlgs.params.ShapeGaps;
import utils.CSVReader;
import utils.Picture;
import utils.Pixel;
import utils.Rectangle;
import utils.SVGGenerator;
import utils.SyntheticDataGenerator;

/**
 * 
 * @author Yannis Tzitzikas (tzitzik@ics.forth.gr) : Algorithm idea and
 *         visualization methods.
 * @author Manos Chatzakis (chatzakis@ics.forth.gr): Labels, GUI application,
 *         SVG generator, CoSpi cleanup.
 * 
 *         This class is the Java implementation of the CoSpi algorithm. It
 *         contains input and visualization methods to load and run datasets to
 *         create layouts.
 * 
 *         Dataset Format: CSV or txt files with each value in a new line, and
 *         names seperated with commas. See the example datasets folder of the
 *         project.
 * 
 *         Last update: Jan 2021, Merged files and code cleanup.
 * 
 *         BUG: Labels wont work properly for pie charts for non value-sorted
 *         datasets. Sort them first.
 *
 */
public class CoSpi {

	public static int N = 1000; // Visualization size

	public static int K = 0; // Number of entities/values to be visualized
	public static int MAX_SIZE; // Normalized max
	public static int MIN_SIZE; // Normalized min

	public static Picture pic = new Picture(N, N); // Visualization image
	public static Pixel[][] pixels = new Pixel[N][N]; // The pixels of the image
	public static Color defColor = Color.blue; // The default color for the buildings

	public static ArrayList<Rectangle> rects = new ArrayList<>(); // Model rectangle list (to be used by the compact
																	// algorithm)
	public static ArrayList<Rectangle> rectangles = new ArrayList<>(); // Complete rectangle list
	public static ArrayList<String> originalNames = new ArrayList<>(); // Original names of entities
	public static ArrayList<Integer> originalValues = new ArrayList<>(); // Original values of entities

	public static ArrayList<Integer> radiiWhenScaleofNumberOfVisObjectChanges = new ArrayList<>(); // Needed for the
																									// axisX
	public static ArrayList<Integer> radiiWhenScaleOfValuesChanges = new ArrayList<>(); // Needed for the axisY

	/**
	 * Load the dataset and creates the CoSpi visualization.
	 * 
	 * @param filename  The name of the file
	 * @param valCol    Index of values
	 * @param labCol    Index of names
	 * @param min       Normalized Min
	 * @param max       Normalized Max
	 * @param conf      Visual Configurations
	 * @param usingGUI  See the visualization in the default image viewer of java
	 * @param createSVG Create the SVG file of visualization
	 */
	public static void loadDataAndRun(String filename, int valCol, int labCol, int min, int max, VisConfig conf,
			boolean usingGUI, boolean createSVG) {

		String csvFile = filename;
		CSVReader.separateMapToLists(originalValues, originalNames,
				CSVReader.readValuesAndCreateMap(valCol, labCol, CSVReader.readFile(csvFile)));
		ArrayList<Integer> fromFile = new ArrayList<>(originalValues);
		CSVReader.Normalize(fromFile, min, max);
		visualize(fromFile, conf, false, true, usingGUI, false, createSVG);
	}

	/**
	 * Load the dataset as a pie chart and create the CoSpi visualization.
	 * 
	 * @param filename
	 * @param groupByCol
	 * @param valCol
	 * @param nameCol
	 * @param min
	 * @param max
	 * @param conf
	 * @param usingGUI
	 * @param createSVG
	 */
	public static void loadDataAndRunPieChart(String filename, int groupByCol, int valCol, int nameCol, int min,
			int max, VisConfig conf, boolean usingGUI, boolean createSVG) {

		System.out.println("Pie chart creation begins");
		Map<String, ArrayList<String>> valueMap = CSVReader.readGroupedByValues(filename, groupByCol, valCol);
		System.out.println("Values retrieved and grouped.");
		Map<String, ArrayList<String>> nameMap = CSVReader.readNames(filename, groupByCol, nameCol);

		createPieChart(valueMap, nameMap, min, max, conf, usingGUI, createSVG);
	}

	/**
	 * 
	 * Creates comparison charts from multiple dataset files
	 * 
	 * @param filenames
	 * @param columns
	 * @param NormalizeMin
	 * @param NormalizeMax
	 * @param visConf
	 */
	public static void loadDataFromFilesAndCompare(String[] filenames, int[] columns, int NormalizeMin,
			int NormalizeMax, VisConfig visConf, boolean usingGUI, boolean createSVG) {

		// Part A: Loads the data from all files and stores them in a map
		Map<String, ArrayList<String>> mapGV = new TreeMap<>();
		for (int i = 0; i < filenames.length; i++) {
			ArrayList<String> dataFromFile = CSVReader.getValueColumn(CSVReader.readFile(filenames[i]), columns[i]); // reading
																														// the
																														// data
																														// from
																														// one
																														//	 file
			mapGV.put(filenames[i] + i, dataFromFile); // put them in the map. The "+i" is for the case we use the same
														// filename more than once
		}
		// Part B: It calls the method that will produce the pie chart
		createPieChart(mapGV, null, NormalizeMin, NormalizeMax, visConf, usingGUI, createSVG); // calls the piechart
																								// visualizer

		// System.out.println("COMPARISON CHART END");
	}

	/**
	 * Creates frequency comparison charts from a single dataset
	 * 
	 * @param filename
	 * @param columns
	 * @param NormalizeMin
	 * @param NormalizeMax
	 * @param visConf
	 * @param usingGUI
	 * @param createSVG
	 * @param allowedOverLap
	 */
	public static void loadDataFromSingleFileAndCompareFrequencies(String filename, int[] columns, int NormalizeMin,
			int NormalizeMax, VisConfig visConf, boolean usingGUI, boolean createSVG) {

		// to create table with the same filename as many times as the number of columns
		// and call the next method
		String filenames[] = new String[columns.length];
		for (int i = 0; i < filenames.length; i++) {
			filenames[i] = filename;
		}
		loadDataFromFilesAndCompareFrequencies(filenames, columns, NormalizeMin, NormalizeMax, visConf, usingGUI,
				createSVG);
	}

	/**
	 * Creates frequency comparison charts from multiple datasets
	 * 
	 * @param filenames
	 * @param columns
	 * @param NormalizeMin
	 * @param NormalizeMax
	 * @param visConf
	 * @param usingGUI
	 * @param createSVG
	 * @param allowOverlap
	 */
	public static void loadDataFromFilesAndCompareFrequencies(String[] filenames, int[] columns, int NormalizeMin,
			int NormalizeMax, VisConfig visConf, boolean usingGUI, boolean createSVG) {
		// A. Computation of a map with the frequencies

		Map<String, ArrayList<String>> mapGV = new TreeMap<>();
		for (int i = 0; i < filenames.length; i++) {
			ArrayList<String> dataFromFile = CSVReader.getValueColumn(CSVReader.readFile(filenames[i]), columns[i]); // reading
																														// the
																														// data
																														// from
																														// one
																														// file

			// A. Find the distinct values and their frequencies and to then keep the
			// frequencies

			// A.1 computation of frequencies
			Map<String, Integer> valFreqs = new TreeMap<String, Integer>();
			for (String a : dataFromFile) {
				Integer freq = valFreqs.get(a);
				valFreqs.put(a, (freq == null) ? 1 : freq + 1);
			}
			// A.2 storage of frequencies
			ArrayList<String> freqsOnly = new ArrayList<String>();
			for (Integer fr : valFreqs.values()) {
				freqsOnly.add(fr.toString());
			}
			// A.3 Adding the frequences to the map
			mapGV.put(filenames[i] + i, freqsOnly); // put them in the map. The "+i" is for the case we use the same
													// filename more than once
		}
		// Part B: It calls the method that will produce the pie chart
		createPieChart(mapGV, null, NormalizeMin, NormalizeMax, visConf, usingGUI, createSVG);
		System.out.println("COMPARISON FREQUENCY CHART END");

	}

	/**
	 * 
	 * @param mapGV
	 * @param mapNames
	 * @param NormalizeMin
	 * @param NormalizeMax
	 * @param conf
	 * @param usingGUI
	 * @param createSVG
	 */
	public static void createPieChart(Map<String, ArrayList<String>> mapGV, Map<String, ArrayList<String>> mapNames,
			int NormalizeMin, int NormalizeMax, VisConfig conf, boolean usingGUI, boolean createSVG) {

		// Part A: General Setup for Angles (the angle of the entire diagram, the angle
		// gap between the slices)
		int piNum = 2; // determines the range of angles in the entire diagram (default: 2pi)
		double angleGap = piNum * Math.PI / 40.0; // to 1/40 tou diskou (athroistika) tha einai diakena

		boolean keepNames = true;
		if (mapNames == null)
			keepNames = false;

		// Part B: The angles for each pie (hereafter with pie I mean slice)
		int pieNum = mapGV.keySet().size();
		System.out.println("Number of pies: " + pieNum);

		double anglePerPie = (piNum * Math.PI - angleGap) / pieNum; // h katharh gonia pou prepei na exei kathe enas
		double angleGapAfterAPie = angleGap / pieNum;

		// Part C: Conversion of the Map to .. Integer, for finding also the Min Max
		// which are needed for proper normalization
		int allValuesMax = Integer.MIN_VALUE;
		int allValuesMin = Integer.MAX_VALUE;

		Map<String, ArrayList<Integer>> mapGVint = new TreeMap<>();
		Map<String, ArrayList<String>> mapGVNames = new TreeMap<>();

		for (String key : mapGV.keySet()) {
			ArrayList<Integer> valsInt = CSVReader.toIntDescending(mapGV.get(key)); // String to Integer and sorting
																					// desc
			ArrayList<String> valsNames = null;
			if (keepNames)
				valsNames = CSVReader.getNamesOfBuildings(mapNames.get(key));
			mapGVint.put(key, valsInt); // addition to the map
			if (keepNames)
				mapGVNames.put(key, valsNames);

			if (valsInt.get(0) > allValuesMax)
				allValuesMax = valsInt.get(0); // update allValuesMax if required
			if (valsInt.get(valsInt.size() - 1) < allValuesMin)
				allValuesMin = valsInt.get(valsInt.size() - 1); // update allValuesMin if required
		}

		// PART D: Drawing of each pie
		pic.preparePixels(pixels, N, Color.white);

		double fromAngle = 0;

		Color[] sliceColors = { Color.red, Color.yellow, Color.green, Color.pink, new Color(255, 255, 153), // yellow-like
				Color.cyan, Color.LIGHT_GRAY, Color.magenta, Color.orange, new Color(255, 229, 204), // mpez
				new Color(204, 255, 204), // light green
				new Color(204, 229, 255) // light blue
		};

		int i = 0; // the pie number
		for (String key : mapGV.keySet()) { // for each grouByVal, i.e. for each pie

			defColor = sliceColors[(i % sliceColors.length)];

			// PART D.1 The data for each pie
			ArrayList<Integer> vals = mapGVint.get(key);
			ArrayList<String> names = null;
			if (keepNames)
				names = mapGVNames.get(key);
			System.out.println("Pie " + i + " for key " + key + "with " + vals.size() + " values, with data:\n" + vals);

			// PART D.2 The drawing of the data of each pie
			VisConfig pieConf = null;
			try {
				pieConf = (VisConfig) conf.clone(); // cloning for getting values from the param conf.
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}

			pieConf.setAngleMin(fromAngle); // sets the angle min for this slice
			pieConf.setAngleMax(fromAngle + anglePerPie); // sets the angle max for this slice

			fromAngle = fromAngle + anglePerPie + angleGapAfterAPie; // update of formAngle for the next loop

			System.out.println(vals.size());
			if (keepNames)
				originalNames.addAll(names);
			originalValues.addAll(vals);

			MIN_SIZE = NormalizeMin; // the desired range of sizes in the plot
			MAX_SIZE = NormalizeMax; // 150 for words, 80 for cities

			CSVReader.Normalize(vals, MIN_SIZE, MAX_SIZE, allValuesMin, allValuesMax); // The normalization for the pies
			boolean isLast = (i == pieNum - 1);

			visualize(vals, pieConf, true, isLast, usingGUI, true, createSVG);
			i++; // the counter of the pie (just for selecting the color)
		}
	}

	/**
	 * This method runs the Concentric Spiral algorithm and manipulates the dataset
	 * visualization using the further parameters.
	 * 
	 * @param values      The normalized rectangle side values.
	 * @param conf        The visual configurations.
	 * @param incremental Re-initialize the pixels at the start (used mostly to
	 *                    create pie charts).
	 * @param lastCall    This parameter is set true if the method is called once or
	 *                    for pie charts to declare that the further parameters
	 *                    should be added or not.
	 * @param GUIMode     Set false to show the picture.
	 * @param pieChart    Declare if we have a pie chart or not.
	 * @param createSVG   Declare if a SVG should be created.
	 */
	public static void visualize(ArrayList<Integer> values, VisConfig conf, boolean incremental, boolean lastCall,
			boolean GUIMode, boolean pieChart, boolean createSVG) {

		if(pieChart) {
			K += values.size();
		}
		else {
			K = values.size();
		}
		
		MAX_SIZE = values.get(0);
		conf.setMax(MAX_SIZE);
		MIN_SIZE = values.get(K - 1);
		conf.setMin(MIN_SIZE);

		System.out.println("Values: " + K);
		System.out.println("Max size: " + MAX_SIZE);
		System.out.println("Min size: " + MIN_SIZE);

		if ((originalValues == null) || (originalValues.isEmpty())) {
			originalValues = new ArrayList<>(values);
		}

		copyInfoToRects(values);
		radiiWhenScaleofNumberOfVisObjectChanges.clear();
		radiiWhenScaleOfValuesChanges.clear();

		if (!incremental) {
			pic.preparePixels(pixels, N, Color.white);
		}

		if (!pieChart) {
			defColor = conf.getRectColor();
		}

		ConcentricSpiralCompact(conf.getDirection(), conf.getExpandStyle(), conf.getDrawStyle(), conf.getRoadSize(),
				conf.getAngleMin(), conf.getAngleMax(), conf.getAxes(), conf.getAllowedOverlap());

		if (conf.getAxes() != Axes.NoAxes) {
			MathUtils.visualizeAxes(values, conf, radiiWhenScaleOfValuesChanges,
					radiiWhenScaleofNumberOfVisObjectChanges, pixels, N);
		}

		pic.updatePixels(pixels, N);

		if (conf.includeLabels() && lastCall) {
			LabelAlgorithms.addLabels(conf, rectangles, pic, N);
		}

		if (!GUIMode && lastCall) {
			pic.show();
		}

		if (conf.isEnableInfo() && lastCall) {
			LabelAlgorithms.writeInfoToImage(pic.image, N, 16, Color.GRAY, MAX_SIZE, MIN_SIZE,
					Collections.max(originalValues), Collections.min(originalValues), K);
		}

		if (createSVG && lastCall) {
			try {
				SVGGenerator.createSVG(conf, rectangles, N, pic.image, "C:\\Users\\manos\\Desktop", "SVGNew.html");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * The concentric spiral algorithm.
	 * 
	 * @param directionMode
	 * @param expandStyle
	 * @param drawStyle
	 * @param roadSz
	 * @param AngleMin
	 * @param AngleMax
	 * @param axes
	 * @param allowedOverlap
	 */
	public static void ConcentricSpiralCompact(Direction directionMode, ExpandStyle expandStyle, DrawStyle drawStyle,
			int roadSz, double AngleMin, double AngleMax, Axes axes, boolean allowedOverlap) {

		int curSquareSize = MAX_SIZE; // building size: initially the biggest
		int roadSize = roadSz; // road size (i.e. gaps between buildings), default = 5
		int szMax = MAX_SIZE; // just for avoiding to use Max_Sq_Sz
		int curBuildingIndex = 0; // index in the arraylist that holds the buildings
		int rad = (int) 1 * rects.get(0).len; // the radious of the 1st cycle = side of biggest building
		double curAngle = AngleMin; // current angle in the ring
		int maxSqSizeInRing = rects.get(0).len; // the size of the biggest square in the ring is the size of the 1st
		boolean outOfPaper = false; // when the canvas is filled
		int minChordIncrement; // min length of required chord to avoid collisions
		double angleIncrement = 0; // min degree to proceed for avoiding collision with the previous building
		Direction direction = directionMode; // either Direction.Expand (for RING) or Direction.Shrink (for ANCIENT
												// THEATER)
		curAngle = AngleMin;

		System.out.println("CoSpi visualizer begins.");

		while ((curBuildingIndex < K) && (!outOfPaper)) { // while there are buildings to draw

			minChordIncrement = rects.get(curBuildingIndex).len; // the side of the square to be drawn (for placing it
																	// just *above* the x-axis)

			if (minChordIncrement == 0) { // (Jan 18, 2020) if the building to be visualized has zero size (this is
											// needed for the case where filled rings are used for the too small values)
				System.out.println("ZERO SIZE SHAPE");
				int radiusForTheFilledRing = rad + maxSqSizeInRing + roadSize; // the radius from which the filled ring
																				// should start
				fillAsARing(curBuildingIndex, radiusForTheFilledRing); // calling the method that draws the filled ring.
				break;
			}

			if (curBuildingIndex > 0) { // i.e. if not the first building
				minChordIncrement += curSquareSize; // addition of the size of the previously drawn square
				// Mathematically Minimum:
				// minChordIncrement= minChordIncrement + (int)
				// ((curSquareSize+rects.get(curBuildingIndex).len)/Math.sqrt(2));
			}

			angleIncrement = MathUtils.degreesOfChord(minChordIncrement, rad); // degrees corresponding to the chord
																				// with size minChordIncrement
			curAngle = curAngle + angleIncrement;

			if (curAngle >= AngleMax) { // if true then a new ring should be started
				switch (direction) {
				case Expand:
					if (expandStyle == ExpandStyle.Spiral) {
						rad += maxSqSizeInRing + roadSize; // (gia to normal spiral)
					} else if (expandStyle == ExpandStyle.Ring) {
						rad += roadSize; // for RING
					}
					break;
				case Shrink:
					rad -= roadSize;
					break;
				}

				if (rad < rects.get(curBuildingIndex).len) { // we are in shrink mode and we have reached the center
					rad = (int) 1 * rects.get(0).len;
					direction = Direction.Expand; // we change mode: from shrink to expansion.
				}

				maxSqSizeInRing = rects.get(curBuildingIndex).len; // this is the max size of squares in this new ring

				if (rad > N / 2 - maxSqSizeInRing) { // out of canvas
					System.out.println("OUT OF PAPER.");
				}

				curAngle = AngleMin; //
				minChordIncrement = rects.get(curBuildingIndex).len; // the size of this square
				angleIncrement = MathUtils.degreesOfChord(minChordIncrement, rad); // for placing the first square
																					// *above* the x-axis
				curAngle = curAngle + (angleIncrement / 2); // to check /2. Not the ideal. (SOS)
			} // new ring
			int x = (int) (N / 2 + rad * Math.cos(curAngle)); // x of point in cycle
			int y = (int) (N / 2 + rad * Math.sin(curAngle)); // y of point in cycle

			Rectangle curRect = rects.get(curBuildingIndex++); // gets the next building

			// overlap: for axes only (not in use however)
			if (MathUtils.isEmpty(x, y, curRect.len, pixels, N) || allowedOverlap) { // not needed for normal spiral
				drawRectangleCenter(x, y, curRect, drawStyle); // draws a rectangle whose center is at x,y//
				keepInfoForRects(x, y, curRect.len, curBuildingIndex);
				curSquareSize = curRect.len;
				if (axes != Axes.NoAxes)
					keepInfoForAxes(curBuildingIndex - 1, curRect.len, rad);
			} else {
				System.out.println("Occupied spot (or not in canvas).");
				curBuildingIndex--; // for trying to find free space in the next iteration(s) of the loop
			}

		}
		System.out.println("Visualization completed normally.");
	}

	/**
	 * For the rectangles with side of size equals 0.
	 * 
	 * @param curIndex
	 * @param curRadius
	 */
	public static void fillAsARing(int curIndex, int curRadius) {

		System.out.println("FILL-AS-RING with INDEX:" + curIndex + " and RADIUS: " + curRadius);
		int numOfPointsWithZero = originalValues.size() - curIndex;
		System.out.println("Number of values with normalized value zero: " + numOfPointsWithZero);

		int sumOfOriginalValues = 0;
		for (int i = curIndex; i < originalValues.size(); i++) {
			sumOfOriginalValues += originalValues.get(i); // for computing the size of the area to be filled
		}
		System.out.println("Sum of original values with normalized value zero: " + sumOfOriginalValues);

		// The NORMALIZATION that is required
		int dataMax = originalValues.get(0);
		int dataMin = originalValues.get(originalValues.size() - 1);
		int normalizedSum = (int) (sumOfOriginalValues * (+0.0) / dataMax);
		System.out.println("Original Data Min Max: " + dataMin + " " + dataMax);
		System.out.println("Max_Sq_Sz: " + MAX_SIZE);
		System.out.println("sumOfOriginalValues: " + sumOfOriginalValues);
		System.out.println("NormalizedSum: " + normalizedSum);

		// Computing the outer radius of the ring
		int radiusOuter = (int) Math.round(Math.sqrt(curRadius * curRadius + (normalizedSum / Math.PI))); // normalized
		// int radiusOuter = (int) Math.round( Math.sqrt(curRadius*curRadius + (
		// sumOfOriginalValues / Math.PI))); // not normalized
		if (curRadius == radiusOuter)
			radiusOuter++; // for making it also visible
		System.out.println("Filled Ring: Starting Radius: " + curRadius);
		System.out.println("Filled Ring: Outer Radius   : " + radiusOuter);

		MathUtils.fillRing(curRadius, radiusOuter, pixels, N);

		// BOOK KEEPING FOR AXES
		ArrayList<Integer> axisPoints = new ArrayList<>();
		for (int i = curIndex; i < originalValues.size(); i++) {

			double curIlog = Math.log10(i); // log 10
			int curIlogInt = (int) curIlog; // integer part of log 10
			if (curIlog - curIlogInt == 0) { // curIndex is a power of 10 (todo: 2 check precision issues)
				axisPoints.add(i); // keeping this point for the axes
			}

		}
		int numOfAxisPoints = axisPoints.size(); // the number of axes points required
		System.out.println(">>>AXES POINT REQUIRED " + numOfAxisPoints);

		int uniformStep = (radiusOuter - curRadius) / numOfAxisPoints; // uniform

		int i = 1;
		for (int axpoint : axisPoints) {
			System.out.println(">>>AXIS POINT FOR " + axpoint);
			radiiWhenScaleofNumberOfVisObjectChanges.add(curRadius + (i * uniformStep)); // DUMMY
			i++;
		}
	}

	/**
	 * Initialize rectangles list with the rectangles side values.
	 * 
	 * @param values
	 */
	public static void copyInfoToRects(ArrayList<Integer> values) {
		if (rects == null)
			rects = new ArrayList<Rectangle>();
		else
			rects.clear();
		for (int i = 0; i < values.size(); i++) {
			/*
			 * if(originalValues.size() != values.size() || originalNames.size() !=
			 * values.size() || originalValues.isEmpty() || originalNames.isEmpty()) {
			 * rects.add(new Rectangle(0,0,values.get(i),-1,"NoName",defColor)); } else{
			 * rects.add(new
			 * Rectangle(0,0,values.get(i),originalValues.get(i),originalNames.get(i),
			 * defColor)); }
			 */
			rects.add(new Rectangle(values.get(i)));
		}
	}

	/**
	 * Draw a rectangle with center at (x,y).
	 * 
	 * @param x
	 * @param y
	 * @param r
	 * @param d
	 */
	public static void drawRectangleCenter(int x, int y, Rectangle r, DrawStyle d) {
		int leftx = x - (r.len / 2);
		int bottomy = y - (r.len / 2);

		if (d == DrawStyle.Outline) {
			drawRectangle(leftx, bottomy, r);
		} else if (d == DrawStyle.Filled) {
			drawFilledRectangle(leftx, bottomy, r);
		}
	}

	/**
	 * Draw a filled rectangle at coordinates (x,y).
	 * 
	 * @param x
	 * @param y
	 * @param r
	 */
	public static void drawFilledRectangle(int x, int y, Rectangle r) {
		if ((x > 0) && (y > 0) && ((x + r.len) < N) && ((y + r.len) < N)) {
			for (int i = 0; i < r.len; i++) {
				for (int j = 0; j < r.len; j++) {
					pixels[x + i][y + j].setColor(defColor);
					pixels[x + i][y + j].setOccupied(true);
				}
			}
		}
	}

	/**
	 * Draw an outline rectangle at coordinates (x,y).
	 * 
	 * @param x
	 * @param y
	 * @param r
	 */
	public static void drawRectangle(int x, int y, Rectangle r) {
		if ((x > 0) && (y > 0) && ((x + r.len) < N) && ((y + r.len) < N)) {
			for (int i = 0; i < r.len; i++) {
				pixels[x + i][y].setColor(defColor);
				pixels[x + i][y].setOccupied(true);
				pixels[x][y + i].setColor(defColor);
				pixels[x][y + i].setOccupied(true);
				pixels[x + i][y + r.len].setColor(defColor);
				pixels[x + i][y + r.len].setOccupied(true);
				pixels[x + r.len][y + i].setColor(defColor);
				pixels[x + r.len][y + i].setOccupied(true);

				for (int j = 0; j < r.len; j++) {
					pixels[x + i][y + j].setOccupied(true);
				}
			}

			/*
			 * for (int i=0; i<r.len; i++) { for (int j=0; j<r.len; j++) {
			 * pixels[x+i][y+j].setOccupied(true); } }
			 */
		}
	}

	/**
	 * Keep info to add axes to the visualization.
	 * 
	 * @param curIndex The current building index.
	 * @param rectLen  The length of the side.
	 * @param radius   The radius.
	 */
	static void keepInfoForAxes(int curIndex, int rectLen, int radius) {
		curIndex = Math.min(curIndex, originalValues.size() - 1); // just for robustness (PATCH)

		// About NumOfObjects (Axis X)
		double curIlog = Math.log10(curIndex); // log 10
		int curIlogInt = (int) curIlog; // integer part of log 10
		if (curIlog - curIlogInt == 0) { // curIndex is a power of 10 (todo: 2 check precision issues)
			if (curIlog >= 1) // for not drawing an axis for 1
				radiiWhenScaleofNumberOfVisObjectChanges.add(radius); // add the radius in the arraylist
		}

		// About Values (the original, not the rescaled), i.e. AxisY
		double vallog = Math.log10(originalValues.get(curIndex)); // log 10
		int vallogInt = (int) vallog; // integer part of log 10
		if (vallog - vallogInt < 0.03) { // val is a power of 10 (todo: 2 check precision issues)
			if (vallog >= 1) { // for not drawing an axis for 1
				if (!radiiWhenScaleOfValuesChanges.contains(radius)) { // if that radius is not already in the arraylist
					radiiWhenScaleOfValuesChanges.add(radius); // add the raius in the arraylist
					// System.out.println(">>>Original val:"+originalValues.get(curIndex) + " Log
					// val:"+vallog);
				}
			}
		}
	}

	/**
	 * Keep info for every building added to the visualization.
	 * 
	 * @param x   The x coordinate.
	 * @param y   The y coordinate.
	 * @param len The length of it's side.
	 */
	static void keepInfoForRects(int x, int y, int len, int rank) {
		int curIndex = rectangles.size();
		if (curIndex >= originalValues.size() || curIndex >= originalNames.size()) {
			rectangles.add(new Rectangle(x, y, len, 0, "-", defColor));
		} else {
			rectangles.add(new Rectangle(x, y, len, originalValues.get(curIndex), rank, originalNames.get(curIndex),
					defColor));
		}
	}

	/**
	 * Clear the rectangle information to load new visualization.
	 */
	public static void clearOldData() {
		rects.clear();
		rectangles.clear();
		originalNames.clear();
		originalValues.clear();
		radiiWhenScaleOfValuesChanges.clear();
		radiiWhenScaleofNumberOfVisObjectChanges.clear();
	}

	/**
	 * CoSpi simple examples.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		VisConfig conf = new VisConfig();

		conf.setDrawStyle(DrawStyle.Filled);
		conf.setDirection(Direction.Expand);
		conf.setExpandStyle(ExpandStyle.Spiral);
		conf.setShapeGaps(ShapeGaps.Minimum);

		conf.setAngleMin(0);
		conf.setAngleMax(2 * Math.PI);
		conf.setRoadSize(10);

		conf.setAxes(Axes.NoAxes);
		conf.setRectColor(Color.blue);
		conf.setEnableInfo(true);
		conf.setLabelParams(false, false, true, Color.black, 4);
		CoSpi.loadDataAndRun("Resources/cities.csv", 1, 0, 1, 50, conf, false, false);
		/*
		 * long startTime = System.nanoTime();
		 * System.out.println("Time measurement started.");
		 * 
		 * CoSpi.loadDataAndRun("Resources/cvAu_cut.csv", 1, 0,10, 80,
		 * conf,false,false);
		 * 
		 * long endTime = System.nanoTime(); long timeElapsed = endTime-startTime;
		 * 
		 * System.out.println("Execution time in milliseconds: " + timeElapsed /
		 * (1000*1000)); System.out.println("Execution time in seconds: " + timeElapsed
		 * / (1000*1000*1000));
		 */

		/// Map<String,ArrayList<String>> mapGV =
		/// CSVReader.groupValsAndNames("Resources/companies.csv",1,2,0);
		// System.out.println(mapGV);

		// loadDataFromFileAndVisualizePie("Resources/citiesContinents.csv", 1, 2, 10,
		// 40, conf);

		// loadDataAndRunPieChart("Resources/companies.csv",1,2,0,10,40,conf);

		/*
		 * ArrayList vals = SyntheticDataGenerator .DecreasingByPercentage(15, 100, 8,
		 * 2); // size, maxsize, minsize, decreasePercentage
		 * //.DecreasingByPercentage(1*1000*1000, 1, 1, 2); // size, maxsize, minsize,
		 * decreasePercentage (to exw paei ews 15 millions) originalValues = new
		 * ArrayList<>(vals); // Jan 5, 2020 visualize(vals, conf, false,true, false,
		 * false, false);
		 */

	}

	/**
	 * Clustered LOD Uniform visualization example.
	 */
	public static void clusteredLODUniform() {
		pic.preparePixels(pixels, N, Color.white);
		ArrayList vals = SyntheticDataGenerator.DecreasingByPercentage(100, 40, 2, 20); // size, maxsize, minsize,
																						// decreasePercentage

		copyInfoToRects(vals);
		K = rects.size();
		System.out.println("SIZE RECTS:" + K);

		int piNum = 1; // for changing the angle range of the entire diagram (default = 2pi)

		double angleGap = piNum * Math.PI / 20.0; // to 5% tou diskou tha einai diakena
		int DNum = 8; // number of domains

		double anglePerDomain = (piNum * Math.PI - angleGap) / DNum; // h katharh gonia pou prepei na exei kathe ena
																		// domain
		double angleGapAfterADomain = angleGap / DNum;

		double fromAngle = 0;
		for (int i = 0; i < DNum; i++) {
			switch (i) {
			case 0:
				defColor = Color.black;
				break;
			case 1:
				defColor = Color.blue;
				break;
			case 2:
				defColor = Color.red;
				break;
			case 3:
				defColor = Color.green;
				break;
			case 4:
				defColor = Color.yellow;
				break;
			case 5:
				defColor = Color.cyan;
				break;
			case 6:
				defColor = Color.lightGray;
				break;
			case 7:
				defColor = Color.magenta;
				break;
			case 8:
				defColor = Color.orange;
				break;
			}
			ConcentricSpiralCompact(Direction.Expand, ExpandStyle.Spiral, DrawStyle.Filled, 5, fromAngle,
					fromAngle + anglePerDomain, Axes.NoAxes, false);
			fromAngle = fromAngle + anglePerDomain + angleGapAfterADomain;
		}
		pic.updatePixels(pixels, N);
		LabelAlgorithms.writeInfoToImageSynthetic(pic.image, N, 16, Color.GRAY, rects.get(0).getLen(),
				rects.get(rects.size() - 1).getLen());
		pic.show();

	}

	/**
	 * Clustered LOD visualization example.
	 */
	public static void clusteredLOD() {
		pic.preparePixels(pixels, N, Color.white);

		ArrayList vals = SyntheticDataGenerator.DecreasingByPercentage(100, 40, 2, 20); // size, maxsize, minsize,
																						// decreasePercentage
		copyInfoToRects(vals);
		K = rects.size();
		System.out.println("SIZE RECTS:" + K);

		//
		int piNum = 2; // for changing the angle range of the entire diagram

		double angleGap = piNum * Math.PI / 20.0; // to 5% tou diskou tha einai diakena
		int DNum = 9; // number of domains

		double anglePerDomain = (piNum * Math.PI - angleGap) / DNum; // h katharh gonia pou prepei na exei kathe enas
		double angleGapAfterADomain = angleGap / DNum;

		// for not uniform:
		int Kbefore = K; // Ã¯Â¿Â½Ã¯Â¿Â½
							// Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½
							// Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½
							// Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½
		int Kfake = 10;

		int multiplier = 2; // how many times the bigger sector will be

		K = Kfake;
		double fromAngle = 0;
		for (int i = 0; i < DNum; i++) {

			switch (i) {
			case 0:
				defColor = Color.red;
				break;
			case 1:
				defColor = Color.blue;
				break;
			case 2:
				defColor = Color.pink;
				break;
			case 3:
				defColor = Color.green;
				break;
			case 4:
				defColor = Color.DARK_GRAY;
				break;
			case 5:
				defColor = Color.cyan;
				break;
			case 6:
				defColor = Color.lightGray;
				break;
			case 7:
				defColor = Color.magenta;
				break;
			case 8:
				defColor = Color.orange;
				break;
			}

			ConcentricSpiralCompact(Direction.Expand, ExpandStyle.Spiral, DrawStyle.Filled, 5, fromAngle,
					fromAngle + anglePerDomain, Axes.NoAxes, false); // This is the stable version
			fromAngle = fromAngle + anglePerDomain + angleGapAfterADomain;
			K = multiplier * K;
			K = Math.min(K, Kbefore); // to avoid out of index
		}
		pic.updatePixels(pixels, N);
		LabelAlgorithms.writeInfoToImageSynthetic(pic.image, N, 16, Color.GRAY, rects.get(0).getLen(),
				rects.get(rects.size() - 1).getLen());
		pic.show();
	}

}

/**
 * This class holds drawing and math utilities to create and draw CoSpi layouts.
 * 
 * @author Yannis Tzitzikas
 * @author Manos Chatzakis (additions)
 */
class MathUtils {

	/**
	 * Calculates the radiant difference of the rectangle coordinates
	 * 
	 * @param chord
	 * @param radius
	 * @return The angle of the new rectangle
	 */
	public static double degreesOfChord(int chord, int radius) {
		return Math.toDegrees(2 * Math.asin(Math.toRadians(1 * ((0.0 + chord) / (2 * radius)))));
	}

	/**
	 * Returns whether or not the coordinates (x,y) of the pixels are occupied by
	 * another rectangle
	 * 
	 * @param x      The x coordinate
	 * @param y      The y coordinate
	 * @param len    The side of the rectangle to be drawn
	 * @param pixels The pixels of the layout
	 * @param N      The size of the layout
	 * @return True if the (x,y) coordinates are empty
	 */
	public static boolean isEmpty(int x, int y, int len, Pixel[][] pixels, int N) {
		// len = len +2 ; // gia einai aneto? To check
		int xleft = x - ((int) (len / 2.0));
		int xright = x + ((int) (len / 2.0));
		int yup = y + ((int) (len / 2.0));
		int ydown = y - ((int) (len / 2.0));

		// checking that we are within canvas
		if ((xleft < 0) || (ydown < 0) || (xright >= N) || (yup >= N)) { // out of canvas
			return false;
		}
		// checks the centers and the four corners
		return ((pixels[x][y].getColor() == Color.white && !pixels[x][y].isOccupied()) // center
				&& (pixels[xleft][ydown].getColor() == Color.white && !pixels[xleft][ydown].isOccupied()) // bottom left
																											// corner
				&& (pixels[xright][ydown].getColor() == Color.white && !pixels[xright][ydown].isOccupied()) // bottom
																											// right
																											// corner
				&& (pixels[xleft][yup].getColor() == Color.white && !pixels[xleft][yup].isOccupied()) // up left corner
				&& (pixels[xright][yup].getColor() == Color.white && !pixels[xright][yup].isOccupied()) // up right
																										// corner
		);
	}

	/**
	 * Draws the axes circles based on the data hold when the rectangles of the
	 * layout are drawn.
	 * 
	 * @param vals                                     The values of the dataset
	 *                                                 visualized
	 * @param conf                                     The visual configurations
	 * @param radiiWhenScaleOfValuesChanges            The data for the rectangles
	 *                                                 of which the value change (y
	 *                                                 axis)
	 * @param radiiWhenScaleofNumberOfVisObjectChanges The data for when the number
	 *                                                 of the rectangles to be
	 *                                                 visualized changes (x axis)
	 * @param pixels                                   The pixels of the layout
	 * @param N                                        The size of the image
	 */
	public static void visualizeAxes(ArrayList vals, VisConfig conf, ArrayList<Integer> radiiWhenScaleOfValuesChanges,
			ArrayList<Integer> radiiWhenScaleofNumberOfVisObjectChanges, Pixel pixels[][], int N) {
		// System.out.println("VISUALIZATION OF AXES");
		// NUM OF OBJECTS SCALE (AxisX)
		if ((conf.getAxes() == Axes.AxisX) || (conf.getAxes() == Axes.AxesXY)) { // if X axis is required
			int scale = 1; // for controlling the thickness
			for (int radNumObjScal : radiiWhenScaleofNumberOfVisObjectChanges) {
				// auxiliaryCircle(radNumObjScal, Color.GRAY,scale++); // it increases the scale
				// for increasing the thickness
				auxiliaryCircle(radNumObjScal, Color.GRAY, scale++, conf.getAngleMin(), conf.getAngleMax(), pixels, N); // it
																														// increases
																														// the
																														// scale
																														// for
																														// increasing
																														// the
																														// thickness

			}
		}
		// VALUES SCALE (AxisY)
		if ((conf.getAxes() == Axes.AxisY) || (conf.getAxes() == Axes.AxesXY)) { // if Y axis is required
			for (int radValueScale : radiiWhenScaleOfValuesChanges) {
				// auxiliaryCircle(radValueScale,Color.ORANGE,1);
				auxiliaryCircle(radValueScale, Color.ORANGE, 1, conf.getAngleMin(), conf.getAngleMax(), pixels, N);
			}
		}
	}

	/**
	 * Draws a circle on the dataset layout.
	 * 
	 * @param rad      The radius of the circle
	 * @param color    The color of the circle
	 * @param width    The opacity of the circle
	 * @param angleMin The starting angle (for full circle 0)
	 * @param angleMax The final angle (for full circle 2PI)
	 * @param pixels   The pixels of the layout
	 * @param N        The size of the layout
	 */
	public static void auxiliaryCircle(int rad, Color color, int width, double angleMin, double angleMax,
			Pixel pixels[][], int N) {
		int paxos = 2; // to control the density of dots even for a single circle

		int numOfCicles = width - 1; //

		for (int i = 0; i <= numOfCicles; i++) { // width of line is interpreted as number of cycles
			rad += i; // for getting a cycle with bigger radius
			for (double m = angleMin; m <= angleMax; m += 0.001) { // loop for the cycle: from angleMin to angleMax
				int x = (int) (N / 2 + rad * Math.cos(m));
				int y = (int) (N / 2 + rad * Math.sin(m));
				if (isInCanvas(x, y, N)) {
					pixels[x][y].setColor(color); // draws the circle using the color parm
					for (int j = -paxos; j <= paxos; j++) { // for increasing the density
						if (isInCanvas(x, y + j, N))
							pixels[x][y + j].setColor(color);
						if (isInCanvas(x + j, y, N))
							pixels[x + j][y].setColor(color);
					}
				} else {
					System.out.println("AuxiliaryCircle: Cannot draw, the size of the canvas is too small");
				}
			}
		}
	}

	/**
	 * Checks if the point with coordinates (x,y) are inside the layout borders.
	 * 
	 * @param x Coordinate x
	 * @param y Coordinate y
	 * @param N The size of the layout (NxN)
	 * @return True if the point (x,y) is inside the layout
	 */
	public static boolean isInCanvas(int x, int y, int N) {
		return ((x >= 0) && (y >= 0) && (x < N) && (y < N));
	}

	/**
	 * Fills the layout with a ring (made of multiple auxiliary circles). This
	 * method is used to visualize values with normilized value 0.
	 * 
	 * @param radiusFrom The starting radius
	 * @param radiusTo   The final radius
	 * @param pixels     The pixels of the layout
	 * @param N          The size of the layout
	 */
	public static void fillRing(int radiusFrom, int radiusTo, Pixel pixels[][], int N) {
		for (int i = radiusFrom; i < radiusTo; i++) {
			auxiliaryCircle(i, Color.blue, 1, pixels, N);
		}
	}

	/**
	 * Draws a full auxiliary circle to the layout
	 * 
	 * @param rad    The circle radius
	 * @param color  The circle color
	 * @param width  The circle opacity
	 * @param pixels The pixels of the layout
	 * @param N      The size of the layout
	 */
	public static void auxiliaryCircle(int rad, Color color, int width, Pixel pixels[][], int N) {
		auxiliaryCircle(rad, color, width, 0, 2 * Math.PI, pixels, N);
	}

}
