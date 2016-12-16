package mobi.square.slots.b.rockclimber;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import mobi.square.slots.api.Connection;
import mobi.square.slots.containers.CardInfo;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.handlers.AsyncJsonHandler;
import mobi.square.slots.handlers.LoadingHandler;
import mobi.square.slots.logger.Log;
import mobi.square.slots.screens.SBasic;
import mobi.square.slots.stages.Header;
import mobi.square.slots.ui.RiskGreaterGroup.RiskGreaterListener;

public class RiskClimber extends Header {

	private RiskClimberGroup risk;
	private boolean action_time;
	private boolean game_over;
	private int choice_index;

	private CardInfo game_card;
	private boolean game_card_closed;

	private final AsyncJsonHandler risk_handler = new AsyncJsonHandler() {
		@Override
		public void onCompleted(String json) {
			try {
				CardInfo[] cards = Connection.getInstance().responseRiskGreater(json);
				risk.setChoiceCards(cards);
				risk.showChoiceCard(choice_index, Connection.getInstance().getAward() > 0);
			} catch (StringCodeException e) {
				Log.log(e);
			}
		}
	};

	private final AsyncJsonHandler init_handler = new AsyncJsonHandler() {
		@Override
		public void onCompleted(String json) {
			try {
				game_card = Connection.getInstance().responseInitGreater(json);
				if (game_card_closed) {
					risk.showGameCard(game_card);
					action_time = true;
				} else {
					action_time = false;
				}
				risk.setAward(Connection.getInstance().getAward());
				game_over = false;
			} catch (StringCodeException e) {
				Log.log(e);
			}
		}
	};

	private final RiskGreaterListener risk_listener = new RiskGreaterListener() {
		@Override
		public void choiceCardsOpened() {
			action_time = false;
			game_over = true;
		}
		@Override
		public void choiceCardOpened() {
			risk.showChoiceCards();
			int award = Connection.getInstance().getAward();
			if (award > 0) {
				risk.getButton().setTextRepeat();
			} else {
				risk.getButton().setTextLeave();
			}
			risk.setAward(award);
		}
		@Override
		public void choiceCardClicked(int index) {
			if (action_time) return;
			if (!game_over) {
				action_time = true;
				choice_index = index;
				risk.pickCard(index);
				try {
					Connection.getInstance().requestRiskGreater(index, risk_handler);
				} catch (StringCodeException e) {
					Log.log(e);
				}
			}
		}
		@Override
		public void gameCardOpened() {
			action_time = false;
			game_over = false;
		}
		@Override
		public void gameCardClosed() {
			if (game_card != null) {
				action_time = true;
				risk.showGameCard(game_card);
			}
		}
	};

	public RiskClimber(SBasic parent) {
		super(parent);
		this.risk = null;
		this.action_time = false;
		this.game_over = false;
		this.choice_index = 0;
		this.game_card = null;
		this.game_card_closed = false;
	}

	@Override
	public void load(LoadingHandler handler) {
		super.load(false);
		Texture background = Connection.getManager().get("RockClimber/RockClimberRiskBg.jpg", Texture.class);
		TextureAtlas cards_atlas = Connection.getManager().get("RockClimber/RockClimberCards.pack", TextureAtlas.class);
		TextureAtlas ani_atlas = Connection.getManager().get("RockClimber/RockClimberAni.pack", TextureAtlas.class);
		TextureAtlas atlas = Connection.getManager().get("RockClimber/RockClimberRisk.pack", TextureAtlas.class);
		this.risk = RiskClimberGroup.newInstance(background, atlas, cards_atlas, ani_atlas);
		this.risk.setListener(this.risk_listener);
		super.addActor(this.risk);
		super.addActors();
		this.risk.getButton().addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (risk.getButton().isDisabled() || action_time) return;
				if (game_over) {
					if (Connection.getInstance().getAward() > 0) {
						action_time = true;
						risk.closeGameCard();
						risk.closeChoiceCards();
						risk.getButton().setTextTake();
						game_card = null;
						try {
							Connection.getInstance().requestInitRiskGreater(init_handler);
						} catch (StringCodeException e) {
							Log.log(e);
						}
						game_card_closed = false;
					} else {
						try {
							LoadingHandler handler = new LoadingHandler();
							Connection.getInstance().requestInitSlots(SlotsType.ROCKCLIMBER, handler);
							parent_screen.parent.showLoading(new SRockClimber(), handler);
						} catch (StringCodeException e) {
							Log.log(e);
						}
					}
				} else {
					if (Connection.getInstance().getAward() > 0) {
						try {
							Connection.getInstance().requestTakeAward();
						} catch (StringCodeException e) {
							Log.log(e);
						}
					}
					try {
						LoadingHandler handler = new LoadingHandler();
						Connection.getInstance().requestInitSlots(SlotsType.ROCKCLIMBER, handler);
						parent_screen.parent.showLoading(new SRockClimber(), handler);
					} catch (StringCodeException e) {
						Log.log(e);
					}
				}
			};
		});
		try {
			CardInfo card = Connection.getInstance().responseInitGreater(handler.getJson());
			this.risk.showGameCard(card);
			this.risk.setAward(Connection.getInstance().getAward());
			this.action_time = true;
		} catch (StringCodeException e) {
			Log.log(e);
		}
		super.updateInfo();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		this.risk.resize(width, height);
	}

}
