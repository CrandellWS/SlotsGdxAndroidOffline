package mobi.square.slots.b.rockclimber;

import mobi.square.slots.app.Game;
import mobi.square.slots.config.SoundList;
import mobi.square.slots.screens.SBasic;

public class SRiskClimber extends SBasic {

    public SRiskClimber() {
        super(null);
        super.stage = new RiskClimber(this);
    }

    public SRiskClimber(Game parent) {
        super(parent);
        super.stage = new RiskClimber(this);
    }

    @Override
    public void show() {
        if (super.stage == null)
            super.stage = new RiskClimber(this);
        super.show();
    }

    @Override
    public TextureInfo[] getTextureList() {
        if (super.texture_ext != null) return super.texture_ext;
        return super.getTextureList(new TextureInfo[]{
                new TextureInfo("RockClimber/RockClimberRiskBg.jpg", false)
        });
    }

    @Override
    public AtlasInfo[] getAtlasList() {
        if (super.atlas_ext != null) return super.atlas_ext;
        return super.getAtlasList(new AtlasInfo[]{
                new AtlasInfo("RockClimber/RockClimberCards.pack", false),
                new AtlasInfo("RockClimber/RockClimberRisk.pack", false),
                new AtlasInfo("RockClimber/RockClimberAni.pack", false)
        });
    }

    @Override
    public FontInfo[] getFontsList() {
        if (super.fonts_ext != null) return super.fonts_ext;
        return super.getFontsList(new FontInfo[]{
                new FontInfo("arialbd.ttf", 14f, false),
                new FontInfo("arialbd.ttf", 20f, false),
                new FontInfo("courbd.ttf", 16f, false)
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
