package aj.apps.java.faceRecog;

import aj.apps.java.Main.popup;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.avutil;
import com.googlecode.javacv.cpp.opencv_contrib;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import java.io.File;
import java.util.Objects;

/**
 * this class contains methods to open camera and initialise various operations like
 * face detection, face recognition, creating dataset etc. on the captured frames
 *
 * <p>This class is actually providing interface between java application and javaCV
 *
 * This class is using Computer Vision for image processing and analysis</p>
 * @author Anuraj Jain
 */
public class cam extends popup {

    private static final String FILENAME = "output.mp4";

    /**
     * this method is used to initiate various operations like face detection, face recognition, creating dataset etc.
     * <p>This class is actually providing interface between java application and javaCV.</p>
     *
     * <p>This class is using Computer Vision for image processing and analysis</p>
     * @author Anuraj Jain
     * @throws Exception
     * @param OPERATION_CODE integer code for the operation to be performed
     * @return integer result
     */
    public static int openCam(int OPERATION_CODE) throws Exception {

        int toReturn = 0;
        boolean present = false;

        File file = new File(".");
        String path1 = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 1) + "dataSet";
        String path2 = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 1) + "resources";
        System.out.println(path1);
        File dir1 = new File(path1);
        File dir2 = new File(path2);
        dir1.mkdirs();
        dir2.mkdirs();

        File list[] = dir2.listFiles();
        if (list.length != 0) {
            for (File aList : list) {
                if (aList.isFile()) {
                    if (aList.exists()) {
                        if (aList.getName().equals("haarcascade_frontalface_default.xml")) {
                            present = true;
                            OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
                            grabber.start();
                            IplImage grabbedImage = grabber.grab();

                            CanvasFrame canvasFrame = new CanvasFrame("Cam");
                            canvasFrame.setCanvasSize(grabbedImage.width(), grabbedImage.height());

                            System.out.println("framerate = " + grabber.getFrameRate());
                            grabber.setFrameRate(grabber.getFrameRate());
                            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(FILENAME, grabber.getImageWidth(), grabber.getImageHeight());

                            recorder.setVideoCodec(13);
                            recorder.setFormat("mp4");
                            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
                            recorder.setFrameRate(30);
                            recorder.setVideoBitrate(10 * 1024 * 1024);

                            recorder.start();

                            //recogniser object
                            opencv_contrib.FaceRecognizer faceRecognizer = null;

                            int counter = 0;
                            while (canvasFrame.isVisible() && (grabbedImage = grabber.grab()) != null) {

                                if (Objects.equals(OPERATION_CODE, cvTask.CAMERA_ONLY)) {
                                    toReturn = 1;
                                } else if (Objects.equals(OPERATION_CODE, cvTask.DETECT_FACE)) {
                                    opencv_core.CvSeq sign = operations.detectFace(grabbedImage);
                                    operations.drawRect(grabbedImage, sign);
                                    toReturn = 1;
                                } else if (Objects.equals(OPERATION_CODE, cvTask.CREATE_DATASET)) {
                                    counter = operations.createSampleData(grabbedImage, counter, cvTask.id);
                                    if (counter > 50) {
                                        toReturn = 1;
                                        break;
                                    }
                                } else if (Objects.equals(OPERATION_CODE, cvTask.RECOGNISE_FACE)) {
                                    if (counter == 0) {
                                        faceRecognizer = operations.trainRecogniser();
                                        counter++;
                                    }
                                    if (faceRecognizer != null) {
                                        toReturn = operations.recogniser(grabbedImage, faceRecognizer);
                                    }
                                    break;
                                } else if (Objects.equals(OPERATION_CODE, cvTask.RECOGNISE_LIVE)) {
                                    if (counter == 0) {
                                        faceRecognizer = operations.trainRecogniser();
                                        System.out.println("trained");
                                        counter++;
                                    }
                                    if (faceRecognizer != null) {
                                        toReturn = operations.recogniser(grabbedImage, faceRecognizer);
                                    }
                                } else {
                                    System.out.println("Enter a valid OPERATION_CODE");
                                    break;
                                }

                                canvasFrame.showImage(grabbedImage);
                                recorder.record(grabbedImage);
                                //code if you want flip the frame captured
                                /*if (grabbedImage != null) {
                                    //Flip image horizontally
                                    cvFlip(grabbedImage, grabbedImage, 1);
                                    //Show video frame in canvas
                                    canvas.showImage(grabbedImage);
                                }*/
                            }

                            recorder.stop();
                            grabber.stop();
                            canvasFrame.dispose();
                            break;
                        }
                    }
                }
            }
        }

        if (!present){
            popup.messageBox("Put haarcascade_frontalface_default.xml in resource folder of application to use face recognition feature");
        }

        return toReturn;
    }
}