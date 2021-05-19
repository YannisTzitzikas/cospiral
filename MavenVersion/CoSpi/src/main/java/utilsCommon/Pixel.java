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
 * @author Yannis Tzitzikas (yannistzitzik@gmail.com)
 *
 * A class for representing pixels each having a x and y coordinate and a color.
 */
@Data
public class Pixel {

    private int x;
    private int y;
    private boolean isOccupied = false;
    private Color color;

    public Pixel(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public Pixel(int x, int y, Color color, boolean isOccupied) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.isOccupied = isOccupied;
    }
}
