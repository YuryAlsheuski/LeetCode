package com.alsheuski.tree;

public class ReverseTree {

  public static void main(String[] args) {
    var node = new Node(1);
    var node1 = new Node(3);
    var node3 = new Node(6);
    var node4 = new Node(9);

    var node5 = new Node(2, node, node1);
    var node6 = new Node(7, node3, node4);

    var node7 = new Node(4, node5, node6);

    var result = reverseTree(node7);
    System.err.println(result);


  }

  private static Node reverseTree(Node root) {
    if (root == null) {
      return null;
    }
    if (!root.hasChild()) {
      return new Node(root.getValue());
    }
    var rootCopy = new Node(root.value);
    var left = root.getLeft();
    var right = root.getRight();
    if (left.value < right.value) {
      rootCopy.setLeft(reverseTree(right));
      rootCopy.setRight(reverseTree(left));
    }
    return rootCopy;
  }

  private static class Node {

    private final int value;
    private Node left;
    private Node right;

    public Node(int value) {
      this.value = value;
    }

    public Node(int value, Node left, Node right) {
      this(value);
      this.left = left;
      this.right = right;
    }

    public int getValue() {
      return value;
    }

    public Node getLeft() {
      return left;
    }

    public Node getRight() {
      return right;
    }

    public boolean hasChild() {
      return left != null && right != null;
    }

    public void setLeft(Node left) {
      this.left = left;
    }

    public void setRight(Node right) {
      this.right = right;
    }
  }
}
