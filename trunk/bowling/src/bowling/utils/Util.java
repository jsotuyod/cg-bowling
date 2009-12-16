package bowling.utils;

import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.Text;

public final class Util {

	public static Text createText (String text, float x, float y) {
		Text t = Text.createDefaultTextLabel("Text", text);
		t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		t.setLightCombineMode(Spatial.LightCombineMode.Off);
		t.setLocalTranslation(new Vector3f(x, y, 0));
		return t;
	}
}
