package mobi.square.slots.b.rockclimber;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import mobi.square.slots.api.Connection;
import mobi.square.slots.classes.Machine;
import mobi.square.slots.enums.BonusGame;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.enums.SymbolType;
import mobi.square.slots.stages.Basic;
import mobi.square.slots.ui.DrawableActor;
import mobi.square.slots.ui.PixelLabel;
import mobi.square.slots.ui.Resizable;
import mobi.square.slots.utils.utils;

public class MRockClimber extends Machine {

    private static int TENT_PAUSE = 15;
    private static SymbolType[] HIGHLIGHTS = {SymbolType.BONUS};
    private static float[][] BOUNDS = {
            {.08f, .65f, .07f, .15f, .2f},
            {.025f, .3f, .09f, .36f, .1f},
            {-.082f, .68f, .22f, .3f, .2f}
    };
    private final float[] time;
    private final int[] frame;
    private MessageBoard board;
    private TextureAtlas animation_atlas;
    private TextureRegion[][] animation_tex;
    private ClimberMan climber;
    private int pause;

    private Sound spin_time_sound;

    protected MRockClimber(Basic parent) {
        super(parent, SlotsType.ROCKCLIMBER);
        super.animation = new AnimationInfo[]{
                new AnimationInfo(SymbolType.BONUS, "bonus_a", 9, .12f),
                new AnimationInfo(SymbolType.N07, "n07_a", 8, .2f),
                new AnimationInfo(SymbolType.WILD, "wild_a", 9, .2f)
        };
        this.animation_atlas = Connection.getManager().get("RockClimber/RockClimberAni.pack", TextureAtlas.class);
        this.animation_tex = new TextureRegion[][]{
                Machine.loadTextures(this.animation_atlas, "bag", 1, 0),
                Machine.loadTextures(this.animation_atlas, "flame", 14, 0),
                Machine.loadTextures(this.animation_atlas, "tent", 9, 0)
        };
        this.climber = ClimberMan.newInstance(this.animation_atlas);
        this.board = MessageBoard.newInstance(this.animation_atlas, false);
        this.time = new float[3];
        this.frame = new int[3];
        this.pause = TENT_PAUSE;
    }

    public static MRockClimber newInstance(Basic parent) {
        MRockClimber instance = new MRockClimber(parent);
        instance.climber.setBounds(904f, 240f, 118f, 114f);
        instance.board.setBounds(897f, 386f, 130f, 85f);
        instance.board.showBetInfo();
        return instance;
    }

    @Override
    public void initialize(SlotsType type) {
        super.initialize(type);
        this.spin_time_sound = Connection.getManager().get("RockClimber/spin_time.ogg", Sound.class);
    }

    @Override
    protected SymbolType[] getHighlightedSymbols() {
        if (Connection.getInstance().getBonusGame() != BonusGame.ROCKCLIMBER_CAVES) {
            return super.getHighlightedSymbols();
        } else return HIGHLIGHTS;
    }

