package bowling.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import bowling.utils.ScToJme;

import com.jme.bounding.BoundingBox;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.contact.MutableContactInfo;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.SimplePhysicsGame;

public class Bowling extends SimplePhysicsGame {

	private DynamicPhysicsNode ball;
	private List<DynamicPhysicsNode> pins;
	//TODO: hacer una clase BowlingLine que contenga todo esto
	private PhysicsBox lineFloor, rightGutter, leftGutter, backGutter, rightWall, leftWall, backWall;
	private int forceMagnitude;
	
	final private static String RESOURCE_PATH = "resources" + File.separatorChar;
	final private static String MODELS_PATH = RESOURCE_PATH + "models" + File.separatorChar;
	
	final private static String PIN_SC_MODEL_PATH = MODELS_PATH + "pin.sc";
	final private static String PIN_JME_MODEL_PATH = MODELS_PATH + "pin.jme";
	
	final private static String BALL_SC_MODEL_PATH = MODELS_PATH + "ball.sc";
	final private static String BALL_JME_MODEL_PATH = MODELS_PATH + "ball.jme";
	final private static int PIN_COUNT = 10;
	
	final private static float PIN_DISTANCE = 1.3f;
	final private static float PIN_OFFSET = 30f;
	
	private void convertModels() {
		
		ScToJme converter = new ScToJme();
		
		try {
			// TODO : Convert all sc files to jme
			InputStream in = new FileInputStream(new File(PIN_SC_MODEL_PATH));
			OutputStream out = new FileOutputStream(new File(PIN_JME_MODEL_PATH));
			converter.convert(in, out);
			
			in = new FileInputStream(new File(BALL_SC_MODEL_PATH));
			out = new FileOutputStream(new File(BALL_JME_MODEL_PATH));
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
		this.convertModels();
		
		// TODO : Replace this for actual loading of the models
		
		// Position camera
		setupCamera();
		
        // Create Needed objects
        createLine();
        createPins();
        createBowlingBall();
        
        // Set up scene for game
        setupScene();
        
        input.addAction( new ApplyForceAction(), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_SPACE, InputHandler.AXIS_NONE, false );
        input.addAction( new SetForceAction(), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_F, InputHandler.AXIS_NONE, true );
        
        Text label = Text.createDefaultTextLabel( "instructions", "[f] to increase force. [space bar] to throw the ball." );
        label.setLocalTranslation( 0, 20, 0 );
        statNode.attachChild( label );
        
        showPhysics = true;
    }

	//TODO: sacar esta clase afuera, meterla en un package aparte
	private class ApplyForceAction extends InputAction {

        public ApplyForceAction() {
        }

        public void performAction( InputActionEvent evt ) {
            if ( evt.getTriggerPressed() ) {
                // key goes down - apply motion
            	Bowling.this.ball.addForce( new Vector3f(0, 0, Bowling.this.forceMagnitude) );
            	
            	Text label = Text.createDefaultTextLabel( "force", "Force applied: " + Bowling.this.forceMagnitude );
                label.setLocalTranslation( 0, 5, 0 );
                statNode.attachChild( label );
            }
        }
    }
	
	//TODO: sacar esta clase afuera, meterla en un package aparte	
	private class SetForceAction extends InputAction {
		private static final int FORCE_STEP = 50;
		private static final int FORCE_MIN = 0;
		private static final int FORCE_MAX = 20000;

		public SetForceAction( ) {
			Bowling.this.forceMagnitude = FORCE_MIN;
        }

        public void performAction( InputActionEvent evt ) {
            if ( evt.getTriggerAllowsRepeats() && Bowling.this.forceMagnitude < FORCE_MAX) {
            	Bowling.this.forceMagnitude += FORCE_STEP;
            }
        }
	}	
	
    private void createLine() {
    	StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();
        rootNode.attachChild( staticNode );

        this.lineFloor = staticNode.createBox( "floor");

        this.leftGutter = staticNode.createBox( "leftGutter" ); 
        this.rightGutter = staticNode.createBox( "rightGutter" ); 
        this.backGutter = staticNode.createBox( "backGutter" ); 
        
        this.leftWall = staticNode.createBox( "leftWall" ); 
        this.rightWall = staticNode.createBox( "rightWall" ); 
        this.backWall = staticNode.createBox( "backWall" ); 
	}

	private void createPins() {
    	
    	if ( null != this.pins ) {
    		return;
    	}
    	
    	this.pins = new LinkedList<DynamicPhysicsNode>();
    	
    	// Create the pins
        for ( int i = 0; i < PIN_COUNT; i++ ) {
        	
        	DynamicPhysicsNode pin = getPhysicsSpace().createDynamicNode();
        	pin.setName("pin" + i);
        	rootNode.attachChild(pin);
        	
        	this.loadModel(PIN_JME_MODEL_PATH, pin, true);
        	
        	pin.setLocalScale(0.25f);
        	pin.setMaterial(Material.WOOD);
        	pin.computeMass();
        	
        	this.pins.add(pin);
        }
	}

	private void createBowlingBall() {
    	
		if (null != this.ball) {
			return;
		}
		
    	// Create the node
    	this.ball = getPhysicsSpace().createDynamicNode();
        this.ball.setName("ball");
        rootNode.attachChild(this.ball);
        
        // Set the physic properties
        final Sphere sphere = new Sphere("ball-geom", 100, 100, 5f);
        color(sphere, ColorRGBA.blue);
        this.ball.attachChild(sphere);
        this.ball.setLocalScale(0.1f);
        
        this.ball.generatePhysicsGeometry();
        this.ball.setMaterial(createMaterial("ball", 20f, 0.05f, 0f));
        this.ball.computeMass();
        
        // Add the model, with no physics, to make it look nice
        this.loadModel(BALL_JME_MODEL_PATH, ball, false);
	}

	private void setupCamera() {
    	cam.setLocation(new Vector3f (10f, 20f, -70f));
        cam.lookAt(new Vector3f(0f, 1.5f, 41f), new Vector3f (0, 1, 0)); //0, 1.5, -3
	}
    
    private void setupScene() {
    	setupLine();
    	setupPins();
    	setupBowlingBall();
	}
    
    private void setupLine() {
    	this.lineFloor.getLocalScale().set( 5, 1f, 70 ); //TODO: ver que material le damos al piso

    	this.leftGutter.getLocalScale().set( 2, 0.6f, 70 );
    	this.leftGutter.setLocalTranslation(3.5f, -0.2f, 0);

    	this.rightGutter.getLocalScale().set( 2, 0.6f, 70 );
    	this.rightGutter.setLocalTranslation(-3.5f, -0.2f, 0);

    	this.backGutter.getLocalScale().set( 9, 0.4f, 5f );
    	this.backGutter.setLocalTranslation(0f, -0.25f, 37.5f);

    	this.leftWall.getLocalScale().set(2f, 2f, 75f );
    	this.leftWall.setLocalTranslation(5.5f, 0.5f, 2.5f);

    	this.rightWall.getLocalScale().set(2f, 2f, 75f );
    	this.rightWall.setLocalTranslation(-5.5f, 0.5f, 2.5f);

    	this.backWall.getLocalScale().set(13f, 4f, 2f );
    	this.backWall.setLocalTranslation(0f, 1.5f, 41f);
	}

	private void setupPins(){
    	float x[] = {0, -0.5f, 0.5f, -1f, 0, 1f, -1.5f, -0.5f, 0.5f, 1.5f};
    	float z[] = {0, 1f, 1f, 2f, 2f, 2f, 3f, 3f, 3f, 3f};

    	//TODO: tener en cuenta cuando caen los pinos, no se crean/colocan todos
    	for ( int i = 0; i < PIN_COUNT; i++ ) {
    		
    		DynamicPhysicsNode pin = this.pins.get(i);
    		
    		pin.setLocalTranslation(x[i] * PIN_DISTANCE, 1.32f, z[i] * PIN_DISTANCE + PIN_OFFSET);
    		Quaternion q = new Quaternion();
    		q.fromAngles((float) -Math.PI/2, 0, 0);
    		pin.setLocalRotation(q);
    	}
    }

	private void setupBowlingBall() {
		this.ball.setLocalTranslation(0f, 1f, -32f);
	}

    private void color( Spatial spatial, ColorRGBA color ) {
        final MaterialState materialState = display.getRenderer().createMaterialState();
        materialState.setDiffuse( color );
        if ( color.a < 1 ) {
            final BlendState blendState = display.getRenderer().createBlendState();
            blendState.setEnabled( true );
            blendState.setBlendEnabled( true );
            blendState.setSourceFunction( BlendState.SourceFunction.SourceAlpha );
            blendState.setDestinationFunction( BlendState.DestinationFunction.OneMinusSourceAlpha );
            spatial.setRenderState( blendState );
            spatial.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
        }
        spatial.setRenderState( materialState );
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
