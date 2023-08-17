package com.mbuddy.musicbuddy.service;

import com.mbuddy.musicbuddy.model.Constants;
import ie.corballis.sox.SoXEncoding;
import ie.corballis.sox.Sox;
import org.springframework.stereotype.Service;

@Service
public class SoxService
{
    public void convertToDAT(String input, String output)
    {
        try {
            Sox sox = new Sox("/usr/bin/sox");
            sox.sampleRate(Constants.SAMPLE_RATE)
                    .inputFile(input)
                    .encoding(SoXEncoding.SIGNED_INTEGER)
                    .bits(Constants.SAMPLE_SIZE_IN_BITS)
                    .outputFile(output)
                    .execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
