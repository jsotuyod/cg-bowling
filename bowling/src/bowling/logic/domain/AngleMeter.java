package bowling.logic.domain;

import java.nio.ByteBuffer;

import bowling.asset.AssetManager;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.RenderState.StateType;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureKey;
import com.jme.util.TextureManager;

/**
 * The angle meter.
 */
public class AngleMeter extends GameDisplay {

	final private static float CYCLE_TIME = 2;
	final private static float HALF_CYCLE_TIME = CYCLE_TIME / 2;
	
	protected Quad image;
	
	protected int lastCol;
	
	/**
	 * Creates a new direction meter instance.
	 */
	public AngleMeter() {
		super("anglemeter container");
	}
	
	/*
	 * (non-Javadoc)
	 * @see bowling.logic.domain.GameDisplay#init()
	 */
	@Override
	protected void init() {
		super.init();
		
		// Load the direction meter container
		this.image = new Quad("anglemeter container texture", 135, 54);
		AssetManager.getInstance().loadAngleMeterContainer(this.image);
		this.containerNode.attachChild(this.image);
		
		this.containerNode.setLocalTranslation(DisplaySystem.getDisplaySystem().getWidth() - 200, 230, 0);
		
		this.lastCol = 134;
	}
	
	/*
	 * (non-Javadoc)
	 * @see bowling.logic.domain.GameDisplay#simpleUpdate()
	 */
	@Override
	public void simpleUpdate() {
		
		currentTick = currentTick % CYCLE_TIME;
		
		// Change color to yellow
		TextureState ts = (TextureState) this.image.getRenderState(StateType.Texture);
		Texture texture = ts.getTexture();
		Image img = texture.getImage();
		ByteBuffer data = img.getData().get(0);
		
		// Compute completion percentage
		int startCol;
		
		if (currentTick < HALF_CYCLE_TIME) {
			startCol = Math.round((img.getWidth() - 1) * (1f - (currentTick / HALF_CYCLE_TIME)));
		} else {
			startCol = Math.round((img.getWidth() - 1) * ((currentTick - HALF_CYCLE_TIME) / HALF_CYCLE_TIME));
		}
		
		// Change colors for needed columns only (for performance)
		int start = startCol < lastCol ? startCol : lastCol;
		int end = startCol < lastCol ? lastCol : startCol;
		
		int color;
		int pos;
		for (int i = start; i <= end; i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				pos = 4 * (j * img.getWidth() + i);
				color = data.getInt(pos);
				if ((color & 0xFF000000) == 0xFF000000) {
					data.putInt(pos, i > startCol ? 0xFF00FFFF : 0xFF0000FF);
				}
			}
		}
		
		lastCol = startCol;
		
		// Change the texture... this was painful to figure out...
		Texture newTexture = new Texture2D();
		newTexture.setMinificationFilter(Texture.MinificationFilter.Trilinear);
		newTexture.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
		newTexture.setImage(img);
		newTexture.setTextureKey(new TextureKey(null, false, img.getFormat()));
		
		ts.removeTexture(texture);
		ts.setTexture(newTexture);
		
		TextureManager.releaseTexture(texture);
	}
	
	/**
	 * Retrieves the current angle.
	 * @return The current angle.
	 */
	public float getAngle() {
		
		// TODO : Fill this in!!
		return 0;
	}
}
