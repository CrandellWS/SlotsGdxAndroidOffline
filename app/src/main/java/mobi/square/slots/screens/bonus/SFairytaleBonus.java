package mobi.square.slots.screens.bonus;

import mobi.square.slots.app.Game;
import mobi.square.slots.screens.SBasic;
import mobi.square.slots.stages.bonus.FairytaleBonus;

public class SFairytaleBonus extends SBasic {

    public SFairytaleBonus() {
        super(null);
        super.stage = new FairytaleBonus(this);
    }

    public SFairytaleBonus(Game parent) {
        super(parent);
        super.stage = new FairytaleBonus(this);
    }

    @Override
    public void show() {
        if (super.stage == null)
            super.stage = new FairytaleBonus(this);
        super.show();
    }

    @Override
    public TextureInfo[] getTextureList() {
        if (super.texture_ext != null) return super.texture_ext;
        return super.getTextureList(new TextureInfo[]{
                new TextureInfo("atlas/FairytaleBonusBg.jpg", false)
        });
    }

    @Override
    public AtlasInfo[] getAtlasList() {
        if (super.atlas_ext != null) return super.atlas_ext;
        return super.getAtlasList(new AtlasInfo[]{
                new AtlasInfo("atlas/FairytaleBonus.pack", false)
        });
    }

    @Override
    public FontInfo[] getFontsList() {
        if (super.fonts_ext != null) return super.fonts_ext;
        return super.getFontsList(new FontInfo[]{
                new FontInfo("Junegull.ttf", 32f, false),
                new FontInfo("arial.ttf", 28f, false)
        });
    }

}
