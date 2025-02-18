package com.alsheuski.arrays.swap_zeros;

import java.util.Arrays;

public class Main {

  public static void main(String[] args) {
    int[] arr = {0, 3, 0, 7, 8, 9, 10, 0, 11, 0};
    int iterations = 0;
    int zeros = 0;
    for (int i = 0; i < arr.length; i++) {
      iterations = iterations + 1;


      if(arr[i] == 0) {
        zeros=zeros+1;
      }else{
        if(zeros!=0){
          var dump = arr[i];
          arr[i]=0;
          arr[i-zeros]=dump;
        }

      }






    /*  if (arr[i] != 0) {
        continue;
      }*/

     /* for (int j = i + 1; j < arr.length; j++) {
        iterations = iterations + 1;
        if (arr[j] != 0) {
          var dump = arr[j];
          arr[j] = 0;
          arr[i] = dump;
          break;
        }
      }*/
    }

    System.err.println(Arrays.toString(arr));
    System.err.println(iterations);
  }
}
