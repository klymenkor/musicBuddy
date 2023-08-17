package com.mbuddy.musicbuddy.service;

import com.mbuddy.musicbuddy.model.Chord;
import com.mbuddy.musicbuddy.model.MIdiInstrument;
import com.mbuddy.musicbuddy.model.OctaveNote;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class MidiPlayer
{

    //https://github.com/leffelmania/android-midi-lib
    Sequencer sequencer = MidiSystem.getSequencer();

    public MidiPlayer() throws MidiUnavailableException
    {
        sequencer.open();
    }

    public void play() throws MidiUnavailableException, InvalidMidiDataException, IOException
    {
        Sequencer sequencer = MidiSystem.getSequencer();

        Transmitter transmitter = sequencer.getTransmitter();
      //  transmitter.setReceiver(new SmcReceiver());
        System.out.println(sequencer.getClass().getName());

        sequencer.open();

        sequencer.setSequence(MidiSystem.getSequence(new File("midifile.mid")));


        sequencer.start();

        while(true) {
            if(sequencer.isRunning()) {
                try {
                    Thread.sleep(200); // Check every second
                } catch(InterruptedException ignore) {
                    break;
                }
            } else {
                break;
            }
        }
        System.out.println("DONE!");
    }

    public void playChordProgression(Collection<Chord> chords,
                                     int tempo,
                                     MIdiInstrument instrument,
                                     int times) throws InvalidMidiDataException
    {
        Sequence s = new Sequence(Sequence.PPQ,24);
        long offset = 0;
        int cnt = 0;
        while(cnt++ < times) {
            for(var chord: chords) {
                var notes = chord.getChordType().getDistances().stream().map(it -> chord.getBaseNote().getIndex() + it).toList();
                fillTrackWithNotes(s.createTrack(),  notes, instrument, offset, tempo);
                while(sequencer.isRunning());
                sequencer.setSequence(s);
                offset += tempo;
            }
        }

        sequencer.start();

    }

    public void playNote(OctaveNote note) throws InvalidMidiDataException
    {
        Sequence s = new Sequence(Sequence.PPQ,24);
        fillTrackWithNotes(s.createTrack(), List.of(note.getIndex()), MIdiInstrument.PIANO, 0, 1000);


        sequencer.setSequence(s);
        sequencer.start();
    }

    private void fillTrackWithNotes(Track t, Collection<Integer> notes, MIdiInstrument mIdiInstrument,
                                    long timeOffsetInTicks,
                                    int tempo) throws InvalidMidiDataException
    {

        // Turn on midi soundset
        byte[] b = {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
        SysexMessage sm = new SysexMessage();
        sm.setMessage(b, 6);
        MidiEvent me = new MidiEvent(sm,(long)0);
        t.add(me);

        // Set tempo
        MetaMessage mt = new MetaMessage();
        byte[] bt = {0x02, (byte)0x00, 0x00};
        mt.setMessage(0x51 ,bt, 3);
        me = new MidiEvent(mt,(long)0);
        t.add(me);


        // Enable omni
        ShortMessage mm = new ShortMessage();
        mm.setMessage(0xB0, 0x7D,0x00);
        me = new MidiEvent(mm,(long)0);
        t.add(me);

        // Enable poly
        mm = new ShortMessage();
        mm.setMessage(0xB0, 0x7F,0x00);
        me = new MidiEvent(mm,(long)0);
        t.add(me);

        // Set instrument
        mm = new ShortMessage();
        mm.setMessage(0xC0, mIdiInstrument.getCode(), 0x00);
        me = new MidiEvent(mm,(long)0);
        t.add(me);

        for(var note: notes) {
            // Mark notes and time
            mm = new ShortMessage();
            mm.setMessage(0x90, note,127);
            me = new MidiEvent(mm,timeOffsetInTicks + 1);
            t.add(me);

            // Disable notes
            mm = new ShortMessage();
            mm.setMessage(0x80,note,0x40);
            me = new MidiEvent(mm,timeOffsetInTicks + tempo);
            t.add(me);
        }
    }
}
