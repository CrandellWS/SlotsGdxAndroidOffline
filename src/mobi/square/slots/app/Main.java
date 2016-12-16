package mobi.square.slots.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import mobi.square.slots.stages.SinglePlayer;
import mobi.square.slots.tools.AtlasLoader;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.utils.utils;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import fr.pixtel.pxinapp.PXInapp;
import fr.pixtel.pxinapp.PXInappProduct;

public class Main extends AndroidApplication implements AppWrapper, PXInapp.PaymentCallback {

	// Game
	private static Game instance = null;
	private static Main app = null;
	static boolean gameLoaded = false;
	private static int backupResult = -9999;
	private static PXInappProduct backupAppProduct = null;

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
				}}
			);
			return;
		}
		
		app = this;
		// PX API
		PXInapp.create( this, "A024005128395134104154590460172200536995B607AA", false );
		//PXInapp.create(this, "A02498732917338113443204513861373695228A09407FE", false);old
		PXInapp.setPaymentCallback(this);
		
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
	
	 private final static int	SPIN_UNLOCK		  	= 1;
	 private final static int	BET_UNLOCK		  	= 2;
	 
	 private void loadInfoPixtel(){
		// System.out.println("info pixtel es " + utils.maxSpinToLock + " bet es " + utils.maxBetToLock);
		 
		PXInappProduct item = PXInapp.getInappProduct(SPIN_UNLOCK);
		utils.maxSpinToLock = item.amount;
		
		item = PXInapp.getInappProduct(BET_UNLOCK);
		utils.maxBetToLock = item.amount;
		
		// System.out.println("info pixtel es4 " + utils.maxSpinToLock + " bet es " + utils.maxBetToLock);
		 
		if(utils.loadData("slotSoftgamesData", this) != null){
			StringTokenizer tokens = new StringTokenizer(utils.loadData("slotSoftgamesData", this), "|");
			utils.numberSpinDone = Integer.parseInt(tokens.nextToken());
			utils.accumalatedBet = Integer.parseInt(tokens.nextToken());
			utils.gameUnlocked = Boolean.parseBoolean(tokens.nextToken());
		}
		
		 if(utils.maxBetToLock == 0 && utils.maxSpinToLock == 0){
			 utils.gameUnlocked = true;
		 }
	}

	@Override
	protected void onStart() {
		Connection.setWrapper(this);
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		PXInapp.pause();
	}

	@Override
	protected void onResume() {
		Connection.setWrapper(this);
		super.onResume();
		PXInapp.resume();
	}

	@Override
	protected void onDestroy() {
		FontsFactory.dispose();
		AtlasLoader.disposeAll();
		Connection.dispose();
		super.onDestroy();
		PXInapp.destroy();
	}

	// Device ID

	@Override
	public String getDeviceId() {
		TelephonyManager tm = (TelephonyManager)super.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
	    String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(super.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    return deviceUuid.toString();
	}

	// Settings

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

	// PX API

	private Header ui_window = null;
	private GetBankItems bank_loader = null;

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
		System.out.println("id ui_window updateHeader "+ ui_window + " parent " + this.ui_window.parent_screen );
	}
	
	@Override
	public void getBankItems(Header window, BankInfo[] items, GetBankItems handler) {
		this.ui_window = window;
		System.out.println("id ui_window getBank "+ ui_window + " parent " + this.ui_window.parent_screen );
		
		
		this.bank_loader = handler;
		
		List<CPurchase> list =  new ArrayList<CPurchase>();
		for (BankInfo item : items) {
			int id = Integer.parseInt(item.getId());
			
			
			PXInappProduct p = PXInapp.getInappProduct(id);
			if (p != null) {
				CPurchase unit = new CPurchase();
				
				//System.out.println("id item es2 "+ id + " precio " + p.priceString);
				item.setDiscount(p.discountRate);
				item.setPriceString(p.priceString);
				item.setGold(p.amount);
				
				unit.setType("" + id);
				unit.setAmount(0);
				unit.setGold(p.amount);
				unit.setBonusGold(0);
				unit.setDiscount(0);
				unit.setRecommended(false);
				list.add(unit);
			} else item.setPriceString("error");
		}
		
		Bank.setGoldList(list);
		
		if (bank_loader != null) {
			bank_loader.get_items(items);
			bank_loader = null;
		}
	}

	@Override
	 public  void gotoCGV()
		{
			//System.out.println("deberia abrir esto");
			Uri 	uri;
			Intent 	browser;
			String 	url = PXInapp.getUrl("CGV"); 
			if (url==null)
				return;
			
			browser 	= new Intent(Intent.ACTION_VIEW);
			uri 		= Uri.parse(url);
			browser.setData(uri);
			getContext().startActivity(browser);
		}
	
	 @Override
	   public  boolean checkShowButton()
	    {
	    	boolean info = false;
	    	
	    	int result = PXInapp.getPaymentAskforSmsCode();
	    	
	    	if(result == PXInapp.RESULT_YES){
	    		info = true;
	    	}
	    	
	    	//info = true; // buque
	    	return info;
	    }
	
	 public  int toEnterCode(final String code)
		{
	    	int result = 0;
	    	if (code.length() > 0) 
	    		result = PXInapp.checkPaymentCode(code);
	    	
	    	
	    	if(result < 0){
	    		ui_window.showMessagePixtelSucesfful(null, "Attention le code "+ code +" n`est pas valide");
	    	} else{
	    		ui_window.showMessagePixtelSucesfful(null, "Paiement accept√©");
	    	}
	    	//result = 0; //buque
	    	return result;
		}
	    
	    private static final int PICK_CONTACT_REQUEST = 1;
	    @Override
	    public  void startEnterCode(){
			Intent intent=new Intent(app, EnterCodeActivity.class); 
			Bundle b = new Bundle(); 
			b.putString("NOMBRE", "NOMBRE");
			intent.putExtras(b);
			//app.startActivity(intent);
			app.startActivityForResult(intent, PICK_CONTACT_REQUEST);
		}
	    
	    
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        // Check which request we're responding to
	    	System.out.println("llega ");
	        if (requestCode == PICK_CONTACT_REQUEST) {
	        	System.out.println("resulta 1 ");
	            // Make sure the request was successful
	            if (resultCode == RESULT_OK) {
	            	System.out.println("resulta 2 ");
	            	
	            	if(data !=null){
	            		
	            		Bundle bundle = data.getExtras();
		            		System.out.println("resultado es " + bundle.getString("EditText"));
		            		final String resul =  bundle.getString("EditText");
		            		toEnterCode(resul);
	            	}
	                // The user picked a contact.
	                // The Intent's data Uri identifies which contact was selected.

	                // Do something with the contact here (bigger example below)
	            }
	        }
	    }
	
	@Override
	public void showMessagePIXTEL(String msg) {
		if(this.ui_window != null){
			this.ui_window.hideBankWindow();
			msg = msg.replace("&", "" + PXInapp.getInappProduct(utils.idPurchase).amount);
			msg = msg.replace("%",  PXInapp.getInappProduct(utils.idPurchase).priceString);
			ui_window.showMessagePixtel(null, msg);
		}
	}
	
	@Override
	public void purchase(String id) {
		PXInapp.clearPayment(Integer.parseInt(id));	
		int result = PXInapp.initiatePayment(Integer.parseInt(id));
		if (result < 0) {
			switch (result) {
				case PXInapp.RESULT_PAYMENT_IN_PROGRESS:
					ui_window.showMessage("title_error", "px_payment_in_progress");
					break;
				case PXInapp.RESULT_ALREADY_PURCHASED:
					ui_window.showMessage("title_error", "px_already_purchased");
					break;
				case PXInapp.RESULT_ERROR_UNINITIALIZED_LIBRARY:
					ui_window.showMessage("title_error", "px_uninitialized_library");
					break;
				case PXInapp.RESULT_ERROR_BAD_INAPPPRODUCT:
					ui_window.showMessage("title_error", "px_bad_inappproduct");
					break;
				case PXInapp.RESULT_FAILED:
					ui_window.showMessage("title_error", "px_purchase_failed");
					break;
				default:
					break;
			}
		}
	}

	@Override
	public void validatePaymentOnResume(){
		gameLoaded = true;
	     	//backupResult = 0;//buque
		    //backupAppProduct = new PXInappProduct();//buque
		    //backupAppProduct.id = 1;//buque
//3043996-
	    if(backupResult != -9999){
	    	System.out.println("Load again onPayment : ");
	    	onPayment(backupAppProduct, backupResult);
	    }
	}
	
	@Override
	public void onPayment(PXInappProduct product, int result) {
	
		if(!gameLoaded){
      		backupResult = result;
      		backupAppProduct = product;
      		System.out.println("Entro onPaymen y no cargo todo: " + product.id);
      		return;
      	}
		
		result = 0;//buque
		if (result < 0) {
			switch (result) {
				case PXInapp.PAYMENT_OFFER_NOT_AVAILABLE:
					ui_window.showMessage("title_error", "px_offer_not_available");
					break;
				case PXInapp.PAYMENT_INSUFFICIENT_CREDIT:
					ui_window.showMessage("title_error", "px_insufficient_credit");
					break;
				case PXInapp.PAYMENT_TIMEOUT:
				case PXInapp.PAYMENT_ERROR:
					ui_window.showMessage("title_error", "px_payment_error");
					break;
				default:
					break;
			}
		} else {
			
			 if(product.id != SPIN_UNLOCK && product.id != BET_UNLOCK){
				 try {
					 Connection.getInstance().requestAddGold(billing_handler, String.valueOf(product.id), String.valueOf(System.currentTimeMillis()));
				 } catch (StringCodeException e) {
					 Log.log(e);
				 }
				// ui_window.updateUserMoney();// lo puse para probar si cambia ..probemos
				// Connection.getInstance().isCanSpin();
				 if(SinglePlayer.footer != null){ // mirar the bet y el money para validar los botones
					 SinglePlayer.footer.disableSpinButton(false);
					 Connection.getInstance().setCanSpin(true);
				 }
			 } else {
				utils.gameUnlocked = true; 
				saveDataPixtel();
			 }
			ui_window.showMessagePixtelSucesfful(null, "Merci. Votre paiement a √©t√© accept√©");
		}
	}

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

	// Authorization

	@Override
	public void authorize(LoginType type) {
		// Nothing need to do
	};

	// Clear

	@Override
	public void clearVars() {
		
	//	this.ui_window = null;
		this.bank_loader = null;
	}

}
