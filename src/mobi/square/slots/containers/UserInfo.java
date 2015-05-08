package mobi.square.slots.containers;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.utils.Base64Coder;

public class UserInfo {
	
	private String id = null;
	private String name = null;
	private String image = null;
	private int value = 0;
	private int score = 0;
	
	private Texture image_tex = null;
	
	// Public Methods
	
	public Texture getTexture() {
		if (this.getImage() == null) return null;
		if (this.image_tex == null) {
			byte[] image_bytes = Base64Coder.decode(this.getImage());
			Pixmap pixmap = new Pixmap(image_bytes, 0, image_bytes.length);
			this.image_tex = new Texture(pixmap);
			this.image_tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			pixmap.dispose();
		}
		return this.image_tex;
	}
	
	public void dispose() {
		if (this.image_tex != null) {
			this.image_tex.dispose();
			this.image_tex = null;
		}
	}
	
	// Getters & Setters
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return this.image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getScore() {
		return this.score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
}
