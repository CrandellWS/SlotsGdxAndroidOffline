package mobi.square.slots.screens.bonus;

import mobi.square.slots.app.Game;
import mobi.square.slots.screens.SBasic;
import mobi.square.slots.stages.bonus.GarageLocks;

public class SGarageLocks extends SBasic {

    public SGarageLocks() {
        super(null);
        super.stage = new GarageLocks(this);
    }

    public SGarageLocks(Game parent) {
        super(parent);
        super.stage = new GarageLocks(this);
    }

    @Override
    public void show() {
        if (super.stage == null)
            super.stage = new GarageLocks(this);
        super.show();
    }

    @Override
    public TextureInfo[] getTextureList() {
        if (super.texture_ext != null) return super.texture_ext;
        return super.getTextureList(new TextureInfo[]{
                new TextureInfo("atlas/GarageBoxesBg.jpg", false),
                new TextureInfo("atlas/GarageLocksBg.jpg", false)
        });
    }

    @Override
    public AtlasInfo[] getAtlasList() {
        if (super.atlas_ext != null) return super.atlas_ext;
        return super.getAtlasList(new AtlasInfo[]{
                new AtlasInfo("atlas/GarageCommon.pack", false),
                new AtlasInfo("atlas/GarageLocks.pack", false)
        });
    }

    @Override
    public FontInfo[] getFontsList() {
        if (super.fonts_ext != null) return super.fonts_ext;
        return super.getFontsList(new FontInfo[]{
                new FontInfo("Junegull.ttf", 36f, false)
        });
    }

}
