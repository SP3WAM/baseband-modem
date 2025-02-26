package com.github.sp3wam.baseband.modem.impl.morse.decoder;

enum MorseSymbol
{
    /***
     * A short mark, dot or dit.
     */
    DIT,
    /***
     * A longer mark, dash or dah.
     */
    DAH,
    /***
     * A short gap (between letters).
     */
    SHORT_GAP,
    /***
     * A medium gap (between words).
     */
    MEDIUM_GAP
}
