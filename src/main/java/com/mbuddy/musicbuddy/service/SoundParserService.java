package com.mbuddy.musicbuddy.service;

import com.mbuddy.musicbuddy.model.Chords;
import com.mbuddy.musicbuddy.model.Interval;
import com.mbuddy.musicbuddy.model.OctaveNote;
import com.mbuddy.musicbuddy.service.tuning.TuningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mbuddy.musicbuddy.model.Octave.FIRST;

@Service
@RequiredArgsConstructor
public class SoundParserService
{
    private final MicrophoneReaderService microphoneReaderService;
    private final SoxService soxService;
    private final FourierTransformatorService fourierTransformatorService;
    private final TuningService tuningService;
    private File BUFFER_WAV = new File("record.wav");
    private File BUFFER_DAT = new File("record.dat");
    public OctaveNote getBase() {
        return getTopNotes(1).get(0);
    }

    public List<OctaveNote> getTopNotes(int n) {
        return new ArrayList<>(getTopNotesWithFrequencies(n).keySet());
    }

    public Chords recognizeChord() {
        var notes = getTopNotesWithFrequencies(4).keySet().stream().sorted(Comparator.comparingInt(it -> it.getNote().ordinal())).toList();
        for(int i = 0; i < notes.size(); i++) {
            var candidateNote = notes.get(i);
            var intervals = new ArrayList<Interval>(notes.size() - 1);
            for(int j = 0; j < notes.size(); j++) {
                if(i != j) {
                    intervals.add(Interval.getInterval(candidateNote, notes.get(j)));
                }
            }
            var chord = Chords.getChord(intervals);
            if(chord != null) return chord;
        }
        return null;
    }

    private HashMap<OctaveNote, Double> getTopNotesWithFrequencies(int n)
    {
        // todo: smart way to configure frames to values ratio
        microphoneReaderService.readAudio(50, BUFFER_WAV);
        soxService.convertToDAT(BUFFER_WAV.getAbsolutePath(), BUFFER_DAT.getAbsolutePath());
        var frequencies = fourierTransformatorService.getFrequencies(BUFFER_DAT);

        var sortedByFrequency = countNotesByFrequency(frequencies, false)
                .entrySet()
                .stream()
                .filter(it -> it.getValue() > 50 && it.getKey().getOctave().ordinal() >= 2 && it.getKey().getOctave().ordinal() <= 6)
                .sorted(Map.Entry.comparingByValue((a, b) -> a < b ? 1 : a.equals(b) ? 0 : -1))
                .toList();
        var topNnotes = new HashMap<OctaveNote, Double>();
        for(var entry: sortedByFrequency) {
            if (topNnotes.containsKey(entry.getKey()))
                topNnotes.put(entry.getKey(), topNnotes.get(entry.getKey()) + entry.getValue());
            else if(topNnotes.size() < n)
                topNnotes.put(entry.getKey(), entry.getValue());
        }
        return topNnotes;
    }

    private HashMap<OctaveNote, Double> countNotesByFrequency(double[][] maxHz, boolean ignoreOctave)
    {
        var notesByFrequency = new HashMap<OctaveNote, Double>();
        for(var val : maxHz) {
            var note = getClosestNote(val[1], ignoreOctave);
            notesByFrequency.put(note, notesByFrequency.getOrDefault(note, 0.0) + val[0]);
        }
        return notesByFrequency;
    }
    private OctaveNote getClosestNote(double frequency, boolean ignoreOctave)
    {
        var noteFrequencies = tuningService.getNoteFrequencies();
        OctaveNote closestNote = null;
        for(var note: noteFrequencies.entrySet()) {
            if(closestNote == null
                    || Math.abs(frequency - note.getValue()) < Math.abs(frequency - noteFrequencies.get(closestNote)))
                if(ignoreOctave) closestNote = OctaveNote.builder().octave(FIRST).note(note.getKey().getNote()).build();
                else closestNote = note.getKey();
        }
        if(ignoreOctave)
            closestNote.setOctave(FIRST);
        return closestNote;
    }
}
