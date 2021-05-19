package cospi;

import utilsCommon.Pixel;
import utilsCommon.Rectangle;
import utilsCommon.SyntheticDataGenerator;
import utilsCommon.SVGGenerator;
import utilsCommon.Picture;
import cospi.params.*;
import csv.CSV;
import csv.CSVLine;
import normalization.Normalizer;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import sort.SortOrder;
import sort.SortType;

@Data
public class CoSpi {
    
    private Picture pic;
    private Pixel[][] pixels;
    
    private ArrayList<Rectangle> rectangles = new ArrayList<>();
    private ArrayList<String> originalNames = new ArrayList<>();
    private ArrayList<Double> originalValues = new ArrayList<>();
    private ArrayList<Integer> rectsLen = new ArrayList<>();
    private ArrayList<Integer> radiiWhenScaleofNumberOfVisObjectChanges = new ArrayList<>();
    private ArrayList<Integer> radiiWhenScaleOfValuesChanges = new ArrayList<>();
    
    private VisConfig conf;
    private CSV dataset;
    private Color defColor;
    
    private int MAX_SIZE;
    private int MIN_SIZE;
    
    private boolean usingGUI;
    
    public CoSpi(String filepath, boolean hasHeader) throws FileNotFoundException, IOException {
        dataset = new CSV(filepath, ",", hasHeader);
        dataset.parse();
        conf = new VisConfig();
        this.usingGUI = false;
    }
    
    public CoSpi(CSV dataset) throws CloneNotSupportedException {
        this.dataset = (CSV) dataset.clone();
        this.conf = new VisConfig();
        this.usingGUI = false;
    }
    
    public CoSpi() {
        this.conf = new VisConfig();
        this.usingGUI = false;
    }
    
    public void visualizeClassic(ArrayList<Integer> rectsLen, VisConfig conf, boolean createSVG) {
        
        conf.setMax(rectsLen.get(0));
        conf.setMin(rectsLen.get(rectsLen.size() - 1));
        
        MAX_SIZE = conf.getMax();
        MIN_SIZE = conf.getMin();
        
        this.rectsLen = new ArrayList<>(rectsLen);
        this.conf = conf;
        
        pic = new Picture(conf.getN(), conf.getN());
        pixels = new Pixel[conf.getN()][conf.getN()];
        
        radiiWhenScaleofNumberOfVisObjectChanges = new ArrayList<>();
        radiiWhenScaleOfValuesChanges = new ArrayList<>();
        rectangles = new ArrayList<>();
        
        visualize(false, true, usingGUI, false, createSVG);
    }
    
    public void visualizeClassic(VisConfig conf, int valueColumn, int nameColumn, boolean createSVG) {
        
        MAX_SIZE = conf.getMax();
        MIN_SIZE = conf.getMin();
        
        this.conf = conf;
        
        pic = new Picture(conf.getN(), conf.getN());
        pixels = new Pixel[conf.getN()][conf.getN()];
        
        dataset.sort(valueColumn, SortOrder.REVERSED, SortType.NUMERIC);
        
        originalNames = dataset.getColumnAsString(nameColumn);
        //System.out.println(originalNames);
        originalValues = dataset.getColumnAsDouble(valueColumn);
        //System.out.println(originalValues);
        rectsLen = MathUtils.Normalize(originalValues, MIN_SIZE, MAX_SIZE, originalValues.get(originalValues.size() - 1), originalValues.get(0));//normalizeList(originalValues);

        radiiWhenScaleofNumberOfVisObjectChanges = new ArrayList<>();
        radiiWhenScaleOfValuesChanges = new ArrayList<>();
        rectangles = new ArrayList<>();
        
        visualize(false, true, usingGUI, false, createSVG);
    }
    
