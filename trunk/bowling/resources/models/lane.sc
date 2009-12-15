
shader {
	name "LineFloor.shader"
	type diffuse
	texture "resources/textures/wood1.jpg"
}

shader {
	name "Gutter.shader"
	type diffuse
	diff { "sRGB nonlinear" 0.200 0.200 0.200 }
}

shader {
	name "Wall.shader"
	type diffuse
	diff { "sRGB nonlinear" 0.000 0.000 1.000 }
}

object {
	transform {
		scalex 5
		scaley 1
		scalez 70
	}
	shader "LineFloor.shader"
	type box
	name "floor"
}


object {
	transform {
		scalex 2
		scaley 0.6
		scalez 70
		translate 3.5 -0.2 0
	}
	shader "Gutter.shader"
	type box
	name "leftGutter"
}


object {
	transform {
		scalex 2
		scaley 0.6
		scalez 70
		translate -3.5 -0.2 0
	}
	shader "Gutter.shader"
	type box
	name "rightGutter"
}

object {
	transform {
		scalex 9
		scaley 0.4
		scalez 5
		translate 0 -0.25 37.5
	}
	shader "Gutter.shader"
	type box
	name "backGutter"
}

object {
	transform {
		scalex 2
		scaley 2
		scalez 75
		translate 5.5 0.5 2.5
	}
	shader "Wall.shader"
	type box
	name "leftWall"
}

object {
	transform {
		scalex 2
		scaley 2
		scalez 75
		translate -5.5 0.5 2.5
	}
	shader "Wall.shader"
	type box
	name "rightWall"
}

object {
	transform {
		scalex 13
		scaley 5
		translate 0 2.5 38.8
	}
	shader "Gutter.shader"
	type box
	name "backWall"
}
