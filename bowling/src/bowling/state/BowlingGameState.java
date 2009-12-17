package bowling.state;

import java.util.LinkedList;
import java.util.List;

import bowling.asset.AssetManager;
import bowling.audio.AudioManager;
import bowling.input.GameInputHandler;
import bowling.logic.domain.AngleMeter;
import bowling.logic.domain.Ball;
import bowling.logic.domain.DirectionMeter;
import bowling.logic.domain.Pin;
import bowling.logic.domain.PowerMeter;
import bowling.logic.domain.ThrowPhase;
import bowling.logic.score.Board;
import bowling.main.Bowling;
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
import com.jmex.game.state.GameState;
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

	private static final int LANE_LIMIT = 8;

	private static final float MAX_TIME_AFTER_BALL_STOPPED = 5;
	
	
	private static BowlingGameState state;
	
	
	private GameInputHandler inputHandler;
	
	private Board scoreBoard;
	
	private Ball ball;
	private List<Pin> pins;
	
	private PowerMeter powermeter;
	private DirectionMeter directionmeter;
	private AngleMeter anglemeter;
	
	private ThrowPhase currentPhase;
	
	private boolean firstFrame;
	
	private String userName;
	
	private float timeSinceBallStopped;
	private boolean ballStopped;
	
	/**
	 * Creates a new bowling game state.
	 */
	private BowlingGameState() {
		super("bowling game");
		
		this.userName = "Player 1";
		
		// Position camera
		this.setupCamera();
		
        // Create Needed objects
		this.createLane();
		this.createPins();
		this.createBowlingBall();
		this.createFloor();
		this.createWall();
		this.createScoreBoard();
		
		this.setLights();
		
		this.setPowerMeter();
		this.setDirectionMeter();
		this.setAngleMeter();
		
		this.reset(this.userName);
		
		// Make sure everything renders properly
		rootNode.updateGeometricState(0, true);
		rootNode.updateRenderState();
	}

	/**
	 * Creates the score board.
	 */
	private void createScoreBoard() {
		this.scoreBoard = new Board(this.userName);
	}
	
	/**
	 * Set's the user name. Reset's the game.
	 * @param value The new user name.
	 */
	public void setUserName(String value) {
		this.userName = value;
		
		this.reset(this.userName);
	}

	/**
	 * Resets the game state.
	 */
	public void reset(String userName) {
		this.resetScene();
		
		this.scoreBoard.reset(userName);
		
		this.currentPhase = ThrowPhase.SET_POWER;
		
		this.firstFrame = false;
		
		this.ballStopped = false;
		this.timeSinceBallStopped = 0;
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
		
    	cam.setLocation(new Vector3f(0f, 6.5f, -40f));
        cam.lookAt(new Vector3f(0f, 0f, -20f), new Vector3f (0, 1, 0));
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
	private void resetPins(){

    	for ( int i = 0; i < PIN_COUNT; i++ ) {
    		
    		Pin pin = this.pins.get(i);
    		pin.reset();
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
        final Sphere sphere = new Sphere("ball-geom", 100, 100, 2.45f);
        ball.attachChild(sphere);
        ball.setLocalScale(0.204f);
        
        ball.generatePhysicsGeometry();
        ball.setMaterial(MaterialFactory.createMaterial("ball", 20f, 0.05f, 0f));
        ball.computeMass();
        
        // Add the model, with no physics, to make it look nice
        AssetManager.getInstance().loadBall(ball, false);
        
        this.ball = new Ball(ball, new Vector3f(0f, 1f, -32f));
	}
	
	/**
	 * Creates the floor
	 */
	private void createFloor() {
		StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();
		rootNode.attachChild(staticNode);
		
		AssetManager.getInstance().loadFloor(staticNode, true);
	}
	
	/**
	 * Creates the wall.
	 */
	private void createWall() {
		StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();
		rootNode.attachChild(staticNode);
		
		AssetManager.getInstance().loadWall(staticNode, true);
	}
	
	/**
	 * Creates the bowling lane.
	 */
	private void createLane() {
    	StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();
        rootNode.attachChild(staticNode);

        AssetManager.getInstance().loadLane(staticNode, true);
	}

	/**
	 * Creates the pins.
	 */
	private void createPins() {
    	
    	if (null != this.pins) {
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
        	pin.setMass(5.0f);
        	
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
		light.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));
		light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
		light.setLocation(new Vector3f(0, 100, -70));
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
		
		// Check if the ball has exited
		Vector3f ballPos = this.ball.getNode().getLocalTranslation();
		
		if (ballPos.x < -LANE_LIMIT || ballPos.x > LANE_LIMIT) {
			this.throwFinished(0);
		}
		
		//Sound effects
		if(Bowling.getGameAudioManager().isEnableBowlingSound()){
			soundEffects();
		}
		
		if (!this.firstFrame && currentPhase == ThrowPhase.IN_PROGRESS) {
			boolean stopped = true;
			
			// Check if the throw is over
			if (ball.hasStopped()) {
				ballStopped = true;
				
				for (Pin pin : this.pins) {
					if (!pin.hasStopped()) {
						stopped = false;
					}
				}
			} else {
				stopped = false;
			}
			
			if (ballStopped) {
				timeSinceBallStopped += tpf;
				
				// Set a time limit for stopping while waiting for the pins
				if (timeSinceBallStopped >= MAX_TIME_AFTER_BALL_STOPPED) {
					stopped = true;
				}
			}
			
			if (stopped) {
				int pinsDown = pinsDown();
				
				this.throwFinished(pinsDown);
				
				ballStopped = false;
				timeSinceBallStopped = 0;
			}
		}
		
		this.firstFrame = false;
		
		scoreBoard.update(tpf);
	}

	private int pinsDown() {
		int pinsDown = 0;
		
		for (Pin pin : this.pins) {
			if (pin.isOnScene() && pin.isFallen()) {
				pinsDown++;
			}
		}
		return pinsDown;
	}
	
	/**
	 * Performs clean up after a throw is finished.
	 * @param pinsDown The number of pins thrown in this ball throw.
	 */
	private void throwFinished(int pinsDown) {
		switch (this.scoreBoard.score(pinsDown)) {
		case DO_NOTHING:
			// Remove fallen pins, set everything ready for next throw
			for (Pin pin : this.pins) {
				pin.place();
			}
			break;
			
		case TURN_ENDED:
			// Set everything so the second player can come next (if it exists)
			
		case RESET_PINS_TURN_NOT_ENDED:
			for (Pin pin : this.pins) {
				pin.reset();
			}
			break;
			
		case GAME_ENDED:
			this.scoreBoard.refreshText();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			setUpEndGameMenu();
			break;
		}
		
		ball.reset();
		
		currentPhase = ThrowPhase.SET_POWER;
		
		directionmeter.setVisible(false);
		anglemeter.setVisible(false);
		powermeter.reset();
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
		scoreBoard.render(tpf);
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
			directionmeter.reset();
			break;
		
		case SET_H_ANGLE:
			directionmeter.setPaused(true);
			currentPhase = ThrowPhase.SET_V_ANGLE;
			anglemeter.setVisible(true);
			anglemeter.reset();
			break;
			
		case SET_V_ANGLE:
			anglemeter.setPaused(true);
			currentPhase = ThrowPhase.IN_PROGRESS;
			firstFrame = true;
			
			Vector3f force = directionmeter.getDirection();
			float yAngle = anglemeter.getAngle();
			
			// Build rotation matrix accross a vector perpendicular to the direction on the same plane
			Matrix4f transform = new Matrix4f();
			transform.fromAngleNormalAxis(yAngle, new Vector3f(force.z, 0, -force.x));
			
			force = transform.mult(force);
			force.multLocal(powermeter.getPower());
			
			this.ball.getNode().addForce(force);
			break;

		default:
			break;
		}
	}
	
	private void soundEffects(){
		Vector3f ballPos = this.ball.getNode().getLocalTranslation();
		
		AudioManager gameAudioManager = Bowling.getGameAudioManager();
		
		boolean hasStopped = this.ball.hasStopped();

		// ball sound
		if( (!hasStopped && 
				(ballPos.x > -5 && ballPos.x < 5 && ballPos.z < 25
						&& ballPos.y < 1)) ){
			gameAudioManager.playBallShotSound();
		}
		
		// pins sound
		if(!hasStopped 
				&& ballPos.z > 30 && ballPos.z < 31
				&& ballPos.x > -3 && ballPos.x < 3){
			gameAudioManager.playPinsSound(PIN_COUNT - pinsDown());
		}
		// gutter sound
		if(ballPos.z < 10 && 
				(ballPos.x < -3 || ballPos.x > 3)){
			gameAudioManager.playGutterSound();
		}
		
		// no sound
		if(hasStopped){
			gameAudioManager.stopAllSounds();
		}
		
	}
	
	/**
	 * Adds the finish menu game state.
	 */
    private void setUpEndGameMenu() {
    	this.reset(this.userName);
		this.setActive(false);
		
    	GameState menu = EndGameMenuState.getState();
		EndGameMenuState.setInputHandler(inputHandler);
		menu.setActive(true);
	}
}
