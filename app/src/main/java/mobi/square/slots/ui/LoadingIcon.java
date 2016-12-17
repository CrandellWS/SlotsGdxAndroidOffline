package mobi.square.slots.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import mobi.square.slots.config.AppConfig;

public class LoadingIcon extends Actor {

    private final TextureRegionDrawable outer_texture;
    private final TextureRegionDrawable inner_texture;

    private final Matrix4 pixel_matrix;
    private final Matrix4 back_matrix;

    private final float OUTER_CIRCLE_SPEED = 90f;
    private final float INNER_CIRCLE_SPEED = 360f;
    private final float[] bounds;
    private float outer_circle_angle;
    private float inner_circle_angle;

    private LoadingIcon(TextureAtlas atlas) {
        this.back_matrix = new Matrix4();
        this.pixel_matrix = new Matrix4();
        this.outer_texture = new TextureRegionDrawable(atlas.findRegion("loading_icon_outer"));
        this.inner_texture = new TextureRegionDrawable(atlas.findRegion("loading_icon_inner"));
        this.outer_circle_angle = 0f;
        this.inner_circle_angle = 0f;
        this.bounds = new float[4];
    }

    public static LoadingIcon newInstance(TextureAtlas atlas) {
        LoadingIcon instance = new LoadingIcon(atlas);
        return instance;
    }

    @Override
    public void act(float delta) {
        if (!super.isVisible()) return;
        this.outer_circle_angle += delta * OUTER_CIRCLE_SPEED;
        this.inner_circle_angle += delta * INNER_CIRCLE_SPEED;
        while (this.outer_circle_angle >= 360f) this.outer_circle_angle -= 360f;
        while (this.outer_circle_angle <= -360f) this.outer_circle_angle += 360f;
        while (this.inner_circle_angle >= 360f) this.inner_circle_angle -= 360f;
        while (this.inner_circle_angle <= -360f) this.inner_circle_angle += 360f;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!super.isVisible()) return;
        this.back_matrix.set(batch.getProjectionMatrix());
        batch.setProjectionMatrix(this.pixel_matrix);
        this.outer_texture.draw(
                batch,
                this.bounds[0],
                this.bounds[1],
                this.bounds[2] / 2f,
                this.bounds[3] / 2f,
                this.bounds[2],
                this.bounds[3],
                1f, 1f,
                this.outer_circle_angle
        );
        this.inner_texture.draw(
                batch,
                this.bounds[0],
                this.bounds[1],
                this.bounds[2] / 2f,
                this.bounds[3] / 2f,
                this.bounds[2],
                this.bounds[3],
                1f, 1f,
                this.inner_circle_angle
        );
        batch.setProjectionMatrix(this.back_matrix);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void resize(int width, int height) {
        this.pixel_matrix.setToOrtho2D(0f, 0f, width, height);
        float ppu_x = (float) width / (float) AppConfig.VIEWPORT_WIDTH;
        float ppu_y = (float) height / (float) AppConfig.VIEWPORT_HEIGHT;
        this.bounds[3] = super.getHeight() * ppu_y;
        this.bounds[2] = this.bounds[3];
        this.bounds[0] = (super.getX() + super.getWidth() / 2f) * ppu_x - this.bounds[2] / 2f;
        this.bounds[1] = super.getY() * ppu_y;
    }

}
