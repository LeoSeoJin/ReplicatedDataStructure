package com.example.puzzle;

import android.content.Context;
import android.graphics.Point;
import android.widget.ImageButton;

public class Piece extends ImageButton{
	private Point location;
	private Point initial_loc;
	
	private boolean hasTop = false;  //indicate whether the piece has other pieces surrounded
	private boolean hasRight = false;
	private boolean hasFeet = false;
	private boolean hasLeft = false;
	
	private boolean traverse = false; // indicate whether the piece can move
	
	public Piece(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public Point getLocation() {
		return location;
	}
	
	public void setLocation(Point p) {
		location = p;
	}
	
	public boolean isHasTop() {
		return hasTop;
	}

	public void setHasTop(boolean hasTop) {
		this.hasTop = hasTop;
	}

	public boolean isHasRight() {
		return hasRight;
	}

	public void setHasRight(boolean hasRight) {
		this.hasRight = hasRight;
	}

	public boolean isHasFeet() {
		return hasFeet;
	}

	public void setHasFeet(boolean hasFeet) {
		this.hasFeet = hasFeet;
	}

	public boolean isHasLeft() {
		return hasLeft;
	}

	public void setHasLeft(boolean hasLeft) {
		this.hasLeft = hasLeft;
	}

	public boolean isTraverse() {
		return traverse;
	}

	public void setTraverse(boolean traverse) {
		this.traverse = traverse;
	}

	public void setInitialLocation(Point p) {
		this.initial_loc = p;
	}
	
	public Point getInitialLocation() {
		return initial_loc;
	}
}
