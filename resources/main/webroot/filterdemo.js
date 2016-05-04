var viewer = new Cesium.Viewer('cesiumContainer');
var scene = viewer.scene;

var initPos = Cesium.Cartesian3.fromDegrees(-75.62898254394531, 40.02804946899414, 1700);
var initRot = Cesium.Transforms.eastNorthUpToFixedFrame(initPos);

var ownShip = createPlaneAt(initPos, initRot);

viewer.extend(Cesium.viewerCesiumInspectorMixin);
viewer.trackedEntity = ownShip;

connectEventBus('http://localhost:8999/visualization', ownShip);

function createPlaneAt(pos, rot) {
    return viewer.entities.add({
                   position: initPos,
                   model: {
                       uri : 'cesium/Apps/SampleData/models/CesiumAir/Cesium_Air.gltf',
                       minimumPixelSize : 16,
                       scale: 1,
                       orientation: initRot
                   }
               });
}

function updatePosition(posLLH, vehicle) {
    vehicle.position = Cesium.Cartesian3.fromRadians(posLLH.longitude, posLLH.latitude, posLLH.height);
}

function connectEventBus(url, vehicle) {
    var eb = new EventBus(url);

    eb.onopen = function() {
        // set a handler to receive a message
        eb.registerHandler('pose-ownship', function(error, message) {
          try {
              var newPos = new Cesium.Cartographic(message.body.pos[1], message.body.pos[0], message.body.pos[2]);
              updatePosition(newPos, vehicle)
          } catch (e) {
              console.error("Received a pose from server that didnt have pos/vel/rot correctly formatted.");
              throw e
          }
        });
    };
}
