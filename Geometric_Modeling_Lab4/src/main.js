
String.prototype.format = function () {

	var str = this;
	for ( var i = 0; i < arguments.length; i ++ ) {
		str = str.replace( '{' + i + '}', arguments[ i ] );
	}
	return str;
};

var static = false;

var objectArray = [];
var geometry = new THREE.BoxGeometry( 10, 10, 10 );

var container;
var camera, scene, renderer, spotlight;
var splineHelperObjects = [];
var transformControl;

/* Control Points */
var controlPoints = {
	size: 0,
	nextIndex: 0,
	positions: []
};


var lineMaterial = new THREE.LineBasicMaterial({
			color: 0xff0000,
			opacity: 0.05,
			linewidth: 1 });
var lineGeometry;
var line;

/* Curve */
var curvePoints = {
	size: 0,
	positions: []
};

var curveColor = new THREE.Color('blue');
var curveMaterial = new THREE.LineBasicMaterial({
	color: curveColor, opacity: 0.5, linewidth: 3 });
var curveGeometry;
var curve;

/* Surface */
const EXTRUSION_TIME = 12;
const OFFSET = 150;
var controlPolygon = {
	row: 0,
	column: 0,
	positions: []
};

var controlPolygonColor = new THREE.Color('green');
var controlPolygonMaterial = new THREE.LineBasicMaterial({
	color: controlPolygonColor, opacity: 0.5, linewidth: 0.5 });
var controlPolygonGeometry;
var controlPolygonLine = [];

var subdivisions = 4;

/* Surface in Lab 3 */
var controlSurface = {
	m: 0,
	n: 0,
	positions:[],
	u: 0.1,
	v: 0.1
};


/* Geometry */
var numberOfVertices;
var numberOfFaces;
var vertices;
var faces;
var edges;
/* edge_faces[i][j]: edge(v[i], v[i + j + 1]) has adjacent faces [F1, F2] or 0 */
var edge_faces;
/*
 * degree_edge[i] := [vp, vq, vr, vs, ...]
 * vertice(i) incident with edge vivp, vivq, vivr, vivs, ...
 */
var degree_edge;
/*
 * degree_face[i] := [Fp, Fq, Fr, Fs, ...]
 * vertice(i) adjacent to faces Fp, Fq, Fr, Fs, ...
 */
var degree_face;

/* Mesh */
var meshArray = [];
var v1, v2, v3;
var mesh;
var meshGeometry = new THREE.Geometry();
var meshMaterial = new THREE.MeshStandardMaterial( { color : 0x00cc00 } );

var params = {

	/* Curve Reconstruction */
	'Curve Reconstruction': '',
	'Curve Reconstruction Visible': false,
	'Curve Shape': '',

	/* Points */
	'Add Point': addPoint,
	'Remove Point': removePoint,
	'Insert Point': insertPoint,
	'Duplicate Point': duplicatePoint,
	'Clear': clear,
	'Generate Random Points': 12,
	"Voronoi Diagram": false,
	"Delaunay Diagram": false,

	/* value */
	'Subdivision': 4,
	'u': 0.1,
	'v': 0.1,

	/* Curve */
	'Curve': '',
	'Curve Visible': false,
	//'Closed': false,

	/* Control Points Generator */
	'Control Polyhedron': '',
	'Control Polyhedron Visible': false,

	/* Surface Type */
	'Surface': '',

	/* Import and Export */
	'Import': importOFF,
	'Export': exportOFF

};

function updateRandomPoints(value)
{
	for (var i = 0; i < value; i++) {			
		controlPoints.size++;
		var x, z;
		do {
          x = Math.random() - 0.5;
          z = Math.random() - 0.5;
        } while(x * x + z * z > 0.25);

        x = (x * 0.96875 + 0.5) * window.innerWidth * 3 / 4;
        z = (z * 0.96875 + 0.5) * window.innerHeight;
		//var x = Math.random() * 1000 - 500;
		var y = 0;
		//var z = Math.random() * 800 - 400;
		var object = addSplineObject(new THREE.Vector3(x, y, z));
		controlPoints.positions.splice(controlPoints.nextIndex, 0, object.position);
		splineHelperObjects.splice(splineHelperObjects.length, 0, object);
		controlPoints.nextIndex++;
	}	
}

function updateSampling()
{
	clear();
	if (params["Curve Shape"] == "Circle") {
		var radius = 400;
		for (var i = 0; i < 30; i++) {
			var angle = Math.random() * 2 * Math.PI;
			controlPoints.size++;
			var x = radius * Math.cos(angle);
			var y = 0;
			var z = radius * Math.sin(angle);
			var object = addSplineObject(new THREE.Vector3(x, y, z));
			controlPoints.positions.splice(controlPoints.nextIndex, 0, object.position);
			splineHelperObjects.splice(splineHelperObjects.length, 0, object);
			controlPoints.nextIndex++;
		}
	}
}

var max_distance = [0, 0, 0]; // [distance, index1, index2]
function updateCurveReconstruction()
{
	var temp = [];

	if (params["Curve Reconstruction"] == "Crust") {
		temp = Crust();
	} else if (params["Curve Reconstruction"] == "NN-Crust") {
		temp = NNCrust();
	}
	//console.log(temp);
	while (controlPoints.size > 0) {
		controlPoints.size --;
		controlPoints.positions.splice(controlPoints.nextIndex - 1, 1);
		controlPoints.nextIndex--;
		scene.remove(splineHelperObjects.splice(controlPoints.nextIndex, 1).pop());
	}
	

	for (var i = 0; i < temp.length; i++) {			
		controlPoints.size++;
		var object = addSplineObject(new THREE.Vector3(temp[i].x, temp[i].y, temp[i].z));
		controlPoints.positions.splice(controlPoints.nextIndex, 0, object.position);
		splineHelperObjects.push(object);
		controlPoints.nextIndex++;
		//update3D();
	}	
}

var VoronoiEdge = [];
function drawVoronoiEdge(p1, p2)
{
	var VoronoiGeometry = new THREE.Geometry();
	VoronoiGeometry.vertices.push(new THREE.Vector3(p1.x, p1.y, p1.z));
	VoronoiGeometry.vertices.push(new THREE.Vector3(p2.x, p2.y, p2.z));
	var new_VoronoiEdge = new THREE.Line(VoronoiGeometry, controlPolygonMaterial);
	scene.add(new_VoronoiEdge);
	new_VoronoiEdge.castShadow = true;
	new_VoronoiEdge.receiveShadow = true;
	VoronoiEdge.push(new_VoronoiEdge);
}
function removeVoronoiEdge()
{
	while (VoronoiEdge.length > 0) {
		scene.remove(VoronoiEdge.pop());
	}
	VoronoiEdge = [];
}

var DelaunayEdge = [];
function drawDelaunayEdge(p1, p2)
{
	var DelaunayGeometry = new THREE.Geometry();
	DelaunayGeometry.vertices.push(new THREE.Vector3(p1.x, p1.y, p1.z));
	DelaunayGeometry.vertices.push(new THREE.Vector3(p2.x, p2.y, p2.z));
	var new_DelaunayEdge = new THREE.Line(DelaunayGeometry, curveMaterial);
	scene.add(new_DelaunayEdge);
	new_DelaunayEdge.castShadow = true;
	new_DelaunayEdge.receiveShadow = true;
	DelaunayEdge.push(new_DelaunayEdge);
}
function removeDelaunayEdge()
{
	while (DelaunayEdge.length > 0) {
		scene.remove(DelaunayEdge.pop());
	}
	DelaunayEdge = [];
}

function computeCircumcircle(V, i, j, k) {
	var x1 = V[i].x, z1 = V[i].z,
		x2 = V[j].x, z2 = V[j].z,
		x3 = V[k].x, z3 = V[k].z;
	var e = 1E-10;
	var k1, k2, x, z, mx1, mx2, mz1, mz2, r;

	/* Calculate middle points */
	mx1 = (x1 + x2) / 2.0;
    mx2 = (x2 + x3) / 2.0;
    mz1 = (z1 + z2) / 2.0;
    mz2 = (z2 + z3) / 2.0;

    if (Math.abs(z1 - z2) < e) {
    	k2  = -((x3 - x2) / (z3 - z2));
        x  = (x2 + x1) / 2.0;
        z  = k2 * (x - mx2) + mz2;
    } else if (Math.abs(z2 - z3) < 0) {
    	k1 = -((x2 - x1) / (z2 - z1));
    	x  = (x3 + x2) / 2.0;
      	z  = k1 * (x - mx1) + mz1;
    } else {
    	k1  = -((x2 - x1) / (z2 - z1));
      	k2  = -((x3 - x2) / (z3 - z2));      	
      	x  = (k1 * mx1 - k2 * mx2 + mz2 - mz1) / (k1 - k2);
      	z  = (Math.abs(z1 - z2) > Math.abs(z2 - z3)) ?
        k1 * (x - mx1) + mz1 :
        k2 * (x - mx2) + mz2;
    }

    r = (x2 - x) * (x2 - x) + (z2 - z) * (z2 - z);

    //console.log([i, j, k, x, z, r]);
    return [i, j, k, x, z, r]; /* vertices of a triangle, center of circle, radius ^ 2 */
}

function dedup(edges) {
    
    var i, j, a, b, m, n;
    for(j = edges.length; j; ) {
      b = edges[--j];
      a = edges[--j];

      for(i = j; i; ) {
        n = edges[--i];
        m = edges[--i];

        if((a === m && b === n) || (a === n && b === m)) {
          edges.splice(j, 2);
          edges.splice(i, 2);
          break;
        }
      }
    }
 }


