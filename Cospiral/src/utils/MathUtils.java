package utils;

import java.awt.Color;
import java.util.ArrayList;

import layoutAlgs.VisConfig;
import layoutAlgs.params.Axes;

/**
 * This class holds drawing and math utilities to create and draw CoSpi layouts.
 * 
 * @author Yannis Tzitzikas
 * @author Manos Chatzakis (additions)
 */
public class MathUtils {

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
