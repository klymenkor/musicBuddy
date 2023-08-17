package com.mbuddy.musicbuddy.service;

import org.jtransforms.fft.DoubleFFT_1D;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FourierTransformatorService
{
    private int SAMPLERATE;

    public double[][] getFrequencies(File input) {
        double[] audioData = parseDATfile(input);

        var fft = new DoubleFFT_1D(audioData.length);

        double[] fftData = new double[audioData.length * 2];
        for (int i = 0; i < audioData.length; i++) {
            fftData[2 * i] = audioData[i];
        }
        fft.complexForward(fftData);

        double[][] result = new double[fftData.length][2];
        for (int i = 0; i < fftData.length; i += 2) {
            double hz = 2 * (i / 2.0 / fftData.length) * SAMPLERATE;
            double vlen = Math.sqrt(Math.pow(fftData[i], 2) + Math.pow(fftData[i + 1], 2));
            result[i][0] = vlen;
            result[i][1] = hz;
        }
        return result;
    }


    private double[] parseDATfile(File file)
    {
        try {
            var lines = Files.readAllLines(Path.of(file.toURI()));
            var headerValues = lines.get(0).trim().split(" ");
            SAMPLERATE = Integer.parseInt(headerValues[headerValues.length - 1]);
            return lines
                    .stream()
                    .skip(2)
                    .map(String::trim)
                    .mapToDouble(it -> Double.parseDouble(it.substring(it.lastIndexOf(" "))))
                    .toArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
