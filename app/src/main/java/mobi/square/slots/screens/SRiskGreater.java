package mobi.square.slots.screens;

import mobi.square.slots.app.Game;
import mobi.square.slots.config.SoundList;
import mobi.square.slots.stages.RiskGreater;

public class SRiskGreater extends SBasic {

    public SRiskGreater() {
        super(null);
        super.stage = new RiskGreater(this);
    }

    public SRiskGreater(Game parent) {
        super(parent);
        super.stage = new RiskGreater(this);
    }

    @Override
    public void show() {
        if (super.stage == null)
            super.stage = new RiskGreater(this);
        super.show();
    }

    @Override
    public TextureInfo[] getTextureList() {
        if (super.texture_ext != null) return super.texture_ext;
        return super.getTextureList(new TextureInfo[]{
                new TextureInfo("atlas/RiskScreenBg.jpg", false)
        });
    }

    @Override
    public AtlasInfo[] getAtlasList() {
        if (super.atlas_ext != null) return super.atlas_ext;
        return super.getAtlasList(new AtlasInfo[]{
                new AtlasInfo("atlas/RiskCards.pack", false),
                new AtlasInfo("atlas/RiskScreen.pack", false)
        });
    }

    @Override
    public FontInfo[] getFontsList() {
        if (super.fonts_ext != null) return super.fonts_ext;
        return super.getFontsList(new FontInfo[]{
                new FontInfo("Taurus.ttf", 32f, false),
        });
    }

    @Override
    public SoundInfo[] getSoundList() {
        if (super.sound_ext != null) return super.sound_ext;
        return super.getSoundList(new SoundInfo[]{
                new SoundInfo(SoundList.FLIP_CARD, false)
        });
    }

}
