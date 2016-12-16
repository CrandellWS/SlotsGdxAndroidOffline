package mobi.square.slots.screens;

import mobi.square.slots.app.Game;
import mobi.square.slots.config.SlotsConfig;
import mobi.square.slots.config.SoundList;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.stages.SinglePlayer;

public class SSinglePlayer extends SBasic {

	private SlotsType type;

	public SSinglePlayer(SlotsType type) {
		super(null);
		super.stage = new SinglePlayer(this, type);
		this.type = type;
	}

	public SSinglePlayer(Game parent, SlotsType type) {
		super(parent);
		super.stage = new SinglePlayer(this, type);
		this.type = type;
	}

	@Override
	public void show() {
		if (super.stage == null)
			super.stage = new SinglePlayer(this, this.type);
		super.show();
	}

	@Override
	public TextureInfo[] getTextureList() {
		if (super.texture_ext != null) return super.texture_ext;
		SlotsConfig conf = SlotsConfig.get(this.type);
		return super.getTextureList(new TextureInfo[] {
			conf.pay_table_bg != null ? new TextureInfo(conf.pay_table_bg, false) : null,
			new TextureInfo(conf.machine_bg, false)
		});
	}

	@Override
	public AtlasInfo[] getAtlasList() {
		if (super.atlas_ext != null) return super.atlas_ext;
		SlotsConfig conf = SlotsConfig.get(this.type);
		return super.getAtlasList(new AtlasInfo[] {
			new AtlasInfo("atlas/Footer.pack", false),
			new AtlasInfo("atlas/Autospin.pack", false),
			conf.pay_table != null ? new AtlasInfo(conf.pay_table, false) : null,
			conf.getLinesAtlas() != null ? new AtlasInfo(conf.getLinesAtlas(), false) : null,
			new AtlasInfo(conf.filename, false)
		});
	}

	@Override
	public FontInfo[] getFontsList() {
		if (super.fonts_ext != null) return super.fonts_ext;
		return super.getFontsList(new FontInfo[] {
			new FontInfo("Junegull.ttf", 15f, false),
			new FontInfo("Junegull.ttf", 20f, false),
			new FontInfo("Junegull.ttf", 32f, false),
			new FontInfo("Junegull.ttf", 36f, false),
			new FontInfo("Taurus.ttf", 16f, false),
			new FontInfo("Taurus.ttf", 18f, false),
			new FontInfo("arial.ttf", 36f, false)
		});
	}

	@Override
	public SoundInfo[] getSoundList() {
		if (super.sound_ext != null) return super.sound_ext;
		return super.getSoundList(new SoundInfo[] {
			new SoundInfo(SoundList.MACHINE_START, false),
			new SoundInfo(SoundList.REEL_STOP, false),
			new SoundInfo(SoundList.START_SPIN, false),
			new SoundInfo(SoundList.LINE_WINS_9X[0], false),
			new SoundInfo(SoundList.LINE_WINS_9X[1], false),
			new SoundInfo(SoundList.LINE_WINS_9X[2], false),
			new SoundInfo(SoundList.LINE_WINS_9X[3], false),
			new SoundInfo(SoundList.LINE_WINS_9X[4], false),
			new SoundInfo(SoundList.LINE_WINS_9X[5], false),
			new SoundInfo(SoundList.LINE_WINS_9X[6], false),
			new SoundInfo(SoundList.LINE_WINS_9X[7], false),
			new SoundInfo(SoundList.LINE_WINS_9X[8], false)
		});
	}

}