function computeDelaunay()
{
	//removeDelaunayEdge();

	/* Initialize indices array */
	var indices = [];
	for (var i = 0; i < controlPoints.size; i++) {
		indices.push(i);
	}
	var n = controlPoints.size;

	/* Sort indices based on the coordinate of x */
	indices.sort(function(i, j) {
        var diff = controlPoints.positions[i].x - controlPoints.positions[j].x;
        return diff !== 0 ? diff : j - i;
    });

	/* Construct a super triangle */
	// var triangles = [];
	// var temp_triangles = [];
	var M = 0;
	for (var i = 0; i < controlPoints.size; i++) {
		var p = controlPoints.positions[i];
		if (Math.abs(p.x) > M) M = Math.abs(p.x);
		if (Math.abs(p.z) > M) M = Math.abs(p.z);
	}
	var a = new THREE.Vector3(0, 0, 3 * M);
	var b = new THREE.Vector3(-3 * M, 0, -3 * M);
	var c = new THREE.Vector3(3 * M, 0, 0);
	var superTriangle = [a, b, c];
	controlPoints.positions.push(a, b, c);
	// triangles.push(superTriangle);
	// temp_triangles.push(superTriangle);

	console.log(controlPoints.positions.length);
	var open = [];
	open.push(computeCircumcircle(controlPoints.positions, n, n + 1, n + 2));
	//console.log(open[0]);
    var closed = [];
    var Delaunay_edges  = [];

	/* Traverse each point in control points */
	var t1, t2, t3, dx, dy;
	for (var i = indices.length; i--; Delaunay_edges.length = 0) {
		t3 = indices[i];
		for(var j = open.length; j--; ) {         
		    dx = controlPoints.positions[t3].x - open[j][3];
		    if(dx > 0.0 && dx * dx > open[j][5]) {
		      closed.push(open[j]);
		      open.splice(j, 1);
		      continue;
		    }


	        dy = controlPoints.positions[t3].z - open[j][4];
	        if(dx * dx + dy * dy - open[j][5] > 1E-10)
	          continue;
          Delaunay_edges.push(
            open[j][0], open[j][1],
            open[j][1], open[j][2],
            open[j][2], open[j][0]
          );
          open.splice(j, 1);
	    }

        /* Remove any doubled edges. */
        dedup(Delaunay_edges);

        /* Add a new triangle for each edge. */
        for(j = Delaunay_edges.length; j; ) {
          t2 = Delaunay_edges[--j];
          t1 = Delaunay_edges[--j];
          open.push(computeCircumcircle(controlPoints.positions, t1, t2, t3));
        }
    }

    for(i = open.length; i--; ) closed.push(open[i]);
    open.length = 0;

    for(i = closed.length; i--; )
        if(closed[i][0] < n && closed[i][1] < n && closed[i][2] < n) 
        	open.push(closed[i]);

    //console.log(open);
    controlPoints.positions.splice(n, 3);
    return open;

}

function computeVoronoi()
{
	removeVoronoiEdge();
}

var nearestNeighbor;

/* O(n) */
function findNearestNeighbor( i )
{
	var point = controlPoints.positions[i];
	var d = 0, ret = 0;
	for (var j = 0; j < controlPoints.size; j++) {
		var p = controlPoints.positions[j];
		var d_temp = Math.sqrt((p.x - point.x) * (p.x - point.x) + (p.z - point.z) * (p.z - point.z));
		if ((d == 0 || d > d_temp) && d_temp != 0) {
			d = d_temp;
			ret = j;
		}
	}

	if (max_distance == 0 || max_distance < d) max_distance = [d, i, ret];
	return ret;
}
/* O(n) */
function halfNeighbor( i1, i2 )
{
	var p1 = controlPoints.positions[i1];
	var p2 = controlPoints.positions[i2];
	var k = (-1) / ((p2.z - p1.z) / (p2.x - p1.x));
	var b = p1.z - k * p1.x;
	var side = p2.z - (k * p2.x + b);
	var d = 0, ret = 0;;

	for (var i = 0; i < controlPoints.size; i++) {
		var p = controlPoints.positions[i];
		if (side * (p.z - (k * p.x + b)) < 0) {
			var d_temp = Math.sqrt((p.x - p1.x) * (p.x - p1.x) + (p.z - p1.z) * (p.z - p1.z));
			if ((d == 0 || d > d_temp) && d_temp != 0) {
				d = d_temp;
				ret = i;
			}
		}
	}
	if (max_distance < d) {
		max_distance = [d, i1, ret];
	}
	return ret;
}

function NNCrust() {

	/* O(n) */
	nearestNeighbor = [];
	for (var i = 0; i < controlPoints.size; i++) {
		nearestNeighbor.splice(i, 0, []);
		if (controlPoints.positions[i].y != 0) {
			alert("The points are not in the same plane! Please refresh the page!");
			return;
		}
	}

	/* Compute the nearest neighbor: O(n^2) */
	for (var i = 0; i < controlPoints.size; i++) {
		if (nearestNeighbor[i].length < 2) {
			var nearest_index = findNearestNeighbor( i );	
			if (nearestNeighbor[i].indexOf(parseInt( nearest_index )) < 0) {
				nearestNeighbor[i].push(parseInt( nearest_index ));	
			} 
			if (nearestNeighbor[parseInt( nearest_index )].indexOf(parseInt( i )) < 0) {
				nearestNeighbor[parseInt( nearest_index )].push(i);
			} 
		}			
	}
	/* Compute the half neighbor: O(n^2) */
	for (var i = 0; i < controlPoints.size; i++) {
		if (nearestNeighbor[i].length != 2) {
			var half_index = halfNeighbor( i, nearestNeighbor[i][0] );
			nearestNeighbor[i].push(parseInt( half_index ));
			nearestNeighbor[parseInt( half_index )].push(i);
		}
	}
	//console.log(nearestNeighbor);
	/* Reorder vertices: O(n) */
	var temp = [];	
	var i1 = max_distance[1];
	var i2 = nearestNeighbor[i1][0] == max_distance[2] ? nearestNeighbor[i1][1] : nearestNeighbor[i1][0];
	var p = controlPoints.positions[i1]
	temp.push(new THREE.Vector3(p.x, p.y, p.z));
	for (var i = 1; i < controlPoints.size; i++) {
		//console.log(i1 + "\t" +i2);
		p = controlPoints.positions[i2]
		temp.push(new THREE.Vector3(p.x, p.y, p.z));
		var i_temp = i1;
		i1 = i2;
		i2 = nearestNeighbor[i1][0] == i_temp ? nearestNeighbor[i1][1] : nearestNeighbor[i1][0];
	}
	

	return copyAndModifyYOfArray(temp, 0, 0);
}

var scene_temp = [];
function Crust() {
	
	/* 
	 *	CRUST(P) 
	 */
	 static = true;
	 scene_temp = [];
	/* compute Vor P */
	var triangles = computeDelaunay();
	/* let V be the Voronoi Vertices of Vor P */
	var size_temp = parseInt(controlPoints.size);
	//var Vor = [];
	//console.log(triangles.length);
	//console.log(controlPoints.positions[0]);
	var temp_ctrl = [];
	for (var i = 0; i < triangles.length; i++) {
		var p1 = controlPoints.positions[triangles[i][0]];
		var p2 = controlPoints.positions[triangles[i][1]];
		var p3 = controlPoints.positions[triangles[i][2]];
		var x = triangles[i][3];
		var z = triangles[i][4];
		temp_ctrl.push(new THREE.Vector3(parseFloat(x), 0, parseFloat(z)));
		//controlPoints.size++;
		//controlPoints.nextIndex++;
		// drawDelaunayEdge(p1, p2);
		// drawDelaunayEdge(p2, p3);
		// drawDelaunayEdge(p3, p1);
		// drawVoronoiEdge(Vor[Vor.length - 1], p1);
		// drawVoronoiEdge(Vor[Vor.length - 1], p2);
		// drawVoronoiEdge(Vor[Vor.length - 1], p3);
	}

	//console.log(controlPoints.positions.length);
	//console.log(controlPoints.positions);
	for (var i = 0; i < temp_ctrl.length; i++) {

		var object = addSplineObject(new THREE.Vector3(temp_ctrl[i].x, 0, temp_ctrl[i].z));
		controlPoints.positions.splice(controlPoints.nextIndex, 0, object.position);
		scene_temp.push(object);
		//splineHelperObjects.splice(splineHelperObjects.length, 0, object);
		controlPoints.size++;
		controlPoints.nextIndex++;
	}
	static = false;
	//console.log(controlPoints.positions.length);

	/* compute Del(P U V) */
	var triangles2 = computeDelaunay();

	/* E := [] */
	var edge = [];
	for (var i = 0; i < size_temp; i++) {
		edge.push([-1, -1]);
	}

	/* 
		for each edge pq in Del(P U V) do
			if p in P and q in P
				E := E U pq;
			endif	/* 
	*/
	//console.log(triangles2);
	//console.log(controlPoints.positions);
	for (var i = 0; i < triangles2.length; i++) {
		var p1 = controlPoints.positions[triangles2[i][0]];
		var p2 = controlPoints.positions[triangles2[i][1]];
		var p3 = controlPoints.positions[triangles2[i][2]];
		drawDelaunayEdge(p1, p2);
		drawDelaunayEdge(p2, p3);
		drawDelaunayEdge(p3, p1);
		if (triangles2[i][0] < size_temp && triangles2[i][1] < size_temp) {
			var i1 = edge[triangles2[i][0]].indexOf(-1);
			edge[triangles2[i][0]][i1] = parseInt(triangles2[i][1]);
			var i2 = edge[triangles2[i][1]].indexOf(-1);
			edge[triangles2[i][1]][i2] = parseInt(triangles2[i][0]);
		}
		else if (triangles2[i][2] < size_temp && triangles2[i][1] < size_temp) {
			var i1 = edge[triangles2[i][2]].indexOf(-1);
			edge[triangles2[i][2]][i1] = parseInt(triangles2[i][1]);
			var i2 = edge[triangles2[i][1]].indexOf(-1);
			edge[triangles2[i][1]][i2] = parseInt(triangles2[i][2]);
		}
		else if (triangles2[i][0] < size_temp && triangles2[i][2] < size_temp) {
			var i1 = edge[triangles2[i][0]].indexOf(-1);
			edge[triangles2[i][0]][i1] = parseInt(triangles2[i][2]);
			var i2 = edge[triangles2[i][2]].indexOf(-1);
			edge[triangles2[i][2]][i2] = parseInt(triangles2[i][0]);
		}
		
	}

	//console.log(controlPoints.positions);
	for (var i = 0; i < triangles.length; i++) {
		controlPoints.positions.splice(size_temp, 1);
	}
	//console.log(controlPoints.positions);
	controlPoints.size = size_temp;
	controlPoints.nextIndex = size_temp;

	var temp = [], i1, i2;
	temp.push(controlPoints.positions[0]);
	i1 = 0;
	i2 = 0;
	console.log(edge);
	//console.log(controlPoints.positions);
	for (var i = 1; i < size_temp; i++) {
		if (edge[i1].length > 0) {
			i2 = edge[i1][0];
			edge[i1].pop();
			edge[i2].splice(edge[i2].indexOf(i1), 1);
			console.log(i1 + "\t" + i2);
			var p = controlPoints.positions[i2];
			temp.push(new THREE.Vector3(p.x, p.y, p.z));
			i1 = i2;
		} 
		//i2 = edge[i1][0] != i1 ? edge[i1][1] : edge[i1][0];
		
	}

	console.log(temp);
	return  copyAndModifyYOfArray(temp, 0, 0);

}

// function addFacet(v1, v2, v3) {

// 	meshGeometry = new THREE.Geometry();
// 	meshGeometry.vertices.push(v1);
// 	meshGeometry.vertices.push(v2);
// 	meshGeometry.vertices.push(v3);
// 	meshGeometry.faces.push(new THREE.Face3(0, 1 ,2));


