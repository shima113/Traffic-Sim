package traffic;

import java.util.ArrayList;

public class NodeList extends ArrayList<Node> {

    public Node searchByDistance(float distance){
        Node resultNode = null;
        float totalDistance = 0;

        for (Node node : this) {
            totalDistance += node.getLength();
            if (totalDistance > distance) {
                resultNode = node;
                break;
            }
        }

        System.out.println(totalDistance);
        System.out.println(distance);
        return resultNode;
    }

    public float getLength(){
        float totalDistance = 0;

        for (Node node : this) {
            totalDistance += node.getLength();
        }
        return totalDistance;
    }

    @Override
    public void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
    }

}
