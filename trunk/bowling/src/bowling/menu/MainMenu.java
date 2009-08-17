package bowling.menu;

import bowling.input.MenuInputHandler;

import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.input.InputHandler;
import com.jme.input.Mouse;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.game.state.BasicGameState;

public class MainMenu extends BasicGameState {

	protected DisplaySystem display;
	
	protected Node cursor;
	
	protected InputHandler input;
	protected Mouse mouse;
	
	public MainMenu() {
		super("main menu");
		
		this.display = DisplaySystem.getDisplaySystem();
        
		initInput();
        initCursor();
        
        Text startGameNode = Text.createDefaultTextLabel("start", "Start game");
        startGameNode.setLocalTranslation(50, 200, 0);
        rootNode.attachChild(startGameNode);
		
        // TODO : Add menu items
//		Text startGameNode = Text.createDefaultTextLabel("start", "Start game");
//		startGameNode.setLocalTranslation(50, 200, 0);
//		rootNode.attachChild(startGameNode);
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
		input = new MenuInputHandler(this);

        DisplaySystem display = DisplaySystem.getDisplaySystem();
        mouse = new AbsoluteMouse("Mouse Input", display.getWidth(),
                display.getHeight());
        mouse.registerWithInputHandler( input );
	}
	
	@Override
	public void update(float tpf) {
		input.update(tpf);
		super.update(tpf);
	}
}
