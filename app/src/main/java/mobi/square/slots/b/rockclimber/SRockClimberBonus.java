package mobi.square.slots.b.rockclimber;

import mobi.square.slots.app.Game;
import mobi.square.slots.screens.SBasic;

public class SRockClimberBonus extends SBasic {

    public SRockClimberBonus() {
        super(null);
        super.stage = new RockClimberBonus(this);
    }

    public SRockClimberBonus(Game parent) {
        super(parent);
        super.stage = new RockClimberBonus(this);
    }

    @Override
    public void show() {
        if (super.stage == null)
            super.stage = new RockClimberBonus(this);
        super.show();
    }

    @Override
    public TextureInfo[] getTextureList() {
        if (super.texture_ext != null) return super.texture_ext;
        return super.getTextureList(new TextureInfo[]{
                new TextureInfo("RockClimber/RockClimberSlope.jpg", false),
                new TextureInfo("RockClimber/RockClimberPeak.jpg", false)
        });
    }

    @Override
    public AtlasInfo[] getAtlasList() {
        if (super.atlas_ext != null) return super.atlas_ext;
        return super.getAtlasList(new AtlasInfo[]{
                new AtlasInfo("RockClimber/RockClimberBonus.pack", false)
        });
    }

    @Override
    public FontInfo[] getFontsList() {
        if (super.fonts_ext != null) return super.fonts_ext;
        return super.getFontsList(new FontInfo[]{
                new FontInfo("courbd.ttf", 34f, false),
                new FontInfo("arial.ttf", 42f, false)
        });
    }

}
