package com.mbuddy.musicbuddy.model;

import java.util.Collection;

public enum Interval
{
    OCTAVE,
    SECOND_MIN,
    SECOND_MAJ,
    THIRD_MIN,
    THIRD_MAJ,
    FOURTH,
    DIMINISHED_FIFTH,
    FIFTH,
    SIXTH_MIN,
    SIXTH_MAJ,
    SEVENTH_MIN,
    SEVENTH_MAJ;

    public static Interval getInterval(OctaveNote note1, OctaveNote note2) {
        return values()[Math.abs(note1.getNote().ordinal() - note2.getNote().ordinal())];
    }

}
