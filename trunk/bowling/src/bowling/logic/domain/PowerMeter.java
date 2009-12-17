package bowling.logic.domain;

import bowling.asset.AssetManager;

import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.system.DisplaySystem;

/**
 * The power meter bar.
 */
public class PowerMeter extends GameDisplay {

	final private static float CYCLE_TIME = 2;
	final private static float HALF_CYCLE_TIME = CYCLE_TIME / 2;
	
	final private static float MAX_POWER = 20000;
	final private static float MIN_POWER = 7000;
	
	protected Node barNode;
	
	/**
	 * Creates a new power meter instance.
	 */
	public PowerMeter() {
		super("powermeter container");
	}

	/*
	 * (non-Javadoc)
	 * @see bowling.logic.domain.GameDisplay#init()
	 */
	@Override
	protected void init() {
		super.init();
		
		// Load the power meter container
		Quad quad = new Quad("powermeter container texture", 20, 200);
		AssetManager.getInstance().loadPowerMeterContainer(quad);
		this.containerNode.attachChild(quad);
		
		// Load the meter bar
		this.barNode = new Node("powermeter bar");
		quad = new Quad("powermeter bar texture", 20, 200);
		AssetManager.getInstance().loadPowerMeterBar(quad);
		quad.setLocalTranslation(0, 100, 0);	// Place it at the top
		this.barNode.attachChild(quad);
		
		this.barNode.setLocalTranslation(0, -100, 0);	// Compensate the quad translation.. The combined effect is that resizing will not move the base of the bar.
		
		this.containerNode.attachChild(this.barNode);
		
		this.containerNode.setLocalTranslation(DisplaySystem.getDisplaySystem().getWidth() - 60, 200, 0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see bowling.logic.domain.GameDisplay#simpleUpdate()
	 */
	@Override
	public void simpleUpdate() {
		
		currentTick = currentTick % CYCLE_TIME;
		
		if (currentTick > HALF_CYCLE_TIME) {
			this.barNode.getLocalScale().y = (CYCLE_TIME - currentTick) / HALF_CYCLE_TIME;
		} else {
			this.barNode.getLocalScale().y = currentTick / HALF_CYCLE_TIME;
		}
	}
	
	/**
	 * Retrieves the power indicated by the power meter.
	 * @return The power indicated by the power meter.
	 */
	public float getPower() {
		
		return MIN_POWER + (MAX_POWER - MIN_POWER) * this.barNode.getLocalScale().y;
	}
}
