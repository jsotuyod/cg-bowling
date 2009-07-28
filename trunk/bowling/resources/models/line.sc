
object {
	transform {
		scalex 2.5
		scaley 0.5
		scalez 35
	}
	shader "LineFloor.shader"
	type box
	name "floor"
}


object {
	transform {
		scalex 1
		scaley 0.3
		scalez 35
		translate 3.5 -0.2 0
	}
	shader "Gutter.shader"
	type box
	name "leftGutter"
}


object {
	transform {
		scalex 1
		scaley 0.3
		scalez 35
		translate -3.5 -0.2 0
	}
	shader "Gutter.shader"
	type box
	name "rightGutter"
}

object {
	transform {
		scalex 4.5
		scaley 0.2
		scalez 2.5
		translate 0 -0.25 37.5
	}
	shader "Gutter.shader"
	type box
	name "backGutter"
}

object {
	transform {
		scalex 1
		scaley 1
		scalez 37.5
		translate 5.5 0.5 2.5
	}
	shader "Wall.shader"
	type box
	name "leftWall"
}

object {
	transform {
		scalex 1
		scaley 1
		scalez 37.5
		translate -5.5 0.5 2.5
	}
	shader "Gutter.shader"
	type box
	name "rightWall"
}

object {
	transform {
		scalex 6.5
		scaley 2
		scalez 2
		translate 0 1.5 41
	}
	shader "Gutter.shader"
	type box
	name "backWall"
}
