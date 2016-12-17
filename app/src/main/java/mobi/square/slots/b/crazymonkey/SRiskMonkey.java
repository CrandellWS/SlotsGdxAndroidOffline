package mobi.square.slots.b.crazymonkey;

import mobi.square.slots.app.Game;
import mobi.square.slots.config.SoundList;
import mobi.square.slots.screens.SBasic;

public class SRiskMonkey extends SBasic {

    public SRiskMonkey() {
        super(null);
        super.stage = new RiskMonkey(this);
    }

    public SRiskMonkey(Game parent) {
        super(parent);
        super.stage = new RiskMonkey(this);
    }

    @Override
    public void show() {
        if (super.stage == null)
            super.stage = new RiskMonkey(this);
        super.show();
    }

    @Override
    public TextureInfo[] getTextureList() {
        if (super.texture_ext != null) return super.texture_ext;
        return super.getTextureList(new TextureInfo[]{
                new TextureInfo("CrazyMonkey/CrazyMonkeyRiskBg.jpg", false)
        });
    }

    @Override
    public AtlasInfo[] getAtlasList() {
        if (super.atlas_ext != null) return super.atlas_ext;
        return super.getAtlasList(new AtlasInfo[]{
                new AtlasInfo("CrazyMonkey/CrazyMonkeyCards.pack", false),
                new AtlasInfo("CrazyMonkey/CrazyMonkeyRisk.pack", false),
                new AtlasInfo("CrazyMonkey/CrazyMonkeyAniR.pack", false)
        });
    }

    @Override
    public FontInfo[] getFontsList() {
        if (super.fonts_ext != null) return super.fonts_ext;
        return super.getFontsList(new FontInfo[]{
                new FontInfo("Junegull.ttf", 30f, false),
                new FontInfo("arialbd.ttf", 30f, false),
                new FontInfo("arialbd.ttf", 20f, false),
                new FontInfo("courbd.ttf", 24f, false)
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
