package APP;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
	
	int MAX; //Normilized max
	int MIN; //Normilized min
	
	VisConfig conf    = new VisConfig(); //Visual Configurations
	FileConf fileConf = new FileConf(0,1,2,true); //File Configurations
	
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
		setTitle("CoSpi GUI");
		
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
		conf.setRectColor(Color.blue);
		conf.setEnableInfo(false);
		conf.setLabelParams(false, false, false, Color.black, 4);
		conf.setAllowOverlap(false);
	}

	/**
	 * Creates the main menu bar of the app.
	 * @param menu
	 */
	public void createMenuBar(JMenuBar menu) {
		createProjectMenu(menu);
		createDesignMenu(menu);
		createDatasetMenu(menu);
		createExportMenu(menu);
		createHelpMenu(menu);
	}
	
	/**
	 * Creates the project menu bar section.
	 * @param menu
	 */
	public void createProjectMenu(JMenuBar menu) {
		JMenu projectMenu = new JMenu("File");
		projectMenu.setMnemonic('P');
		JMenu newFile = new JMenu("Open");
		JMenuItem classic = new JMenuItem("Classic (to visualize one column)");
		classic.setToolTipText("to become available soon");
		
		
		JMenuItem pieChart = new JMenuItem("Pie Chart (to visualize two columns");
		pieChart.setToolTipText("to become available soon");
		
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
		JMenu designMenu     = new JMenu("Design");
		designMenu.setMnemonic('D');
		
		/*
		 * Creating mode menu
		 */
		JMenu mode     	  = new JMenu("Spiral Style");
		//JMenuItem classic = new JMenuItem("Classic");
		JCheckBoxMenuItem classic = new JCheckBoxMenuItem("Classic",true);
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
		JMenuItem parameters = new JMenuItem("Other Parameters..");
		parameters.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	setLayoutParameters();
            }});
		
		designMenu.add(mode);
		designMenu.add(shapeDrawStyle);
		designMenu.add(directionStyle);
		designMenu.add(collisions);
		designMenu.add(parameters);
		menu.add(designMenu);
	}
	
	/**
	 * Creates the dataset menu bar section.
	 * @param menu
	 */
	public void createDatasetMenu(JMenuBar menu) {
		JMenu datasetMenu = new JMenu("Labels and Axes");
		datasetMenu.setMnemonic('D');
		createLabelMenu(datasetMenu);
		createAxesMenu(datasetMenu);
		createInfoMenu(datasetMenu);
		menu.add(datasetMenu);
	}
	
	/**
	 * Creates the label menu bar section.
	 * @param cMenu
	 */
	public void createLabelMenu(JMenu cMenu) {
		
		JMenu labels = new JMenu("Labels");
		JMenu show = new JMenu("Visibilty");
		
		JCheckBoxMenuItem disableLabel = new JCheckBoxMenuItem("Disabled",true);
		JCheckBoxMenuItem name     = new JCheckBoxMenuItem("Names",false);
		JCheckBoxMenuItem value    = new JCheckBoxMenuItem("Values",false); 
		JCheckBoxMenuItem rank  = new JCheckBoxMenuItem("Rank",false); 
		
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
		JCheckBoxMenuItem enabled = new JCheckBoxMenuItem("Enabled",false); 
		
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
		JMenu exportMenu = new JMenu("Export");
		
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
	 */
	public void visualizeOnFrame() {
		CoSpi.clearOldData();
		if(!fileConf.toBePieChart) {CoSpi.loadDataAndRun(fileConf.filename, fileConf.valueColumn, fileConf.nameColumn, MIN, MAX, conf,true,false);}
		else {CoSpi.loadDataAndRunPieChart(fileConf.filename, fileConf.groupingColumn, fileConf.valueColumn, fileConf.nameColumn, MIN, MAX, conf,true,false);}
		setContentPane(CoSpi.pic.getJLabel());
		SwingUtilities.updateComponentTreeUI(this);
	}
	
	/**
	 * Simple GUI to change the layout parameters.
	 */
	public void setLayoutParameters() {
		
		double [] possibleAngles = {0,Math.PI/2,Math.PI,3*Math.PI/2,2*Math.PI};		
		String [] possibleAnglesDisplay = {"0","pi/2","pi","3p/2" };
		
		Color [] possibleColors = {Color.blue,Color.green,Color.red,Color.gray,Color.orange,Color.cyan,Color.yellow,Color.black};
		String [] possibleColorsDisplay = {"Blue","Green","Red","Gray","Orange","Cyan","Yellow","Black"};
		
		JFrame parFrame = new JFrame("Parameters");
		parFrame.setResizable(false);
		parFrame.setSize(250,180);
		parFrame.setBounds(1000,0,250,180);
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
		
		JPanel panel = new JPanel(new GridLayout(6,2));
		
		panel.add(max);
		panel.add(maxText);
		panel.add(min);
		panel.add(minText);
		panel.add(roadSize);
		panel.add(road);
		panel.add(thetaMin);
		panel.add(minAngles);
		panel.add(colorText);
		panel.add(colors);
		panel.add(reset);
		panel.add(apply);
		
		parFrame.add(panel);
		parFrame.setVisible(true);
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
		JButton reset = new JButton ("Default");
		
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
		JFrame win = new JFrame("ColumnSelection");
		win.setResizable(false);
		win.setBounds(700,0,320,120); // x,y,widht, height
		setLayout(new FlowLayout());
		JLabel nameCol = new JLabel( " Column with Names: ");
		JLabel valCol =  new JLabel( " Column with Values: ");
		JLabel mes = new JLabel( " ");
		JTextField ntext = new JTextField("0");
		ntext.setToolTipText("Use 0 for the first column");
		JTextField vtext = new JTextField("1");
		vtext.setToolTipText("Use 1 for the second column");
		
		JButton vis = new JButton("Visualize!");
		JPanel p1 = new JPanel(new GridLayout(3,2));
		
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
		win.setBounds(700,0,320,120);
		setLayout(new FlowLayout());
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
		JPanel p1 = new JPanel(new GridLayout(4,2));
		
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
	
	FileConf(int nameColumn,int valueColumn,
			int groupingColumn,boolean toBePieChart){ 
		this.groupingColumn = groupingColumn;
		this.nameColumn = nameColumn;
		this.valueColumn = valueColumn;
		this.toBePieChart = toBePieChart;
	}
	
	FileConf(int nameColumn,int valueColumn,
			boolean toBePieChart,
			String filename){
		this.nameColumn = nameColumn;
		this.valueColumn = valueColumn;
		this.toBePieChart = toBePieChart;
		this.filename = filename;
	}
	
	FileConf(int nameColumn,int valueColumn,
			int groupingColumn,boolean toBePieChart,
			String filename){
		this.groupingColumn = groupingColumn;
		this.nameColumn = nameColumn;
		this.valueColumn = valueColumn;
		this.toBePieChart = toBePieChart;
		this.filename = filename;
	}
}


