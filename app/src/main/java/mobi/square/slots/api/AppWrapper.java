package mobi.square.slots.api;

import java.util.Map;

import mobi.square.slots.api.SlotsApi.LoginType;
import mobi.square.slots.containers.BankInfo;
import mobi.square.slots.stages.Header;

public interface AppWrapper {

    void clearVars();

    String getDeviceId();

    boolean readSoundState();

    void writeSoundState(boolean state);

    boolean readNotificationState();

    void writeNotificationState(boolean state);

    String readDeviceId();

    void writeDeviceId(String device_id);

    void readData(Map<String, String> strings, Map<String, Integer> integers);

    void writeData(Map<String, String> strings, Map<String, Integer> integers);

    void authorize(LoginType type);

    void saveDataPixtel();

    boolean checkShowButton();

    void startEnterCode();

    void updateHeader(Header window);

    void gotoCGV();

    //	public void showMessagePIXTEL(String msg);
    void showMessagePIXTEL(String msg, int multipler);

    void getBankItems(Header window, BankInfo[] items, GetBankItems handler);

    interface GetBankItems {
        void get_items(BankInfo[] items);
    }

}
