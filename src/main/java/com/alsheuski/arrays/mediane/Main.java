package com.alsheuski.arrays.mediane;
import java.util.stream.*;
import java.util.*;
public class Main {

//https://leetcode.com/problems/median-of-two-sorted-arrays/
  class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
      List<Integer> list = Stream.concat(Arrays.stream(nums1).boxed(), Arrays.stream(nums2).boxed()).sorted().collect(Collectors.toList());
      return findArrMediane(list);
    }

    private double findArrMediane(List<Integer> list) {

      if (list.size() == 0) {
        return 0d;
      }
      int index = list.size() / 2;

      if (list.size() % 2 == 0) {
        int leftIndex = index - 1;
        return ((double) list.get(leftIndex) + list.get(index)) / 2;
      }

      return list.get(index);
    }
  }
}
