// Evan Tynan Geary 20098723 Applied Computing Forensics
package com.example.deepspaceimageryanalyzer;

public class UnionFind {
    public UnionFind() {
    }

    public static int find(int[] a, int id) {
        // find method finds the root of the set that id belongs to
        if (a[id] < 0) {
            // if the element at id is negative, its the root of the set
            return a[id];
        } else {
            // finds root by following parent pointers until reaching the root
            return a[id] == id ? id : find(a, a[id]);
        }
    }


    // merges the sets containing elements p and q
    public static void union(int[] a, int p, int q) {
        // find the roots of the sets containing p and q
        a[find(a, q)] = find(a, p);
        // we set the root of the set containing q to be the root of the set containing p
    }
}
