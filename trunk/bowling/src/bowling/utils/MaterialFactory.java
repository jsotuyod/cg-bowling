package bowling.utils;

import com.jmex.physics.contact.MutableContactInfo;
import com.jmex.physics.material.Material;

/**
 * Material factory. Auxiliar class to create materials.
 */
public class MaterialFactory {

	/**
	 * Creates a new MAterialFactory instance. Should never be called.
	 */
	private MaterialFactory() {
		
	}
	
	/**
	 * Creates a new material.
	 * @param name The name of the material being created.
	 * @param density The density of the material being created.
	 * @param mu The mu coefficient of the material being created.
	 * @param bounce The bounce coefficient of the material being created.
	 * @return The created material.
	 */
	public static Material createMaterial(String name, float density, float mu, float bounce) {
    	Material m = new Material(name);
    	m.setDensity(density);
    	MutableContactInfo contactDetails = new MutableContactInfo();
    	contactDetails.setBounce(bounce);
    	contactDetails.setMu(mu);
    	m.putContactHandlingDetails(Material.DEFAULT, contactDetails);

    	return m;
    }
}
