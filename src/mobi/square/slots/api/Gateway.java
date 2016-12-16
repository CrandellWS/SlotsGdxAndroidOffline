package mobi.square.slots.api;

import java.util.HashMap;
import java.util.Map;

import mobi.square.slots.enums.CardColor;
import mobi.square.slots.enums.Method;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.game.user.CUser;
import mobi.square.slots.game.user.CUserController;

public class Gateway {

	private CUser user;
	private Map<String, String> params;

	public Gateway() {
		this.user = null;
	}

	// Public Methods

	public String call(Map<String, String> params) throws StringCodeException {
		if (params == null)
			params = new HashMap<String, String>();
		this.params = params;
		String method_str = params.get("method");
		if (method_str == null)
			throw new StringCodeException("unknown_method");
		String json = null;
		Method method = Method.valueOf(method_str);
		switch (method) {
			case jsLobby:			json = jsLobby();			break;
			case jsInit:			json = jsInit();			break;
			case jsSpin:			json = jsSpin();			break;
			case jsMaxBet:			json = jsMaxBet();			break;
			case jsChangeConfig:	json = jsChangeConfig();	break;
			case jsInitRisk:		json = jsInitRisk();		break;
			case jsRisk:			json = jsRisk();			break;
			case jsInitGreater:		json = jsInitGreater();		break;
			case jsRiskGreater:		json = jsRiskGreater();		break;
			case jsTakeAward:		json = jsTakeAward();		break;
			case jsBank:			json = jsBank();			break;
			case jsBonusState:		json = jsBonusState();		break;
			case jsBonusProc:		json = jsBonusProc();		break;
			case jsAddGold:			json = jsAddGold();			break;
			case jsSetUserMoney:	json = jsSetUserMoney();	break;
			case jsTakeBonus:		json = jsTakeBonus();		break;
			case jsTakeSuperBonus:	json = jsTakeSuperBonus();	break;
			case jsSpinRoulette:	json = jsSpinRoulette();	break;
			default:
				throw new StringCodeException("unknown_method");
		}
		this.getUser().save();
		return json;
	}

	// Private Methods

	private CUser getUser() {
		if (this.user == null)
			this.user = CUserController.newUser(Connection.getInstance().getDeviceId());
		return this.user;
	}

	private int getInt(String key) {
		if (this.params == null)
			return 0;
		String value = this.params.get(key);
		if (value == null)
			return 0;
		return Integer.parseInt(value);
	}

	private String getString(String key) {
		if (this.params == null)
			return "";
		String value = this.params.get(key);
		if (value == null)
			return "";
		return value;
	}

	// Methods

	public String jsLobby() {
		return this.getUser().getJsonController().jsLobby();
	}

	public String jsInit() {
		SlotsType type = SlotsType.valueOf(this.getString("type").toUpperCase());
		String json = this.getUser().getJsonController().jsInit(type);
		return json;
	}

	public String jsSpin() {
		SlotsType type = SlotsType.valueOf(this.getString("type").toUpperCase());
		String json = this.getUser().getJsonController().jsSpin(type);
		return json;
	}

	public String jsMaxBet() {
		this.getUser().getSlots().maxBet();
		return this.getUser().getJsonController().jsChangeConfig();
	}

	public String jsChangeConfig() {
		int lines = this.getInt("lines");
		int bet = this.getInt("bet");
		String json = this.getUser().getJsonController().jsChangeConfig(lines, bet);
		return json;
	}

	public String jsInitRisk() {
		return this.getUser().getJsonController().jsInitRisk();
	}

	public String jsRisk() {
		CardColor color = CardColor.valueOf(this.getString("color"));
		return this.getUser().getJsonController().jsRisk(color);
	}

	public String jsInitGreater() {
		return this.getUser().getJsonController().jsInitRiskGreater();
	}

	public String jsRiskGreater() {
		int index = this.getInt("index");
		return this.getUser().getJsonController().jsRiskGreater(index);
	}

	public String jsTakeAward() {
		return this.getUser().getJsonController().jsTakeAward();
	}

	public String jsBank() {
		return this.getUser().getJsonController().jsBank();
	}

	public String jsBonusState() {
		return this.getUser().getJsonController().jsBonusState();
	}

	public String jsBonusProc() {
		int index = this.getInt("index");
		String json = this.getUser().getJsonController().jsBonusProc(index);
		this.getUser().getSlots().getBonusGameInstance().save();
		return json;
	}

	public String jsAddGold() {
		String order_id = this.getString("order_id");
		String purchase_id = this.getString("purchase_id");
		String json = this.getUser().getJsonController().jsAddGold(order_id, purchase_id);
		return json;
	}

	public String jsTakeBonus() {
		return this.getUser().getJsonController().jsTakeBonus();
	}

	public String jsTakeSuperBonus() {
		return this.getUser().getJsonController().jsTakeSuperBonus();
	}

	public String jsSpinRoulette() {
		return this.getUser().getJsonController().jsSpinRoulette();
	}

	public String jsSetUserMoney() {
		int money = this.getInt("money");
		this.getUser().setMoney(money);
		return "{\"error\":0,\"money\":" + money + "}";
	}

}
