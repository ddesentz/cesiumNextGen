var INIT_POS = Cesium.Cartesian3.fromRadians(-1.4669223582307533, 0.6949654640951038, 2000);
var INIT_ROT = new Cesium.ConstantProperty(Cesium.Transforms.headingPitchRollQuaternion(INIT_POS, 0, 1.5, 0));
var EBUS_URL = window.location.origin + '/services/visualize';
var AIR_MODEL = '../vendor/bower_components/cesium/Apps/SampleData/models/CesiumAir/Cesium_Air.gltf';
var MAX_PATH_LEN = 3600;

var viewer = new Cesium.Viewer('cesiumContainer');
var scene = viewer.scene;
var vehicles = [];
var bus = startEventBus(EBUS_URL);
viewer.extend(Cesium.viewerCesiumInspectorMixin);

var count = 0;
var colorArray = [];
colorArray.push(Cesium.Color.RED);
colorArray.push(Cesium.Color.WHITE);
colorArray.push(Cesium.Color.BLUE);
colorArray.push(Cesium.Color.YELLOW);
var pathCoords = [];
var aircraftPaths = [];

function startEventBus(url) {
    var eb = new EventBus(url);
    eb.onopen = function () {
        console.log("Requesting server send list of topics.");
        eb.send("cesium_topics", "req", function (err, msgs) {
            console.log("Got topic list from server.");
            msgs.body.forEach(function (topic) {
                console.log("Connecting to " + topic);
                count++;
                vehicles.push(createPlaneAt(INIT_POS, INIT_ROT));
                //If more than 4 planes are created generate random colors for their paths
                if (count > 4) {
                    colorArray.push(randomColor());
                }
                aircraftPaths.push(createPath(colorArray[count - 1]));
                pathCoords.push([]);
                vehicles[vehicles.length - 1].position = INIT_POS;
                connectPlaneToBus(eb, vehicles[vehicles.length - 1], topic);
                viewer.trackedEntity = vehicles[vehicles.length - 1];
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
            var newRot;
            if (!message.body.rot) {
                plane.model.uri = undefined;
                plane.box = {
                    dimensions: new Cesium.Cartesian3(5.0, 5.0, 5.0),
                    material: colorArray[count - 1]
                };
            }
            else
                newRot = message.body.rot;
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
        id: count,
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
    aircraftPaths[vehicle.id - 1].polyline.positions = Cesium.Cartesian3.fromRadiansArrayHeights(updatePath(posLLH.longitude, posLLH.latitude, posLLH.height, vehicle.id - 1));
}

function updateOrientation(rotMat, vehicle) {
    vehicle.orientation = new Cesium.ConstantProperty(Cesium.Transforms.headingPitchRollQuaternion(INIT_POS, rotMat[2], rotMat[1], rotMat[0]));
}

function updatePath(long, lat, height, id) {
    var path = pathCoords[id];
    path.push(long);
    path.push(lat);
    path.push(height);
    //Set Max path length for 10mins @ 2Hz
    if (path.length == (MAX_PATH_LEN)) {
        var removed = path.splice(0, 3);
    }
    return path;
}

function createPath(color) {
    return viewer.entities.add({
        name: "Plane " + count + " Path",
        polyline: {
            positions: [],
            width: 5,
            material: color
        }
    });
}

function randomColor() {
    return new Cesium.Color(Math.random(), Math.random(), Math.random(), 1);
}
