package mobi.square.slots.screens.bonus;

import mobi.square.slots.app.Game;
import mobi.square.slots.screens.SBasic;
import mobi.square.slots.stages.bonus.ResidentLocks;

public class SResidentLocks extends SBasic {

	public SResidentLocks() {
		super(null);
		super.stage = new ResidentLocks(this);
	}

	public SResidentLocks(Game parent) {
		super(parent);
		super.stage = new ResidentLocks(this);
	}

	@Override
	public void show() {
		if (super.stage == null)
			super.stage = new ResidentLocks(this);
		super.show();
	}

	@Override
	public TextureInfo[] getTextureList() {
		if (super.texture_ext != null) return super.texture_ext;
		return super.getTextureList(new TextureInfo[] {
			new TextureInfo("atlas/ResidentLocksBg.jpg", false)
		});
	}

	@Override
	public AtlasInfo[] getAtlasList() {
		if (super.atlas_ext != null) return super.atlas_ext;
		return super.getAtlasList(new AtlasInfo[] {
			new AtlasInfo("atlas/ResidentCommon.pack", false),
			new AtlasInfo("atlas/ResidentLocks.pack", false)
		});
	}

	@Override
	public FontInfo[] getFontsList() {
		if (super.fonts_ext != null) return super.fonts_ext;
		return super.getFontsList(new FontInfo[] {
			new FontInfo("Junegull.ttf", 36f, false),
			new FontInfo("arial.ttf", 22f, false)
		});
	}

}
