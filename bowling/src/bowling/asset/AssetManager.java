package bowling.asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bowling.utils.ScToJme;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.physics.PhysicsNode;

/**
 * An asset manager for the bowling game.
 */
public class AssetManager {

	final private static String RESOURCE_PATH = "resources" + File.separatorChar;
	final private static String MODELS_PATH = RESOURCE_PATH + "models" + File.separatorChar;
	final private static String TEXTURES_PATH = RESOURCE_PATH + "textures" + File.separatorChar;
	
	final private static String PIN_SC_MODEL_PATH = MODELS_PATH + "pin.sc";
	final private static String PIN_JME_MODEL_PATH = MODELS_PATH + "pin.jme";
	
	final private static String BALL_SC_MODEL_PATH = MODELS_PATH + "ball.sc";
	final private static String BALL_JME_MODEL_PATH = MODELS_PATH + "ball.jme";
	
	final private static String LANE_SC_MODEL_PATH = MODELS_PATH + "lane.sc";
	final private static String LANE_JME_MODEL_PATH = MODELS_PATH + "lane.jme";
	
	final private static String CURSOR_TEXTURE_PATH = TEXTURES_PATH + "cursor1.png";
	
	final private static String POWERMETER_CONTAINER_TEXTURE_PATH = TEXTURES_PATH + "powermeter-container.png";
	final private static String POWERMETER_BAR_TEXTURE_PATH = TEXTURES_PATH + "powermeter-bar.png";
	
	final private static String DIRECTIONMETER_CONTAINER_TEXTURE_PATH = TEXTURES_PATH + "directionmeter-container.png";
	final private static String DIRECTIONMETER_AIM_TEXTURE_PATH = TEXTURES_PATH + "directionmeter-aim.png";
	
	final private static String ANGLEMETER_TEXTURE_PATH = TEXTURES_PATH + "anglemeter-container.png";
	
	
	private static AssetManager instance;
	
	/**
	 * Creates a new AssetManager instance.
	 */
	private AssetManager() {
		
		// Convert all models to JME
		this.convertModels();
	}
	
	/**
	 * Retrieves the one and only instance of AssetManager.
	 * @return The only instance of AssetManager.
	 */
	public static AssetManager getInstance() {
		if (instance == null) {
			instance = new AssetManager();
		}
		
		return instance;
	}
	
	/**
	 * Converts all models from SC to JME.
	 */
	private void convertModels() {
		
		ScToJme converter = new ScToJme();
		
		try {
			InputStream in = new FileInputStream(new File(PIN_SC_MODEL_PATH));
			OutputStream out = new FileOutputStream(new File(PIN_JME_MODEL_PATH));
			converter.convert(in, out);
			
			in = new FileInputStream(new File(BALL_SC_MODEL_PATH));
			out = new FileOutputStream(new File(BALL_JME_MODEL_PATH));
			converter.convert(in, out);
			
			in = new FileInputStream(new File(LANE_SC_MODEL_PATH));
			out = new FileOutputStream(new File(LANE_JME_MODEL_PATH));
			converter.convert(in, out);
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Loads a model into the given node.
	 * @param path The path of the model to be loaded.
	 * @param parent The node to which to add the loaded model.
	 * @param generatePhysics Wether if the physics should be generated or not.
	 */
	private void loadModel(String path, Node parent, boolean generatePhysics) {
		
        // load the model
		JMEImporter importer = new BinaryImporter();
        try {
			Node model = (Node) importer.load(new File(path));
			
			parent.attachChild(model);
			
			if (generatePhysics && parent instanceof PhysicsNode) {
				// Generate physics
				model.setModelBound(new BoundingBox());
				model.updateModelBound();
				((PhysicsNode) parent).generatePhysicsGeometry();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Loads the ball model.
	 * @param parent The node to which to add the ball model.
	 * @param generatePhysics Wether if the physics should be generated or not.
	 */
	public void loadBall(Node parent, boolean generatePhysics) {
		
		this.loadModel(BALL_JME_MODEL_PATH, parent, generatePhysics);
	}
	
	/**
	 * Loads the pin model.
	 * @param parent The node to which to add the pin model.
	 * @param generatePhysics Wether if the physics should be generated or not.
	 */
	public void loadPin(Node parent, boolean generatePhysics) {
		
		this.loadModel(PIN_JME_MODEL_PATH, parent, generatePhysics);
	}
	
	/**
	 * Loads the lane model.
	 * @param parent The node to which to add the lane model.
	 * @param generatePhysics Wether if the physics should be generated or not.
	 */
	public void loadLane(Node parent, boolean generatePhysics) {
		
		this.loadModel(LANE_JME_MODEL_PATH, parent, generatePhysics);
	}
	
	/**
	 * Loads the requested texture into the given node.
	 * @param path The path to the texture to be loaded.
	 * @param container The spatial into which to load the texture.
	 */
	private void loadTexture(String path, Spatial container) {
		Texture texture = TextureManager.loadTexture(path,
	    	        Texture.MinificationFilter.Trilinear,
	    	        Texture.MagnificationFilter.Bilinear);
		
		TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		ts.setTexture(texture);
		
		BlendState alpha = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
		alpha.setBlendEnabled(true);
		alpha.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		alpha.setDestinationFunction(BlendState.DestinationFunction.One);
		alpha.setTestEnabled(true);
		alpha.setTestFunction(BlendState.TestFunction.GreaterThan);
		alpha.setEnabled(true);
		
		container.setRenderState(ts);
		container.setRenderState(alpha);
	}
	
	/**
	 * Loads the cursor texture into the given node.
	 * @param container The spatial into which to load the texture.
	 */
	public void loadCursorTexture(Spatial container) {
		
		this.loadTexture(CURSOR_TEXTURE_PATH, container);
	}
	
	/**
	 * Loads the power meter container texture into the given node.
	 * @param container The spatial into which to load the texture.
	 */
	public void loadPowerMeterContainer(Spatial container) {
		
		this.loadTexture(POWERMETER_CONTAINER_TEXTURE_PATH, container);
	}
	
	/**
	 * Loads the power meter bar texture into the given node.
	 * @param container The spatial into which to load the texture.
	 */
	public void loadPowerMeterBar(Spatial container) {
		
		this.loadTexture(POWERMETER_BAR_TEXTURE_PATH, container);
	}

	/**
	 * Loads the direction meter container texture into the given node.
	 * @param container The spatial into which to load the texture.
	 */
	public void loadDirectionMeterContainer(Spatial container) {
		
		this.loadTexture(DIRECTIONMETER_CONTAINER_TEXTURE_PATH, container);
	}

	/**
	 * Loads the direction meter aim texture into the given node.
	 * @param container The spatial into which to load the texture.
	 */
	public void loadDirectionMeterAim(Spatial container) {
		
		this.loadTexture(DIRECTIONMETER_AIM_TEXTURE_PATH, container);
	}

	/**
	 * Loads the angle meter texture into the given node.
	 * @param container The spatial into which to load the texture.
	 */
	public void loadAngleMeterContainer(Spatial container) {
		
		this.loadTexture(ANGLEMETER_TEXTURE_PATH, container);
	}
}
