package mobi.square.slots.screens;

import mobi.square.slots.api.Connection;
import mobi.square.slots.app.Game;
import mobi.square.slots.stages.Lobby;

public class SLobby extends SBasic {

    public SLobby() {
        super(null);
        super.stage = new Lobby(this);
    }

    public SLobby(Game parent) {
        super(parent);
        super.stage = new Lobby(this);
    }

    @Override
    public void show() {
        if (super.stage == null)
            super.stage = new Lobby(this);
        super.show();
    }

    @Override
    public FontInfo[] getFontsList() {
        if (super.fonts_ext != null) return super.fonts_ext;
        return super.getFontsList(new FontInfo[]{
                new FontInfo("Junegull.ttf", 36f, false),
                new FontInfo("Junegull.ttf", 28f, false),
                new FontInfo("arial.ttf", 16f, false)
        });
    }

    @Override
    public AtlasInfo[] getAtlasList() {
        if (super.atlas_ext != null) return super.atlas_ext;
        return super.getAtlasList(new AtlasInfo[]{
                        new AtlasInfo("atlas/LobbyBackground.pack", false),
                        new AtlasInfo("atlas/DailyBonus.pack", false),
                        Connection.getLocale().getLanguage().equals("ru")
                                ? new AtlasInfo("atlas/LobbyIcons.pack", false)
                                : new AtlasInfo("atlas/LobbyIconsEn.pack", false)
                }
        );
    }

}
