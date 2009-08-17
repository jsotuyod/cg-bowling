package bowling.logic.domain;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;

public class Ball {

	protected Node parent;
	protected DynamicPhysicsNode node;
	
	protected Vector3f originalPos;

	public Ball(DynamicPhysicsNode node, Vector3f originalPos) {
		super();
		this.node = node;
		this.originalPos = originalPos;
		this.parent = node.getParent();
		
		this.reset();
	}
	
	public void reset() {
		this.node.setLocalTranslation(originalPos.clone());
	}
	
	public boolean isBeyondPoint(Vector3f point) {
		return this.node.getLocalTranslation().subtract(this.originalPos).length() > point.subtract(this.originalPos).length();
	}

	public DynamicPhysicsNode getNode() {
		return node;
	}
}
