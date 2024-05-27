package com.openstructures.sequence.avltree;

import static com.google.common.base.Preconditions.checkNotNull;


public class TreeUtils {

    private TreeUtils() {
    }

    public static <T> AVLNode<T> getRightmost(AVLNode<T> node) {
        checkNotNull(node);

        return node.getRight() != null ? getRightmost(node.getRight()) : node;
    }

    public static <T> AVLNode<T> getLeftmost(AVLNode<T> node) {
        checkNotNull(node);

        return node.getLeft() != null ? getLeftmost(node.getLeft()) : node;
    }

    public static <T> boolean isLeftChild(AVLNode<T> node) {
        checkNotNull(node);

        return node.getParent() != null && node.equals(node.getParent().getLeft());
    }

    public static <T> boolean isRightChild(AVLNode<T> node) {
        checkNotNull(node);

        return node.getParent() != null && node.equals(node.getParent().getRight());
    }

    public static <T> boolean isLeaf(AVLNode<T> node) {
        checkNotNull(node);

        return node.getLeft() == null && node.getRight() == null;
    }
}
