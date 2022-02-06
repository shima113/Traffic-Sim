package cars;

import java.util.ArrayList;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import tools.Colors;
import traffic.Node;

public class PassengerCar extends Car {
	
	public PassengerCar(int load, ArrayList<Node> nodes, Point3f startPoint, double direction) {
		super(load, nodes, startPoint, direction);
		
		carObjectGroup = createObject();
		init(startPoint, direction);
	}
	
	private TransformGroup createObject() {
		TransformGroup object = new TransformGroup();
		object.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		//create body
		Point3d[] vertices = {new Point3d(0.0, 0.0, 0.0), new Point3d(0.0175, 0.0, 0.0), new Point3d(0.0, -0.0075, 0.0), 
				new Point3d(0.0175, -0.0075, 0.0), new Point3d(0.0175, -0.0075, -0.015), new Point3d(0.0, -0.0075, -0.015), 
				new Point3d(0.0, 0.0, -0.015), new Point3d(0.0175, 0, -0.015), new Point3d(0.0, 0.0075, -0.015), 
				new Point3d(0.0175, 0.0075, -0.015), new Point3d(0.0, 0.0075, -0.05), new Point3d(0.0175, 0.0075, -0.05), 
				new Point3d(0.0, -0.0075, -0.05), new Point3d(0.0175, -0.0075, -0.05)};
		
		int[] indices = {0, 1, 3, 2,   0, 1, 7, 6,   1, 3, 4, 7,   0, 2, 5, 6, 
				4, 9, 11, 13,   5, 8, 10, 12,   6, 7, 9, 8,   10, 11, 13, 12,   2, 3, 13, 12};
		
		Color3f color = new Color3f((float)Math.random(), (float)Math.random(), (float)Math.random());
		Color3f[] colors = {color, Colors.CYAN};
		
		int[] colorIndices = {0, 0, 0, 0,   0, 0, 0, 0,   0, 0, 0, 0,   0, 0, 0, 0, 
				0, 0, 0, 0,   0, 0, 0, 0,   1, 1, 1, 1,   0, 0, 0, 0,   0, 0, 0, 0};
		
		IndexedQuadArray carObject = new IndexedQuadArray(
				vertices.length, GeometryArray.COORDINATES | GeometryArray.COLOR_3, indices.length);
		carObject.setCoordinates(0, vertices);
		carObject.setCoordinateIndices(0, indices);
		carObject.setColors(0, colors);
		carObject.setColorIndices(0, colorIndices);
		
		Shape3D shape = new Shape3D(carObject);
		object.addChild(shape);
		
		//create tires
		
		return object;
	}
	
	private void init(Point3f startPoint, double direction) {
		Transform3D pointTransform3d = new Transform3D();
		Transform3D angleTransform3d = new Transform3D();
		
		Vector3f pointVector3f = new Vector3f(startPoint.x, startPoint.y, startPoint.z);
		pointTransform3d.setTranslation(pointVector3f);
		angleTransform3d.rotY(-direction);
		
		pointTransform3d.mul(angleTransform3d);
		carObjectGroup.setTransform(pointTransform3d);
	}

}
