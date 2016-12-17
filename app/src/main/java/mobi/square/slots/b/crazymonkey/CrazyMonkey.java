package mobi.square.slots.b.crazymonkey;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import mobi.square.slots.api.Connection;
import mobi.square.slots.classes.Machine;
import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.handlers.LoadingHandler;
import mobi.square.slots.logger.Log;
import mobi.square.slots.screens.SBasic;
import mobi.square.slots.stages.SinglePlayer;
import mobi.square.slots.ui.payout.TBasic;

public class CrazyMonkey extends SinglePlayer {

    private Sound sound_bonus_game;

    public CrazyMonkey(SBasic parent) {
        super(parent, SlotsType.CRAZY_MONKEY);
    }

    @Override
    public void load(LoadingHandler handler) {
        super.load(handler);
        this.sound_bonus_game = Connection.getManager().get("CrazyMonkey/bonus_game.ogg", Sound.class);
    }

    @Override
    protected Machine getMachineInstance() {
        return MCrazyMonkey.newInstance(this);
    }

    @Override
    protected TBasic getPayTableInstance() {
        Texture pay_background = Connection.getManager().get(this.machine.config.pay_table_bg, Texture.class);
        TextureAtlas pay_atlas = Connection.getManager().get(this.machine.config.pay_table, TextureAtlas.class);
        TextureAtlas windows_atlas = Connection.getManager().get("atlas/Windows.pack", TextureAtlas.class);
        return TCrazyMonkey.newInstance(pay_background, pay_atlas, this.machine.atlas, windows_atlas);
    }

    @Override
    protected void showRiskScreen() {
        try {
            LoadingHandler handler = new LoadingHandler();
            Connection.getInstance().requestInitRiskGreater(handler);
            parent_screen.parent.showLoading(new SRiskMonkey(), handler);
        } catch (StringCodeException e) {
            Log.log(e);
        }
    }

    @Override
    protected boolean updateBonusGame() {
        if (Connection.getInstance().getBonusGame() == BonusGame.MONKEY_ROPES) {
            MCrazyMonkey.class.cast(super.machine).playBonusAction();
            if (Connection.getInstance().isSoundOn() && this.sound_bonus_game != null) {
                this.sound_bonus_game.play();
                super.showBonusGameScreen(new SCrazyMonkeyRopes(), 9f);
            } else super.showBonusGameScreen(new SCrazyMonkeyRopes(), 3f);
            return true;
        } else return false;
    }

    @Override
    protected void betChanged() {
        MCrazyMonkey.class.cast(super.machine).setHelmeted(Connection.getInstance().isMaxBet());
    }

    @Override
    protected Sound getChangeBetSound() {
        return Connection.getManager().get("CrazyMonkey/change_bet.ogg", Sound.class);
    }

    @Override
    protected Sound getChangeLinesSound() {
        return Connection.getManager().get("CrazyMonkey/change_lines.ogg", Sound.class);
    }

}
