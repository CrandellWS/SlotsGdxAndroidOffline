package mobi.square.slots.game.slots.lines;

import mobi.square.slots.game.slots.CSymbol;

public interface ILines {

	public CSymbol[] getLine(int index);
	
	public int[][] getLines();
	public String[] getColors();
	
}
