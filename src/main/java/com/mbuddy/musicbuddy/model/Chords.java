package com.mbuddy.musicbuddy.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public enum Chords
{
    POWER(Set.of(Interval.FIFTH)),
    MIN(Set.of(Interval.THIRD_MIN, Interval.FIFTH)),
    MAJ(Set.of(Interval.THIRD_MAJ, Interval.FIFTH)),
    SUS2(Set.of(Interval.SECOND_MAJ, Interval.FIFTH)),
    SUS4(Set.of(Interval.FOURTH, Interval.FIFTH)),
    DIM(Set.of(Interval.THIRD_MIN, Interval.DIMINISHED_FIFTH)),
    MAJ7(Set.of(Interval.THIRD_MAJ, Interval.SEVENTH_MAJ)),
    MIN7(Set.of(Interval.THIRD_MIN, Interval.SEVENTH_MIN)),
    MAJ7_DOM(Set.of(Interval.THIRD_MAJ, Interval.SEVENTH_MIN));
 //   AUGMENTED(List.of(Interval.THIRD_MIN, Interval.THIRD_MAJ))
    // todo: extend, at least 9, 11 ,13

    Chords(Collection<Interval> intervals) {
        this.intervals = intervals;
    }

    public Collection<Interval> getIntervals()
    {
        return intervals;
    }

    private Collection<Interval> intervals;

    public static Chords getChord(List<Interval> intervals) {
        for(int i = values().length - 1; i >= 0; i--) {
            if(intervals.containsAll(values()[i].intervals)) return values()[i];
        }
        return null;
    }

    public Collection<Integer> getDistances() {
        return Stream.concat(Stream.of(0), intervals.stream().map(Enum::ordinal)).toList();
    }
}
