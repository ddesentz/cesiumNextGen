function cPlane(pos, rot, num) {
    count++;
    return viewer.entities.add({
        id : num,
        position: initPos,
        model: {
            uri: 'cesium/Apps/SampleData/models/CesiumAir/Cesium_Air.gltf',
            minimumPixelSize: 16,
            scale: 1,
            orientation: initRot
        }
    });
}

function updatePath(long,lat,height,id,coords){
var oldPath = coords[id];
oldPath.push(long);
oldPath.push(lat);
oldPath.push(height);
var newPath = oldPath;
return newPath;
}
function createPath(color){
return viewer.entities.add({
    polyline : {
        positions : [],
        width : 5,
        material : color
    }
});
}

function updateView(long,lat,scope){
var west = long - scope;
var south = lat - scope;
var east = long + scope;
var north = lat + scope;
var newView = Cesium.Rectangle.fromDegrees(west, south, east, north);
return newView;
}

function createViews(color){
return viewer.entities.add({
        rectangle : {
            coordinates : Cesium.Rectangle.fromDegrees(),
            material : color.withAlpha(0.35),
            outline : true,
            height : 2,
            outlineColor : color
        }
    });
}