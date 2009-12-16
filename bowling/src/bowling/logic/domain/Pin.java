package bowling.logic.domain;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;

/**
 * A bowling pin.
 */
public class Pin {

	private static final float VELOCITY_THRESHOLD = 0.5f;

	private static final float POSITION_THRESHOLD = 0.01f;

	private static final float ANGLE_THRESHOLD = 0.2f;
	
	
	protected Node parent;
	protected DynamicPhysicsNode node;
	
	protected Vector3f originalPos;

	/**
	 * Creates a new pin instance.
	 * @param node The physic node for the pin.
	 * @param originalPos Te position where the pin is to be placed. 
	 */
	public Pin(DynamicPhysicsNode node, Vector3f originalPos) {
		super();
		this.node = node;
		this.originalPos = originalPos;
		this.parent = node.getParent();
		
		this.reset();
	}
	
	/**
	 * Places the pin for the next throw (either removes it if fallen, or resets it's position if standing)
	 */
	public void place() {
		if (!this.isFallen()) {
			this.reset();
		} else {
			// remove it from view
			node.removeFromParent();
		}
	}
	
	/**
	 * Resets the pin back to the scene tree and in it's initial position.
	 */
	public void reset() {
		this.node.setLocalTranslation(originalPos.clone());
		
		Quaternion q = new Quaternion();
		q.fromAngles((float) -Math.PI/2, 0, 0);
		this.node.setLocalRotation(q);
		
		this.node.clearDynamics();
		
		if (this.node.getParent() == null) {
			this.parent.attachChild(this.node);
		}
	}
	
	/**
	 * Checks if the pin has fallen or not.
	 * @return True if the pin has fallen, false otherwise.
	 */
	public boolean isFallen() {
		// Check position
		if (node.getLocalTranslation().y < originalPos.y - POSITION_THRESHOLD) {
			return true;
		}
		
		// Check pitch
		float[] angles = node.getLocalRotation().toAngles(null);
		if (angles[2] > ANGLE_THRESHOLD) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if the pin is on the scene or not.
	 * @return True if the pin is on the scene, false otherwise.
	 */
	public boolean isOnScene() {
		return (this.node.getParent() != null);
	}
	
	/**
	 * Checks if the pin has stopped moving.
	 * @return True if the pin has stopped moving, false otherwise.
	 */
	public boolean hasStopped() {
		return this.node.getLinearVelocity(null).length() < VELOCITY_THRESHOLD;
	}
}
