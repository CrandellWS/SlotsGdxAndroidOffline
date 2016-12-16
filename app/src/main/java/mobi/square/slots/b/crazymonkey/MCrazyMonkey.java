package mobi.square.slots.b.crazymonkey;

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

public class MCrazyMonkey extends Machine {

    private static SymbolType[] HIGHLIGHTS = {SymbolType.BONUS};

    private final MessageBoard board;
    private final DrawableActor grass;
    private final MonkeyAnim monkey;

    private Sound spin_time_sound;

    protected MCrazyMonkey(Basic parent) {
        super(parent, SlotsType.CRAZY_MONKEY);
        super.animation = new AnimationInfo[]{
                new AnimationInfo(SymbolType.BONUS, "bonus_a", 4, .2f)
        };
        TextureAtlas animation_atlas = Connection.getManager().get("CrazyMonkey/CrazyMonkeyAni.pack", TextureAtlas.class);
        this.board = MessageBoard.newInstance(animation_atlas, false);
        this.grass = DrawableActor.newInstance(animation_atlas.findRegion("grass"));
        this.monkey = MonkeyAnim.newInstance(animation_atlas);
    }

    public static MCrazyMonkey newInstance(Basic parent) {
        MCrazyMonkey instance = new MCrazyMonkey(parent);
        instance.board.setBounds(862f, 380f, 150f, 70f);
        instance.grass.setBounds(835f, 95f, 189f, 105f);
        instance.monkey.setBounds(860f, 160f, 180f, 160f);
        instance.board.showBetInfo();
        return instance;
    }

    @Override
    public void initialize(SlotsType type) {
        super.initialize(type);
        this.spin_time_sound = Connection.getManager().get("CrazyMonkey/spin_time.ogg", Sound.class);
    }

    @Override
    protected SymbolType[] getHighlightedSymbols() {
        if (Connection.getInstance().getBonusGame() != BonusGame.MONKEY_ROPES) {
            return super.getHighlightedSymbols();
        } else return HIGHLIGHTS;
    }

    @Override
    protected void machineStopped() {
        if (super.lines != null && super.lines.getCount() > 0) {
            this.monkey.playYesAction();
        } else if (super.img_lines != null && super.img_lines.getCount() > 0) {
            this.monkey.playYesAction();
        }
        if (Connection.getInstance().isSoundOn() && this.spin_time_sound != null) {
            this.spin_time_sound.stop();
        }
        super.machineStopped();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        this.monkey.act(delta);
        this.board.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        this.board.draw(batch, parentAlpha);
        this.monkey.draw(batch, parentAlpha);
        this.grass.draw(batch, parentAlpha);
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
        return Connection.getManager().get("CrazyMonkey/reel_stop.ogg", Sound.class);
    }

    @Override
    protected Sound getMachineStartSound() {
        return null;
    }

    @Override
    protected Sound getStartSpinSound() {
        return null;
    }

    public void setHelmeted(boolean value) {
        this.monkey.setHelmeted(value);
    }

    public void playBonusAction() {
        this.monkey.playBonusAction();
    }

    private static class MonkeyAnim extends Actor {
        private static final float[] TIME = {.4f, .2f, .2f, .2f, .2f, .2f, .2f, .2f, .2f};
        private static final int[] RANDOMS = {1, 3, 4, 5};
        private final float[] bounds;
        private final TextureRegion[][] monkey;
        private final TextureRegion[][] helmet;
        private boolean helmeted;
        private int state;
        private int frame;
        private int loop;
        private float time;

