package bowling.logic.domain;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;


public class Pin {

	protected Node parent;
	protected DynamicPhysicsNode node;
	
	protected Vector3f originalPos;

	public Pin(DynamicPhysicsNode node, Vector3f originalPos) {
		super();
		this.node = node;
		this.originalPos = originalPos;
		this.parent = node.getParent();
		
		this.reset();
	}
	
	public void place() {
		if (!this.isFallen()) {
			this.reset();
		} else {
			// remove it from view
			node.removeFromParent();
		}
	}
	
	public void reset() {
		this.node.setLocalTranslation(originalPos);
		
		Quaternion q = new Quaternion();
		q.fromAngles((float) -Math.PI/2, 0, 0);
		this.node.setLocalRotation(q);
	}
	
	public boolean isFallen() {
		float[] angles = node.getLocalRotation().toAngles(null);
		
		if (Math.abs(angles[0] + Math.PI / 2) > Math.PI / 6) {
			return true;
		}
		
		if (Math.abs(angles[2]) > Math.PI / 6) {
			return true;
		}
		
		// TODO : Considerar que puede estar "parado" mientras cae al vacio o en la caja del fondo....
		
		return false;
	}
}
