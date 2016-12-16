package mobi.square.slots.app;

import com.badlogic.gdx.Gdx;

import mobi.square.slots.api.Connection;
import mobi.square.slots.b.bookofra.SBookOfRa;
import mobi.square.slots.b.crazymonkey.SCrazyMonkey;
import mobi.square.slots.b.rockclimber.SRockClimber;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.handlers.LoadingHandler;
import mobi.square.slots.logger.Log;
import mobi.square.slots.screens.SBasic;
import mobi.square.slots.screens.SLoading;
import mobi.square.slots.screens.SLobby;
import mobi.square.slots.screens.SLogin;
import mobi.square.slots.screens.SSinglePlayer;
import mobi.square.slots.stages.Lobby.LobbyIconRunnable;
import mobi.square.slots.tools.AtlasLoader;

public class Game extends com.badlogic.gdx.Game {

    private SBasic screen = null;

    //private static int inst = 0;

    public Game() {
        super();
        //inst++;
        //System.out.println("Create instance " + inst);
    }

    @Override
    public void create() {
        //System.out.println("Game::create() " + inst);
        if (screen != null) {
            super.setScreen(screen);
        } else if (Connection.getLastScreen() != null) {
            this.showSlotsScreen(Connection.getLastScreen());
        } else this.showLoginScreenAuth();
    }

    public void showLoading(SBasic screen_, LoadingHandler handler) {
        if (screen_ != null) {
            //System.out.println("Game::showLoading(" + screen.getClass().getName() + ")");
            screen_.parent = this;
            screen = new SLoading(this, screen, screen_, handler);
            super.setScreen(screen);
        } else {
            Log.log("Game::showLoading(SBasic, LoadingHandler) first parameter is null");
        }
    }

    public void changeScreen(SBasic screen_) {
        if (screen_ != null) {
            //System.out.println("Game::changeScreen(" + screen.getClass().getName() + ")");
            screen = screen_;
            super.setScreen(screen_);
            if (!(screen_ instanceof SLoading)) {
                AtlasLoader.dispose(AppConfig.LOADING_SCREEN_ATLAS);
            }
        } else {
            Log.log("Game::changeScreen(SBasic) parameter is null");
        }
    }

    public void showLoginScreen() {
        //System.out.println("showLoginScreen()");
        screen = new SLogin(this);
        super.setScreen(screen);
    }

    private void showSlotsScreen(SlotsType type) {
        LoadingHandler handler = new LoadingHandler();
        SBasic screen = null;
        switch (type) {
            case BOOK_OF_RA:
                screen = new SBookOfRa();
                break;
            case ROCKCLIMBER:
                screen = new SRockClimber();
                break;
            case CRAZY_MONKEY:
                screen = new SCrazyMonkey();
                break;
            case RESIDENT:
            case GARAGE:
            case GLADIATORS:
            case FAIRYTALE:
            case UNDERWATER_LIFE:
            case MONEY_GAME:
                screen = new SSinglePlayer(type);
                break;
            default:
                this.showLoginScreenAuth();
                return;
        }
        Gdx.app.postRunnable(new LobbyIconRunnable(this, type, screen, handler));
    }

    public void showLoginScreenAuth() {
        //System.out.println("showLoginScreenAuth() " + inst);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (Connection.getInstance().loadDeviceId()) {
                    try {
                        LoadingHandler handler = new LoadingHandler();
                        Connection.getInstance().requestSlotsList(handler);
                        Game.this.showLoading(new SLobby(), handler);
                    } catch (StringCodeException e) {
                        Log.log(e);
                    }
                } else Game.this.showLoginScreen();
            }
        });
    }

}
