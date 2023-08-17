package com.mbuddy.musicbuddy.service;


import com.mbuddy.musicbuddy.model.Constants;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class MicrophoneReaderService
{
    private static final int FRAME_SIZE = 1024;
    public void readAudio(int frames, File output)
    {
        AudioFormat a = getAudioFormat();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, a);
        try(var targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            var outStream = new ByteArrayOutputStream()) {
            targetDataLine.open(a);
            targetDataLine.start();

            byte[] buffer = new byte[FRAME_SIZE];

            int times = 0;
            while (times++ < frames)
                outStream.write(buffer, 0, targetDataLine.read(buffer, 0, buffer.length));

            var data = outStream.toByteArray();
            var bais = new ByteArrayInputStream(data);
            var ais = new AudioInputStream(bais, a, data.length);

            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, output);
            targetDataLine.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private AudioFormat getAudioFormat()
    {
        return new AudioFormat(Constants.SAMPLE_RATE, Constants.SAMPLE_SIZE_IN_BITS, 1, true, false);
    }
}
