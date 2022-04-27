package test;

import org.bouncycastle.util.io.TeeInputStream;
import traffic.CurveNode;
import traffic.Node;

import javax.vecmath.Point3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Tesst {
    LinkedList<Integer> list;
    public static void main(String[] args) {
        new Tesst();
    }

    public Tesst(){
        list = new LinkedList<>();
        list.add(4);
        list.add(44);
        list.add(1);
        list.add(7);
        list.add(2);
        list.add(9);
        list.add(19);
        list.add(28);
        list.add(6);
        list.add(5);

        for (Integer integer : list) {
            System.out.println(integer);
        }
        System.out.println("--------------------");

        long l = System.nanoTime();
        sort();
        System.out.println("time=" + (System.nanoTime() - l));

        for (Integer integer : list) {
            System.out.println(integer);
        }
    }

    public void sort(){
        int length = list.size();

        int num;
        int pos;
        int temp;

        for (num = 0; num < length; num++){
            for (pos = length - 1; pos >= num + 1; pos--){
                if (list.get(pos) < list.get(pos - 1)){
                    temp = list.get(pos);
                    list.set(pos, list.get(pos - 1));
                    list.set(pos - 1, temp);
                }
            }
        }
    }
}
