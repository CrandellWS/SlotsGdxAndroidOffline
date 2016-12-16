package mobi.square.slots.screens.bonus;

import mobi.square.slots.app.Game;
import mobi.square.slots.screens.SBasic;
import mobi.square.slots.stages.bonus.GladiatorsBonus;

public class SGladiatorsBonus extends SBasic {

	public SGladiatorsBonus() {
		super(null);
		super.stage = new GladiatorsBonus(this);
	}

	public SGladiatorsBonus(Game parent) {
		super(parent);
		super.stage = new GladiatorsBonus(this);
	}

	@Override
	public void show() {
		if (super.stage == null)
			super.stage = new GladiatorsBonus(this);
		super.show();
	}

	@Override
	public TextureInfo[] getTextureList() {
		if (super.texture_ext != null) return super.texture_ext;
		return super.getTextureList(new TextureInfo[] {
			new TextureInfo("atlas/GladiatorsBonusBg.jpg", false)
		});
	}

	@Override
	public AtlasInfo[] getAtlasList() {
		if (super.atlas_ext != null) return super.atlas_ext;
		return super.getAtlasList(new AtlasInfo[] {
			new AtlasInfo("atlas/GladiatorsBonus.pack", false)
		});
	}

	@Override
	public FontInfo[] getFontsList() {
		if (super.fonts_ext != null) return super.fonts_ext;
		return super.getFontsList(new FontInfo[] {
			new FontInfo("Junegull.ttf", 34f, false),
			new FontInfo("arial.ttf", 28f, false)
		});
	}

}