// 	mesh = new THREE.Mesh(meshGeometry, meshMaterial);
// 	meshArray.push(mesh);
// 	mesh.castShadow = true;
// 	mesh.receiveShadow = true;
// 	scene.add(mesh);
// }
// function addFacet4(v1, v2, v3, v4) {
// 	meshGeometry = new THREE.Geometry();
// 	meshGeometry.vertices.push(v1);
// 	meshGeometry.vertices.push(v2);
// 	meshGeometry.vertices.push(v3);
// 	meshGeometry.vertices.push(v4);
// 	meshGeometry.faces.push(new THREE.Face4(0, 1 ,2, 3));


// 	mesh = new THREE.Mesh(meshGeometry, meshMaterial);
// 	meshArray.push(mesh);
// 	mesh.castShadow = true;
// 	mesh.receiveShadow = true;
// 	scene.add(mesh);
// }
// function clearMesh() {
// 	while (meshArray.length > 0){
// 		scene.remove(meshArray.pop());
// 	}
// 	meshArray = [];
// }

/* Bezier Surface and Cubic B-Spline Surface */
var currentVerticeIndex = -1;
var currentEdgeIndex = -1;

function addMiddlePoints() {

	controlSurface.positions = [];
	if (controlPolygon.positions.length <= 0) {
		return;
	}

	/* Rows */
	for (var i = 0; i < controlPolygon.row; i++) {

		var new_row = [];
		new_row.push(new THREE.Vector3(controlPolygon.positions[i][0].x,
			controlPolygon.positions[i][0].y, controlPolygon.positions[i][0].z));
		new_row.push(new THREE.Vector3(controlPolygon.positions[i][1].x,
			controlPolygon.positions[i][1].y, controlPolygon.positions[i][1].z));

		for (var j = 2; j < controlPolygon.column - 3; j += 2) {
			new_row.push(new THREE.Vector3(controlPolygon.positions[i][j].x,
				controlPolygon.positions[i][j].y, controlPolygon.positions[i][j].z));


			new_row.push(new THREE.Vector3((controlPolygon.positions[i][j].x + controlPolygon.positions[i][j + 1].x) / 2,
			(controlPolygon.positions[i][j].y + controlPolygon.positions[i][j + 1].y) / 2,
			(controlPolygon.positions[i][j].z + controlPolygon.positions[i][j + 1].z) / 2));

			new_row.push(new THREE.Vector3(controlPolygon.positions[i][j + 1].x,
				controlPolygon.positions[i][j + 1].y, controlPolygon.positions[i][j + 1].z));
		}

		if (controlPolygon.column % 2 != 0) {

			var j = controlPolygon.column - 3;

			new_row.push(new THREE.Vector3(controlPolygon.positions[i][j].x,
				controlPolygon.positions[i][j].y, controlPolygon.positions[i][j].z));

			new_row.push(new THREE.Vector3((controlPolygon.positions[i][j].x + controlPolygon.positions[i][j + 1].x) / 2,
			(controlPolygon.positions[i][j].y + controlPolygon.positions[i][j + 1].y) / 2,
			(controlPolygon.positions[i][j].z + controlPolygon.positions[i][j + 1].z) / 2));

			new_row.push(new THREE.Vector3(controlPolygon.positions[i][j + 1].x,
				controlPolygon.positions[i][j + 1].y, controlPolygon.positions[i][j + 1].z));

			new_row.push(new THREE.Vector3((controlPolygon.positions[i][j + 2].x + controlPolygon.positions[i][j + 1].x) / 2,
			(controlPolygon.positions[i][j + 2].y + controlPolygon.positions[i][j + 1].y) / 2,
			(controlPolygon.positions[i][j + 2].z + controlPolygon.positions[i][j + 1].z) / 2));

			new_row.push(new THREE.Vector3(controlPolygon.positions[i][j + 2].x,
				controlPolygon.positions[i][j + 2].y, controlPolygon.positions[i][j + 2].z));
		} else {
			for (var j = controlPolygon.column - 2; j < controlPolygon.column; j++) {
				new_row.push(new THREE.Vector3(controlPolygon.positions[i][j].x,
					controlPolygon.positions[i][j].y, controlPolygon.positions[i][j].z));
			}
		}

		controlSurface.positions.push(new_row);

		if (i > 0 && ((i % 2 == 0 && i < controlPolygon.row - 3) ||
			(controlPolygon.row % 2 != 0 && i == controlPolygon.row - 3) ||
			(controlPolygon.row % 2 != 0 && i == controlPolygon.row - 2))) {
			var temp = [];
			controlSurface.positions.push(temp);
		}
	}

	controlSurface.n = controlSurface.positions[0].length;
	controlSurface.m = controlSurface.positions.length;



	/* Columns */
	for (var j = 0; j < controlSurface.n; j++) {
		for (var i = 3; i < controlSurface.m - 1; i += 3 ) {
			var p1 = controlSurface.positions[i - 1][j];
			var p2 = controlSurface.positions[i + 1][j];
			controlSurface.positions[i].push(new THREE.Vector3((p1.x + p2.x) / 2, (p1.y + p2.y) / 2,
				(p1.z + p2.z) / 2));
		}

		if (controlPolygon.row % 2 != 0) {
			var i = controlSurface.m - 2;
			var p1 = controlSurface.positions[i - 1][j];
			var p2 = controlSurface.positions[i + 1][j];
			controlSurface.positions[i].push(new THREE.Vector3((p1.x + p2.x) / 2, (p1.y + p2.y) / 2,
				(p1.z + p2.z) / 2));
		}
	}


}

function calculateBezierSurfaceP(u, w, P) {
	if (P.length != 4 || P[0].length != 4) {
		alert("Error!");
		return;
	}

	var A1, A2, A3, A4;
	A1 = (1-u)*(1-u)*(1-u);
	A2 = 3*u*(1-u)*(1-u);
	A3 = 3*u*u*(1-u);
	A4 = u*u*u;
	var A = [A1, A2, A3, A4];

	var B1, B2, B3, B4;
	B1 = (1-w)*(1-w)*(1-w);
	B2 = 3*w*(1-w)*(1-w);
	B3 = 3*w*w*(1-w);
	B4 = w*w*w;
	var B = [B1, B2, B3, B4];


	var newA = [];
	for (var i = 0; i < 4; i++){
		var x = 0;
		var y = 0;
		var z = 0;
		for (var j = 0; j < 4; j++) {


			x += A[j] * P[j][i].x;
			y += A[j] * P[j][i].y;
			z += A[j] * P[j][i].z;
		}
		newA.push(new THREE.Vector3(x, y, z));
	}

	var result;
	var x = 0;
	var y = 0;
	var z = 0;
	for (var i = 0; i < 4; i++){
		x += newA[i].x * B[i];
		y += newA[i].y * B[i];
		z += newA[i].z * B[i];
	}
	result = new THREE.Vector3(x, y, z);
	return result;
}

function updateBezierSurface() {
	currentVerticeIndex = -1;
	vertices = [];
	faces = [];

	if (controlPolygon.positions.length <= 0) {
		return;
	}

	addMiddlePoints();
	console.log(controlSurface.positions.length + "  " + controlSurface.positions[0].length);

	var temp_col1 = [];
	var temp_col2 = [];
	var temp_col3 = [];
	for (var j = 3; j < controlSurface.n; j += 3) {
		var temp1 = [];
		temp_col1 = [];
		temp_col2 = [];
		for (var i = 3; i < controlSurface.m; i += 3) {

			var P = [[controlSurface.positions[i - 3][j - 3], controlSurface.positions[i - 3][j - 2],
				controlSurface.positions[i - 3][j - 1], controlSurface.positions[i - 3][j]],
				[controlSurface.positions[i - 2][j - 3], controlSurface.positions[i - 2][j - 2],
				controlSurface.positions[i - 2][j - 1], controlSurface.positions[i - 2][j]],
				[controlSurface.positions[i - 1][j - 3], controlSurface.positions[i - 1][j - 2],
				controlSurface.positions[i - 1][j - 1], controlSurface.positions[i - 1][j]],
				[controlSurface.positions[i][j - 3], controlSurface.positions[i][j - 2],
				controlSurface.positions[i][j - 1], controlSurface.positions[i][j]]];

			var u = 0;
			var v = 0;


			while (u <= 1) {

				var temp2 = [];
				v = 0;
				while (v <= 1) {
					temp2.push(calculateBezierSurfaceP(u,v,P));
					v += controlSurface.v;
				}

				if (temp1.length > 0) {
					for (var k = 0; k < temp1.length - 2; k++) {
						var temp3 = [];
						var p1 = addVertices(temp1[k]);
						var p2 = addVertices(temp1[k + 1]);
						var p3 = addVertices(temp2[k + 1]);
						var p4 = addVertices(temp2[k]);
						temp3.push(p1);
						temp3.push(p2);
						temp3.push(p3);
						temp3.push(p4);
						faces.push(temp3);
						//addFacet4(vertices[temp3[0]], vertices[temp3[1]], vertices[temp3[2]], vertices[temp3[3]]);

						if (k == temp1.length - 3) {
							if (temp_col2.length == 0) {
								temp_col2.push(parseInt(p2));
							}
							temp_col2.push(parseInt(p3));
						}

						if (j > 3 && k == 0) {
							if (temp_col1.length == 0) {
								temp_col1.push(parseInt(p1));
							}
							temp_col1.push(parseInt(p4));
						}
					}
				}
				temp1 = copyAndModifyYOfArray(temp2, 0, 0);
				u += controlSurface.u;
			}
		}

		if (j > 3) {
			for (var k = 0; k < temp_col1.length - 1; k++) {
				var temp3 = [];
				console.log(temp_col1.length + "\t" + temp_col2.length);
				temp3.push(temp_col1[k]);
				temp3.push(temp_col1[k + 1]);
				temp3.push(temp_col3[k + 1]);
				temp3.push(temp_col3[k]);
				faces.push(temp3);
				//addFacet4(vertices[temp3[0]], vertices[temp3[1]], vertices[temp3[2]], vertices[temp3[3]]);
			}
		}

		temp_col3 = [];
		for (var k = 0; k < temp_col2.length; k++) {
			temp_col3.push(parseInt(temp_col2[k]));
		}

	}

	numberOfVertices = vertices.length;
	numberOfFaces = faces.length;
}

function addVertices(v) {
	vertices.forEach(function(element){
		if (parseFloat(v.x) == parseFloat(element.x) &&
			parseFloat(v.y) == parseFloat(element.y) &&
			parseFloat(v.z) == parseFloat(element.z)) {
			return vertices.indexOf(element);
		}
	});


	vertices.push(v);
	currentVerticeIndex++;
	return currentVerticeIndex;
}

function basis(i, u) {

	var result = 0;
	switch (i) {
		case 1:
			result = 1 / 6.0 * (-u * u * u + 3 * u * u - 3 * u + 1);
			break;
		case 2:
			result = 1 / 6.0 * (3 * u * u * u - 6 * u * u + 4);
			break;
		case 3:
			result = 1 / 6.0 * (-3 * u * u * u + 3 * u * u + 3 * u + 1);
			break;
		case 4:
			result = 1 / 6.0 * u * u * u;
			break;
		default:
			break;
	}
	return result;
}

