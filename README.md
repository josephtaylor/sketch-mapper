# sketch-mapper

This is the SketchMapper library for processing.
![screenshot](http://s17.postimg.org/qfy5a6lz3/Screenshot_from_2015_02_09_22_18_18.png)
SketchMapper is a GUI tool that allows you to map processing sketches on surfaces.<br/>
This was built off of the [Surface Mapper] library by [Ixagon] and the original [SurfaceMapperGui] by Jason Webb.<br/>
This is mostly *their* work ! This is just adapted to do some different things.

### Current Version
Processing 3 - 4.0.0   
Processing 2 - 3.0.2

### Library Dependencies
uses [ControlP5] version 2.2.5   
when using this library, the ControlP5 library must be imported as well.

## Building
This section will describe how to build the library
##### prerequisites
you will need [maven] installed.<br/>
clone the [processing-deps] project, cd into the root directory of it and run:
```sh
./install.sh
```
This will install the processing dependencies and a number of libraries in your local maven repository (we will need ControlP5).  Once you have the dependencies installed, go into the root dir of this project and run:
```sh
./build.sh
```
This will put the library folder called ```SketchMapper``` in the ```target``` directory as well as a ```SketchMapper.zip``` archive. Copy the folder to your processing sketchbook's libraries folder to use it in processing.

## Usage in Code
This section is about how to use the library in your processing sketches.
#### Creating sketches
This library provides a `Sketch` interface and `AbstractSketch` base implementation that are to be extended to create skeches.  This is a base template for a `TestSketch` that draws an ellipse in the middle of the screen:
```
public class TestSketch extends AbstractSketch {

    public TestSketch(final PApplet parent, final int width, final int height) {
        super(parent, width, height);
    }

    @Override
    public void draw() {
        graphics.beginDraw();
        graphics.background(255);
        graphics.fill(0);
        graphics.ellipse(graphics.width / 2, graphics.height / 2, 25, 25);
        }
        graphics.endDraw();
    }

    @Override
    public void keyEvent(KeyEvent event) {

    }

    @Override
    public void mouseEvent(MouseEvent event) {

    }

    @Override
    public void setup() {

    }
}
```
The constructor must be present and at least have that `super` call.<br/>
Notice in the `draw` method we are using `graphics` to do the drawing. `graphics` is defined in `AbstractSketch` and is an instance of `PGraphics` that is unique to this sketch.<br/>
`AbstractSketch` also has a `parent` variable that is the parent `PApplet` class or the main sketch creating this sketch.  Use the `parent` object when you need to call processing methods.<br/>
`setup` is invoked once by `SketchMapper` when the sketch is initialized.<br/>
The `keyEvent` and `mouseEvent` methods get invoked on key events and mouse events respectively.
#### Using the SketchMapper object
Construct the SketchMapper object by passing it `this` from your main sketch.
```
SketchMapper sketchMapper = new SketchMapper(this);
```
Add sketches to the `SketchMapper` using the `addSketch` method.
```
sketchMapper.addSketch(new TestSketch(this, 500, 500));
```
The sketches that are added will show up in the sketch dropdown in the UI.<br/>
The only other requirement is that you call the `draw` method on the object at the top of your `draw` function.
```
public void draw() {
    sketchMapper.draw();
}
```

### Using the GUI Tool
#### Adding surfaces
quad surfaces and bezier surfaces can be added to the sketch by using the buttons<br/>
<b>Create a new Quad Surface</b> and <b>Create a new Bezier Surface</b>
#### Selecting a sketch for a surface
Click on a surface to select it.<br/>
Select the desired sketch from the dropdown list of sketches<br/>
#### Setting up your layout
You can move a surface by clicking in the middle of it and dragging it.<br/>
You can change the shape of the surface by dragging it's corners.<br/>
Alternatively, if you click on a corner to highlight it, you can use the arrow keys to move it more precisely<br/>
#### Removing a surface
Surfaces can be removed by clicking on them to highlight them and pressing the `delete` key.
#### Saving layouts
Your surface layout can be saved by clicking on <b>Save Layout</b>.<br/>
The sketch will prompt you for a place to save it.<br/>
Layouts are saved in XML format and include the layout of the surfaces and which sketches are on which surfaces.
#### Loading layouts
To load a layout, click on <b>Load Layout</b> and open the layout in the pop-up dialog.

#### Running the sketch(es)
To run the thing, click on <b>Switch to render mode</b>.

#### Returning to the configuration mode.
Double click anywhere on the canvas while in render mode to return to calibration mode.


[surface mapper]:http://ixagon.se/surfacemapper
[ixagon]:http://ixagon.se/
[surfacemappergui]:http://jason-webb.info/2013/11/surfacemappergui-a-simple-processing-interface-for-projection-mapping/
[maven]:http//maven.apache.org
[processing-deps]:https://github.com/josephtaylor/processing-deps
[ControlP5]:http://www.sojamo.de/libraries/controlP5/
