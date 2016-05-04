var viewer = new Cesium.Viewer('cesiumContainer');
var scene = viewer.scene;

var initPos = Cesium.Cartesian3.fromDegrees(-75.62898254394531, 40.02804946899414, 1700);
var initRot = Cesium.Transforms.eastNorthUpToFixedFrame(initPos);

var plane = viewer.entities.add({
    position: initPos,
    model: {
        uri : 'cesium/Apps/SampleData/models/CesiumAir/Cesium_Air.gltf',
        minimumPixelSize : 16,
        scale: 1,
        orientation: initRot
    }
});

viewer.extend(Cesium.viewerCesiumInspectorMixin);
viewer.trackedEntity = plane;

var posLLH = Cesium.Cartographic.fromDegrees(-75.62898254394531, 40.02804946899414, 2000)
function receiveCoords() {
    posLLH.longitude = posLLH.longitude +.00001;
    plane.position = Cesium.Cartesian3.fromRadians(posLLH.longitude, posLLH.latitude, posLLH.height);
    setTimeout(receiveCoords, 1000)
}
receiveCoords()

var eb = new EventBus('http://localhost:8080/eventbus');

eb.onopen = function() {

  // set a handler to receive a message
  eb.registerHandler('some-address', function(error, message) {
    console.log('received a message: ' + JSON.stringify(message));
  });

  // send a message
  eb.send('some-address', {name: 'tim', age: 587});

}
