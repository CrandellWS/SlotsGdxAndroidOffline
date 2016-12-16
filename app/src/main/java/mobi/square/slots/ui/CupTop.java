package mobi.square.slots.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import java.util.LinkedList;
import java.util.List;

import mobi.square.slots.api.Connection;
import mobi.square.slots.containers.UserInfo;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.utils.utils;

public class CupTop extends CupBigWindow {

    private final TopScroll scroll;
    private final PrizeFrame frame;

    private CupTop(TextureAtlas atlas, TextureAtlas windows_atlas) {
        super(atlas, windows_atlas, Connection.getString("cup_top_title"));
        this.scroll = TopScroll.newInstance(atlas);
        super.addActor(this.scroll);
        this.frame = PrizeFrame.newInstance(atlas);
        super.addActor(this.frame);
    }

    public static CupTop newInstance(TextureAtlas atlas, TextureAtlas windows_atlas) {
        CupTop instance = new CupTop(atlas, windows_atlas);
        instance.initialize();
        instance.scroll.setBounds(0f, 0f, 630f, 442f);
        instance.frame.setBounds(640f, 5f, 375f, 425f);
        return instance;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.scroll.resize(width, height);
        this.frame.resize(width, height);
    }

    public void show(int[] awards, UserInfo[] leaders) {
        this.scroll.fillList(leaders);
        this.frame.fillList(awards);
        super.show();
    }

    @Override
    public void hide() {
        this.scroll.clearList();
        super.hide();
    }

    private static class TopScroll extends ScrollPane implements Resizable {
        private final Table table;
        private final TextureRegion money_icon;
        private final TextureRegion score_line;
        private final TextureRegion horizontal_line;
        private final List<TopUser> list;

        private TopScroll(Table table, TextureRegion money_icon, TextureRegion horizontal_line, TextureRegion score_line) {
            super(table);
            this.table = table;
            this.money_icon = money_icon;
            this.score_line = score_line;
            this.horizontal_line = horizontal_line;
            this.list = new LinkedList<TopUser>();
        }

