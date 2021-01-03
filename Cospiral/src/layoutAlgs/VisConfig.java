package layoutAlgs;

import java.awt.Color;
import layoutAlgs.params.*;

/**
 * @author Yannis Tzitzikas (yannistzitzik@gmail.com): Main author
 * @author Manos Chatzakis (chatzakis@ics.forth.gr): (Added parameters for Coloring Labels and information about max and min values)
 * Class holding all parameters and visual configurations.
 */
public class VisConfig implements Cloneable {
	
	/*
	 * Standard configurations 
	 */
	private DrawStyle drawStyle     = DrawStyle.Filled; 
	private Direction direction     = Direction.Expand;
	private ExpandStyle expandStyle = ExpandStyle.Spiral; 
	private ShapeGaps  shapeGaps    = ShapeGaps.Normal; 
	private Axes axes               = Axes.NoAxes; 	

	private double AngleMin = 0; 
	private double AngleMax = 2 * Math.PI;	
	
	/*
	 * Extended configurations
	 */
	private Color color      = Color.blue;
	private Color labelColor = Color.black;
	private Color rectColor  = Color.blue;
	
	private int roadSize = 2;
	private int labelDecreasingRate = 4;
	
	private int max;
	private int min;
	
	private boolean enableInfo   = false;
	private boolean showRank     = false;
	private boolean showName     = false;
	private boolean showVal      = false;
	private boolean allowOverlap = false;
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public VisConfig(){
		
	}

	public DrawStyle getDrawStyle() {
		return drawStyle;
	}

	public void setDrawStyle(DrawStyle drawStyle) {
		this.drawStyle = drawStyle;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public ExpandStyle getExpandStyle() {
		return expandStyle;
	}

	public void setExpandStyle(ExpandStyle expandStyle) {
		this.expandStyle = expandStyle;
	}

	public ShapeGaps getShapeGaps() {
		return shapeGaps;
	}

	public void setShapeGaps(ShapeGaps shapeGaps) {
		this.shapeGaps = shapeGaps;
	}

	public int getRoadSize() {
		return roadSize;
	}

	public void setRoadSize(int roadSize) {
		this.roadSize = roadSize;
	}

	public double getAngleMin() {
		return AngleMin;
	}

	public void setAngleMin(double angleMin) {
		AngleMin = angleMin;
	}

	public double getAngleMax() {
		return AngleMax;
	}

	public void setAngleMax(double angleMax) {
		AngleMax = angleMax;
	}

	public Axes getAxes() {
		return axes;
	}

	public void setAxes(Axes axes) {
		this.axes = axes;
	}
	
	public void setLabel(Color labelColor,int sizeDecreasingRate) {
		this.labelColor = labelColor;
		this.labelDecreasingRate = sizeDecreasingRate;
	}
	
	public Color getLabelColor() {
		return labelColor;
	}

	public void setLabelColor(Color labelColor) {
		this.labelColor = labelColor;
	}

	public int getLabelDecreasingRate() {
		return labelDecreasingRate;
	}

	public void setLabelDecreasingRate(int labelDecreasingRate) {
		this.labelDecreasingRate = labelDecreasingRate;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getRectColor() {
		return rectColor;
	}

	public void setRectColor(Color rectColor) {
		this.rectColor = rectColor;
	}

	public boolean isEnableInfo() {
		return enableInfo;
	}

	public void setEnableInfo(boolean enableInfo) {
		this.enableInfo = enableInfo;
	}

	public boolean isShowRank() {
		return showRank;
	}

	public void setShowRank(boolean showRank) {
		this.showRank = showRank;
	}

	public boolean isShowName() {
		return showName;
	}

	public void setShowName(boolean showName) {
		this.showName = showName;
	}

	public boolean isShowVal() {
		return showVal;
	}

	public void setShowVal(boolean showVal) {
		this.showVal = showVal;
	}
	
	public boolean includeLabels() {
		return showVal||showName||showRank;
	}
	
	public void setLabelParams(boolean rank,boolean name,boolean val,Color color, int decRate) {
		this.showRank = rank;
		this.showName = name;
		this.showVal = val;
		this.labelColor = color;
		this.labelDecreasingRate = decRate;
	}
	
	public void disableLabels() {
		this.showRank = false;
		this.showName = false;
		this.showVal = false;
	}
	
	public void enableLabels() {
		this.showRank = true;
		this.showName = true;
		this.showVal = true;
	}

	public boolean isAllowOverlap() {
		return allowOverlap;
	}
	
	public boolean getAllowedOverlap() {
		return allowOverlap;
	}

	public void setAllowOverlap(boolean allowOverlap) {
		this.allowOverlap = allowOverlap;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

}
