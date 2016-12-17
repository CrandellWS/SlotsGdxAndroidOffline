package mobi.square.slots.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import mobi.square.slots.config.AppConfig;

public class LoadingLights extends Group {

    private final LoadingLight[] lights;
    private float loading_time;
    private int loading_index;

    private LoadingLights(TextureRegion inactive, TextureRegion active) {
        super();
        this.loading_time = .0f;
        this.loading_index = 0;
        this.lights = new LoadingLight[]{
                new LoadingLight(inactive, active),
                new LoadingLight(inactive, active),
                new LoadingLight(inactive, active),
                new LoadingLight(inactive, active)
        };
        for (int i = 0; i < this.lights.length; ++i) {
            super.addActor(this.lights[i]);
        }
    }

    public static LoadingLights newInstance(TextureRegion inactive, TextureRegion active) {
        LoadingLights instance = new LoadingLights(inactive, active);
        instance.lights[0].active = true;
        instance.lights[0].setBounds(.0f, .0f, 70.f, 70.f);
        instance.lights[1].setBounds(70.0f, 7.0f, 70.f, 70.f);
        instance.lights[2].setBounds(140.0f, 16.0f, 70.f, 70.f);
        instance.lights[3].setBounds(210.0f, 29.0f, 70.f, 70.f);
        return instance;
    }

    @Override
    public void act(float delta) {
        this.loading_time += delta;
        while (this.loading_time > AppConfig.LOADING_LIGHTS_SPEED) {
            this.loading_time -= AppConfig.LOADING_LIGHTS_SPEED;
            this.lights[this.loading_index].active = false;
            if (++this.loading_index > 3) this.loading_index = 0;
            this.lights[this.loading_index].active = true;
        }
    }

    private class LoadingLight extends Actor {
        private final TextureRegion t_inactive;
        private final TextureRegion t_active;
        private boolean active;

        public LoadingLight(TextureRegion inactive, TextureRegion active) {
            this.t_inactive = inactive;
            this.t_active = active;
            this.active = false;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            TextureRegion texture = this.active ? this.t_active : this.t_inactive;
            batch.draw(texture, super.getX(), super.getY(), super.getWidth(), super.getHeight());
        }
    }

}
