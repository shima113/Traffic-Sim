package test;

import traffic.CurveNode;
import traffic.Node;

import javax.vecmath.Point3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tesst {
    public static void main(String[] args) {
        process(new ArrayList<>(
                Arrays.asList("1", "2", "|", "3", "4")));
    }

    static void process(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals("|")) {
                list.remove(i);
                continue;
            }
            System.out.println(list.get(i));
        }
    }

}