    @Override
    protected void machineStopped() {
        if (super.lines != null && super.lines.getCount() > 0) {
            this.climber.playYesAction();
        } else if (super.img_lines != null && super.img_lines.getCount() > 0) {
            this.climber.playYesAction();
        }
        if (Connection.getInstance().isSoundOn() && this.spin_time_sound != null) {
            this.spin_time_sound.stop();
        }
        super.machineStopped();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        this.climber.act(delta);
        this.board.act(delta);
        // Static objects
        for (int i = 0; i < 3; i++) {
            this.time[i] += delta;
            while (this.time[i] >= BOUNDS[i][4]) {
                this.time[i] -= BOUNDS[i][4];
                if (i == 2 && this.frame[i] == 0) {
                    if (this.pause >= 0) {
                        this.pause--;
                        continue;
                    } else this.pause = TENT_PAUSE;
                }
                this.frame[i]++;
                if (this.frame[i] >= this.animation_tex[i].length) {
                    this.frame[i] = 0;
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float x = super.getX();
        float y = super.getY();
        float w = super.getWidth();
        float h = super.getHeight();
        for (int i = 2; i >= 0; i--) {
            batch.draw(this.animation_tex[i][this.frame[i]], BOUNDS[i][0] * w + x, BOUNDS[i][1] * h + y, BOUNDS[i][2] * w, BOUNDS[i][3] * h);
        }
        this.climber.draw(batch, parentAlpha);
        this.board.draw(batch, parentAlpha);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.board.resize(width, height);
    }

    @Override
    public void start() {
        if (super.started) return;
        this.board.showRandomHint();
        super.start();
        if (Connection.getInstance().isSoundOn() && this.spin_time_sound != null) {
            this.spin_time_sound.loop();
        }
    }

    @Override
    protected void lineShowed(int line, int award) {
        if (!super.isAwardLinesShowed())
            this.board.showLineAward(line, award);
        super.lineShowed(line, award);
    }

    @Override
    protected void linesShowed() {
        if (Connection.getInstance().getAward() > 0) {
            this.board.showRiskInfo();
        } else this.board.showBetInfo();
        super.linesShowed();
    }

    @Override
    protected Sound getReelStopSound() {
        return Connection.getManager().get("RockClimber/reel_stop.ogg", Sound.class);
    }

    @Override
    protected Sound getMachineStartSound() {
        return null;
    }

    @Override
    protected Sound getStartSpinSound() {
        return null;
    }

    public static class ClimberMan extends Actor {
        private static final float[] FRAMES_TIME = {.6f, .25f, .2f, .25f, .3f, .16f};
        private TextureRegion[][] animation_tex;
        private float time;
        private int frame;
        private int cur_anim;
        private int breathe_total;
        private int breathe_current;

        private ClimberMan(TextureAtlas atlas) {
            this.animation_tex = new TextureRegion[][]{
                    Machine.loadTextures(atlas, "breathe", 2, 0),
                    Machine.loadTextures(atlas, "drink", 12, 0),
                    Machine.loadTextures(atlas, "glasses", 13, 0),
                    Machine.loadTextures(atlas, "look_left", 8, 0),
                    Machine.loadTextures(atlas, "look_right", 6, 0),
                    Machine.loadTextures(atlas, "yes", 10, 0)
            };
            this.time = 0f;
            this.frame = 0;
            this.cur_anim = 0;
            this.breathe_total = 4;
            this.breathe_current = 0;
        }

        public static ClimberMan newInstance(TextureAtlas atlas) {
            ClimberMan instance = new ClimberMan(atlas);
            return instance;
        }

        @Override
        public void act(float delta) {
            this.time += delta;
            if (this.time >= FRAMES_TIME[this.cur_anim]) {
                this.time -= FRAMES_TIME[this.cur_anim];
                this.frame++;
                if (this.frame >= this.animation_tex[this.cur_anim].length) {
                    this.frame = 0;
                    if (this.cur_anim != 0) {
                        this.cur_anim = 0;
                        this.breathe_current = 0;
                        this.breathe_total = utils.getRandom(2, 5);
                    } else {
                        this.breathe_current++;
                        if (this.breathe_current >= this.breathe_total) {
                            this.cur_anim = utils.getRandom(1, 4);
                        }
                    }
                }
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            float x = super.getX();
            float y = super.getY();
            float w = super.getWidth();
            float h = super.getHeight();
            batch.draw(this.animation_tex[this.cur_anim][this.frame], x, y, w, h);
        }

        public void playYesAction() {
            this.time = 0f;
            this.frame = 0;
            this.cur_anim = 5;
        }
    }

    public static class MessageBoard extends Group implements Resizable {
        private static final float BLINK_TIME = .5f;
        private final DrawableActor background;
        private final DrawableActor bonus;
        private final DrawableActor wild;
        private final PixelLabel[] bet_text;
        private final PixelLabel[] risk_text;
        private final PixelLabel[] award_text;
        private final PixelLabel[] double_text;
        private float time;
        private int state;
        private int mode;

        private MessageBoard(TextureAtlas atlas, boolean big) {
            this.mode = 0;
            this.state = 0;
            this.time = 0f;
            this.background = DrawableActor.newInstance(atlas.findRegion("board_background"));
            super.addActor(this.background);
            this.bonus = DrawableActor.newInstance(atlas.findRegion("board_bonus_".concat(Connection.getTextureLanguage())));
            super.addActor(this.bonus);
            this.wild = DrawableActor.newInstance(atlas.findRegion("board_wild_".concat(Connection.getTextureLanguage())));
            super.addActor(this.wild);
            this.bet_text = new PixelLabel[]{
                    PixelLabel.newInstance(Connection.getString("rock_climber_bet_1"), "courbd.ttf", big ? 16 : 12, new Color(1f, 1f, .6f, 1f), Align.right),
                    PixelLabel.newInstance("1", "arialbd.ttf", big ? 20 : 16, new Color(1f, 1f, 0f, 1f), Align.center),
                    PixelLabel.newInstance(Connection.getString("rock_climber_bet_2"), "courbd.ttf", big ? 16 : 12, new Color(1f, 1f, .6f, 1f), Align.left),
                    PixelLabel.newInstance("225", "arialbd.ttf", big ? 20 : 16, new Color(1f, 1f, 0f, 1f), Align.center),
                    PixelLabel.newInstance(Connection.getString("rock_climber_bet_3"), "courbd.ttf", big ? 16 : 12, new Color(1f, 1f, .6f, 1f), Align.left),
                    PixelLabel.newInstance("1", "arialbd.ttf", big ? 20 : 16, new Color(1f, 1f, 1f, 1f), Align.center),
                    PixelLabel.newInstance("225", "arialbd.ttf", big ? 20 : 16, new Color(1f, 1f, 1f, 1f), Align.center)
            };
            for (int i = 0; i < this.bet_text.length; super.addActor(this.bet_text[i++])) ;
            this.risk_text = new PixelLabel[]{
                    PixelLabel.newInstance(Connection.getString("rock_climber_risk"), "courbd.ttf", big ? 16 : 12, new Color(1f, 1f, .6f, 1f), Align.center),
                    PixelLabel.newInstance(Connection.getString("rock_climber_risk"), "courbd.ttf", big ? 16 : 12, new Color(1f, 1f, 1f, 1f), Align.center)
            };
            for (int i = 0; i < this.risk_text.length; super.addActor(this.risk_text[i++])) ;
            this.award_text = new PixelLabel[]{
                    PixelLabel.newInstance(Connection.getString("rock_climber_line"), "courbd.ttf", big ? 16 : 12, new Color(1f, 1f, .6f, 1f), Align.right),
                    PixelLabel.newInstance("0", "arialbd.ttf", big ? 20 : 16, new Color(1f, 1f, 0f, 1f), Align.center),
                    PixelLabel.newInstance("0", "arialbd.ttf", big ? 20 : 16, new Color(1f, 1f, 0f, 1f), Align.center)
            };
            for (int i = 0; i < this.award_text.length; super.addActor(this.award_text[i++])) ;
            this.double_text = new PixelLabel[]{
                    PixelLabel.newInstance(Connection.getString("rock_climber_double"), "courbd.ttf", big ? 16 : 12, new Color(1f, 1f, .6f, 1f), Align.center),
                    PixelLabel.newInstance("0", "arialbd.ttf", big ? 20 : 16, new Color(1f, 1f, 0f, 1f), Align.center),
                    PixelLabel.newInstance("0", "arialbd.ttf", big ? 20 : 16, new Color(1f, 1f, 1f, 1f), Align.center)
            };
            for (int i = 0; i < this.double_text.length; super.addActor(this.double_text[i++])) ;
        }

        public static MessageBoard newInstance(TextureAtlas atlas, boolean big) {
            MessageBoard instance = new MessageBoard(atlas, big);
            instance.hideAll();
            return instance;
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.background.setBounds(0f, 0f, width, height);
            this.wild.setBounds(.06f * width, .46f * height, .82f * width, .45f * height);
            this.bonus.setBounds(.06f * width, .46f * height, .82f * width, .45f * height);
            this.bet_text[0].setBounds(.04f * width, .56f * height, .5f * width, .45f * height);
            this.bet_text[1].setBounds(.58f * width, .56f * height, .1f * width, .45f * height);
            this.bet_text[2].setBounds(.71f * width, .56f * height, .2f * width, .45f * height);
            this.bet_text[3].setBounds(.08f * width, .36f * height, .3f * width, .45f * height);
            this.bet_text[4].setBounds(.36f * width, .36f * height, .5f * width, .45f * height);
            this.bet_text[5].setBounds(.58f * width, .56f * height, .1f * width, .45f * height);
            this.bet_text[6].setBounds(.08f * width, .36f * height, .3f * width, .45f * height);
            this.risk_text[0].setBounds(.06f * width, .46f * height, .82f * width, .45f * height);
            this.risk_text[1].setBounds(.06f * width, .46f * height, .82f * width, .45f * height);
            this.award_text[0].setBounds(.04f * width, .56f * height, .68f * width, .45f * height);
            this.award_text[1].setBounds(.76f * width, .56f * height, .1f * width, .45f * height);
            this.award_text[2].setBounds(.08f * width, .36f * height, .8f * width, .45f * height);
            this.double_text[0].setBounds(.08f * width, .56f * height, .8f * width, .45f * height);
            this.double_text[1].setBounds(.08f * width, .36f * height, .8f * width, .45f * height);
            this.double_text[2].setBounds(.08f * width, .36f * height, .8f * width, .45f * height);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (this.mode == 1) {
                this.time += delta;
                while (this.time >= BLINK_TIME) {
                    this.time -= BLINK_TIME;
                    this.state = this.state == 0 ? 1 : 0;
                    if (this.state == 0) {
                        this.bet_text[1].setVisible(true);
                        this.bet_text[3].setVisible(true);
                        this.bet_text[5].setVisible(false);
                        this.bet_text[6].setVisible(false);
                    } else {
                        this.bet_text[1].setVisible(false);
                        this.bet_text[3].setVisible(false);
                        this.bet_text[5].setVisible(true);
                        this.bet_text[6].setVisible(true);
                    }
                }
            } else if (this.mode == 4) {
                this.time += delta;
                while (this.time >= BLINK_TIME) {
                    this.time -= BLINK_TIME;
                    this.state = this.state == 0 ? 1 : 0;
                    if (this.state == 0) {
                        this.risk_text[0].setVisible(true);
                        this.risk_text[1].setVisible(false);
                    } else {
                        this.risk_text[0].setVisible(false);
                        this.risk_text[1].setVisible(true);
                    }
                }
            } else if (this.mode == 6) {
                this.time += delta;
                while (this.time >= BLINK_TIME) {
                    this.time -= BLINK_TIME;
                    this.state = this.state == 0 ? 1 : 0;
                    if (this.state == 0) {
                        this.double_text[1].setVisible(true);
                        this.double_text[2].setVisible(false);
                    } else {
                        this.double_text[1].setVisible(false);
                        this.double_text[2].setVisible(true);
                    }
                }
            }
        }

        @Override
        public void resize(int width, int height) {
            for (int i = 0; i < this.bet_text.length; this.bet_text[i++].resize(width, height)) ;
            for (int i = 0; i < this.risk_text.length; this.risk_text[i++].resize(width, height)) ;
            for (int i = 0; i < this.award_text.length; this.award_text[i++].resize(width, height))
                ;
            for (int i = 0; i < this.double_text.length; this.double_text[i++].resize(width, height))
                ;
        }

        private void hideAll() {
            this.bonus.setVisible(false);
            this.wild.setVisible(false);
            for (int i = 0; i < this.bet_text.length; this.bet_text[i++].setVisible(false)) ;
            for (int i = 0; i < this.risk_text.length; this.risk_text[i++].setVisible(false)) ;
            for (int i = 0; i < this.award_text.length; this.award_text[i++].setVisible(false)) ;
            for (int i = 0; i < this.double_text.length; this.double_text[i++].setVisible(false)) ;
        }

        public void showRandomHint() {
            this.hideAll();
            if (utils.getRandomBoolean(.5f) && true) {
                this.mode = 2;
                this.wild.setVisible(true);
            } else {
                this.mode = 3;
                this.bonus.setVisible(true);
            }
        }

        public void showBetInfo() {
            this.hideAll();
            this.mode = 1;
            this.state = 0;
            this.time = 0f;
            for (int i = 0; i < 5; this.bet_text[i++].setVisible(true)) ;
        }

        public void showRiskInfo() {
            this.hideAll();
            this.mode = 4;
            this.state = 0;
            this.time = 0f;
            this.risk_text[0].setVisible(true);
        }

        public void showLineAward(int line, int award) {
            this.hideAll();
            this.mode = 5;
            this.award_text[1].setText(String.valueOf(line));
            this.award_text[2].setText(String.valueOf(award));
            for (int i = 0; i < this.award_text.length; this.award_text[i++].setVisible(true)) ;
        }

        public void showDoubleTo(int award) {
            this.hideAll();
            this.state = 0;
            this.time = 0f;
            if (award > 0) {
                this.mode = 6;
                this.double_text[1].setText(String.valueOf(award));
                this.double_text[2].setText(String.valueOf(award));
                for (int i = 0; i < 2; this.double_text[i++].setVisible(true)) ;
            } else this.mode = 0;
        }
    }

}
