package bowling.logic.domain;

import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;

/**
 * Abstract display class. Allows to place controls outside the main world tree.
 */
public abstract class GameDisplay {

	protected Node containerNode;
	
	protected float currentTick;
	
	protected boolean visible;
	protected boolean paused;
	
	/**
	 * Creates a new instance of GameDisplay.
	 * @param name The name of the root node to be created.
	 */
	public GameDisplay(String name) {
		// This will be our root node for the display
		this.containerNode = new Node(name);
		
		this.visible = false;
		this.paused = true;
		
		this.currentTick = 0;
		
		this.init();
		
		containerNode.setCullHint(Spatial.CullHint.Never);
		containerNode.setLightCombineMode(Spatial.LightCombineMode.Off);
		containerNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		containerNode.updateRenderState();
		containerNode.updateGeometricState(0, true);
	}
	
	/**
	 * Performs one time initialization.
	 */
	protected void init() {
		// Let subclasses implement it
	}
	
	/**
	 * Shows or hides the display bar.
	 * @param visible True if the display should be visible, false otherwise.
	 */
	public void setVisible(boolean visible) {
		
		this.visible = visible;
	}
	
	/**
	 * Retrieves the current display's visibility setting.
	 * @return True if the display is visible, false otherwise.
	 */
	public boolean isVisible() {
		return this.visible;
	}
	
	/**
	 * Updates the display animation-
	 * @param tpf The current ticks per frame.
	 */
	public final void update(float tpf) {
		
		if (!this.paused) {
			currentTick += tpf;
			
			this.simpleUpdate();
			
			// Update the node!
			this.containerNode.updateGeometricState(tpf, true);
		}
	}
	
	/**
	 * Updates the internal state of the display.
	 */
	protected void simpleUpdate() {
		// Let subclasses implement it
	}
	
	/**
	 * Renders the display. It's only actually displayed if visible.
	 * @param tpf The current ticks per frame.
	 */
	public void render(float tpf) {
		
		if (this.visible) {
			DisplaySystem.getDisplaySystem().getRenderer().draw(this.containerNode);
		}
	}

	/**
	 * Retrieves wether the display is paused or not.
	 * @return True if the display is paused, false otherwise.
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Sets the display paused state-
	 * @param paused True if the display should be paused, false otherwise.
	 */
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	/**
	 * Resets the display, resuming it.
	 */
	public void reset() {
		this.currentTick = 0;
		
		this.setPaused(false);
	}
}
