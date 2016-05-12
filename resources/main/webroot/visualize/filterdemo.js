var INIT_POS = Cesium.Cartesian3.fromRadians(-1.4669223582307533, 0.6949654640951038, 2000);
var INIT_ROT = new Cesium.ConstantProperty(Cesium.Transforms.headingPitchRollQuaternion(INIT_POS, 0, 1.5, 0));
var AIR_MODEL = '../cesium/Apps/SampleData/models/CesiumAir/Cesium_Air.gltf';
var EBUS_URL = 'http://localhost:8999/services/visualize';

var viewer = new Cesium.Viewer('cesiumContainer');
var scene = viewer.scene;
var vehicles = [];
var bus = startEventBus(EBUS_URL);
viewer.extend(Cesium.viewerCesiumInspectorMixin);


function startEventBus(url) {
    var eb = new EventBus(url);
    eb.onopen = function () {
        console.log("Requesting server send list of topics.");
        eb.send("cesium_topics", "req", function(err, msgs) {
            console.log("Got topic list from server.");
            msgs.body.forEach(function(topic) {
                console.log("Connecting to " + topic);
                vehicles.push(createPlaneAt(INIT_POS, INIT_ROT));
                vehicles[vehicles.length-1].position = INIT_POS;
                connectPlaneToBus(eb, vehicles[vehicles.length-1], topic);
                viewer.trackedEntity = vehicles[vehicles.length-1];
            })
        });
    };
    return eb;
}

function connectPlaneToBus(eb, plane, msgname) {
    eb.registerHandler(msgname, function (error, message) {
        try {
            // Invert lon/lat to lat/lon
            var newPos = new Cesium.Cartographic(message.body.pos[1], message.body.pos[0], message.body.pos[2]);
            var newRot = NaN;
            try {
                newRot = message.body.rot;
            } catch(e) {
                newRot = [0,0,0];
            }
            updatePosition(newPos, plane);
            updateOrientation(newRot, plane);
        } catch (e) {
            console.error("Received a pose from server that didnt have pos/vel/rot correctly formatted.");
            throw e;
        }
    });
}

function createPlaneAt(pos, rot) {

    return viewer.entities.add({
        position: INIT_POS,
        orientation: INIT_ROT,
        model: {
            uri: AIR_MODEL,
            minimumPixelSize: 16,
            scale: 1
        }
    });
}

function updatePosition(posLLH, vehicle) {
    vehicle.position = Cesium.Cartesian3.fromRadians(posLLH.longitude, posLLH.latitude, posLLH.height);
}

function updateOrientation(rotMat, vehicle) {
    vehicle.orientation = new Cesium.ConstantProperty(Cesium.Transforms.headingPitchRollQuaternion(INIT_POS, rotMat[2], rotMat[1], rotMat[0]));
}