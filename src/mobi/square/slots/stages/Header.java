package mobi.square.slots.stages;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import mobi.square.slots.api.AndroidApi;
import mobi.square.slots.api.AppWrapper.GetBankItems;
import mobi.square.slots.api.Connection;
import mobi.square.slots.config.ApiConfig;
import mobi.square.slots.containers.BankInfo;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.handlers.AsyncJsonHandler;
import mobi.square.slots.handlers.LoadingHandler;
import mobi.square.slots.logger.Log;
import mobi.square.slots.screens.SBasic;
import mobi.square.slots.screens.SCup;
import mobi.square.slots.screens.SLobby;
import mobi.square.slots.ui.BankWindow;
import mobi.square.slots.ui.BankWindow.BankLoadingHandler;
import mobi.square.slots.ui.CupRules;
import mobi.square.slots.ui.CupTop;
import mobi.square.slots.ui.CupWindow;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.MessageWindow;
import mobi.square.slots.ui.SettingsWindow;
import mobi.square.slots.ui.SettingsWindow.SettingsChecked;
import mobi.square.slots.ui.TopWindow;
import mobi.square.slots.utils.utils;

public abstract class Header extends Basic {

	protected GameHeader header;
	protected SettingsWindow settings;
	protected TopWindow top_window;
	protected BankWindow bank_window;
	protected MessageWindow message_window;
	protected CupWindow cup_window;
	protected CupRules cup_rules;
	protected CupTop cup_top;
	protected final BankLoadingHandler bank_loading_handler;
	private int cup_window_visible;

	protected AsyncJsonHandler money_handler = new AsyncJsonHandler() {
		@Override
		public void onCompleted(String json) {
			try {
				Connection.getInstance().responseSetMoney(json);
			} catch (StringCodeException e) {
				Log.log(e);
			}
			header.getMoneyLabel().changeMoneyNoAnimation(Connection.getInstance().getMoney());
		}
	};

	protected AsyncJsonHandler bank_handler = new AsyncJsonHandler() {
		@Override
		public void onCompleted(String json) {
			try {
				BankInfo[] items = Connection.getInstance().responseInitBank(json);
				Connection.getWrapper().getBankItems(Header.this, items, bank_items_handler);
			} catch (StringCodeException e) {
				Log.log(e);
			}
		}
	};

	protected AsyncJsonHandler cup_handler = new AsyncJsonHandler() {
		@Override
		public void onCompleted(String json) {
			try {
				Connection.getInstance().responseCupTop(json);
			} catch (StringCodeException e) {
				Log.log(e);
			}
			switch (cup_window_visible) {
				case 1:
					showCupWindow();
					break;
				case 2:
					showCupRules();
					break;
				case 3:
					showCupTop();
					break;
				default:
					break;
			}
		}
	};