        private MonkeyAnim(TextureAtlas atlas) {
            this.time = 0f;
            this.loop = 4;
            this.state = 0;
            this.frame = 0;
            this.helmeted = false;
            this.bounds = new float[4];
            this.monkey = new TextureRegion[][]{
                    Machine.loadTextures(atlas, "a001", 4, 1),
                    Machine.loadTextures(atlas, "a002", 2, 1),
                    Machine.loadTextures(atlas, "a003", 5, 1),
                    Machine.loadTextures(atlas, "a004", 4, 1),
                    Machine.loadTextures(atlas, "a005", 8, 1),
                    Machine.loadTextures(atlas, "a006", 6, 1),
                    Machine.loadTextures(atlas, "a007", 2, 1),
                    Machine.loadTextures(atlas, "a008", 2, 1),
                    Machine.loadTextures(atlas, "a009", 4, 1)
            };
            this.helmet = new TextureRegion[][]{
                    Machine.loadTextures(atlas, "c001", 4, 1),
                    Machine.loadTextures(atlas, "c002", 2, 1),
                    Machine.loadTextures(atlas, "c003", 5, 1),
                    Machine.loadTextures(atlas, "c004", 4, 1),
                    Machine.loadTextures(atlas, "c005", 8, 1),
                    Machine.loadTextures(atlas, "c006", 6, 1),
                    Machine.loadTextures(atlas, "c007", 2, 1),
                    Machine.loadTextures(atlas, "c008", 2, 1),
                    Machine.loadTextures(atlas, "c009", 4, 1)
            };
        }

        public static MonkeyAnim newInstance(TextureAtlas atlas) {
            MonkeyAnim instance = new MonkeyAnim(atlas);
            return instance;
        }

        @Override
        public void act(float delta) {
            this.time += delta;
            while (this.time >= TIME[this.state]) {
                this.time -= TIME[this.state];
                this.frame++;
                if (this.frame >= this.monkey[this.state].length) {
                    this.frame = 0;
                    switch (this.state) {
                        case 0:
                            this.loop--;
                            if (this.loop <= 0) {
                                this.state = RANDOMS[utils.getRandom(RANDOMS.length)];
                                this.loop = utils.getRandom(1, 2);
                            }
                            break;
                        case 1:
                            this.state = 2;
                            this.loop = utils.getRandom(2, 4);
                            break;
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 7:
                            this.loop();
                            break;
                        case 6:
                            this.state = 7;
                            this.loop = 4;
                            break;
                        case 8:
                            break;
                        default:
                            this.state = 0;
                            break;
                    }
                }
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.draw(
                    this.monkey[this.state][this.frame],
                    this.bounds[0], this.bounds[1],
                    this.bounds[2], this.bounds[3]
            );
            if (this.helmeted) {
                batch.draw(
                        this.helmet[this.state][this.frame],
                        this.bounds[0], this.bounds[1],
                        this.bounds[2], this.bounds[3]
                );
            }
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            this.bounds[0] = x;
            this.bounds[1] = y;
            this.bounds[2] = width;
            this.bounds[3] = height;
            super.setBounds(x, y, width, height);
        }

        public void setHelmeted(boolean value) {
            this.helmeted = value;
        }

        public void playYesAction() {
            this.time = 0f;
            this.frame = 0;
            this.state = 6;
        }

        public void playBonusAction() {
            this.time = 0f;
            this.frame = 0;
            this.state = 8;
        }

        private void loop() {
            this.loop--;
            if (this.loop <= 0) {
                this.state = 0;
                this.loop = utils.getRandom(3, 5);
            }
        }
    }

