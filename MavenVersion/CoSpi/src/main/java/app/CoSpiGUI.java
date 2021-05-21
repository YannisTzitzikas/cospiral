/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.jkjk
 */
package app;

import utilsCommon.SVGGenerator;
import cospi.CoSpi;
import cospi.VisConfig;
import cospi.params.Axes;
import cospi.params.Direction;
import cospi.params.DrawStyle;
import cospi.params.ExpandStyle;
import cospi.params.ShapeGaps;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import lombok.Data;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CoSpiGUI extends JFrame {

    CoSpi cospi;

    VisConfig conf = new VisConfig();
    FileConf fileConf = new FileConf(0, 1, 2, true);

    boolean fileSelected = false;
    int RESOLUTION = 1000;

    JMenu designMenu;
    JMenu labelsAxesMenu;
    JMenu exportMenu;

    JFrame parFrame;
    JTextArea consoleOutputArea;

    public CoSpiGUI() {

        ImageIcon icon;
        String path = "/icons/Image.png";
        java.net.URL imgURL = getClass().getResource(path);
        System.out.println(getClass());
        if (imgURL != null) {
            icon = new ImageIcon(imgURL, "Eikonidio"); // imgURL
            this.setIconImage(icon.getImage());
        } else {
            System.err.println("Couldn't find file: " + path);
        }

        System.out.println("CoSpi application started.");

        int WIDTH = RESOLUTION;
        int HEIGHT = RESOLUTION + 50;

        JMenuBar menu = new JMenuBar();

        setResizable(false);
        setSize(WIDTH, HEIGHT);
        setTitle("CoSpi APP");

        initializeConfig();
        createMenuBar(menu);

        setJMenuBar(menu);
        setLayout(null);
        setVisible(true);
    }

    private void initializeConfig() {
        conf.setDrawStyle(DrawStyle.Filled);
        conf.setDirection(Direction.Expand);
        conf.setExpandStyle(ExpandStyle.Spiral);
        conf.setShapeGaps(ShapeGaps.Minimum);

        conf.setAngleMin(0);
        conf.setAngleMax(2 * Math.PI);

        conf.setRoadSize(4);

        conf.setAxes(Axes.NoAxes);
        conf.setRectColor(Color.orange);

        conf.setAllowOverlap(false);
        conf.setEnableInfo(true);
        conf.setLabelParams(false, true, true, Color.black, 4);

        conf.setMax(50);
        conf.setMin(1);
        conf.setN(RESOLUTION);
    }

    private void createMenuBar(JMenuBar menu) {
        createProjectMenu(menu);
        createDesignMenu(menu);
        createLabelsAxesMenu(menu);
        createExportMenu(menu);
        createHelpMenu(menu);
    }

    private void createProjectMenu(JMenuBar menu) {

        JMenu projectMenu = new JMenu("File");
        projectMenu.setMnemonic('F');

        JMenu newFile = new JMenu("Open");

        JMenuItem classic = new JMenuItem("Classic (visualize two name-value columns from a file)");
        classic.setToolTipText("Allows you to visualize two name-value columns from a file");

        JMenuItem pieChart = new JMenuItem("Pie Chart (visualize three  name-value-group columns from a file");
        pieChart.setToolTipText("Allows you to visualize three name-value-group columns from a file");

        JMenuItem saveVis = new JMenuItem("Save Visualization..");
        saveVis.setToolTipText("Save the current visualization with its parameters to re-load it later");

        JMenuItem loadVis = new JMenuItem("Load Visualization..");
        loadVis.setToolTipText("Load a CoSpi visualization from file system");

        JMenu loadDemo = new JMenu("Load Demo");
        loadDemo.setToolTipText("Load a demo dataset to try our software");

        JMenuItem citiesDemo = new JMenuItem("Cities dataset demo");
        citiesDemo.setToolTipText("A dataset containing the 1000 biggest cities on population");

        //JMenuItem citiesDemo = new JMenuItem("Cities dataset demo");
        //citiesDemo.setToolTipText("A dataset containing the 1000 biggest cities on population");
        classic.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileConf = new FileConf(0, 1, false, fileSelectionGUI());
                fileConf.setUsingInputStream(false);

                columnParametersClassic();

            }
        });

        pieChart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileConf = new FileConf(0, 2, 1, true, fileSelectionGUI());
                fileConf.setUsingInputStream(false);
                columnParametersPieChart();

            }
        });

        saveVis.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveProgressGUI(conf, fileConf);
            }
        });

        loadVis.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String filePath = fileSelectionGUI();
                ImmutablePair<VisConfig, FileConf> cf = null;//= new ImmutablePair<>(2, "Two");
                //nteger key = pair.getKey();
                //String value = pair.getValue();*/
                //Pair<VisConfig, FileConf> cf = null;
                try {
                    cf = loadSavedProgress(filePath);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                conf = cf.getKey();
                fileConf = cf.getValue();

                visualizeOnFrame();
            }
        });

        citiesDemo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /*File ff = null;
                URL resource = getClass().getResource("/datasets/DemoDatasets/cities.csv");
                if (resource == null) {
                    throw new IllegalArgumentException("Cities file not found!");
                } else {

                    try {
                       
                        ff = new File(resource.toURI());
                    } catch (URISyntaxException ex) {
                        Logger.getLogger(CoSpiGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                fileConf.setFilename(ff.getAbsolutePath());
                fileConf.setToBePieChart(false);
                fileConf.setNameColumn(0);
                fileConf.setValueColumn(1);
                fileConf.setHasHeader(false);

                initializeConfig();
                conf.setMax(60);
                conf.setMin(2);

                visualizeOnFrame();*/
 /*URL resource = getClass().getResource("/datasets/DemoDatasets/cities.csv");
                if (resource == null) {
                    throw new IllegalArgumentException("Cities file not found!");
                }
                //InputStream is = null;
                try {
                    fileConf.setIs(resource.openStream()); //getFileFromResourceAsStream("/datasets/DemoDatasets/cities.csv");
                } catch (IOException ex) {
                    Logger.getLogger(CoSpiGUI.class.getName()).log(Level.SEVERE, null, ex);
                }*/

                fileConf.setFilename("/datasets/DemoDatasets/cities.csv");
                fileConf.setUsingInputStream(true);
                fileConf.setToBePieChart(false);
                fileConf.setNameColumn(0);
                fileConf.setValueColumn(1);
                fileConf.setHasHeader(false);

                initializeConfig();
                conf.setMax(60);
                conf.setMin(2);

                visualizeOnFrame();

            }
        });

        newFile.add(classic);
        newFile.add(pieChart);
        loadDemo.add(citiesDemo);
        projectMenu.add(newFile);
        projectMenu.add(saveVis);
        projectMenu.add(loadVis);
        projectMenu.add(loadDemo);
        menu.add(projectMenu);
    }

    private void createDesignMenu(JMenuBar menu) {
        designMenu = new JMenu("Design");
        designMenu.setMnemonic('D');

        JMenu mode = new JMenu("Spiral Style");
        // JMenuItem classic = new JMenuItem("Classic");
        JCheckBoxMenuItem classic = new JCheckBoxMenuItem("Classic Spiral", true);
        JCheckBoxMenuItem ring = new JCheckBoxMenuItem("Ring", false);
        classic.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conf.setExpandStyle(ExpandStyle.Spiral);
                classic.setState(true);
                ring.setState(false);
                visualizeOnFrame();
            }
        });
        ring.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conf.setExpandStyle(ExpandStyle.Ring);
                classic.setState(false);
                ring.setState(true);
                visualizeOnFrame();
            }
        });
        mode.add(classic);
        mode.add(ring);

        JMenu shapeDrawStyle = new JMenu("Fill Mode");
        JCheckBoxMenuItem filled = new JCheckBoxMenuItem("Filled", true);
        JCheckBoxMenuItem outline = new JCheckBoxMenuItem("OutLine", false);
        filled.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conf.setDrawStyle(DrawStyle.Filled);
                filled.setState(true);
                outline.setState(false);
                visualizeOnFrame();
            }
        });
        outline.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conf.setDrawStyle(DrawStyle.Outline);
                outline.setState(true);
                filled.setState(false);
                visualizeOnFrame();
            }
        });
        shapeDrawStyle.add(filled);
        shapeDrawStyle.add(outline);

        JMenu directionStyle = new JMenu("Spiral Direction");
        JCheckBoxMenuItem expand = new JCheckBoxMenuItem("Expanding", true);
        JCheckBoxMenuItem shrink = new JCheckBoxMenuItem("Shrinking", false);
        expand.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conf.setDirection(Direction.Expand);
                expand.setState(true);
                shrink.setState(false);
                visualizeOnFrame();
            }
        });

        shrink.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conf.setDirection(Direction.Shrink);
                shrink.setState(true);
                expand.setState(false);
                visualizeOnFrame();
            }
        });
        directionStyle.add(expand);
        directionStyle.add(shrink);

        JMenu collisions = new JMenu("Collisions");
        JCheckBoxMenuItem allowCollisions = new JCheckBoxMenuItem("Allowed", false);
        allowCollisions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (allowCollisions.getState()) {
                    conf.setAllowOverlap(true);
                } else {
                    conf.setAllowOverlap(false);
                }
                visualizeOnFrame();
            }
        });
        collisions.add(allowCollisions);

        JMenuItem parameters = new JMenuItem("Other Parameters (Sizes, Colors, etc)");
        parameters.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (parFrame == null) {
                    setLayoutParameters(); // opens the LayoutParameters if not already opened
                }
            }
        });

        JComponent designMenuOptions[] = {mode, shapeDrawStyle, directionStyle, collisions, parameters};
        for (JComponent jc : designMenuOptions) {
            designMenu.add(jc);
        }

        menu.add(designMenu);

        designMenu.setEnabled(false); // disabled if the user has not selected a file

    }

    private void createLabelsAxesMenu(JMenuBar menu) {
        labelsAxesMenu = new JMenu("Labels and Axes");
        labelsAxesMenu.setMnemonic('D');
        createLabelMenu(labelsAxesMenu);
        createAxesMenu(labelsAxesMenu);
        createInfoMenu(labelsAxesMenu);
        menu.add(labelsAxesMenu);
        labelsAxesMenu.setEnabled(false);
    }

    private void createLabelMenu(JMenu cMenu) {

        JMenu labels = new JMenu("Labels");
        JMenu show = new JMenu("Visibilty");

        JCheckBoxMenuItem disableLabel = new JCheckBoxMenuItem("Disabled", false);// ytz: true htan
        JCheckBoxMenuItem name = new JCheckBoxMenuItem("Names", true);
        JCheckBoxMenuItem value = new JCheckBoxMenuItem("Values", true);
        JCheckBoxMenuItem rank = new JCheckBoxMenuItem("Rank", false);

        JMenuItem labelParameters = new JMenuItem("More..");

        name.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (name.getState()) {
                    name.setState(true);
                    disableLabel.setState(false);
                    conf.setShowName(true);
                } else {
                    name.setState(false);
                    conf.setShowName(false);
                    if (!conf.includeLabels()) {
                        disableLabel.setState(true);
                    }
                }
                visualizeOnFrame();
            }
        });

        value.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (value.getState()) {
                    value.setState(true);
                    disableLabel.setState(false);
                    conf.setShowVal(true);
                } else {
                    value.setState(false);
                    conf.setShowVal(false);
                    if (!conf.includeLabels()) {
                        disableLabel.setState(true);
                    }
                }
                visualizeOnFrame();
            }
        });

        rank.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (rank.getState()) {
                    rank.setState(true);
                    conf.setShowRank(true);
                    disableLabel.setState(false);
                } else {
                    rank.setState(false);
                    conf.setShowRank(false);
                    if (!conf.includeLabels()) {
                        disableLabel.setState(true);
                    }

                }
                visualizeOnFrame();
            }
        });

        disableLabel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (disableLabel.getState()) {
                    conf.disableLabels();
                    rank.setState(false);
                    value.setState(false);
                    name.setState(false);
                } else {
                    rank.setState(true);
                    value.setState(true);
                    name.setState(true);
                    conf.enableLabels();
                }
                visualizeOnFrame();
            }
        });

        labelParameters.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setLabelParameters();
            }
        });

        show.add(rank);
        show.add(name);
        show.add(value);

        labels.add(show);
        labels.add(disableLabel);
        labels.add(labelParameters);

        cMenu.add(labels);
    }

    private void createAxesMenu(JMenu cMenu) {

        JMenu axes = new JMenu("Axes");

        JCheckBoxMenuItem xAxes = new JCheckBoxMenuItem("Axis-x", false);
        JCheckBoxMenuItem yAxes = new JCheckBoxMenuItem("Axis-y", false);
        JCheckBoxMenuItem xyAxes = new JCheckBoxMenuItem("Axes-xy", false);
        JCheckBoxMenuItem noAxes = new JCheckBoxMenuItem("Disabled", true);

        xAxes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                xAxes.setState(true);
                yAxes.setState(false);
                xyAxes.setState(false);
                noAxes.setState(false);
                conf.setAxes(Axes.AxisX);
                visualizeOnFrame();
            }
        });

        yAxes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                xAxes.setState(false);
                yAxes.setState(true);
                xyAxes.setState(false);
                noAxes.setState(false);
                conf.setAxes(Axes.AxisY);
                visualizeOnFrame();
            }
        });

        xyAxes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                xAxes.setState(false);
                yAxes.setState(false);
                xyAxes.setState(true);
                noAxes.setState(false);
                conf.setAxes(Axes.AxesXY);
                visualizeOnFrame();
            }
        });

        noAxes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                xAxes.setState(false);
                yAxes.setState(false);
                xyAxes.setState(false);
                noAxes.setState(true);
                conf.setAxes(Axes.NoAxes);
                visualizeOnFrame();
            }
        });

        axes.add(xAxes);
        axes.add(yAxes);
        axes.add(xyAxes);
        axes.add(noAxes);

        cMenu.add(axes);
    }

    private void createInfoMenu(JMenu cMenu) {
        JMenu info = new JMenu("Information");
        JCheckBoxMenuItem enabled = new JCheckBoxMenuItem("Enabled", true);

        enabled.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (enabled.getState()) {
                    enabled.setState(true);
                    conf.setEnableInfo(true);
                } else {
                    enabled.setState(false);
                    conf.setEnableInfo(false);
                }
                visualizeOnFrame();
            }
        });

        info.add(enabled);
        cMenu.add(info);
    }

    private void createExportMenu(JMenuBar menu) {
        exportMenu = new JMenu("Export");

        JMenuItem saveImage = new JMenuItem("Export as image..");
        JMenuItem svgCreator = new JMenuItem("Generate SVG..");

        exportMenu.setMnemonic('E');

        saveImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveImageGUI(cospi.getPic().image);
            }
        });

        svgCreator.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                    String filename = timeStamp + "_COSPI.html";
                    SVGGenerator.createSVG(conf, cospi.getRectangles(), RESOLUTION, cospi.getPic().image, folderSelectionGUI(), filename);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        exportMenu.add(saveImage);
        exportMenu.add(svgCreator);

        menu.add(exportMenu);

        exportMenu.setEnabled(false); // disabled if the user has not selected a file
    }

    private void createHelpMenu(JMenuBar menu) {
        JMenu helpMenu = new JMenu("About");

        JMenuItem demoExample = new JMenuItem("WebSite");
        JMenuItem aboutCS = new JMenuItem("Contact");

        helpMenu.setMnemonic('A');

        demoExample.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openSiteInBrowser("http://users.ics.forth.gr/~tzitzik/demos/cospi/index.html");
            }
        });

        aboutCS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openSiteInBrowser("http://users.ics.forth.gr/~tzitzik/");
            }
        });

        helpMenu.add(demoExample);
        helpMenu.add(aboutCS);

        menu.add(helpMenu);
    }

    private void visualizeOnFrame() {

        // Motivation: to avoid blocking if sizes are too big
        // The runnable that contains the code that prepares the drawing
        Runnable runnable4Visualization = () -> {
            try {
                if (!fileConf.isUsingInputStream()) {
                    cospi = new CoSpi(fileConf.getFilename(), fileConf.isHasHeader());
                } else {
                    InputStream is = null;
                    URL resource = getClass().getResource(fileConf.getFilename());
                    if (resource == null) {
                        throw new IllegalArgumentException("Resources file not found!");
                    }
                    try {
                        is = resource.openStream(); //getFileFromResourceAsStream("/datasets/DemoDatasets/cities.csv");
                    } catch (IOException ex) {
                        Logger.getLogger(CoSpiGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    cospi = new CoSpi(is, fileConf.isHasHeader());
                }
                if (!fileConf.isToBePieChart()) {
                    //Cospi.loadDataAndRun(fileConf.getFilename(), fileConf.getValueColumn(), fileConf.getNameColumn(),MIN, MAX, conf, true, false);
                    cospi.setUsingGUI(true);
                    cospi.visualizeClassic(conf, fileConf.getValueColumn(), fileConf.nameColumn, false);
                } else {
                    cospi.setUsingGUI(true);
                    //new CoSpi("./datasets/DemoDatasets/companies.csv", false).
                    cospi.visualizePieChart(conf, fileConf.getValueColumn(), fileConf.nameColumn, fileConf.getGroupingColumn(), false);
                    //cospi.visualizeClassic(conf, fileConf.getValueColumn(), fileConf.nameColumn, false);
                    // Cospi.loadDataAndRunPieChart(fileConf.getFilename(), fileConf.getGroupingColumn(),  fileConf.getValueColumn(), fileConf.getNameColumn(), MIN, MAX, conf, true, false);
                }
                setContentPane(cospi.getPic().getJLabel());
                SwingUtilities.updateComponentTreeUI(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // The thread
        Thread thread4Visualization = new Thread(runnable4Visualization);

        // A new thread that will be used for measuring time without blocking the
        // current GUI thread
        Thread threadController = new Thread(() -> {
            thread4Visualization.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } // 1 sec delay for the algorithm.
            if (thread4Visualization.isAlive()) {
                System.out.println("I WILL STOP THE THREAD");
                JOptionPane.showMessageDialog(this,
                        "The sizes that you have specified are too big to fit in the canvas. Reduce the sizes and try again.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
            thread4Visualization.stop(); // stopping the visualization thread
        });
        threadController.start();

        // Menu-related adjustements
        this.fileSelected = true; // a file has been loaded (ytz)
        JMenu[] menus = {designMenu, labelsAxesMenu, exportMenu}; // the menus that should be enabled only if a file
        // has been selected
        for (JMenu menu : menus) {
            if (menu != null) {
                menu.setEnabled(true); // enabling the other menus:
            }
        }
        if (parFrame == null) // i.e. if not already opened
        {
            setLayoutParameters(); // opens directly the LayoutParams frame
        }		// ytz end
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void visualizeOnFrame(InputStream is) {

        // Motivation: to avoid blocking if sizes are too big
        // The runnable that contains the code that prepares the drawing
        Runnable runnable4Visualization = () -> {
            try {
                cospi = new CoSpi(is, fileConf.isHasHeader());
                if (!fileConf.isToBePieChart()) {
                    cospi.setUsingGUI(true);
                    cospi.visualizeClassic(conf, fileConf.getValueColumn(), fileConf.nameColumn, false);
                } else {
                    cospi.setUsingGUI(true);
                    cospi.visualizePieChart(conf, fileConf.getValueColumn(), fileConf.nameColumn, fileConf.getGroupingColumn(), false);
                }
                setContentPane(cospi.getPic().getJLabel());
                SwingUtilities.updateComponentTreeUI(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // The thread
        Thread thread4Visualization = new Thread(runnable4Visualization);

        // A new thread that will be used for measuring time without blocking the
        // current GUI thread
        Thread threadController = new Thread(() -> {
            thread4Visualization.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } // 1 sec delay for the algorithm.
            if (thread4Visualization.isAlive()) {
                System.out.println("I WILL STOP THE THREAD");
                JOptionPane.showMessageDialog(this,
                        "The sizes that you have specified are too big to fit in the canvas. Reduce the sizes and try again.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
            thread4Visualization.stop(); // stopping the visualization thread
        });
        threadController.start();

        // Menu-related adjustements
        this.fileSelected = true; // a file has been loaded (ytz)
        JMenu[] menus = {designMenu, labelsAxesMenu, exportMenu}; // the menus that should be enabled only if a file
        // has been selected
        for (JMenu menu : menus) {
            if (menu != null) {
                menu.setEnabled(true); // enabling the other menus:
            }
        }
        if (parFrame == null) // i.e. if not already opened
        {
            setLayoutParameters(); // opens directly the LayoutParams frame
        }		// ytz end
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void setLayoutParameters() {
        // Creating the Frame
        parFrame = new JFrame("Parameters");
        parFrame.setSize(350, 400);
        parFrame.setBounds(1000, 0, 350, 400);
        parFrame.setLayout(new GridLayout(0, 1, 5, 1)); // rows, columns, int hgap, int vgap)

        // Panel for Max Value
        JPanel maxOperatorPanel = new JPanel(new GridLayout(0, 3, 5, 5)); // rows, columns, int hgap, int vgap)
        maxOperatorPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Max Value Size"));
        JTextField maxText = new JTextField(String.valueOf(conf.getMax()));
        JButton maxPlus = new JButton("+");
        JButton maxMinus = new JButton("-");
        maxPlus.addActionListener(e -> {
            maxText.setText(String.valueOf(((int) (Integer.parseInt(maxText.getText()) * 1.2))));
        });
        maxMinus.addActionListener(e -> {
            maxText.setText(String.valueOf(((int) (Integer.parseInt(maxText.getText()) * 0.8))));
        });
        for (JComponent jc : new JComponent[]{maxText, maxPlus, maxMinus}) {
            maxOperatorPanel.add(jc);
        }

        // Panel for Min Value
        JPanel minOperatorPanel = new JPanel(new GridLayout(0, 3, 5, 5)); // rows, columns, int hgap, int vgap)
        minOperatorPanel
                .setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Min Value Size"));
        JTextField minText = new JTextField(String.valueOf(conf.getMin()));
        JButton minPlus = new JButton("+");
        JButton minMinus = new JButton("-");
        minPlus.addActionListener(e -> {
            minText.setText(String.valueOf(1 + ((int) (Integer.parseInt(minText.getText()) * 1.5))));
        });
        minMinus.addActionListener(e -> {
            minText.setText(String.valueOf(((int) (Integer.parseInt(minText.getText()) * 0.5))));
        });
        for (JComponent jc : new JComponent[]{minText, minPlus, minMinus}) {
            minOperatorPanel.add(jc);
        }

        // Panel for Roads
        JPanel roadOperatorPanel = new JPanel(new GridLayout(0, 3, 5, 5)); // rows, columns, int hgap, int vgap)
        roadOperatorPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Road Size"));
        JTextField roadText = new JTextField(String.valueOf(conf.getRoadSize()));
        JButton roadPlus = new JButton("+");
        JButton roadMinus = new JButton("-");
        roadPlus.addActionListener(e -> {
            roadText.setText(String.valueOf(1 + ((int) (Integer.parseInt(roadText.getText()) * 1.5))));
        });
        roadMinus.addActionListener(e -> {
            roadText.setText(String.valueOf(((int) (Integer.parseInt(roadText.getText()) * 0.5))));
        });
        for (JComponent jc : new JComponent[]{roadText, roadPlus, roadMinus}) {
            roadOperatorPanel.add(jc);
        }

        // Panel for Angles
        JPanel anglesOperatorPanel = new JPanel(new GridLayout(0, 3, 5, 5)); // rows, columns, int hgap, int vgap)
        anglesOperatorPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Angles"));
        double[] possibleAngles = {0, Math.PI / 2, Math.PI, 3 * Math.PI / 2, 2 * Math.PI};
        String[] possibleAnglesDisplay = {"0", "pi/2", "pi", "3p/2"};
        JComboBox minAngles = new JComboBox(possibleAnglesDisplay);
        for (JComponent jc : new JComponent[]{roadText, roadPlus, roadMinus}) {
            anglesOperatorPanel.add(minAngles);
        }

        // Panel for Colors
        JPanel colorOperatorPanel = new JPanel(new GridLayout(0, 3, 5, 5)); // rows, columns, int hgap, int vgap)
        colorOperatorPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Colors"));
        Color[] possibleColors = {Color.orange, Color.blue, Color.green, Color.red, Color.gray, Color.cyan,
            Color.yellow, Color.black};
        String[] possibleColorsDisplay = {"Orange", "Blue", "Green", "Red", "Gray", "Cyan", "Yellow", "Black"};
        JComboBox colors = new JComboBox(possibleColorsDisplay);
        colorOperatorPanel.add(colors);

        // Adding all Panels to the Frame
        for (JPanel jp : new JPanel[]{maxOperatorPanel, minOperatorPanel, roadOperatorPanel, anglesOperatorPanel,
            colorOperatorPanel}) {
            parFrame.add(jp);
        }
        parFrame.setVisible(true);

        // Adding the DEFAULT and APPLY buttons
        JButton apply = new JButton("Apply");
        JButton reset = new JButton("Default");
        apply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conf.setMax(Integer.parseInt(maxText.getText()));
                conf.setMin(Integer.parseInt(minText.getText()));
                conf.setRoadSize(Integer.parseInt(roadText.getText()));
                conf.setAngleMin(possibleAngles[minAngles.getSelectedIndex()]);
                conf.setRectColor(possibleColors[colors.getSelectedIndex()]);
                visualizeOnFrame();
            }
        });
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (fileConf.toBePieChart) {
                    conf.setMax(20);
                    conf.setMin(5);
                } else {
                    conf.setMax(30);
                    conf.setMin(1);
                }
                // initializeConfig();
                visualizeOnFrame();
                maxText.setText(String.valueOf(conf.getMax()));
                minText.setText(String.valueOf(conf.getMin()));
                parFrame.setVisible(true);
            }
        });
        parFrame.add(apply);
        parFrame.add(reset);

        // for opening again the frame from the menu
        parFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                parFrame = null;
            }
        });
    }

    private void setLabelParameters() {
        JFrame parFrame = new JFrame("Parameters");
        parFrame.setResizable(false);
        parFrame.setBounds(1000, 0, 220, 120);
        setLayout(new FlowLayout());

        Color[] possibleColors = {Color.blue, Color.green, Color.red, Color.gray, Color.orange, Color.cyan,
            Color.yellow, Color.black};
        String[] possibleColorsDisplay = {"Blue", "Green", "Red", "Gray", "Orange", "Cyan", "Yellow", "Black"};

        JLabel size = new JLabel(" Dec. Rate: ");
        JTextField sText = new JTextField(String.valueOf(conf.getLabelDecreasingRate()));
        JLabel colorText = new JLabel(" Color: ");
        JComboBox colors = new JComboBox(possibleColorsDisplay);

        JButton apply = new JButton("Apply");
        JButton reset = new JButton("Default");

        apply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conf.setLabel(possibleColors[colors.getSelectedIndex()], Integer.parseInt(sText.getText()));
                visualizeOnFrame();
            }
        });

        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conf.setLabel(Color.black, 4);
                visualizeOnFrame();
            }
        });

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(size);
        panel.add(sText);
        panel.add(colorText);
        panel.add(colors);
        panel.add(reset);
        panel.add(apply);
        parFrame.add(panel);
        parFrame.setVisible(true);
    }

    public void columnParametersClassic() {
        JFrame win = new JFrame("Column Selection");
        win.setResizable(false);
        win.setBounds(700, 0, 320, 120); // x,y,widht, height
        setLayout(new FlowLayout());

        JCheckBoxMenuItem header = new JCheckBoxMenuItem("Header", false);
        JLabel firstFileLinesLabel = new JLabel("First line of the file:"); // yt
        JLabel firstFileLines = new JLabel(fileConf.firstLinesText); // yt

        JTextField textField = new JTextField(20);

        firstFileLines.setForeground(Color.blue);
        firstFileLines.setFont(new Font("Serif", Font.BOLD, 18));

        JLabel nameCol = new JLabel(" Column with Names: ");
        JLabel valCol = new JLabel(" Column with Values: ");
        JLabel mes = new JLabel(" ");

        JTextField ntext = new JTextField("0");
        ntext.setToolTipText("Use 0 for the first column");
        JTextField vtext = new JTextField("1");
        vtext.setToolTipText("Use 1 for the second column");

        JButton vis = new JButton("Visualize!");
        JPanel p1 = new JPanel(new GridLayout(0, 2));

        vis.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileConf.nameColumn = Integer.parseInt(ntext.getText());
                fileConf.valueColumn = Integer.parseInt(vtext.getText());
                win.setVisible(false);
                conf.setMax(30);
                conf.setMin(2);
                initializeConfig();
                visualizeOnFrame();
            }
        });

        header.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (header.getState()) {
                    fileConf.setHasHeader(true);
                } else {
                    fileConf.setHasHeader(false);
                }
            }
        });

        p1.add(firstFileLinesLabel);
        p1.add(firstFileLines);
        p1.add(nameCol);
        p1.add(ntext);
        p1.add(valCol);
        p1.add(vtext);
        p1.add(mes);
        p1.add(vis);
        p1.add(header);

        win.add(p1);
        win.setVisible(true);

    }

    public void columnParametersPieChart() {
        JFrame win = new JFrame("ColumnSelection");
        win.setResizable(false);
        win.setBounds(700, 0, 520, 200);
        setLayout(new FlowLayout());

        JLabel firstFileLinesLabel = new JLabel("First line of the file:"); // yt
        JLabel firstFileLines = new JLabel(fileConf.firstLinesText); // yt
        firstFileLines.setForeground(Color.blue);
        firstFileLines.setFont(new Font("Serif", Font.BOLD, 18));

        JLabel nameCol = new JLabel(" Column with Names:  ");
        JLabel valCol = new JLabel("Column with Values: ");
        JLabel gpcol = new JLabel(" Column with Groupby values: ");
        JLabel mes = new JLabel(" ");
        JTextField ntext = new JTextField("0");
        ntext.setToolTipText("Use 0 for the first column");
        JTextField gptext = new JTextField("1");
        gptext.setToolTipText("Use 1 for the second column");
        JTextField vtext = new JTextField("2");
        vtext.setToolTipText("Use 2 for the third column");

        JButton vis = new JButton("Visualize!");
        JPanel p1 = new JPanel(new GridLayout(0, 2));

        vis.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileConf.nameColumn = Integer.parseInt(ntext.getText());
                fileConf.valueColumn = Integer.parseInt(vtext.getText());
                fileConf.groupingColumn = Integer.parseInt(gptext.getText());
                win.setVisible(false);
                conf.setMax(10);
                conf.setMin(5);
                initializeConfig();
                visualizeOnFrame();
            }
        });

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

    public static void saveProgressGUI(VisConfig conf, FileConf fconf) {
        JFrame modelFrame = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select save location.");
        int userSelection = fileChooser.showSaveDialog(modelFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                saveCurrentProgress(conf, fconf, filePath + ".json");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void saveCurrentProgress(VisConfig conf, FileConf fconf, String path) throws IOException {

        JSONObject cospiSave = new JSONObject();

        cospiSave.put("Dataset", fconf.getFilename());
        cospiSave.put("Min", conf.getMin());
        cospiSave.put("Max", conf.getMax());
        cospiSave.put("AngleMax", conf.getAngleMax());
        cospiSave.put("AngleMin", conf.getAngleMin());
        cospiSave.put("RoadSize", conf.getRoadSize());
        cospiSave.put("LabelColorRGB", conf.getLabelColor().getRGB());
        cospiSave.put("LabelDecRate", conf.getLabelDecreasingRate());
        cospiSave.put("RectColorRGB", conf.getRectColor().getRGB());
        cospiSave.put("EnableInfo", conf.isEnableInfo());
        cospiSave.put("ShowName", conf.isShowName());
        cospiSave.put("ShowRank", conf.isShowRank());
        cospiSave.put("ShowVal", conf.isShowVal());
        cospiSave.put("AllowOverlap", conf.isAllowOverlap());
        cospiSave.put("GroupCol", fconf.getGroupingColumn());
        cospiSave.put("NameCol", fconf.getNameColumn());
        cospiSave.put("ValueCol", fconf.getValueColumn());
        cospiSave.put("ShapeGaps", conf.getShapeGaps().toString());
        cospiSave.put("DrawStyle", conf.getDrawStyle().toString());
        cospiSave.put("ExpandStyle", conf.getExpandStyle().toString());
        cospiSave.put("Direction", conf.getDirection().toString());
        cospiSave.put("Axes", conf.getAxes().toString());
        cospiSave.put("ToBePieChart", fconf.isToBePieChart());

        Files.write(Paths.get(path), cospiSave.toString().getBytes());
    }

    public static ImmutablePair<VisConfig, FileConf> loadSavedProgress(String filepath) throws IOException, ParseException {

        VisConfig conf = new VisConfig();
        FileConf fconf = new FileConf();

        // FileReader reader = new FileReader(filepath);
        JSONObject cospiConf = (JSONObject) new JSONParser().parse(new FileReader(filepath));

        // conf.setLabel(labelColor, sizeDecreasingRate);
        // conf.setLabelParams(rank, name, val, color, decRate);
        conf.setMax(((Long) cospiConf.get("Max")).intValue());
        conf.setMin(((Long) cospiConf.get("Min")).intValue());
        conf.setAngleMax((Double) cospiConf.get("AngleMax"));
        conf.setAngleMin((Double) cospiConf.get("AngleMin"));
        conf.setRoadSize(((Long) cospiConf.get("RoadSize")).intValue());
        conf.setLabelDecreasingRate(((Long) cospiConf.get("LabelDecRate")).intValue());

        conf.setShapeGaps(ShapeGaps.valueOf((String) cospiConf.get("ShapeGaps")));
        conf.setDirection(Direction.valueOf((String) cospiConf.get("Direction")));
        conf.setDrawStyle(DrawStyle.valueOf((String) cospiConf.get("DrawStyle")));
        conf.setExpandStyle(ExpandStyle.valueOf((String) cospiConf.get("ExpandStyle")));
        conf.setAxes(Axes.valueOf((String) cospiConf.get("Axes")));

        conf.setRectColor(new Color(((Long) cospiConf.get("RectColorRGB")).intValue()));
        conf.setLabelColor(new Color(((Long) cospiConf.get("LabelColorRGB")).intValue()));

        conf.setAllowOverlap((Boolean) cospiConf.get("AllowOverlap"));
        conf.setEnableInfo((Boolean) cospiConf.get("EnableInfo"));
        conf.setShowName((Boolean) cospiConf.get("ShowName"));
        conf.setShowRank((Boolean) cospiConf.get("ShowRank"));
        conf.setShowVal((Boolean) cospiConf.get("ShowVal"));

        fconf.setFilename((String) cospiConf.get("Dataset"));
        fconf.setGroupingColumn(((Long) cospiConf.get("GroupCol")).intValue());
        fconf.setNameColumn(((Long) cospiConf.get("NameCol")).intValue());
        fconf.setValueColumn(((Long) cospiConf.get("ValueCol")).intValue());
        fconf.setToBePieChart((Boolean) cospiConf.get("ToBePieChart"));

        return new ImmutablePair<>(conf, fconf);
    }

    public static void openSiteInBrowser(String URL) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(URL));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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

    public static String fileSelectionGUI() {
        String filepath = "";
        try {

            //java.net.URL imgURL = getClass().getResource(path);
            JFileChooser fileChooser = new JFileChooser();
            //String path = this.getClass().getClassLoader().getResource("/datasets/").toExternalForm();
            //ClassLoader classLoader = getClass().getClassLoader();
            //File f = new File(classLoader.getResource("datasets/").getFile());
            fileChooser.setCurrentDirectory(new File("./src/main/resources/datasets")); // for opening the folder Resources //
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

    void createConsoleOutput(JPanel parentPanel) {
        JPanel consolePanel = new JPanel(new GridLayout(1, 1, 5, 5)); // rows, columns, int hgap, int vgap)
        consolePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Console output"));

        // A: OUTPUT TEXT AREA
        //JTextArea 
        consoleOutputArea = new JTextArea(
                "Console output"
        );
        //textOutputArea.setFont(new Font("Courier", NORMAL, 22));  //
        //consoleOutputArea.setFont(consoleTextfont);
        consoleOutputArea.setLineWrap(true);
        consoleOutputArea.setWrapStyleWord(true);
        consoleOutputArea.setEditable(false);

        JScrollPane areaScrollPane = new JScrollPane(consoleOutputArea);
        areaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //areaScrollPane.setPreferredSize(textAreaDimension);

        consolePanel.add(areaScrollPane);
        //add(outputPanel); // adds to Frame
        if (parentPanel == null) { // if no parent panel
            this.add(consolePanel); // adds to Frame
        } else {
            parentPanel.add(consolePanel);
        }

    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {

            public void write(int b) throws IOException {
                updateConsoleTextArea(String.valueOf((char) b));
            }

            public void write(byte[] b, int off, int len) throws IOException {
                updateConsoleTextArea(new String(b, off, len));
            }

            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    private void updateConsoleTextArea(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                consoleOutputArea.append(text);

            }
        });
    }

    private InputStream getFileFromResourceAsStream(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        //InputStream cpResource = this.getClass().getClassLoader().getResourceAsStream("file.name");

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }

    }

    public static void main(String[] args) {
        AppWelcome t = new AppWelcome();
        CoSpiGUI app = new CoSpiGUI();
    }

}