function updateSplinePoints() {

	currentVerticeIndex = -1;
	vertices = [];
	faces = [];
	controlSurface.positions = [];
	if (controlPolygon.positions.length <= 0) {
		return;
	}
	controlSurface.m = controlPolygon.row - 1;
	controlSurface.n = controlPolygon.column - 1;

	var temp4 = [];
	var temp_col1 = [];
	var temp_col2 = [];
	var temp_col3 = [];
	for (var j = 1; j <= controlSurface.n - 2; j++) {
		var temp1 = [];
		temp_col1 = [];
		temp_col2 = [];
		for (var i = 1; i <= controlSurface.m - 2; i++) {

			var P = [];
			var p00 = controlPolygon.positions[i - 1][j - 1];
			var p01 = controlPolygon.positions[i - 1][j];
			var p02 = controlPolygon.positions[i - 1][j + 1];
			var p03 = controlPolygon.positions[i - 1][j + 2];
			var p10 = controlPolygon.positions[i][j - 1];
			var p11 = controlPolygon.positions[i][j];
			var p12 = controlPolygon.positions[i][j + 1];
			var p13 = controlPolygon.positions[i][j + 2];
			var p20 = controlPolygon.positions[i + 1][j - 1];
			var p21 = controlPolygon.positions[i + 1][j];
			var p22 = controlPolygon.positions[i + 1][j + 1];
			var p23 = controlPolygon.positions[i + 1][j + 2];
			var p30 = controlPolygon.positions[i + 2][j - 1];
			var p31 = controlPolygon.positions[i + 2][j];
			var p32 = controlPolygon.positions[i + 2][j + 1];
			var p33 = controlPolygon.positions[i + 2][j + 2];
			P = [[p00, p01, p02, p03], [p10, p11, p12, p13],
					[p20, p21, p22, p23], [p30, p31, p32, p33]];

			var u = 0;
			while (u <= 1) {
				var v = 0;

				var temp2 = [];
				while (v <= 1) {

					var x = 0, y = 0, z = 0;

					for (var k = 1; k <= 4; k++) {
						for (var l = 1; l <= 4; l++) {
							x += basis(k, u) * basis(l, v) * P[k - 1][l - 1].x;
							y += basis(k, u) * basis(l, v) * P[k - 1][l - 1].y;
							z += basis(k, u) * basis(l, v) * P[k - 1][l - 1].z;
						}
					}

					temp2.push(new THREE.Vector3(x, y, z));
					v += controlSurface.v;
				}
				temp4.push(new THREE.Vector3(temp2[0].x, temp2[0].y, temp2[0].z));
				if (temp4.length > 0) {
					temp2.splice(0, 0, temp4.splice(0, 1).pop());
				}

				if (temp1.length > 0) {

					for (var k = 0; k < temp1.length - 2; k++) {
						var temp3 = [];
						var p1 = addVertices(temp1[k]);
						var p2 = addVertices(temp1[k + 1]);
						var p3 = addVertices(temp2[k + 1]);
						var p4 = addVertices(temp2[k]);
						temp3.push(p1);
						temp3.push(p2);
						temp3.push(p3);
						temp3.push(p4);
						faces.push(temp3);

						if (k == temp1.length - 3) {
							if (temp_col2.length == 0) {
								temp_col2.push(parseInt(p2));
							}
							temp_col2.push(parseInt(p3));
						}

						if (j > 1 && k == 0) {
							if (temp_col1.length == 0) {
								temp_col1.push(parseInt(p1));
							}
							temp_col1.push(parseInt(p4));
						}
					}
				}

				temp1 = copyAndModifyYOfArray(temp2, 0, 0);
				u += controlSurface.u;
			}
		}
		if (j > 1) {
			for (var k = 0; k < temp_col1.length - 1; k++) {
				var temp3 = [];
				console.log(temp_col1.length + "\t" + temp_col2.length);
				temp3.push(temp_col1[k]);
				temp3.push(temp_col1[k + 1]);
				temp3.push(temp_col3[k + 1]);
				temp3.push(temp_col3[k]);
				faces.push(temp3);
				//addFacet4(vertices[temp3[0]], vertices[temp3[1]], vertices[temp3[2]], vertices[temp3[3]]);
			}
		}

		temp_col3 = [];
		for (var k = 0; k < temp_col2.length; k++) {
			temp_col3.push(parseInt(temp_col2[k]));
		}
	}

	numberOfVertices = vertices.length;
	numberOfFaces = faces.length;

}

/* Calculate degree_edge and degree_face */
function getDegree() {

	degree_edge = [];
	degree_face = [];

	/* Check whether the data is correct stored */
	if (numberOfVertices == 0 || numberOfFaces == 0 ||
			numberOfFaces != faces.length || numberOfVertices != vertices.length) {
				alert("Fail to process getDegree()! Clear data!");
				vertices = [];
				faces = [];
				edge_faces = [];
				currentEdgeIndex = -1;
				currentVerticeIndex = -1;
				return;
			}

	for (var i = 0; i < numberOfVertices; i++) {
		degree_edge.push(0);
		degree_face.push(0);
	}

	for (var i = 0; i < numberOfFaces; i++) {

		var oneface = faces[i];
		for (var j = 0; j < oneface.length; j++) {

					var v1 = faces[i][j];
					var v2;
					if (j == oneface.length - 1) {
						v2 = faces[i][0];
					} else {
						v2 = faces[i][j + 1];
					}

					/* Add to degree_edge */
					if (typeof(degree_edge[v1]) == typeof(0)) {
						degree_edge[v1] = [parseInt(v2)];
					} else if (degree_edge[v1].indexOf(parseInt(v2)) < 0) {
						degree_edge[v1].push(parseInt(v2));
					}

					if (typeof(degree_edge[v2]) == typeof(0)) {
						degree_edge[v2] = [parseInt(v1)];
					} else if (degree_edge[v2].indexOf(parseInt(v1)) < 0) {
						degree_edge[v2].push(parseInt(v1));
					}

					/* Add to degree_face */
					if (typeof(degree_face[v1]) == typeof(0)) {
						degree_face[v1] = [parseInt(i)];
					} else {
						degree_face[v1].push(parseInt(i));
					}

		}

	}

}

var facePoint = [];
var edgePoint = [];
var faces_temp = [];
var vertices_temp = [];
var edgeLib = [];
/* Doo-Sabin Surface */
function updateDooSabin() {

	if (numberOfFaces == 0 || numberOfVertices == 0) {
		alert("Invalid Vertices and Faces Data!");
		return;
	}

	for (var sub = 0; sub < subdivisions; sub++) {
		console.log("Doo-Sabin Subdivision\t" + sub);
		addToEdges();
		getDegree();

		/* Clear vertices and faces */
		currentVerticeIndex = numberOfVertices - 1;
		vertices_temp = copyAndModifyYOfArray(vertices, 0, 0);
		vertices = [];
		var vLib = []; 		/* vLib[i] := start index of v[i] in vertices */
		for (var i = 0; i < numberOfVertices; i++) {
			vLib.push(parseInt(vertices.length));
			for (var j = 0; j < degree_face[i].length; j++) {
						vertices.push(0);
			}
		}
		edgeLib = [];
		for (var i = 0; i < numberOfVertices - 1; i++) {
			var temp = [];
			for (var j = i + 1; j < numberOfVertices; j++) {
				if (typeof(edge_faces[i][j - i - 1]) != typeof(0)) {
					temp.push([-1, -1, -1, -1]);
				} else {
					temp.push([-2]);
				}
			}
			edgeLib.push(temp);
		}
		faces_temp = [];
		facePoint = [];
		edgePoint = [];
		for (var i = 0; i < numberOfFaces; i++) {
			var temp1 = faces.splice(0, 1).pop();
			var verticesInFace = temp1.length;
			var temp2 = [];
			for (var j = 0; j < verticesInFace; j++) {
				var v = parseInt(temp1[j]);
				temp2.push(v);
			}
			faces_temp.push(temp2);
		}
		faces = [];

		computeFacePoint();

		/* Calculate new points */
		for (var i = 0; i < numberOfFaces; i++) {

			var oneface = faces_temp[i];
			var temp = [];
			var x = facePoint[i];
			for (var j = 0; j < oneface.length; j++) {

				var vp = faces_temp[i][j];
				var wp = j < oneface.length - 1 ? faces_temp[i][j + 1] : faces_temp[i][0];
				var indexi = vp < wp ? vp : wp;
				var indexj = vp < wp ? wp - vp - 1 : vp - wp - 1;

				var newVp = parseInt(vLib[vp] + degree_face[vp].indexOf(i));
				var newWp = parseInt(vLib[wp] + degree_face[wp].indexOf(i));
				var v = vertices_temp[vp];

				/* Calculate middle points */
				var v1, v2, ve1, ve2, a, b, c;
				if (j == 0) {
					v1 = vertices_temp[faces_temp[i][oneface.length - 1]];
				} else {
					v1 = vertices_temp[faces_temp[i][j - 1]];
				}
				if (j == oneface.length - 1) {
					v2 = vertices_temp[faces_temp[i][0]];
				} else {
					v2 = vertices_temp[faces_temp[i][j + 1]];
				}
				a = (v.x + v1.x) / 2;
				b = (v.y + v1.y) / 2;
				c = (v.z + v1.z) / 2;
				ve1 = new THREE.Vector3(a, b, c);
				a = (v.x + v2.x) / 2;
				b = (v.y + v2.y) / 2;
				c = (v.z + v2.z) / 2;
				ve2 = new THREE.Vector3(a, b, c);

				/* Add result into vertices and faces */
				a = (x.x + ve1.x + ve2.x + v.x) / 4;
				b = (x.y + ve1.y + ve2.y + v.y) / 4;
				c = (x.z + ve1.z + ve2.z + v.z) / 4;
				vertices[newVp] = new THREE.Vector3(a, b, c);
				temp.push(newVp);


				if (edgeLib[indexi][indexj].indexOf(-1) >= 0) {
					edgeLib[indexi][indexj].splice(0, 2);
					if (vp < wp) {
						edgeLib[indexi][indexj].push(parseInt(newVp));
						edgeLib[indexi][indexj].push(parseInt(newWp));
					} else {
						edgeLib[indexi][indexj].push(parseInt(newWp));
						edgeLib[indexi][indexj].push(parseInt(newVp));
					}

				}

			}
			faces.push(temp);
		}

		/* Add points split faces */
		for (var i = 0; i < numberOfVertices; i++) {
			var temp = [];

			for (var j = vLib[i]; j < vLib[i] + degree_face[i].length; j+=2) {
				temp.push(j);
			}
			for (var j = vLib[i] + 1; j < vLib[i] + degree_face[i].length; j+=2) {
				temp.push(j);
			}
			faces.push(temp);
		}

		/* Add edges split faces */
		for (var i = 0; i < numberOfVertices - 1; i++) {
			for (var j = 0; j + i < numberOfVertices - 1; j++) {
				if (edgeLib[i][j].indexOf(-2) < 0) {
					var temp = edgeLib[i][j];
					if (temp[0] == -1 || temp[1] == -1 || temp[2] == -1 || temp[3] == -1)
					{
						console.log(i + "\t" + j);
					}
					faces.push([temp[0], temp[1], temp[3], temp[2]]);
				}
			}
		}

		/* End of function */
		numberOfVertices = vertices.length;
		numberOfFaces = faces.length;
	}
}

