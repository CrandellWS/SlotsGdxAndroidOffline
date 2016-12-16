package mobi.square.slots.screens.bonus;

import mobi.square.slots.app.Game;
import mobi.square.slots.screens.SBasic;
import mobi.square.slots.stages.bonus.GarageBoxes;

public class SGarageBoxes extends SBasic {

	public SGarageBoxes() {
		super(null);
		super.stage = new GarageBoxes(this);
	}

	public SGarageBoxes(Game parent) {
		super(parent);
		super.stage = new GarageBoxes(this);
	}

	@Override
	public void show() {
		if (super.stage == null)
			super.stage = new GarageBoxes(this);
		super.show();
	}

	@Override
	public TextureInfo[] getTextureList() {
		if (super.texture_ext != null) return super.texture_ext;
		return super.getTextureList(new TextureInfo[] {
			new TextureInfo("atlas/GarageBoxesBg.jpg", false)
		});
	}

	@Override
	public AtlasInfo[] getAtlasList() {
		if (super.atlas_ext != null) return super.atlas_ext;
		return super.getAtlasList(new AtlasInfo[] {
			new AtlasInfo("atlas/GarageCommon.pack", false),
			new AtlasInfo("atlas/GarageBoxes.pack", false)
		});
	}

	@Override
	public FontInfo[] getFontsList() {
		if (super.fonts_ext != null) return super.fonts_ext;
		return super.getFontsList(new FontInfo[] {
			new FontInfo("arial.ttf", 14f, false),
			new FontInfo("arial.ttf", 18f, false),
			new FontInfo("Junegull.ttf", 36f, false)
		});
	}

}
