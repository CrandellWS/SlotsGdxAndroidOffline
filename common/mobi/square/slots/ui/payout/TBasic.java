package mobi.square.slots.ui.payout;

import mobi.square.slots.api.Connection;
import mobi.square.slots.config.AppConfig;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.GameHeader;
import mobi.square.slots.ui.PixelLabel;
import mobi.square.slots.ui.Resizable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public abstract class TBasic extends Group implements Resizable {

	private static final float SHOW_SPEED = 2000f;

	protected int state;

	protected TBasic() {
		this.state = 0;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		switch (this.state) {
			case 1:
				super.setY(super.getY() + delta * SHOW_SPEED);
				if (super.getY() >= 0f) {
					super.setY(0f);
					super.setVisible(true);
					this.state = 3;
				}
				break;
			case 2:
				super.setY(super.getY() - delta * SHOW_SPEED);
				float hide_y = GameHeader.HEADER_HEIGHT - AppConfig.VIEWPORT_HEIGHT;
				if (super.getY() <= hide_y) {
					super.setY(hide_y);
					super.setVisible(false);
					this.state = 0;
				}
				break;
			default:
				break;
		}
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
		super.setY(GameHeader.HEADER_HEIGHT - AppConfig.VIEWPORT_HEIGHT);
		super.setVisible(false);
	}

	protected static class TableTitle extends PixelLabel {
		private final float[] bounds;
		private final TextureRegionDrawable background;
		private final float[] offset;
		private TableTitle(LabelStyle style, TextureRegion background, float x, float y, float width, float height) {
			super(Connection.getString("payout_table_title"), style);
			this.bounds = new float[4];
			this.background = new TextureRegionDrawable(background);
			this.offset = new float[] { x, y, width, height };
		}
		public static TableTitle newInstance(TextureRegion background, Color color, float x, float y, float width, float height) {
			LabelStyle style = new LabelStyle();
			style.font = FontsFactory.getAsync("arial.ttf", 36);
			style.fontColor = color;
			TableTitle instance = new TableTitle(style, background, x, y, width, height);
			instance.setAlignment(Align.left, Align.center);
			return instance;
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			this.background.draw(batch, this.bounds[0], this.bounds[1], this.bounds[2], this.bounds[3]);
			super.draw(batch, parentAlpha);
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			this.bounds[0] = x;
			this.bounds[1] = y;
			this.bounds[2] = width;
			this.bounds[3] = height;
			super.setBounds(x + this.offset[0] * width, y + this.offset[1] * height, this.offset[2] * width, this.offset[3] * height);
		}
	}

	protected static class TableScroll extends ScrollPane {
		private final Table table;
		private TableScroll(Table table) {
			super(table);
			this.table = table;
		}
		public static TableScroll newInstance() {
			Table table = new Table();
			TableScroll instance = new TableScroll(table);
			instance.setScrollingDisabled(true, false);
			instance.setFadeScrollBars(false);
			return instance;
		}
		public Group newRow(float height, float padBottom) {
			Cell<?> cell = this.table.add();
			float width = super.getWidth();
			cell.width(width);
			cell.height(height);
			cell.padBottom(padBottom);
			Group group = new Group();
			group.setBounds(0f, 0f, width, height);
			cell.setActor(group);
			this.table.row();
			return group;
		}
	}

	protected static class TableSymbol extends Actor {
		private final TextureRegionDrawable background;
		private final TextureRegionDrawable symbol;
		private final float[] offset;
		private TableSymbol(TextureRegionDrawable background, TextureRegionDrawable symbol, float x, float y, float width, float height) {
			super();
			this.background = background;
			this.symbol = symbol;
			this.offset = new float[] { x, y, width, height };
		}
		public static TableSymbol newInstance(TextureRegionDrawable background, TextureRegionDrawable symbol) {
			TableSymbol instance = new TableSymbol(background, symbol, .03f, .03f, .94f, .94f);
			return instance;
		}
		public static TableSymbol newInstance(TextureRegionDrawable background, TextureRegionDrawable symbol, float x, float y, float width, float height) {
			TableSymbol instance = new TableSymbol(background, symbol, x, y, width, height);
			return instance;
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			float x = super.getX();
			float y = super.getY();
			float width = super.getWidth();
			float height = super.getHeight();
			if (this.background != null) this.background.draw(batch, x, y, width, height);
			this.symbol.draw(batch, x + this.offset[0] * width, y + this.offset[1] * height, this.offset[2] * width, this.offset[3] * height);
		}
	}

}
