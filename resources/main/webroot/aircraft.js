function cPlane(pos, rot, num) {
    count++;
    return viewer.entities.add({
        id : num,
        position: initPos,
        model: {
            uri: 'vendor/bower_components/cesium/Apps/SampleData/models/CesiumAir/Cesium_Air.gltf',
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
var west = new Cesium.Cartographic(long - scope,lat);
var south = new Cesium.Cartographic(long,lat - scope);
var east = new Cesium.Cartographic(long + scope,lat);
var north = new Cesium.Cartographic(long,lat + scope);
var arr = [west,south,east,north];
var newView = Cesium.Rectangle.fromCartographicArray(arr);
return newView;
}

function createViews(color){
return viewer.entities.add({
        rectangle : {
            material : color.withAlpha(0.35),
            outline : true,
            height : 2,
            outlineColor : color
        }
    });
}
