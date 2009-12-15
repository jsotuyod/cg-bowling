package bowling.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import bowling.utils.Parser.ParserException;
import bowling.utils.sc.DiffuseShader;
import bowling.utils.sc.Shader;
import cg.math.Matrix4;
import cg.utils.Color;

import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.geom.BufferUtils;
import com.jmex.model.converters.FormatConverter;

public class ScToJme extends FormatConverter {

	private boolean debug = true;
	private Map<String, Shader> shadersMap;
	
	@Override
	public void convert(InputStream format, OutputStream format2)
			throws IOException {
		
		shadersMap = new HashMap<String, Shader>();
		Parser parser = new Parser(format);

		BinaryExporter.getInstance().save(parseFile(parser),format2);
		
		parser.close();
	}

	private Spatial parseFile(Parser p) throws IOException {
		Node node = new Node("sc file");
		
		while (true) {
			String token = p.getNextToken();
			
			if (token == null) {
				break;
			}
			
			try {
				if (token.equals("shader")) {
                    parseShader(p);
                } else if (token.equals("object")) {
					node.attachChild(parseObjectBlock(p));
				}
				
				// TODO : Support other stuff
				
				
			} catch (ParserException e) {
				e.printStackTrace();
				return null;
	        } catch (UnsupportedShaderException e) {
				e.printStackTrace();
				return null;
	        }
		}
		
		return node;
	}

