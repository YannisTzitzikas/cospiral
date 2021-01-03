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

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import layoutAlgs.VisConfig;
import layoutAlgs.params.ShapeGaps;

/**
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 * @author Yannis Tzitzikas (yannistzitzikas@gmail.com): A few additions.
 * This class contains frame utilities to be used for the CoSpi application.
 *
 */
public class GUIUtilities {
	
	/**
	 * Open a website using the default web browser
	 * @param URL The URL of the site to be opened
	 */
	public static void openSiteInBrowser(String URL) {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
    	    try {Desktop.getDesktop().browse(new URI(URL));}
    	    catch (Exception e) {e.printStackTrace();}
    	}
	}
	
	/**
	 * A frame to save a raster image
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
				ImageIO.write(pic,"jpg",fileToSave);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * A frame to select a file
	 * @return the filepath of the file selected
	 */
	public static String fileSelectionGUI(){
		String filepath="";
		try {
			
		//java.net.URL imgURL = getClass().getResource(path);
			
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("./Resources/DemoDatasets")); // for opening the folder Resources that has examples
		
		fileChooser.setDialogTitle("Select a file");
		//int userSelection = fileChooser.showSaveDialog(null);
		int userSelection = fileChooser.showOpenDialog(null); // prevVersion: 		showSaveDialog(null);
		
		
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			filepath = file.getAbsolutePath();
			System.out.println("The path of the selected file is: " + filepath);
		}
		
		}catch(Exception e) {
			//e.printStackTrace();
		}
		return filepath; //It returns a string in order to use it easily while creating a file
	}

	/**
	 * A frame to select a folder
	 * @return the filepath of the folder
	 */
	public static String folderSelectionGUI(){
		String filepath="";
		try {
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setDialogTitle("Select folder for the HTML script");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int userSelection = fileChooser.showSaveDialog(null);
		
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			filepath = file.getAbsolutePath();
			System.out.println("The path of the selected file is: " + filepath);
		}
		
		}catch(Exception e) {
			System.out.println("Folder not selected.");
		}
		return filepath; //It returns a string in order to use it easily while creating a file
 	}
	
	public static String saveCurrentProgress(VisConfig conf,String path) throws IOException {
		
		File cospi = new File(path+".cospi");
        
		FileOutputStream fr = new FileOutputStream(cospi);
        OutputStreamWriter writer = new OutputStreamWriter(fr, "UTF-8");
		
        String information = conf.getMin() + "\n" + conf.getMax() + "\n" + conf.getAngleMax() + "\n" + conf.getAngleMin() + "\n" + conf.getRoadSize() + "\n" + conf.getLabelColor().getRGB() + "\n" + conf.getLabelDecreasingRate() + "\n" + conf.getColor().getRGB() + "\n";
				
		if(conf.isEnableInfo()) information += 1 + "\n";
		else information += 0 + "\n";
		
		if(conf.isShowName()) information += 1 + "\n";
		else information += 0 + "\n";
		
		if(conf.isShowRank()) information += 1 + "\n";
		else information += 0 + "\n";
			
		if(conf.isShowVal()) information += 1 + "\n";
		else information += 0 + "\n";
		
		if(conf.isAllowOverlap()) information += 1 + "\n";
		else information += 0 + "\n";
		
		if(conf.getShapeGaps() == ShapeGaps.Normal) information += 1 + "\n";
		else information += 0 + "\n";
		
		switch(conf.getDrawStyle()) {
		case Outline:
			information += 1 + "\n";
			break;
		case Filled:
			information += 0 + "\n";
		default:
			information += 1 + "\n";
			break;
		}
		
		switch(conf.getExpandStyle()) {
		case Spiral:
			information += 1 + "\n";
			break;
		case Ring:
			information += 0 + "\n";
		default:
			information += 1 + "\n";
			break;
		}
		
		switch(conf.getDirection()) {
		case Expand:
			information += 1 + "\n";
			break;
		case Shrink:
			information += 0 + "\n";
		default:
			information += 1 + "\n";
			break;
		}
		
		switch(conf.getAxes()) {
		case AxisX:
			information += 1 + "\n";
			break;
		case AxisY:
			information += 2 + "\n";
		case AxesXY:
			information += 3 + "\n";
		case NoAxes:
			information += 0 + "\n";
		default:
			information += 0 + "\n";
			break;
		}
		
		writer.write(information);
        writer.flush();
		writer.close();
        
		return cospi.getAbsolutePath();	
	}
	
	public static VisConfig loadSavedProgress(String path) {
		VisConfig conf = null;
		
		return conf;
	}
	
	public static void main(String [] args) {
		//System.out.println(Color.red.);
	}
}