class AppWelcome extends JFrame {

    AppWelcome() {
        Image image = null;
        try {
            // Setting the image in the window
            URL imgURL = getClass().getResource("/icons/Image.png");
            image = ImageIO.read(imgURL);

            // Setting the ICON
            ImageIcon icon = new ImageIcon(imgURL, "Icon");
            this.setIconImage(icon.getImage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // this.setSize(600, 300);
        this.setBounds(600, 300, 600, 300); // setBounds(x, y, width, height)
        this.getContentPane().setBackground(Color.white);
        this.setLayout(new GridLayout(0, 2, 0, 0)); // rows, columns, int hgap, int vgap)
        this.add(new JLabel(new ImageIcon(image)));
        this.add(new JLabel(
                "<html> <h1>COSPI v1.0 </h1> <br> by Yannis Tzitzikas<br> and Manos Chatzakis <br> Nov, 2020</html>"));
        this.setVisible(true);
        try {
            Thread.sleep(3000);
            dispose();
        } catch (Exception e) {
            System.err.println(e);
        }

    }
}

@Data
class FileConf {

    String filename;
    String firstLinesText = "Lala | Lala | Lala";

    InputStream is;

    boolean usingInputStream;
    boolean toBePieChart;
    boolean hasHeader = false;

    int nameColumn;
    int valueColumn;
    int groupingColumn;

    private void readFirstLines(int numOfLines) {

        if (filename == null) {
            return;
        }

        firstLinesText = "";

        String line = "";
        String cvsSplitBy = ",";
        int lineno = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while (((line = br.readLine()) != null) && (lineno < numOfLines)) {
                System.out.println("Read from file the line: " + line);
                lineno++;
                for (String s : line.split(cvsSplitBy)) {
                    firstLinesText = firstLinesText + " | " + s;
                }
                firstLinesText += "\n";
            }
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("Canceled File Selection");
        }
    }

    public FileConf(int nameColumn, int valueColumn, int groupingColumn, boolean toBePieChart) {
        this.groupingColumn = groupingColumn;
        this.nameColumn = nameColumn;
        this.valueColumn = valueColumn;
        this.toBePieChart = toBePieChart;
        readFirstLines(1);
    }

    public FileConf(int nameColumn, int valueColumn, boolean toBePieChart, String filename) {
        this.nameColumn = nameColumn;
        this.valueColumn = valueColumn;
        this.toBePieChart = toBePieChart;
        this.filename = filename;
        readFirstLines(1);
    }

    public FileConf(int nameColumn, int valueColumn, int groupingColumn, boolean toBePieChart, String filename) {
        this.groupingColumn = groupingColumn;
        this.nameColumn = nameColumn;
        this.valueColumn = valueColumn;
        this.toBePieChart = toBePieChart;
        this.filename = filename;
        readFirstLines(1);
    }

    public FileConf() {
    }
}
