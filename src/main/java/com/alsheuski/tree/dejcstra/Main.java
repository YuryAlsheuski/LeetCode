package com.alsheuski.tree.dejcstra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main {

public static void main(String[] args) {
    int [][] arr = {{0,1,3},{1,2,1},{1,3,4},{2,3,1}};
    System.err.println(findTheCity(4, arr, 4));
}
  public static int findTheCity(int n, int[][] edges, int distanceThreshold) {

    Map<Integer,Integer> pointToRealDistance = new HashMap<>();
    Map<Integer,Integer> pointToRelationsCount = new HashMap<>();

    Set<String> ss = new HashSet<>();
    ss.toArray(new String[0]);

    for(int[] edge : edges){
      int from = edge[0];
      int to = edge[1];
      int distance = edge[2];

     // if(distance>distanceThreshold){
    //    continue;
   //   }

   /*   int relative = pointToRelationsCount.getOrDefault(from,0);
      pointToRelationsCount.put(from,relative + 1);
      relative = pointToRelationsCount.getOrDefault(to,0);
      pointToRelationsCount.put(to,relative + 1);*/


         if(pointToRealDistance.containsKey(from)){
             distance = distance + pointToRealDistance.get(from);
          }

         if(pointToRealDistance.containsKey(to)){
            int storedDistance = pointToRealDistance.get(to);
             distance = Math.min(storedDistance,distance );
          }
         pointToRealDistance.put(to, distance );
    }

    int minReletions = Integer.MAX_VALUE;
    int maxNumber = -1;

    for(Map.Entry<Integer,Integer> entry : pointToRelationsCount.entrySet()){
      int number = entry.getKey();
      int rels = entry.getValue();

      if(rels==minReletions){
        if(number>maxNumber){
          maxNumber = number;
        }
      }

      if(rels<minReletions){
        minReletions=rels;
        if(number>maxNumber){
          maxNumber = number;
        }

      }



    }
    return maxNumber;
  }
}
