package mobi.square.slots.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import mobi.square.slots.api.AndroidApi;
import mobi.square.slots.api.AppWrapper;
import mobi.square.slots.api.Connection;
import mobi.square.slots.api.SlotsApi.LoginType;
import mobi.square.slots.containers.BankInfo;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.game.bank.Bank;
import mobi.square.slots.game.bank.CPurchase;
import mobi.square.slots.handlers.AsyncJsonHandler;
import mobi.square.slots.logger.Log;
import mobi.square.slots.stages.Header;
import mobi.square.slots.tools.AtlasLoader;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.utils.utils;


public class Main extends AndroidApplication implements AppWrapper {

    private final static int SPIN_UNLOCK = 1;
    private final static int BET_UNLOCK = 2;
    private static final int PICK_CONTACT_REQUEST = 1;
    static boolean gameLoaded = false;
    // Game
    private static Game instance = null;
    //	private static PXInappProduct backupAppProduct = null;
    private static Main app = null;
    private static int backupResult = -9999;
    private static int appProduct = 0;
    private Header ui_window = null;
    private GetBankItems bank_loader = null;
    private AsyncJsonHandler billing_handler = new AsyncJsonHandler() {
        @Override
        public void onCompleted(String json) {
            try {
                Connection.getInstance().responseAddGold(json);
            } catch (StringCodeException e) {
                Log.log(e);
            }
            ui_window.updateUserMoney();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Connection.setWrapper(this);
        //System.out.println("onCreate");

        if (savedInstanceState != null && instance != null) {
            //System.out.println("Fast init");
            instance = null;
            super.initialize(new com.badlogic.gdx.Game() {
                                 @Override
                                 public void create() {
                                     // Nothing need to do
                                 }
                             }
            );
            return;
        }

        app = this;
        // PX API
//		PXInapp.create( this, "A024005128395134104154590460172200536995B607AA", false );
//		//PXInapp.create(this, "A02498732917338113443204513861373695228A09407FE", false);old
//		PXInapp.setPaymentCallback(this);

        // Application
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useGLSurfaceView20API18 = false;
        config.useWakelock = false;
        config.hideStatusBar = true;
        instance = new Game();
        super.initialize(instance, config);

        loadInfoPixtel();
    }

    private void loadInfoPixtel() {
        // System.out.println("info pixtel es " + utils.maxSpinToLock + " bet es " + utils.maxBetToLock);

//		PXInappProduct item = PXInapp.getInappProduct(SPIN_UNLOCK);
//		utils.maxSpinToLock = item.amount;//??

//		item = PXInapp.getInappProduct(BET_UNLOCK);
//		utils.maxBetToLock = item.amount;//??

        // System.out.println("info pixtel es4 " + utils.maxSpinToLock + " bet es " + utils.maxBetToLock);

        if (utils.loadData("slotSoftgamesData", this) != null) {
            StringTokenizer tokens = new StringTokenizer(utils.loadData("slotSoftgamesData", this), "|");
            utils.numberSpinDone = Integer.parseInt(tokens.nextToken());
            utils.accumalatedBet = Integer.parseInt(tokens.nextToken());
            utils.gameUnlocked = Boolean.parseBoolean(tokens.nextToken());
        }

        if (utils.maxBetToLock == 0 && utils.maxSpinToLock == 0) {
            utils.gameUnlocked = true;
        }
    }

    // Device ID

    @Override
    protected void onStart() {
        Connection.setWrapper(this);
        super.onStart();
    }

    // Settings

    @Override
    protected void onPause() {
        super.onPause();
//		PXInapp.pause();
    }

    @Override
    protected void onResume() {
        Connection.setWrapper(this);
        super.onResume();
//		PXInapp.resume();
    }

    @Override
    protected void onDestroy() {
        FontsFactory.dispose();
        AtlasLoader.disposeAll();
        Connection.dispose();
        super.onDestroy();
    }

    @Override
    public String getDeviceId() {
        TelephonyManager tm = (TelephonyManager) super.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(super.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }

    @Override
    public boolean readSoundState() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean("soundOn", true);
    }

    @Override
    public void writeSoundState(boolean state) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean("soundOn", state);
        editor.commit();
    }

