class Initialize {
	static addElements() {
	
		var body = document.getElementsByTagName("body")[0];
		var div, label, radio;
	
		/* Add Curve and Ctrl Poly buttons */
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
	
		/* Add Surface Buttons */
		div = document.createElement("div");
		body.appendChild(div);

		var button_Bezier_Surface = document.createElement("button");
		button_Bezier_Surface.innerHTML = "Bezier Surface";
		div.appendChild(button_Bezier_Surface);
		button_Bezier_Surface.addEventListener ("click", function() {
			alert("did something");
		});

		var button_BSpline_Surface = document.createElement("button");
		button_BSpline_Surface.innerHTML = "Cubic B-spline Surface";
		div.appendChild(button_BSpline_Surface);
		button_BSpline_Surface.addEventListener ("click", function() {
			alert("did something");
		});

		var button_DooSabin_Surface = document.createElement("button");
		button_DooSabin_Surface.innerHTML = "Doo-Sabin Surface";
		div.appendChild(button_DooSabin_Surface);
		button_DooSabin_Surface.addEventListener ("click", function() {
			alert("did something");
		});

		var button_CatmullClark_Surface = document.createElement("button");
		button_CatmullClark_Surface.innerHTML = "Catmull-Clark Surface";
		div.appendChild(button_CatmullClark_Surface);
		button_CatmullClark_Surface.addEventListener ("click", function() {
			alert("did something");
		});

		var button_Loop_Surface = document.createElement("button");
		button_Loop_Surface.innerHTML = "Loop Surface";
		div.appendChild(button_Loop_Surface);
		button_Loop_Surface.addEventListener ("click", function() {
			alert("did something");
		});

		/* Add Radio Buttons */
		div = document.createElement("div");
		body.appendChild(div);

		// Add a point
		radio = document.createElement("input");
		radio.setAttribute("class", "unchecked");
		radio.setAttribute("type", "radio");
		radio.setAttribute("id", "add");	
		radio.addEventListener("click",function(){
			radio.setAttribute("class", "checked");
		})	
		div.appendChild(radio);	
		label = document.createElement("label");
		label.setAttribute("for", "add");
		label.innerHTML = "Add a point";
		div.appendChild(label);

		// Edit a point
		radio = document.createElement("input");
		radio.setAttribute("class", "unchecked");
		radio.setAttribute("type", "radio");
		radio.setAttribute("id", "edit");	
		radio.addEventListener("click",function(){
			radio.setAttribute("class", "checked");
		})	
		div.appendChild(radio);		
		label = document.createElement("label");
		label.setAttribute("for", "edit");
		label.innerHTML = "Edit a point";
		div.appendChild(label);

		// Insert a point
		radio = document.createElement("input");
		radio.setAttribute("class", "unchecked");
		radio.setAttribute("type", "radio");
		radio.setAttribute("id", "insert");	
		radio.addEventListener("click",function(){
			radio.setAttribute("class", "checked");
		})	
		div.appendChild(radio);		
		label = document.createElement("label");
		label.setAttribute("for", "Insert");
		label.innerHTML = "Insert a point";
		div.appendChild(label);

	}
	
}
