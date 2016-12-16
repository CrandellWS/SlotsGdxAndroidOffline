package mobi.square.slots.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.dl.FilesList;
import mobi.square.slots.dl.InstallManager;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.stages.Lobby;
import mobi.square.slots.ui.DownloadDialog.DialogClosedListener;

public class LobbyScroll extends ScrollPane implements Resizable {

    private final Lobby stage;
    private final Table table;

    public LobbyScroll(TextureAtlas atlas, Table table, Lobby parent) {
        super(table);
        this.table = table;
        this.stage = parent;
    }

    public static LobbyScroll newInstance(TextureAtlas atlas, Lobby parent) {
        Table table = new Table();
        LobbyScroll instance = new LobbyScroll(atlas, table, parent);
        instance.setBounds(0f, 0f, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT - GameHeader.HEADER_HEIGHT);
        instance.setScrollingDisabled(false, true);
        instance.setFadeScrollBars(false);
        return instance;
    }

    public void addButtons(TextureAtlas atlas, LobbyConfig[] buttons) {
        if (buttons == null) return;
        int max = buttons.length / 2;
        if (buttons.length % 2 != 0) max++;
        float x_aspect = (float) Gdx.graphics.getWidth() / (float) AppConfig.VIEWPORT_WIDTH;
        float y_aspect = (float) Gdx.graphics.getHeight() / (float) AppConfig.VIEWPORT_HEIGHT;
        float cell_height = super.getHeight() / 2f * .8f;
        float cell_width = cell_height * y_aspect / x_aspect;
        for (int i = 0; i < buttons.length; i++) {
            //System.out.println("buttons type jose " + buttons[i].type);
            LobbyButton button = LobbyButton.newInstance(atlas, buttons[i].type, this.stage);
            Cell<?> cell = this.table.add(button);
            cell.height(cell_height);
            cell.width(cell_width);
            cell.align(Align.center);
            if (i == 0) {
                cell.padLeft(50f);
                cell.padRight(20f);
            } else if (i == max - 1) {
                cell.padRight(50f);
            } else cell.padRight(20f);
            cell.padBottom(10f);
            if (buttons[i].listener != null) {
                button.addListener(buttons[i].listener);
            }
        }
        //this.table.row();
        /*for (int i = max; i < buttons.length; i++) {
			LobbyButton button = LobbyButton.newInstance(atlas, buttons[i].type, this.stage);
			Cell<?> cell = this.table.add(button);
			cell.height(cell_height);
			cell.width(cell_width);
			cell.align(Align.center);
			cell.padTop(10f);
			if ( buttons[i].type == SlotsType.BOOK_OF_RA) { 
				cell.padLeft(50f);
				cell.padRight(20f);
			} else if (i == buttons.length - 1) {
				cell.padRight(50f);
			} else cell.padRight(20f);
			if (buttons[i].listener != null) {
				button.addListener(buttons[i].listener);
			}
		}*/
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        try {
            super.draw(batch, parentAlpha);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void resize(int width, int height) {
        Array<Cell> cells = this.table.getCells();
        float x_aspect = (float) width / (float) AppConfig.VIEWPORT_WIDTH;
        float y_aspect = (float) height / (float) AppConfig.VIEWPORT_HEIGHT;
        float cell_height = super.getHeight() / 2f * .8f;
        float cell_width = cell_height * y_aspect / x_aspect;
        for (Cell cell : cells) {
            cell.size(cell_width, cell_height);
        }
        this.table.invalidate();
        super.invalidate();
    }

    public static class LobbyConfig {
        public final boolean available;
        final SlotsType type;
        final ClickListener listener;

        public LobbyConfig(SlotsType type, ClickListener listener, boolean available) {
            this.type = type;
            this.listener = listener;
            this.available = available;
        }
    }

    private static class ProgressBar extends Group {
        private final DrawableActor region;
        private final Camera camera;
        private float value;

        private ProgressBar(AtlasRegion region, Camera camera) {
            this.region = DrawableActor.newInstance(region);
            super.addActor(this.region);
            this.camera = camera;
            this.value = 0;
        }

        public static ProgressBar newInstance(AtlasRegion region, Camera camera) {
            ProgressBar instance = new ProgressBar(region, camera);
            return instance;
        }

        public void setProgress(float value) {
            if (value < 0f) value = 0f;
            if (value > 100f) value = 100f;
            this.value = value;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (!super.isVisible()) return;
            if (this.value == 0f) return;
            batch.flush();
            Rectangle scissor = new Rectangle();
            Rectangle clipBounds = new Rectangle(super.getX(), super.getY(), super.getWidth() * (this.value / 100f), super.getHeight());
            ScissorStack.calculateScissors(this.camera, 0, 0, AppConfig.VIEWPORT_WIDTH, AppConfig.VIEWPORT_HEIGHT, batch.getTransformMatrix(), clipBounds, scissor);
            boolean result = ScissorStack.pushScissors(scissor);
            super.draw(batch, parentAlpha);
            batch.flush();
            if (result) ScissorStack.popScissors();
            batch.flush();
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            this.region.setBounds(0f, 0f, width, height);
            super.setBounds(x, y, width, height);
        }
    }

    private static class LobbyButton extends Button {
        private final Lobby stage;
        private final SlotsType type;
        private InstallManager manager;
        private int state;
        private int progress;
        private boolean installed;
        private PixelLabel text;
        private float update_timer;
        private GreyscaleActor background;
        private DrawableActor dl_icon;
        private DrawableActor cl_icon;
        private DrawableActor dl_barb;
        private ProgressBar dl_bar;

        private LobbyButton(ButtonStyle style, TextureAtlas atlas, SlotsType type, Lobby parent) {
            super(style);
            this.type = type;
            this.stage = parent;
            this.manager = null;
            this.state = 0;
            this.progress = 0;
            this.installed = false;
            this.update_timer = 0f;
            this.background = GreyscaleActor.newInstance(atlas.findRegion(type.toLowerString()));
            super.addActor(this.background);
            this.dl_icon = DrawableActor.newInstance(atlas.findRegion("dl_download"));
            super.addActor(this.dl_icon);
            this.cl_icon = DrawableActor.newInstance(atlas.findRegion("dl_cancel"));
            super.addActor(this.cl_icon);
            this.dl_barb = DrawableActor.newInstance(atlas.findRegion("dl_progress_empty"));
            super.addActor(this.dl_barb);
            this.dl_bar = ProgressBar.newInstance(atlas.findRegion("dl_progress_full"), parent.getViewport().getCamera());
            super.addActor(this.dl_bar);
            this.text = PixelLabel.newInstance("", "Taurus.ttf", 20, new Color(.11f, .73f, .97f, 1f));
            super.addActor(this.text);
        }

        public static LobbyButton newInstance(TextureAtlas atlas, SlotsType type, Lobby parent) {
            ButtonStyle style = new ButtonStyle();
            style.pressedOffsetY = -2f;
            LobbyButton instance = new LobbyButton(style, atlas, type, parent);
            instance.installed = FilesList.installed(type);
            instance.updateState();
            return instance;
        }

        public boolean addListener(ClickListener listener) {
            return super.addListener(new LobbyButtonListener(listener));
        }

        private void updateState() {
            if (this.installed) {
                this.background.greyscale = false;
                this.dl_icon.setVisible(false);
                this.cl_icon.setVisible(false);
                this.dl_barb.setVisible(false);
                this.dl_bar.setVisible(false);
                this.text.setVisible(false);
            } else {
                if (this.state == 0) {
                    this.background.greyscale = true;
                    this.dl_icon.setVisible(true);
                    this.cl_icon.setVisible(false);
                    this.dl_barb.setVisible(false);
                    this.dl_bar.setVisible(false);
                    this.text.setVisible(false);
                } else {
                    this.dl_bar.setProgress(this.progress);
                    this.background.greyscale = true;
                    this.dl_icon.setVisible(false);
                    this.cl_icon.setVisible(true);
                    this.dl_barb.setVisible(true);
                    this.dl_bar.setVisible(true);
                    this.text.setVisible(true);
                    synchronized (this.text) {
                        if (this.update_timer > .3f) {
                            if (this.state == 1) {
                                this.text.setText(Connection.getDefaultI18N().format("lobby_downloading", this.progress));
                            } else
                                this.text.setText(Connection.getDefaultI18N().format("lobby_installing", this.progress));
                            this.update_timer = 0f;
                        }
                    }
                }
            }
        }

        @Override
        public void act(float delta) {
            if (this.update_timer < 1f)
                this.update_timer += delta;
            super.act(delta);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            synchronized (this.text) {
                super.draw(batch, parentAlpha);
            }
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            this.background.setBounds(0f, 0f, width, height);
            this.text.setBounds(.05f * width, -.1f * height, .9f * width, .2f * height);
            this.text.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            this.dl_icon.setBounds(.25f * width, .25f * height, .5f * width, .5f * height);
            this.cl_icon.setBounds(.6f * width, .6f * height, .3f * width, .3f * height);
            this.dl_barb.setBounds(.05f * width, .07f * height, .9f * width, .13f * height);
            this.dl_bar.setBounds(.05f * width, .07f * height, .9f * width, .13f * height);
            super.setBounds(x, y, width, height);
        }

        private class LobbyButtonListener extends ClickListener {
            private final ClickListener listener;

            public LobbyButtonListener(ClickListener listener) {
                this.listener = listener;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!LobbyButton.this.installed) {
                    if (LobbyButton.this.manager == null) {
                        LobbyButton.this.stage.showDownloadDialog(new DialogClosedListener() {
                            @Override
                            public void ok() {
                                LobbyButton.this.manager = InstallManager.install(
                                        LobbyButton.this.type,
                                        new InstallManager.InstallHandler() {
                                            @Override
                                            public void progress(float percent) {
                                                LobbyButton.this.progress = (int) percent;
                                                LobbyButton.this.updateState();
                                            }

                                            @Override
                                            public void extract_started() {
                                                LobbyButton.this.state = 2;
                                                LobbyButton.this.updateState();
                                            }

                                            @Override
                                            public void extract_completed() {
                                                LobbyButton.this.state = 0;
                                                LobbyButton.this.manager = null;
                                                LobbyButton.this.installed = true;
                                                LobbyButton.this.updateState();
                                            }

                                            @Override
                                            public void extract_cancelled() {
                                                LobbyButton.this.state = 0;
                                                LobbyButton.this.updateState();
                                            }

                                            @Override
                                            public void download_started() {
                                                LobbyButton.this.state = 1;
                                                LobbyButton.this.updateState();
                                            }

                                            @Override
                                            public void download_completed() {
                                                // Nothing need to do
                                            }

                                            @Override
                                            public void download_cancelled() {
                                                LobbyButton.this.state = 0;
                                                LobbyButton.this.manager = null;
                                                LobbyButton.this.updateState();
                                            }
                                        }
                                );
                            }

                            @Override
                            public void cancel() {
                                // Nothing need to do
                            }
                        });
                    } else LobbyButton.this.manager.cancel();
                } else if (this.listener != null) {
                    this.listener.clicked(event, x, y);
                }
                super.clicked(event, x, y);
            }
        }
    }

}
