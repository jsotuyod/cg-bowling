package bowling.menu;

import java.util.List;

import bowling.input.MenuItemListener;

import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.input.InputHandler;
import com.jme.input.Mouse;
import com.jme.input.MouseInput;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.MouseInputAction;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.game.state.BasicGameState;

/**
 * A generic game menu.
 */
public class Menu extends BasicGameState {

	private static final float FONT_SCALE = 4;
	private static final float SPACE_BETWEEN_ITEMS = 50;

	protected DisplaySystem display;
	
	protected Node cursor;
	
	protected InputHandler input;
	protected Mouse mouse;
	
	/**
	 * Creates a new menu.
	 * @param name The name of the menu being created.
	 * @param items The list of MenuItems to be included.
	 */
	public Menu(String name, List<MenuItem> items) {
		super(name);
		
		this.display = DisplaySystem.getDisplaySystem();
		
		initInput();
        initCursor();
        
        int counter = 0;
        for (MenuItem menuItem : items) {
        	Text menuItemNode = Text.createDefaultTextLabel(menuItem.getName(), menuItem.getDisplayText());
        	menuItemNode.setLocalScale(FONT_SCALE);
        	
        	// Compute the position of each item along the Y axis to make them centered
        	float totalMenuHeight = menuItemNode.getHeight() * items.size() + SPACE_BETWEEN_ITEMS * (items.size() - 1);
        	float positionY = display.getHeight() - (display.getHeight() - totalMenuHeight) / 2 - counter * (menuItemNode.getHeight() + SPACE_BETWEEN_ITEMS) - menuItemNode.getHeight() / 2;
        	
        	float positionX = (display.getWidth() - menuItemNode.getWidth()) / 2;
        	counter++;
        	
        	rootNode.attachChild(menuItemNode);
        	menuItemNode.setLocalTranslation(positionX, positionY, 0);
            
            // Add event handlers
            MenuItemListener listener = menuItem.getListener();
            if (listener != null) {
            	if (listener.getBindedKey() != null) {
            		input.addAction(listener, InputHandler.DEVICE_KEYBOARD, listener.getBindedKey(), InputHandler.AXIS_NONE, false);
            	}

            	input.addAction(new MouseItemSelector(listener, menuItemNode));
            }
		}
        
        // Make sure everything renders properly
        rootNode.setLightCombineMode(Spatial.LightCombineMode.Off);
        rootNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        rootNode.updateRenderState();
        rootNode.updateGeometricState(0, true);
	}
	
	/**
	 * Creates a pretty cursor.
	 */
	private void initCursor() {		
		Texture texture =
	        TextureManager.loadTexture(
	    	        "resources/textures/cursor1.png",
	    	        Texture.MinificationFilter.Trilinear,
	    	        Texture.MagnificationFilter.Bilinear);
		
		TextureState ts = display.getRenderer().createTextureState();
		ts.setTexture(texture);
		
		BlendState alpha = display.getRenderer().createBlendState();
		alpha.setBlendEnabled(true);
		alpha.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		alpha.setDestinationFunction(BlendState.DestinationFunction.One);
		alpha.setTestEnabled(true);
		alpha.setTestFunction(BlendState.TestFunction.GreaterThan);
		alpha.setEnabled(true);
		
		mouse.setRenderState(ts);
        mouse.setRenderState(alpha);
		
		cursor = new Node("Cursor");
		cursor.attachChild( mouse );
		
		rootNode.attachChild(cursor);
	}
	
	/**
	 * Inits the input handler we will use for navigation of the menu.
	 */
	protected void initInput() {
		input = new InputHandler();

        mouse = new AbsoluteMouse("Mouse Input", display.getWidth(),
                display.getHeight());
        mouse.registerWithInputHandler( input );
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jmex.game.state.BasicGameState#update(float)
	 */
	@Override
	public void update(float tpf) {
		input.update(tpf);
		super.update(tpf);
	}
	
	/**
	 * Mouse selector for menu items-
	 */
	class MouseItemSelector extends MouseInputAction {

		protected MenuItemListener listener;
		protected Text textNode;
		
		/**
		 * Creates a new mouse selector for menu items.
		 * @param listener The listener fot he menu item.
		 * @param textNode The text node for the item.
		 */
		public MouseItemSelector(MenuItemListener listener, Text textNode) {
			super();
			this.listener = listener;
			this.textNode = textNode;
		}

		/*
		 * (non-Javadoc)
		 * @see com.jme.input.action.InputActionInterface#performAction(com.jme.input.action.InputActionEvent)
		 */
		@Override
		public void performAction(InputActionEvent evt) {
			
			MouseInput input = MouseInput.get();
			
			if (input.isButtonDown(0)) {	// It's a click!
				// Check if the cursor seems to be somewhere over the text...
				
				// Get distance from cursor node to text node
				Vector3f distance = (new Vector3f(input.getXAbsolute(), input.getYAbsolute(), 0)).subtractLocal(this.textNode.getWorldTranslation());
				
				if (distance.x > 0 && distance.x < this.textNode.getWidth()
						&& distance.y > 0 && distance.y < this.textNode.getHeight()) {
					// Trigger event!
	            	listener.performAction(evt);
				}
			}
		}
	}
}
