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
		this.node.setLocalTranslation(originalPos.clone());
		
		Quaternion q = new Quaternion();
		q.fromAngles((float) -Math.PI/2, 0, 0);
		this.node.setLocalRotation(q);
	}
	
	public boolean isFallen() {
		if (node.getLocalTranslation().y < originalPos.y - 0.01f) {
			return true;
		}
		
		return false;
	}
}
