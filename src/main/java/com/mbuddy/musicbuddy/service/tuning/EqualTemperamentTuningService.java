package com.mbuddy.musicbuddy.service.tuning;

import com.mbuddy.musicbuddy.model.Note;
import com.mbuddy.musicbuddy.model.Octave;
import com.mbuddy.musicbuddy.model.OctaveNote;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EqualTemperamentTuningService implements TuningService
{
    private final static double DEFAULT_SEED = 16.35;
    private final Map<OctaveNote, Double> noteFrequencies = generateNoteFrequencies();
    private final double seed;

    public EqualTemperamentTuningService(){
        this(DEFAULT_SEED);
    }

    public EqualTemperamentTuningService(double seed) {
        this.seed = seed;
    }
    @Override
    public Map<OctaveNote, Double> getNoteFrequencies()
    {
        return noteFrequencies;
    }

    private static Map<OctaveNote, Double> generateNoteFrequencies() {
        var map = new HashMap<OctaveNote, Double>();
        var notes = 9 * 12;
        var cnt = -1;
        var octave = -1;
        var next = 16.35;
        while(++cnt < notes) {
            if(cnt % 12 == 0) {
                octave++;
            }
            map.put(OctaveNote.builder().note(Note.values()[cnt % 12]).octave(Octave.values()[octave]).build(), next);
            next *= Math.pow(2, 1.0 / 12);
        }
        return map;
    }

}
