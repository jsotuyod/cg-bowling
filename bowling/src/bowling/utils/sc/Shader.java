package bowling.utils.sc;

import com.jme.scene.Spatial;

public interface Shader {

	/**
	 * Applies a shader to the given spatial.
	 * @return
	 */
	void apply(Spatial spatial);
}
