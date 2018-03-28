function addElements() {
	
	var body = document.getElementsByTagName("body")[0];
	var div;

	/* Add buttons */
	// Bezier Curve
	div = document.createElement("div")
	var button_Bezier = document.createElement("button");
	button_Bezier.innerHTML = "Bezier Curve";
	body.appendChild(div);
	div.appendChild(button_Bezier)
	button_Bezier.addEventListener ("click", function() {
	  alert("did something");
	});

	// Extrusion
	div = document.createElement("div")
	var button_Extrusion = document.createElement("button");
	button_Extrusion.innerHTML = "Extrusion";	
	body.appendChild(div);
	div.appendChild(button_Extrusion);
	button_Extrusion.addEventListener ("click", function() {
	  alert("Extrusion");
	});

	/* Add canvas */
	div = document.createElement("div")
	var canvas = document.createElement('canvas');
	canvas.id = 'canvas2d';
	body.appendChild(div);
	div.appendChild(canvas);
}
