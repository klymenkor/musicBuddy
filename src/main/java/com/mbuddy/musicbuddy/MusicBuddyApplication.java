package com.mbuddy.musicbuddy;

import com.mbuddy.musicbuddy.model.Chord;
import com.mbuddy.musicbuddy.model.Chords;
import com.mbuddy.musicbuddy.model.MIdiInstrument;
import com.mbuddy.musicbuddy.model.Note;
import com.mbuddy.musicbuddy.model.OctaveNote;
import com.mbuddy.musicbuddy.service.MidiPlayer;
import com.mbuddy.musicbuddy.service.SoundParserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;
import java.util.List;

import static com.mbuddy.musicbuddy.model.Octave.FIRST;

@SpringBootApplication
public class MusicBuddyApplication
{
    public static void main(String[] args)
            throws InterruptedException, MidiUnavailableException, InvalidMidiDataException, IOException
    {
        MidiPlayer mp = new MidiPlayer();
        var app = SpringApplication.run(MusicBuddyApplication.class, args);
        var tuner = app.getBean(SoundParserService.class);
//        while(true) {
//            System.out.println(tuner.recognizeChord());
//        }

        while(true) {
           // var currentNote = tuner.getTopNotes(3);
            var note1 = OctaveNote.builder().note(Note.E).octave(FIRST).build();
            var chord1 = Chord.builder().baseNote(note1).chordType(Chords.MAJ).build();
            var note2 = OctaveNote.builder().note(Note.C).octave(FIRST).build();
            var chord2 = Chord.builder().baseNote(note2).chordType(Chords.MAJ).build();
            var note3 = OctaveNote.builder().note(Note.A).octave(FIRST).build();
            var chord3 = Chord.builder().baseNote(note3).chordType(Chords.MIN).build();
            var note4 = OctaveNote.builder().note(Note.F).octave(FIRST).build();
            var chord4 = Chord.builder().baseNote(note4).chordType(Chords.MAJ).build();
            mp.playChordProgression(List.of(chord2, chord1, chord3, chord4), 500, MIdiInstrument.PIANO, 500);
        }

    }

}
