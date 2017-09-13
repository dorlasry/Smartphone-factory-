/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.test;

import bgu.spl.a2.Task;
import bgu.spl.a2.WorkStealingThreadPool;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * an implementation for the assignment's part1: doing merge sort using multiple threads
 */
public class MergeSort extends Task<int[]> {

    private final int[] array;

    public MergeSort(int[] array) {
        this.array = array;
    }

    @Override
    protected void start() {
        if(array.length==1)
        	complete(array);
        else{
        	int[] left=new int[array.length/2];
        	int[] right=new int[array.length-array.length/2];
        	for(int i=0; i<array.length/2; i++)
        		left[i]=array[i];
        	for(int i=array.length/2, j=0; i<array.length; i++, j++)
        		right[j]=array[i];
        	LinkedList<MergeSort> tasks=new LinkedList<MergeSort>();
        	MergeSort mergeLeft=new MergeSort(left);
        	MergeSort mergeRight=new MergeSort(right);
        	spawn(mergeLeft);
        	spawn(mergeRight);
        	tasks.add(mergeLeft);
        	tasks.add(mergeRight);
        	whenResolved(tasks, ()->{
        		int[] res= new int[array.length];
        		int[] sortedLeft= tasks.get(0).getResult().get();
        		int[] sortedRight= tasks.get(1).getResult().get();
        		for(int i=0, leftCount=0, rightCount=0; i<res.length; i++){
        			if(leftCount<sortedLeft.length && (rightCount==sortedRight.length || sortedLeft[leftCount]< sortedRight[rightCount])){
        				res[i]=sortedLeft[leftCount];
        				leftCount++;
        			}
        			else{
        				res[i]=sortedRight[rightCount];
        				rightCount++;
        			}
        		}
        		complete(res);        		
        	});
        }
    }

    public static void main(String[] args) throws InterruptedException {
        WorkStealingThreadPool pool = new WorkStealingThreadPool(3);
        int n = 1000000; //you may check on different number of elements if you like
        int[] array = new Random().ints(n).toArray();
        MergeSort task = new MergeSort(array);

        CountDownLatch l = new CountDownLatch(1);
        pool.start();
        pool.submit(task);
        task.getResult().whenResolved(() -> {                		          		      		   
        	//warning - a large print!! - you can remove this line if you wish
          //  System.out.print(" "+Arrays.toString(task.getResult().get()));
            l.countDown();
        });
        
        l.await();
        pool.shutdown();  
    }

}
