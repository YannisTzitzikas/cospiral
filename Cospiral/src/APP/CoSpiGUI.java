package APP;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

//import com.sun.glass.events.WindowEvent;
import java.awt.event.WindowEvent;

import layoutAlgs.CoSpi;
import layoutAlgs.VisConfig;
import layoutAlgs.params.Axes;
import layoutAlgs.params.Direction;
import layoutAlgs.params.DrawStyle;
import layoutAlgs.params.ExpandStyle;
import layoutAlgs.params.ShapeGaps;
import utils.GUIUtilities;
import utils.SVGGenerator;

/**
 * Version 3.0: Files merged, name changed and code cleaned, August 2020.
 * This class creates a UI environment for the CoSpi algorithm, to easily visualize datasets.
 * BUG: When new visualization begins, checkboxes don't refresh, as they are local objects. Make global to fix?.. 
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 * @supervisor Yannis Tzitzikas (tzitzik@ics.forth.gr) - Lead author of the project.
 */
public class CoSpiGUI extends JFrame{
	
	int MAX; //Normalized max
	int MIN; //Normalized min
	
	VisConfig conf    = new VisConfig(); //Visual Configurations
	FileConf fileConf = new FileConf(0,1,2,true); //File Configurations
	
	boolean fileSelected = false;  // true if the user has selected a file (used for enabling/disabling the rest menus).
	
	// menus
	JMenu designMenu;
	JMenu labelsAxesMenu;
	JMenu exportMenu;
	JFrame parFrame; // the frame with the parameters
	
	
	
	/**
	 * Creates the menu bar and runs the app.
	 */
	public CoSpiGUI() {
		// ICON 
		ImageIcon icon;
		String path = "/Image.png";
		java.net.URL imgURL = getClass().getResource(path);
		System.out.println(getClass());
		if (imgURL != null) {
			icon = new ImageIcon(imgURL, "Eikonidio"); //imgURL
			this.setIconImage(icon.getImage());
		} else {
			System.err.println("Couldn't find file: " + path);
		}
		
		//int RESOLUTION = Integer.parseInt(JOptionPane.showInputDialog(new Frame("Resolution Selection"),"Visualization Resolution(normally 1000):"));  
		//CoSpi.N = RESOLUTION;
		
		System.out.println("CoSpi application started.");
		
		int RESOLUTION = CoSpi.N;
		int WIDTH = RESOLUTION;
		int HEIGHT = RESOLUTION+50;
		
		JMenuBar menu = new JMenuBar();
		setResizable(false);
		setSize(WIDTH,HEIGHT);
		setTitle("CoSpi APP");
		
		initializeConfig();
		createMenuBar(menu);
		
		setJMenuBar(menu);
		setLayout(null);
		setVisible(true);
	}
	
	/**
	 * Initializes the conf object with default configurations.
	 */
	public void initializeConfig() {
		conf.setDrawStyle(DrawStyle.Filled);  
		conf.setDirection(Direction.Expand); 
		conf.setExpandStyle(ExpandStyle.Spiral);
		conf.setShapeGaps(ShapeGaps.Minimum); 
		conf.setAngleMin(0);
		conf.setAngleMax(2*Math.PI);
		conf.setRoadSize(4);
		conf.setAxes(Axes.NoAxes);
		conf.setRectColor(Color.orange); // blue
		conf.setEnableInfo(true); 
		conf.setLabelParams(false, true, true, Color.black, 4);  // rank,  name, val ,Color , decRate) {
		conf.setAllowOverlap(false);
	}

	/**
	 * Creates the main menu bar of the app.
	 * @param menu
	 */
	public void createMenuBar(JMenuBar menu) {
		createProjectMenu(menu);
		createDesignMenu(menu);
		createLabelsAxesMenu(menu);
		createExportMenu(menu);
		createHelpMenu(menu);
	}
	
