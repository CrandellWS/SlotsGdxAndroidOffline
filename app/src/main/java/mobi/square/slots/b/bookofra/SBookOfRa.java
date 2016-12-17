package mobi.square.slots.b.bookofra;

import mobi.square.slots.app.Game;
import mobi.square.slots.config.SlotsConfig;
import mobi.square.slots.config.SoundList;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.screens.SBasic;

public class SBookOfRa extends SBasic {

    public SBookOfRa() {
        super(null);
        super.stage = new BookOfRa(this);
    }

    public SBookOfRa(Game parent) {
        super(parent);
        super.stage = new BookOfRa(this);
    }

    @Override
    public void show() {
        if (super.stage == null)
            super.stage = new BookOfRa(this);
        super.show();
    }

    @Override
    public TextureInfo[] getTextureList() {
        if (super.texture_ext != null) return super.texture_ext;
        return super.getTextureList(new TextureInfo[]{
                new TextureInfo(SlotsConfig.get(SlotsType.BOOK_OF_RA).pay_table_bg, false),
                new TextureInfo(SlotsConfig.get(SlotsType.BOOK_OF_RA).machine_bg, false)
        });
    }

    @Override
    public AtlasInfo[] getAtlasList() {
        if (super.atlas_ext != null) return super.atlas_ext;
        return super.getAtlasList(new AtlasInfo[]{
                new AtlasInfo("atlas/Footer.pack", false),
                new AtlasInfo("atlas/Autospin.pack", false),
                new AtlasInfo(SlotsConfig.get(SlotsType.BOOK_OF_RA).getLinesAtlas(), false),
                new AtlasInfo(SlotsConfig.get(SlotsType.BOOK_OF_RA).filename, false),
                new AtlasInfo(SlotsConfig.get(SlotsType.BOOK_OF_RA).pay_table, false)
        });
    }

    @Override
    public FontInfo[] getFontsList() {
        if (super.fonts_ext != null) return super.fonts_ext;
        return super.getFontsList(new FontInfo[]{
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
        return super.getSoundList(new SoundInfo[]{
                new SoundInfo("BookOfRa/bonus_game.ogg", false),
                new SoundInfo("BookOfRa/change_bet.ogg", false),
                new SoundInfo("BookOfRa/change_lines.ogg", false),
                new SoundInfo("BookOfRa/reel_stop.ogg", false),
                new SoundInfo("BookOfRa/spin_time.ogg", false),
                new SoundInfo("BookOfRa/win.ogg", false),
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