/* Catmull-Clark Surface */
function updateCatmullClark() {
	if (numberOfFaces == 0 || numberOfVertices == 0) {
		alert("Invalid Vertices and Faces Data!");
		return;
	}

	for (var sub = 0; sub < subdivisions; sub++) {
		console.log("Catmull-Clark Subdivision\t" + sub);
		//rectangulation();
		addToEdges();
		getDegree();

		/* Clear vertices and faces */
		currentVerticeIndex = numberOfVertices - 1;
		vertices_temp = copyAndModifyYOfArray(vertices, 0, 0);
		for (var i = 0; i < numberOfVertices; i++) {
			vertices[i] = 0;
		}
		faces_temp = [];
		facePoint = [];
		edgePoint = [];
		for (var i = 0; i < numberOfFaces; i++) {
			var temp1 = faces.splice(0, 1).pop();
			var verticesInFace = temp1.length;
			var temp2 = [];
			for (var j = 0; j < verticesInFace; j++) {
				var v = parseInt(temp1[j]);
				temp2.push(v);
			}
			faces_temp.push(temp2);
		}
		faces = [];

		/* Compute face points, edge points, and new vertex points */
		/* facePoint[i] = vertices[numberOfVertices + i] */
		computeFacePoint();
		for (var i = 0; i < facePoint.length; i++) {
			vertices.push(new THREE.Vector3(facePoint[i].x, facePoint[i].y, facePoint[i].z));
			currentVerticeIndex++;
		}
		computeEdgePoint();

		for (var i = 0; i < numberOfFaces; i++) {

			var x, y, z;
			/* Calculate vF */
			var vF = numberOfVertices + i;

			/* Calculate vV0, vV1, vV2, vV3 */
			/* v' = Q/n + 2R/n + (n-3)v/n */
			var vV = [];
			for (var j = 0; j < faces_temp[i].length; j++) {
				var vp = faces_temp[i][j];
				if (typeof(vertices[vp]) != typeof(0)) {
					vV.push(vp);
				} else {
					var v = vertices_temp[vp];
					var adjacentFaces = degree_face[vp];
					var incidentEdges = degree_edge[vp];

					/* Original point */
					x = (faces_temp[i].length - 3) / faces_temp[i].length * v.x;
					y = (faces_temp[i].length - 3) / faces_temp[i].length * v.y;
					z = (faces_temp[i].length - 3) / faces_temp[i].length * v.z;

					/* Average of face points */
					for (var k = 0; k < adjacentFaces.length; k++) {
							var fp = adjacentFaces[k];
							x += ((facePoint[fp].x) / adjacentFaces.length) / faces_temp[i].length;
							y += ((facePoint[fp].y) / adjacentFaces.length) / faces_temp[i].length;
							z += ((facePoint[fp].z) / adjacentFaces.length) / faces_temp[i].length;
					}

					/* Average of midpoints */
					for (var k = 0; k < incidentEdges.length; k++) {
						var wp = incidentEdges[k];
						var indexi = wp > vp ? vp : wp;
						var indexj = wp > vp ? wp - vp - 1 : vp - wp - 1;
						x += (2 * (edgePoint[indexi][indexj].x) / incidentEdges.length) / faces_temp[i].length;
						y += (2 * (edgePoint[indexi][indexj].y) / incidentEdges.length) / faces_temp[i].length;
						z += (2 * (edgePoint[indexi][indexj].z) / incidentEdges.length) / faces_temp[i].length;
					}


					vertices[vp] = new THREE.Vector3(x, y, z);
					vV.push(vp);
				}
			}

			/* Calculate vE0, vE1, vE2, vE3 */
			var vE = [];
			for (var j = 0; j < faces_temp[i].length; j++) {

				var vp = faces_temp[i][j];
				var wp;
				if (j < faces_temp[i].length - 1) {
					wp = faces_temp[i][j + 1];
				} else {
					wp = faces_temp[i][0];
				}

				if (vp > wp) {
					var temp = vp;
					vp = parseInt(wp);
					wp = parseInt(temp);
				}
				var index = wp - vp - 1;
				var vEi;
				if (typeof(edgePoint[vp][index]) != typeof(1)) {
					vEi = edgePoint[vp][index];
					vertices.push(new THREE.Vector3(vEi.x, vEi.y, vEi.z));
					currentVerticeIndex++;
					edgePoint[vp][index] = parseInt(currentVerticeIndex);
				}

				vEi = parseInt(edgePoint[vp][index]);

				vE.push(vEi);
			}

			for (var j = 0; j < vV.length; j++) {
				var temp = [];
				temp.push(vF);
				temp.push(vE[j]);
				temp.push(vV[j]);
				if (j != 0) {
					temp.push(vE[j - 1]);
				} else {
					temp.push(vE[vV.length - 1]);
				}
				faces.push(temp);
			}
		}

		/* End of function */
		numberOfVertices = vertices.length;
		numberOfFaces = faces.length;
	}

}
// function rectangulation() {
//
// 	currentVerticeIndex = numberOfVertices - 1;
// 	for (var m = 0; m < numberOfFaces; m++) {
//
// 		var oneface = faces.splice(0, 1).pop();
// 		if (oneface.length < 4) {
// 			alert("Fail to rectangulation! Please re-import a file!");
// 			vertices = [];
// 			faces = [];
// 			edge_faces = [];
// 			currentEdgeIndex = -1;
// 			currentVerticeIndex = -1;
// 			return;
// 		}
// 		if (oneface.length > 4) {
// 			/* Even number of vertices of each face */
// 			if (oneface.length % 2 == 0) {
// 				for (var i = 1; oneface.length - i >= 3; i += 2) {
// 					var v1, v2, v3, v4;
// 					v1 = oneface[0];
// 					v2 = oneface[i];
// 					v3 = oneface[i + 1];
// 					v4 = oneface[i + 2];
// 					var temp = [parseInt(v1), parseInt(v2), parseInt(v3), parseInt(v4)];
// 					faces.push(temp);
// 				}
// 			}
// 			/* Odd number of vertices of each face */
// 			else {
// 				for (var i = 1; oneface.length - i != 4; i += 2) {
// 					var v1, v2, v3, v4;
// 					v1 = oneface[0];
// 					v2 = oneface[i];
// 					v3 = oneface[i + 1];
// 					v4 = oneface[i + 2];
// 					var temp = [parseInt(v1), parseInt(v2), parseInt(v3), parseInt(v4)];
// 					faces.push(temp);
// 				}
//
// 				var v0, v1, v2, v3, v4, vAdd;
// 				var i = oneface.length - 4;
// 				v0 = oneface[0];
// 				v1 = oneface[i];
// 				v2 = oneface[i + 1];
// 				v3 = oneface[i + 2];
// 				v4 = oneface[i + 3];
//
// 				var p0, p1, pAdd;
// 				p0 = new THREE.Vector3(vertices[v0].x, vertices[v0].y, vertices[v0].z);
// 				p1 = new THREE.Vector3(vertices[v1].x, vertices[v1].y, vertices[v1].z);
// 				pAdd = new THREE.Vector3((p0.x + p1.x) / 2, (p0.y + p1.y) / 2, (p0.z + p1.z) / 2);
// 				vAdd = addVertices(pAdd);
//
// 				var temp1 = [parseInt(v0), parseInt(vAdd), parseInt(v3), parseInt(v4)];
// 				var temp2 = [parseInt(vAdd), parseInt(v1), parseInt(v2), parseInt(v3)];
// 				faces.push(temp1);
// 				faces.push(temp2);
//  			}
// 		}
// 		else {
// 			faces.push(oneface);
// 		}
// 	}
//
// 	numberOfFaces = faces.length;
// 	numberOfVertices = vertices.length;
// }
function computeFacePoint() {
	if (faces_temp.length == 0 || vertices_temp.length == 0) {
		alert("Fail to compute face points! Please re-import a file!");
		vertices = [];
		faces = [];
		edge_faces = [];
		currentEdgeIndex = -1;
		currentVerticeIndex = -1;
		return;
	}

	facePoint = [];
	for (var i = 0; i < numberOfFaces; i++) {
		var x = 0;
		var y = 0;
		var z = 0;

		for (var j = 0; j < faces_temp[i].length; j++) {
			x += vertices_temp[faces_temp[i][j]].x;
			y += vertices_temp[faces_temp[i][j]].y;
			z += vertices_temp[faces_temp[i][j]].z;
		}

		x /= faces_temp[i].length;
		y /= faces_temp[i].length;
		z /= faces_temp[i].length;

		facePoint.push(new THREE.Vector3(x, y, z));

	}
}
function computeEdgePoint() {
	if (faces_temp.length == 0 || vertices_temp.length == 0) {
		alert("Fail to compute face points! Please re-import a file!");
		vertices = [];
		faces = [];
		edge_faces = [];
		currentEdgeIndex = -1;
		currentVerticeIndex = -1;
		return;
	}

	edgePoint = [];
	for (var i = 0; i < edge_faces.length; i++) {

		/* Ve = (v + w + vF1 + vF2) / 4 */
		edgePoint.push(0);
		edgePoint[i] = [];
		var v = vertices_temp[i];
		for (var j = 0; i + j < edge_faces.length; j++) {

			if (edge_faces[i][j] != 0) {

				var w = vertices_temp[i + j + 1];
				var vF1p = edge_faces[i][j][0];
				var vF2p = edge_faces[i][j][1];
				var vF1 = facePoint[vF1p];
				var vF2 = facePoint[vF2p];

				var x = (v.x + w.x + vF1.x + vF2.x) / 4;
				var y = (v.y + w.y + vF1.y + vF2.y) / 4;
				var z = (v.z + w.z + vF1.z + vF2.z) / 4;
				edgePoint[i].push(new THREE.Vector3(x, y, z));
			} else {
				edgePoint[i].push(new THREE.Vector3(0, 0, 0));
			}

		}
	}

}


