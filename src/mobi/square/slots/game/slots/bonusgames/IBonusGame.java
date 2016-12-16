package mobi.square.slots.game.slots.bonusgames;

import java.io.Serializable;
import java.util.HashMap;

import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.game.slots.CSlots;
import mobi.square.slots.utils.json.JsonNode;

public interface IBonusGame extends Serializable {
	
	public void save();
	
	public void proc(int index);
	public void end();
	
	public void setOver(boolean over);
	public boolean isOver();
	 
	public int getAward();
	
	public BonusGame getType();
	
	public CSlots getParent();
	public void setParent(CSlots parent);
	
	public HashMap<String, JsonNode> getStateJson();
	
}
