var viewer = new Cesium.Viewer('cesiumContainer');
var scene = viewer.scene;

var initPos = Cesium.Cartesian3.fromRadians(-1.4669223582307533, 0.6949654640951038, 2000);
var initRot = Cesium.Transforms.eastNorthUpToFixedFrame(initPos);


var estPos = createPlaneAt(initPos, initRot);
var truePos = createPlaneAt(initPos, initRot);

var bus = connectEventBus('http://localhost:8999/visualization');
var bus_handlers = [function () {connectPlaneToBus(bus, truePos, "pose-ownship-truth");},
                    function () {connectPlaneToBus(bus, estPos, "pose-ownship");}];

viewer.extend(Cesium.viewerCesiumInspectorMixin);
viewer.trackedEntity = estPos;


function createPlaneAt(pos, rot) {
    return viewer.entities.add({
        position: initPos,
        model: {
            uri: 'cesium/Apps/SampleData/models/CesiumAir/Cesium_Air.gltf',
            minimumPixelSize: 16,
            scale: 1,
            orientation: initRot
        }
    });
}

function updatePosition(posLLH, vehicle) {
    vehicle.position = Cesium.Cartesian3.fromRadians(posLLH.longitude, posLLH.latitude, posLLH.height);
}

function connectEventBus(url) {
    var eb = new EventBus(url);
    eb.onopen = function () {
        bus_handlers.forEach(function(handler){handler()})
    };

    return eb;
}
function connectPlaneToBus(eb, plane, msgname) {
    eb.registerHandler(msgname, function (error, message) {
        try {
            var newPos = new Cesium.Cartographic(message.body.pos[1], message.body.pos[0], message.body.pos[2]);
            updatePosition(newPos, plane)
        } catch (e) {
            console.error("Received a pose from server that didnt have pos/vel/rot correctly formatted.");
            throw e
        }
    });
}
