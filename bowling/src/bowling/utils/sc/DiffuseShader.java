package bowling.utils.sc;

import cg.utils.Color;

import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;


public class DiffuseShader implements Shader {

	private String name;
	private Color color;
	private Texture texture;
	
	public DiffuseShader(String name, Color color) {
		super();
		this.name = name;
		this.color = color;
		this.texture = null;
	}
	
	public DiffuseShader(String name, Texture texture) {
		super();
		this.name = name;
		this.color = null;
		this.texture = texture;
	}

	public String getName() {
		return name;
	}

	public Color getColor() {
		return color;
	}
	
	/**
     * Sets the color of a spatial.
     * @param spatial
     * @param color
     */
	@Override
	public void apply(Spatial spatial) {
		if (color != null) {
			int rgb = color.toRGB();
			float a = 1;
			float r = ((rgb & 0xff0000) >> 16) / 255.0f;
			float g = ((rgb & 0xff00) >> 8) / 255.0f;
			float b = (rgb & 0xff) / 255.0f;
			
			this.setDiffuse(spatial, new ColorRGBA(r, g, b, a));
		} else {
			// texture!
			this.setTexture(spatial, this.texture);
		}
	}
	
    private void setTexture(Spatial spatial, Texture texture2) {
    	final TextureState textureState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
    	textureState.setTexture(texture);
    	spatial.setRenderState(textureState);
	}

	private void setDiffuse(Spatial spatial, ColorRGBA color) {
    	final MaterialState materialState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
        materialState.setDiffuse( color );
        if ( color.a < 1 ) {
            final BlendState blendState = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
            blendState.setEnabled( true );
            blendState.setBlendEnabled( true );
            blendState.setSourceFunction( BlendState.SourceFunction.SourceAlpha );
            blendState.setDestinationFunction( BlendState.DestinationFunction.OneMinusSourceAlpha );
            spatial.setRenderState( blendState );
            spatial.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
        }
        spatial.setRenderState( materialState );
    }

}
