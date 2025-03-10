package com.github.sp3wam.baseband.modem.core;

public interface BlockListenerIf<T extends SignalIf>
{
    void onCurrentValueSet(T newValue);
}
