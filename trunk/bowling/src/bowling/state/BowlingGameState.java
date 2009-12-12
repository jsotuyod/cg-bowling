package bowling.state;

import java.util.LinkedList;
import java.util.List;

import bowling.asset.AssetManager;
import bowling.input.GameInputHandler;
import bowling.logic.domain.AngleMeter;
import bowling.logic.domain.Ball;
import bowling.logic.domain.DirectionMeter;
import bowling.logic.domain.Pin;
import bowling.logic.domain.PowerMeter;
import bowling.logic.domain.ThrowPhase;
import bowling.utils.MaterialFactory;

import com.jme.input.InputHandler;
import com.jme.light.PointLight;
import com.jme.math.Matrix4f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.states.PhysicsGameState;

/**
 * The bowling's game state,
 */
public class BowlingGameState extends PhysicsGameState {

	final private static int PIN_COUNT = 10;
	
	final private static float PIN_DISTANCE = 1.3f;
	final private static float PIN_OFFSET = 30f;
	
	
	private static BowlingGameState state;
	
	
	private GameInputHandler inputHandler;
	
	private Ball ball;
	private List<Pin> pins;
	
	private PowerMeter powermeter;
	private DirectionMeter directionmeter;
	private AngleMeter anglemeter;
	
	private ThrowPhase currentPhase;
	
	/**
	 * Creates a new bowling game state.
	 */
	private BowlingGameState() {
		super("bowling game");
		
		// Position camera
		this.setupCamera();
		
        // Create Needed objects
		this.createLane();
		this.createPins();
		this.createBowlingBall();
		
		this.resetScene();
		
		this.setLights();
		
		this.setPowerMeter();
		this.setDirectionMeter();
		this.setAngleMeter();
		
		this.currentPhase = ThrowPhase.SET_POWER;
		
		// Make sure everything renders properly
		rootNode.updateGeometricState(0, true);
		rootNode.updateRenderState();
	}

	/**
	 * Retrieves the bowling game state.
	 * @return The bowling game state.
	 */
	public static BowlingGameState getState() {
		
		if (state == null) {
			state = new BowlingGameState();
		}
		
		return state;
	}
	
	/**
	 * Setups the camera for the bowling game state.
	 */
	public void setupCamera() {
		Camera cam = DisplaySystem.getDisplaySystem().getRenderer().getCamera();
		
    	cam.setLocation(new Vector3f (10f, 20f, -70f));
        cam.lookAt(new Vector3f(0f, 1.5f, 41f), new Vector3f (0, 1, 0));
	}
	
	/**
	 * Resets the scene's for a ball throw.
	 */
	public void resetScene() {
    	resetPins();
    	resetBowlingBall();
	}

	/**
	 * Sets the pins in their correct positions for a new throw.
	 */
	public void resetPins(){

    	for ( int i = 0; i < PIN_COUNT; i++ ) {
    		
    		Pin pin = this.pins.get(i);
    		pin.place();
    	}
    }

	/**
	 * Sets the ball in it's correct position for a new throw.
	 */
	private void resetBowlingBall() {
		this.ball.reset();
	}
	
	/**
	 * Creates the bowling ball model.
	 */
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
        ball.setMaterial(MaterialFactory.createMaterial("ball", 20f, 0.05f, 0f));
        ball.computeMass();
        
        // Add the model, with no physics, to make it look nice
        AssetManager.getInstance().loadBall(ball, false);
        
