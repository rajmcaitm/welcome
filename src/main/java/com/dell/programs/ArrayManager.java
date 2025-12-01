package com.dell.programs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ArrayManager {
    private int[] arr = new int[100];
    private int index = 0;
    private int[] oldState;

    public void addNumber(int no) {
        if (no <= 0) {
            System.out.println("Only positive numbers allowed!");
            return;
        }

        oldState = Arrays.copyOf(arr, arr.length);

        if (index < arr.length) {
            arr[index] = no;
            index++;
            System.out.println("Added: " + no +
                    " | Current array: " + Arrays.toString(Arrays.copyOf(arr, index)) +
                    " | Length: " + index);
        }

        // Reset when full
        if (index == arr.length) {
            System.out.println("Array is full. Resetting...");
            arr = new int[100];
            index = 0;
        }
    }

    public void addElement(int no) {
        List list = new ArrayList<Integer>();
        list.add(no);
        System.err.println(list);
    }

    public void start() throws InterruptedException {
        while (true) {
            int no = new Random().nextInt(100);
            System.err.println(no);
            addNumber(no);
            Thread.sleep(3000);
        }
    }
}