    @Override
    public boolean readNotificationState() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean("notificationsOn", true);
    }

    @Override
    public void writeNotificationState(boolean state) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean("notificationsOn", state);
        editor.commit();
    }

    // PX API

    @Override
    public String readDeviceId() {
        if (AndroidApi.ONLINE) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            return preferences.getString("deviceId", null);
        } else return this.getDeviceId();
    }

    @Override
    public void writeDeviceId(String device_id) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if (device_id != null) {
            editor.putString("deviceId", device_id);
        } else editor.remove("deviceId");
        editor.commit();
    }

    @Override
    public void readData(Map<String, String> strings, Map<String, Integer> integers) {
        Set<String> keys;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (strings != null) {
            keys = strings.keySet();
            for (String key : keys) {
                strings.put(key, preferences.getString(key, strings.get(key)));
            }
        }
        if (integers != null) {
            keys = integers.keySet();
            for (String key : keys) {
                Integer value = integers.get(key);
                integers.put(key, preferences.getInt(key, value != null ? value.intValue() : 0));
            }
        }
    }

    @Override
    public void writeData(Map<String, String> strings, Map<String, Integer> integers) {
        Set<String> keys;
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if (strings != null) {
            keys = strings.keySet();
            for (String key : keys) {
                editor.putString(key, strings.get(key));
            }
        }
        if (integers != null) {
            keys = integers.keySet();
            for (String key : keys) {
                editor.putInt(key, integers.get(key));
            }
        }
        editor.commit();
    }

    @Override
    public void saveDataPixtel() {
        String register = String.valueOf(utils.numberSpinDone);
        register += "|";
        register += String.valueOf(utils.accumalatedBet);
        register += "|";
        register += String.valueOf(utils.gameUnlocked);

        utils.saveData("slotSoftgamesData", register, this);
    }

    @Override
    public void updateHeader(Header window) {
        ui_window = window;
        System.out.println("id ui_window updateHeader " + ui_window + " parent " + this.ui_window.parent_screen);
    }

    @Override
    public void getBankItems(Header window, BankInfo[] items, GetBankItems handler) {
        this.ui_window = window;
        System.out.println("id ui_window getBank " + ui_window + " parent " + this.ui_window.parent_screen);


        this.bank_loader = handler;

        List<CPurchase> list = new ArrayList<CPurchase>();
        for (BankInfo item : items) {
            int id = Integer.parseInt(item.getId());


//			PXInappProduct p = PXInapp.getInappProduct(id);
//			if (p != null) {
            CPurchase unit = new CPurchase();

            //System.out.println("id item es2 "+ id + " precio " + p.priceString);

            item.setDiscount(10);
//				item.setDiscount(p.discountRate);

            item.setPriceString("FreeFun");
//				item.setPriceString(p.priceString);

//				item.setGold(p.amount);
            item.setGold(1000 * id);

            unit.setType("" + id);
            unit.setAmount(0);

            unit.setGold(1000 * id);

            unit.setBonusGold(0);
            unit.setDiscount(0);
            unit.setRecommended(false);
            list.add(unit);
//			} else item.setPriceString("error");
        }

        Bank.setGoldList(list);

        if (bank_loader != null) {
            bank_loader.get_items(items);
            bank_loader = null;
        }
    }

    @Override
    public void gotoCGV() {
        //System.out.println("open this url");
        Uri uri;
        Intent browser;
        String url = "http://www.contractor.support";
        if (url == null)
            return;

        browser = new Intent(Intent.ACTION_VIEW);
        uri = Uri.parse(url);
        browser.setData(uri);
        getContext().startActivity(browser);
    }

    @Override
    public boolean checkShowButton() {
        boolean info = false;

//	    	int result = PXInapp.getPaymentAskforSmsCode();

//	    	if(result == PXInapp.RESULT_YES){
//	    		info = true;
//	    	}
        //50% odds
        if (new Random().nextBoolean()) {
            info = true;
        }

        //info = true; // buque
        return info;
    }

    public int toEnterCode(final String code) {
        int result = 0;
        if (code.length() > 0)
//	    		result = PXInapp.checkPaymentCode(code);
            result = 1;


        if (result < 0) {
            ui_window.showMessagePixtelSucesfful(null, "Check the code " + code + " it is not valid");
        } else {
            ui_window.showMessagePixtelSucesfful(null, "Payment accepted©");
        }
        //result = 0; //buque
        return result;
    }

    @Override
    public void startEnterCode() {
        Intent intent = new Intent(app, EnterCodeActivity.class);
        Bundle b = new Bundle();
        b.putString("First Name", "First Name");
        intent.putExtras(b);
        //app.startActivity(intent);
        app.startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        System.out.println("in coming ");
        if (requestCode == PICK_CONTACT_REQUEST) {
            System.out.println("result 1 ");
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                System.out.println("result 2 ");

                if (data != null) {

                    Bundle bundle = data.getExtras();
                    System.out.println("Input Results " + bundle.getString("EditText"));
                    final String resul = bundle.getString("EditText");
                    toEnterCode(resul);
                }
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
    }

    @Override
    public void showMessagePIXTEL(String msg, int multiplier) {
        if (this.ui_window != null) {
            this.ui_window.hideBankWindow();
//			msg = msg.replace("&", "" + PXInapp.getInappProduct(utils.idPurchase).amount);
//			msg = msg.replace("%",  PXInapp.getInappProduct(utils.idPurchase).priceString);
            msg = msg.replace("&", "" + (1000 * multiplier));
            msg = msg.replace("%", "Free");
            ui_window.showMessagePixtel(null, msg);
        }
    }

    // Authorization

    @Override
    public void authorize(LoginType type) {
        // Nothing need to do
    }

    // Clear

    @Override
    public void clearVars() {

        //	this.ui_window = null;
        this.bank_loader = null;
    }

}
