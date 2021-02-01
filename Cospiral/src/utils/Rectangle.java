package utils;

import java.awt.Color;

/**
 * This class simulates a rectangle in the terms of the CoSpi algorithm.
 * 
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 */
public class Rectangle {

	public int x;
	public int y;
	public int len;
	public int originalValue;
	public int rank;
	public int normalizedValue;

	public Color color;
	public String originalName;

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
	public Rectangle(int x, int y, int len, int originalValue, String originalName) {
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
	public Rectangle(int x, int y, int len, int originalValue, String originalName, Color color) {
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
	public Rectangle(int x, int y, int len, int originalValue, int rank, String originalName, Color color) {
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
	public Rectangle(int x, int y, int len, int originalValue, int normalizedValue, int rank, String originalName,
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

	/**
	 * 
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * 
	 * @param x
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * 
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * 
	 * @param y
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * 
	 * @return
	 */
	public int getLen() {
		return len;
	}

	/**
	 * 
	 * @param len
	 */
	public void setLen(int len) {
		this.len = len;
	}

	/**
	 * 
	 * @return
	 */
	public int getOriginalValue() {
		return originalValue;
	}

	/**
	 * 
	 * @param originalValue
	 */
	public void setOriginalValue(int originalValue) {
		this.originalValue = originalValue;
	}

	/**
	 * 
	 * @return
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * 
	 * @param color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * 
	 * @return
	 */
	public String getOriginalName() {
		return originalName;
	}

	/**
	 * 
	 * @param originalName
	 */
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	/**
	 * 
	 * @param rank
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * 
	 * @return
	 */
	public int getRank() {
		return rank;
	}
}
