package com.mbuddy.musicbuddy.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Chord
{
    private OctaveNote baseNote;
    private Chords chordType;
}
