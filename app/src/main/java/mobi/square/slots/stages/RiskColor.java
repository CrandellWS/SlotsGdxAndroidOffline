package mobi.square.slots.stages;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import mobi.square.slots.api.Connection;
import mobi.square.slots.b.bookofra.SBookOfRa;
import mobi.square.slots.containers.CardInfo;
import mobi.square.slots.enums.CardColor;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.handlers.AsyncJsonHandler;
import mobi.square.slots.handlers.LoadingHandler;
import mobi.square.slots.logger.Log;
import mobi.square.slots.screens.SBasic;
import mobi.square.slots.screens.SSinglePlayer;
import mobi.square.slots.ui.RiskColorGroup;
import mobi.square.slots.ui.RiskColorGroup.RiskColorListener;

public class RiskColor extends Header {

    private RiskColorGroup risk;
    private final AsyncJsonHandler risk_handler = new AsyncJsonHandler() {
        @Override
        public void onCompleted(String json) {
            try {
                CardInfo card = Connection.getInstance().responseRisk(json);
                risk.showGameCard(card);
            } catch (StringCodeException e) {
                Log.log(e);
            }
        }
    };
    private boolean action_time;
    private final AsyncJsonHandler init_handler = new AsyncJsonHandler() {
        @Override
        public void onCompleted(String json) {
            action_time = false;
            try {
                CardInfo[] cards = Connection.getInstance().responseInitRisk(json);
                risk.setLastCards(cards);
            } catch (StringCodeException e) {
                Log.log(e);
            }
        }
    };
    private boolean game_over;
    private final RiskColorListener risk_listener = new RiskColorListener() {
        @Override
        public void gameCardOpened() {
            action_time = false;
            game_over = true;
            int award = Connection.getInstance().getAward();
            risk.setAward(award);
            if (award > 0) {
                risk.setTextRiskWin();
                risk.getLeftButton().setDisabled(false);
            } else {
                risk.setTextRiskLose();
                risk.getRightButton().setTextOver();
            }
        }

        @Override
        public void gameCardClosed() {
            risk.setColorButtonsDisabled(false);
            action_time = false;
            game_over = false;
        }
    };

    public RiskColor(SBasic parent) {
        super(parent);
        this.risk = null;
        this.action_time = false;
        this.game_over = false;
    }

    @Override
    public void load(LoadingHandler handler) {
        super.load(false);
        Texture background = Connection.getManager().get("atlas/RiskScreenBg.jpg", Texture.class);
        TextureAtlas cards_atlas = Connection.getManager().get("atlas/RiskCards.pack", TextureAtlas.class);
        TextureAtlas atlas = Connection.getManager().get("atlas/RiskScreen.pack", TextureAtlas.class);
        this.risk = RiskColorGroup.newInstance(background, cards_atlas, atlas);
        this.risk.getLeftButton().setDisabled(true);
        this.risk.setListener(this.risk_listener);
        this.risk.setTextRiskRules();
        super.addActor(this.risk);
        super.addActors();
        this.risk.getRedButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (action_time || game_over) return;
                action_time = true;
                risk.setColorButtonsDisabled(true);
                try {
                    Connection.getInstance().requestRisk(CardColor.RED, risk_handler);
                } catch (StringCodeException e) {
                    Log.log(e);
                }
            }
        });
        this.risk.getBlackButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (action_time || game_over) return;
                action_time = true;
                risk.setColorButtonsDisabled(true);
                try {
                    Connection.getInstance().requestRisk(CardColor.BLACK, risk_handler);
                } catch (StringCodeException e) {
                    Log.log(e);
                }
            }
        });
        this.risk.getLeftButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!action_time && game_over && Connection.getInstance().getAward() > 0) {
                    action_time = true;
                    risk.setTextRiskRules();
                    risk.closeGameCard();
                    try {
                        Connection.getInstance().requestInitRisk(init_handler);
                    } catch (StringCodeException e) {
                        Log.log(e);
                    }
                    risk.getLeftButton().setDisabled(true);
                }
            }
        });
        this.risk.getRightButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (action_time) return;
                if (Connection.getInstance().getAward() > 0) {
                    try {
                        Connection.getInstance().requestTakeAward();
                    } catch (StringCodeException e) {
                        Log.log(e);
                    }
                }
                try {
                    LoadingHandler handler = new LoadingHandler();
                    SlotsType type = Connection.getInstance().getSlotsType();
                    Connection.getInstance().requestInitSlots(type, handler);
                    parent_screen.parent.showLoading(type == SlotsType.BOOK_OF_RA ? new SBookOfRa() : new SSinglePlayer(type), handler);
                } catch (StringCodeException e) {
                    Log.log(e);
                }
            }
        });
        try {
            CardInfo[] cards = Connection.getInstance().responseInitRisk(handler.getJson());
            this.risk.setAward(Connection.getInstance().getAward());
            this.risk.setColorButtonsDisabled(false);
            this.risk.setLastCards(cards);
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
