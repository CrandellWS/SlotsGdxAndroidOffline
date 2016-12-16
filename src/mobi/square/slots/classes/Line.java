package mobi.square.slots.classes;

import mobi.square.slots.config.SlotsConfig;
import mobi.square.slots.containers.LinesExt.LineExt.Multiplier;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Line {

	Lines parent;
	Color color;

	private final int award;
	final int[] positions;
	final boolean[] highlights;
	private Multiplier[] multipliers;

	Line(Lines parent, Color color, int award) {
		this.parent = parent;
		this.color = color;
		this.positions = new int[parent.parent.REELS_COUNT];
		this.highlights = new boolean[parent.parent.REELS_COUNT];
		this.setMultipliers(new Multiplier[0]);
		this.award = award;
	}

	public void draw(ShapeRenderer renderer, float size) {
		renderer.setColor(this.color);
		int reels_count = this.parent.parent.REELS_COUNT;
		for (int i = 0; i < reels_count; ++i) {
			if (this.highlights[i]) {
				this.drawRect(renderer, i, this.positions[i], size);
			}
		}
		this.drawLines(renderer, size);
	}

	private void drawLines(ShapeRenderer renderer, float size) {
		int i = 0, j;
		float x0, y0, x1, y1;
		float pw = this.parent.parent.getWidth();
		{
			x0 = this.parent.parent.config.lines_left_padding * pw;
			y0 = y1 = this.getSymbolCenterY(this.positions[i], size);
			x1 = this.highlights[i] ? this.getSymbolLeft(i, size) : this.getSymbolCenterX(i, size);
			this.drawLine(renderer, x0, y0, x1, y1);
		}
		for (i = 1; i < this.positions.length; i++) {
			j = i - 1;
			if (this.highlights[j]) {
				x0 = this.getSymbolRight(j, size);
				if (this.positions[j] < this.positions[i]) {
					y0 = this.getSymbolBottom(this.positions[j], size);
				} else if (this.positions[j] > this.positions[i]) {
					y0 = this.getSymbolTop(this.positions[j], size);
				} else {
					y0 = this.getSymbolCenterY(this.positions[j], size);
				}
			} else {
				x0 = this.getSymbolCenterX(j, size);
				y0 = this.getSymbolCenterY(this.positions[j], size);
			}
			if (this.highlights[i]) {
				x1 = this.getSymbolLeft(i, size);
				if (this.positions[j] < this.positions[i]) {
					y1 = this.getSymbolTop(this.positions[i], size);
				} else if (this.positions[j] > this.positions[i]) {
					y1 = this.getSymbolBottom(this.positions[i], size);
				} else {
					y1 = this.getSymbolCenterY(this.positions[j], size);
				}
			} else {
				x1 = this.getSymbolCenterX(i, size);
				y1 = this.getSymbolCenterY(this.positions[i], size);
			}
			this.drawLine(renderer, x0, y0, x1, y1);
		}
		{
			i = this.parent.parent.REELS_COUNT - 1;
			x1 = pw - pw * this.parent.parent.config.lines_right_padding;
			x0 = this.highlights[i] ? this.getSymbolRight(i, size) : this.getSymbolCenterX(i, size);
			y0 = y1 = this.getSymbolCenterY(this.positions[i], size);
			this.drawLine(renderer, x0, y0, x1, y1);
		}
	}

	private void drawLine(ShapeRenderer renderer, float x0, float y0, float x1, float y1) {
		float x = this.parent.parent.getX();
		float y = this.parent.parent.getY();
		renderer.line(x0 + x, y0 + y, x1 + x, y1 + y);
	}

	private float getSymbolLeft(int reel, float size) {
		return this.parent.parent.config.left_padding[reel] * this.parent.parent.getWidth() + size;
	}

	private float getSymbolTop(int position, float size) {
		SlotsConfig config = this.parent.parent.config;
		int count = config.symbols_count - position - 1;
		float top = config.bottom_padding + config.symbol_height;
		for (int i = 0; i < count; ++i) {
			top += config.symbol_height + config.vertical_padding;
		}
		return top * this.parent.parent.getHeight() - size;
	}

	private float getSymbolRight(int reel, float size) {
		return (this.parent.parent.config.left_padding[reel] + this.parent.parent.config.symbol_width) * this.parent.parent.getWidth() - size;
	}

	private float getSymbolBottom(int position, float size) {
		SlotsConfig config = this.parent.parent.config;
		int count = config.symbols_count - position - 1;
		float top = config.bottom_padding;
		for (int i = 0; i < count; ++i) {
			top += config.symbol_height + config.vertical_padding;
		}
		return top * this.parent.parent.getHeight() + size;
	}

	private float getSymbolCenterX(int reel, float size) {
		return this.getSymbolLeft(reel, size) + this.parent.parent.getWidth() * this.parent.parent.config.symbol_width / 2f - size;
	}

	private float getSymbolCenterY(int position, float size) {
		return this.getSymbolTop(position, size) - this.parent.parent.getHeight() * this.parent.parent.config.symbol_height / 2f + size;
	}

	private void drawRect(ShapeRenderer renderer, int reel, int position, float size) {
		SlotsConfig config = this.parent.parent.config;
		float pw = this.parent.parent.getWidth();
		float ph = this.parent.parent.getHeight();
		float width = config.symbol_width * pw;
		float height = config.symbol_height * ph;
		float x = config.left_padding[reel] * pw;
		int count = config.symbols_count - position - 1;
		float y = config.bottom_padding * ph;
		for (int i = 0; i < count; ++i) {
			y += (config.symbol_height + config.vertical_padding) * ph;
		}
		this.drawRect(renderer, x, y, width, height, size);
	}

	private void drawRect(ShapeRenderer renderer, float x, float y, float width, float height, float size) {
		float offset = (float)size / 2f;
		x += this.parent.parent.getX();
		y += this.parent.parent.getY();
		renderer.line(
			x + offset,
			y,
			x + offset,
			y + height
		);
		renderer.line(
			x,
			y + offset,
			x + width,
			y + offset
		);
		renderer.line(
			x,
			y + height - offset,
			x + width,
			y + height - offset
		);
		renderer.line(
			x + width - offset,
			y,
			x + width - offset,
			y + height
		);
	}

	public void setCount(int count, boolean left) {
		int reels_count = this.parent.parent.REELS_COUNT;
		if (left) {
			for (int i = 0; i < count; i++) {
				this.highlights[i] = true;
			}
		} else {
			for (int i = 0; i < count; i++) {
				this.highlights[reels_count - 1 - i] = true;
			}
		}
	}

	public void setHighlights(boolean[] highlights) {
		for (int i = 0; i < highlights.length && i < this.highlights.length; i++) {
			this.highlights[i] = highlights[i];
		}
	}

	public void setPositions(int[] positions) {
		int count = this.positions.length < positions.length ? this.positions.length : positions.length;
		for (int i = 0; i < count; ++i) {
			this.positions[i] = positions[i];
		}
	}

	public Multiplier[] getMultipliers() {
		if (this.multipliers == null)
			this.multipliers = new Multiplier[0];
		return this.multipliers;
	}

	public void setMultipliers(Multiplier[] multipliers) {
		this.multipliers = multipliers;
	}

	public int getAward() {
		return this.award;
	}

}
