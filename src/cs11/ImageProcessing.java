package cs11;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.MouseEvent;

/**
 * Created by sydney on 11.05.15.
 */
public class ImageProcessing extends PApplet {
    final int WINDOW_HEIGHT = 1000;
    final int WINDOW_WIDTH = 1500;

    final int BACKGROUND_HEIGHT = 250;

    final int BOARDWIDTH = 400;
    final int BOARDLENGTH = 400;
    final int BOARDHEIGHT = 20;

    final float ROTY_COEFF = PI/64;
    final float DEFAULT_TILT_COEFF = 0.01f;
    final float MAX_TILT_COEFF = 1.5f*DEFAULT_TILT_COEFF;
    final float MIN_TILT_COEFF = 0.2f*DEFAULT_TILT_COEFF;
    final float TILT_MAX = PI/3;
    final float UP_TILT = -PI/6;

    final float BALL_RADIUS = 25;

    float tilt_coeff = DEFAULT_TILT_COEFF;

    float rotation = 0.0f;
    float tiltX = 0.0f;
    float tiltZ = 0.0f;
    float tiltXBackup = 0.0f;
    float tiltZBackup = 0.0f;
    float rotationBackup = 0.0f;

    Mover mover;

    boolean showAxis = false;
    boolean drawOrigin = false;
    float longueurAxes = 1000;

    boolean addingCylinderMode = false;

    PGraphics dataBackground;

    public void setup() {
        size(WINDOW_WIDTH, WINDOW_HEIGHT, P3D);
        noStroke();
        mover = new Mover(this, BALL_RADIUS, BOARDLENGTH, BOARDHEIGHT, BOARDWIDTH);
        dataBackground = createGraphics(WINDOW_WIDTH, BACKGROUND_HEIGHT, P2D);
    }

    public void draw() {
        background(200);
        directionalLight(255, 255, 255, 0, 1, -1);
        ambientLight(102, 102, 102);

        dataBackground.beginDraw();
        dataBackground.background(255, 255, 200);
        dataBackground.endDraw();
        image(dataBackground, 0, WINDOW_HEIGHT-BACKGROUND_HEIGHT);

        // Based on which mode we are, camera is placed nearer the board
        if(addingCylinderMode)
            camera(width/2, height/2, 400, width/2, height/2, 0, 0, 1, 0);
        else
            camera(width/2, height/2, 600, width/2, height/2, 0, 0, 1, 0);

        // Place the coordinate system
        translate(width/2, height/2, 0);
        if(!addingCylinderMode) rotateX(tiltX + UP_TILT);
        else rotateX(tiltX);
        rotateZ(tiltZ);
        rotateY(rotation);

        // Optional : show Axis
        if(showAxis) {
            //Axe X
            stroke(0, 255, 0);
            line(-longueurAxes/2, 0, 0, longueurAxes/2, 0, 0);
            //Axe Y
            stroke(255, 0, 0);
            line(0, -longueurAxes/2, 0, 0, longueurAxes/2, 0);
            //Axe Z
            stroke(0,0,255);
            line(0, 0, -longueurAxes/2, 0, 0, longueurAxes/2);
        }

        //If we are in adding cylinder mode, place a cylinder
        if(addingCylinderMode) {
            mover.placeCylinder(map(mouseX, 0, width, -BOARDLENGTH/2, BOARDLENGTH/2), map(mouseY, 0, height, -BOARDWIDTH/2, BOARDWIDTH/2));
        }
        else {
            // update and display environnement here
            mover.update(tiltX, tiltZ);
            mover.display();
        }
    }

    public void keyPressed() {
        if(key == CODED) {
            if(keyCode == SHIFT && !addingCylinderMode) {
                addingCylinderMode = true;
                mover.setAddingCylinderMode(true);
                tiltXBackup = tiltX; // Needed to restore the tilt after adding cylinder(s)
                tiltZBackup = tiltZ; // Same here
                rotationBackup = rotation; // Same here
                rotation = 0; // Clear rotation

                tiltX = -PI/2; // Rotate the board in front of the camera
                tiltZ = 0;
            }
            if(!addingCylinderMode) {
                if(keyCode == LEFT) {
                    rotation += ROTY_COEFF;
                }
                else if(keyCode == RIGHT) {
                    rotation -= ROTY_COEFF;
                }
            }
        }
    }

    public void keyReleased() {
        if(key == CODED) {
            if(keyCode == SHIFT) {
                addingCylinderMode = false;
                mover.setAddingCylinderMode(false);
                tiltX = tiltXBackup;
                tiltZ = tiltZBackup;
                rotation = rotationBackup;
            }
        }
    }

    public void mouseDragged() {
        if(!addingCylinderMode) {
            float tiltXIncrement = -tilt_coeff*(mouseY - pmouseY);
            float tiltZIncrement = tilt_coeff*(mouseX - pmouseX);

            if(abs(tiltX + tiltXIncrement) < TILT_MAX)
                tiltX += tiltXIncrement;
            if(abs(tiltZ + tiltZIncrement) < TILT_MAX)
                tiltZ += tiltZIncrement;
        }
    }

    public void mousePressed() {
        mover.addCylinder(map(mouseX, 0, width, -BOARDLENGTH/2, BOARDLENGTH/2), map(mouseY, 0, height, -BOARDWIDTH/2, BOARDWIDTH/2));
    }

    public void mouseWheel(MouseEvent event) {
        float newTilt = tilt_coeff + -event.getCount()*0.1f*DEFAULT_TILT_COEFF;

        if(newTilt > MIN_TILT_COEFF && newTilt < MAX_TILT_COEFF)
            tilt_coeff = newTilt;
    }
}
