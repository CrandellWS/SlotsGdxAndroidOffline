package mobi.square.slots.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.LinkedList;
import java.util.List;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.containers.UserInfo;
import mobi.square.slots.error.StringCodeException;
import mobi.square.slots.handlers.LoadingHandler;
import mobi.square.slots.logger.Log;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.SettingsWindow.HideButton;
import mobi.square.slots.utils.utils;

public class TopWindow extends Group implements Resizable {

    private static final float SHOW_SPEED = 2000f;

    private final NinePatchDrawable background;
    private final TextureRegionDrawable highlight;
    private final TextureRegionDrawable shadow_top;
    private final TextureRegionDrawable shadow_bottom;
    private final TopScroll top_scroll;
    private final HideButton hide_button;
    private final ButtonsBox hourly_box;
    private final ButtonsBox daily_box;
    private final LoadingIcon loading_icon;
    private final PixelLabel title_lable;

    private LoadingHandler handler;
    private int last_type;
    private float target_y;
    private int state;

    private TopWindow(TextureAtlas header_atlas, TextureAtlas windows_atlas) {
        super();
        AtlasRegion shadow = windows_atlas.findRegion("top_shadow");
        this.shadow_top = new TextureRegionDrawable(shadow);
        shadow = new AtlasRegion(
                shadow.getTexture(),
                shadow.getRegionX(),
                shadow.getRegionY(),
                shadow.getRegionWidth(),
                shadow.getRegionHeight()
        );
        shadow.flip(false, true);
        this.shadow_bottom = new TextureRegionDrawable(shadow);
        this.background = new NinePatchDrawable(windows_atlas.createPatch("top_background"));
        this.highlight = new TextureRegionDrawable(windows_atlas.findRegion("top_highlight"));
        this.hide_button = HideButton.newInstance(windows_atlas);
        this.hourly_box = ButtonsBox.newInstance(windows_atlas, "green");
        this.daily_box = ButtonsBox.newInstance(windows_atlas, "orange");
        this.top_scroll = TopScroll.newInstance(windows_atlas);
        this.loading_icon = LoadingIcon.newInstance(header_atlas);
        LabelStyle ls = new LabelStyle();
        ls.font = FontsFactory.getAsync("Junegull.ttf", 30);
        ls.fontColor = Color.WHITE;
        this.title_lable = new PixelLabel(Connection.getString("top_window_title"), ls);
        this.title_lable.setAlignment(Align.center, Align.center);
        super.addActor(this.hide_button);
        super.addActor(this.hourly_box);
        super.addActor(this.daily_box);
        super.addActor(this.title_lable);
        super.addActor(this.top_scroll);
        super.addActor(this.loading_icon);
        this.handler = null;
        this.target_y = 0f;
        this.last_type = 0;
        this.state = 0;
        this.hide_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (hide_button.isDisabled()) return;
                hide();
            }
        });
    }

    public static TopWindow newInstance(TextureAtlas header_atlas, TextureAtlas windows_atlas) {
        TopWindow instance = new TopWindow(header_atlas, windows_atlas);
        instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        instance.hide_button.setBounds(AppConfig.VIEWPORT_WIDTH - 160f, 10f, 160f, 60f);
        instance.hourly_box.setBounds(780f, 260f, 220f, 150f);
        instance.daily_box.setBounds(780f, 90f, 220f, 150f);
        instance.title_lable.setBounds(757f, 430f, 260f, 50f);
        instance.top_scroll.setBounds(36f, 0f, 690f, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        instance.hourly_box.getTopButton().setText(Connection.getString("top_button_hourly_leaders"));
        instance.hourly_box.getBottomButton().setText(Connection.getString("top_button_hourly_winners"));
        instance.daily_box.getTopButton().setText(Connection.getString("top_button_daily_leaders"));
        instance.daily_box.getBottomButton().setText(Connection.getString("top_button_daily_winners"));
        instance.loading_icon.setBounds(36f, 200f, 710f, 120f);
        instance.loading_icon.setVisible(false);
        instance.hideNow();
        return instance;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        switch (this.state) {
            case 1:
                super.setY(super.getY() - delta * SHOW_SPEED);
                if (super.getY() <= this.target_y) {
                    super.setY(this.target_y);
                    super.setVisible(true);
                    this.state = 3;
                }
                break;
            case 2:
                super.setY(super.getY() + delta * SHOW_SPEED);
                float hide_y = AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT;
                if (super.getY() >= hide_y) {
                    super.setY(hide_y);
                    super.setVisible(false);
                    this.clearList();
                    this.state = 0;
                }
                break;
            default:
                break;
        }
        if (this.handler != null) {
            if (this.handler.isLoaded()) {
                try {
                    UserInfo[] users = Connection.getInstance().responseGetTop(this.handler.getJson());
                    this.hourly_box.setCountdown(Connection.getInstance().getLeadersHourlyTime());
                    this.daily_box.setCountdown(Connection.getInstance().getLeadersDailyTime());
                    this.top_scroll.fillList(users);
                    this.loading_icon.setVisible(false);
                    this.handler = null;
                } catch (StringCodeException e) {
                    Log.log(e);
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
        this.background.draw(batch, x, y, width, height);
        this.highlight.draw(batch, x + .737f * width, y + .834f * height, .26f * width, .16f * height);
        float shadow_height = .04f * height;
        this.shadow_top.draw(batch, x, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT - shadow_height, width, shadow_height);
        this.shadow_bottom.draw(batch, x, y, width, shadow_height);
        super.draw(batch, parentAlpha);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        this.target_y = y;
    }

    public void show() {
        if (this.state == 3) return;
        super.setVisible(true);
        this.state = 1;
    }

    public void hide() {
        if (this.state == 0) return;
        this.state = 2;
    }

    public void hideNow() {
        this.state = 0;
        super.setY(AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        super.setVisible(false);
    }

    public void showList(LoadingHandler handler, int type) {
        this.handler = handler;
        this.hourly_box.top_button.setDisabled(type == 1);
        this.hourly_box.bottom_button.setDisabled(type == 2);
        this.daily_box.top_button.setDisabled(type == 3);
        this.daily_box.bottom_button.setDisabled(type == 4);
        this.top_scroll.clearList();
        this.loading_icon.setVisible(true);
        this.last_type = type;
    }

    public void clearList() {
        this.top_scroll.clearList();
    }

    public void fillList(UserInfo[] users) {
        this.top_scroll.fillList(users);
    }

    @Override
    public void resize(int width, int height) {
        this.hide_button.resize(width, height);
        this.hourly_box.resize(width, height);
        this.daily_box.resize(width, height);
        this.top_scroll.resize(width, height);
        this.loading_icon.resize(width, height);
        this.title_lable.resize(width, height);
    }

    public int getLastType() {
        if (this.last_type < 1 ||
                this.last_type > 4)
            this.last_type = 1;
        return this.last_type;
    }

    public BoxButtonTop getHourlyLeadersButton() {
        return this.hourly_box.top_button;
    }

    public BoxButtonBottom getHourlyWinnersButton() {
        return this.hourly_box.bottom_button;
    }

    public BoxButtonTop getDailyLeadersButton() {
        return this.daily_box.top_button;
    }

    public BoxButtonBottom getDailyWinnersButton() {
        return this.daily_box.bottom_button;
    }

    public static class ButtonsBox extends Group implements Resizable {
        private final BoxButtonTop top_button;
        private final BoxButtonBottom bottom_button;
        private final BoxLabel label;
        private int int_countdown;
        private float countdown;

        private ButtonsBox(TextureAtlas atlas, String color) {
            super();
            this.top_button = BoxButtonTop.newInstance(atlas);
            this.bottom_button = BoxButtonBottom.newInstance(atlas);
            this.label = BoxLabel.newInstance(atlas, color);
            super.addActor(this.label);
            super.addActor(this.top_button);
            super.addActor(this.bottom_button);
            this.int_countdown = 0;
            this.countdown = 0f;
        }

        public static ButtonsBox newInstance(TextureAtlas atlas, String color) {
            ButtonsBox instance = new ButtonsBox(atlas, color);
            return instance;
        }

        @Override
        public void act(float delta) {
            if (this.countdown > 0f) {
                this.countdown -= delta;
                if (this.countdown <= 0f) {
                    this.countdown = 0f;
                    this.int_countdown = 0;
                    this.setButtonTime();
                } else {
                    int seconds = (int) this.countdown;
                    if (seconds != this.int_countdown) {
                        this.int_countdown = seconds;
                        this.setButtonTime();
                    }
                }
            }
            super.act(delta);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.bottom_button.setBounds(0f, 0f, width, .485f * height);
            this.top_button.setBounds(0f, .485f * height, width, .515f * height);
            this.label.setBounds(.17f * width, .375f * height, .648f * width, .252f * height);
        }

        @Override
        public void resize(int width, int height) {
            this.top_button.resize(width, height);
            this.bottom_button.resize(width, height);
            this.label.resize(width, height);
        }

        public void setCountdown(int seconds) {
            this.countdown = (float) seconds;
            this.int_countdown = seconds;
            this.setButtonTime();
        }

        public BoxButtonTop getTopButton() {
            return this.top_button;
        }

        public BoxButtonBottom getBottomButton() {
            return this.bottom_button;
        }

        private void setButtonTime() {
            int h = this.int_countdown / 3600;
            int t = this.int_countdown - h * 3600;
            int m = t / 60;
            int s = t - m * 60;
            StringBuilder builder = new StringBuilder();
            builder.append(h);
            builder.append(":");
            if (m < 10) builder.append("0");
            builder.append(m);
            builder.append(":");
            if (s < 10) builder.append("0");
            builder.append(s);
            this.label.setText(builder.toString());
        }
    }

    public static class BoxButtonTop extends Button implements Resizable {
        private final PixelLabel label;

        private BoxButtonTop(ButtonStyle style) {
            super(style);
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 18);
            ls.fontColor = Color.WHITE;
            this.label = new PixelLabel("", ls);
            this.label.setAlignment(Align.center, Align.center);
            super.addActor(this.label);
        }

        public static BoxButtonTop newInstance(TextureAtlas atlas) {
            ButtonStyle style = new ButtonStyle();
            style.up = new TextureRegionDrawable(atlas.findRegion("top_button_up_active"));
            style.down = new TextureRegionDrawable(atlas.findRegion("top_button_up_pressed"));
            style.disabled = new TextureRegionDrawable(atlas.findRegion("top_button_up_inactive"));
            style.pressedOffsetY = -2f;
            BoxButtonTop instance = new BoxButtonTop(style);
            return instance;
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.label.setBounds(0f, .2f * height, width, .8f * height);
        }

        @Override
        public void resize(int width, int height) {
            this.label.resize(width, height);
        }

        public void setText(CharSequence text) {
            this.label.setText(text);
        }
    }

    public static class BoxButtonBottom extends Button implements Resizable {
        private final PixelLabel label;

        private BoxButtonBottom(ButtonStyle style) {
            super(style);
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 18);
            ls.fontColor = Color.WHITE;
            this.label = new PixelLabel("", ls);
            this.label.setAlignment(Align.center, Align.center);
            super.addActor(this.label);
        }

        public static BoxButtonBottom newInstance(TextureAtlas atlas) {
            ButtonStyle style = new ButtonStyle();
            style.up = new TextureRegionDrawable(atlas.findRegion("top_button_down_active"));
            style.down = new TextureRegionDrawable(atlas.findRegion("top_button_down_pressed"));
            style.disabled = new TextureRegionDrawable(atlas.findRegion("top_button_down_inactive"));
            style.pressedOffsetY = -2f;
            BoxButtonBottom instance = new BoxButtonBottom(style);
            return instance;
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.label.setBounds(0f, 0f, width, .85f * height);
        }

        @Override
        public void resize(int width, int height) {
            this.label.resize(width, height);
        }

        public void setText(CharSequence text) {
            this.label.setText(text);
        }
    }

    public static class BoxLabel extends Group implements Resizable {
        private final TextureRegionDrawable background;
        private final PixelLabel label;

        private BoxLabel(TextureAtlas atlas, String color) {
            this.background = new TextureRegionDrawable(atlas.findRegion("top_timer_frame_".concat(color)));
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("whitrabt.ttf", 24);
            ls.fontColor = new Color(.1f, .1f, .1f, 1f);
            this.label = new PixelLabel("0:00:00", ls);
            this.label.setAlignment(Align.center, Align.center);
            super.addActor(this.label);
        }

        public static BoxLabel newInstance(TextureAtlas atlas, String color) {
            BoxLabel instance = new BoxLabel(atlas, color);
            return instance;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            this.background.draw(batch, super.getX(), super.getY(), super.getWidth(), super.getHeight());
            super.draw(batch, parentAlpha);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.label.setBounds(.01f * width, 0f, width, height);
        }

        @Override
        public void resize(int width, int height) {
            this.label.resize(width, height);
        }

        public void setText(String text) {
            this.label.setText(text);
        }
    }

    private static class TopScroll extends ScrollPane implements Resizable {
        private final Table table;
        private final TextureRegionDrawable money_icon;
        private final TextureRegionDrawable horizontal_line;
        private final List<TopUser> list;

        private TopScroll(Table table, TextureRegion money_icon, TextureRegion horizontal_line) {
            super(table);
            this.table = table;
            this.money_icon = new TextureRegionDrawable(money_icon);
            this.horizontal_line = new TextureRegionDrawable(horizontal_line);
            this.list = new LinkedList<TopUser>();
        }

        public static TopScroll newInstance(TextureAtlas atlas) {
            Table table = new Table();
            TopScroll instance = new TopScroll(table, atlas.findRegion("money_icon"), atlas.findRegion("top_line_horisontal"));
            instance.setScrollingDisabled(true, false);
            instance.setFadeScrollBars(false);
            return instance;
        }

        @Override
        public void resize(int width, int height) {
            for (TopUser user : this.list)
                user.resize(width, height);
            this.table.invalidate();
            super.invalidate();
        }

        public void clearList() {
            for (TopUser user : this.list)
                user.dispose();
            this.table.clear();
            this.table.invalidate();
            this.list.clear();
        }

        public void fillList(UserInfo[] users) {
            this.clearList();
            if (users == null) return;
            float width = super.getWidth();
            float height = 100f;
            String id = Connection.getInstance().getUserId();
            for (int i = 0; i < users.length; ++i) {
                if (i > 0) this.table.row();
                TopUser user;
                if (id != null && id.equals(users[i].getId())) {
                    int place = i == users.length - 1 && i > 9 ? 0 : i + 1;
                    user = new TopUser(this.money_icon, this.horizontal_line, users[i].getTexture(), Connection.getString("cup_you"), users[i].getValue(), place);
                } else
                    user = new TopUser(this.money_icon, this.horizontal_line, users[i].getTexture(), users[i].getName(), users[i].getValue(), i + 1);
                if (i == users.length - 1) user.setDrawLine(false);
                Cell<?> cell = this.table.add(user);
                cell.padTop(6f);
                cell.size(width, height);
                this.list.add(user);
            }
            this.table.invalidate();
            this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    public static class TopUser extends Group implements Resizable {
        private final PixelLabel label_place;
        private final PixelLabel label_name;
        private final PixelLabel label_money;
        private final TextureRegionDrawable horizontal_line;
        private final TextureRegionDrawable money_icon;
        private final float[] money_icon_bounds;
        private final float[] image_bounds;
        private final Texture image;
        private boolean draw_line;

        private TopUser(TextureRegionDrawable money_icon, TextureRegionDrawable horizontal_line, Texture image, String name, int money, int place) {
            super();
            this.image = image;
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = Color.WHITE;
            this.label_place = new PixelLabel(place > 0 ? String.valueOf(place) : "", ls);
            this.label_place.setAlignment(Align.center, Align.center);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = new Color(.467f, .882f, .918f, 1f);
            /*try {
				name = new String(name.getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) { Log.log(e); }*/
            this.label_name = new PixelLabel(name, ls);
            this.label_name.setAlignment(Align.left, Align.center);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = Color.WHITE;
            this.label_money = new PixelLabel(utils.splitNumber(money, 3), ls);
            this.label_money.setAlignment(Align.left, Align.center);
            this.horizontal_line = horizontal_line;
            this.money_icon = money_icon;
            this.money_icon_bounds = new float[4];
            this.image_bounds = new float[4];
            super.addActor(this.label_place);
            super.addActor(this.label_name);
            super.addActor(this.label_money);
            this.draw_line = true;
        }

        public static TopUser newInstance(TextureRegionDrawable money_icon, TextureRegionDrawable horizontal_line, Texture image, String name, int money, int place) {
            TopUser instance = new TopUser(money_icon, horizontal_line, image, name, money, place);
            return instance;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            float x = super.getX();
            float y = super.getY();
            this.money_icon.draw(
                    batch,
                    this.money_icon_bounds[0] + x,
                    this.money_icon_bounds[1] + y,
                    this.money_icon_bounds[2],
                    this.money_icon_bounds[3]
            );
            if (this.image != null) {
                batch.draw(
                        this.image,
                        this.image_bounds[0] + x,
                        this.image_bounds[1] + y,
                        this.image_bounds[2],
                        this.image_bounds[3]
                );
            }
            if (this.draw_line) {
                float width = super.getWidth();
                float height = super.getHeight();
                this.horizontal_line.draw(batch, x + .1f * width, y, .9f * width, .04f * height);
            }
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            float bottom_line = .1f * height;
            float inner_height = .9f * height;
            this.label_place.setBounds(0f, bottom_line, .1f * width, inner_height);
            this.label_name.setBounds(.12f * width + inner_height, bottom_line, .3f * width, inner_height);
            float icon_aspect = (float) this.money_icon.getRegion().getRegionWidth() / (float) this.money_icon.getRegion().getRegionHeight();
            this.money_icon_bounds[0] = .65f * width;
            this.money_icon_bounds[1] = bottom_line + .25f * inner_height;
            this.money_icon_bounds[3] = .5f * inner_height;
            this.money_icon_bounds[2] = this.money_icon_bounds[3] * icon_aspect;
            this.image_bounds[0] = .1f * width;
            this.image_bounds[1] = bottom_line;
            if (this.image != null) {
                int tw = this.image.getWidth();
                int th = this.image.getHeight();
                if (tw > th) {
                    float aspect = (float) th / (float) tw;
                    this.image_bounds[2] = inner_height;
                    this.image_bounds[3] = inner_height * aspect;
                } else if (tw < th) {
                    float aspect = (float) tw / (float) th;
                    this.image_bounds[2] = inner_height * aspect;
                    this.image_bounds[3] = inner_height;
                } else {
                    this.image_bounds[2] = inner_height;
                    this.image_bounds[3] = inner_height;
                }
            } else {
                this.image_bounds[2] = inner_height;
                this.image_bounds[3] = inner_height;
            }
            float money_x = this.money_icon_bounds[0] + this.money_icon_bounds[2] + .01f * width;
            this.label_money.setBounds(money_x, bottom_line, width - money_x, inner_height);
        }

        public void dispose() {
            if (this.image != null) {
                this.image.dispose();
            }
        }

        @Override
        public void resize(int width, int height) {
            this.label_place.resize(width, height);
            this.label_name.resize(width, height);
            this.label_money.resize(width, height);
        }

        public boolean isDrawLine() {
            return this.draw_line;
        }

        public void setDrawLine(boolean draw_line) {
            this.draw_line = draw_line;
        }
    }

}