    public void visualizePieChart(VisConfig conf, int valueColumn, int nameColumn, int groupByColumn, boolean createSVG) throws CloneNotSupportedException {
        
        this.conf = conf;
        
        pic = new Picture(conf.getN(), conf.getN());
        pixels = new Pixel[conf.getN()][conf.getN()];
        
        originalNames = new ArrayList<>();
        originalValues = new ArrayList<>();
        rectsLen = new ArrayList<>();
        rectangles = new ArrayList<>();
        radiiWhenScaleofNumberOfVisObjectChanges = new ArrayList<>();
        radiiWhenScaleOfValuesChanges = new ArrayList<>();
        
        dataset.sort(valueColumn, SortOrder.REVERSED, SortType.NUMERIC);
        
        Map<String, ArrayList<CSVLine>> data = dataset.groupCSVByColumn(groupByColumn);
        createPieChart(data, usingGUI, createSVG, valueColumn, nameColumn);
        
    }
    
    private void createPieChart(Map<String, ArrayList<CSVLine>> data, boolean usingGUI, boolean createSVG, int valueColumn, int nameColumn) {
        
        Map<String, ArrayList<Double>> dataValues = new TreeMap<>();
        Map<String, ArrayList<String>> dataNames = new TreeMap<>();

        // Part A: General Setup for Angles (the angle of the entire diagram, the angle
        // gap between the slices)
        int piNum = 2; // determines the range of angles in the entire diagram (default: 2pi)
        double angleGap = piNum * Math.PI / 40.0; // to 1/40 tou diskou (athroistika) tha einai diakena

        // Part B: The angles for each pie (hereafter with pie I mean slice)
        int pieNum = data.keySet().size();
        System.out.println("Number of pies: " + pieNum);
        
        double anglePerPie = (piNum * Math.PI - angleGap) / pieNum; // h katharh gonia pou prepei na exei kathe enas
        double angleGapAfterAPie = angleGap / pieNum;

        // Part C: Conversion of the Map to .. Integer, for finding also the Min Max
        // which are needed for proper normalization
        double allValuesMax = Integer.MIN_VALUE;
        double allValuesMin = Integer.MAX_VALUE;
        
        boolean keepNames = true;
        
        if (dataNames == null) {
            keepNames = false;
        }
        
        for (Map.Entry<String, ArrayList<CSVLine>> entry : data.entrySet()) {
            ArrayList<CSVLine> lines = entry.getValue();
            ArrayList<String> names = new ArrayList<>();
            ArrayList<Double> values = new ArrayList<>();
            
            for (CSVLine line : lines) {
                String name = line.getLine().get(nameColumn);
                Double value = Double.parseDouble(line.getLine().get(valueColumn));
                names.add(name);
                values.add(value);
                
                if (value > allValuesMax) {
                    allValuesMax = value;
                }
                
                if (value < allValuesMin) {
                    allValuesMin = value;
                }
            }
            
            dataValues.put(entry.getKey(), values);
            dataNames.put(entry.getKey(), names);
        }

        // PART D: Drawing of each pie
        pic.preparePixels(pixels, conf.getN(), Color.white);
        
        double fromAngle = 0;
        
        Color[] sliceColors = {Color.red, Color.yellow, Color.green, Color.pink, new Color(255, 255, 153), // yellow-like
            Color.cyan, Color.LIGHT_GRAY, Color.magenta, Color.orange, new Color(255, 229, 204), // mpez
            new Color(204, 255, 204), // light green
            new Color(204, 229, 255) // light blue 
    };
        
        int i = 0; // the pie number
        for (String key : dataValues.keySet()) { // for each grouByVal, i.e. for each pie

            defColor = sliceColors[(i % sliceColors.length)];

            // PART D.1 The data for each pie
            ArrayList<Double> vals = dataValues.get(key);
            ArrayList<String> names = dataNames.get(key);
            
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
            if (keepNames) {
                originalNames.addAll(names);
            }
            originalValues.addAll(vals);
            
            MIN_SIZE = conf.getMin(); // the desired range of sizes in the plot
            MAX_SIZE = conf.getMax(); // 150 for words, 80 for cities

            rectsLen = new ArrayList<>();
            Normalizer norm = new Normalizer(allValuesMin, allValuesMax, conf.getMin(), conf.getMax());
            
            for (Double d : vals) {
                rectsLen.add((int) norm.normalizeValue(d));
            }
            
            boolean isLast = (i == pieNum - 1);
            
            this.conf = pieConf;
            visualize(true, isLast, usingGUI, true, createSVG);
            i++; // the counter of the pie (just for selecting the color)
        }
        
    }
    
