
shader {
	name "WorldWall.shader"
	type diffuse
	diff { "sRGB nonlinear" 1.000 0.000 0.000 }
}

object {
	transform {
		scalex 120
		scaley 20
		scalez 0.1
		translate 0 9.5 39
	}
	shader "WorldWall.shader"
	type box
	name "worldwall"
}
