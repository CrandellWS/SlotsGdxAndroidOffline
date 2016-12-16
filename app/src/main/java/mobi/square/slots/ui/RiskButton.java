package mobi.square.slots.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.tools.FontsFactory;

public class RiskButton extends Button implements Resizable {

    private static final float DISABLING_TIME = 1f;

    private final PixelLabel risk_label;
    private final AnimationLayer[] animation;

    private final Matrix4 pixel_matrix;
    private final Matrix4 backup_matrix;

    private float animation_aplha;
    private boolean disabling_in_progress;

    private RiskButton(TextureAtlas atlas, ButtonStyle style) {
        super(style);
        this.pixel_matrix = new Matrix4();
        this.backup_matrix = new Matrix4();
        LabelStyle ls = new LabelStyle();
        ls.fontColor = Color.WHITE;
        ls.font = FontsFactory.getAsync("Junegull.ttf", 36);
        this.risk_label = new PixelLabel(Connection.getString("footer_button_risk"), ls);
        this.risk_label.setAlignment(Align.center, Align.center);
        super.addActor(this.risk_label);
        this.animation = new AnimationLayer[]{
                new AnimationLayer(new TextureRegionDrawable(atlas.findRegion("button_risk_star", 1)), 0f, 45f),
                new AnimationLayer(new TextureRegionDrawable(atlas.findRegion("button_risk_star", 2)), 0f, -45f),
                new AnimationLayer(new TextureRegionDrawable(atlas.findRegion("button_risk_star", 3)), 0f, -45f),
                new AnimationLayer(new TextureRegionDrawable(atlas.findRegion("button_risk_star", 4)), 0f, -45f)
        };
        this.disabling_in_progress = false;
        this.animation_aplha = 0f;
        super.setDisabled(true);
    }

    public static RiskButton newInstance(TextureAtlas atlas) {
        ButtonStyle style = new ButtonStyle();
        style.up = new TextureRegionDrawable(atlas.findRegion("button_risk_active"));
        style.down = new TextureRegionDrawable(atlas.findRegion("button_risk_pressed"));
        style.disabled = new TextureRegionDrawable(atlas.findRegion("button_risk_inactive"));
        style.pressedOffsetY = -2f;
        RiskButton instance = new RiskButton(atlas, style);
        return instance;
    }

    @Override
    public void resize(int width, int height) {
        if (this.risk_label != null) {
            this.risk_label.setBounds(0f, super.getHeight() * .07f, super.getWidth(), super.getHeight() * .93f);
            this.risk_label.resize(width, height);
        }
        if (this.pixel_matrix != null) {
            this.pixel_matrix.setToOrtho2D(0, 0, width, height);
        }
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void setDisabled(boolean isDisabled) {
        if (isDisabled != super.isDisabled()) {
            this.disabling_in_progress = true;
        }
        super.setDisabled(isDisabled);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for (AnimationLayer layer : this.animation) {
            layer.angle += delta * layer.speed;
            while (layer.angle < 360f) {
                layer.angle += 360f;
            }
            while (layer.angle > 360f) {
                layer.angle -= 360f;
            }
        }
        if (this.disabling_in_progress) {
            if (super.isDisabled()) {
                this.animation_aplha -= delta / DISABLING_TIME;
                if (this.animation_aplha <= 0f) {
                    this.animation_aplha = 0f;
                    this.disabling_in_progress = false;
                }
            } else {
                this.animation_aplha += delta / DISABLING_TIME;
                if (this.animation_aplha >= 1f) {
                    this.animation_aplha = 1f;
                    this.disabling_in_progress = false;
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (super.isDisabled() && !this.disabling_in_progress) return;
        batch.setColor(1f, 1f, 1f, this.animation_aplha);
        float x_aspect = (float) Gdx.graphics.getWidth() / (float) AppConfig.VIEWPORT_WIDTH;
        float y_aspect = (float) Gdx.graphics.getHeight() / (float) AppConfig.VIEWPORT_HEIGHT;
        float x = super.getX() * x_aspect;
        float y = super.getY() * y_aspect;
        float width = super.getWidth() * x_aspect;
        float height = super.getHeight() * y_aspect;
        this.backup_matrix.set(batch.getProjectionMatrix());
        batch.setProjectionMatrix(this.pixel_matrix);
        for (int i = this.animation.length - 1; i >= 0; --i)
            this.animation[i].texture.draw(batch, x, y, width / 2f, height / 2f, width, height, 1f, 1f, this.animation[i].angle);
        batch.setProjectionMatrix(this.backup_matrix);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private class AnimationLayer {
        final TextureRegionDrawable texture;
        final float speed;
        float angle;

        public AnimationLayer(TextureRegionDrawable texture, float angle, float speed) {
            this.texture = texture;
            this.angle = angle;
            this.speed = speed;
        }
    }

}
