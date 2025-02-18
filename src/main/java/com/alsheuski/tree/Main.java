package com.alsheuski.tree;

import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) {

    TreeNode ch3 = new TreeNode(3, null, null);
    TreeNode ch2 = new TreeNode(2, ch3, null);
    TreeNode root = new TreeNode(1, ch2, null);
    System.err.println(kthLargestLevelSum(root, 1));
  }

  public static long kthLargestLevelSum(TreeNode root, int k) {

    List<Integer> sum = new ArrayList<>();
    List<TreeNode> nextLEvel = new ArrayList<>(2);
    List<TreeNode> currentLEvel = new ArrayList<>(2);
    currentLEvel.add(root);

    while (!currentLEvel.isEmpty()) {
      int levelSum = 0;
      for (TreeNode node : currentLEvel) {
        if (node == null) {
          continue;
        }
        levelSum = levelSum + node.val;
        TreeNode left = node.left;
        TreeNode right = node.right;
        nextLEvel.add(right);
        nextLEvel.add(left);
      }

      if (levelSum > 0) {
        sum.add(levelSum);
      }
      currentLEvel = new ArrayList<>(nextLEvel);
      nextLEvel.clear();
    }

    sum.sort((a, b) -> Integer.compare(b, a));

    if (sum.size() < k) {
      return -1;
    }

    return sum.get(k - 1);
  }

  public static class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {}

    TreeNode(int val) {
      this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
      this.val = val;
      this.left = left;
      this.right = right;
    }
  }
}
