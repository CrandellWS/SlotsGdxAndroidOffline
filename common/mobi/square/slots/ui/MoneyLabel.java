package mobi.square.slots.ui;

import mobi.square.slots.api.Connection;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.utils.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MoneyLabel extends Group implements Resizable {

	private final AtlasRegion background;
	private final AtlasRegion money_icon;
	private final Button bank_button;
	private final PixelLabel bank_button_label;
	private final PixelLabel money_label;

	private final float[] background_bounds;
	private final float[] money_icon_bounds;

	private static final int MIN_CHANGE_VALUE = 100;
	private static final float MAX_CHANGE_TIME = 1.5f;

	private int target_money;
	private int change_speed;
	private int money;

	private MoneyLabel(TextureAtlas atlas) {
		this.background_bounds = new float[4];
		this.money_icon_bounds = new float[4];
		this.background = atlas.findRegion("money_frame");
		this.money_icon = atlas.findRegion("money_icon");
		ButtonStyle style = new ButtonStyle();
		style.up = new TextureRegionDrawable(atlas.findRegion("button_bank_active"));
		style.down = new TextureRegionDrawable(atlas.findRegion("button_bank_pressed"));
		style.disabled = new TextureRegionDrawable(atlas.findRegion("button_bank_inactive"));
		style.pressedOffsetY = -2f;
		this.bank_button = new Button(style);
		LabelStyle ls = new LabelStyle();
		ls.font = FontsFactory.getAsync("Junegull.ttf", 22);
		ls.fontColor = new Color(1f, 1f, 1f, 1f);
		this.bank_button_label = new PixelLabel(Connection.getString("header_button_buy"), ls);
		this.bank_button_label.setAlignment(Align.center, Align.center);
		this.bank_button_label.setPosition(5, 5);
		this.bank_button.addActor(this.bank_button_label);
		super.addActor(this.bank_button);
		ls = new LabelStyle();
		ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
		ls.fontColor = new Color(1f, 0.914f, 0.349f, 1f);
		this.money_label = new PixelLabel("0", ls);
		this.money_label.setAlignment(Align.center, Align.center);
		super.addActor(this.money_label);
		this.money = 0;
	}

	public static MoneyLabel newInstance(TextureAtlas atlas) {
		MoneyLabel instance = new MoneyLabel(atlas);
		instance.updateBounds();
		return instance;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (this.target_money != this.money) {
			if (this.change_speed == 0)
				this.change_speed = this.evalChangeSpeed(this.money, this.target_money);
			int value = (int)((float)this.change_speed * delta);
			this.money += value != 0 ? value : this.change_speed < 0 ? -1 : 1;
			if (this.change_speed < 0) {
				if (this.money <= this.target_money) {
					this.money = this.target_money;
					this.change_speed = 0;
				}
			} else {
				if (this.money >= this.target_money) {
					this.money = this.target_money;
					this.change_speed = 0;
				}
			}
			this.money_label.setText(utils.splitNumber(this.money, 3));
		}
	}

	private int evalChangeSpeed(int current, int target) {
		int value = (int)((float)(target - current) / MAX_CHANGE_TIME);
		if (value < 0) {
			return value > -MIN_CHANGE_VALUE ? -MIN_CHANGE_VALUE : value;
		} else if (value > 0) {
			return value < MIN_CHANGE_VALUE ? MIN_CHANGE_VALUE : value;
		} else return 0;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(
			this.background,
			this.background_bounds[0],
			this.background_bounds[1],
			this.background_bounds[2],
			this.background_bounds[3]
		);
		batch.draw(
			this.money_icon,
			this.money_icon_bounds[0],
			this.money_icon_bounds[1],
			this.money_icon_bounds[2],
			this.money_icon_bounds[3]
		);
		super.draw(batch, parentAlpha);
	}

	@Override
	public void resize(int width, int height) {
		this.money_label.resize(width, height);
		this.bank_button_label.resize(width, height);
	}

	public Button getBankButton() {
		return this.bank_button;
	}

	public void setMoney(int money) {
		this.money = money;
		this.target_money = money;
		this.change_speed = 0;
		this.money_label.setText(utils.splitNumber(money, 3));
	}

	public void changeMoney(int money) {
		this.target_money = money;
		this.change_speed = this.evalChangeSpeed(this.money, money);
	}

	public void changeMoneyNoAnimation(int money) {
		this.setMoney(money);
	}

	@Override
	public void setBounds(float x, float y, float width, float height) {
		super.setBounds(x, y, width, height);
		this.updateBounds();
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		this.updateBounds();
	}

	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		this.updateBounds();
	}

	public void updateBounds() {
		float x = super.getX();
		float y = super.getY();
		float width = super.getWidth();
		float height = super.getHeight();
		int icon_width = this.money_icon.getRegionWidth();
		int icon_height = this.money_icon.getRegionHeight();
		float icon_aspect = (float)icon_width / (float)icon_height;
		this.money_icon_bounds[0] = x;
		this.money_icon_bounds[1] = y;
		this.money_icon_bounds[2] = height * icon_aspect;
		this.money_icon_bounds[3] = height;
		this.background_bounds[0] = x + this.money_icon_bounds[2] * .5f;
		this.background_bounds[1] = y + height * .05f;
		this.background_bounds[2] = width - this.money_icon_bounds[2] * .5f;
		this.background_bounds[3] = height * .9f;
		this.bank_button.setBounds(
			width * .688f,
			height * .15f,
			width * .3f,
			height * .75f
		);
		this.bank_button_label.setBounds(0f, 0f, this.bank_button.getWidth(), this.bank_button.getHeight());
		this.money_label.setBounds(this.money_icon_bounds[2], 0f, this.bank_button.getX() - this.money_icon_bounds[2], height);
		this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

}