        public static TopScroll newInstance(TextureAtlas atlas) {
            Table table = new Table();
            TopScroll instance = new TopScroll(table, atlas.findRegion("chips_icon"), atlas.findRegion("top_line_horisontal"), atlas.findRegion("top_score_line"));
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
            for (int i = 0; i < users.length; ++i) {
                if (i > 0) this.table.row();
                TopUser user = new TopUser(this.money_icon, this.horizontal_line, this.score_line, users[i], users[i].getName(), users[i].getValue(), users[i].getScore(), i + 1);
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
        private final PixelLabel label_points;
        private final PixelLabel label_score;
        private final TextureRegion horizontal_line;
        private final TextureRegion score_line;
        private final TextureRegion money_icon;
        private final float[] money_icon_bounds;
        private final float[] image_bounds;
        private final Texture image;
        private final UserInfo info;
        private boolean draw_line;

        private TopUser(TextureRegion money_icon, TextureRegion horizontal_line, TextureRegion score_line, UserInfo info, String name, int money, int score, int place) {
            super();
            this.info = info;
            this.image = info.getTexture();
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = place <= 3 ? new Color(1f, .84f, 0f, 1f) : Color.WHITE;
            this.label_place = new PixelLabel(String.valueOf(place), ls);
            this.label_place.setAlignment(Align.center);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = new Color(1f, .84f, 0f, 1f);
            this.label_name = new PixelLabel(name, ls);
            this.label_name.setAlignment(Align.left);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = Color.WHITE;
            this.label_money = new PixelLabel(utils.splitNumber(money, 3), ls);
            this.label_money.setAlignment(Align.left);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Taurus.ttf", 24);
            ls.fontColor = Color.WHITE;
            this.label_points = new PixelLabel(Connection.getString("cup_score"), ls);
            this.label_points.setAlignment(Align.left);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = Color.GREEN;
            this.label_score = new PixelLabel(utils.splitNumber(score, 3), ls);
            this.label_score.setAlignment(Align.left);
            this.horizontal_line = horizontal_line;
            this.score_line = score_line;
            this.money_icon = money_icon;
            this.money_icon_bounds = new float[4];
            this.image_bounds = new float[4];
            super.addActor(this.label_place);
            super.addActor(this.label_name);
            super.addActor(this.label_money);
            super.addActor(this.label_points);
            super.addActor(this.label_score);
            this.draw_line = true;
        }

        public static TopUser newInstance(TextureRegion money_icon, TextureRegion horizontal_line, TextureRegion score_line, UserInfo info, String name, int money, int score, int place) {
            TopUser instance = new TopUser(money_icon, horizontal_line, score_line, info, name, money, score, place);
            return instance;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            float x = super.getX();
            float y = super.getY();
            batch.draw(
                    this.money_icon,
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
            float width = super.getWidth();
            float height = super.getHeight();
            if (this.draw_line) {
                batch.draw(this.horizontal_line, x + .1f * width, y, .9f * width, .04f * height);
            }
            batch.draw(this.score_line, x + .4f * width, y + .3f * height, .292f * width, .03f * height);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            float bottom_line = .1f * height;
            float inner_height = .9f * height;
            float inner_level2 = .4f * height;
            float bottom_level2 = bottom_line + .45f * height;
            this.label_place.setBounds(0f, bottom_line, .1f * width, inner_height);
            this.label_name.setBounds(.12f * width + inner_height, bottom_level2, .3f * width, inner_level2);
            this.label_points.setBounds(.12f * width + inner_height, bottom_line, .3f * width, .5f * inner_height);
            float icon_aspect = (float) this.money_icon.getRegionWidth() / (float) this.money_icon.getRegionHeight();
            this.money_icon_bounds[0] = .65f * width;
            this.money_icon_bounds[1] = bottom_line + .4f * height;
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
            this.label_money.setBounds(money_x, bottom_level2, width - money_x, inner_level2);
            this.label_score.setBounds(money_x, bottom_line, width - money_x, .5f * inner_height);
        }

        public void dispose() {
            if (this.info != null) {
                this.info.dispose();
            }
        }

        @Override
        public void resize(int width, int height) {
            this.label_place.resize(width, height);
            this.label_name.resize(width, height);
            this.label_money.resize(width, height);
            this.label_points.resize(width, height);
            this.label_score.resize(width, height);
        }

        public boolean isDrawLine() {
            return this.draw_line;
        }

        public void setDrawLine(boolean draw_line) {
            this.draw_line = draw_line;
        }
    }

    private static class PrizeFrame extends Group implements Resizable {
        private final DrawableActor background;
        private final PrizeItem[] items;
        private final PixelLabel title;

        private PrizeFrame(TextureAtlas atlas) {
            super();
            this.background = DrawableActor.newInstance(atlas.findRegion("top_prize_frame"));
            super.addActor(this.background);
            TextureRegion background = atlas.findRegion("top_timer_background");
            TextureRegion frame = atlas.findRegion("price_background");
            TextureRegion icon = atlas.findRegion("chips_icon");
            this.items = new PrizeItem[10];
            for (int i = 0; i < this.items.length; i++) {
                this.items[i] = PrizeItem.newInstance(background, frame, icon);
                super.addActor(this.items[i]);
            }
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = Color.WHITE;
            this.title = new PixelLabel(Connection.getString("cup_prize_places"), ls);
            this.title.setAlignment(Align.center);
            super.addActor(this.title);
        }

        public static PrizeFrame newInstance(TextureAtlas atlas) {
            PrizeFrame instance = new PrizeFrame(atlas);
            return instance;
        }

        @Override
        public void resize(int width, int height) {
            for (int i = 0; i < this.items.length; this.items[i++].resize(width, height)) ;
            this.title.resize(width, height);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.background.setBounds(0f, 0f, width, height);
            float inner = height * .87f;
            float line = inner / 10.4f;
            float offset = .15f * line;
            float item = .85f * line;
            for (int i = 0; i < this.items.length; i++) {
                this.items[i].setBounds(.05f * width, inner - (i + 1) * item - i * offset, .9f * width, item);
            }
            this.title.setBounds(0f, inner, width, height - inner);
        }

        public void fillList(int[] awards) {
            if (awards != null) {
                for (int i = 0; i < this.items.length; i++) {
                    if (awards.length > i) {
                        this.items[i].setValue(i + 1, awards[i]);
                        this.items[i].setVisible(true);
                    } else this.items[i].setVisible(false);
                }
            } else for (int i = 0; i < this.items.length; this.items[i++].setVisible(false)) ;
        }
    }

    private static class PrizeItem extends Group implements Resizable {
        private final DrawableActor background;
        private final DrawableActor frame;
        private final DrawableActor icon;
        private final PixelLabel place;
        private final PixelLabel award;

        private PrizeItem(TextureRegion background, TextureRegion frame, TextureRegion icon) {
            this.background = DrawableActor.newInstance(background);
            this.frame = DrawableActor.newInstance(frame);
            this.icon = DrawableActor.newInstance(icon);
            super.addActor(this.background);
            super.addActor(this.frame);
            super.addActor(this.icon);
            LabelStyle ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 22);
            ls.fontColor = new Color(0.38f, 0.075f, 0f, 1f);
            this.place = new PixelLabel("", ls);
            this.place.setAlignment(Align.center);
            super.addActor(this.place);
            ls = new LabelStyle();
            ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
            ls.fontColor = new Color(1f, .84f, 0f, 1f);
            this.award = new PixelLabel("", ls);
            this.award.setAlignment(Align.center);
            super.addActor(this.award);
        }

        public static PrizeItem newInstance(TextureRegion background, TextureRegion frame, TextureRegion icon) {
            PrizeItem instance = new PrizeItem(background, frame, icon);
            return instance;
        }

        @Override
        public void resize(int width, int height) {
            this.place.resize(width, height);
            this.award.resize(width, height);
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            super.setBounds(x, y, width, height);
            this.background.setBounds(0f, 0f, width, height);
            this.frame.setBounds(.4f * width, -.05f * height, .6f * width, 1.1f * height);
            this.icon.setBounds(.38f * width, 0f, .08f * width, height);
            this.place.setBounds(0f, 0f, .4f * width, height);
            this.award.setBounds(.4f * width, 0f, .6f * width, height);
        }

        public void setValue(int place, int award) {
            this.place.setText(String.valueOf(place).concat(" ").concat(Connection.getString("cup_place")));
            this.award.setText(String.valueOf(award));
        }
    }

}