/* Loop Surface */
function updateLoop() {
	if (numberOfFaces == 0 || numberOfVertices == 0) {
		alert("Invalid Vertices and Faces Data!");
		return;
	}

	triangulation();

	for (var sub = 0; sub < subdivisions; sub++) {
		console.log("Loop Subdivision\t" + sub);
		addToEdges();
		getDegree();

		/* Clear vertices and faces */
		currentVerticeIndex = numberOfVertices - 1;
		vertices_temp = copyAndModifyYOfArray(vertices, 0, 0);
		for (var i = 0; i < numberOfVertices; i++) {
			vertices[i] = 0;
		}
		var edge_save_odd = [];
		for (var a = 0; a < numberOfVertices - 1; a++) {
			var temp2 = [];
			for (var b = a + 1; b < numberOfVertices; b++) {
					temp2.push(-1);
			}
			edge_save_odd.push(temp2);
		}
		faces_temp = [];
		facePoint = [];
		edgePoint = [];
		for (var i = 0; i < numberOfFaces; i++) {
			var temp1 = faces.splice(0, 1).pop();
			var verticesInFace = temp1.length;
			var temp2 = [];
			for (var j = 0; j < verticesInFace; j++) {
				var v = parseInt(temp1[j]);
				temp2.push(v);
			}
			faces_temp.push(temp2);
		}
		faces = [];

		for (var i = 0; i < numberOfFaces; i++) {

			var r, s, p, q;
			var x, y, z;
			var odd_v = [];
			var even_v = [];

			for (var j = 0; j < 3; j++) {
				r = faces_temp[i][j % 3];
				s = faces_temp[i][(j + 1) % 3];
				p = faces_temp[i][(j + 2) % 3];

				var indexi = r > s ? s : r;
				var indexj = r > s ? r - s - 1 : s - r - 1;
				var indexQ = edge_faces[indexi][indexj][0] == i ?
					edge_faces[indexi][indexj][1] : edge_faces[indexi][indexj][0];

				/* Calculate odd_v */
				if (edge_save_odd[indexi][indexj] < 0) {
					for (var k = 0; k < 3; k++) {
						if (faces_temp[indexQ][k] != r && faces_temp[indexQ][k] != s) {
							q = faces_temp[indexQ][k];
							k = 3;
						}
					}

					x = vertices_temp[p].x / 8 + 3 * vertices_temp[r].x / 8 + 3 * vertices_temp[s].x / 8 + vertices_temp[q].x / 8;
					y = vertices_temp[p].y / 8 + 3 * vertices_temp[r].y / 8 + 3 * vertices_temp[s].y / 8 + vertices_temp[q].y / 8;
					z = vertices_temp[p].z / 8 + 3 * vertices_temp[r].z / 8 + 3 * vertices_temp[s].z / 8 + vertices_temp[q].z / 8;
					vertices.push(new THREE.Vector3(x, y, z));
					currentVerticeIndex++;
					edge_save_odd[indexi][indexj] = currentVerticeIndex;
				}
				odd_v.push(parseInt(edge_save_odd[indexi][indexj]));

				/* Calculate even_v */
				var v = vertices_temp[r];
				x = 5 / 8 * v.x;
				y = 5 / 8 * v.y;
				z = 5 / 8 * v.z;
				for (var k = 0; k < degree_edge[r].length; k++) {
					var index = degree_edge[r][k];
					x += 3 / 8 / degree_edge[r].length * vertices_temp[index].x;
					y += 3 / 8 / degree_edge[r].length * vertices_temp[index].y;
					z += 3 / 8 / degree_edge[r].length * vertices_temp[index].z;
				}

				vertices[r] = new THREE.Vector3(x, y, z);
				even_v.push(parseInt(r));
			}

			faces.push([even_v[0], odd_v[0], odd_v[2]]);
			faces.push([even_v[2], odd_v[1], odd_v[2]]);
			faces.push([even_v[1], odd_v[0], odd_v[1]]);
			faces.push([odd_v[1], odd_v[0], odd_v[2]]);
		}

		/* End of function */
		numberOfVertices = vertices.length;
		numberOfFaces = faces.length;
	}

}
function triangulation() {

	for (var m = 0; m < numberOfFaces; m++) {

		var oneface = faces.splice(0, 1).pop();
		for (var n = 1; n <= oneface.length - 2; n++) {
			// var v1 = vertices[faces[m][n]];
			// var v2 = vertices[faces[m][n + 1]];
			// var v3;
			// if ( n != faces[m].length - 2) {
			// 	v3 = vertices[faces[m][n + 2]];
			// } else {
			// 	v3 = vertices[faces[m][0]];
			// }
			// v1 = new THREE.Vector3(v1.x, v1.y, v1.z);
			// v2 = new THREE.Vector3(v2.x, v2.y, v2.z);
			// v3 = new THREE.Vector3(v3.x, v3.y, v3.z);
			// var temp = [];
			// temp.push(v1);
			// temp.push(v2);
			// temp.push(v3);
			// faces.push(temp);
			// addFacet(v1,v2,v3);

			var v1, v2, v3;
			v1 = oneface[0];
			v2 = oneface[n];
			v3 = oneface[n + 1];


			var temp = [parseInt(v1), parseInt(v2), parseInt(v3)];
			faces.push(temp);
		}

	}
	numberOfFaces = faces.length;

}
/* Import and Export */
var readBuffer;

function updateInput() {

	if (typeof(readBuffer) != typeof("string")) {
		return;
	}

	currentVerticeIndex = -1;
	currentEdgeIndex = -1;

	/* Find number of vertices and faces */
	var i = 0;
	while (readBuffer.charAt(i) != " " && readBuffer.charAt(i) != "\t") {
		i++;
	}
	numberOfVertices = parseInt(readBuffer.substring(0, i));
	var j = i + 1;
	while (readBuffer.charAt(i) != "\n") {
		i++;
	}
	numberOfFaces = parseInt(readBuffer.substring(j, i));


	/* Store Vertices */
	vertices = [];
	i++;
	for (var m = 0; m < numberOfVertices; m++) {
		j = i;
		while (readBuffer.charAt(i) != " " && readBuffer.charAt(i) != "\t") {
			i++;
		}
		var x = parseFloat(readBuffer.substring(j, i));
		i++;
		j = i;
		while (readBuffer.charAt(i) != " " && readBuffer.charAt(i) != "\t") {
			i++;
		}
		var y = parseFloat(readBuffer.substring(j, i));
		i++;
		j = i;
		while (readBuffer.charAt(i) != "\n") {
			i++;
		}
		var z = parseFloat(readBuffer.substr(j, i));
		currentVerticeIndex++;
		vertices.push(new THREE.Vector3(x, y, z));
	}

	/* Store Faces */
	faces = [];
	i++;

	//clearMesh();

	for (var m = 0; m < numberOfFaces; m++) {
		j = i;
		while (readBuffer.charAt(i) != " "  && readBuffer.charAt(i) != "\t") {
			i++;
		}
		var verticesOfFace = parseInt(readBuffer.substring(j, i));

		i++;
		var temp = [];
		for (var n = 0; n < verticesOfFace - 1; n++) {
			j = i;
			while (readBuffer.charAt(i) != " " && readBuffer.charAt(i) != "\t") {
				i++;
			}
			var vi = parseInt(readBuffer.substring(j, i));
			i++;
			temp.push(vi);
		}
		j = i;
		while (readBuffer.charAt(i) != '\n' && i != readBuffer.length - 1) {
			i++;
		}
		if (i == readBuffer.length - 1) {
			i++;
		}
		var vi = parseInt(readBuffer.substring(j, i));
		temp.push(vi);
		faces.push(temp);

	}

	//addToEdges();
}

function addToEdges() {

		/* Store edges */
		//edges = [];
		edge_faces = [];
		for (var a = 0; a < numberOfVertices - 1; a++) {
			//var temp1 = [];
			var temp2 = [];
			for (var b = a + 1; b < numberOfVertices; b++) {
					//temp1.push(parseInt(b));
					temp2.push(0);
			}
			//edges.push(temp1);
			edge_faces.push(temp2);
		}


		for (var a = 0; a < numberOfFaces; a++)
		{
			var temp = faces[a];
			var verticesInFace = parseInt(faces[a].length);

			for (var b = 0; b < verticesInFace; b++) {

				var v1 = temp[b];
				var v2;
				if (b == verticesInFace - 1) {
					v2 = temp[0];
				} else {
					v2 = temp[b + 1];
				}

				if (parseInt(v1) > parseInt(v2)) {
					var vtemp = parseInt(v1);
					v1 = parseInt(v2);
					v2 = vtemp;
				}

				// var faceNumber = addEdges(v1, v2);
				// if (faceNumber > edge_faces.length - 1)
				// {
				// 	var temp = [];
				// 	edge_faces.push(temp);
				// }
				// edge_faces[faceNumber].push(parseInt(a));

				var index = parseInt(parseInt(v2) - parseInt(v1) - 1);

				if (typeof(edge_faces[v1][index]) == typeof(0)) {
					edge_faces[v1][index] = [];
				}

				edge_faces[v1][index].push(parseInt(a));

			}

		}
}

// function addEdges(v1, v2) {
//
// 	for (var i = 0; i < edges.length; i++) {
// 		if ((v1 == edges[i][0] && v2 == edges[i][1]) || (v2 == edges[i][0] && v1 == edges[i][1])) {
// 			return i;
// 		}
// 	}
//
// 	var temp = [];
// 	temp.push(parseInt(v1));
// 	temp.push(parseInt(v2));
// 	edges.push(temp);
// 	currentEdgeIndex++;
//
// 	return currentEdgeIndex;
// }

function handleFiles(files) {
	if (files.length) {
		var file = files[0];

		var reader = new FileReader();
        if (/text\/\w+/.test(file.type)) {
            reader.onload = function() {
				readBuffer = this.result;
				updateInput();
            }
            reader.readAsText(file);
		}


	}



}

// function update3DSurface() {

// 	clear();
// 	removePoint();
// 	triangulation();


// 	for (var i = 0; i < numberOfFaces; i++) {
// 		//create a triangular geometry
// 		meshGeometry = new THREE.Geometry();
// 		var p0 = vertices[faces[i][0]];
// 		var p1 = vertices[faces[i][1]];
// 		var p2 = vertices[faces[i][2]];
// 		meshGeometry.vertices.push( new THREE.Vector3(p0.x, p0.y, p0.z) );
// 		meshGeometry.vertices.push( new THREE.Vector3(p1.x, p1.y, p1.z) );
// 		meshGeometry.vertices.push( new THREE.Vector3(p2.x, p2.y, p2.z) );

// 		var face = new THREE.Face3( 0, 1, 2);
// 		//add the face to the geometry's faces array
// 		meshGeometry.faces.push( face );

// 		//the face normals and vertex normals can be calculated automatically if not supplied above
// 		meshGeometry.computeFaceNormals();
// 		meshGeometry.computeVertexNormals();

// 		scene.add( new THREE.Mesh( meshGeometry, meshMaterial ) );
// 	}
// }

function importOFF() {
	var x = document.getElementById('myInput');
	x.click();
}

