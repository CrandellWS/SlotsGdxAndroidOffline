package mobi.square.slots.enums;

public enum Method {

    lobby,
    slots,
    spin,
    spinJSON,
    maxBet,
    maxBetJSON,
    switchVersion,
    showBonus,
    takeBonus,
    takeAward,
    riskGame,
    riskOpen,
    riskWin,
    riskLoose,
    riskGreaterOpen,
    bonusGame,
    bonusOpen,
    bonusLoose,
    betMore,
    betLess,
    linesMore,
    linesLess,
    minBet,
    help,
    bank,
    top,
    invite,
    cupSlots,
    cupRegister,
    cupRules,
    sendMessage,
    getMessages,
    deleteMessage,
    getBonusCode,
    switchSound,
    changeRoom,
    readBonusMail,
    statistics,
    jsLogin,
    jsLobby,
    jsInit,
    jsSpin,
    jsMaxBet,
    jsChangeConfig,
    jsTakeBonus,
    jsTakeSuperBonus,
    jsSpinRoulette,
    jsInitRisk,
    jsRisk,
    jsInitGreater,
    jsRiskGreater,
    jsTakeAward,
    jsTop,
    jsBank,
    jsChangeName,
    jsInitChests,
    jsOpenChest,
    jsAddGold,
    jsCupTop,
    jsCupRegister,
    jsCupInit,
    jsBonusState,
    jsBonusProc,
    jsSetUserMoney,
    sgAdmin,
    setLines,
    setBet,
    selectBet,
    selectLines;

    public static Method convert(int value) {
        return Method.class.getEnumConstants()[value];
    }

}
