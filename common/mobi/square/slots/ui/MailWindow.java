package mobi.square.slots.ui;

import mobi.square.slots.api.Connection;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.MessageWindow.CloseButton;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MailWindow extends Window implements Resizable {

	private final CloseButton close_button;
	private final PixelLabel title_label;
	private final PixelLabel message_label;
	private final PixelLabel money_label;
	private MailWindowListener listener;

	private MailWindow(WindowStyle style, TextureAtlas atlas) {
		super("", style);
		this.close_button = CloseButton.newInstance(atlas);
		this.close_button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (close_button.isDisabled()) return;
				if (listener != null) listener.okButtonClicked();
				hide();
			}
		});
		super.addActor(this.close_button);
		this.title_label = PixelLabel.newInstance(Connection.getString("mail_window_title"), "Junegull.ttf", 30, Color.WHITE, Align.center);
		super.addActor(this.title_label);
		this.message_label = PixelLabel.newInstance("", "Taurus.ttf", 28, Color.WHITE, Align.center);
		this.message_label.setWrap(true);
		super.addActor(this.message_label);
		this.money_label = PixelLabel.newInstance("", "Junegull.ttf", 48, Color.YELLOW, Align.center);
		super.addActor(this.money_label);
		this.listener = null;
	}

	public static MailWindow newInstance(TextureAtlas atlas) {
		WindowStyle style = new WindowStyle();
		style.background = new TextureRegionDrawable(atlas.findRegion("bank_background"));
		style.titleFont = FontsFactory.getAsync("Junegull.ttf", 30);
		style.titleFontColor = Color.WHITE;
		MailWindow instance = new MailWindow(style, atlas);
		instance.setBounds(194f, 60f, 628f, 402f);
		instance.setVisible(false);
		instance.setMovable(false);
		instance.setModal(true);
		instance.close_button.setTextOk();
		instance.close_button.setBounds(214f, 24f, 200f, 66f);
		instance.title_label.setBounds(0f, 338f, 628f, 60f);
		instance.message_label.setBounds(80f, 200f, 468f, 120f);
		instance.money_label.setBounds(0f, 110f, 628f, 80f);
		return instance;
	}

	public void show(String message, int money) {
		this.message_label.setText(message);
		this.money_label.setText(String.valueOf(money));
		super.setVisible(true);
	}

	public void hide() {
		super.setVisible(false);
	}

	@Override
	public void resize(int width, int height) {
		this.close_button.resize(width, height);
		this.title_label.resize(width, height);
		this.message_label.resize(width, height);
		this.money_label.resize(width, height);
	}

	public void setListener(MailWindowListener listener) {
		this.listener = listener;
	}

	public interface MailWindowListener {
		public void okButtonClicked();
	}

}