	private void parseShader(Parser p) throws IOException, ParserException, UnsupportedShaderException {
		
		p.checkNextToken("{");
        p.checkNextToken("name");
        String name = p.getNextToken();
        Shader shader;
        
        if ( debug ) {
        	System.out.println("Reading shader: " + name + " ...");
        }
        
        p.checkNextToken("type");
        String type = p.getNextToken();
        
        if (type.equals("diffuse")) {
        	if (p.peekNextToken("diff")) {
	            shader = new DiffuseShader(name, parseColor(p));
	            shadersMap.put(name, shader);
        	} else if (p.peekNextToken("texture")) {
            	String textureFilePath = p.getNextToken();
            	
            	Texture tex = TextureManager.loadTexture(textureFilePath,
                        Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
            	
            	shader = new DiffuseShader(name, tex);
	            shadersMap.put(name, shader);
        	}
        } else {
        	throw new UnsupportedShaderException("Unsupported shader type found: " + type);
        }
	}
	
	private Spatial parseObjectBlock(Parser p) throws IOException, ParserException {
		p.checkNextToken("{");
        Matrix4 transform = null;
        String name = null;
        Spatial ret = null;
        
        if (p.peekNextToken("transform")) {
            transform = parseMatrix(p);
        }
        
        // Discard shader
        p.checkNextToken("shader");
        String shader = p.getNextToken();
        
        p.checkNextToken("type");
        String type = p.getNextToken();
        
        if (p.peekNextToken("name")) {
            name = p.getNextToken();
        }
        
        if (type.equals("generic-mesh")) {
        	ret = parseGenericMesh(name, p, transform);
        } else if (type.equals("box")){
        	ret = parseBox(name, p, transform);
        } else {
        	ret = null;
        }
        
        if (ret != null) {
        	shadersMap.get(shader).apply(ret);
        }
        
        return ret;
	}
	
	
	
	private Spatial parseBox(String name, Parser p, Matrix4 transform) {
		if(debug) {
    		System.out.println("Reading box... ");
    	}
		
		Vector3f center = transform.transform(new Vector3f(0, 0, 0));
		Vector3f x = transform.transform(new Vector3f(0.5f, 0, 0)).subtractLocal(center);
		Vector3f y = transform.transform(new Vector3f(0, 0.5f, 0)).subtractLocal(center);
		Vector3f z = transform.transform(new Vector3f(0, 0, 0.5f)).subtractLocal(center);
		
		Box box = new Box(name, center, x.length(), y.length(), z.length());
		
		x.normalizeLocal();
		y.normalizeLocal();
		z.normalizeLocal();
		
		// Apply rotation in each axis
		Quaternion q = new Quaternion();
		q.fromAngles(x.angleBetween(new Vector3f(1, 0, 0)), y.angleBetween(new Vector3f(0, 1, 0)), z.angleBetween(new Vector3f(0, 0, 1)));
		box.setLocalRotation(q);
		
		return box;
	}

	private Spatial parseGenericMesh(String name, Parser p, Matrix4 transform) throws ParserException, IOException {
    	if(debug) {
    		System.out.println("Reading generic mesh... ");
    	}
        // parse vertices
        p.checkNextToken("points");
        int np = p.getNextInt();
        Vector3f[] points = parseVector3fArray(p, np);
        float[] uvVertex = null, normalsVertex = null;
        
        // transform all points
        if ( transform != null ) {
        	for ( int i = 0; i < np; i++ ) {
        		points[i] = transform.transform(points[i]);
        	}
        }
        
        // parse triangle indices
        p.checkNextToken("triangles");
        int nt = p.getNextInt();
        int[] indices = parseIntArray(p, nt * 3);
        
        // parse normals
        p.checkNextToken("normals");
        if (p.peekNextToken("vertex")){
        	normalsVertex = parseFloatArray(p, np * 3);
        } else if (p.peekNextToken("facevarying")){
        	// TODO : Facevarying is unsupported
        } else {
            p.checkNextToken("none");
            
            normalsVertex = new float[np * 3];
            
            // Area weighted mean of face normals
            for (int i = 0; i < np; i++) {
            	int j;
            	Vector3f n = null;
            	Vector3f v1 = null, v2 = null;
            	for (j = 0; j < nt * 3; j += 3) {
            		if (i == indices[j] || i == indices[j+1] || i == indices[j+2]) {
            			v1 = new Vector3f(points[indices[j]]).subtractLocal(points[indices[j+1]]);
            			v2 = new Vector3f(points[indices[j]]).subtractLocal(points[indices[j+2]]);
            			
            			n = v1.cross(v2);
            		}
            	}
            	
            	n.normalizeLocal();
            	
            	normalsVertex[i*3] = n.x;
            	normalsVertex[i*3+1] = n.y;
            	normalsVertex[i*3+2] = n.z;
            }
        }
        
        // parse texture coordinates
        p.checkNextToken("uvs");
        if (p.peekNextToken("vertex")){
        	uvVertex = parseFloatArray(p, np * 2);
        }
        else if (p.peekNextToken("facevarying")){
        	// TODO : Facevarying is unsupported
        }
        else {
            p.checkNextToken("none");
            
            // FIXME : Todas en cero - PARCHE!!
            uvVertex = new float[np * 2];
        }
        
        if (p.peekNextToken("face_shaders")) {
        	parseIntArray(p, nt);
        }

        return new TriMesh(name, BufferUtils.createFloatBuffer(points), FloatBuffer.wrap(normalsVertex), null, new TexCoords(BufferUtils.createFloatBuffer(uvVertex), 2), IntBuffer.wrap(indices) );
	}

	private Vector3f[] parseVector3fArray(Parser p, int size) throws IOException {
		Vector3f[] data = new Vector3f[size];
        for (int i = 0; i < size; i++)
            data[i] = parseVector3d(p);
        return data;
	}

	private Vector3f parseVector3d(Parser p) throws IOException {
		float x = p.getNextFloat();
        float y = p.getNextFloat();
        float z = p.getNextFloat();
        return new Vector3f(x, y, z);
	}

	private Matrix4 parseMatrix(Parser p) throws IOException, ParserException {
        if (p.peekNextToken("row")) {
            return new Matrix4(parseFloatArray(p, 16), true);
        } else if (p.peekNextToken("col")) {
            return new Matrix4(parseFloatArray(p, 16), false);
        } else {
    		Matrix4 m = Matrix4.identity();
            p.checkNextToken("{");
            while (!p.peekNextToken("}")) {
                if (p.peekNextToken("translate")) {
                    float x = p.getNextFloat();
                    float y = p.getNextFloat();
                    float z = p.getNextFloat();
                    m = m.translate(x, y, z);
                } else if (p.peekNextToken("scaleu")) {
                    float s = p.getNextFloat();
                    m = m.scale(s);
                } else if (p.peekNextToken("scalex")) {
                    float x = p.getNextFloat();
                    m = m.scale(x, 1, 1);
                } else if (p.peekNextToken("scaley")) {
                    float y = p.getNextFloat();
                    m = m.scale(1, y, 1);
                } else if (p.peekNextToken("scalez")) {
                    float z = p.getNextFloat();
                    m = m.scale(1, 1, z);
                } else if (p.peekNextToken("rotatex")) {
                    float angle = p.getNextFloat();
                    m = m.rotateX((float) Math.toRadians(angle));
                } else if (p.peekNextToken("rotatey")) {
                    float angle = p.getNextFloat();
                    m = m.rotateY((float) Math.toRadians(angle));
                } else if (p.peekNextToken("rotatez")) {
                    float angle = p.getNextFloat();
                    m = m.rotateZ((float) Math.toRadians(angle));
                } else if (p.peekNextToken("rotate")) {
                    float x = p.getNextFloat();
                    float y = p.getNextFloat();
                    float z = p.getNextFloat();
                    float angle = p.getNextFloat();
                    m = m.rotate(x, y, z, (float) Math.toRadians(angle));
                } else
                    System.err.println( "Unrecognized transformation type: " + p.getNextToken());
            }
            return m;
        }
    }
	
	private float[] parseFloatArray(Parser p, int size) throws IOException {
        float[] data = new float[size];
        for (int i = 0; i < size; i++)
            data[i] = p.getNextFloat();
        return data;
    }
    
    private int[] parseIntArray(Parser p, int size) throws IOException {
        int[] data = new int[size];
        for (int i = 0; i < size; i++)
            data[i] = p.getNextInt();
        return data;
    }
    
    private Color parseColor(Parser p) throws IOException, ParserException {
    	float r,g,b;
    	String space;
    	
        if (p.peekNextToken("{")) {
            space = p.getNextToken();
            Color c = null;
            if (space.equals("sRGB nonlinear")) {
                r = p.getNextFloat();
                g = p.getNextFloat();
                b = p.getNextFloat();
                c = new Color(r, g, b);
                
                c.toLinear();
            } else if (space.equals("sRGB linear")) {
                r = p.getNextFloat();
                g = p.getNextFloat();
                b = p.getNextFloat();
                c = new Color(r, g, b);
            } else
            	System.err.println("Unrecognized color space: " + space);

            p.checkNextToken("}");
            return c;
        } else {
            r = p.getNextFloat();
            g = p.getNextFloat();
            b = p.getNextFloat();
            return new Color(r, g, b);
        }
    }
    
    /**
     * Exception thrown when an unsupported shader is defined.
     * @author jsotuyod
     *
     */
    class UnsupportedShaderException extends Exception {
    	
    	private static final long serialVersionUID = 1L;
    	
		public UnsupportedShaderException(String string) {
			super(string);
		}
    	
    }
}
