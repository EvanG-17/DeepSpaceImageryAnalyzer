package com.example.deepspaceimageryanalyzer;

public class UnionFind {
    private int[] parent; // array to store the parent of each element
    private int[] size; // array to store the size of each set


    //Recursive version of find

    public UnionFind(int n){    // constructor to initialize the data structure
        // Initialize parent and size arrays
        parent = new int[n];    // create an array to store the parent of each element
        size = new int[n];      // create an array to store the size of each set


        for(int i = 0; i < n; i++) { // initialize each element's parent to itself and its size to 1
            parent[i] = i;
            size[i] = 1;
        }
    }

    //Define a method to join two sets together
    public int find(int p){
        //Traverse the parent array until we reach the root
        while (p != parent[p]) {
            parent[p] = parent[parent[p]];
            p = parent[p];
        }
        //Returns the root element found
        return p;

    }


    //A method to join two sets together
    public void union(int p, int q) {
        //Find the root of both elements
        int rootP = find(p);
        int rootQ = find(q);
        //If both elements are already in the same set, its complete
        if (rootP == rootQ) {
        }
        //Otherwise, join the sets together by  setting the smaller set's root to the larger set's root
        if(size[rootP] < size[rootQ]){
            parent[rootP] = rootQ;
            size[rootQ] += size[rootP];
        } else {
            parent[rootQ] = rootP;
            size[rootP] += size[rootQ];
        }
    }


    //Union-by-height of disjoint sets containing elements p and q. Whichever
    //negative-valued root has the largest absolute value is used as the merged root
//    public static void unionByHeight(int[] a, int p, int q) {
//        int rootp=find(a,p);
//        int rootq=find(a,q);
//        int deeperRoot=a[rootp]<a[rootq] ? rootp : rootq;
//        int shallowerRoot=deeperRoot==rootp ? rootq : rootp;
//        int temp=a[shallowerRoot];
//        a[shallowerRoot]=deeperRoot;
//        if(a[deeperRoot]==temp) a[deeperRoot]--; //Level increased by 1 (negative value)
//    //for same height set trees only
//    }
}
