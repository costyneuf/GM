
String.prototype.format = function () {

	var str = this;
	for ( var i = 0; i < arguments.length; i ++ ) {
		str = str.replace( '{' + i + '}', arguments[ i ] );
	}
	return str;
};


var objectArray = [];
var geometry = new THREE.BoxGeometry( 20, 20, 20 );

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
const EXTRUSION_TIME = 8;
const OFFSET = 150;
var controlPolygon = {
	row: 0,
	column: 0,
	positions: []
};

var controlPolygonColor = new THREE.Color('green');
var controlPolygonMaterial = new THREE.LineBasicMaterial({
	color: controlPolygonColor, opacity: 0.5, linewidth: 1 });
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
var edge_faces;

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
	mesh.castShadow = true;
	mesh.receiveShadow = true;
	scene.add(mesh);
}
function addFacet4(v1, v2, v3, v4) {
	meshGeometry = new THREE.Geometry();
	meshGeometry.vertices.push(v1);
	meshGeometry.vertices.push(v2);
	meshGeometry.vertices.push(v3);
	meshGeometry.vertices.push(v4);
	meshGeometry.faces.push(new THREE.Face4(0, 1 ,2, 3));


	mesh = new THREE.Mesh(meshGeometry, meshMaterial);
	meshArray.push(mesh);
	mesh.castShadow = true;
	mesh.receiveShadow = true;
	scene.add(mesh);
}
function clearMesh() {
	while (meshArray.length > 0){
		scene.remove(meshArray.pop());
	}
	meshArray = [];
}


var currentVerticeIndex = -1;

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

	for (var j = 3; j < controlSurface.n; j += 3) {
		var temp1 = [];
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
						temp3.push(addVertices(temp1[k]));
						temp3.push(addVertices(temp1[k + 1]));
						temp3.push(addVertices(temp2[k + 1]));
						temp3.push(addVertices(temp2[k]));
						faces.push(temp3);
						//addFacet4(vertices[temp3[0]], vertices[temp3[1]], vertices[temp3[2]], vertices[temp3[3]]);
					}
				}
				temp1 = copyAndModifyYOfArray(temp2, 0, 0);
				u += controlSurface.u;
			}
		}
	}

	numberOfVertices = vertices.length;
	numberOfFaces = faces.length;
}

function addVertices(v) {
	vertices.forEach(function(element){
		if (v.x === element.x && v.y == element.y && v.z == element.z) {
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
	for (var j = 1; j <= controlSurface.n - 2; j++) {
		var temp1 = [];
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
						temp3.push(addVertices(temp1[k]));
						temp3.push(addVertices(temp1[k + 1]));
						temp3.push(addVertices(temp2[k + 1]));
						temp3.push(addVertices(temp2[k]));
						faces.push(temp3);
					}
				}

				temp1 = copyAndModifyYOfArray(temp2, 0, 0);


				u += controlSurface.u;
			}
		}
	}

	numberOfVertices = vertices.length;
	numberOfFaces = faces.length;

}

var params = {

	/* Points */
	'Add Point': addPoint,
	'Remove Point': removePoint,
	'Insert Point': insertPoint,
	'Duplicate Point': duplicatePoint,
	'Clear': clear,

	/* value */
	'Subdivision': 4,

	/* Curve */
	'Curve': 'Bezier Curve',
	'Curve Visible': true,
	//'Closed': false,

	/* Control Points Generator */
	'Control Polyhedron': 'Extrusion',
	'Control Polyhedron Visible': false,

	/* Surface Type */
	'Surface': '',

	/* Import and Export */
	'Import': importOFF,
	'Export': exportOFF

};

var readBuffer;


function updateInput() {

	if (typeof(readBuffer) != typeof("string")) {
		return;
	}
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
		vertices.push(new THREE.Vector3(x, y, z));
	}

	/* Store Faces */
	faces = [];
	i++;

	clearMesh();

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

function copyAndModifyYOfArray(A, row_index, offset) {

	var B = [];
	for (var i = 0; i < A.length; i++) {
		var p = new THREE.Vector3(A[i].x, A[i].y, A[i].z);
		p.setY(p.y - row_index * 70);
		B.push(p);
	}

	return B;
}

function update3D() {
	updateLine();
	if (params["Curve Visible"]){
		updateCurve();
	} else {
		clearCurvePoints();
	}
	if (params["Control Polyhedron Visible"]){
		updateControlPolygon();
	} else {
		clearControlPolygon();
	}
	//updateInput();
	if (params.Surface == "Bezier Surface") {
		updateBezierSurface();
	} else if (params.Surface == "Cubic B-Spline Surface") {
		updateSplinePoints();
	}

}