        this.ball = new Ball(ball, new Vector3f(0f, 1f, -32f));
	}
	
	/**
	 * Creates the bowling lane.
	 */
	private void createLane() {
    	StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();
        rootNode.attachChild( staticNode );

        AssetManager.getInstance().loadLane(staticNode, true);
	}

	/**
	 * Creates the pins.
	 */
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
        	
        	AssetManager.getInstance().loadPin(pin, true);
        	
        	pin.setLocalScale(0.25f);
        	pin.setMaterial(Material.WOOD);
        	pin.computeMass();
        	
        	Vector3f originalPos = new Vector3f(x[i] * PIN_DISTANCE, 1.32f, z[i] * PIN_DISTANCE + PIN_OFFSET);
        	this.pins.add(new Pin(pin, originalPos));
        }
	}
	
	/**
     * Setups the input handling.
     */
	public void setInputHandler(InputHandler input) {
		
		// If a previous one existed, clear it
		if (this.inputHandler != null) {
			this.inputHandler.clear();
		}
		
		this.inputHandler = new GameInputHandler(input);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jmex.game.state.GameState#setActive(boolean)
	 */
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		
		if (active) {
			// Set game input handler
			if (this.inputHandler != null) {
				this.inputHandler.setUp(this);
			}
		} else {
			// Clear the input handler
			if (this.inputHandler != null) {
				this.inputHandler.clear();
			}
		}
	}
	
	/**
	 * Initializes game scene's lights.
	 */
	private void setLights() {
		// Set up a basic, default light.
		PointLight light = new PointLight();
		light.setDiffuse(new ColorRGBA( 0.75f, 0.75f, 0.75f, 0.75f ));
		light.setAmbient(new ColorRGBA( 0.5f, 0.5f, 0.5f, 1.0f ));
		light.setLocation(new Vector3f(100, 100, 100));
		light.setEnabled(true);

		// Attach the light to a lightState and the lightState to rootNode.
		LightState lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
		lightState.setEnabled(true);
		lightState.attach(light);
		rootNode.setRenderState(lightState);
	}
	
	/**
	 * Sets the power meter.
	 */
	private void setPowerMeter() {
		powermeter = new PowerMeter();
		powermeter.setVisible(true);
		powermeter.setPaused(false);
	}
	
	/**
	 * Sets the direction meter.
	 */
	private void setDirectionMeter() {
		directionmeter = new DirectionMeter();
	}
	
	/**
	 * Sets the angle meter.
	 */
	private void setAngleMeter() {
		anglemeter = new AngleMeter();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jmex.physics.util.states.PhysicsGameState#update(float)
	 */
	@Override
	public void update(float tpf) {
		super.update(tpf);
		
		// Update the game displays
		powermeter.update(tpf);
		directionmeter.update(tpf);
		anglemeter.update(tpf);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jmex.game.state.BasicGameState#render(float)
	 */
	@Override
	public void render(float tpf) {
		super.render(tpf);
		
		// Render the game displays
		powermeter.render(tpf);
		directionmeter.render(tpf);
		anglemeter.render(tpf);
	}

	/**
	 * Notify the game state that a bar has been stoped.
	 */
	public void barStoped() {
		switch (currentPhase) {
		case SET_POWER:
			powermeter.setPaused(true);
			currentPhase = ThrowPhase.SET_H_ANGLE;
			directionmeter.setVisible(true);
			directionmeter.setPaused(false);
			break;
		
		case SET_H_ANGLE:
			directionmeter.setPaused(true);
			currentPhase = ThrowPhase.SET_V_ANGLE;
			anglemeter.setVisible(true);
			anglemeter.setPaused(false);
			break;
			
		case SET_V_ANGLE:
			anglemeter.setPaused(true);
			
			Vector3f force = directionmeter.getDirection();
			float yAngle = anglemeter.getAngle();
			
			System.out.println("ANGULO : " + yAngle);
			System.out.println("DIR : " + force);
			
			// Build rotation matrix accross a vector perpendicular to the direction on the same plane
			Matrix4f transform = new Matrix4f();
			transform.fromAngleNormalAxis(yAngle, new Vector3f(force.z, 0, -force.x));
			
			force = transform.mult(force);
			System.out.println("DIR POSTA : " + force);
			force.multLocal(powermeter.getPower());
			System.out.println("FUERZA : " + force);
			
			this.ball.getNode().addForce(force);
			break;

		default:
			break;
		}
	}
}