    private void visualize(boolean incremental, boolean lastCall, boolean GUIMode, boolean pieChart, boolean createSVG) {
        
        System.out.println("Values: " + rectsLen.size());
        System.out.println("Max size: " + rectsLen.get(0));
        System.out.println("Min size: " + rectsLen.get(rectsLen.size() - 1));
        
        if ((originalValues == null) || (originalValues.isEmpty())) {
            originalValues = new ArrayList<>();
            for (Integer i : rectsLen) {
                originalValues.add((double) i);
            }
        }
        
        if ((originalNames == null) || (originalNames.isEmpty())) {
            originalNames = new ArrayList<>();
            for (Integer i : rectsLen) {
                originalNames.add(i + "");
            }
        }

        //copyInfoToRects(values);
        if (!incremental) {
            pic.preparePixels(pixels, conf.getN(), Color.white);
        }
        
        if (!pieChart) {
            defColor = conf.getRectColor();
        }
        
        cospiCompact(
                conf.getDirection(),
                conf.getExpandStyle(),
                conf.getDrawStyle(),
                conf.getRoadSize(),
                conf.getAngleMin(),
                conf.getAngleMax(),
                conf.getAxes(),
                conf.isAllowOverlap()
        );
        
        if (conf.getAxes() != Axes.NoAxes) {
            MathUtils.visualizeAxes(rectsLen, conf, radiiWhenScaleOfValuesChanges,
                    radiiWhenScaleofNumberOfVisObjectChanges, pixels, conf.getN());
        }
        
        pic.updatePixels(pixels, conf.getN());
        
        if (conf.includeLabels() && lastCall) {
            Labels.addLabels(conf, rectangles, pic, conf.getN());
        }
        
        if (!GUIMode && lastCall) {
            pic.show();
        }
        
        if (conf.isEnableInfo() && lastCall) {
            Labels.writeInfoToImage(pic.image, conf.getN(), 16, Color.GRAY, conf.getMax(), conf.getMin(), originalValues.get(0), originalValues.get(originalValues.size() - 1), rectsLen.size());
        }
        
        if (createSVG && lastCall) {
            try {
                SVGGenerator.createSVG(conf, rectangles, conf.getN(), pic.image, "C:\\Users\\manos\\Desktop", "SVGNew.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void cospiCompact(Direction directionMode, ExpandStyle expandStyle, DrawStyle drawStyle, int roadSz, double AngleMin, double AngleMax, Axes axes, boolean allowedOverlap) {
        
        int K = rectsLen.size();
        int N = conf.getN();
        
        int curSquareSize = conf.getMax(); // building size: initially the biggest
        int roadSize = roadSz; // road size (i.e. gaps between buildings), default = 5
        int szMax = conf.getMin(); // just for avoiding to use Max_Sq_Sz
        int curBuildingIndex = 0; // index in the arraylist that holds the buildings
        int rad = (int) 1 * rectsLen.get(0); // the radious of the 1st cycle = side of biggest building
        int maxSqSizeInRing = rectsLen.get(0); // the size of the biggest square in the ring is the size of the 1st
        int minChordIncrement; // min length of required chord to avoid collisions

        double curAngle = AngleMin; // current angle in the ring
        double angleIncrement = 0; // min degree to proceed for avoiding collision with the previous building

        boolean outOfPaper = false; // when the canvas is filled

        Direction direction = directionMode; // either Direction.Expand (for RING) or Direction.Shrink (for ANCIENT  THEATER)
        curAngle = AngleMin;
        
        System.out.println("CoSpi visualizer begins.");
        
        while ((curBuildingIndex < K) && (!outOfPaper)) { // while there are buildings to draw

            minChordIncrement = rectsLen.get(curBuildingIndex); // the side of the square to be drawn (for placing it
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
                
                if (rad < rectsLen.get(curBuildingIndex)) { // we are in shrink mode and we have reached the center
                    rad = (int) 1 * rectsLen.get(0);
                    direction = Direction.Expand; // we change mode: from shrink to expansion.
                }
                
                maxSqSizeInRing = rectsLen.get(curBuildingIndex); // this is the max size of squares in this new ring

                if (rad > N / 2 - maxSqSizeInRing) { // out of canvas
                    System.out.println("OUT OF PAPER.");
                }
                
                curAngle = AngleMin; //
                minChordIncrement = rectsLen.get(curBuildingIndex); // the size of this square
                angleIncrement = MathUtils.degreesOfChord(minChordIncrement, rad); // for placing the first square
                // *above* the x-axis
                curAngle = curAngle + (angleIncrement / 2); // to check /2. Not the ideal. (SOS)
            } // new ring
            int x = (int) (N / 2 + rad * Math.cos(curAngle)); // x of point in cycle
            int y = (int) (N / 2 + rad * Math.sin(curAngle)); // y of point in cycle

            Rectangle curRect = new Rectangle(rectsLen.get(curBuildingIndex++)); //gets the next building
            // overlap: for axes only (not in use however)
            if (MathUtils.isEmpty(x, y, curRect.getLen(), pixels, N) || allowedOverlap) { // not needed for normal spiral
                drawRectangleCenter(x, y, curRect, drawStyle); // draws a rectangle whose center is at x,y//
                keepInfoForRects(x, y, curRect.getLen(), curBuildingIndex);
                curSquareSize = curRect.getLen();
                if (axes != Axes.NoAxes) {
                    keepInfoForAxes(curBuildingIndex - 1, curRect.getLen(), rad);
                }
            } else {
                System.out.println("Occupied spot (or not in canvas).");
                curBuildingIndex--; // for trying to find free space in the next iteration(s) of the loop
            }
            
        }
        System.out.println("Visualization completed normally.");
    }
    
    private void fillAsARing(int curIndex, int curRadius) {
        
        System.out.println("FILL-AS-RING with INDEX:" + curIndex + " and RADIUS: " + curRadius);
        int numOfPointsWithZero = originalValues.size() - curIndex;
        System.out.println("Number of values with normalized value zero: " + numOfPointsWithZero);
        
        int sumOfOriginalValues = 0;
        for (int i = curIndex; i < originalValues.size(); i++) {
            sumOfOriginalValues += originalValues.get(i); // for computing the size of the area to be filled
        }
        System.out.println("Sum of original values with normalized value zero: " + sumOfOriginalValues);

        // The NORMALIZATION that is required
        double dataMax = originalValues.get(0);
        double dataMin = originalValues.get(originalValues.size() - 1);
        int normalizedSum = (int) (sumOfOriginalValues * (+0.0) / dataMax);
        System.out.println("Original Data Min Max: " + dataMin + " " + dataMax);
        System.out.println("Max_Sq_Sz: " + MAX_SIZE);
        System.out.println("sumOfOriginalValues: " + sumOfOriginalValues);
        System.out.println("NormalizedSum: " + normalizedSum);

        // Computing the outer radius of the ring
        int radiusOuter = (int) Math.round(Math.sqrt(curRadius * curRadius + (normalizedSum / Math.PI))); // normalized
        // int radiusOuter = (int) Math.round( Math.sqrt(curRadius*curRadius + (
        // sumOfOriginalValues / Math.PI))); // not normalized
        if (curRadius == radiusOuter) {
            radiusOuter++; // for making it also visible
        }
        System.out.println("Filled Ring: Starting Radius: " + curRadius);
        System.out.println("Filled Ring: Outer Radius   : " + radiusOuter);
        
        MathUtils.fillRing(curRadius, radiusOuter, pixels, conf.getN());

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
    
    private void drawRectangleCenter(int x, int y, Rectangle r, DrawStyle d) {
        int leftx = x - (r.getLen() / 2);
        int bottomy = y - (r.getLen() / 2);
        
        if (d == DrawStyle.Outline) {
            drawRectangle(leftx, bottomy, r);
        } else if (d == DrawStyle.Filled) {
            drawFilledRectangle(leftx, bottomy, r);
        }
    }
    
    private void drawFilledRectangle(int x, int y, Rectangle r) {
        if ((x > 0) && (y > 0) && ((x + r.getLen()) < conf.getN()) && ((y + r.getLen()) < conf.getN())) {
            for (int i = 0; i < r.getLen(); i++) {
                for (int j = 0; j < r.getLen(); j++) {
                    pixels[x + i][y + j].setColor(defColor);
                    pixels[x + i][y + j].setOccupied(true);
                }
            }
        }
    }
    
    private void drawRectangle(int x, int y, Rectangle r) {
        if ((x > 0) && (y > 0) && ((x + r.getLen()) < conf.getN()) && ((y + r.getLen()) < conf.getN())) {
            for (int i = 0; i < r.getLen(); i++) {
                pixels[x + i][y].setColor(defColor);
                pixels[x + i][y].setOccupied(true);
                pixels[x][y + i].setColor(defColor);
                pixels[x][y + i].setOccupied(true);
                pixels[x + i][y + r.getLen()].setColor(defColor);
                pixels[x + i][y + r.getLen()].setOccupied(true);
                pixels[x + r.getLen()][y + i].setColor(defColor);
                pixels[x + r.getLen()][y + i].setOccupied(true);
                
                for (int j = 0; j < r.getLen(); j++) {
                    pixels[x + i][y + j].setOccupied(true);
                }
            }
            
        }
    }
    
    private void keepInfoForAxes(int curIndex, int rectLen, int radius) {
        curIndex = Math.min(curIndex, originalValues.size() - 1); // just for robustness (PATCH)

        // About NumOfObjects (Axis X)
        double curIlog = Math.log10(curIndex); // log 10
        int curIlogInt = (int) curIlog; // integer part of log 10
        if (curIlog - curIlogInt == 0) { // curIndex is a power of 10 (todo: 2 check precision issues)
            if (curIlog >= 1) // for not drawing an axis for 1
            {
                radiiWhenScaleofNumberOfVisObjectChanges.add(radius); // add the radius in the arraylist
            }
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
    
    private void keepInfoForRects(int x, int y, int len, int rank) {
        int curIndex = rectangles.size();
        if (curIndex >= originalValues.size() || curIndex >= originalNames.size()) {
            rectangles.add(new Rectangle(x, y, len, 0, "-", defColor));
        } else {
            rectangles.add(new Rectangle(x, y, len, originalValues.get(curIndex), rank, originalNames.get(curIndex),
                    defColor));
        }
    }
    
    private ArrayList<Integer> normalizeList(ArrayList<Double> values) {
        ArrayList<Integer> normalized = new ArrayList<>();
        Normalizer norm = new Normalizer(values.get(values.size() - 1), values.get(0), conf.getMin(), conf.getMax());
        
        for (Double d : values) {
            normalized.add((int) norm.normalizeValue(d));
        }
        
        return normalized;
    }
    
    public void clusteredLODUniform() {
        pic = new Picture(conf.getN(), conf.getN());
        pixels = new Pixel[conf.getN()][conf.getN()];
        
        pic.preparePixels(pixels, conf.getN(), Color.white);
        rectsLen = SyntheticDataGenerator.DecreasingByPercentage(100, 40, 2, 20); // size, maxsize, minsize,
        // decreasePercentage

        //copyInfoToRects(vals);
        int K = rectsLen.size();
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
            cospiCompact(Direction.Expand,
                    ExpandStyle.Spiral,
                    DrawStyle.Filled,
                    5,
                    fromAngle,
                    fromAngle + anglePerDomain,
                    Axes.NoAxes,
                    false);
            fromAngle = fromAngle + anglePerDomain + angleGapAfterADomain;
        }
        pic.updatePixels(pixels, conf.getN());
        //LabelAlgorithms.writeInfoToImageSynthetic(pic.image, N, 16, Color.GRAY, rects.get(0).getLen(),  rects.get(rects.size() - 1).getLen());
        pic.show();
    }
    
    public void clusteredLOD() {
        pic = new Picture(conf.getN(), conf.getN());
        pixels = new Pixel[conf.getN()][conf.getN()];
        
        pic.preparePixels(pixels, conf.getN(), Color.white);
        
        rectsLen = SyntheticDataGenerator.DecreasingByPercentage(100, 40, 2, 20); // size, maxsize, minsize,
        // decreasePercentage
        //copyInfoToRects(vals);
        int K = rectsLen.size();
        System.out.println("SIZE RECTS:" + K);

        //
        int piNum = 2; // for changing the angle range of the entire diagram

        double angleGap = piNum * Math.PI / 20.0; // to 5% tou diskou tha einai diakena
        int DNum = 9; // number of domains

        double anglePerDomain = (piNum * Math.PI - angleGap) / DNum; // h katharh gonia pou prepei na exei kathe enas
        double angleGapAfterADomain = angleGap / DNum;

        // for not uniform:
        int Kbefore = K;
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
            
            cospiCompact(Direction.Expand, ExpandStyle.Spiral, DrawStyle.Filled, 5, fromAngle,
                    fromAngle + anglePerDomain, Axes.NoAxes, false); // This is the stable version
            fromAngle = fromAngle + anglePerDomain + angleGapAfterADomain;
            K = multiplier * K;
            K = Math.min(K, Kbefore); // to avoid out of index
        }
        pic.updatePixels(pixels, conf.getN());
        //LabelAlgorithms.writeInfoToImageSynthetic(pic.image, conf.getN(), 16, Color.GRAY, rects.get(0).getLen(), rects.get(rects.size() - 1).getLen());
        pic.show();
    }
    
}

class MathUtils {
    
    public static ArrayList<Integer> Normalize(ArrayList<Double> a, int min, int max, double dataMinD, double dataMaxD) {
        ArrayList<Integer> norm = new ArrayList<>();
        int dataMin = (int) Math.round(dataMinD);
        int dataMax = (int) Math.round(dataMaxD);
        for (int i = 0; i < a.size(); i++) {
            int v = (int) Math.round(a.get(i));
            int vn = (int) (min + ((v - dataMin + 0.0) * (max - min) / (dataMax - dataMin)));
            //System.out.println(v + " to " + vn);
            norm.add(vn);
        }
        return norm;
    }
    
    public static double degreesOfChord(int chord, int radius) {
        return Math.toDegrees(2 * Math.asin(Math.toRadians(1 * ((0.0 + chord) / (2 * radius)))));
    }
    
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
    
    public static void visualizeAxes(ArrayList vals, VisConfig conf, ArrayList<Integer> radiiWhenScaleOfValuesChanges, ArrayList<Integer> radiiWhenScaleofNumberOfVisObjectChanges, Pixel pixels[][], int N) {
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
    
    public static void auxiliaryCircle(int rad, Color color, int width, double angleMin, double angleMax, Pixel pixels[][], int N) {
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
                        if (isInCanvas(x, y + j, N)) {
                            pixels[x][y + j].setColor(color);
                        }
                        if (isInCanvas(x + j, y, N)) {
                            pixels[x + j][y].setColor(color);
                        }
                    }
                } else {
                    System.out.println("AuxiliaryCircle: Cannot draw, the size of the canvas is too small");
                }
            }
        }
    }
    
    public static boolean isInCanvas(int x, int y, int N) {
        return ((x >= 0) && (y >= 0) && (x < N) && (y < N));
    }
    
    public static void fillRing(int radiusFrom, int radiusTo, Pixel pixels[][], int N) {
        for (int i = radiusFrom; i < radiusTo; i++) {
            auxiliaryCircle(i, Color.blue, 1, pixels, N);
        }
    }
    
    public static void auxiliaryCircle(int rad, Color color, int width, Pixel pixels[][], int N) {
        auxiliaryCircle(rad, color, width, 0, 2 * Math.PI, pixels, N);
    }
    
}
