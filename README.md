# Endless Sky Shipyard

Coordinate finder and ship builder for Endless Sky. To be the complete ship maker for ES covering all from stats, outfitting, to positioning hardpoints.

![image](https://github.com/user-attachments/assets/51fd8d99-d7b7-45a6-abfc-d7f952c52dd0)

## Requirement

Java 21.0.2 (What I used, may or may not work with older version)

## Compilation

```bash
javac *.java
jar -cfe es_shipyard.jar Shipyard *.class
#todo compile to .exe executable?
```
(Literally just too lazy to get a proper IDE setup.)

Or you could probably open it in your favorite Java IDE and click on whatever build and run the program.

## Running

Double click es_shipyard.jar

```bash
java -jar es_shipyard.jar
```

## Usage

* Left-Click anywhere within the image bound to select a position.
* Right-Click within the central section to select the hardpoint type to add.
* Arrow Keys to fine tune when focus is on the central section (click it).
	* Use Ctrl and/or Shift modifier for rougher, faster moves.
* Ctrl-Z to undo last added hardpoint.
* Ctrl-Shift-Z to redo. (Currently history doesn't clear after doing other stuffs.)
* Alt-M or select "Mirror" on the left menu to automatically duplicate points across x-axis.
* Alt-C select "Snap to center" to snap X 0.5 to 0 or X within +-0.005x of the image width.
* Alt-X or Alt-Y or select Lock X or Y axis when moving the pointer.
* Hold A to draw a angle guide and display the current angle relative to the front.
* Scroll wheel to zoom in and out.
* Middle mouse button to drag
* Home key to reset zoom and drag.
* Configure hardpoint values on the left panel.
