package layoutAlgs;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import layoutAlgs.params.LabelStyle;
import utils.Picture;
import utils.Rectangle;

/**
 * 
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 * This class contains the algorithms to add labels to a CoSpi layout.
 *
 */
public class LabelAlgorithms {
	
	/**
	 * Adds labels to the rectangles of the layout based on the visual configurations.
	 * @param conf
	 * @param rectangles
	 * @param pic
	 * @param N
	 */
	public static void addLabels(VisConfig conf,ArrayList<Rectangle>rectangles,Picture pic,int N) {
		
		//If decreasing rate is >= exit the method.
		if(conf.getLabelDecreasingRate() <= 0) return;
		
		//Getting the configurations
		int decRate = conf.getLabelDecreasingRate();
		Color color = conf.getLabelColor();
		Graphics g  = pic.image.getGraphics();

		//Iterating the list of the layout rectangles
		for(int i=0; i<rectangles.size(); i++) {
			
			//Getting their tags
			String rank  = rectangles.get(i).getRank()+"";
			String name  = rectangles.get(i).getOriginalName();
			String value = rectangles.get(i).getOriginalValue() + "";
			
			//Getting the side
			int side = rectangles.get(i).getLen();
			
			//Setting the label font
			Font font = new Font("Sansserif", Font.PLAIN, side/decRate);
			  
			//Apply the color and font settings
		  	g.setColor(color);		  
		  	g.setFont(font);
			
		  	//Getting the font metrics to calcute the label center
		  	FontMetrics metrics = g.getFontMetrics(font);
			
		  	//Getting the height of the text
		  	int height   = metrics.getHeight();		  
		  	
		  	//Setting the distance between the labels
		  	int diffRate = height; 
			
		  	//Getting each text size
		  	int nameWidth  = g.getFontMetrics().stringWidth(name);
		  	int valueWidth = g.getFontMetrics().stringWidth(value);
		  	int rankWidth  = g.getFontMetrics().stringWidth(rank);
		  	
		  	//Calculate x center coordinates:
		  	int xRank  = rectangles.get(i).getX() - (rankWidth/2);
		  	int xName  = rectangles.get(i).getX() - (nameWidth/2);
		  	int xValue = rectangles.get(i).getX() - (valueWidth/2);
		  	
		  	//Calculate the y center coordinates
		  	int y     = N-rectangles.get(i).y - height/2 + metrics.getAscent();
		 	int yDown = N-rectangles.get(i).y - height/2 + diffRate + metrics.getAscent();
		  	int yUp   = N-rectangles.get(i).y - height/2 - diffRate + metrics.getAscent();
		  	
		  	//Getting how many labels the layout includes (to re-calculate the y coordinates properly)
		  	LabelStyle selectedLabel            = LabelStyle.noLabel;
		  	if(conf.isShowRank()) selectedLabel = LabelStyle.values()[selectedLabel.ordinal() + 1];
		  	if( conf.isShowVal()) selectedLabel = LabelStyle.values()[selectedLabel.ordinal() + 1];
		  	if(conf.isShowName()) selectedLabel = LabelStyle.values()[selectedLabel.ordinal() + 1];
		  	
		  	//Calculate the y coordinates based on the number of labels to be added
		  	switch(selectedLabel) {
		  	case oneLabel:
		  		//Draw the selected tag in the center of every rectangle
		  		if(conf.isShowRank()) g.drawString(rank, xRank, y);
		  		if(conf.isShowVal() ) g.drawString(value, xValue, y);
		  		if(conf.isShowName()) g.drawString(name, xName, y);
				break;
		  	case twoLabels:
		  		//Draw the two labels in the center of the rectangle
		  		diffRate = height/2;
		  		yDown    = N-rectangles.get(i).y - height/2 + diffRate + metrics.getAscent();
			  	yUp      = N-rectangles.get(i).y - height/2 - diffRate + metrics.getAscent();
		  		if(conf.isShowRank()) {
		  			g.drawString(rank, xRank, yUp);
		  			if(conf.isShowVal()) g.drawString(value, xValue, yDown);
		  			else g.drawString(name, xName, yDown);
		  		}
		  		else {
		  			g.drawString(name, xName, yUp);
		  			g.drawString(value, xValue, yDown);
		  		}
		  		break;
		  	case threeLabels:
		  		//Draw all three labels in the center of rectangle
		  		diffRate = height;
		  		yDown    = N-rectangles.get(i).y - height/2 + diffRate + metrics.getAscent();
			  	yUp      = N-rectangles.get(i).y - height/2 - diffRate + metrics.getAscent();
			  	g.drawString(rank, xRank, yUp);
			  	g.drawString(name, xName, y);
			  	g.drawString(value, xValue, yDown);
		  		break;
			default:
				break;
		  	}
		}
		//All labels added.
	}
	
