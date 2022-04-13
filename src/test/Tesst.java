package test;

import traffic.CurveNode;
import traffic.Node;

import javax.vecmath.Point3f;
import java.util.ArrayList;

public class Tesst {
    public static void main(String[] args) {

        /*for (int i = 0; i < 100; i++) {
            long l = System.nanoTime();
            double d = 129 * Math.cos(11.2);
            System.out.println(System.nanoTime() - l);
        }*/

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("yahho");
        arrayList.add("yah");
        arrayList.add("yahqwdho");
        arrayList.add("hho");
        arrayList.add("aho");
        arrayList.add("ahhoddd");
        arrayList.add("yahhosssssssss");

        arrayList.add(arrayList.indexOf("yahqwdho"),"joiej");

        for (String s : arrayList) {
            System.out.println(s);
        }
    }

}
