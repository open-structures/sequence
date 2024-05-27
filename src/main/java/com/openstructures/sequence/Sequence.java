package com.openstructures.sequence;

public interface Sequence<T extends C, C> {

  SequenceLink<T> insert(T key);

  void delete(T key);

  SequenceLink<T> get(T key);

  /**
   * Returns an element that is greater than the comparable.
   */
  T greaterThan(C comparable);

  /**
   * Returns an element that is less than the comparable.
   */
  T lessThan(C comparable);

  T equalTo(C comparable);
}
