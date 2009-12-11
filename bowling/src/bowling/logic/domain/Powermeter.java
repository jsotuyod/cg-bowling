package bowling.logic.domain;

import bowling.asset.AssetManager;

import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.system.DisplaySystem;

/**
 * The powermeter bar.
 */
public class Powermeter {

	final private static float CYCLE_TIME = 2;
	final private static float HALF_CYCLE_TIME = CYCLE_TIME / 2;
	
	protected Node containerNode;
	protected Node barNode;
	
	protected float currentTick;
	
	protected boolean visible;
	
	/**
	 * Creates a new powermeter instance.
	 */
	public Powermeter() {
		super();
		
		// This will be our root node for powermeter
		this.containerNode = new Node("powermeter container");
		
		// Load the powermeter container
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
		
		containerNode.setCullHint(Spatial.CullHint.Never);
		containerNode.setLightCombineMode(Spatial.LightCombineMode.Off);
		containerNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		containerNode.updateRenderState();
		containerNode.updateGeometricState(0, true);
		
		this.visible = false;
	}
	
	/**
	 * Shows or hides the powermeter bar.
	 * @param visible True if the powermeter should be visible, false otherwise.
	 */
	public void setVisible(boolean visible) {
		
		this.visible = visible;
	}
	
	/**
	 * Updates the powermeter animation-
	 * @param tpf The current ticks per frame.
	 */
	public void update(float tpf) {
		
		if (this.visible) {
			currentTick += tpf;
			
			currentTick = currentTick % CYCLE_TIME;
			
			if (currentTick > HALF_CYCLE_TIME) {
				this.barNode.getLocalScale().y = (CYCLE_TIME - currentTick) / HALF_CYCLE_TIME;
			} else {
				this.barNode.getLocalScale().y = currentTick / HALF_CYCLE_TIME;
			}
			
			// Update the node!
			this.containerNode.updateGeometricState(tpf, true);
		}
	}
	
	/**
	 * Rendes the powermeter. It's only actually displayed if visible.
	 * @param tpf The current ticks per frame.
	 */
	public void render(float tpf) {
		
		if (this.visible) {
			DisplaySystem.getDisplaySystem().getRenderer().draw(this.containerNode);
		}
	}
}
