String.prototype.format = function () {

	var str = this;

	for ( var i = 0; i < arguments.length; i ++ ) {

		str = str.replace( '{' + i + '}', arguments[ i ] );

	}
	return str;

};

var container;
var camera, scene, renderer;
var splineHelperObjects = [], splineOutline;
var splinePointsLength = 4;
var positions = [];
var options;

var geometry = new THREE.BoxGeometry( 20, 20, 20 );
var transformControl;

var ARC_SEGMENTS = 200;
var splineMesh;

var splines = {};

var params = {
	
	uniform: true,

	bezierCurve: bezierCurve,

	addPoint: addPoint,
	removePoint: removePoint,
	insertPoint: insertPoint,
	duplicatePoint: duplicatePoint,

	clear: clear 
};


function init() {

	container = document.getElementById( 'container' );

	scene = new THREE.Scene();
	scene.background = new THREE.Color( 0xf0f0f0 );

	camera = new THREE.PerspectiveCamera( 70, window.innerWidth / window.innerHeight, 1, 10000 );
	camera.position.set( 0, 250, 1000 );
	scene.add( camera );

	scene.add( new THREE.AmbientLight( 0xf0f0f0 ) );
	var light = new THREE.SpotLight( 0xffffff, 1.5 );
	light.position.set( 0, 1500, 200 );
	light.castShadow = true;
	light.shadow = new THREE.LightShadow( new THREE.PerspectiveCamera( 70, 1, 200, 2000 ) );
	light.shadow.bias = -0.000222;
	light.shadow.mapSize.width = 1024;
	light.shadow.mapSize.height = 1024;
	scene.add( light );
	spotlight = light;

	var planeGeometry = new THREE.PlaneGeometry( 2000, 2000 );
	planeGeometry.rotateX( - Math.PI / 2 );
	var planeMaterial = new THREE.ShadowMaterial( { opacity: 0.2 } );

	var plane = new THREE.Mesh( planeGeometry, planeMaterial );
	plane.position.y = -200;
	plane.receiveShadow = true;
	scene.add( plane );

	var helper = new THREE.GridHelper( 2000, 100 );
	helper.position.y = - 199;
	helper.material.opacity = 0.25;
	helper.material.transparent = true;
	scene.add( helper );

	renderer = new THREE.WebGLRenderer( { antialias: true } );
	renderer.setPixelRatio( window.devicePixelRatio );
	renderer.setSize( window.innerWidth, window.innerHeight );
	renderer.shadowMap.enabled = true;
	container.appendChild( renderer.domElement );


	var gui = new dat.GUI({ autoPlace: false });
	var customContainer = document.getElementById('gui');
	customContainer.appendChild(gui.domElement);
	
	var curveMenu = gui.addFolder('Curve');
	curveMenu.add( params, 'uniform' );
	curveMenu.add( params, 'bezierCurve' );


	var editMenu = gui.addFolder('Edit');
	editMenu.add( params, 'addPoint' );
	editMenu.add( params, 'removePoint');
	editMenu.add( params, 'insertPoint');
	editMenu.add( params, 'duplicatePoint');
	editMenu.add(params, 'clear');

	gui.open();

	// Controls
	var controls = new THREE.OrbitControls( camera, renderer.domElement );
	controls.damping = 0.2;
	controls.addEventListener( 'change', render );

	controls.addEventListener( 'start', function() {

		cancelHideTransorm();

	} );

	controls.addEventListener( 'end', function() {

		delayHideTransform();

	} );

	transformControl = new THREE.TransformControls( camera, renderer.domElement );
	transformControl.addEventListener( 'change', render );
	scene.add( transformControl );

	// Hiding transform situation is a little in a mess :()
	transformControl.addEventListener( 'change', function( e ) {

		cancelHideTransorm();

	} );

	transformControl.addEventListener( 'mouseDown', function( e ) {

		cancelHideTransorm();

	} );

	transformControl.addEventListener( 'mouseUp', function( e ) {

		delayHideTransform();

	} );

	transformControl.addEventListener( 'objectChange', function( e ) {

		updateSplineOutline();

	} );

	var dragcontrols = new THREE.DragControls( splineHelperObjects, camera, renderer.domElement ); //
	dragcontrols.enabled = false;
	dragcontrols.addEventListener( 'hoveron', function ( event ) {

		transformControl.attach( event.object );
		cancelHideTransorm();

	} );

	dragcontrols.addEventListener( 'hoveroff', function ( event ) {

		delayHideTransform();

	} );

	var hiding;

	function delayHideTransform() {

		cancelHideTransorm();
		hideTransform();

	}

	function hideTransform() {

		hiding = setTimeout( function() {

			transformControl.detach( transformControl.object );

		}, 2500 )

	}

	function cancelHideTransorm() {

		if ( hiding ) clearTimeout( hiding );

	}


	/*******
	 * Curves
	 *********/

	for ( var i = 0; i < splinePointsLength; i ++ ) {

		addSplineObject( positions[ i ] );

	}

	positions = [];

	for ( var i = 0; i < splinePointsLength; i ++ ) {

		positions.push( splineHelperObjects[ i ].position );

	}

	var geometry = new THREE.Geometry();

	for ( var i = 0; i < ARC_SEGMENTS; i ++ ) {

		geometry.vertices.push( new THREE.Vector3() );

	}

	var curve = new THREE.CatmullRomCurve3( positions );
	curve.curveType = 'catmullrom';
	curve.mesh = new THREE.Line( geometry.clone(), new THREE.LineBasicMaterial( {
		color: 0xff0000,
		opacity: 0.35,
		linewidth: 2
		} ) );
	curve.mesh.castShadow = true;
	splines.uniform = curve;


	for ( var k in splines ) {

		var spline = splines[ k ];
		scene.add( spline.mesh );

	}

	load( [ new THREE.Vector3( 289.76843686945404, 452.51481137238443, 56.10018915737797 ),
			new THREE.Vector3( -53.56300074753207, 171.49711742836848, -14.495472686253045 ),
			new THREE.Vector3( -91.40118730204415, 176.4306956436485, -6.958271935582161 ),
			new THREE.Vector3( -383.785318791128, 491.1365363371675, 47.869296953772746 ) ] );

}