	protected AsyncJsonHandler cup_register_handler = new AsyncJsonHandler() {
		@Override
		public void onCompleted(String json) {
			try {
				Connection.getInstance().responseCupRegister(json);
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						showCupScreen();
					}
				});
			} catch (StringCodeException e) {
				Log.log(e);
			}
		}
	};

	protected GetBankItems bank_items_handler = new GetBankItems() {
		@Override
		public void get_items(BankInfo[] items) {
			List<BankInfo> list = new ArrayList<BankInfo>();
			if (items != null) {
				for (BankInfo info : items) {
					if (!ApiConfig.RULETTE_SKU.equals(info.getId())) {
						list.add(info);
					}
				}
			}
			bank_loading_handler.setItems(list.toArray(new BankInfo[0]));
		}
	};

	public Header(SBasic parent) {
		super(parent);
		this.header = null;
		this.settings = null;
		this.top_window = null;
		this.bank_window = null;
		this.message_window = null;
		this.cup_window = null;
		this.cup_rules = null;
		this.cup_top = null;
		this.bank_loading_handler = new BankLoadingHandler();
		this.cup_window_visible = 0;
	}

	public void load(boolean show_login) {
		Connection.setLastScreen(null);
		// Header
		TextureAtlas atlas = Connection.getManager().get("atlas/Header.pack", TextureAtlas.class);
		this.header = GameHeader.newInstance(atlas);
		
		// Settings window
		TextureAtlas windows_atlas = Connection.getManager().get("atlas/Windows.pack", TextureAtlas.class);
		this.settings = SettingsWindow.newInstance(this, windows_atlas);
		// Top window
		this.top_window = TopWindow.newInstance(atlas, windows_atlas);
		// Bank window
		this.bank_window = BankWindow.newInstance(atlas, windows_atlas);
		// Message window
		this.message_window = MessageWindow.newInstance(windows_atlas);
		// Cup window
		if (AndroidApi.ONLINE) {
			TextureAtlas cup_atlas = Connection.getManager().get("atlas/Cup.pack", TextureAtlas.class);
			this.cup_window = CupWindow.newInstance(cup_atlas);
			this.cup_window.setListener(new CupWindow.CupWindowListener() {
				@Override
				public void timerExpired() {
					if (cup_window.isVisible()) {
						cup_window_visible = 1;
					} else if (cup_rules.isVisible()) {
						cup_window_visible = 2;
					} else if (cup_top.isVisible()) {
						cup_window_visible = 3;
					} else cup_window_visible = 0;
					cup_window.hide();
					cup_rules.hide();
					cup_top.hide();
					try {
						Connection.getInstance().requestCupTop(cup_handler);
					} catch (StringCodeException e) {
						Log.log(e);
					}
				}
				@Override
				public void showTop() {
					cup_window.hide();
					showCupTop();
				}
				@Override
				public void showRules() {
					cup_window.hide();
					showCupRules();
				}
				@Override
				public void actionClicked() {
					cup_window.hide();
					if (Connection.getInstance().isCupActive()) {
						if (Connection.getInstance().isCupRegistered() &&
							Connection.getInstance().getCupSpinsRemain() > 0) {
							if (parent_screen instanceof SCup) {
								cup_window.hide();
							} else showCupScreen();
						} else if (Connection.getInstance().getCupPay() >
							Connection.getInstance().getMoney()) {
							cup_window.hide();
							showBankWindow();
						} else try {
							Connection.getInstance().requestCupRegister(cup_register_handler);
						} catch (StringCodeException e) {
							Log.log(e);
						}
					}
				}
			});
			this.cup_window.setCountdown(Connection.getInstance().getCupCountdown());
			this.cup_rules = CupRules.newInstance(cup_atlas, windows_atlas);
			this.cup_top = CupTop.newInstance(cup_atlas, windows_atlas);
		} else {
			this.cup_window = null;
			this.cup_rules = null;
			this.cup_top = null;
		}
		// Lobby button
		if (show_login) {
			if (AndroidApi.ONLINE) {
				this.header.getBackButton().setLoginText();
				this.header.getBackButton().addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						if (header.getBackButton().isDisabled()) return;
						parent_screen.parent.showLoginScreen();
					}
				});
			} else {
				this.header.getBackButton().setVisible(false);
				//this.header.getBackButton().setDisabled(true);
			}
		} else {
			this.header.getBackButton().setLobbyText();
			this.header.getBackButton().addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (header.getBackButton().isDisabled()) return;
					if (!isLobbyButtonMakeAction()) {
						LoadingHandler handler = new LoadingHandler();
						try {
							Connection.getInstance().requestSlotsList(handler);
							parent_screen.parent.showLoading(new SLobby(null), handler);
						} catch (StringCodeException e) {
							Log.log(e);
							parent_screen.parent.showLoginScreen();
						}
					}
				}
			});
		}
		// Cup button
		this.header.getCupButton().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//if (header.getCupButton().isDisabled()) return;
				if (cup_rules != null)
					cup_rules.hide();
				if (cup_top != null)
					cup_top.hide();
				showCupWindow();
			}
		});
		// Settings button
		this.header.getSettingsButton().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (header.getSettingsButton().isDisabled()) return;
				if (settings.isVisible()) {
					settings.hide();
				} else settings.show();
			}
		});
		// Top button
		this.header.getTopButton().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (header.getTopButton().isDisabled()) return;
				if (!top_window.isVisible()) {
					top_window.show();
					try {
						int type = top_window.getLastType();
						LoadingHandler handler = new LoadingHandler();
						Connection.getInstance().requestGetTop(type, handler);
						top_window.showList(handler, type);
					} catch (StringCodeException e) {
						Log.log(e);
					}
				} else top_window.hide();
			}
		});
		// Bank button
		this.header.getBankButton().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (header.getBankButton().isDisabled()) return;
				if(utils.gameUnlocked){
					showBankWindow();
				}
				/*try {
					Connection.getInstance().requestSetMoney(1000000, money_handler);
				} catch (StringCodeException e) {
					Log.log(e);
				}*/
			}
		});
		// Daily award button
		this.top_window.getHourlyLeadersButton().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				try {
					LoadingHandler handler = new LoadingHandler();
					Connection.getInstance().requestGetTop(1, handler);
					top_window.showList(handler, 1);
				} catch (StringCodeException e) {
					Log.log(e);
				}
			}
		});
		// Daily risk button
		this.top_window.getHourlyWinnersButton().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				try {
					LoadingHandler handler = new LoadingHandler();
					Connection.getInstance().requestGetTop(2, handler);
					top_window.showList(handler, 2);
				} catch (StringCodeException e) {
					Log.log(e);
				}
			}
		});
		// Weekly award button
		this.top_window.getDailyLeadersButton().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				try {
					LoadingHandler handler = new LoadingHandler();
					Connection.getInstance().requestGetTop(3, handler);
					top_window.showList(handler, 3);
				} catch (StringCodeException e) {
					Log.log(e);
				}
			}
		});
		// Weekly risk button
		this.top_window.getDailyWinnersButton().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				try {
					LoadingHandler handler = new LoadingHandler();
					Connection.getInstance().requestGetTop(4, handler);
					top_window.showList(handler, 4);
				} catch (StringCodeException e) {
					Log.log(e);
				}
			}
		});
		// Sound box
		this.settings.getSoundBox().setBoxChecked(Connection.getInstance().isSoundOn());
		this.settings.getSoundBox().setListener(new SettingsChecked() {
			@Override
			public void checked(boolean checked) {
				Connection.getInstance().setSoundOn(checked);
			}
		});
		// Notifications box
		this.settings.getNotifyBox().setBoxChecked(Connection.getInstance().isNotificationsOn());
		this.settings.getNotifyBox().setListener(new SettingsChecked() {
			@Override
			public void checked(boolean checked) {
				Connection.getInstance().setNotificationsOn(checked);
			}
		});
		
		Connection.getWrapper().updateHeader(Header.this);
	}

	
	protected boolean isLobbyButtonMakeAction() {
		return false;
	}

	public void showMessage(String title, String message) {
		this.message_window.show(title, message);
	}
	public void showMessagePixtel(String title, String message) {
		Connection.getWrapper().updateHeader(Header.this);
		this.message_window.showMSGPixtel(title, message);
	}
	
	public void showMessagePixtelSucesfful(String title, String message) {
		Connection.getWrapper().updateHeader(Header.this);
		this.message_window.showMSGPixtelSucess(title, message);
	}

	protected void addLowActors() {
		super.addActor(this.settings);
	}

	protected void addHighActors() {
		if (this.cup_window != null)
			super.addActor(this.cup_window);
		if (this.cup_rules != null)
			super.addActor(this.cup_rules);
		if (this.cup_top != null)
			super.addActor(this.cup_top);
		super.addActor(this.top_window);
		super.addActor(this.header);
		super.addActor(this.bank_window);
		super.addActor(this.message_window);
	}

	protected void addActors() {
		if (this.cup_window != null)
			super.addActor(this.cup_window);
		if (this.cup_rules != null)
			super.addActor(this.cup_rules);
		if (this.cup_top != null)
			super.addActor(this.cup_top);
		super.addActor(this.top_window);
		super.addActor(this.settings);
		super.addActor(this.header);
		super.addActor(this.bank_window);
		super.addActor(this.message_window);
	}

	protected void updateInfo() {
		this.settings.setUserId(Connection.getInstance().getUserId());
		this.header.getMoneyLabel().setMoney(Connection.getInstance().getMoney());
		if (Connection.getInstance().isCupActive()) {
			this.header.setCupTimer(Connection.getInstance().getCupEnds(), true);
		} else {
			this.header.setCupTimer(Connection.getInstance().getCupStarts(), false);
		}
	}

	protected void showCupWindow() {
		if (this.cup_window == null) return;
		this.cup_window.show(
			Connection.getInstance().getCupCountdown(),
			Connection.getInstance().isCupActive(),
			Connection.getInstance().isCupRegistered(),
			Connection.getInstance().getCupSpinsRemain(),
			Connection.getInstance().getCupPay(),
			Connection.getInstance().getMoney()
		);
	}

	protected void showCupRules() {
		if (cup_rules == null) return;
		this.cup_rules.show(
			Connection.getInstance().getCupCountdown(),
			Connection.getInstance().isCupActive(),
			Connection.getInstance().getCupPay(),
			Connection.getInstance().getCupSpins(),
			Connection.getInstance().getCupAward()
		);
	}

	protected void showCupTop() {
		if (cup_top == null) return;
		this.cup_top.show(
			Connection.getInstance().getCupAwards(),
			Connection.getInstance().getCupLeaders()
		);
	}

	protected void showBankWindow() {
		try {
			Connection.getInstance().requestInitBank(this.bank_handler);
		} catch (StringCodeException e) {
			Log.log(e);
		}
		this.bank_window.show(this.bank_loading_handler);
	}
	
	public void hideBankWindow() {
		/*try {
			Connection.getInstance().requestInitBank(this.bank_handler);
		} catch (StringCodeException e) {
			Log.log(e);
		}*/
		
		if(this.bank_window != null){
			this.bank_window.hide();
		}
	}

	protected void showCupScreen() {
		try {
			LoadingHandler handler = new LoadingHandler();
			SlotsType type = Connection.getInstance().getCupType();
			Connection.getInstance().requestCupInit(handler, type);
			parent_screen.parent.showLoading(new SCup(type), handler);
		} catch (StringCodeException e) {
			Log.log(e);
		}
	}

	public void updateUserMoney() {
		this.header.getMoneyLabel().changeMoney(Connection.getInstance().getMoney());
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		if (this.cup_window != null)
			this.cup_window.resize(width, height);
		if (this.cup_rules != null)
			this.cup_rules.resize(width, height);
		if (this.cup_top != null)
			this.cup_top.resize(width, height);
		this.header.resize(width, height);
		this.settings.resize(width, height);
		this.top_window.resize(width, height);
		this.bank_window.resize(width, height);
		this.message_window.resize(width, height);
	}

}
