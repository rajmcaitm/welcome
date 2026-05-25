package com.dell.programs;

import java.util.HashSet;
import java.util.Set;

public class FindDuplicatesArray {
    public static void main(String[] args) {
        System.out.println("---------------");
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 5,4,3,2,1};
        Set<Integer> set = new HashSet<>();
        for(int i : arr){
            if(!set.add(i)){
                System.out.println(i);
            }
        }
    }
}