function addSplineObject( position ) {

	var material = new THREE.MeshLambertMaterial( { color: Math.random() * 0xffffff } );
	var object = new THREE.Mesh( geometry, material );

	if ( position ) {

		object.position.copy( position );

	} else {

		object.position.x = Math.random() * 1000 - 500;
		object.position.y = Math.random() * 600;
		object.position.z = Math.random() * 800 - 400;

	}

	object.castShadow = true;
	object.receiveShadow = true;
	scene.add( object );
	splineHelperObjects.push( object );
	return object;

}

function bezierCurve() {

}

function addPoint() {
	
	splinePointsLength ++;
	positions.push( addSplineObject().position );
	
	updateSplineOutline();
}

function removePoint() {
	
	if ( splinePointsLength <= 4 ) {
		return;
	}
	splinePointsLength --;
	positions.pop();
	scene.remove( splineHelperObjects.pop() );

	updateSplineOutline();
}

function insertPoint() {

}

function duplicatePoint() {

}

function clear() {

}

function updateSplineOutline() {

	for ( var k in splines ) {
		
		var spline = splines[ k ];
		splineMesh = spline.mesh;

		for ( var i = 0; i < ARC_SEGMENTS; i ++ ) {

			var p = splineMesh.geometry.vertices[ i ];
			var t = i /  ( ARC_SEGMENTS - 1 );
			spline.getPoint( t, p );

		}

		splineMesh.geometry.verticesNeedUpdate = true;

	}

}

function load( new_positions ) {

	while ( new_positions.length > positions.length ) {

		addPoint();

	}

	while ( new_positions.length < positions.length ) {

		removePoint();

	}

	for ( var i = 0; i < positions.length; i ++ ) {

		positions[ i ].copy( new_positions[ i ] );

	}

	updateSplineOutline();

}

function animate() {
	requestAnimationFrame( animate );
	render();
	transformControl.update();
}

function render() {
	splines.uniform.mesh.visible = params.uniform;
	renderer.render( scene, camera );
}


window.onload = function(){

	//Initialize.addElements();
	
	/* Add canvas */
	var div = document.createElement("div");
	var body = document.getElementsByTagName("body")[0];
	div.setAttribute("id", 'container');
	body.appendChild(div);
	init();
	animate();

}

window.onload();

