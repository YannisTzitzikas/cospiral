package utils;


import java.awt.Color;

/**
 * 
 * @author Yannis Tzitzikas (yannistzitzik@gmail.com)
 *
 *  A class for representing pixels each having a x and y coordinate and a color.
 */

public class Pixel {
    
    private int x;
    private int y;
    private boolean isOccupied = false;
    private Color color;    
    
    public Pixel(int x, int y, Color color){
        this.x=x;
        this.y=y;
        this.color=color;
    }
    
    public Pixel(int x, int y, Color color, boolean isOccupied){
        this.x=x;
        this.y=y;
        this.color=color;
        this.isOccupied = isOccupied;
    }
    
    public void setX(int x){
        this.x=x;
    }
    
    public void setY(int y){
        this.y=y;
    }
    
    public void setColor(Color color){
        this.color=color;
    }
    
    public int getX(){
        return this.x;
    }
    
    public int getY(){
        return this.y;
    }
    
    public Color getColor(){
        return this.color;
    }

	public boolean isOccupied() {
		return isOccupied;
	}

	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}
    
}
