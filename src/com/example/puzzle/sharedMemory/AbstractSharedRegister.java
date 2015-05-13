package com.example.puzzle.sharedMemory;

import org.worldsproject.puzzle.PuzzleGroup;

public abstract class AbstractSharedRegister {
	public abstract Object read();
	public abstract void write(String type, PuzzleGroup group);
}
