package com.mbuddy.musicbuddy.model;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder
public class OctaveNote
{
    private Note note;
    private Octave octave;
    private boolean ignoreOctave;

    public int getIndex() {
        return octave.ordinal() * 12 + note.ordinal();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OctaveNote that = (OctaveNote) o;
        if(ignoreOctave)
            return note == that.note;
        return octave == that.octave && note == that.note;
    }

    @Override
    public int hashCode()
    {
        if(ignoreOctave)
            return Objects.hash(note);
        return Objects.hash(note, octave);
    }
}
