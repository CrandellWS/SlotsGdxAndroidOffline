package mobi.square.slots.classes;

import java.util.LinkedList;
import java.util.List;

import mobi.square.slots.classes.Machine.AnimationInfo;
import mobi.square.slots.config.SlotsConfig;
import mobi.square.slots.enums.SymbolType;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Reel extends Actor {

	int index;
	final Machine parent;
	private float offset;
	private float speed;
	private float a;
	private boolean stopping;
	private final List<Symbol> symbols;
	private final List<SymbolType> target;

	Reel(Machine parent, int index) {
		this.index = index;
		this.parent = parent;
		this.symbols = new LinkedList<Symbol>();
		this.target = new LinkedList<SymbolType>();
		this.offset = .0f;
		this.speed = .0f;
		this.a = .0f;
		this.stopping = false;
	}

	public void initialize(int index) {
		this.index = index;
		this.symbols.clear();
		this.updateSymbols();
	}

	private void updateSymbols() {
		int max = this.parent.config.symbols_count;
		int now = this.symbols.size();
		for (int i = now; i <= max; ++i) {
			if (this.stopping && this.target.size() > 0) {
				int index = this.target.size() - 1;
				this.symbols.add(0, this.createSymbol(this.target.get(index)));
				this.target.remove(index);
			} else {
				this.symbols.add(0, this.createSymbol(this.parent.getRandomSymbol()));
			}
		}
	}

	private Symbol createSymbol(SymbolType type)
		{ return this.createSymbol(type, null); }
	private Symbol createSymbol(SymbolType type, String suffix) {
		Symbol instance = suffix == null ? new Symbol(this, type) : new Symbol(this, type, suffix);
		if (this.parent.animation != null) {
			for (int i = 0; i < this.parent.animation.length; i++) {
				AnimationInfo info = this.parent.animation[i];
				if (info.type == type) {
					//System.out.println("Create animation for symbol " + info.type.toString());
					instance.setAnimation(info.getTextures(), info.frames_count, info.frames_time);
					break;
				}
			}
		}
		return instance;
	}

	List<Symbol> getSymbols() {
		return this.symbols;
	}

	public void start() {
		this.stopping = false;
		this.speed = this.parent.config.rolling_speed;
		this.target.clear();
	}

	public void stop(SymbolType[] symbols) {
		SlotsConfig conf = this.parent.config;
		float s = this.offset + (conf.symbols_count + 1) * (conf.symbol_height + conf.vertical_padding) + conf.symbol_height / 3f;
		float v = conf.min_speed;
		float v0 = conf.rolling_speed;
		this.a = (v * v - v0 * v0) / (2 * s);
		this.target.clear();
		for (int i = 0; i < symbols.length; ++i) {
			this.target.add(symbols[i]);
		}
		this.stopping = true;
	}

	public void playAnimation(int[] index_list) {
		if (index_list == null) return;
		for (int i = 0; i < index_list.length; i++) {
			int index = index_list[i] + 1;
			if (index < this.symbols.size()) {
				this.symbols.get(index).playAnimation();
			}
		}
	}

	public void stopAnimation() {
		for (Symbol symbol : this.symbols) {
			symbol.stopAnimation();
		}
	}

	public void stopNow(SymbolType[] symbols) {
		synchronized (this.symbols) {
			this.a = 0f;
			this.speed = 0f;
			this.offset = 0f;
			this.stopping = false;
			this.symbols.clear();
			for (int i = 0; i < symbols.length; this.symbols.add(this.createSymbol(symbols[i++])));
			this.act(0f);
		}
	}

	public void stopNowBookOfRa(SymbolType[] symbols, SymbolType bonus_symbol) {
		synchronized (this.symbols) {
			this.a = 0f;
			this.speed = 0f;
			this.offset = 0f;
			this.stopping = false;
			this.symbols.clear();
			for (int i = 0; i < symbols.length; i++) {
				if (symbols[i] != bonus_symbol) {
					this.symbols.add(this.createSymbol(symbols[i], "g"));
				} else this.symbols.add(this.createSymbol(symbols[i]));
			}
			this.act(0f);
		}
	}

	@Override
	public void act(float delta) {
		SlotsConfig conf = this.parent.config;
		if (this.speed > .0f) {
			if (this.stopping) {
				this.speed += this.a * delta;
			}
			this.offset -= this.speed * delta;
			if (this.speed < conf.min_speed) {
				// Revert speed
				this.speed = -conf.back_speed;
				this.parent.stopNext(this);
			}// else if (this.speed < conf.min_speed + (conf.rolling_speed - conf.min_speed) / 3f) {}
			float min = conf.symbol_height + conf.vertical_padding;
			if (this.offset < -min) {
				this.symbols.remove(this.symbols.size() - 1);
				this.offset += min;
			}
		} else if (this.speed < .0f) {
			this.offset -= this.speed * delta;
			if (this.offset > .0f) {
				// Total stop
				this.offset = .0f;
				this.speed = .0f;
				this.parent.reelStopped(this);
			}
		}
		synchronized (this.symbols) {
			this.updateSymbols();
			float x, y, width, height;
			float pw = this.parent.getWidth();
			float ph = this.parent.getHeight();
			for (int i = this.symbols.size() - 1, j = 0; i >= 0; --i, ++j) {
				Symbol symbol = this.symbols.get(i);
				x = conf.left_padding[this.index] * pw;
				y = (this.offset + conf.bottom_padding + j * (conf.symbol_height + conf.vertical_padding)) * ph;
				width = conf.symbol_width * pw;
				height = conf.symbol_height * ph;
				symbol.setBounds(x, y, width, height);
				symbol.act(delta);
			}
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		synchronized (this.symbols) {
			for (Symbol symbol : this.symbols) {
				symbol.draw(batch, parentAlpha);
			}
		}
	}

}
