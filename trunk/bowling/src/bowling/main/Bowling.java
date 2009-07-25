package bowling.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bowling.utils.ScToJme;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingCapsule;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.geometry.PhysicsMesh;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.SimplePhysicsGame;

public class Bowling extends SimplePhysicsGame {

	final private static String RESOURCE_PATH = "resources" + File.separatorChar;
	final private static String MODELS_PATH = RESOURCE_PATH + "models" + File.separatorChar;
	
	final private static String PIN_SC_MODEL_PATH = MODELS_PATH + "pin.sc";
	final private static String PIN_JME_MODEL_PATH = MODELS_PATH + "pin.jme";
	final private static int PIN_COUNT = 10;
	
	private void convertModels() {
		
		ScToJme converter = new ScToJme();
		
		try {
			// TODO : Convert all sc files to jme
			InputStream in = new FileInputStream(new File(PIN_SC_MODEL_PATH));
			OutputStream out = new FileOutputStream(new File(PIN_JME_MODEL_PATH));
			converter.convert(in, out);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void loadModel(String path, String name, Node parent, boolean dynamic) {
		
		PhysicsNode newNode;
		Node model;
		
		// Create the node
		if (dynamic) {
			newNode = getPhysicsSpace().createDynamicNode();
		} else {
			newNode = getPhysicsSpace().createStaticNode();
		}
		
		newNode.setName(name);
		
		parent.attachChild(newNode);

        // load the model
		JMEImporter importer = new BinaryImporter();
        try {
			model = (Node) importer.load(new File(path));
			
			// TODO : El bounding box es demasiado tosco, buscar una solucion mejor
			model.setModelBound(new BoundingBox());
			model.updateModelBound();
			
			newNode.attachChild(model);
			newNode.generatePhysicsGeometry();
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

        // now we create a collision geometry for the floor - a box
        PhysicsBox floorBox = staticNode.createBox( "floor" );

        // the box is already attached to our static node
        // it currently has height, width and depth of 1
        // resize it to be 10x10 thin (0.5) floor
        floorBox.getLocalScale().set( 10, 0.5f, 10 );
        
        float scale = 1.2f;
        float x[] = {0, -0.5f, 0.5f, -1f, 0, 1f, -1.5f, -0.5f, 0.5f, 1,5f};
        float z[] = {0, 1f, 1f, 2f, 2f, 2f, 3f, 3f, 3f, 3f};
        
        for (int i =0; i < PIN_COUNT; i++) {
        	x[i] /= scale;
        	z[i] /= scale;
        }
        // Load the pines
        for ( int i = 0; i < PIN_COUNT; i++ ) {
        	
        	this.loadModel(PIN_JME_MODEL_PATH, "pin" + i, rootNode, true);
        	DynamicPhysicsNode pin = (DynamicPhysicsNode) rootNode.getChild("pin" + i);
        	if (i != 0) {
        		pin.setCenterOfMass(new Vector3f(0f, 0f, -1.3f));
        	}
        	else {
        		pin.setCenterOfMass(new Vector3f(0f, -50f, -1.3f));
        	}
        	//pin.clearDynamics();
        	pin.setMaterial(Material.PLASTIC);
        	pin.setLocalScale(0.25f);
        	pin.setLocalTranslation(x[i], 1.1f, z[i]);
        	Quaternion q = new Quaternion();
        	q.fromAngles((float) -Math.PI/2, 0, 0);
        	pin.setLocalRotation( q );

        }
        
//        this.loadModel(PIN_JME_MODEL_PATH, "pin0", rootNode, true);
//        rootNode.getChild("pin0").getLocalTranslation().set(0, 3, 0);
        
        
        // TODO : Set up scene for game
        
        
        // note: we do not move the collision geometry but the physics node!

        // ok we have created some physics stuff but no actual meshes that could be seen
        // thus we activate the debug mode to allow us to see anything (can be toggled in game with key V)
        showPhysics = true;
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
