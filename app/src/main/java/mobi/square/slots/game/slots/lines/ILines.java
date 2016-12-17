package mobi.square.slots.game.slots.lines;

import mobi.square.slots.game.slots.CSymbol;

public interface ILines {

    CSymbol[] getLine(int index);

    int[][] getLines();

    String[] getColors();

}