	/**
	 * Creates the project menu bar section.
	 * @param menu
	 */
	public void createProjectMenu(JMenuBar menu) {
		JMenu projectMenu = new JMenu("File");
		projectMenu.setMnemonic('F');
		JMenu newFile = new JMenu("Open");
		JMenuItem classic = new JMenuItem("Classic (visualize two name-value columns from a file)");
		classic.setToolTipText("Allows you to visualize two  name-value columns from a file");
		
		JMenuItem pieChart = new JMenuItem("Pie Chart (visualize three  name-value-group columns from a file");
		pieChart.setToolTipText("Allows you to visualize three  name-value-group columns from a file");
		
		classic.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	fileConf = new FileConf(0,1,false,GUIUtilities.fileSelectionGUI());
            	columnParametersClassic();
              	
         }});
		
		pieChart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	fileConf = new FileConf(0,2,1,true,GUIUtilities.fileSelectionGUI());
            	columnParametersPieChart();
            	
       }});
		
		newFile.add(classic);
		newFile.add(pieChart);
		projectMenu.add(newFile);
		menu.add(projectMenu);
	}

	/**
	 * Creates the design menu bar section.
	 * @param menu
	 */
	public void createDesignMenu(JMenuBar menu) {
		designMenu     = new JMenu("Design");
		designMenu.setMnemonic('D');
		
		/*
		 * Creating mode menu
		 */
		JMenu mode     	  = new JMenu("Spiral Style");
		//JMenuItem classic = new JMenuItem("Classic");
		JCheckBoxMenuItem classic = new JCheckBoxMenuItem("Classic Spiral",true);
		JCheckBoxMenuItem ring    = new JCheckBoxMenuItem("Ring",false);
		classic.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        		conf.setExpandStyle(ExpandStyle.Spiral); 
        		classic.setState(true);
        		ring.setState(false);
        		visualizeOnFrame();
            }});
		ring.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        		conf.setExpandStyle(ExpandStyle.Ring);
        		classic.setState(false);
        		ring.setState(true);
            	visualizeOnFrame();
            }});
		mode.add(classic);
		mode.add(ring);
		
		/*
		 * Creating draw menu
		 */
		JMenu shapeDrawStyle = new JMenu("Fill Mode");
		JCheckBoxMenuItem filled     = new JCheckBoxMenuItem("Filled",true);
		JCheckBoxMenuItem outline    = new JCheckBoxMenuItem("OutLine",false);
		filled.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        		conf.setDrawStyle(DrawStyle.Filled); 
        		filled.setState(true);
        		outline.setState(false);
        		visualizeOnFrame();
            }});
		outline.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        		conf.setDrawStyle(DrawStyle.Outline);
        		outline.setState(true);
        		filled.setState(false);
        		visualizeOnFrame();
            }});
		shapeDrawStyle.add(filled);
		shapeDrawStyle.add(outline);
		
		/*
		 * Creating direction style
		 */
		JMenu directionStyle = new JMenu("Spiral Direction");
		JCheckBoxMenuItem expand     = new JCheckBoxMenuItem("Expanding",true);
		JCheckBoxMenuItem shrink     = new JCheckBoxMenuItem("Shrinking",false);
		expand.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	conf.setDirection(Direction.Expand);  
            	expand.setState(true);
        		shrink.setState(false);
            	visualizeOnFrame();
            }});
		
		shrink.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	conf.setDirection(Direction.Shrink); 
            	shrink.setState(true);
        		expand.setState(false);
                visualizeOnFrame();
            }});
		directionStyle.add(expand);
		directionStyle.add(shrink);
	
		JMenu collisions = new JMenu("Collisions");
		JCheckBoxMenuItem allowCollisions     = new JCheckBoxMenuItem("Allowed",false);
		allowCollisions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(allowCollisions.getState()) {
            		conf.setAllowOverlap(true);
            	}
            	else {
            		conf.setAllowOverlap(false);
            	}
                visualizeOnFrame();
            }});
		collisions.add(allowCollisions);
		/*
		 * Creating parameters menu
		 */
		JMenuItem parameters = new JMenuItem("Other Parameters (Sizes, Colors, etc)");
		parameters.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (parFrame==null) setLayoutParameters(); // opens the LayoutParameters if not already opened
            }});
		
		JComponent designMenuOptions[] = { mode, shapeDrawStyle, directionStyle, collisions, parameters};
		for (JComponent jc: designMenuOptions)
			designMenu.add(jc);
		
		/*
		designMenu.add(mode);
		designMenu.add(shapeDrawStyle);
		designMenu.add(directionStyle);
		designMenu.add(collisions);
		designMenu.add(parameters);
		*/
		menu.add(designMenu);
				
		designMenu.setEnabled(false); // disabled if the user has not selected a file
		
	}
	
	/**
	 * Creates the dataset menu bar section.
	 * @param menu
	 */
	public void createLabelsAxesMenu(JMenuBar menu) {
		labelsAxesMenu = new JMenu("Labels and Axes");
		labelsAxesMenu.setMnemonic('D');
		createLabelMenu(labelsAxesMenu);
		createAxesMenu(labelsAxesMenu);
		createInfoMenu(labelsAxesMenu);
		menu.add(labelsAxesMenu);
		labelsAxesMenu.setEnabled(false); 
	}

	/**
	 * Creates the label menu bar section.
	 * @param cMenu
	 */
	public void createLabelMenu(JMenu cMenu) {
		
		JMenu labels = new JMenu("Labels");
		JMenu show = new JMenu("Visibilty");
		
		JCheckBoxMenuItem disableLabel 	= new JCheckBoxMenuItem("Disabled",false);// ytz: true htan
		JCheckBoxMenuItem name     		= new JCheckBoxMenuItem("Names",true);
		JCheckBoxMenuItem value    		= new JCheckBoxMenuItem("Values",true); 
		JCheckBoxMenuItem rank  		= new JCheckBoxMenuItem("Rank",false); 
		
		JMenuItem labelParameters = new JMenuItem("More..");
		
		name.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(name.getState()) {
            		name.setState(true);
            		disableLabel.setState(false);
            		conf.setShowName(true);
            	}
            	else {
            		name.setState(false);
            		conf.setShowName(false);
            		if(!conf.includeLabels()) {
            			disableLabel.setState(true);
            		}
            	}
            	visualizeOnFrame();
            }});
		
		value.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(value.getState()) {
            		value.setState(true);
            		disableLabel.setState(false);
            		conf.setShowVal(true);
            	}
            	else {
            		value.setState(false);
            		conf.setShowVal(false);
            		if(!conf.includeLabels()) {
            			disableLabel.setState(true);
            		}
            	}
        		visualizeOnFrame();
            }});
		
		rank.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(rank.getState()) {
            		rank.setState(true);
            		conf.setShowRank(true);
            		disableLabel.setState(false);
            	}
            	else {
            		rank.setState(false);
            		conf.setShowRank(false);
            		if(!conf.includeLabels()) {
            			disableLabel.setState(true);
            		}
            		
            	}
        		visualizeOnFrame();
            }});
		
		disableLabel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(disableLabel.getState()) {            		
            		conf.disableLabels();
            		rank.setState(false);
            		value.setState(false);
            		name.setState(false);
            	}
            	else {
            		rank.setState(true);
            		value.setState(true);
            		name.setState(true);
            		conf.enableLabels();
            	}
        		visualizeOnFrame();
            }});
		
		labelParameters.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	setLabelParameters();
        }});
		
		show.add(rank);
		show.add(name);
		show.add(value);
		
		labels.add(show);
		labels.add(disableLabel);
		labels.add(labelParameters);
		
		cMenu.add(labels);
	}
	
	/**
	 * Creates the axes menu bar section.
	 * @param cMenu
	 */
	public void createAxesMenu(JMenu cMenu) {
		
		JMenu axes = new JMenu("Axes");
				
		JCheckBoxMenuItem xAxes  = new JCheckBoxMenuItem("Axis-x",false);
		JCheckBoxMenuItem yAxes  = new JCheckBoxMenuItem("Axis-y",false);
		JCheckBoxMenuItem xyAxes = new JCheckBoxMenuItem("Axes-xy",false);
		JCheckBoxMenuItem noAxes = new JCheckBoxMenuItem("Disabled",true);
		
		xAxes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	xAxes.setState(true);
            	yAxes.setState(false);
            	xyAxes.setState(false);
            	noAxes.setState(false);
        		conf.setAxes(Axes.AxisX);
        		visualizeOnFrame();
            }});
		
		yAxes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	xAxes.setState(false);
            	yAxes.setState(true);
            	xyAxes.setState(false);
            	noAxes.setState(false);
        		conf.setAxes(Axes.AxisY); 
        		visualizeOnFrame();
            }});
		
		xyAxes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	xAxes.setState(false);
            	yAxes.setState(false);
            	xyAxes.setState(true);
            	noAxes.setState(false);
        		conf.setAxes(Axes.AxesXY);
        		visualizeOnFrame();
            }});
		
		noAxes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	xAxes.setState(false);
            	yAxes.setState(false);
            	xyAxes.setState(false);
            	noAxes.setState(true);
        		conf.setAxes(Axes.NoAxes); 
        		visualizeOnFrame();
            }});
		
		axes.add(xAxes);
		axes.add(yAxes);
		axes.add(xyAxes);
		axes.add(noAxes);
		
		cMenu.add(axes);	
	}
	
	/**
	 * Creates the info menu bar section.
	 * @param cMenu
	 */
	public void createInfoMenu(JMenu cMenu) {
		JMenu info = new JMenu("Information");
		JCheckBoxMenuItem enabled = new JCheckBoxMenuItem("Enabled",true); 
		
		enabled.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        		if(enabled.getState()) {
        			enabled.setState(true);
        			conf.setEnableInfo(true);
        		}
        		else {
        			enabled.setState(false);
        			conf.setEnableInfo(false);
        		}
        		visualizeOnFrame();
            }});

		info.add(enabled);
		cMenu.add(info);
	}
	
	/**
	 * Creates the export menu bar section.
	 * @param menu
	 */
	public void createExportMenu(JMenuBar menu) {
		exportMenu = new JMenu("Export");
		
		JMenuItem saveImage = new JMenuItem("Export as image..");
		JMenuItem svgCreator = new JMenuItem("Generate SVG.."); 
		
		exportMenu.setMnemonic('E');
		
		saveImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	GUIUtilities.saveImageGUI(CoSpi.pic.image);
            }});
		
		svgCreator.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	try {
					String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
					String filename = timeStamp + "_COSPI.html";
					SVGGenerator.createSVG(conf, CoSpi.rectangles, CoSpi.N, CoSpi.pic.image, GUIUtilities.folderSelectionGUI(), filename);
            	} catch (Exception e1) {
					e1.printStackTrace();
				}
            }});
		
		exportMenu.add(saveImage);		
		exportMenu.add(svgCreator);
		
		menu.add(exportMenu);
	
		exportMenu.setEnabled(false); // disabled if the user has not selected a file
	}

	/**
	 * Creates the help menu bar section.
	 * @param menu
	 */
	public void createHelpMenu(JMenuBar menu) {
		JMenu helpMenu = new JMenu("About");
		
		JMenuItem demoExample = new JMenuItem("WebSite");
		JMenuItem aboutCS = new JMenuItem("Contact");
		
		helpMenu.setMnemonic('A');
		
		demoExample.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	GUIUtilities.openSiteInBrowser("http://users.ics.forth.gr/~tzitzik/demos/cospi/index.html");
        }});
		
		aboutCS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	GUIUtilities.openSiteInBrowser("http://users.ics.forth.gr/~tzitzik/");
        }});
		
		helpMenu.add(demoExample);
		helpMenu.add(aboutCS);
		
		menu.add(helpMenu);
	}
	
	/**
	 * Runs the CoSpi algorithm and adds the outcome to the frame.
	 * It wraps it in threads for capturing cases where the algorithm cannot respond (i.e. if the canvas is too small)
	 */
	public void visualizeOnFrame() {
			
		//Motivation: to avoid blocking if sizes are too big
		// The runnable that contains the code that prepares the drawing
		Runnable runnable4Visualization = () -> {
		    try {
		    	CoSpi.clearOldData();
		    	if(!fileConf.toBePieChart) {CoSpi.loadDataAndRun(fileConf.filename, fileConf.valueColumn, fileConf.nameColumn, MIN, MAX, conf,true,false);}
				else {CoSpi.loadDataAndRunPieChart(fileConf.filename, fileConf.groupingColumn, fileConf.valueColumn, fileConf.nameColumn, MIN, MAX, conf,true,false);}
				setContentPane(CoSpi.pic.getJLabel());
				SwingUtilities.updateComponentTreeUI(this);
		    }
		    catch (Exception e) {e.printStackTrace();}
		};

		// The thread
		Thread thread4Visualization = new Thread(runnable4Visualization);
		
		
		// A new thread that will be used for measuring time without blocking the current GUI thread
		Thread threadController = new Thread (() -> {
			thread4Visualization.start(); 
			try {Thread.sleep(1000);} catch (InterruptedException e)  {  e.printStackTrace(); } // 1 sec delay for the algorithm.
	        if (thread4Visualization.isAlive()) {  
	        	System.out.println("I WILL STOP THE THREAD");
	        	JOptionPane.showMessageDialog(this,"The sizes that you have specified are too big to fit in the canvas. Reduce the sizes and try again.","Warning",JOptionPane.WARNING_MESSAGE);
	        }
			thread4Visualization.stop();  // stopping the visualization thread  
		});
		threadController.start();
		
	
		// Menu-related adjustements
		this.fileSelected = true; // a file has been loaded (ytz)
		JMenu[] menus = { designMenu, labelsAxesMenu, exportMenu}; // the menus that should be enabled only if a file has been selected
		for (JMenu menu: menus)
			if (menu!=null)
				menu.setEnabled(true);  // enabling the other menus:
		if (parFrame==null) // i.e. if not already opened
			setLayoutParameters(); // opens directly the LayoutParams frame
		//ytz end
		SwingUtilities.updateComponentTreeUI(this);
	}
	

	  // new version (ytz)
		public void setLayoutParameters() {
			// Creating the Frame
			parFrame = new JFrame("Parameters");
			parFrame.setSize(350,400);
			parFrame.setBounds(1000,0,350,400);
			parFrame.setLayout(new GridLayout(0,1,5,1)); // rows, columns, int hgap, int vgap)
			
			// Panel for Max Value
			JPanel maxOperatorPanel = new JPanel(new GridLayout(0,3,5,5)); // rows, columns, int hgap, int vgap)
			maxOperatorPanel.setBorder(BorderFactory.createTitledBorder(
			        BorderFactory.createEtchedBorder(), "Max Value Size"));
			JTextField maxText = new JTextField(String.valueOf(MAX));
			JButton maxPlus  = new JButton("+");
			JButton maxMinus = new JButton("-");
			maxPlus.addActionListener( e->{maxText.setText(String.valueOf(((int)(Integer.parseInt(maxText.getText())*1.2))));});
			maxMinus.addActionListener(e->{maxText.setText(String.valueOf(((int)(Integer.parseInt(maxText.getText())*0.8))));});
			for (JComponent jc: new JComponent[] {maxText,maxPlus,maxMinus})
				maxOperatorPanel.add(jc);
			
			// Panel for Min Value
			JPanel minOperatorPanel = new JPanel(new GridLayout(0,3,5,5)); // rows, columns, int hgap, int vgap)
			minOperatorPanel.setBorder(BorderFactory.createTitledBorder(
			        BorderFactory.createEtchedBorder(), "Min Value Size"));
			JTextField minText = new JTextField(String.valueOf(MIN));
			JButton minPlus  = new JButton("+");
			JButton minMinus = new JButton("-");
			minPlus.addActionListener( e->{minText.setText(String.valueOf(1+((int)(Integer.parseInt(minText.getText())*1.5))));});
			minMinus.addActionListener(e->{minText.setText(String.valueOf(((int)(Integer.parseInt(minText.getText())*0.5))));});
			for (JComponent jc: new JComponent[]{minText,minPlus,minMinus})
				minOperatorPanel.add(jc);
				
			// Panel for Roads
			JPanel roadOperatorPanel = new JPanel(new GridLayout(0,3,5,5)); // rows, columns, int hgap, int vgap)
			roadOperatorPanel.setBorder(BorderFactory.createTitledBorder(
			        BorderFactory.createEtchedBorder(), "Road Size"));
			JTextField roadText = new JTextField(String.valueOf(conf.getRoadSize()));
			JButton roadPlus  = new JButton("+");
			JButton roadMinus = new JButton("-");
			roadPlus.addActionListener( e->{roadText.setText(String.valueOf(1+((int)(Integer.parseInt(roadText.getText())*1.5))));});
			roadMinus.addActionListener(e->{roadText.setText(String.valueOf(((int)(Integer.parseInt(roadText.getText())*0.5))));});
			for (JComponent jc: new JComponent[]{roadText,roadPlus,roadMinus})
				roadOperatorPanel.add(jc);
			
			// Panel for Angles
			JPanel anglesOperatorPanel = new JPanel(new GridLayout(0,3,5,5)); // rows, columns, int hgap, int vgap)
			anglesOperatorPanel.setBorder(BorderFactory.createTitledBorder(
			        BorderFactory.createEtchedBorder(), "Angles"));
			double [] possibleAngles = {0,Math.PI/2,Math.PI,3*Math.PI/2,2*Math.PI};		
			String [] possibleAnglesDisplay = {"0","pi/2","pi","3p/2" };
			JComboBox minAngles = new JComboBox(possibleAnglesDisplay);
			for (JComponent jc: new JComponent[]{roadText,roadPlus,roadMinus})
				anglesOperatorPanel.add(minAngles);
			
			// Panel for Colors
			JPanel colorOperatorPanel = new JPanel(new GridLayout(0,3,5,5)); // rows, columns, int hgap, int vgap)
			colorOperatorPanel.setBorder(BorderFactory.createTitledBorder(
			        BorderFactory.createEtchedBorder(), "Colors"));
			Color [] possibleColors = {Color.orange,Color.blue,Color.green,Color.red,Color.gray,Color.cyan,Color.yellow,Color.black};
			String [] possibleColorsDisplay = {"Orange", "Blue","Green","Red","Gray","Cyan","Yellow","Black"};
			JComboBox colors = new JComboBox(possibleColorsDisplay);
			colorOperatorPanel.add(colors);
			
			// Adding all Panels to the Frame
			for (JPanel jp: new JPanel[]{ 	
							maxOperatorPanel, 
							minOperatorPanel,
							roadOperatorPanel,
							anglesOperatorPanel,
							colorOperatorPanel}
				)
			  parFrame.add(jp);
			parFrame.setVisible(true);
			
			// Adding the DEFAULT and APPLY buttons
			JButton apply = new JButton("Apply");
			JButton reset = new JButton("Default");
			apply.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	MAX = Integer.parseInt(maxText.getText());
	            	MIN = Integer.parseInt(minText.getText());
	            	conf.setRoadSize(Integer.parseInt(roadText.getText()));
	            	conf.setAngleMin(possibleAngles[minAngles.getSelectedIndex()]);
	            	conf.setRectColor(possibleColors[colors.getSelectedIndex()]);
	            	visualizeOnFrame();
	            }});
			reset.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	if(fileConf.toBePieChart) {
	            		MAX = 20;
	            		MIN = 5;
	            	}
	            	else{
	            		MAX = 30;
	            		MIN = 1;
	            	}
	            	//initializeConfig();
	            	visualizeOnFrame();
	            	maxText.setText(String.valueOf(MAX));
	            	minText.setText(String.valueOf(MIN));
	            	parFrame.setVisible(true);
	            }});
			parFrame.add(apply);
			parFrame.add(reset);
			
			// for opening again the frame from the menu
			parFrame.addWindowListener(new WindowAdapter() {
				  public void windowClosing(WindowEvent we) {  parFrame = null;  }
			});
		}
	
	/**
	 * OLD VERSION:
	 */
	public void setLayoutParametersOLD() {
		
		double [] possibleAngles = {0,Math.PI/2,Math.PI,3*Math.PI/2,2*Math.PI};		
		String [] possibleAnglesDisplay = {"0","pi/2","pi","3p/2" };
		
		Color [] possibleColors = {Color.orange,Color.blue,Color.green,Color.red,Color.gray,Color.cyan,Color.yellow,Color.black};
		String [] possibleColorsDisplay = {"Orange", "Blue","Green","Red","Gray","Cyan","Yellow","Black"};
		
		parFrame = new JFrame("Parameters");
		int frameWidth=300;
		parFrame.setResizable(true); // false before
		parFrame.setSize(frameWidth,180);
		parFrame.setBounds(1000,0,frameWidth,180);
		setLayout(new FlowLayout());
		
		JLabel max = new JLabel(" Max value size: ");
		JLabel min = new JLabel(" Min value size: ");
	
		JLabel roadSize = new JLabel(" Road size: ");
		
		JLabel thetaMin = new JLabel(" Angle margin: ");
		JLabel colorText = new JLabel(" Color: ");
		
		JTextField maxText = new JTextField(String.valueOf(MAX));
		JTextField minText = new JTextField(String.valueOf(MIN));
		JTextField road = new JTextField(String.valueOf(conf.getRoadSize()));
		
		JComboBox minAngles = new JComboBox(possibleAnglesDisplay);
		JComboBox colors = new JComboBox(possibleColorsDisplay);
		
		
		// relative adjustment of Max sizes
		JButton maxPlus  = new JButton("+");
		JButton maxMinus = new JButton("-");
		maxPlus.addActionListener( e->{maxText.setText(String.valueOf(((int)(Integer.parseInt(maxText.getText())*1.2))));});
		maxMinus.addActionListener(e->{maxText.setText(String.valueOf(((int)(Integer.parseInt(maxText.getText())*0.8))));});
		
		// relative adjustment of Min sizes
		JButton minPlus  = new JButton("+");
		JButton minMinus = new JButton("-");
		minPlus.addActionListener( e->{minText.setText(String.valueOf(((int)(Integer.parseInt(minText.getText())*1.5))));});
		minMinus.addActionListener(e->{minText.setText(String.valueOf(((int)(Integer.parseInt(minText.getText())*0.5))));});
		
		
		// relative adjustment of Min sizes
		JButton roadPlus  = new JButton("+");
		JButton roadMinus = new JButton("-");
		roadPlus.addActionListener( e->{road.setText(String.valueOf(((int)(Integer.parseInt(road.getText())*1.5))));});
		roadMinus.addActionListener(e->{road.setText(String.valueOf(((int)(Integer.parseInt(road.getText())*0.5))));});
		
		
		JButton apply = new JButton("Apply");
		JButton reset = new JButton("Default");
		
		apply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	String newMax = maxText.getText();
            	String newMin = minText.getText();
            	String newRoad = road.getText();
            	MAX = Integer.parseInt(newMax);
            	MIN = Integer.parseInt(newMin);
            	int j = minAngles.getSelectedIndex();
            	int c = colors.getSelectedIndex();
            	conf.setRoadSize(Integer.parseInt(newRoad));
            	conf.setAngleMin(possibleAngles[j]);
            	conf.setRectColor(possibleColors[c]);
            	visualizeOnFrame();
            }});
		
		reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(fileConf.toBePieChart) {
            		MAX = 20;
            		MIN = 5;
            	}
            	else{
            		MAX = 30;
            		MIN = 1;
            	}
            	//initializeConfig();
            	visualizeOnFrame();
            	maxText.setText(String.valueOf(MAX));
            	minText.setText(String.valueOf(MIN));
            	parFrame.setVisible(true);
            }});
		
		
		JPanel panel = new JPanel(new GridLayout(0,4));
		
		panel.add(max);
		panel.add(maxText);
		panel.add(maxPlus); //ytz
		panel.add(maxMinus); //ytz
		panel.add(min);
		panel.add(minText);
		panel.add(minPlus); // new
		panel.add(minMinus); // new
		panel.add(roadSize);
		panel.add(road);
		panel.add(roadPlus); // new
		panel.add(roadMinus); // new
		panel.add(thetaMin);
		panel.add(minAngles);
		panel.add(colorText);
		panel.add(colors);
		panel.add(reset);
		panel.add(apply);
		
		parFrame.add(panel);
		parFrame.setVisible(true);
		
		parFrame.addWindowListener(new WindowAdapter() {
			  @Override
			  public void windowClosing(WindowEvent we) {
				  parFrame = null;  // ytz: for opening again the frame from the menu
			  }
			});
	}

	/**
	 * Simple GUI to change the label parameters.
	 */
	public void setLabelParameters() {
		JFrame parFrame = new JFrame("Parameters");
		parFrame.setResizable(false);
		parFrame.setBounds(1000,0,220,120);
		setLayout(new FlowLayout());
		
		Color [] possibleColors = {Color.blue,Color.green,Color.red,Color.gray,Color.orange,Color.cyan,Color.yellow,Color.black};
		String [] possibleColorsDisplay = {"Blue","Green","Red","Gray","Orange","Cyan","Yellow","Black"};
		
		JLabel size = new JLabel( " Dec. Rate: ");
		JTextField sText = new JTextField(String.valueOf(conf.getLabelDecreasingRate()));
		JLabel colorText = new JLabel(" Color: ");
		JComboBox colors = new JComboBox(possibleColorsDisplay);

		JButton apply = new JButton("Apply");
		JButton reset = new JButton("Default");
		
		apply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	conf.setLabel(possibleColors[colors.getSelectedIndex()], Integer.parseInt(sText.getText()));
            	visualizeOnFrame();
            }});
		
		reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	conf.setLabel(Color.black, 4);
            	visualizeOnFrame();
            }});
		
		JPanel panel = new JPanel(new GridLayout(3,2));
		panel.add(size);
		panel.add(sText);
		panel.add(colorText);
		panel.add(colors);
		panel.add(reset);
		panel.add(apply);
		parFrame.add(panel);
		parFrame.setVisible(true);
	}
	
	/**
	 * Simple GUI to configure the parameters to load a dataset.
	 */
	public void columnParametersClassic() {
		JFrame win = new JFrame("Column Selection");
		win.setResizable(false);
		win.setBounds(700,0,320,120); // x,y,widht, height
		setLayout(new FlowLayout());
		JLabel  firstFileLinesLabel =  new JLabel("First line of the file:"); // yt
		JLabel  firstFileLines =       new JLabel(fileConf.firstLinesText); // yt
		firstFileLines.setForeground(Color.blue);
		firstFileLines.setFont(new Font("Serif", Font.BOLD, 18));
		
		JLabel nameCol = new JLabel( " Column with Names: ");
		JLabel valCol =  new JLabel( " Column with Values: ");
		JLabel mes = new JLabel( " ");
		JTextField ntext = new JTextField("0");
		ntext.setToolTipText("Use 0 for the first column");
		JTextField vtext = new JTextField("1");
		vtext.setToolTipText("Use 1 for the second column");
		
		JButton vis = new JButton("Visualize!");
		JPanel p1 = new JPanel(new GridLayout(0,2));
		
		vis.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	fileConf.nameColumn = Integer.parseInt(ntext.getText());
            	fileConf.valueColumn = Integer.parseInt(vtext.getText());
            	win.setVisible(false);
            	MAX = 30;
            	MIN = 2;
            	initializeConfig();
            	visualizeOnFrame();
            }});
		
		p1.add(firstFileLinesLabel);
		p1.add(firstFileLines);
		p1.add(nameCol);
		p1.add(ntext);
		p1.add(valCol);
		p1.add(vtext);
		p1.add(mes);
		p1.add(vis);
		
		win.add(p1);
		win.setVisible(true);
		
	}
	
	/**
	 * Simple GUI to configure the parameters to load a pie chart dataset.
	 */
	public void columnParametersPieChart() {
		JFrame win = new JFrame("ColumnSelection");
		win.setResizable(false);
		win.setBounds(700,0,520,200);
		setLayout(new FlowLayout());
		
		JLabel  firstFileLinesLabel =  new JLabel("First line of the file:"); // yt
		JLabel  firstFileLines =       new JLabel(fileConf.firstLinesText); // yt
		firstFileLines.setForeground(Color.blue);
		firstFileLines.setFont(new Font("Serif", Font.BOLD, 18));
		
		JLabel nameCol = new JLabel( " Column with Names:  ");
		JLabel valCol = new JLabel( "Column with Values: ");
		JLabel gpcol = new JLabel( " Column with Groupby values: ");
		JLabel mes = new JLabel( " ");
		JTextField ntext = new JTextField("0");
		ntext.setToolTipText("Use 0 for the first column");
		JTextField gptext = new JTextField("1");
		gptext.setToolTipText("Use 1 for the second column");
		JTextField vtext = new JTextField("2");
		vtext.setToolTipText("Use 2 for the third column");
		
		JButton vis = new JButton("Visualize!");
		JPanel p1 = new JPanel(new GridLayout(0,2));
		
		vis.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	fileConf.nameColumn = Integer.parseInt(ntext.getText());
            	fileConf.valueColumn = Integer.parseInt(vtext.getText());
            	fileConf.groupingColumn = Integer.parseInt(gptext.getText());
            	win.setVisible(false);
            	MAX = 10;
            	MIN = 5;
            	initializeConfig();
            	visualizeOnFrame();
            }});
		
		
		p1.add(firstFileLinesLabel);
		p1.add(firstFileLines);
		p1.add(nameCol);
		p1.add(ntext);
		p1.add(gpcol);
		p1.add(gptext);
		p1.add(valCol);
		p1.add(vtext);
		p1.add(mes);
		p1.add(vis);
		
		win.add(p1);
		win.setVisible(true);
		
	}
	
	/**
	 * Run the app
	 * @param args
	 */
	public static void main(String [] args) {
		AppWelcome t = new AppWelcome();
		CoSpiGUI app = new CoSpiGUI();
	}
}