function exportOFF() {

	var newWindow = window.open();
	newWindow.document.open()
	newWindow.document.write("<html><head></head><body>");
	newWindow.document.write("<p>OFF");
	newWindow.document.write("<br>" + numberOfVertices + " "+ numberOfFaces + " " + "0");

	for (var i = 0; i < numberOfVertices; i++)
	{
		newWindow.document.write("<br>"+ vertices[i].x + " " + vertices[i].y + " " + vertices[i].z);
	}

	for (var i = 0; i < numberOfFaces; i++) {

		newWindow.document.write("<br>"+ faces[i].length);
		for (var j = 0; j < faces[i].length; j++) {
			newWindow.document.write(" " + faces[i][j]);
		}
		//newWindow.document.write("</p>");
	}


	newWindow.document.write("</p></body></html>");
	newWindow.document.close();
}

/* Lab 1 and Lab 2 */
function copyAndModifyYOfArray(A, row_index, offset) {

	var B = [];
	for (var i = 0; i < A.length; i++) {
		var p = new THREE.Vector3(A[i].x, A[i].y, A[i].z);
		p.setY(p.y - row_index * 70);
		B.push(p);
	}

	return B;
}

function update3D(changeControlPolygon) {
	
	if (params["Curve Reconstruction Visible"]){
		updateLine();
	} else {
		removeLine();
	}

	if (controlPoints.size >= 3) {
		updateCurve();	
		if (params["Curve Visible"]){
			drawCurvePoints();
		} else {
			removeCurvePoints();
		}
	}
	if (controlPoints.size >= 4) {
		if (changeControlPolygon){
			// Do nothing
		} else {
			updateControlPolygon();
		}
		if (params["Control Polyhedron Visible"]){
			drawControlPolygon();
		} else {
			removeControlPolygon();
		}
	}	
	//updateInput();
	if (params.Surface == "Bezier Surface") {
		updateBezierSurface();
	} else if (params.Surface == "Cubic B-Spline Surface") {
		updateSplinePoints();
	} else if (params.Surface == "Doo Sabin Surface") {
		updateDooSabin();
	} else if (params.Surface == "Catmull-Clark Surface") {
		updateCatmullClark();
	} else if (params.Surface == "Loop Surface") {
		updateLoop();
		//update3DSurface();
	}

}

function updateSurface() {

	if (params.Surface == "Bezier Surface") {
		updateBezierSurface();
	} else if (params.Surface == "Cubic B-Spline Surface") {
		updateSplinePoints();
	} else if (params.Surface == "Doo Sabin Surface") {
		updateDooSabin();
	} else if (params.Surface == "Catmull-Clark Surface") {
		updateCatmullClark();
	} else if (params.Surface == "Loop Surface") {
		updateLoop();
	}
}

function updateExtrusion() {

	if (controlPoints.size <= 3){
		return;
	}

	

	while(objectArray.length > 0) {
		//splineHelperObjects.splice(controlPoints.nextIndex, 1);
		scene.remove(objectArray.pop());

	}
	for (var i = 1; i < controlPolygon.row; i++) {
		for (var j = 0; j < controlPolygon.column; j++) {
			splineHelperObjects.pop();
		}
	}
	removeControlPolygon();
	controlPolygonLine = [];
	objectArray = [];
	controlPolygon.positions = [];
	controlPolygon.row = 0;
	controlPolygon.column = 0;

	/* Add 1st row of control polygon points */
	var temp = copyAndModifyYOfArray(controlPoints.positions, 0, OFFSET);
	controlPolygon.positions.push(temp);
	controlPolygon.column = controlPoints.size;
	controlPolygon.row++;

	/* Add 2nd ~ (EXTRUSION_TIME + 1)th row of control polygon points */
	for (var i = 1; i <= EXTRUSION_TIME; i++) {

		controlPolygon.positions[i] = [];
		temp = copyAndModifyYOfArray(controlPoints.positions, i, OFFSET);
		for (var j = 0; j < temp.length; j++) {
			var pos = new THREE.Vector3(temp[j].x, temp[j].y, temp[j].z);
			var object = addSplineObject(pos);
			controlPolygon.positions[i].splice(j, 0, object.position);
			objectArray.push(object);
			splineHelperObjects.push(object);
		}
		//controlPolygon.positions.push(temp);
		controlPolygon.row++;
	}

	// for (var i = 1; i < controlPolygon.row; i++) {
	// 	for (var j = 0; j < controlPolygon.column; j++) {
	// 		var object = addSplineObject(controlPolygon.positions[i][j]);
	// 		controlPolygon.positions[i][j] = object.position;
	// 		objectArray.push(object);
	// 		splineHelperObjects.push(object);
	// 	}
	// }

	// /* Add Mesh */
	// for (var i = 1; i < controlPolygon.row; i++) {

	// 	var temp1 = controlPolygon.positions[i - 1];
	// 	var temp2 = controlPolygon.positions[i];
	// 	for (var j = 0; j < controlPolygon.column - 1; j++) {
	// 		var p1 = new THREE.Vector3(temp1[j].x, temp1[j].y, temp1[j].z);
	// 		var p2 = new THREE.Vector3(temp2[j].x, temp2[j].y, temp2[j].z);
	// 		var p3 = new THREE.Vector3(temp1[j + 1].x, temp1[j + 1].y, temp1[j + 1].z);
	// 		addFacet(p1, p2, p3);
	// 		var v1 = new THREE.Vector3(temp1[j + 1].x, temp1[j + 1].y, temp1[j + 1].z);
	// 		var v2 = new THREE.Vector3(temp2[j + 1].x, temp2[j + 1].y, temp2[j + 1].z);
	// 		var v3 = new THREE.Vector3(temp2[j].x, temp2[j].y, temp2[j].z);
	// 		addFacet(v1, v2, v3);
	// 	}
	// }

}

function drawControlPolygon() {
	
	// Check whether the condition has been satisfied
	if (controlPoints.size <= 3){
		alert("The size of control points is less than 4!");
		return;
	}

	// Clear data and canvas	
	//removeControlPolygon();
	
	while (controlPolygonLine.length > 0){
		scene.remove(controlPolygonLine.splice(0, 1).pop());
	}
	controlPolygonLine = [];

	// Draw control polygon
	if (controlPolygon.row * controlPolygon.column !== 0){
		// for (var i = 1; i < controlPolygon.row; i++) {
		// 	for (var j = 0; j < controlPolygon.column; j++) {
		// 		var object = addSplineObject(controlPolygon.positions[i][j]);
		// 		objectArray.push(object);
		// 		splineHelperObjects.push(object);
		// 	}
		// }

		for (var i = 0; i < controlPolygon.row; i++) {

			controlPolygonGeometry = new THREE.Geometry();
			controlPolygonGeometry.vertices = copyAndModifyYOfArray(
				controlPolygon.positions[i], 0, OFFSET);
			var new_controlPolygonLine = new THREE.Line(
				controlPolygonGeometry, controlPolygonMaterial);
			scene.add(new_controlPolygonLine);
			new_controlPolygonLine.castShadow = true;
			new_controlPolygonLine.receiveShadow = true;
			controlPolygonLine.push(new_controlPolygonLine);
			
		}

		for (var i = 0; i < controlPolygon.row - 1; i++) {
			for (var j = 0; j < controlPolygon.positions[i].length - 1; j++) {
				
				controlPolygonGeometry = new THREE.Geometry();

				var p1 = controlPolygon.positions[i][j];
				var p2 = controlPolygon.positions[i + 1][j + 1];
				controlPolygonGeometry.vertices.push(new THREE.Vector3(p1.x, p1.y, p1.z));
				controlPolygonGeometry.vertices.push(new THREE.Vector3(p2.x, p2.y, p2.z));

				var new_controlPolygonLine = new THREE.Line(
				controlPolygonGeometry, controlPolygonMaterial);
				scene.add(new_controlPolygonLine);
				new_controlPolygonLine.castShadow = true;
				new_controlPolygonLine.receiveShadow = true;
				controlPolygonLine.push(new_controlPolygonLine);
			}
		}

		for (var i = 0; i < controlPolygon.column; i++) {

			var column_position = [];
			for (var j = 0; j < controlPolygon.row; j++) {
				var p = new THREE.Vector3(controlPolygon.positions[j][i].x,
					controlPolygon.positions[j][i].y, controlPolygon.positions[j][i].z);
				column_position.push(p);
			}

			controlPolygonGeometry = new THREE.Geometry();
			controlPolygonGeometry.vertices = copyAndModifyYOfArray(
					column_position, 0, OFFSET);
			var new_controlPolygonLine = new THREE.Line(
				controlPolygonGeometry, controlPolygonMaterial);
			scene.add(new_controlPolygonLine);
			new_controlPolygonLine.castShadow = true;
			new_controlPolygonLine.receiveShadow = true;
			controlPolygonLine.push(new_controlPolygonLine);
		}

	}

}

function updateControlPolygon() {

	if (controlPoints.size <= 3){
		alert("The size of control points is less than 4!");
		return;
	}


	// controlPolygonLine = [];
	// objectArray = [];
	// controlPolygon.positions = [];
	// controlPolygon.row = 0;
	// controlPolygon.column = 0;
	//clearMesh();


	if (params["Control Polyhedron"] === "Extrusion") {
		updateExtrusion();
	}

	
}

