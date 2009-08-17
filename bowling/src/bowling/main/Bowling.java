package bowling.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import bowling.input.InputHandler;
import bowling.logic.domain.Ball;
import bowling.logic.domain.Pin;
import bowling.menu.MainMenu;
import bowling.utils.ScToJme;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Sphere;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.contact.MutableContactInfo;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.SimplePhysicsGame;

public class Bowling extends SimplePhysicsGame {

	private Ball ball;
	private List<Pin> pins;
	
	private InputHandler inputHandler;
	
	final private static String RESOURCE_PATH = "resources" + File.separatorChar;
	final private static String MODELS_PATH = RESOURCE_PATH + "models" + File.separatorChar;
	
	final private static String PIN_SC_MODEL_PATH = MODELS_PATH + "pin.sc";
	final private static String PIN_JME_MODEL_PATH = MODELS_PATH + "pin.jme";
	
	final private static String BALL_SC_MODEL_PATH = MODELS_PATH + "ball.sc";
	final private static String BALL_JME_MODEL_PATH = MODELS_PATH + "ball.jme";
	
	final private static String LANE_SC_MODEL_PATH = MODELS_PATH + "lane.sc";
	final private static String LANE_JME_MODEL_PATH = MODELS_PATH + "lane.jme";
	
	final private static int PIN_COUNT = 10;
	
	final private static float PIN_DISTANCE = 1.3f;
	final private static float PIN_OFFSET = 30f;
	
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
	
	protected void simpleInitGame() {
		// Creates the GameStateManager. Only needs to be called once.
		GameStateManager.create();
		
		this.convertModels();
		
		this.setUpMainMenu();
		
		// TODO : add other game states to the managaer
		
		// TODO : Move everything to the play game state
		// Position camera
		this.setupCamera();
		
        // Create Needed objects
		this.createLine();
		this.createPins();
		this.createBowlingBall();
        
        // Set up scene for game
		this.setupScene();
        
		this.setupInputHandler();
        
		statNode.attachChild( this.inputHandler.getInstructions() );
		
        showPhysics = true;
    }

	@Override
	protected void preRender() {
		GameStateManager.getInstance().update(tpf);
		GameStateManager.getInstance().render(tpf);
    }
	
    private void setUpMainMenu() {
		GameState menu = new MainMenu();
		menu.setActive(true);
		GameStateManager.getInstance().attachChild(menu);
	}

	private void setupInputHandler() {
		this.inputHandler = new InputHandler(input);
		this.inputHandler.setUp(this.ball.getNode());
	}

	private void createLine() {
    	StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();
        rootNode.attachChild( staticNode );

        this.loadModel(LANE_JME_MODEL_PATH, staticNode, true);
	}

	private void createPins() {
    	
    	if ( null != this.pins ) {
    		return;
    	}
    	
    	this.pins = new LinkedList<Pin>();
    	
    	float x[] = {0, -0.5f, 0.5f, -1f, 0, 1f, -1.5f, -0.5f, 0.5f, 1.5f};
    	float z[] = {0, 1f, 1f, 2f, 2f, 2f, 3f, 3f, 3f, 3f};
    		
    	// Create the pins
        for ( int i = 0; i < PIN_COUNT; i++ ) {
        	
        	DynamicPhysicsNode pin = getPhysicsSpace().createDynamicNode();
        	pin.setName("pin" + i);
        	rootNode.attachChild(pin);
        	
        	this.loadModel(PIN_JME_MODEL_PATH, pin, true);
        	
        	pin.setLocalScale(0.25f);
        	pin.setMaterial(Material.WOOD);
        	pin.computeMass();
        	
        	Vector3f originalPos = new Vector3f(x[i] * PIN_DISTANCE, 1.32f, z[i] * PIN_DISTANCE + PIN_OFFSET);
        	this.pins.add(new Pin(pin, originalPos));
        }
	}

	private void createBowlingBall() {
    	
		if (null != this.ball) {
			return;
		}
		
    	// Create the node
		DynamicPhysicsNode ball;
    	ball = getPhysicsSpace().createDynamicNode();
        ball.setName("ball");
        rootNode.attachChild(ball);
        
        // Set the physic properties
        final Sphere sphere = new Sphere("ball-geom", 100, 100, 5f);
        ball.attachChild(sphere);
        ball.setLocalScale(0.1f);
        
        ball.generatePhysicsGeometry();
        ball.setMaterial(createMaterial("ball", 20f, 0.05f, 0f));
        ball.computeMass();
        
        // Add the model, with no physics, to make it look nice
        this.loadModel(BALL_JME_MODEL_PATH, ball, false);
        
        this.ball = new Ball(ball, new Vector3f(0f, 1f, -32f));
	}

	private void setupCamera() {
    	cam.setLocation(new Vector3f (10f, 20f, -70f));
        cam.lookAt(new Vector3f(0f, 1.5f, 41f), new Vector3f (0, 1, 0));
	}
    
    private void setupScene() {
    	setupPins();
    	setupBowlingBall();
	}

	private void setupPins(){

    	for ( int i = 0; i < PIN_COUNT; i++ ) {
    		
    		Pin pin = this.pins.get(i);
    		pin.place();
    	}
    }

	private void setupBowlingBall() {
		this.ball.reset();
	}
    
    private Material createMaterial(String name, float density, float mu, float bounce) {
    	Material ballMaterial = new Material(name);
    	ballMaterial.setDensity(density);
    	MutableContactInfo contactDetails = new MutableContactInfo();
    	contactDetails.setBounce(bounce);
    	contactDetails.setMu(mu);
    	ballMaterial.putContactHandlingDetails( Material.DEFAULT, contactDetails );

    	return ballMaterial;
    }
    
    /**
     * The main method to allow starting this class as application.
     *
     * @param args command line arguments
     */
    public static void main( String[] args ) {
    	new Bowling().start();
    }
}
