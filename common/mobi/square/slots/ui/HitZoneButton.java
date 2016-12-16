package mobi.square.slots.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

public class HitZoneButton extends Button {

	private final List<Vector2> polyline;
	private final Vector2 origin_size;

	public HitZoneButton() {
		super();
		this.polyline = new ArrayList<Vector2>();
		this.origin_size = new Vector2(.0f, .0f);
	}

	public void addHitPoint(float x, float y) {
		this.polyline.add(new Vector2(x, y));
	}

	public void addHitPoint(float x, float y, float origin_height) {
		this.polyline.add(new Vector2(x, origin_height - y - 1));
	}

	public void setOriginSize(float width, float height) {
		this.origin_size.x = width;
		this.origin_size.y = height;
	}

	public void setBounds(Rectangle rect) {
		super.setBounds(rect.x, rect.y, rect.width, rect.height);
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		if (x < 0 || x >= super.getWidth() || y < 0 || y >= super.getHeight()) return null;
		if (this.polyline.size() < 3) return null;
		int intersections = 0;
		Vector2 a = new Vector2(x, y);
		Vector2 b = new Vector2(x + super.getWidth() + 1.f, y + super.getHeight() + 1.f);
		float kx = this.origin_size.x / super.getWidth();
		float ky = this.origin_size.y / super.getHeight();
		Vector2 c, d;
		for (int i = this.polyline.size() - 1; i >= 0; --i) {
			c = this.polyline.get(i);
			c = new Vector2(c.x / kx, c.y / ky);
			d = this.polyline.get(i == 0 ? this.polyline.size() - 1 : i - 1);
			d = new Vector2(d.x / kx, d.y / ky);
			if (this.intersect(a, b, c, d)) intersections++;
		}
		return intersections % 2 != 0 ? this : null;
	}

	private boolean intersect(Vector2 a, Vector2 b, Vector2 c, Vector2 d) {
		return this.intersect(a.x, b.x, c.x, d.x) &&
			this.intersect(a.y, b.y, c.y, d.y) &&
			this.area(a, b, c) * this.area(a, b, d) <= 0f &&
			this.area(c, d, a) * this.area(c, d, b) <= 0f;
	}

	private boolean intersect(float a, float b, float c, float d) {
		if (a > b) {
			float t = a;
			a = b;
			b = t;
		}
		if (c > d) {
			float t = c;
			c = d;
			d = t;
		}
		return Math.max(a, c) <= Math.min(b, d);
	}

	private float area(Vector2 a, Vector2 b, Vector2 c) {
		return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
	}

}
