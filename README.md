# JMorph
JMorph is a application designed to render a triangular piecewise morph of two images.

This works by having a start image and end image with a grid of control points for each. 
Each image's control points correspond to the opposite image's control points. 
(i.e. The top left control point for the start image corresponds to the top left control point for the end image.)
To visually simplify this, when dragging a control point on one image, the corresponding control point for the opposite image will turn red.

Once the images and control points are defined and the 'preview' button is pressed, an animation of the morph will play. 
From here, the frames of the animation and a video showing the animation can be exported by pressing 'export'.

An example of such animation can be seen here:

![morph](https://user-images.githubusercontent.com/21374971/160241221-d4c21835-4ba5-4c01-b6d7-1cb3eef04d92.gif)