function init() {

	container = document.getElementById( 'container' );

	/* Initialize scene and camera */
	scene = new THREE.Scene();
	scene.background = new THREE.Color( 0xf0f0f0 );
	camera = new THREE.PerspectiveCamera( 70, window.innerWidth * 3 / 4 / window.innerHeight, 1, 10000 );
	camera.position.set( 0, 1000, 0 );
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
	renderer.setSize( window.innerWidth * 3 / 4, window.innerHeight );
	renderer.shadowMap.enabled = true;
	container.appendChild( renderer.domElement );

	/* Add gui-dat menu */
	var gui = new dat.GUI({autoPlace: false, width: 512, height: 700});
	var customContainer = document.getElementById('gui');
	customContainer.appendChild(gui.domElement);

	gui.add(params, 'Curve Reconstruction', ['','Crust', 'NN-Crust']).onChange(function(){
		updateCurveReconstruction();
	});
	gui.add(params, 'Curve Reconstruction Visible').onChange(function(value){
		if (value) {
			updateLine();
		} else {
			removeLine();
		}
	});
	gui.add(params, 'Curve Shape', ['','Circle']).onChange(function(){
		updateSampling();
	});

	gui.add(params, 'Curve', ['','Bezier Curve', 'Cubic Uniform B-Spline', 'De Casteljau Subdivision', 'Quadric B-Spline']).onChange(function(){
		updateCurve();
	});

	gui.add(params, 'Curve Visible').onChange(function(value){
		if (value) {
			drawCurvePoints();
		} else {
			removeCurvePoints();
		}
	});
	// gui.add(params, 'Closed').onChange(function() {
	// 	var p = controlPoints.positions[0];
	// 	controlPoints.positions.push(new THREE.Vector3(p.x, p.y, p.z));
	// 	update3D();
	// });
	gui.add(params, 'Control Polyhedron', ['','Revolution', 'Extrusion', 'Swap']).onChange(function(){
		updateControlPolygon();
	});
	gui.add(params, 'Control Polyhedron Visible').onChange(function(value){
		if (value) {
			drawControlPolygon();
		} else {
			removeControlPolygon();
		}
	});
	gui.add(params, 'u', 0.05, 0.5).step(0.05).onChange(function(value){
		controlSurface.u = value;
	});
	gui.add(params, 'v', 0.05, 0.5).step(0.05).onChange(function(value){
		controlSurface.v = value;
	});
	gui.add(params, 'Surface', ['','Bezier Surface', 'Cubic B-Spline Surface',
	'Doo Sabin Surface', 'Catmull-Clark Surface', 'Loop Surface']).onChange(function(){
		updateSurface();
	});

	gui.add( params, 'Subdivision', 1, 6).step(1).onChange(function(value){
		subdivisions = value;
	});
	gui.add( params, 'Add Point' );
	gui.add( params, 'Remove Point');
	gui.add( params, 'Insert Point');
	gui.add( params, 'Duplicate Point');
	gui.add(params, 'Clear');
	gui.add(params, 'Generate Random Points', 10, 100).step(1).onChange(function(value){
		updateRandomPoints(value);
		update3D();
	});
	gui.add(params, 'Voronoi Diagram').onChange(function(value){
		computeVoronoi();
	});
	gui.add(params, 'Delaunay Diagram').onChange(function(value){
		computeDelaunay();
	});
	gui.add(params, 'Import');
	gui.add(params, 'Export');
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

		// if (controlPoints.size >= 4 && params["Control Polyhedron Visible"]) {
		// 		// Remove Control Polygon Lines

		// 		// Update Control Polygon Points

		// 		// Draw Control Polygon
		// }
		
		update3D(true);
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

	var material = new THREE.MeshLambertMaterial( { color: 0x283747 } );
	if (static) {
		material = new THREE.MeshLambertMaterial({ color: 0xC0392B });
	}
	var object = new THREE.Mesh( geometry, material );

	if ( position ) {
		object.position.copy( position );
	} else {
		object.position.x = Math.random() * 1000 - 500;
		object.position.y = 0;
		object.position.z = Math.random() * 800 - 400;
	}

	object.castShadow = true;
	object.receiveShadow = true;
	scene.add( object );

	return object;
}

function factorial(n) {
	var fact = 1;
	for (var i = 1; i <= n; i++) {
		fact *= i;
	}
	return fact;
}

function bernstein(n, i, u) {

	var result = 1.0;
	result *= (factorial(n) / factorial(n - i) / factorial(i));
	result *= Math.pow(u, i);
	result *= Math.pow(1.0 - u, n - i);
	return result;

}

function updateBezierCurve() {

	curvePoints.positions = [];
	curvePoints.size = 0;

	var seg = 0.01;
	var j = seg;

	curvePoints.positions.push(controlPoints.positions[0]);
	curvePoints.size++;

	while (j < 1) {

		var p = new THREE.Vector3(0.0, 0.0, 0.0);
		for (var i = 0; i < controlPoints.size; i++) {
			p.setX(p.x + controlPoints.positions[i].x *
				bernstein(controlPoints.size - 1, i, j));
			p.setY(p.y + controlPoints.positions[i].y *
				bernstein(controlPoints.size - 1, i, j));
			p.setZ(p.z + controlPoints.positions[i].z *
				bernstein(controlPoints.size - 1, i, j));
		}
		curvePoints.size++;
		curvePoints.positions.push(p);

		j += seg;
	}

	curvePoints.size++;
	curvePoints.positions.push(controlPoints.positions[controlPoints.size - 1]);

}

function updateCubicSplineCurve() {

	curvePoints.positions = [];
	curvePoints.size = 0;

	var n =  controlPoints.size - 1;
	for (var i = 1; i <= n - 2; i++) {
		var p1 = controlPoints.positions[i - 1];
		var p2 = controlPoints.positions[i];
		var p3 = controlPoints.positions[i + 1];
		var p4 = controlPoints.positions[i + 2];
		var pt = [p1, p2, p3, p4];

		var seg = 0.01;
		var u = seg;
		while (u < 1) {
			var x = 0, y = 0, z = 0;
			for (var j = 1; j <= 4; j++) {
				x += basis(j, u) * pt[j - 1].x;
				y += basis(j, u) * pt[j - 1].y;
				z += basis(j, u) * pt[j - 1].z;
			}

			var p = new THREE.Vector3(x, y, z);
			curvePoints.size++;
			curvePoints.positions.push(p);
			u += seg;

		}
	}
}

function updateCurve() {

	if (controlPoints.size <= 2) {
		alert("The size of points is less than 3!");
		return;
	}

	if (params.Curve === "Bezier Curve")
	{
		updateBezierCurve();
	} else if (params.Curve == "Cubic Uniform B-Spline") {
		updateCubicSplineCurve();
	} else if (params.Curve == "De Casteljau Subdivision") {
		updateDeCasteljau();
	} else if (params.Curve == "Quadric B-Spline") {
		updateQuadSpline();
	}
	

}

var SEGMENT = 0.3;
var poly1 = [], poly2 = [];
function updateDeCasteljau() {
	curvePoints.positions = copyAndModifyYOfArray(controlPoints.positions, 0, 0);
	curvePoints.size = controlPoints.size;
	poly1 = [], poly2 = [];
	for (var i = 0; i < subdivisions; i++) {
		var curvePoints_temp = [];
		var start = curvePoints.positions[0];
		curvePoints_temp.push(new THREE.Vector3(start.x, start.y, start.z));
		for (var j = 0; j < curvePoints.size - 1; j++) {
			var x, y, z;
			var p1 = curvePoints.positions[j];
			var p2 = curvePoints.positions[j + 1];
			x = SEGMENT * p1.x + (1 - SEGMENT) * p2.x;
			y = SEGMENT * p1.y + (1 - SEGMENT) * p2.y;
			z = SEGMENT * p1.z + (1 - SEGMENT) * p2.z;
			curvePoints_temp.push(new THREE.Vector3(x, y, z));

		}
		var end = curvePoints.positions[curvePoints.size - 1];
		curvePoints_temp.push(new THREE.Vector3(end.x, end.y, end.z));
		curvePoints.positions = copyAndModifyYOfArray(curvePoints_temp, 0, 0);
		curvePoints.size = curvePoints.positions.length;
	}
}

function oneSubdivide() {
	
}

function drawCurvePoints() {

	if (controlPoints.size <= 2) {
		alert("The size of points is less than 3!");
		return;
	}

	scene.remove(curve);	
	curveGeometry = new THREE.Geometry();
	for (var i = 0; i < curvePoints.size; i++){
		curveGeometry.vertices.push(curvePoints.positions[i]);
	}
	curve = new THREE.Line(curveGeometry, curveMaterial);
	curve.castShadow = true;
	curve.receiveShadow = true;
	scene.add(curve);

}
function updateLine() {

	removeDelaunayEdge();
	scene.remove(line);
	lineGeometry = new THREE.Geometry();
	for (var i = 0; i < controlPoints.size; i++){
		lineGeometry.vertices.push(controlPoints.positions[i]);
	}
	line = new THREE.Line(lineGeometry, lineMaterial);
	line.castShadow = true;
	line.receiveShadow = true;
	scene.add(line);
}

function removeLine() {
	scene.remove(line);
	lineGeometry = new THREE.Geometry();
	line = new THREE.Line(lineGeometry, lineMaterial);
	scene.add(line);
}

function addPoint() {

	controlPoints.size++;
	var object = addSplineObject();
	controlPoints.positions.splice(controlPoints.nextIndex, 0, object.position);
	splineHelperObjects.splice(controlPoints.nextIndex, 0, object);
	controlPoints.nextIndex++;
	update3D();
}


function duplicatePoint() {

	controlPoints.size++;
	var object = controlPoints.positions[controlPoints.nextIndex - 1];
	var new_object = addSplineObject(
		new THREE.Vector3(object.x, object.y, object.z));
	splineHelperObjects.splice(controlPoints.nextIndex, 0, new_object);
	controlPoints.positions.splice(controlPoints.nextIndex, 0, new_object.position);
	controlPoints.nextIndex++;
	update3D();
}

function removePoint() {

	if ( controlPoints.size <= 0 ) {
		return;
	}
	controlPoints.size --;
	controlPoints.positions.splice(controlPoints.nextIndex - 1, 1);
	controlPoints.nextIndex--;
	scene.remove(splineHelperObjects.splice(controlPoints.nextIndex, 1).pop());
	update3D();
}

function insertPoint() {

	if ( controlPoints.nextIndex <= 0 ) {
		return;
	}
	controlPoints.size++;
	var object = addSplineObject();
	controlPoints.positions.splice(controlPoints.nextIndex - 1, 0, object.position);
	splineHelperObjects.splice(controlPoints.nextIndex - 1, 0, object);
	controlPoints.nextIndex++;
	update3D();
}

/* Clear data in control polygen */
function removeControlPolygon() {

	/* Clear canvas */
	while (controlPolygonLine.length > 0) {
		scene.remove(controlPolygonLine.pop());
	}
	controlPolygonLine = [];
	
	
	
}
function removeCurvePoints() {
	scene.remove(curve);
	curveGeometry = new THREE.Geometry();
	curve = new THREE.Line(curveGeometry, curveMaterial);
	scene.add(curve);
}

function clear() {

	
	while(objectArray.length > 0) {
		//splineHelperObjects.splice(controlPoints.nextIndex, 1);
		scene.remove(objectArray.pop());

	}
	for (var i = 1; i < controlPolygon.row; i++) {
		for (var j = 0; j < controlPolygon.column; j++) {
			splineHelperObjects.pop();
		}
	}
	removeControlPolygon();
	controlPolygonLine = [];
	objectArray = [];
	controlPolygon.positions = [];
	controlPolygon.row = 0;
	controlPolygon.column = 0;

	//clearMesh();
	removeLine();
	while(controlPoints.size > 0){
		controlPoints.size --;
		controlPoints.positions.splice(controlPoints.nextIndex - 1, 1);
		controlPoints.nextIndex--;
		scene.remove(splineHelperObjects.splice(controlPoints.nextIndex, 1).pop());
	}

	removeCurvePoints();
	curvePoints.size = 0;
	curvePoints.positions = [];

	

	currentVerticeIndex = -1;
	currentEdgeIndex = -1;
	vertices = [];
	faces = [];
	numberOfVertices = 0;
	numberOfFaces = 0;

	//splineHelperObjects = [];

	//addPoint();
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

	var body = document.getElementsByTagName("body")[0];
	var div;

	/* Add gui control */
	div = document.createElement("div");
	div.setAttribute("id", 'container');
	body.appendChild(div);
	var gui = document.createElement("div");
	gui.setAttribute("id", 'gui');
	div.appendChild(gui);


	init();
	animate();

}

window.onload();
