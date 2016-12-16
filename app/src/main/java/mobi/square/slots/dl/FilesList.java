package mobi.square.slots.dl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;

import mobi.square.slots.enums.SlotsType;

public class FilesList {

    public static final String INSTALL_PATH = "SuperSlots/";

    private static final Map<SlotsType, String[]> files;

    static {
        files = new HashMap<SlotsType, String[]>();
        files.put(SlotsType.BOOK_OF_RA, new String[]{
                "BookOfRa",
                "BookOfRa_150312.bin",
                // sound
                "bonus_game.ogg",
                "change_bet.ogg",
                "change_lines.ogg",
                "reel_stop.ogg",
                "spin_time.ogg",
                "win.ogg",
                // atlas
                "BookOfRa.pack",
                "BookOfRa.png",
                "BookOfRa2.png",
                "BookOfRaPay.pack",
                "BookOfRaPay.png",
                // jpg
                "BookOfRaBg.jpg",
                "BookOfRaPayBg.jpg"
        });
        files.put(SlotsType.ROCKCLIMBER, new String[]{
                "RockClimber",
                "RockClimber_150312.bin",
                // sound
                "bonus_game.ogg",
                "change_bet.ogg",
                "change_lines.ogg",
                "reel_stop.ogg",
                "spin_time.ogg",
                // atlas
                "RockClimber.pack",
                "RockClimber.png",
                "RockClimberAni.pack",
                "RockClimberAni.png",
                "RockClimberAni2.png",
                "RockClimberBonus.pack",
                "RockClimberBonus.png",
                "RockClimberBonus2.png",
                "RockClimberBonus3.png",
                "RockClimberBonus4.png",
                "RockClimberCards.pack",
                "RockClimberCards.png",
                "RockClimberCards2.png",
                "RockClimberPay.pack",
                "RockClimberPay.png",
                "RockClimberRisk.pack",
                "RockClimberRisk.png",
                // jpg
                "RockClimberBg.jpg",
                "RockClimberPayBg.jpg",
                "RockClimberPeak.jpg",
                "RockClimberRiskBg.jpg",
                "RockClimberSlope.jpg",
        });
        files.put(SlotsType.CRAZY_MONKEY, new String[]{
                "CrazyMonkey",
                "CrazyMonkey_150312.bin",
                // sound
                "bonus_game.ogg",
                "change_bet.ogg",
                "change_lines.ogg",
                "reel_stop.ogg",
                "spin_time.ogg",
                // atlas
                "CrazyMonkey.pack",
                "CrazyMonkey.png",
                "CrazyMonkeyAni.pack",
                "CrazyMonkeyAni.png",
                "CrazyMonkeyAni2.png",
                "CrazyMonkeyAni3.png",
                "CrazyMonkeyAniR.pack",
                "CrazyMonkeyAniR.png",
                "CrazyMonkeyAniR2.png",
                "CrazyMonkeyBonus.pack",
                "CrazyMonkeyBonus.png",
                "CrazyMonkeyBonus2.png",
                "CrazyMonkeyBonus3.png",
                "CrazyMonkeyBonus4.png",
                "CrazyMonkeyBonus5.png",
                "CrazyMonkeyBonus6.png",
                "CrazyMonkeyCards.pack",
                "CrazyMonkeyCards.png",
                "CrazyMonkeyCards2.png",
                "CrazyMonkeyPay.pack",
                "CrazyMonkeyPay.png",
                "CrazyMonkeyRisk.pack",
                "CrazyMonkeyRisk.png",
                // jpg
                "CrazyMonkeyBg.jpg",
                "CrazyMonkeyPayBg.jpg",
                "CrazyMonkeyRiskBg.jpg",
                "CrazyMonkeyBonusBg.jpg"
        });
        files.put(SlotsType.GARAGE, new String[]{
                "atlas",
                "Garage_150312.bin",
                // atlas
                "Garage.pack",
                "Garage.png",
                "GarageBoxes.pack",
                "GarageBoxes.png",
                "GarageCommon.pack",
                "GarageCommon.png",
                "GarageLocks.pack",
                "GarageLocks.png",
                "GaragePay.pack",
                "GaragePay.png",
                // jpg
                "GarageBg.jpg",
                "GarageBoxesBg.jpg",
                "GarageLocksBg.jpg",
                "GaragePayBg.jpg"
        });
        files.put(SlotsType.UNDERWATER_LIFE, new String[]{
                "atlas",
                "UnderwaterLife_150312.bin",
                // atlas
                "UnderwaterLife.pack",
                "UnderwaterLife.png",
                "UnderwaterLifeBonus.pack",
                "UnderwaterLifeBonus.png",
                "UnderwaterLifePay.pack",
                "UnderwaterLifePay.png",
                // jpg
                "UnderwaterLifeBg.jpg",
                "UnderwaterLifeBonusBg.jpg",
                "UnderwaterLifePayBg.jpg"
        });
    }

    public static boolean installed(SlotsType type) {
        String[] list = files.get(type);
        if (list == null) return true;
        for (int i = 2; i < list.length; i++) {
            FileHandle file = Gdx.files.external(INSTALL_PATH.concat(list[0]).concat("/").concat(list[i]));
            if (!file.exists()) return false;
        }
        return true;
    }

    public static String getFileName(SlotsType type) {
        String[] list = files.get(type);
        if (list == null) return null;
        if (list.length < 2) return null;
        return list[1];
    }

}
