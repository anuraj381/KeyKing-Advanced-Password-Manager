package aj.apps.java.faceRecog;

import aj.apps.java.Main.popup;

/**
 * Contains constants for operations performed by openCam method from cam class
 * @author Anuraj Jain
 * @see cam
 */
public class cvTask extends cam {

    public static final int CAMERA_ONLY = 1;
    public static final int DETECT_FACE = 2;
    public static final int CREATE_DATASET = 3;
    public static final int RECOGNISE_FACE = 4;
    public static final int RECOGNISE_LIVE = 5;

    public static int id = 0;

}