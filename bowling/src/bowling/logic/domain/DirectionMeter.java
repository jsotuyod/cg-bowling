package bowling.logic.domain;

import bowling.asset.AssetManager;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.system.DisplaySystem;

/**
 * The direction meter bar.
 */
public class DirectionMeter extends GameDisplay {

	final private static float CYCLE_TIME = 2;
	final private static float HALF_CYCLE_TIME = CYCLE_TIME / 2;
	
	/**
	 * Maximum direction in the x axis.
	 */
	final private static float MAX_ANGLE = (float) Math.PI / 12;
	
	protected Node aimNode;
	
	/**
	 * Creates a new direction meter instance.
	 */
	public DirectionMeter() {
		super("powermeter container");
	}
	
	/*
	 * (non-Javadoc)
	 * @see bowling.logic.domain.GameDisplay#init()
	 */
	@Override
	protected void init() {
		super.init();
		
		// Load the direction meter container
		Quad quad = new Quad("directionmeter container texture", 200, 30);
		AssetManager.getInstance().loadDirectionMeterContainer(quad);
		this.containerNode.attachChild(quad);
		
		// Load the meter bar
		this.aimNode = new Node("directionmeter aim");
		quad = new Quad("directionmeter aim texture", 11, 12);
		AssetManager.getInstance().loadDirectionMeterAim(quad);
		this.aimNode.attachChild(quad);
		
		this.aimNode.setLocalTranslation(100, 0, 0);	// Center the aim node
		
		this.containerNode.attachChild(this.aimNode);
		
		this.containerNode.setLocalTranslation(DisplaySystem.getDisplaySystem().getWidth() - 200, 110, 0);
		
		this.currentTick = HALF_CYCLE_TIME / 2;
	}
	
	/*
	 * (non-Javadoc)
	 * @see bowling.logic.domain.GameDisplay#simpleUpdate()
	 */
	@Override
	public void simpleUpdate() {
		
		currentTick = currentTick % CYCLE_TIME;
		
		if (currentTick < HALF_CYCLE_TIME) {
			this.aimNode.getLocalTranslation().x = (currentTick / HALF_CYCLE_TIME) * 180 - 90;
		} else {
			this.aimNode.getLocalTranslation().x = 90 - ((currentTick - HALF_CYCLE_TIME) / HALF_CYCLE_TIME ) * 180;
		}
	}
	
	/**
	 * Retrieves the current direction.
	 * @return The current direction
	 */
	public Vector3f getDirection() {
		
		float dirX = this.aimNode.getLocalTranslation().x;
		float angle = MAX_ANGLE * (dirX / 90);
		
		return new Vector3f((float) -Math.sin(angle), 0, (float) Math.cos(angle));
	}
}
