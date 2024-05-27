package org.openstructures.sequence;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static org.openstructures.sequence.avltree.TreeUtils.getLeftmost;
import static org.openstructures.sequence.avltree.TreeUtils.getRightmost;
import static org.openstructures.sequence.avltree.TreeUtils.isLeftChild;
import static org.openstructures.sequence.avltree.TreeUtils.isRightChild;

import org.openstructures.sequence.avltree.AVLNode;
import org.openstructures.sequence.avltree.AVLTree;
import java.util.Comparator;
import java.util.Map;

public class InMemorySequence<T extends C, C> implements Sequence<T, C> {

  private final AVLTree<T> avlTree;
  private final Map<T, SequenceLinkImpl<T>> keysAndLinks = newHashMap();
  private final Comparator<C> comparator;

  public InMemorySequence(Comparator<C> comparator) {
    checkNotNull(comparator);

    this.avlTree = new AVLTree<>(comparator);
    this.comparator = comparator;
  }

  private InMemorySequence(AVLTree<T> avlTree, Comparator<C> comparator) {
    this.avlTree = avlTree;
    this.comparator = comparator;
  }

  public static <T extends C, C> InMemorySequence<T, C> join(InMemorySequence<T, C> left,
      InMemorySequence<T, C> right) {
    checkNotNull(left);
    checkNotNull(right);
    checkArgument(left.comparator.equals(right.comparator));

    T leftRightmost = !left.isEmpty() ? getRightmost(left.avlTree.getRoot()).getValue() : null;
    T rightLeftmost = !right.isEmpty() ? getLeftmost(right.avlTree.getRoot()).getValue() : null;

    AVLTree<T> tree = AVLTree.join(left.avlTree, right.avlTree);

    InMemorySequence<T, C> newSequence = new InMemorySequence<>(tree, left.comparator);
    newSequence.keysAndLinks.putAll(left.keysAndLinks);
    newSequence.keysAndLinks.putAll(right.keysAndLinks);
    if (leftRightmost != null && rightLeftmost != null) {
      join(newSequence.keysAndLinks.get(leftRightmost),
          newSequence.keysAndLinks.get(rightLeftmost));
    }

    return newSequence;
  }

  @Override
  public SequenceLink<T> insert(T key) {
    checkNotNull(key, "can't added nulls");
    checkArgument(!keysAndLinks.containsKey(key), "%s is already part of the sequence", key);

    AVLNode<T> node = avlTree.insert(key);
    SequenceLinkImpl<T> link = new SequenceLinkImpl<>(key);
    AVLNode<T> nextLeftNode = getNextLeft(node);
    if (nextLeftNode != null) {
      SequenceLinkImpl<T> leftLink = keysAndLinks.get(nextLeftNode.getValue());
      SequenceLinkImpl<T> leftRight = leftLink.right;
      leftLink.right = link;
      link.left = leftLink;
      if (leftRight != null) {
        link.right = leftRight;
        leftRight.left = link;
      }
    } else {
      AVLNode<T> nextRightNode = getNextRight(node);
      if (nextRightNode != null) {
        SequenceLinkImpl<T> nextRight = keysAndLinks.get(nextRightNode.getValue());
        link.right = nextRight;
        nextRight.left = link;
      }
    }

    keysAndLinks.put(key, link);
    return get(key);
  }

  @Override
  public void delete(T key) {
    checkNotNull(key);
    if (keysAndLinks.containsKey(key)) {
      avlTree.delete(key);
      SequenceLinkImpl<T> link = keysAndLinks.get(key);
      join(link.left, link.right);
      keysAndLinks.remove(key);
    }
  }

  @Override
  public SequenceLink<T> get(T key) {
    checkNotNull(key);
    return keysAndLinks.get(key);
  }

  @Override
  public T greaterThan(C comparable) {
    checkNotNull(comparable);
    if (isEmpty()) {
      return null;
    } else {
      return greaterThan(comparable, avlTree.getRoot());
    }
  }

  @Override
  public T lessThan(C comparable) {
    checkNotNull(comparable);
    if (isEmpty()) {
      return null;
    } else {
      return lessThan(comparable, avlTree.getRoot());
    }
  }

  @Override
  public T equalTo(C comparable) {
    checkNotNull(comparable);
    if (isEmpty()) {
      return null;
    } else {
      return equalTo(comparable, avlTree.getRoot());
    }
  }

  private T equalTo(C comparable, AVLNode<T> node) {
    if (node == null) {
      return null;
    } else if (comparator.compare(comparable, node.getValue()) == 0) {
      return node.getValue();
    } else if (comparator.compare(comparable, node.getValue()) < 0) {
      return equalTo(comparable, node.getLeft());
    } else {
      return equalTo(comparable, node.getRight());
    }
  }

  private T lessThan(C comparable, AVLNode<T> node) {
    if (comparator.compare(node.getValue(), comparable) < 0) {
      if (node.getRight() != null) {
        T result = lessThan(comparable, node.getRight());
        if (result != null) {
          return result;
        }
      }
      return node.getValue();
    } else if (node.getLeft() != null) {
      return lessThan(comparable, node.getLeft());
    } else {
      return null;
    }
  }

  private T greaterThan(C comparable, AVLNode<T> node) {
    if (comparator.compare(node.getValue(), comparable) > 0) {
      if (node.getLeft() != null) {
        T result = greaterThan(comparable, node.getLeft());
        if (result != null) {
          return result;
        }
      }
      return node.getValue();
    } else if (node.getRight() != null) {
      return greaterThan(comparable, node.getRight());
    } else {
      return null;
    }
  }

  private static <T> void join(SequenceLinkImpl<T> left, SequenceLinkImpl<T> right) {
    if (left != null) {
      left.right = right;
    }
    if (right != null) {
      right.left = left;
    }
  }

  private static <T> AVLNode<T> getNextLeft(AVLNode<T> node) {
    if (node.getLeft() != null) {
      return getRightmost(node.getLeft());
    } else if (node.getParent() != null) {
      if (node.equals(node.getParent().getRight())) {
        return node.getParent();
      } else if (isRightChild(node.getParent())) {
        return node.getParent().getParent();
      }
    }
    return null;
  }

  private static <T> AVLNode<T> getNextRight(AVLNode<T> node) {
    if (node.getRight() != null) {
      return getLeftmost(node.getLeft());
    } else if (node.getParent() != null) {
      if (node.equals(node.getParent().getLeft())) {
        return node.getParent();
      } else if (isLeftChild(node.getParent())) {
        return node.getParent().getParent();
      }
    }
    return null;
  }

  public boolean isEmpty() {
    return avlTree.isEmpty();
  }

  private static class SequenceLinkImpl<T> implements SequenceLink<T> {

    private final T value;
    private SequenceLinkImpl<T> left, right;

    private SequenceLinkImpl(T value) {
      this.value = value;
    }

    @Override
    public T getValue() {
      return value;
    }

    @Override
    public SequenceLink<T> getLeft() {
      return left;
    }

    @Override
    public SequenceLink<T> getRight() {
      return right;
    }
  }
}
