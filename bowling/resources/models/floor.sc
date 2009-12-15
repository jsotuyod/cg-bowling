
shader {
	name "WorldFloor.shader"
	type diffuse
	texture "resources/textures/floor.jpg"
}

object {
	transform {
		scalex 120
		scaley 0.1
		scalez 74
		translate 0 -0.5 2
	}
	shader "WorldFloor.shader"
	type box
	name "worldfloor"
}
