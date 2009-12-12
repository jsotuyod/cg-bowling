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
	
	private static final int DISTANCE_FOR_ANGLE_COMPUTATION = 2;
	
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
		this.image = new Quad("anglemeter container texture", 128, 54);
		AssetManager.getInstance().loadAngleMeterContainer(this.image);
		this.containerNode.attachChild(this.image);
		
		this.containerNode.setLocalTranslation(DisplaySystem.getDisplaySystem().getWidth() - 200, 230, 0);
		
		this.lastCol = 127;
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
		
		TextureState ts = (TextureState) this.image.getRenderState(StateType.Texture);
		Texture texture = ts.getTexture();
		Image img = texture.getImage();
		ByteBuffer data = img.getData().get(0);
		
		// We will compute angle from the image itself (sneaky)
		int startCol = Math.min(lastCol + DISTANCE_FOR_ANGLE_COMPUTATION, img.getWidth() - 1);
		int endCol = Math.max(lastCol - DISTANCE_FOR_ANGLE_COMPUTATION, 0);
		
		int startValue = img.getHeight() - 1;
		int endValue = img.getHeight() - 1;
		
		// Go over the y axis to find the first coloured point
		for (int j = startValue; j > 0; j--) {
			if ((data.getInt(4 * (j * img.getWidth() + startCol)) & 0xFF000000) != 0x00000000) {
				startValue = j;
				break;
			}
		}
		
		for (int j = endValue; j > 0; j--) {
			if ((data.getInt(4 * (j * img.getWidth() + endCol)) & 0xFF000000) != 0x00000000) {
				endValue = j;
				break;
			}
		}
		
		// Compute angle
		return (float) Math.atan((startValue - endValue) / (float) (startCol - endCol + 1));
	}
}
