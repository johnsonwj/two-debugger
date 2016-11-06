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

import java.awt.Point;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 * @author johns
 */
public abstract class BefungeEngine implements TwoDimEngine {
	
	private final Stack<Long> stack = new Stack<>();
	
	protected int ix = 0;
	protected int iy = 0;
	protected Direction id = Direction.RIGHT;
	
	private boolean stringMode = false;
	
	private boolean terminated = false;
	
	protected final char validateAscii(char c) {
		if ((int)c > 255) {
			String msg = "Only ASCII values allowed; got " + c + " (code point "
					+ Integer.toString((int)c) + ").";
			throw new IllegalArgumentException(msg);
		}
		return c;
	}
	
	@Override
	public Point getPosition() {
		return new Point(ix, iy);
	}
	
	@Override
	public boolean isTerminated() { return terminated; }
	
	/*
	 * These depend on the board implementation
	 */
	protected abstract char getNextInstruction();
	protected abstract void travelLeft();
	protected abstract void travelRight();
	protected abstract void travelUp();
	protected abstract void travelDown();
	protected abstract char getValue(int x, int y);
	protected abstract void putValue(int x, int y, char val);
	
	private void movePointer() {
		switch (id) {
			case UP:
				--iy;
				return;
			case DOWN:
				++iy;
				return;
			case LEFT:
				--ix;
				return;
			case RIGHT:
				++ix;
		}
	}
	
	private void travel() {
		switch (id) {
			case UP:
				travelUp();
				return;
			case DOWN:
				travelDown();
				return;
			case LEFT:
				travelLeft();
				return;
			case RIGHT:
				travelRight();
		}
	}
	
	private Long popEndlessStack() {
		if (stack.isEmpty()) return 0L;
		return stack.pop();
	}
	
	// Befunge division pops a then b, and returns the integer quotient b/a, rounded toward zero
	private long divide() {
		long a = popEndlessStack();
		long b = popEndlessStack();
		
		long floorDivQuotient = Math.floorDiv(b, a);
		
		if ((a > 0) ^ (b > 0))
			return floorDivQuotient;
		
		return floorDivQuotient + 1;
	}
	
	private long subtract() {
		long a = popEndlessStack();
		long b = popEndlessStack();
		return b - a;
	}
	
	private long mod() {
		long a = popEndlessStack();
		long b = popEndlessStack();
		return b % a;
	}
	
	private long greater() {
		long a = popEndlessStack();
		long b = popEndlessStack();
		return b > a ? 1 : 0;
	}
	
	private void swapTop() {
		long top = popEndlessStack();
		long next = popEndlessStack();
		stack.push(top);
		stack.push(next);
	}
	
	private void putValue() {
		int y = popEndlessStack().intValue();
		int x = popEndlessStack().intValue();
		long v = popEndlessStack();
		putValue(x, y, (char)v);
	}
	
	private long getValue() {
		int y = popEndlessStack().intValue();
		int x = popEndlessStack().intValue();
		return getValue(x, y);
	}
	
	private String input(String msg) {
		System.out.println();
		System.out.print(msg);
		String input = new Scanner(System.in).nextLine();
		System.out.println();
		return input;
	}
	
	private long inputInteger() {
		return Long.parseLong(input("Please give me an integer: "));
	}
	
	private char inputAscii() {
		String input = input("Please give me an ASCII character: ");
		while (input.length() != 1)
			input = input("Please provide exactly one ASCII character: ");
		
		return validateAscii(input.charAt(0));
	}
	
	@Override
	public void step() {
		if (terminated) return;
		
		char inst = getNextInstruction();
		
		if (stringMode)
			stack.push((long)inst);
		else if (inst >= '0' && inst <= '9') {
			stack.push((long)(inst - '0'));
		} else {
			switch (inst) {
				case '+':
					stack.push(popEndlessStack() + popEndlessStack());
					break;
				case '-':
					stack.push(subtract());
					break;
				case '*':
					stack.push(popEndlessStack() * popEndlessStack());
					break;
				case '/':
					stack.push(divide());
					break;
				case '%':
					stack.push(mod());
					break;
				case '!':
					stack.push(popEndlessStack() == 0L ? 1L : 0L);
					break;
				case '`':
					stack.push(greater());
					break;
				case '>':
					id = Direction.RIGHT;
					break;
				case '<':
					id = Direction.LEFT;
					break;
				case '^':
					id = Direction.UP;
					break;
				case 'v':
					id = Direction.DOWN;
					break;
				case '?':
					id = Direction.values()[new Random().nextInt() % 4];
					break;
				case '_':
					id = (popEndlessStack() == 0 ? Direction.RIGHT : Direction.LEFT);
					break;
				case '|':
					id = (popEndlessStack() == 0 ? Direction.DOWN : Direction.UP);
					break;
				case '"':
					stringMode = !stringMode;
					break;
				case ':':
					stack.push(stack.peek());
					break;
				case '\\':
					swapTop();
					break;
				case '$':
					popEndlessStack();
					break;
				case '.':
					System.out.print(popEndlessStack());
					System.out.print(' ');
					break;
				case ',':
					System.out.print((char)popEndlessStack().intValue());
					break;
				case '#':
					movePointer();
					break;
				case 'p':
					putValue();
					break;
				case 'g':
					stack.push(getValue());
					break;
				case '&':
					stack.push(inputInteger());
					break;
				case '~':
					stack.push((long)inputAscii());
					break;
				case '@':
					terminated = true;
					return;
				case ' ': break;
				default:
					throw new IllegalStateException("Undefined instruction: "
							+ new Integer(inst).toString()
					);
			}
		}
		
		movePointer();
		travel();
	}
}
