package utils;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
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
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setDialogTitle("Select a file");
		int userSelection = fileChooser.showSaveDialog(null);
		
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			filepath = file.getAbsolutePath();
			System.out.println("The path of the selected file is: " + filepath);
		}
		
		}catch(Exception e) {
			e.printStackTrace();
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
}
