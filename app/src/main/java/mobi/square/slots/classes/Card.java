package mobi.square.slots.classes;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.SoundList;
import mobi.square.slots.enums.CardColor;
import mobi.square.slots.enums.CardRank;
import mobi.square.slots.enums.CardSuit;

public class Card extends Actor {

    private static final float FLIP_SPEED = 4f;

    private final TextureRegionDrawable background;
    private final Sound flip_sound;
    private TextureRegionDrawable foreground;
    private boolean opened;
    private boolean flip_time;
    private float flip_state;
    private CardOpenedListener open_listener;
    private CardFlippedListener flip_listener;

    private Card(TextureAtlas atlas) {
        this.background = new TextureRegionDrawable(atlas.findRegion("card_back"));
        this.foreground = null;
        this.opened = false;
        this.open_listener = null;
        this.flip_listener = null;
        this.flip_sound = Connection.getManager().get(SoundList.FLIP_CARD, Sound.class);
    }

    public static Card newInstance(TextureAtlas atlas) {
        Card instance = new Card(atlas);
        return instance;
    }

    public void setCard(TextureAtlas atlas, CardSuit suit, CardRank rank, CardColor color) {
        if (atlas == null) return;
        StringBuilder builder = new StringBuilder();
        builder.append("card_");
        builder.append(rank.toLowerString());
        builder.append("_");
        if (rank == CardRank.JOKER) {
            builder.append(color.toLowerString());
        } else {
            builder.append(suit.toLowerString());
        }
        AtlasRegion region = atlas.findRegion(builder.toString());
        if (region == null) return;
        this.foreground = new TextureRegionDrawable(region);
    }

    @Override
    public void act(float delta) {
        if (this.flip_time) {
            float speed = this.opened ? -1f * FLIP_SPEED : FLIP_SPEED;
            this.flip_state += speed * delta;
            if (this.opened) {
                if (this.flip_state <= -1f) {
                    this.flip_state = -1f;
                    this.flip_time = false;
                    this.opened = false;
                    this.riseCardFlippedEvent();
                }
            } else {
                if (this.flip_state >= 1f) {
                    this.flip_state = 1f;
                    this.flip_time = false;
                    this.opened = true;
                    this.riseCardFlippedEvent();
                    this.riseCardOpenedEvent();
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = super.getX();
        float y = super.getY();
        float width = super.getWidth();
        float height = super.getHeight();
        if (this.flip_time) {
            if (this.flip_state > 0f) {
                if (this.foreground != null) {
                    float current = this.flip_state * width;
                    float offset = (width - current) / 2f;
                    this.foreground.draw(batch, x + offset, y, current, height);
                }
            } else if (this.flip_state < 0f) {
                float current = -this.flip_state * width;
                float offset = (width - current) / 2f;
                this.background.draw(batch, x + offset, y, current, height);
            }
        } else {
            if (this.opened) {
                if (this.foreground != null) {
                    this.foreground.draw(batch, x, y, width, height);
                }
            } else {
                this.background.draw(batch, x, y, width, height);
            }
        }
    }

    public void flip() {
        if (this.flip_time) return;
        if (this.flip_sound != null && Connection.getInstance().isSoundOn()) {
            this.flip_sound.play();
        }
        this.flip_state = this.opened ? 1f : -1f;
        this.flip_time = true;
    }

    public void open() {
        if (this.opened) return;
        this.flip();
    }

    public void close() {
        if (!this.opened) return;
        this.flip();
    }

    public boolean isOpened() {
        return this.opened;
    }

    public void setOpened(boolean opened) {
        this.flip_time = false;
        this.opened = opened;
    }

    public void setCardFlippedListener(CardFlippedListener listener) {
        this.flip_listener = listener;
    }

    public void setCardOpenedListener(CardOpenedListener listener) {
        this.open_listener = listener;
    }

    private void riseCardFlippedEvent() {
        if (this.flip_listener != null) {
            this.flip_listener.flipped();
        }
    }

    private void riseCardOpenedEvent() {
        if (this.open_listener != null) {
            this.open_listener.opened();
        }
    }

    public interface CardOpenedListener {
        void opened();
    }

    public interface CardFlippedListener {
        void flipped();
    }

}
