/* This class converts the transformed pixel coordinates of the svg map in the wall to corresponding coordinates in the overlayed canvas for each monitor location */
"use strict"

const WIDTH = 11520;
const HEIGHT = 6480;

var transform =  function(X,Y){
	this.startingX = X;
	this.startingY = Y;
};

transform.prototype.convertToActualXY = function(x,y) {
	var transformedXY=[];

	transformedXY.push(x-this.startingX);
	transformedXY.push(y-this.startingY);

	return transformedXY;
};


transform.findGreaterDifferenceLatOrLon= function(a,b){
	  //getting lat and lon values for both wind points
	  var lat1 = Number(a["Wind_Lat"]),
  	 	  lon1 = Number(a["Wind_Lon"]),
          lat2 = Number(b["Wind_Lat"]),
          lon2 = Number(b["Wind_Lon"]);

      //getting both lon diff and lat diff between two wind instances
      var lat_diff = lat1>=lat2 ? lat1-lat2: lat2-lat1,
          lon_diff = lon1>=lon2 ? lon1-lon2 : lon2-lon1;
       
        return lat_diff>=lon_diff ? lat_diff : lon_diff;

}

//this function draws arrow heads on the top of the stream lines
// function createArrows(offScreenContext,arrowPath) {

//   //for drawing arrow heads above lines
//   for (var i = 0; i < totalPathData.length; i++) {
//     var flow = totalPathData[i];
//     //drawing only those flows which are active
//     if (flow.active == 1) {

//       for (var j = 0; j < flow.value.length; j++) {
    
//           var obj = flow.value[j],
//           leftX = Math.ceil(obj.x1 + Math.cos(obj.beforeAngle) * obj.h),
//           leftY = Math.ceil(obj.y1 + Math.sin(obj.beforeAngle) * obj.h),
//           rightX = Math.ceil(obj.x1 + Math.cos(obj.afterAngle) * obj.h),
//           rightY = Math.ceil(obj.y1 + Math.sin(obj.afterAngle) * obj.h);


//         arrowPath.moveTo(obj.x1, obj.y1);
//         arrowPath.lineTo(leftX, leftY);
//         arrowPath.lineTo(rightX, rightY);
//         arrowPath.lineTo(obj.x1, obj.y1);

//         // offScreenContext.moveTo(obj.x1, obj.y1);
//         // offScreenContext.lineTo(leftX, leftY);
//         // offScreenContext.lineTo(rightX, rightY);
//         // offScreenContext.lineTo(obj.x1, obj.y1);
//       }
//     }
//   }
//   canvas.strokeStyle = "#F0FFFF";
//   canvas.stroke(arrowPath)
//   canvas.fillStyle = "#F0FFFF";
//   canvas.fill(arrowPath);

//   offScreenContext.strokeStyle = "#F0FFFF";
//   offScreenContext.stroke(arrowPath)
//   offScreenContext.fillStyle = "#F0FFFF";
//   offScreenContext.fill(arrowPath);

// }


//this function takes care of hover over event in canvas

// document.getElementById("canvasDemo").onmousemove = function(e){
//     var rect = this.getBoundingClientRect(),
//         x = e.clientX - rect.left,
//         y = e.clientY - rect.top;
//     topSvg.selectAll("text").remove(); 

//     for (var i = 0; i < totalPathData.length; i++) {
//         var flow = totalPathData[i];
//         //drawing only those flows which are active
//         if (flow.active == 1) {
//           for (var j = 0; j < flow.value.length; j++) {
//             var obj = flow.value[j],
//                 path = obj['line'];
//                 if(canvas.isPointInStroke(path,x,y)){
//                   topSvg.append("text").attr("class", "velocity").attr("transform", "translate("+x+","+y+")").attr("dy", "2em")
//                   .text(obj.v + "m/s");
//                 }

//           }
//         }
//       }

// } 

//   var canvasLeftX=1920,canvasLeftY=1080,canvasRightX=0,canvasRightY=0;

//   for(var i=0;i<totalPathData.length;i++){
//     var flow = totalPathData[i].value;

//         for(var j=0;j<flow.length;j++){
//             var obj = flow[j];
//             var smallX,bigX,smallY,bigY;
//             if(obj.x0<=obj.x1){
//               smallX = obj.x0;
//               bigX = obj.x1;
//             }else{
//               smallX = obj.x1;
//               bigX = obj.x0;
//             }

//             if(obj.y0<=obj.y1){
//               smallY = obj.y0;
//               bigY = obj.y1;
//             }else{
//               smallY = obj.y1;
//               bigY = obj.y0;
//             }

//             if(smallX<canvasLeftX){
//               canvasLeftX = smallX;
//             }
//             if(bigX>canvasRightX){
//               canvasRightX = bigX
//             }
//             if(smallY<canvasLeftY){
//               canvasLeftY = smallY;
//             }
//             if(bigY>canvasRightY){
//               canvasRightY=bigY;
//             }

//         }
//   }

// function findNewCanvasBoundedXCoordinate(input){
//     return (canvasWidth- (canvasRightX-input));
// }

// function findNewCanvasBoundedYCoordinate(input){
//     return (canvasHeight- (canvasRightY-input));
// }

//new optimal canvas height and width required according to the data
// var canvasWidth = (canvasRightX-canvasLeftX),
//     canvasHeight = (canvasRightY-canvasLeftY);

//defining canvas and setting the bounding box and properties for canvas

//calculates the angle of rotation for the arrow for d3 symbol
// function findRotateAngle(x1,y1,x2,y2){
//   var angle = Math.atan2(y1-y2,x2-x1)*(180/Math.PI);
//   if(angle<0){
//     return (Math.abs(angle)+90);
//   }else if(angle>=0){
//     return (90-angle);
//   
// }


