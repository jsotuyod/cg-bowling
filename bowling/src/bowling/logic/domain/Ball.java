package bowling.logic.domain;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;

/**
 * A bowling ball.
 */
public class Ball {

	private static final float VELOCITY_THRESHOLD = 0.5f;
	
	protected Node parent;
	protected DynamicPhysicsNode node;
	
	protected Vector3f originalPos;

	/**
	 * Creates a new ball instance.
	 * @param node The physic node of the ball.
	 * @param originalPos The position from which to throw the ball.
	 */
	public Ball(DynamicPhysicsNode node, Vector3f originalPos) {
		super();
		this.node = node;
		this.originalPos = originalPos;
		this.parent = node.getParent();
		
		this.reset();
	}
	
	/**
	 * Resets the ball back to it's original position.
	 */
	public void reset() {
		this.node.setLocalTranslation(originalPos.clone());
		this.node.clearDynamics();
	}
	
	/**
	 * Checks if the ball is beyond the given point in space.
	 * @param point The point to check against.
	 * @return True if the ball is beyond the given point, false otherwise.
	 */
	public boolean isBeyondPoint(Vector3f point) {
		return this.node.getLocalTranslation().subtract(this.originalPos).length() > point.subtract(this.originalPos).length();
	}

	/**
	 * Retrieves the ball's physic node.
	 * @return The ball's physic node.
	 */
	public DynamicPhysicsNode getNode() {
		return node;
	}
	
	/**
	 * Checks if the ball has stopped.
	 * @return True if the ball has stoped moving, false otherwise.
	 */
	public boolean hasStopped() {
		return this.node.getLinearVelocity(null).length() < VELOCITY_THRESHOLD;
	}
}