    public static class MessageBoard extends Group implements Resizable {
        private static final float BLINK_TIME = .5f;
        private final DrawableActor background;
        private final DrawableActor bonus;
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
            this.bet_text = new PixelLabel[]{
                    PixelLabel.newInstance(Connection.getString("rock_climber_bet_1"), "courbd.ttf", big ? 24 : 16, new Color(1f, 1f, .6f, 1f), Align.right),
                    PixelLabel.newInstance("1", "arialbd.ttf", big ? 30 : 20, new Color(1f, 1f, 0f, 1f), Align.center),
                    PixelLabel.newInstance(Connection.getString("rock_climber_bet_2"), "courbd.ttf", big ? 24 : 16, new Color(1f, 1f, .6f, 1f), Align.left),
                    PixelLabel.newInstance("225", "arialbd.ttf", big ? 30 : 20, new Color(1f, 1f, 0f, 1f), Align.center),
                    PixelLabel.newInstance(Connection.getString("rock_climber_bet_3"), "courbd.ttf", big ? 24 : 16, new Color(1f, 1f, .6f, 1f), Align.left),
                    PixelLabel.newInstance("1", "arialbd.ttf", big ? 30 : 20, new Color(.95f, .39f, .13f, 1f), Align.center),
                    PixelLabel.newInstance("225", "arialbd.ttf", big ? 30 : 20, new Color(.95f, .39f, .13f, 1f), Align.center)
            };
            for (int i = 0; i < this.bet_text.length; super.addActor(this.bet_text[i++])) ;
            this.risk_text = new PixelLabel[]{
                    PixelLabel.newInstance(Connection.getString("rock_climber_risk"), "courbd.ttf", big ? 24 : 16, new Color(1f, 1f, .6f, 1f), Align.center),
                    PixelLabel.newInstance(Connection.getString("rock_climber_risk"), "courbd.ttf", big ? 24 : 16, new Color(.95f, .39f, .13f, 1f), Align.center)
            };
            for (int i = 0; i < this.risk_text.length; super.addActor(this.risk_text[i++])) ;
            this.award_text = new PixelLabel[]{
                    PixelLabel.newInstance(Connection.getString("rock_climber_line"), "courbd.ttf", big ? 24 : 16, new Color(1f, 1f, .6f, 1f), Align.right),
                    PixelLabel.newInstance("0", "arialbd.ttf", big ? 30 : 20, new Color(1f, 1f, 0f, 1f), Align.center),
                    PixelLabel.newInstance("0", "arialbd.ttf", big ? 30 : 20, new Color(.95f, .39f, .13f, 1f), Align.center)
            };
            for (int i = 0; i < this.award_text.length; super.addActor(this.award_text[i++])) ;
            this.double_text = new PixelLabel[]{
                    PixelLabel.newInstance(Connection.getString("rock_climber_double"), "courbd.ttf", big ? 24 : 16, new Color(1f, 1f, .6f, 1f), Align.center),
                    PixelLabel.newInstance("0", "arialbd.ttf", big ? 30 : 20, new Color(1f, 1f, 0f, 1f), Align.center),
                    PixelLabel.newInstance("0", "arialbd.ttf", big ? 30 : 20, new Color(.95f, .39f, .13f, 1f), Align.center)
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
            this.bonus.setBounds(0f, 0f, width, height);
            this.bet_text[0].setBounds(.04f * width, .47f * height, .5f * width, .45f * height);
            this.bet_text[1].setBounds(.58f * width, .47f * height, .1f * width, .45f * height);
            this.bet_text[2].setBounds(.71f * width, .47f * height, .2f * width, .45f * height);
            this.bet_text[3].setBounds(.08f * width, .17f * height, .3f * width, .45f * height);
            this.bet_text[4].setBounds(.36f * width, .17f * height, .5f * width, .45f * height);
            this.bet_text[5].setBounds(.58f * width, .47f * height, .1f * width, .45f * height);
            this.bet_text[6].setBounds(.08f * width, .17f * height, .3f * width, .45f * height);
            this.risk_text[0].setBounds(0f, 0f, width, height);
            this.risk_text[1].setBounds(0f, 0f, width, height);
            this.award_text[0].setBounds(.08f * width, .47f * height, .68f * width, .45f * height);
            this.award_text[1].setBounds(.8f * width, .47f * height, .1f * width, .45f * height);
            this.award_text[2].setBounds(.12f * width, .17f * height, .8f * width, .45f * height);
            this.double_text[0].setBounds(.08f * width, .47f * height, .8f * width, .45f * height);
            this.double_text[1].setBounds(.08f * width, .17f * height, .8f * width, .45f * height);
            this.double_text[2].setBounds(.08f * width, .17f * height, .8f * width, .45f * height);
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
            for (int i = 0; i < this.bet_text.length; this.bet_text[i++].setVisible(false)) ;
            for (int i = 0; i < this.risk_text.length; this.risk_text[i++].setVisible(false)) ;
            for (int i = 0; i < this.award_text.length; this.award_text[i++].setVisible(false)) ;
            for (int i = 0; i < this.double_text.length; this.double_text[i++].setVisible(false)) ;
        }

        public void showRandomHint() {
            this.hideAll();
            if (utils.getRandomBoolean(.5f) && true) {
                this.mode = 2;
                this.bonus.setVisible(true);
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
