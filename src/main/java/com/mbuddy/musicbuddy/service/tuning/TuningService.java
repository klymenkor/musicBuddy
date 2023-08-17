package com.mbuddy.musicbuddy.service.tuning;

import com.mbuddy.musicbuddy.model.OctaveNote;

import java.util.Map;

public interface TuningService
{
    Map<OctaveNote, Double> getNoteFrequencies();
}
