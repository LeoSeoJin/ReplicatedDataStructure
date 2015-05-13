package org.worldsproject.puzzle.enums;

public enum Difficulty {
	EASY, MEDIUM, HARD;

	public int pieceSize() {
		if (this == EASY) 
			return 384;
		else if (this == MEDIUM) 
			return 256;
		else 
			return 192;
	}
	
	public int getOffset() {
		if (this == EASY)
			return 60;
		if (this == MEDIUM)
			return 40;
		else
			return 30;
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
			return 4;
		else if (this == MEDIUM) 
			return 9;
		else 
			return 16;
	}
	
	public int getRow() {
		if (this == EASY) 
			return 2;
		else if (this == MEDIUM) 
			return 3;
		else 
			return 4;		
	}
	
	public int getCol() {
		if (this == EASY) 
			return 2;
		else if (this == MEDIUM) 
			return 3;
		else 
			return 4;			
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
