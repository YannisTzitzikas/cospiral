/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilsCommon;

import java.awt.Color;
import lombok.Data;

/**
 *
 * @author manos
 */
@Data
public class Rectangle {

    private int x;
    private int y;
    private int len;
    private double originalValue;
    private int rank;
    private int normalizedValue;

    private Color color;
    private String originalName;

    /**
     *
     */
    public Rectangle() {

    }

    public Rectangle(int len) {
        this.len = len;
    }

    /**
     *
     * @param x
     * @param y
     * @param len
     */
    public Rectangle(int x, int y, int len) {
        this.x = x;
        this.y = y;
        this.len = len;
    }

    /**
     *
     * @param x
     * @param y
     * @param len
     * @param originalValue
     * @param originalName
     */
    public Rectangle(int x, int y, int len, double originalValue, String originalName) {
        this.x = x;
        this.y = y;
        this.len = len;
        this.originalName = originalName;
        this.originalValue = originalValue;
    }

    /**
     *
     * @param x
     * @param y
     * @param len
     * @param originalValue
     * @param originalName
     * @param color
     */
    public Rectangle(int x, int y, int len, double originalValue, String originalName, Color color) {
        this.x = x;
        this.y = y;
        this.len = len;
        this.originalName = originalName;
        this.originalValue = originalValue;
        this.color = color;
    }

    /**
     *
     * @param x
     * @param y
     * @param len
     * @param originalValue
     * @param rank
     * @param originalName
     * @param color
     */
    public Rectangle(int x, int y, int len, double originalValue, int rank, String originalName, Color color) {
        this.x = x;
        this.y = y;
        this.len = len;
        this.originalName = originalName;
        this.originalValue = originalValue;
        this.color = color;
        this.rank = rank;
    }

    /**
     *
     * @param x
     * @param y
     * @param len
     * @param originalValue
     * @param rank
     * @param originalName
     * @param color
     */
    public Rectangle(int x, int y, int len, double originalValue, int normalizedValue, int rank, String originalName,
            Color color) {
        this.x = x;
        this.y = y;
        this.len = len;
        this.originalName = originalName;
        this.originalValue = originalValue;
        this.color = color;
        this.rank = rank;
        this.normalizedValue = normalizedValue;
    }
    
    public String getRealValueAsStr(){
        if(originalValue == (int) originalValue){
            return ((int)originalValue) + "";
        }
        else{
            return originalValue + "";
        }
    }
    
}
