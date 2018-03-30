String.prototype.format = function () {

	var str = this;
	for ( var i = 0; i < arguments.length; i ++ ) {
		str = str.replace( '{' + i + '}', arguments[ i ] );
	}
	
	return str;

};

var objectArray = [];

var container;
var camera, scene, renderer;
var splineHelperObjects = [];
var transformControl;

/* Control Points */
var controlPointsLength = 0;
var indexToBeEditted = 0;
var positions = [];
var geometry = new THREE.BoxGeometry( 10, 10, 10 );
var lineMaterial = new THREE.LineBasicMaterial({
			color: 0xff0000,
			opacity: 0.05,
			linewidth: 1 });
var lineGeometry;
var line;

/* Curve */
var curvePointsLength = 0;
var curvePoints = [];
var curveColor = new THREE.Color('blue');
var curveMaterial = new THREE.LineBasicMaterial({
	color: curveColor,
	opacity: 0.5,
	linewidth: 2 });
var curveGeometry;
var curve;

/* Surface */
var controlPolygon =[];
var controlPolygonColor = new THREE.Color('green');
var controlPolygonMaterial = new THREE.LineBasicMaterial({
	color: controlPolygonColor,
	opacity: 0.5,
	linewidth: 1 });
var controlPolygonGeometry;
var controlPolygonLine;
const EXTRUSION_TIME = 7;

/* Mesh */
var meshArray = [];
var v1, v2, v3;
var mesh;
var meshGeometry = new THREE.Geometry();
var meshMaterial = new THREE.MeshBasicMaterial();

function addFacet(v1, v2, v3) {
	
	meshGeometry = new THREE.Geometry();
	meshGeometry.vertices.push(v1);
	meshGeometry.vertices.push(v2);
	meshGeometry.vertices.push(v3);
	meshGeometry.faces.push(new THREE.Face3(0, 1 ,2));	


	mesh = new THREE.Mesh(meshGeometry, meshMaterial);
	meshArray.push(mesh);
	scene.add(mesh);
}



var params = {
	
	/* Curve */
	bezierCurve: true,

	/* Surface */

	/* Points */
	addPoint: addPoint,
	removePoint: removePoint,
	insertPoint: insertPoint,
	//duplicatePoint: duplicatePoint,
	clear: clear 
};

function extrusion() {
	
	if (controlPointsLength <= 3){
		return;
	}

	controlPolygon = [];

	var temp = [];
	for (var i = 0; i < controlPointsLength; i++) {
		var p = positions[i];
		temp.push(new THREE.Vector3(p.x, p.y, p.z));
	}
	controlPolygon.push(temp);

	for (var i = 1; i <= EXTRUSION_TIME; i++) {
		var new_position = [];
		for (var j = 0; j < controlPointsLength; j++) {
			var p = new THREE.Vector3(positions[j].x, positions[j].y, positions[j].z);
			p.setY(p.y - i * 70);			
			new_position.push(addSplineObject(p).position);
		}

		controlPolygon.push(new_position);
	}

	for (var i = 1; i <= EXTRUSION_TIME; i++) {
		
		var temp1 = controlPolygon[i - 1];
		var temp2 = controlPolygon[i];
		for (var j = 0; j < controlPointsLength - 1; j++) {
			var p1 = new THREE.Vector3(temp1[j].x, temp1[j].y, temp1[j].z);
			var p2 = new THREE.Vector3(temp2[j].x, temp2[j].y, temp2[j].z);
			var p3 = new THREE.Vector3(temp1[j + 1].x, temp1[j + 1].y, temp1[j + 1].z);
			addFacet(p1, p2, p3);
			// var v1 = new THREE.Vector3(temp2[j + 1].x, temp2[j + 1].y, temp2[j + 1].z);
			// var v2 = new THREE.Vector3(temp2[j].x, temp2[j].y, temp2[j].z);
			// var v3 = new THREE.Vector3(temp1[j + 1].x, temp1[j + 1].y, temp1[j + 1].z);
			// addFacet(v1, v2, v3);
		}
	}

	updateControlPolygon();

}

