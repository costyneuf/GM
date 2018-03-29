
window.onload = function(){
	Initialize.addElements();
	/* Add canvas */
	var div = document.createElement("div");
	var body = document.getElementsByTagName("body")[0];
	body.appendChild(div);

	var scene = new THREE.Scene();
	var camera = new THREE.PerspectiveCamera(75, window.innerWidth/window.innerHeight, 0.1, 1000);
	var renderer = new THREE.WebGLRenderer();
	renderer.setSize(window.innerWidth - 100, window.innerHeight - 100);
	div.appendChild(renderer.domElement);
	var geometry = new THREE.CubeGeometry(1,1,1);
	var material = new THREE.MeshBasicMaterial({color: 0x00ff00});
	var cube = new THREE.Mesh(geometry, material); scene.add(cube);
	camera.position.z = 5;

	function render() {
	    requestAnimationFrame(render);
	    cube.rotation.x += 0.1;
	    cube.rotation.y += 0.1;
	    renderer.render(scene, camera);
	}
	render();
}

window.onload();


