package com.mbuddy.musicbuddy.model;

public enum MIdiInstrument
{
    PIANO(0),
    SYNTH(50),
    GUITAR(25),
    BASS(33);

    private int code;

    MIdiInstrument(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
