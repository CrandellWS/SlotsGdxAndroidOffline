package mobi.square.slots.stages;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

import mobi.square.slots.config.AppConfig;
import mobi.square.slots.handlers.LoadingHandler;
import mobi.square.slots.screens.SBasic;

public abstract class Basic extends Stage {

    public SBasic parent_screen;

    public Basic(SBasic parent) {
        super(
                new ScalingViewport(
                        Scaling.stretch,
                        AppConfig.VIEWPORT_WIDTH,
                        AppConfig.VIEWPORT_HEIGHT,
                        new OrthographicCamera()
                ),
                parent != null ? parent.getSpriteBatch() : new SpriteBatch()
        );
        this.parent_screen = parent;
    }

    public void setParentScreen(SBasic parent) {
        this.parent_screen = parent;
    }

    public void resize(int width, int height) {
        super.getViewport().update(width, height, true);
    }

    public abstract void load(LoadingHandler handler);

}
