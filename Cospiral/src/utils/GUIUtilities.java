package utils;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import layoutAlgs.VisConfig;
import layoutAlgs.params.Axes;
import layoutAlgs.params.Direction;
import layoutAlgs.params.DrawStyle;
import layoutAlgs.params.ExpandStyle;
import layoutAlgs.params.ShapeGaps;

/**
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 * @author Yannis Tzitzikas (yannistzitzikas@gmail.com): A few additions. This
 *         class contains frame utilities to be used for the CoSpi application.
 *
 */
public class GUIUtilities {

	/**
	 * Open a website using the default web browser
	 * 
	 * @param URL The URL of the site to be opened
	 */
	public static void openSiteInBrowser(String URL) {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			try {
				Desktop.getDesktop().browse(new URI(URL));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * A frame to save a raster image
	 * 
	 * @param pic The picture to be saved
	 */
	public static void saveImageGUI(BufferedImage pic) {
		JFrame modelFrame = new JFrame();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Use a .jpg extension.");
		int userSelection = fileChooser.showSaveDialog(modelFrame);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			System.out.println("Save as file(add a photo extension): " + fileToSave.getAbsolutePath());
			try {
				ImageIO.write(pic, "jpg", fileToSave);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void saveProgressGUI(VisConfig conf) {
		JFrame modelFrame = new JFrame();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select save location.");
		int userSelection = fileChooser.showSaveDialog(modelFrame);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			String filePath = fileChooser.getSelectedFile().getAbsolutePath();
			try {
				saveCurrentProgress(conf, filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * A frame to select a file
	 * 
	 * @return the filepath of the file selected
	 */
	public static String fileSelectionGUI() {
		String filepath = "";
		try {

			// java.net.URL imgURL = getClass().getResource(path);

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("./Resources/DemoDatasets")); // for opening the folder Resources
																					// that has examples

			fileChooser.setDialogTitle("Select a file");
			// int userSelection = fileChooser.showSaveDialog(null);
			int userSelection = fileChooser.showOpenDialog(null); // prevVersion: showSaveDialog(null);

			if (userSelection == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				filepath = file.getAbsolutePath();
				System.out.println("The path of the selected file is: " + filepath);
			}

		} catch (Exception e) {
			// e.printStackTrace();
		}
		return filepath; // It returns a string in order to use it easily while creating a file
	}

	/**
	 * A frame to select a folder
	 * 
	 * @return the filepath of the folder
	 */
	public static String folderSelectionGUI() {
		String filepath = "";
		try {
			JFileChooser fileChooser = new JFileChooser();

			fileChooser.setDialogTitle("Select folder");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int userSelection = fileChooser.showSaveDialog(null);

			if (userSelection == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				filepath = file.getAbsolutePath();
				// System.out.println("The path of the selected file is: " + filepath);
			}

		} catch (Exception e) {
			System.out.println("Folder not selected.");
		}
		return filepath; // It returns a string in order to use it easily while creating a file
	}

	//public static VisConfig 
	
	public static String saveCurrentProgress(VisConfig conf, String path) throws IOException {

		File cospi = new File(path + ".cospi");

		FileOutputStream fr = new FileOutputStream(cospi);
		OutputStreamWriter writer = new OutputStreamWriter(fr, "UTF-8");

		String information = 
				  conf.getMin() + "\n" 
				+ conf.getMax() + "\n"
				+ conf.getAngleMax() + "\n"
				+ conf.getAngleMin() + "\n"
				+ conf.getRoadSize() + "\n"
				+ conf.getLabelColor().getRGB() + "\n"
				+ conf.getLabelDecreasingRate() + "\n" 
				+ conf.getColor().getRGB() + "\n";

		if (conf.isEnableInfo())
			information += 1 + "\n";
		else
			information += 0 + "\n";

		if (conf.isShowName())
			information += 1 + "\n";
		else
			information += 0 + "\n";

		if (conf.isShowRank())
			information += 1 + "\n";
		else
			information += 0 + "\n";

		if (conf.isShowVal())
			information += 1 + "\n";
		else
			information += 0 + "\n";

		if (conf.isAllowOverlap())
			information += 1 + "\n";
		else
			information += 0 + "\n";

		if (conf.getShapeGaps() == ShapeGaps.Normal)
			information += 1 + "\n";
		else
			information += 0 + "\n";

		switch (conf.getDrawStyle()) {
		case Outline:
			information += 1 + "\n";
			break;
		case Filled:
			information += 0 + "\n";
			break;
		default:
			information += 1 + "\n";
			break;
		}

		switch (conf.getExpandStyle()) {
		case Spiral:
			information += 1 + "\n";
			break;
		case Ring:
			information += 0 + "\n";
			break;
		default:
			information += 1 + "\n";
			break;
		}

		switch (conf.getDirection()) {
		case Expand:
			information += 1 + "\n";
			break;
		case Shrink:
			information += 0 + "\n";
			break;
		default:
			information += 1 + "\n";
			break;
		}

		switch (conf.getAxes()) {
		case AxisX:
			information += 1 + "\n";
			break;
		case AxisY:
			information += 2 + "\n";
			break;
		case AxesXY:
			information += 3 + "\n";
			break;
		case NoAxes:
			information += 0 + "\n";
			break;
		default:
			information += 0 + "\n";
			break;
		}

		writer.write(information);
		writer.flush();
		writer.close();

		return cospi.getAbsolutePath();
	}

	public static VisConfig loadSavedProgress(String filepath) {
		File cospiFile = new File(filepath);
		VisConfig conf = new VisConfig();
		Scanner myReader;
		ArrayList<Integer> data = new ArrayList<>();
		try {
			myReader = new Scanner(cospiFile);
			while (myReader.hasNextLine()) {
				String line = myReader.nextLine();
				data.add(Integer.parseInt(line));
				// System.out.println(data);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < data.size(); i++) {
			switch (i) {
			case 0:
				conf.setMin(data.get(i));
				break;
			case 1:
				conf.setMax(data.get(i));
				break;
			case 2:
				conf.setAngleMax(data.get(i));
				break;
			case 3:
				conf.setAngleMin(data.get(i));
				break;
			case 4:
				conf.setRoadSize(data.get(i));
				break;
			case 5:
				conf.setLabelColor(new Color(data.get(i)));
				break;
			case 6:
				conf.setLabelDecreasingRate(data.get(i));
				break;
			case 7:
				conf.setColor(new Color(data.get(i)));
				break;
			case 8:
				if (data.get(i) == 0) {
					conf.setEnableInfo(false);
				} else {
					conf.setEnableInfo(true);
				}
				break;
			case 9:
				if (data.get(i) == 0) {
					conf.setShowName(false);
				} else {
					conf.setShowName(true);
				}
				break;
			case 10:
				if (data.get(i) == 0) {
					conf.setShowRank(false);
				} else {
					conf.setShowRank(true);
				}
				break;
			case 11:
				if (data.get(i) == 0) {
					conf.setShowVal(false);
				} else {
					conf.setShowVal(true);
				}
				break;
			case 12:
				if (data.get(i) == 0) {
					conf.setAllowOverlap(false);
				} else {
					conf.setAllowOverlap(true);
				}
				break;
			case 13:
				if (data.get(i) == 0) {
					conf.setShapeGaps(ShapeGaps.Minimum);
				} else {
					conf.setShapeGaps(ShapeGaps.Normal);
				}
				break;
			case 14:
				if (data.get(i) == 0) {
					conf.setDrawStyle(DrawStyle.Filled);
				} else {
					conf.setDrawStyle(DrawStyle.Outline);
				}
				break;
			case 15:
				if (data.get(i) == 0) {
					conf.setExpandStyle(ExpandStyle.Ring);
				} else {
					conf.setExpandStyle(ExpandStyle.Spiral);
				}
				break;
			case 16:
				if (data.get(i) == 0) {
					conf.setDirection(Direction.Shrink);
				} else {
					conf.setDirection(Direction.Expand);
				}
				break;
			case 17:
				switch (data.get(i)) {
				case 0:
					conf.setAxes(Axes.NoAxes);
					break;
				case 1:
					conf.setAxes(Axes.AxisX);
					break;
				case 2:
					conf.setAxes(Axes.AxisY);
					break;
				case 3:
					conf.setAxes(Axes.AxesXY);
					break;
				}
				/* add enabled labels! */
			}

		}

		return conf;
	}

	public static void main(String[] args) {

	}
}
