/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cospi;

import lombok.Data;
import cospi.params.*;
import java.awt.Color;

/**
 *
 * @author manos
 */
@Data
public class VisConfig implements Cloneable {

    private int N = 1000;

    private DrawStyle drawStyle = DrawStyle.Filled;
    private Direction direction = Direction.Expand;
    private ExpandStyle expandStyle = ExpandStyle.Spiral;
    private ShapeGaps shapeGaps = ShapeGaps.Normal;
    private Axes axes = Axes.NoAxes;

    private double AngleMin = 0;
    private double AngleMax = 2 * Math.PI;

    private Color labelColor = Color.black;
    private Color rectColor = Color.blue;

    private int roadSize = 2;
    private int labelDecreasingRate = 4;

    private int max = 20;
    private int min = 1;

    private boolean enableInfo = false;
    private boolean showRank = false;
    private boolean showName = false;
    private boolean showVal = false;
    private boolean allowOverlap = false;

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean includeLabels() {
        return showVal || showName || showRank;
    }

    public void setLabelParams(boolean rank, boolean name, boolean val, Color color, int decRate) {
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

    public void setLabel(Color labelColor, int sizeDecreasingRate) {
        this.labelColor = labelColor;
        this.labelDecreasingRate = sizeDecreasingRate;
    }

}