/*
 * File Configurations information.
 * Holds the information about the file containing the dataset to be visualized.
 * @author Manos Chatzakis
 */
class FileConf{
	
	String filename;
	
	boolean toBePieChart;
	
	int nameColumn;
	int valueColumn;
	int groupingColumn;
	String firstLinesText = "Lala | Lala | Lala";
	
	private void readFirstLines(int numOfLines) {
		if (filename==null)
				return;
		firstLinesText="";
		
		String line = "";
        String cvsSplitBy = ",";
        int lineno=0;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while (((line = br.readLine()) != null) && (lineno<numOfLines)){
            	System.out.println("Read from file the line: " + line);
                lineno++;
                for (String s: line.split(cvsSplitBy) ) {
                	firstLinesText =  firstLinesText + " | " + s;
                }
                firstLinesText +=  "\n";
            }
        } catch (IOException e) {
            //e.printStackTrace();
        	System.out.println("Canceled File Selection");
        }
	}
	
	
	FileConf(int nameColumn,int valueColumn,
			int groupingColumn,boolean toBePieChart){ 
		this.groupingColumn = groupingColumn;
		this.nameColumn = nameColumn;
		this.valueColumn = valueColumn;
		this.toBePieChart = toBePieChart;
		readFirstLines(1);
	}
	
	FileConf(int nameColumn,int valueColumn,
			boolean toBePieChart,
			String filename){
		this.nameColumn = nameColumn;
		this.valueColumn = valueColumn;
		this.toBePieChart = toBePieChart;
		this.filename = filename;
		readFirstLines(1);
	}
	
	FileConf(int nameColumn,int valueColumn,
			int groupingColumn,boolean toBePieChart,
			String filename){
		this.groupingColumn = groupingColumn;
		this.nameColumn = nameColumn;
		this.valueColumn = valueColumn;
		this.toBePieChart = toBePieChart;
		this.filename = filename;
		readFirstLines(1);
	}
}