function updateControlPolygon() {

	if (controlPointsLength > 3) {

		for (var i = 0; controlPolygonLine !== undefined && 
			i <= (EXTRUSION_TIME + controlPointsLength); i++) {
			scene.remove(controlPolygonLine[i]);
		}

		controlPolygonLine = [];
		for (var i = 0; i <= EXTRUSION_TIME; i++) {

			controlPolygonGeometry = new THREE.Geometry();
			var temp = controlPolygon[i];
			for (var j = 0; j < controlPointsLength; j++) {
				controlPolygonGeometry.vertices.push(temp[j]);
			}
			
			var controlPolygonLine_i = new THREE.Line(controlPolygonGeometry, controlPolygonMaterial);
			scene.add(controlPolygonLine_i);
			controlPolygonLine.push(controlPolygonLine_i); 
		}

		for (var i = 0; i < controlPointsLength; i++) {
			var column_position = [];

			for (var j = 0; j <= EXTRUSION_TIME; j++) {
				var temp = controlPolygon[j];
				column_position.push(temp[i]);
			}

			controlPolygonGeometry = new THREE.Geometry();
			for (var j = 0; j <= EXTRUSION_TIME; j++) {
				controlPolygonGeometry.vertices.push(column_position[j]);
			}
			var controlPolygonLine_i = new THREE.Line(controlPolygonGeometry, controlPolygonMaterial);
			scene.add(controlPolygonLine_i); 
			controlPolygonLine.push(controlPolygonLine_i); 
		}

	}
}


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
	plane.position.y = 100;
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
	
	gui.add(params, 'bezierCurve');
	
	var editMenu = gui.addFolder('Edit');
	editMenu.add( params, 'addPoint' );
	editMenu.add( params, 'removePoint');
	editMenu.add( params, 'insertPoint');
	//editMenu.add( params, 'duplicatePoint');
	editMenu.add(params, 'clear');
	editMenu.open();

	gui.open();

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
	} );
	transformControl.addEventListener( 'mouseDown', function( e ) {
		cancelHideTransorm();
	} );
	transformControl.addEventListener( 'mouseUp', function( e ) {
		delayHideTransform();
	} );
	transformControl.addEventListener( 'objectChange', function( e ) {
		updateLine();
		updateCurve();
		extrusion();
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

	lineGeometry = new THREE.Geometry();
	line = new THREE.Line(lineGeometry, lineMaterial);	
	scene.add(line);
	curveGeometry = new THREE.Geometry();
	curve = new THREE.Line(curveGeometry, curveMaterial);
	scene.add(curve);	

	addPoint();
	addPoint();

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
	objectArray.push(object);
	splineHelperObjects.splice(indexToBeEditted, 0, object);
	return object;

}

function updateBezierCurve() {

	curvePoints = [];
	curvePointsLength = 0;

	var factorial = function(n) {
		var fact = 1;
		for (var i = 1; i <= n; i++) {
			fact *= i;
		}
		return fact;
	}		
	var bernstein = function(n, i, u) {
		var result = 1.0;        
        result *= (factorial(n) / factorial(n - i) / factorial(i));        
        result *= Math.pow(u, i);
        result *= Math.pow(1.0 - u, n - i);
        return result;
	}

	var seg = 0.01;
	var j = seg;

	curvePoints.push(positions[0]);
	curvePointsLength++;

	while (j < 1) {

		var p = new THREE.Vector3(0.0, 0.0, 0.0);
		for (var i = 0; i < controlPointsLength; i++) {
			p.setX(p.x + positions[i].x * 
				bernstein(controlPointsLength - 1, i, j));
			p.setY(p.y + positions[i].y * 
				bernstein(controlPointsLength - 1, i, j));
			p.setZ(p.z + positions[i].z * 
				bernstein(controlPointsLength - 1, i, j));
		}
		curvePointsLength++;
		curvePoints.push(p);

		j += seg;
	}

	curvePointsLength++;
	curvePoints.push(positions[controlPointsLength - 1]);

}

function updateCurve() {
	
	if (!params.bezierCurve) {
		scene.remove(curve);
		curveGeometry = new THREE.Geometry();
		curve = new THREE.Line(curveGeometry, curveMaterial);
		curvePointsLength = 0;
		curvePoints = [];
		scene.add(curve);	
		return;
	}

	if (params.bezierCurve && controlPointsLength > 2) {
		scene.remove(curve);
		updateBezierCurve();
		curveGeometry = new THREE.Geometry();
		for (var i = 0; i < curvePointsLength; i++){
			curveGeometry.vertices.push(curvePoints[i]);
		}
		curve = new THREE.Line(curveGeometry, curveMaterial);	
		curve.castShadow = true;
		curve.receiveShadow = true;
		splineHelperObjects.push(curve);
		scene.add(curve);
	}
	
}


function updateLine() {
	scene.remove(line);
	lineGeometry = new THREE.Geometry();
	for (var i = 0; i < controlPointsLength; i++){
		lineGeometry.vertices.push(positions[i]);
	}
	line = new THREE.Line(lineGeometry, lineMaterial);	
	line.castShadow = true;
	line.receiveShadow = true;
	splineHelperObjects.push(line);
	scene.add(line);
}

function addPoint() {
	
	controlPointsLength++;
	positions.push(addSplineObject().position);
	indexToBeEditted++;
	updateLine();
	updateCurve();
	extrusion();
}

function removePoint() {
	
	if ( controlPointsLength <= 0 ) {
		return;
	}
	controlPointsLength --;
	positions.splice(indexToBeEditted - 1, 1);
	indexToBeEditted--;
	scene.remove(splineHelperObjects.splice(indexToBeEditted, 1).pop());

	updateLine();
	updateCurve();
	extrusion();
}

function insertPoint() {
	
	if ( indexToBeEditted <= 0 ) {
		return;
	}
	controlPointsLength++;
	positions.splice(indexToBeEditted - 1, 0, addSplineObject().position);
	indexToBeEditted++;
	updateLine();
	updateCurve();
	extrusion();
}


function clear() {

	for (var i = 0; controlPolygonLine !== [] && 
		i <= (EXTRUSION_TIME + controlPointsLength); i++) {
		scene.remove(controlPolygonLine[i]);
	}

	while(controlPointsLength > 0){
		removePoint();
	}
	addPoint();

	scene.remove(curve);
	curveGeometry = new THREE.Geometry();
	curve = new THREE.Line(curveGeometry, curveMaterial);
	curvePointsLength = 0;
	curvePoints = [];
	scene.add(curve);

	while(objectArray.length > 0) {
		scene.remove(objectArray.pop());
	}
	
	
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


