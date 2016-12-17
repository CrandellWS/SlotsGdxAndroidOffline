package mobi.square.slots.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import mobi.square.slots.api.Connection;
import mobi.square.slots.tools.FontsFactory;

public class CupRules extends CupBigWindow {

    private final CupWindow.TimerFrame timer_frame;
    private final CupWindow.BlackFrame spins_frame;
    private final CupWindow.PriceFrame price_frame;
    private final DrawableActor timer_line;
    private final PrizeFrame prize_frame;
    private final PixelLabel price_label;
    private final PixelLabel spins_label;
    private final PixelLabel timer_label;
    private final PixelLabel rules_label;

    private CupRules(TextureAtlas atlas, TextureAtlas windows_atlas) {
        super(atlas, windows_atlas, Connection.getString("cup_rules_title"));
        { // Labels
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 30);
            ls.fontColor = Color.WHITE;
            this.price_label = new PixelLabel(Connection.getString("cup_price"), ls);
            super.addActor(this.price_label);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = Color.WHITE;
            this.spins_label = new PixelLabel(Connection.getString("cup_spins_n"), ls);
            super.addActor(this.spins_label);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 30);
            ls.fontColor = new Color(.47f, .89f, .91f, 1f);
            this.timer_label = new PixelLabel(Connection.getString("cup_end"), ls);
            super.addActor(this.timer_label);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Taurus.ttf", 24);
            ls.fontColor = Color.WHITE;
            this.rules_label = new PixelLabel(Connection.getDefaultI18N().format("cup_rules", Integer.valueOf(50)), ls);
            this.rules_label.setAlignment(Align.top | Align.left);
            this.rules_label.setWrap(true);
            super.addActor(this.rules_label);

        }
        this.timer_frame = CupWindow.TimerFrame.newInstance(atlas);
        this.timer_frame.setListener(new CupWindow.TimerFrame.CupTimerListener() {
            @Override
            public void timerExpired() {
                // TODO Auto-generated method stub
            }
        });
        super.addActor(this.timer_frame);
        this.timer_line = DrawableActor.newInstance(atlas.findRegion("rules_line"));
        super.addActor(this.timer_line);
        this.price_frame = CupWindow.PriceFrame.newInstance(atlas);
        super.addActor(this.price_frame);
        this.spins_frame = CupWindow.BlackFrame.newInstance(atlas);
        super.addActor(this.spins_frame);
        this.prize_frame = PrizeFrame.newInstance(atlas);
        super.addActor(this.prize_frame);
    }

    public static CupRules newInstance(TextureAtlas atlas, TextureAtlas windows_atlas) {
        CupRules instance = new CupRules(atlas, windows_atlas);
        instance.initialize();
        instance.timer_line.setBounds(445f, 375f, 200f, 30f);
        instance.timer_frame.setBounds(660f, 360f, 330f, 60f);
        instance.price_frame.setBounds(860f, 280f, 130f, 50f);
        instance.spins_frame.setBounds(840f, 200f, 150f, 50f);
        instance.prize_frame.setBounds(657f, 30f, 336f, 140f);
        instance.price_label.setBounds(665f, 280f, 195f, 50f);
        instance.spins_label.setBounds(665f, 200f, 195f, 50f);
        instance.timer_label.setBounds(30f, 360f, 600f, 60f);
        instance.rules_label.setBounds(30f, 20f, 600f, 320f);
        return instance;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.timer_frame.resize(width, height);
        this.spins_frame.resize(width, height);
        this.price_frame.resize(width, height);
        this.prize_frame.resize(width, height);
        this.price_label.resize(width, height);
        this.spins_label.resize(width, height);
        this.timer_label.resize(width, height);
        this.rules_label.resize(width, height);
    }

    public void show(int countdown, boolean active, int price, int spins, int prize) {
        if (active) {
            this.timer_label.setText(Connection.getString("cup_end"));
        } else this.timer_label.setText(Connection.getString("cup_begin"));
        this.timer_frame.setCountdown(countdown);
        this.price_frame.setValue(price);
        this.spins_frame.setValue(spins);
        this.prize_frame.setValue(prize);
        super.show();
    }

    private static class PrizeFrame extends Group implements Resizable {
        private final DrawableActor background;
        private final DrawableActor icon;
        private final PixelLabel title_label;
        private final PixelLabel value_label;

        private PrizeFrame(TextureAtlas atlas) {
            super();
            this.background = DrawableActor.newInstance(atlas.findRegion("rules_prize_frame"));
            super.addActor(this.background);
            this.icon = DrawableActor.newInstance(atlas.findRegion("chips_icon"));
            super.addActor(this.icon);
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = Color.WHITE;
            this.title_label = new PixelLabel(Connection.getString("cup_prize"), ls);
            this.title_label.setAlignment(Align.center);
            super.addActor(this.title_label);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 30);
            ls.fontColor = Color.GREEN;
            this.value_label = new PixelLabel("", ls);
            this.value_label.setAlignment(Align.center);
            super.addActor(this.value_label);
        }

        public static PrizeFrame newInstance(TextureAtlas atlas) {
            PrizeFrame instance = new PrizeFrame(atlas);
            return instance;
        }

        @Override
        public void resize(int width, int height) {
            this.title_label.resize(width, height);
            this.value_label.resize(width, height);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.background.setBounds(0f, 0f, width, height);
            this.title_label.setBounds(0f, .57f * height, width, .4f * height);
            this.value_label.setBounds(.1f * width, .1f * height, .9f * width, .6f * height);
            this.icon.setBounds(.02f * width, .18f * height, .15f * width, .4f * height);
        }

        public void setValue(int value) {
            this.value_label.setText(String.valueOf(value));
        }
    }

}