/**
 * 
 * @author Yannis Tzitzikas (yannistzitzik@gmail.com)
 * A splash screen
 */
class AppWelcome extends JFrame {
	AppWelcome() {
		Image image = null;
	    try {
	    	// Setting the image in the window
	    	URL imgURL = getClass().getResource("/Image.png");
	    	image = ImageIO.read(imgURL);
	    	
	    	// Setting the ICON 
	 		ImageIcon icon =  new ImageIcon(imgURL, "Icon");
 			this.setIconImage(icon.getImage());
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    //this.setSize(600, 300);
	    this.setBounds(600, 300, 600, 300);  // setBounds(x, y, width, height) 
	    this.getContentPane().setBackground( Color.white );
	    this.setLayout(new GridLayout(0,2,0,0)); // rows, columns, int hgap, int vgap)
	    this.add(new JLabel(new ImageIcon(image)));
	    this.add(new JLabel("<html> <h1>COSPI v1.0 </h1> <br> by Yannis Tzitzikas<br> and Manos Chatzakis <br> Nov, 2020</html>"));
	    this.setVisible(true);   
	    try {
	    Thread.sleep(3000);
		dispose();
	    } catch (Exception e) {
	    	System.err.println(e);
	    }
		
	}
	public static void main(String[] args)
	{     
	    AppWelcome t = new AppWelcome();
	}
	
}

