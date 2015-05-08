package mobi.square.slots.ui;

import mobi.square.slots.api.Connection;
import mobi.square.slots.tools.FontsFactory;
import mobi.square.slots.ui.MessageWindow.CloseButton;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class DownloadDialog extends Window implements Resizable {

	private final CloseButton ok_button;
	private final CloseButton cancel_button;
	private final PixelLabel label_title;
	private final PixelLabel label_message;

	private DialogClosedListener listener;

	private DownloadDialog(WindowStyle style, TextureAtlas atlas) {
		super("", style);
		this.listener = null;
		this.ok_button = CloseButton.newInstance(atlas);
		this.ok_button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (ok_button.isDisabled()) return;
				hide();
				if (listener != null) {
					listener.ok();
				}
			}
		});
		this.cancel_button = CloseButton.newInstance(atlas);
		this.cancel_button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (cancel_button.isDisabled()) return;
				hide();
				if (listener != null) {
					listener.cancel();
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
		this.label_message.setWrap(true);
		super.addActor(this.label_message);
		super.addActor(this.label_title);
		super.addActor(this.cancel_button);
		super.addActor(this.ok_button);
	}

	public static DownloadDialog newInstance(TextureAtlas atlas) {
		WindowStyle style = new WindowStyle();
		style.background = new TextureRegionDrawable(atlas.findRegion("message_background"));
		style.titleFont = FontsFactory.getAsync("Junegull.ttf", 30);
		style.titleFontColor = Color.WHITE;
		DownloadDialog instance = new DownloadDialog(style, atlas);
		instance.setBounds(162f, 100f, 702f, 330f);
		instance.setVisible(false);
		instance.setMovable(false);
		instance.setModal(true);
		instance.ok_button.setTextOk();
		instance.ok_button.setBounds(170f, 26f, 160f, 64f);
		instance.cancel_button.setTextClose();
		instance.cancel_button.setBounds(370f, 26f, 160f, 64f);
		instance.label_message.setBounds(20f, 90f, 670f, 140f);
		instance.label_title.setBounds(50f, 260f, 640f, 50f);
		return instance;
	}

	@Override
	public void resize(int width, int height) {
		this.label_title.resize(width, height);
		this.label_message.resize(width, height);
		this.cancel_button.resize(width, height);
		this.ok_button.resize(width, height);
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

	public void setListener(DialogClosedListener listener) {
		this.listener = listener;
	}

	public interface DialogClosedListener {
		public void ok();
		public void cancel();
	}

}
