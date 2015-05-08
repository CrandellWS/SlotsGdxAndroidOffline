package mobi.square.slots.stages;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import mobi.square.slots.api.AppWrapper.GetBankItems;
import mobi.square.slots.api.AndroidApi;
import mobi.square.slots.api.Connection;
import mobi.square.slots.app.Game;
import mobi.square.slots.b.bookofra.SBookOfRa;
import mobi.square.slots.b.crazymonkey.SCrazyMonkey;
import mobi.square.slots.b.rockclimber.SRockClimber;
import mobi.square.slots.config.ApiConfig;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.config.SoundList;
import mobi.square.slots.containers.BankInfo;
import mobi.square.slots.containers.SlotsInfo;
import mobi.square.slots.dl.FilesList;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.handlers.AsyncJsonHandler;
import mobi.square.slots.handlers.LoadingHandler;
import mobi.square.slots.logger.Log;
import mobi.square.slots.screens.SBasic;
import mobi.square.slots.screens.SSinglePlayer;
import mobi.square.slots.ui.BonusWindow;
import mobi.square.slots.ui.BonusWindow.BonusWindowListener;
import mobi.square.slots.ui.DailyBonusPanel;
import mobi.square.slots.ui.DailyBonusPanel.BonusPanelListener;
import mobi.square.slots.ui.DownloadDialog;
import mobi.square.slots.ui.DownloadDialog.DialogClosedListener;
import mobi.square.slots.ui.DrawableActor;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.LobbyScroll;
import mobi.square.slots.ui.LobbyScroll.LobbyConfig;
import mobi.square.slots.ui.MailWindow;
import mobi.square.slots.ui.RouletteWindow;
import mobi.square.slots.ui.RouletteWindow.RouletteWindowListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Lobby extends Header {

	private Sound sound_money;
	private DrawableActor background;
	private LobbyScroll scroll;
	private DailyBonusPanel bonus_panel;
	private RouletteWindow roulette_window;
	private BonusWindow bonus_window;
	private MailWindow mail_window;
	private DownloadDialog dl_dialog;
	private boolean roulette_requested;

	public Lobby(SBasic parent) {
		super(parent);
		this.sound_money = null;
		this.background = null;
		this.scroll = null;
		this.bonus_panel = null;
		this.roulette_window = null;
		this.bonus_window = null;
		this.dl_dialog = null;
		this.roulette_requested = false;
	}

	private final AsyncJsonHandler bonus_handler = new AsyncJsonHandler() {
		@Override
		public void onCompleted(String json) {
			try {
				Connection.getInstance().responseTakeBonus(json);
			} catch (StringCodeException e) {
				Log.log(e);
			}
			Lobby.this.changeUserMoney();
			Lobby.this.bonus_panel.setBonusTime(
				Connection.getInstance().getHourlyBonusTotal(),
				Connection.getInstance().getHourlyBonusTime(),
				Connection.getInstance().getSuperBonusCount()
			);
			if (Connection.getInstance().getSuperBonusCount() >= 5) {
				bonus_window.show();
			}
		}
	};

	private final AsyncJsonHandler bonus_box_handler = new AsyncJsonHandler() {
		@Override
		public void onCompleted(String json) {
			try {
				int value = Connection.getInstance().responseSuperBonus(json);
				bonus_window.openBox(value);
			} catch (StringCodeException e) {
				Log.log(e);
			}
			Lobby.this.changeUserMoney();
			Lobby.this.bonus_panel.setBonusTime(
				Connection.getInstance().getHourlyBonusTotal(),
				Connection.getInstance().getHourlyBonusTime(),
				Connection.getInstance().getSuperBonusCount()
			);
		}
	};

	private final AsyncJsonHandler roulette_handler = new AsyncJsonHandler() {
		@Override
		public void onCompleted(String json) {
			int value = 0;
			try {
				value = Connection.getInstance().responseSpinRoulette(json);
			} catch (StringCodeException e) {
				Log.log(e);
			}
			int time = Connection.getInstance().getRouletteTime();
			if (Connection.getInstance().getRouletteSpins() < 1) {
				bonus_panel.setRouletteTime(Connection.getInstance().getRouletteTotal(), time);
			} else bonus_panel.setRouletteTime(1, 0);
			int index = Connection.getInstance().getRoulettePosition();
			roulette_window.stopRoulette(
				index, value,
				Connection.getInstance().getRouletteTime(),
				Connection.getInstance().getRouletteBonusSpins()
			);
		}
	};

	private final GetBankItems roulette_price_handler = new GetBankItems() {
		@Override
		public void get_items(BankInfo[] items) {
			roulette_requested = false;
			BankInfo price = null;
			for (BankInfo info : items) {
				if (ApiConfig.RULETTE_SKU.equals(info.getId())) {
					price = info;
					break;
				}
			}
			if (price != null) {
				roulette_window.show(
					Connection.getInstance().getRouletteTime(),
					Connection.getInstance().getRouletteMultipler(),
					Connection.getInstance().getRouletteBonusSpins(),
					price.getPriceString(),
					price.getGold()
				);
			} else {
				roulette_window.show(
					Connection.getInstance().getRouletteTime(),
					Connection.getInstance().getRouletteMultipler(),
					Connection.getInstance().getRouletteBonusSpins(),
					null, 0
				);
			}
		}
	};

	private final AsyncJsonHandler get_roulette_handler = new AsyncJsonHandler() {
		@Override
		public void onCompleted(String json) {
			/*try {
				BankInfo info = Connection.getInstance().responseGetPurchase(json);
				Connection.getWrapper().getBankItems(Lobby.this, new BankInfo[] { info }, roulette_price_handler);
			} catch (StringCodeException e) {
				Log.log(e);
			}*/
			try {
				BankInfo[] items = Connection.getInstance().responseInitBank(json);
				Connection.getWrapper().getBankItems(Lobby.this, items, roulette_price_handler);
			} catch (StringCodeException e) {
				Log.log(e);
			}
		}
	};

	private final AsyncJsonHandler take_mail_handler = new AsyncJsonHandler() {
		@Override
		public void onCompleted(String json) {
			try {
				Connection.getInstance().responseTakeBonusMail(json);
				checkBonusMailWindow();
			} catch (StringCodeException e) {
				Log.log(e);
			}
			changeUserMoney();
		}
	};

	private class LobbyIconListener extends ClickListener {
		protected final SlotsType type;
		public LobbyIconListener(SlotsType type) {
			this.type = type;
		}
		@Override
		public void clicked(InputEvent event, float x, float y) {
			LoadingHandler handler = new LoadingHandler();
			SBasic screen = null;
			switch (this.type) {
				case BOOK_OF_RA:	screen = new SBookOfRa();				break;
				case RESIDENT:
				case GARAGE:
				case GLADIATORS:
				case FAIRYTALE:
				case UNDERWATER_LIFE:
				case MONEY_GAME:	screen = new SSinglePlayer(this.type);	break;
				case ROCKCLIMBER:	screen = new SRockClimber();			break;
				case CRAZY_MONKEY:	screen = new SCrazyMonkey();			break;
				default:													return;
			}
			Gdx.app.postRunnable(new LobbyIconRunnable(parent_screen.parent, this.type, screen, handler));
		}
	}

	public static class LobbyIconRunnable implements Runnable {
		protected final Game parent;
		protected final SlotsType type;
		protected final SBasic screen;
		protected final LoadingHandler handler;
		public LobbyIconRunnable(Game parent, SlotsType type, SBasic screen, LoadingHandler handler) {
			this.parent = parent;
			this.type = type;
			this.screen = screen;
			this.handler = handler;
		}
		@Override
		public void run() {
			try {
				Connection.getInstance().requestInitSlots(type, handler);
				this.parent.showLoading(screen, handler);
			} catch (StringCodeException e) {
				Log.log(e);
			}
		}
	}

	@Override
	public void load(LoadingHandler handler) {
		super.load(true);
		this.sound_money = Connection.getManager().get(SoundList.MONEY_COMMON, Sound.class);
		TextureAtlas atlas = Connection.getManager().get("atlas/LobbyBackground.pack", TextureAtlas.class);
		this.scroll = LobbyScroll.newInstance(atlas, this);
		this.background = DrawableActor.newInstance(atlas.findRegion("lobby_background"));
		this.background.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
		atlas = Connection.getManager().get("atlas/DailyBonus.pack", TextureAtlas.class);
		this.bonus_panel = DailyBonusPanel.newInstance(atlas);
		float bonus_width = this.bonus_panel.getPanelWidth();
		this.scroll.setBounds(bonus_width, 0f, AppConfig.VIEWPORT_WIDTH - bonus_width, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
		TextureAtlas windows_atlas = Connection.getManager().get("atlas/Windows.pack", TextureAtlas.class);
		this.roulette_window = RouletteWindow.newInstance(atlas, windows_atlas);
		this.roulette_window.setListener(new RouletteWindowListener() {
			@Override
			public void spinClicked() {
				if (Connection.getInstance().getRouletteSpins() > 0) {
					try {
						Connection.getInstance().requestSpinRoulette(roulette_handler);
					} catch (StringCodeException e) {
						Log.log(e);
					}
				} else{
					System.out.print("Creo que esta desbloqueand Jose3");
					//Connection.getWrapper().purchase(ApiConfig.RULETTE_SKU);
					//Connection.getWrapper().purchase("1");
				}
			}
			@Override
			public void rouletteStopped() {
				Lobby.this.changeUserMoney();
			}
		});
		this.bonus_window = BonusWindow.newInstance(atlas);
		this.bonus_window.setListener(new BonusWindowListener() {
			@Override
			public void boxClicked() {
				try {
					Connection.getInstance().requestSuperBonus(bonus_box_handler);
				} catch (StringCodeException e) {
					Log.log(e);
				}
			}
		});
		this.dl_dialog = DownloadDialog.newInstance(windows_atlas);
		this.mail_window = MailWindow.newInstance(windows_atlas);
		this.mail_window.setListener(new MailWindow.MailWindowListener() {
			@Override
			public void okButtonClicked() {
				try {
					Connection.getInstance().requestTakeBonusMail(Connection.getInstance().getBonusMailId(), take_mail_handler);
				} catch (StringCodeException e) {
					Log.log(e);
				}
			}
		});
		String atlas_name = Connection.getLocale().getLanguage().equals("ru") ? "atlas/LobbyIcons.pack" : "atlas/LobbyIconsEn.pack";
		atlas = Connection.getManager().get(atlas_name, TextureAtlas.class);
		List<LobbyConfig> config = new LinkedList<LobbyConfig>();
		try {
			List<SlotsInfo> info_list = Connection.getInstance().responseSlotsList(handler.getJson());
			for (final SlotsInfo info : info_list) {
				config.add(new LobbyConfig(
					info.getType(),
					new LobbyIconListener(info.getType()),
					FilesList.installed(info.getType())
				));
			}
		} catch (StringCodeException e) {
			Log.log(e);
			super.parent_screen.parent.showLoginScreen();
		}
		Collections.sort(config, new Comparator<LobbyConfig>() {
			@Override
			public int compare(LobbyConfig o1, LobbyConfig o2) {
				if (o1.available && !o2.available) {
					return -1;
				} else if (!o1.available && o2.available) {
					return 1;
				} else return 0;
			}
		});
		this.scroll.addButtons(atlas, config.toArray(new LobbyConfig[0]));
		this.bonus_panel.setBonusTime(Connection.getInstance().getHourlyBonusTotal(), Connection.getInstance().getHourlyBonusTime(), Connection.getInstance().getSuperBonusCount());
		if (Connection.getInstance().getRouletteSpins() < 1) {
			this.bonus_panel.setRouletteTime(Connection.getInstance().getRouletteTotal(), Connection.getInstance().getRouletteTime());
		} else this.bonus_panel.setRouletteTime(1, 0);
		this.bonus_panel.setListener(new BonusPanelListener() {
			@Override
			public void roulettePressed() {
				if (roulette_requested) return;
				if (AndroidApi.ONLINE) {
					roulette_requested = true;
					/*try {
						Connection.getInstance().requestGetPurchase(ApiConfig.RULETTE_SKU, get_roulette_handler);
					} catch (StringCodeException e) {
						Log.log(e);
					}*/
					try {
						Connection.getInstance().requestInitBank(get_roulette_handler);
					} catch (StringCodeException e) {
						Log.log(e);
					}
				} else {
					roulette_window.show(
						Connection.getInstance().getRouletteTime(),
						Connection.getInstance().getRouletteMultipler(),
						Connection.getInstance().getRouletteBonusSpins(),
						null, 0
					);
				}
			}
			@Override
			public void bonusPressed() {
				bonus_panel.disableBonusButton();
				try {
					Connection.getInstance().requestTakeBonus(bonus_handler);
				} catch (StringCodeException e) {
					Log.log(e);
				}
			}
		});
		this.checkBonusMailWindow();
		super.addActor(this.background);
		super.addActor(this.scroll);
		super.addActor(this.bonus_panel);
		super.addActors();
		super.addActor(this.bonus_window);
		super.addActor(this.roulette_window);
		super.addActor(this.mail_window);
		super.addActor(this.dl_dialog);
		super.updateInfo();
		
		Connection.getWrapper().validatePaymentOnResume();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		this.scroll.resize(width, height);
		this.bonus_panel.resize(width, height);
		this.roulette_window.resize(width, height);
		this.bonus_window.resize(width, height);
		this.mail_window.resize(width, height);
		this.dl_dialog.resize(width, height);
	}

	@Override
	public void updateUserMoney() {
		super.updateUserMoney();
		if (Connection.getInstance().getRouletteSpins() < 1) {
			this.bonus_panel.setRouletteTime(
				Connection.getInstance().getRouletteTotal(),
				Connection.getInstance().getRouletteTime()
			);
		} else this.bonus_panel.setRouletteTime(1, 0);
		if (this.roulette_window.isVisible()) {
			this.roulette_window.updateValues();
		}
	}

	@Override
	public void showMessage(String title, String message) {
		this.roulette_requested = false;
		super.showMessage(title, message);
	}

	public void showDownloadDialog(DialogClosedListener listener) {
		this.dl_dialog.setListener(listener);
		this.dl_dialog.show("lobby_dl_title", "lobby_dl_message");
	}

	private void checkBonusMailWindow() {
		if (Connection.getInstance().isBonusMailExists()) {
			this.mail_window.show(Connection.getInstance().getBonusMailMessage(), Connection.getInstance().getBonusMailMoney());
		} else this.mail_window.hide();
	}

	private void changeUserMoney() {
		if (Connection.getInstance().isSoundOn())
			sound_money.play();
		super.header.getMoneyLabel().changeMoney(Connection.getInstance().getMoney());
	}

}
