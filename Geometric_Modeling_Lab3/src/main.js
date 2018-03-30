String.prototype.format = function () {

	var str = this;
	for ( var i = 0; i < arguments.length; i ++ ) {
		str = str.replace( '{' + i + '}', arguments[ i ] );
	}
	
	return str;

};

var container;
var camera, scene, renderer;

var splineHelperObjects = [];
var splinePointsLength = 0;
var positions = [];
var options;

var geometry = new THREE.BoxGeometry( 10, 10, 10 );
var lineMaterial = new THREE.LineBasicMaterial({
			color: 0xff0000,
			opacity: 0.35,
			linewidth: 1 });
var lineGeometry;
var line;


var transformControl;
var indexToBeEditted = 0;

var params = {
	
	/* Curve */
	bezierCurve: bezierCurve,

	/* Surface */

	/* Points */
	addPoint: addPoint,
	removePoint: removePoint,
	insertPoint: insertPoint,
	//duplicatePoint: duplicatePoint,
	clear: clear 
};

function init() {

	container = document.getElementById( 'container' );

	/* Initialize scene and camera */
	scene = new THREE.Scene();
	scene.background = new THREE.Color( 0xf0f0f0 );
	camera = new THREE.PerspectiveCamera( 70, window.innerWidth / window.innerHeight, 1, 10000 );
	camera.position.set( 0, 250, 1000 );
	scene.add( camera );

	/* Initialize light */
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

	/* Initialize shadow on plane */
	var planeGeometry = new THREE.PlaneGeometry( 2000, 2000 );
	planeGeometry.rotateX( - Math.PI / 2 );
	var planeMaterial = new THREE.ShadowMaterial( { opacity: 0.2 } );
	var plane = new THREE.Mesh( planeGeometry, planeMaterial );
	plane.position.y = -200;
	plane.receiveShadow = true;
	scene.add( plane );

	/* Initialize grid on plane */
	var helper = new THREE.GridHelper( 4000, 100 );
	helper.position.y = - 199;
	helper.material.opacity = 0.7;
	helper.material.transparent = true;
	scene.add( helper );

	/* Initialize renderer */
	renderer = new THREE.WebGLRenderer( { antialias: true } );
	renderer.setPixelRatio( window.devicePixelRatio );
	renderer.setSize( window.innerWidth, window.innerHeight );
	renderer.shadowMap.enabled = true;
	container.appendChild( renderer.domElement );

	/* Add gui-dat menu */
	var gui = new dat.GUI();
	var customContainer = document.getElementById('gui');
	customContainer.appendChild(gui.domElement);
	
	var curveMenu = gui.addFolder('Curve');
	curveMenu.add( params, 'bezierCurve' );
	curveMenu.open();

	var editMenu = gui.addFolder('Edit');
	editMenu.add( params, 'addPoint' );
	editMenu.add( params, 'removePoint');
	editMenu.add( params, 'insertPoint');
	//editMenu.add( params, 'duplicatePoint');
	editMenu.add(params, 'clear');
	editMenu.open();

	/* Add controls */
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
	transformControl.addEventListener( 'change', function( e ) {
		cancelHideTransorm();
		updateLine();
	} );
	transformControl.addEventListener( 'mouseDown', function( e ) {
		cancelHideTransorm();
		updateLine();
	} );
	transformControl.addEventListener( 'mouseUp', function( e ) {
		delayHideTransform();
		updateLine();
	} );
	transformControl.addEventListener( 'objectChange', function( e ) {
		updateLine();
	} );

	var dragcontrols = new THREE.DragControls( splineHelperObjects, camera, renderer.domElement ); //
	dragcontrols.enabled = false;
	dragcontrols.addEventListener( 'hoveron', function ( event ) {

		transformControl.attach( event.object );
		cancelHideTransorm();
		updateLine();
	} );

	dragcontrols.addEventListener( 'hoveroff', function ( event ) {

		delayHideTransform();
		updateLine();
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

	line = new THREE.Line(lineGeometry, lineMaterial);	
	scene.add(line);	

	clear();

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
	splineHelperObjects.splice(indexToBeEditted, 0, object);
	return object;

}

function bezierCurve() {

}

function updateLine() {
	scene.remove(line);
	lineGeometry = new THREE.Geometry();
	for (var i = 0; i < splinePointsLength; i++){
		lineGeometry.vertices.push(positions[i]);
	}
	line = new THREE.Line(lineGeometry, lineMaterial);	
	line.castShadow = true;
	line.receiveShadow = true;
	splineHelperObjects.push(line);
	scene.add(line);
}

function addPoint() {
	
	splinePointsLength++;
	positions.push(addSplineObject().position);
	indexToBeEditted++;
	updateLine();
}

function removePoint() {
	
	if ( splinePointsLength <= 0 ) {
		return;
	}
	splinePointsLength --;
	positions.splice(indexToBeEditted - 1, 1);
	indexToBeEditted--;
	scene.remove(splineHelperObjects.splice(indexToBeEditted, 1).pop());
	updateLine();
}

function insertPoint() {
	
	if ( indexToBeEditted <= 0 ) {
		return;
	}
	splinePointsLength++;
	positions.splice(indexToBeEditted - 1, 0, addSplineObject().position);
	indexToBeEditted++;
	updateLine();
}


function clear() {
	positions = [];
	splinePointsLength = 0;
	indexToBeEditted = 0;
	addPoint();
}



function animate() {
	requestAnimationFrame( animate );
	render();
	transformControl.update();
}

function render() {
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


