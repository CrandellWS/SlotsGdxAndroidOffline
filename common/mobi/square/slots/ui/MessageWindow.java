package mobi.square.slots.ui;

import mobi.square.slots.api.Connection;
import mobi.square.slots.tools.FontsFactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MessageWindow extends Window implements Resizable {

	private final CloseButton close_button;
	private final PixelLabel label_title;
	private final PixelLabel label_message;

	private MessageClosedListener listener;

	private MessageWindow(WindowStyle style, TextureAtlas atlas) {
		super("", style);
		this.listener = null;
		this.close_button = CloseButton.newInstance(atlas);
		this.close_button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (close_button.isDisabled()) return;
				hide();
				if (listener != null) {
					listener.messageClosed();
				}
			}
		});
		LabelStyle ls = new LabelStyle();
		ls.font = FontsFactory.getAsync("Junegull.ttf", 30);
		ls.fontColor = Color.WHITE;
		this.label_title = new PixelLabel("", ls);
		this.label_title.setAlignment(Align.left, Align.center);
		ls = new LabelStyle();
		ls.font = FontsFactory.getAsync("Taurus.ttf", 28);
		ls.fontColor = Color.WHITE;
		this.label_message = new PixelLabel("", ls);
		this.label_message.setAlignment(Align.center | Align.top, Align.center);
		super.addActor(this.label_message);
		super.addActor(this.label_title);
		super.addActor(this.close_button);
	}

	public static MessageWindow newInstance(TextureAtlas atlas) {
		WindowStyle style = new WindowStyle();
		style.background = new TextureRegionDrawable(atlas.findRegion("message_background"));
		style.titleFont = FontsFactory.getAsync("Junegull.ttf", 30);
		style.titleFontColor = Color.WHITE;
		MessageWindow instance = new MessageWindow(style, atlas);
		instance.setBounds(162f, 100f, 702f, 330f);
		instance.setVisible(false);
		instance.setMovable(false);
		instance.setModal(true);
		instance.close_button.setTextOk();
		instance.close_button.setBounds(270f, 26f, 160f, 64f);
		instance.label_message.setBounds(20f, 90f, 670f, 140f);
		instance.label_title.setBounds(50f, 260f, 640f, 50f);
		return instance;
	}

	@Override
	public void resize(int width, int height) {
		this.label_title.resize(width, height);
		this.label_message.resize(width, height);
		this.close_button.resize(width, height);
	}

	public void show(String title, String message) {
		if (title != null) {
			this.label_title.setText(Connection.getString(title));
			this.label_title.setVisible(true);
		} else this.label_title.setVisible(false);
		this.label_message.setText(Connection.getString(message));
		super.setVisible(true);
	}

	public void hide() {
		super.setVisible(false);
	}

	public void setListener(MessageClosedListener listener) {
		this.listener = listener;
	}

	public interface MessageClosedListener {
		public void messageClosed();
	}

	public static class CloseButton extends Button implements Resizable {
		private final PixelLabel label;
		private CloseButton(ButtonStyle style) {
			super(style);
			LabelStyle ls = new LabelStyle();
			ls.font = FontsFactory.getAsync("Junegull.ttf", 24);
			ls.fontColor = Color.WHITE;
			this.label = new PixelLabel("", ls);
			this.label.setAlignment(Align.center, Align.center);
			super.addActor(this.label);
		}
		public static CloseButton newInstance(TextureAtlas atlas) {
			ButtonStyle style = new ButtonStyle();
			style.up = new TextureRegionDrawable(atlas.findRegion("button_close_active"));
			style.down = new TextureRegionDrawable(atlas.findRegion("button_close_pressed"));
			style.disabled = new TextureRegionDrawable(atlas.findRegion("button_close_inactive"));
			style.pressedOffsetY = -2f;
			CloseButton instance = new CloseButton(style);
			return instance;
		}
		public void setTextOk() {
			this.label.setText(Connection.getString("common_ok"));
		}
		public void setTextClose() {
			this.label.setText(Connection.getString("common_close"));
		}
		public void setTextCancel() {
			this.label.setText(Connection.getString("common_cancel"));
		}
		@Override
		public void setBounds(float x, float y, float width, float height) {
			super.setBounds(x, y, width, height);
			this.label.setBounds(0f, 0f, width, height);
		}
		@Override
		public void resize(int width, int height) {
			this.label.resize(width, height);
		}
	}

}
