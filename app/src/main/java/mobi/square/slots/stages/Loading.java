package mobi.square.slots.stages;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import mobi.square.slots.config.AppConfig;
import mobi.square.slots.handlers.LoadingHandler;
import mobi.square.slots.screens.SBasic;
import mobi.square.slots.tools.AtlasLoader;
import mobi.square.slots.ui.LoadingLights;

public class Loading extends Basic {

    private TextureAtlas atlas;

    public Loading(SBasic parent) {
        super(parent);
    }

    @Override
    public void load(LoadingHandler handler) {
        this.atlas = AtlasLoader.get(AppConfig.LOADING_SCREEN_ATLAS);
        super.addActor(new Background(this.atlas.findRegion("loading")));
        LoadingLights lights = LoadingLights.newInstance(
                this.atlas.findRegion("inactive"),
                this.atlas.findRegion("active")
        );
        lights.setPosition(403f, 69f);
        super.addActor(lights);
    }

    private class Background extends Actor {
        private final TextureRegion texture;

        public Background(TextureRegion texture) {
            this.texture = texture;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.draw(this.texture, 0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT);
        }
    }

}
