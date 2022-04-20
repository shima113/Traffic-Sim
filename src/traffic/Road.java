package traffic;

import java.util.ArrayList;

/**
 * indexが小さいほうが左側のレーン
 * carlistによるlist(1つしかないため)
 */
public class Road extends ArrayList<CarList> {

    /**
     *
     * @param nowNode 現在のNode
     * @param targetNode 車線変更先のNode
     * @return true:右に車線変更するとき<br>false:左に車線変更するとき
     */
    public boolean changeLaneDirection(CarList nowNode, CarList targetNode){
        boolean direction;

        int nowIndex = this.indexOf(nowNode);
        int tarIndex = this.indexOf(targetNode);
        if (nowIndex == -1 || tarIndex == -1){
            System.err.println("Bad change lane requests");
            System.exit(1);
        }

        direction = nowIndex < tarIndex;

        return direction;
    }
}
