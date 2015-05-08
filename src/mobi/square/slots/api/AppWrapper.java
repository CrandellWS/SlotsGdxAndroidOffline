package mobi.square.slots.api;

import java.util.Map;

import mobi.square.slots.api.SlotsApi.LoginType;
import mobi.square.slots.containers.BankInfo;
import mobi.square.slots.stages.Header;

public interface AppWrapper {

	public void clearVars();
	public String getDeviceId();

	public boolean readSoundState();
	public void writeSoundState(boolean state);

	public boolean readNotificationState();
	public void writeNotificationState(boolean state);

	public String readDeviceId();
	public void writeDeviceId(String device_id);

	public void readData(Map<String, String> strings, Map<String, Integer> integers);
	public void writeData(Map<String, String> strings, Map<String, Integer> integers);

	public void authorize(LoginType type);

	void validatePaymentOnResume();
	void saveDataPixtel();
	boolean checkShowButton();
	void startEnterCode();
	void updateHeader(Header window);
	public void gotoCGV();
	public void showMessagePIXTEL(String msg);
	public void purchase(String id);
	public void getBankItems(Header window, BankInfo[] items, GetBankItems handler);
	public interface GetBankItems {
		public void get_items(BankInfo[] items);
	}

}
