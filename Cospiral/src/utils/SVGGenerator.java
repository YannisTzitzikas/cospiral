package utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import layoutAlgs.VisConfig;
import layoutAlgs.params.DrawStyle;
import layoutAlgs.params.LabelStyle;

/**
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 * This class contains the SVG generator to create SVG version of CoSpi layouts.
 */
public class SVGGenerator {

	/**
	 * Creates the SVG version of a CoSpi layout by generating HTML script.
	 * @param conf The Visual Configurations.
	 * @param rects The rectangles of the spiral.
	 * @param N The size of the SVG and picture resolution.
	 * @param pic The picture created from the layout.
	 * @throws IOException
	 */
	public static void createSVG(VisConfig conf, ArrayList<Rectangle>rects, int N,Image pic,
									String filepath,String filename) throws IOException{
		
		//Create the html file
		FileWriter editor = new FileWriter( filepath + "/" + filename );
		//System.out.println("SVG file to be saved at: " + filepath + "/" + filename);
		//FileWriter editor = new FileWriter(filename);
		
		//Basic html tags
		String htmlHead = "<!DOCTYPE html>\n<html>\n<body>\n";
		String htmlEnd = "</body>\n</html>";
		
		//xml info
		String xmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";
		
		//Charset in case characters do not show
		String charset = "<meta charset=\"ASCII\">";
		
		//The main script
		String body = "";

		//The labels as strings
		String label_name = "";
		String label_val  = "";
		String label_rank = "";
		
		//Svg creation tags
		String start = "<svg width=\""+ N + "\" height=\"" + N +"\" viewBox=\"0 0 "+ N +" "+ N +"\" xmlns=\"http://www.w3.org/2000/svg\">\n";
		String end = "</svg>";
		
		//Simulate the image creation to center y text.
		Graphics g = pic.getGraphics();
		
		int decRate = conf.getLabelDecreasingRate();
		
		//Iteration to draw the rectangles
		for(int i = 0; i<rects.size(); i++) {

			//The rectangles color
			Color rectColor= rects.get(i).getColor();
			
			//Filled or outline
			String fill = "none";
			
			//Get rgb to use as xml command
			int red   = rectColor.getRed();
			int green = rectColor.getGreen();
			int blue  = rectColor.getBlue();
			
			//Default color
			String Rcolor = "rgb("+red+","+ green + "," + blue +")";
			
			if(conf.getDrawStyle() == DrawStyle.Filled) {
				fill = Rcolor;
			}
			
			//The rectangle side
			int side = rects.get(i).getLen();

			//Rectangle center coordinates
			int x = rects.get(i).getX();
			int y = N - rects.get(i).getY();
			
			//Coordinates to draw rectangle with center x,y
			int xrect = rects.get(i).getX()-(side/2);
			int yrect = N - rects.get(i).getY() -(side/2);
			
			//Initialize the labels
			String rank  = rects.get(i).getRank()+"";
			String name  = rects.get(i).getOriginalName();
			String value = rects.get(i).getOriginalValue()+"";			

			//Font simulation
			Font font = new Font("Sansserif",Font.PLAIN,side/decRate);
			
			//Get font info
			g.setFont(font);
			FontMetrics metrics = g.getFontMetrics(font);
			
			//The text height
			int height = metrics.getHeight();
			
			//Rate to place both labels
			int diffRate = height;
			if(diffRate <= 1) {
				diffRate +=1;
			}
						
			//Label coordinates
			int xlab = x; //dominant-baseline="middle"
			int ylab = y;//- height/2 + metrics.getAscent();
			
			//Label colors
			Color labColor = conf.getLabelColor();
			red   = labColor.getRed();
			green = labColor.getGreen();
			blue  = labColor.getBlue();
			String color = "rgb("+red+","+ green + "," + blue +")";
						
			LabelStyle selectedLabel            = LabelStyle.noLabel;
		  	if(conf.isShowRank()) selectedLabel = LabelStyle.values()[selectedLabel.ordinal() + 1];
		  	if( conf.isShowVal()) selectedLabel = LabelStyle.values()[selectedLabel.ordinal() + 1];
		  	if(conf.isShowName()) selectedLabel = LabelStyle.values()[selectedLabel.ordinal() + 1];
		  			  	
		  	switch(selectedLabel) {
		  	case oneLabel:
		  		if(conf.isShowRank()) label_rank = "<text x=\""+(xlab)+"\" y=\""+(ylab)+"\"  font-family=\"Sansserif\" font-size=\""+(side/decRate)+"\" fill=\""+ color +"\" dominant-baseline=\"middle\" text-anchor=\"middle\">"+rank+"</text>\n";	
		  		if(conf.isShowVal() ) label_val  = "<text x=\""+(xlab)+"\" y=\""+(ylab)+"\"  font-family=\"Sansserif\" font-size=\""+(side/decRate)+"\" fill=\""+ color +"\" dominant-baseline=\"middle\" text-anchor=\"middle\">"+value+"</text>\n";	
		  		if(conf.isShowName()) label_name = "<text x=\""+(xlab)+"\" y=\""+(ylab)+"\"  font-family=\"Sansserif\" font-size=\""+(side/decRate)+"\" fill=\""+ color +"\" dominant-baseline=\"middle\" text-anchor=\"middle\">"+name+"</text>\n";	
				break;
		  	case twoLabels:
		  		if(conf.isShowRank()) {
		  			diffRate = height/2;
		  			label_rank = "<text x=\""+(xlab)+"\" y=\""+(ylab - diffRate)+"\"  font-family=\"Sansserif\" font-size=\""+(side/decRate)+"\" fill=\""+ color +"\" dominant-baseline=\"middle\" text-anchor=\"middle\">"+rank +"</text>\n";
		  			if(conf.isShowVal()) { 
		  				label_val =  "<text x=\""+(xlab)+"\" y=\""+(ylab + diffRate)+"\"  font-family=\"Sansserif\" font-size=\""+(side/decRate)+"\" fill=\""+ color +"\" dominant-baseline=\"middle\" text-anchor=\"middle\">"+value+"</text>\n";	
		  			}
		  			else {
		  				label_name = "<text x=\""+(xlab)+"\" y=\""+(ylab + diffRate)+"\"  font-family=\"Sansserif\" font-size=\""+(side/decRate)+"\" fill=\""+ color +"\" dominant-baseline=\"middle\" text-anchor=\"middle\">"+name +"</text>\n";	
		  			}
		  		}
		  		else {
	  				label_name = "<text x=\""+(xlab)+"\" y=\""+(ylab - diffRate)+"\"  font-family=\"Sansserif\" font-size=\""+(side/decRate)+"\" fill=\""+ color +"\" dominant-baseline=\"middle\" text-anchor=\"middle\">"+name +"</text>\n";	
		  		}
		  		break;
		  	case threeLabels:
		  		label_rank = "<text x=\""+(xlab)+"\" y=\""+(ylab - diffRate)+"\"  font-family=\"Sansserif\" font-size=\""+(side/decRate)+"\" fill=\""+ color +"\" dominant-baseline=\"middle\" text-anchor=\"middle\">"+rank +"</text>\n";	
				label_name = "<text x=\""+(xlab)+"\" y=\""+(ylab)           +"\"  font-family=\"Sansserif\" font-size=\""+(side/decRate)+"\" fill=\""+ color +"\" dominant-baseline=\"middle\" text-anchor=\"middle\">"+name +"</text>\n";	
				label_val  =  "<text x=\""+(xlab)+"\" y=\""+(ylab + diffRate)+"\"  font-family=\"Sansserif\" font-size=\""+(side/decRate)+"\" fill=\""+ color +"\" dominant-baseline=\"middle\" text-anchor=\"middle\">"+value+"</text>\n";	
		  		break;
			default:
				break;
		  	}
		  	
			//Create every rectangle
			body += "	<rect x=\""+(xrect)+"\" y=\""+(yrect)
				 +"\" width=\""+side+"\" height=\""+side
				 +"\" fill=\""+(fill)+"\" stroke-width=\"0.3\" stroke=\""+Rcolor+"\"/>\n" 
				 + label_rank +label_name + label_val;							
		}
		
		//Rectangles drawn
		
		//Write the html script to the file
		editor.write(htmlHead);
		//editor.write(title);
		editor.write(xmlHead);
		//editor.write(charset);
		editor.write(start);
		editor.write(body);
		editor.write(end);
		editor.write(htmlEnd);
		
		//Close the file
		editor.close();
	}
	
	}