	/**
	 * Add information to bottom of the image.
	 * @param pic
	 * @param N
	 * @param size
	 * @param color
	 * @param MAX_SIZE
	 * @param MIN_SIZE
	 */
	public static void writeInfoToImage(Image pic,int N,int size,Color color,int MAX_SIZE,int MIN_SIZE,int realMax, int realMin,int K) {
		
		//Getting the text to be drawn
		String values = "Entities: " + K;
		String nMax   = "Normilized Max: "+String.valueOf(MAX_SIZE);
		String nMin   = "Normilized Min: "+String.valueOf(MIN_SIZE);
		String nRmax  = "Actual Max: "+  String.valueOf(realMax);
		String nRmin  = "Actual Min: "+  String.valueOf(realMin);

		//Get the graphics of the image
		Graphics g = pic.getGraphics();
	
		//Set the text font
		Font font = new Font("Sansserif", Font.PLAIN, size);
		g.setColor(color);
		g.setFont(font);
		
		//Get the metrics to calculate the size of the text
		FontMetrics metrics = g.getFontMetrics(font);
		
		//Get the height and calculate the max width of the text
		int height   = metrics.getHeight();
		int maxWidth = Math.max(g.getFontMetrics().stringWidth(values),(Math.max(g.getFontMetrics().stringWidth(nMax),g.getFontMetrics().stringWidth(nRmax))));
		
		//Center text under the spiral at the right bottom corner
		int x = N-maxWidth-4;
		int y1 = N-height;
		int y2 = N-2*height;
		int y3 = N-3*height;
		int y4 = N-4*height;
		int y5 = N-5*height;
		
		//Draw the information to the layout
		g.drawString(nMax, x,y3);
		g.drawString(nMin, x,y1);
		g.drawString(nRmin,x,y2);
		g.drawString(nRmax,x,y4);
		g.drawString(values,x,y5);
		
	}

	public static void writeInfoToImageSynthetic(Image pic,int N,int size,Color color,int realMax, int realMin) {
		//Getting the text to be drawn
		String nRmax = "Max: "+  String.valueOf(realMax);
		String nRmin = "Min: "+  String.valueOf(realMin);

		//Get the graphics of the image
		Graphics g = pic.getGraphics();
			
		//Set the text font
		Font font = new Font("Sansserif", Font.PLAIN, size);
		g.setColor(color);
		g.setFont(font);
				
		//Get the metrics to calculate the size of the text
		FontMetrics metrics = g.getFontMetrics(font);
				
		//Get the height and calculate the max width of the text
		int height   = metrics.getHeight();
		int maxWidth = g.getFontMetrics().stringWidth(nRmax);
				
		//Center text under the spiral at the right bottom corner
		int x = N-maxWidth-4;
		int y1 = N-height;
		int y2 = N-2*height;
		int y3 = N-3*height;
		int y4 = N-4*height;
				
		//Draw the information to the layout
		g.drawString(nRmin,x,y1);
		g.drawString(nRmax,x,y2);		
	}
}
