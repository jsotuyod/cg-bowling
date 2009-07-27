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
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.binary.BinaryImporter;
import com.jmetest.physics.Utils;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.contact.MutableContactInfo;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.SimplePhysicsGame;

public class Bowling extends SimplePhysicsGame {

	private InputHandler physicsStepInputHandler;
	private DynamicPhysicsNode ball;
	private List<DynamicPhysicsNode> pins;
	
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
		
        // first we will create the floor
        // as the floor can't move we create a _static_ physics node
        StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();

        // attach the node to the root node to have it updated each frame
        rootNode.attachChild( staticNode );

        
        //TODO: REFACTOR
        
        // now we create a collision geometry for the floor - a box
        PhysicsBox floorBox = staticNode.createBox( "floor");

        
        // the box is already attached to our static node
        // it currently has height, width and depth of 1
        // resize it to be 10x10 thin (0.5) floor
        floorBox.getLocalScale().set( 5, 1f, 70 );
        //floorBox.setMaterial(Material.ICE);

        PhysicsBox leftGutter = staticNode.createBox( "leftGutter" ); 
        leftGutter.getLocalScale().set( 2, 0.5f, 70 );
        leftGutter.setLocalTranslation(3.5f, -0.25f, 0);

        PhysicsBox rightGutter = staticNode.createBox( "rightGutter" ); 
        rightGutter.getLocalScale().set( 2, 0.5f, 70 );
        rightGutter.setLocalTranslation(-3.5f, -0.25f, 0);

        PhysicsBox backGutter = staticNode.createBox( "backGutter" ); 
        backGutter.getLocalScale().set( 9, 0.5f, 5f );
        backGutter.setLocalTranslation(0f, -0.25f, 37.5f);

        PhysicsBox leftWall = staticNode.createBox( "leftWall" ); 
        leftWall.getLocalScale().set(2f, 2f, 75f );
        leftWall.setLocalTranslation(5.5f, 0.5f, 2.5f);

        PhysicsBox rightWall = staticNode.createBox( "rightWall" ); 
        rightWall.getLocalScale().set(2f, 2f, 75f );
        rightWall.setLocalTranslation(-5.5f, 0.5f, 2.5f);

        PhysicsBox backWall = staticNode.createBox( "backWall" ); 
        backWall.getLocalScale().set(13f, 4f, 2f );
        backWall.setLocalTranslation(0f, 1.5f, 41f);
        //color (backWall, new ColorRGBA(1f, 1f, 1f, 1f));

        Utils.color(backWall, new ColorRGBA(1f, 1f, 1f, 1f), 1);
        
        
        // Create Needed objects
        createPins();
        createBowlingBall();
        
        // Position camera
        setupCamera();
        
        // Set up scene for game
        setupScene();
        
        /*physicsStepInputHandler = new InputHandler();
        getPhysicsSpace().addToUpdateCallbacks( new PhysicsUpdateCallback() {
            public void beforeStep( PhysicsSpace space, float time ) {
                physicsStepInputHandler.update( time );
            }
            public void afterStep( PhysicsSpace space, float time ) {

            }
        } );

        physicsStepInputHandler.addAction( new MyInputAction( new Vector3f( 0, 0, 700 ) ),
                InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_O, InputHandler.AXIS_NONE, true );
        // the action is defined below
        // we register it to be invoked every update of the input handler while the HOME key (POS1) is down
        //                              ( last parameter value is 'true' )
        // note: as the used input handler gets updated each physics step the force is framerate independent -
        //       we can't use the normal input handler here!

        // register an action for the other direction, too
        physicsStepInputHandler.addAction( new MyInputAction( new Vector3f( 0, 0, -700 ) ),
                InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_P, InputHandler.AXIS_NONE, true );
        
        // note: we do not move the collision geometry but the physics node!

        // ok we have created some physics stuff but no actual meshes that could be seen
        // thus we activate the debug mode to allow us to see anything (can be toggled in game with key V)
         * 
         * 
         */
        
        this.ball.addForce(new Vector3f( 0, 0, 15000 ));
        
        showPhysics = true;
    }
	
    private void createPins() {
    	
    	if (null != this.pins) {
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
//        	pin.setMaterial(createMaterial("pin", 3f, 0.5f, 0.05f));
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
        this.ball.attachChild(sphere);
        this.ball.setLocalScale(0.1f);
        
        this.ball.generatePhysicsGeometry();
        this.ball.setMaterial(createMaterial("ball", 20f, 0.05f, 0f));
        this.ball.computeMass();
        
        // Add the model, with no physics, to make it look nice
        this.loadModel(BALL_JME_MODEL_PATH, ball, false);
	}

	private void setupCamera() {
    	
    	cam.setLocation(new Vector3f (30f, 5f, 0f));
        cam.lookAt(new Vector3f(0f, 1.5f, -3f), new Vector3f (0, 1, 0));
        
        //cam.setLeft(new Vector3f (0f, 0f, 0f));
        //cam.setDirection(new Vector3f(-5f, 1.5f, -3f));
        //cam.
	}
    
    private void setupScene() {
    	float x[] = {0, -0.5f, 0.5f, -1f, 0, 1f, -1.5f, -0.5f, 0.5f, 1.5f};
        float z[] = {0, 1f, 1f, 2f, 2f, 2f, 3f, 3f, 3f, 3f};
        
        for ( int i = 0; i < PIN_COUNT; i++ ) {
        	
        	DynamicPhysicsNode pin = this.pins.get(i);
        	
	        pin.setLocalTranslation(x[i] * PIN_DISTANCE, 1.32f, z[i] * PIN_DISTANCE + PIN_OFFSET);
	    	Quaternion q = new Quaternion();
	    	q.fromAngles((float) -Math.PI/2, 0, 0);
	    	pin.setLocalRotation(q);
        }
        
        /*DynamicPhysicsNode ball = (DynamicPhysicsNode) rootNode.getChild("ball") ;
        ball.setMaterial(Material.PLASTIC);
        ball.computeMass();
        color (ball, new ColorRGBA( 1f, 1f, 1f, 0.6f ) );
        //ball.addForce(new Vector3f(50f, 50f, 0f));*/
        
        ball.setLocalTranslation(0f, 1f, -32f);
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
    
	/**
     * The main method to allow starting this class as application.
     *
     * @param args command line arguments
     */
    public static void main( String[] args ) {
        new Bowling().start();
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
}
