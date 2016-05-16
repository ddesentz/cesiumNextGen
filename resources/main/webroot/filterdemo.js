var viewer = new Cesium.Viewer('cesiumContainer');
var scene = viewer.scene;
var inspector = new Cesium.CesiumInspectorViewModel(scene);
inspector.destroy();
var count = 0;
var initPos = Cesium.Cartesian3.fromRadians(-1.4669223582307533, 0.6949654640951038, 2000);
var initRot = Cesium.Transforms.eastNorthUpToFixedFrame(initPos);

var estPos = cPlane(initPos, initRot, count);
var truePos = cPlane(initPos, initRot, count);
var nextPos = cPlane(initPos, initRot, count);
var planes = [estPos,truePos,nextPos];

var pathCoords = [];
var colorArray = [];
var aircraftPaths = [];
var views = [];
for (var i = 0; i < planes.length; i++){
    var tmpColor = new Cesium.Color(Math.random(),Math.random(),Math.random(),1);
    colorArray.push(tmpColor);
    var tmpPath = createPath(colorArray[i])
    aircraftPaths.push(tmpPath)
    var tmpCoord = [-1.4669223582307533, 0.6949654640951038, 2000]
    pathCoords.push(tmpCoord)
    var tmpView = createViews(colorArray[i]);
    views.push(tmpView)
}


var bus = connectEventBus('http://localhost:8999/visualization');
var bus_handlers = [function () {connectPlaneToBus(bus, truePos, "pose-ownship-truth");},
                    function () {connectPlaneToBus(bus, estPos, "pose-ownship");},
                    function () {connectPlaneToBus(bus, nextPos, "next");}
                    ];

viewer.trackedEntity = planes[0];

function updatePosition(posLLH, rot, vehicle) {
    vehicle.position = Cesium.Cartesian3.fromRadians(posLLH.longitude, posLLH.latitude, posLLH.height);
    vehicle.orientation = rot;
    var newCoords = updatePath(posLLH.longitude, posLLH.latitude, posLLH.height,vehicle.id,pathCoords);
    aircraftPaths[vehicle.id].polyline.positions = Cesium.Cartesian3.fromRadiansArrayHeights(newCoords);
    var view = updateView(posLLH.longitude, posLLH.latitude,.0001);
    var airView = views[vehicle.id];
    airView.rectangle.coordinates = view;
}

function connectEventBus(url) {
    var eb = new EventBus(url);
    eb.onopen = function () {
        bus_handlers.forEach(function(handler){handler()})
    };
    return eb;
}
function connectPlaneToBus(eb,plane, msgname) {
    eb.registerHandler(msgname, function (error, message) {
        try {
            var origin = new Cesium.Cartesian3.fromRadians(message.body.pos[1], message.body.pos[0], message.body.pos[2]);
            var newPos = new Cesium.Cartographic(message.body.pos[1], message.body.pos[0], message.body.pos[2]);
            var newRot = new Cesium.Transforms.headingPitchRollQuaternion(origin,message.body.rot[0],message.body.rot[1],message.body.rot[2]);
            updatePosition(newPos, newRot, plane)
        } catch (e) {
            console.error("Received a pose from server that didnt have pos/vel/rot correctly formatted.");
            throw e
        }
    });
}
function zoomTo(num){
    viewer.trackedEntity = planes[num];
}



