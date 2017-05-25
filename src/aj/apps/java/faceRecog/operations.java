package aj.apps.java.faceRecog;

import com.googlecode.javacv.cpp.opencv_contrib;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import java.io.File;
import java.io.FilenameFilter;

import static com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

/**
 * This class provides methods for face detection, recognition, creating dataset etc.
 * @author Anuraj Jain
 */
class operations extends cam {

    private static final String file = "resources/haarcascade_frontalface_default.xml";

    /**
     * Method for face detection
     */
    static CvSeq detectFace(IplImage src){
        CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(file));
        CvMemStorage storage = CvMemStorage.create();
        CvSeq sign = cvHaarDetectObjects(
                src,
                cascade,
                storage,
                1.5,
                3,
                CV_HAAR_DO_CANNY_PRUNING);

        cvClearMemStorage(storage);

        return sign;
    }

    /**
     * Method for drawing rectangle on frame
     */
    static CvRect drawRect(IplImage src, CvSeq sign){
        CvRect r = null;
        int total_Faces = sign.total();

        for(int i = 0; i < total_Faces; i++){
            r = new CvRect(cvGetSeqElem(sign, i));
            cvRectangle (
                    src,
                    cvPoint(r.x(), r.y()),
                    cvPoint(r.width() + r.x(), r.height() + r.y()),
                    CvScalar.RED,
                    2,
                    CV_AA,
                    0);
        }
        return r;
    }

    /**
     * Method for face creating data set
     */
    static int createSampleData(IplImage src, int counter, int id){

        CvSeq sign = detectFace(src);
        CvRect r = drawRect(src, sign);

        if(r != null) {
            IplImage test = cvCreateImage(cvGetSize(src), src.depth(), src.nChannels());
            cvCopy(src, test);
            cvSetImageROI(test, r);
            cvSaveImage("dataSet/"+id+"-user_"+counter+".png", test);
            counter++;
        }
        return counter;
    }

    /**
     * Method for training the face recogniser
     */
    static opencv_contrib.FaceRecognizer trainRecogniser(){
        String trainingDir = "dataSet";
        File root = new File(trainingDir);

        FilenameFilter pngFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".png");
            }
        };

        File[] imageFiles = root.listFiles(pngFilter);

        MatVector images = new MatVector(imageFiles.length);

        int[] labels = new int[imageFiles.length];

        int counter = 0;
        int label;

        IplImage img;
        IplImage grayImg;

        for (File image : imageFiles) {
            img = cvLoadImage(image.getAbsolutePath());
            label = Integer.parseInt(image.getName().split("\\-")[0]);
            grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
            cvCvtColor(img, grayImg, CV_BGR2GRAY);
            images.put(counter, grayImg);
            labels[counter] = label;
            counter++;
        }

        opencv_contrib.FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();
        faceRecognizer.train(images, labels);
        return faceRecognizer;
    }

    /**
     * Method for face recognition
     */
    static int recogniser(IplImage testImage, opencv_contrib.FaceRecognizer faceRecognizer){
        IplImage greyTestImage = IplImage.create(testImage.width(), testImage.height(), IPL_DEPTH_8U, 1);
        cvCvtColor(testImage, greyTestImage, CV_BGR2GRAY);

        CvRect r = drawRect(testImage, detectFace(greyTestImage));
        IplImage test = null;
        if(r != null) {
            System.out.println("face detected");
            test = cvCreateImage(cvGetSize(greyTestImage), greyTestImage.depth(), greyTestImage.nChannels());
            cvCopy(greyTestImage, test);
            cvSetImageROI(test, r);
        }

        int id = 0;
        if (test != null){
            id = faceRecognizer.predict(test);
        }
        System.out.println(id);

        CvFont font = new CvFont(CV_FONT_HERSHEY_SCRIPT_SIMPLEX, 1, 1);
        cvPutText (testImage, String.valueOf(id), cvPoint(20,20), font, CvScalar.BLUE);
        return id;
    }
}