package mobi.square.slots.b.crazymonkey;

import mobi.square.slots.app.Game;
import mobi.square.slots.screens.SBasic;

public class SCrazyMonkeyBoxes extends SBasic {

    public SCrazyMonkeyBoxes() {
        super(null);
        super.stage = new CrazyMonkeyBoxes(this);
    }

    public SCrazyMonkeyBoxes(Game parent) {
        super(parent);
        super.stage = new CrazyMonkeyBoxes(this);
    }

    @Override
    public void show() {
        if (super.stage == null)
            super.stage = new CrazyMonkeyBoxes(this);
        super.show();
    }

    @Override
    public TextureInfo[] getTextureList() {
        if (super.texture_ext != null) return super.texture_ext;
        return super.getTextureList(new TextureInfo[]{
                new TextureInfo("CrazyMonkey/CrazyMonkeyBonusBg.jpg", false)
        });
    }

    @Override
    public AtlasInfo[] getAtlasList() {
        if (super.atlas_ext != null) return super.atlas_ext;
        return super.getAtlasList(new AtlasInfo[]{
                new AtlasInfo("CrazyMonkey/CrazyMonkeyBonus.pack", false)
        });
    }

    @Override
    public FontInfo[] getFontsList() {
        if (super.fonts_ext != null) return super.fonts_ext;
        return super.getFontsList(new FontInfo[]{
                new FontInfo("arialbd.ttf", 30f, false)
        });
    }

}