function updateExtrusion() {

	if (controlPoints.size <= 3){
		return;
	}


	/* Add 1st row of control polygon points */
	var temp = copyAndModifyYOfArray(controlPoints.positions, 0, OFFSET);
	controlPolygon.positions.push(temp);
	controlPolygon.column = controlPoints.size;
	controlPolygon.row++;

	/* Add 2nd ~ (EXTRUSION_TIME + 1)th row of control polygon points */
	for (var i = 1; i <= EXTRUSION_TIME; i++) {
		temp = copyAndModifyYOfArray(controlPoints.positions, i, OFFSET);
		controlPolygon.positions.push(temp);
		controlPolygon.row++;
	}

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

function updateControlPolygon() {

	if (controlPoints.size <= 3){
		return;
	}


	if (controlPolygon.row * controlPolygon.column !== 0) {
		clearControlPolygon();
		clearMesh();
	}

	if (params["Control Polyhedron"] === "Extrusion") {
		updateExtrusion();
	}

	if (controlPolygon.row * controlPolygon.column !== 0){
		for (var i = 1; i < controlPolygon.row; i++) {
			for (var j = 0; j < controlPolygon.column; j++) {
				var object = addSplineObject(controlPolygon.positions[i][j]);
				objectArray.push(object);
				//splineHelperObjects.push(object);
			}
		}

		for (var i = 0; i < controlPolygon.row; i++) {

			controlPolygonGeometry = new THREE.Geometry();
			controlPolygonGeometry.vertices = copyAndModifyYOfArray(
				controlPolygon.positions[i], 0, OFFSET);
			var new_controlPolygonLine = new THREE.Line(
				controlPolygonGeometry, controlPolygonMaterial);
			scene.add(new_controlPolygonLine);
			controlPolygonLine.push(new_controlPolygonLine);
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
			controlPolygonLine.push(new_controlPolygonLine);
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
	var gui = new dat.GUI({autoPlace: false, width: 512, height: 700});
	var customContainer = document.getElementById('gui');
	customContainer.appendChild(gui.domElement);

	gui.add(params, 'Curve', ['Bezier Curve']).onChange(function(){
		update3D();
	});

	gui.add(params, 'Curve Visible').onChange(function(){
		update3D();
	});
	// gui.add(params, 'Closed').onChange(function() {
	// 	var p = controlPoints.positions[0];
	// 	controlPoints.positions.push(new THREE.Vector3(p.x, p.y, p.z));
	// 	update3D();
	// });
	gui.add(params, 'Control Polyhedron', ['Extrusion']).onChange(function(){
		update3D();
	});
	gui.add(params, 'Control Polyhedron Visible').onChange(function(){
		update3D();
	});
	gui.add(params, 'Surface', ['Extrusion', 'Bezier Surface', 'Cubic B-Spline Surface',
	'Doo Sabin Surface', 'Catmull-Clark Surface', 'Loop Surface']).onChange(function(){
		update3D();
	});

	gui.add( params, 'Subdivision', 2, 20).step(1).onChange(function(value){
		subdivisions = value;
		update3D();
	});
	gui.add( params, 'Add Point' );
	gui.add( params, 'Remove Point');
	gui.add( params, 'Insert Point');
	gui.add( params, 'Duplicate Point');
	gui.add(params, 'Clear');
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
		update3D();
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

function updateCurve() {

	if (controlPoints.size > 2) {

		scene.remove(curve);

		if (params.Curve === "Bezier Curve")
		{
			updateBezierCurve();
		}

		curveGeometry = new THREE.Geometry();
		for (var i = 0; i < curvePoints.size; i++){
			curveGeometry.vertices.push(curvePoints.positions[i]);
		}
		curve = new THREE.Line(curveGeometry, curveMaterial);
		curve.castShadow = true;
		curve.receiveShadow = true;
		scene.add(curve);
	}

}


function updateLine() {
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
	controlPoints.positions.push(new_object.position);
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
function clearControlPolygon() {

	/* Clear canvas */
	for (var i = 0; controlPolygonLine !== [] &&
		i < controlPolygon.row + controlPolygon.column; i++) {
		scene.remove(controlPolygonLine[i]);
	}


	while(objectArray.length > 0) {
		//splineHelperObjects.splice(controlPoints.nextIndex, 1);
		scene.remove(objectArray.pop());
	}

	controlPolygonLine = [];
	objectArray = [];
	controlPolygon.positions = [];
	controlPolygon.row = 0;
	controlPolygon.column = 0;
}
function clearCurvePoints() {
	scene.remove(curve);
	curveGeometry = new THREE.Geometry();
	curve = new THREE.Line(curveGeometry, curveMaterial);
	curvePoints.size = 0;
	curvePoints.positions = [];
	scene.add(curve);
}
function clear() {

	clearControlPolygon();

	clearMesh();

	while(controlPoints.size > 0){
		removePoint();
	}


	clearCurvePoints();

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
