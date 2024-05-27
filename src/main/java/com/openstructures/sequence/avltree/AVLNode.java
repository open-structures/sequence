package com.openstructures.sequence.avltree;

public interface AVLNode<T> {
    T getValue();

    AVLNode<T> getLeft();

    AVLNode<T> getRight();

    AVLNode<T> getParent();
}
