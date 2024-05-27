package com.openstructures.sequence;

public interface SequenceLink<T> {
    T getValue();

    SequenceLink<T> getLeft();

    SequenceLink<T> getRight();
}
