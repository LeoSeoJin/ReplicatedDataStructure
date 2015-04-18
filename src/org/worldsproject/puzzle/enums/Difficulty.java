package org.worldsproject.puzzle.enums;

public enum Difficulty {
	EASY, MEDIUM, HARD;

	public int pieceSize() {
		if (this == EASY) 
			return 240;
		else if (this == MEDIUM) 
			return 180;
		else 
			return 144;
	}
	
	public int getOffset() {
		if (this == EASY)
			return 10;
		if (this == MEDIUM)
			return 8;
		else
			return 5;
	}

	public String toString() {
		if (this == EASY)
			return "easy";
		else if (this == MEDIUM)
			return "medium";
		else
			return "hard";
	}

	public int getPieceNum() {
		if (this == EASY) 
			return 9;
		else if (this == MEDIUM) 
			return 16;
		else 
			return 25;
	}
	
	public int getRow() {
		if (this == EASY) 
			return 3;
		else if (this == MEDIUM) 
			return 4;
		else 
			return 5;		
	}
	
	public int getCol() {
		if (this == EASY) 
			return 3;
		else if (this == MEDIUM) 
			return 4;
		else 
			return 5;			
	}
	
	public static Difficulty getEnumFromString(String e) {
		if (e.equals("easy"))
			return EASY;
		else if (e.equals("medium"))
			return MEDIUM;
		else
			return HARD;
	}
}
