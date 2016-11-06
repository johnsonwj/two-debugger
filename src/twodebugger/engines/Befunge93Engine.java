/*
 * Copyright (C) 2016 johns
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package twodebugger.engines;

import java.util.Scanner;

/**
 *
 * @author johns
 */
public class Befunge93Engine extends BefungeEngine {
	
	private static final int MAX_GRID_HEIGHT = 25;
	private static final int MAX_GRID_WIDTH = 80;

	private final char[][] grid = new char[MAX_GRID_WIDTH][MAX_GRID_HEIGHT];

	public Befunge93Engine(Scanner srcScanner) {
		int row = 0;
		while (srcScanner.hasNextLine()) {
			if (row >= MAX_GRID_HEIGHT) {
				String msg = "Source has too many rows.  "
						+ "(max length is " + Integer.toString(MAX_GRID_HEIGHT) + " lines)";
				throw new IllegalArgumentException(msg);
			}
			
			String line = srcScanner.nextLine();
			
			if (line.length() >= MAX_GRID_WIDTH) {
				String msg = "Source row " + Integer.toString(row) + " is too long.  "
						+ "(max length is " + Integer.toString(MAX_GRID_WIDTH) + " chars)";
				throw new IllegalArgumentException(msg);
			}
			
			for (int col = 0; col < line.length(); ++col) {
				
				grid[row][col] = validateAscii(line.charAt(col));
			}
				
			
			++row;
		}
	}
	
	@Override
	protected char getNextInstruction() {
		return grid[ix][iy];
	}
	
	@Override
	protected void travelLeft() {
		if (ix < 0) ix = MAX_GRID_WIDTH - 1;
		
		while (getNextInstruction() != ' ')
			--ix;
	}
	
	@Override
	protected void travelRight() {
		if (ix >= MAX_GRID_WIDTH) ix = 0;
		
		while (getNextInstruction() != ' ')
			++ix;
	}
	
	@Override
	protected void travelUp() {
		if (iy < 0) iy = MAX_GRID_HEIGHT - 1;
		
		while (getNextInstruction() != ' ')
			--iy;
	}
	
	@Override
	protected void travelDown() {
		if (iy >= MAX_GRID_HEIGHT) iy = 0;
		
		while (getNextInstruction() != ' ')
			++iy;
	}
	
	@Override
	protected char getValue(int x, int y) {
		return grid[x][y];
	}
	
	@Override protected void putValue(int x, int y, char val) {
		grid[x][y] = val;
	}
}
