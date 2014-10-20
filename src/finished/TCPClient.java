/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finished;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import javax.imageio.ImageIO;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_GAUSSIAN;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import org.bytedeco.javacv.CanvasFrame;

/**
 *
 * @author mithul
 */
public class TCPClient {

    private static byte[] imageBytes;

    public static void main(String args[]) {
        try {
//            opencv_core.IplImage frame = grabber.grab();
            opencv_core.IplImage frame = null;

            opencv_core.IplImage image = null;
            opencv_core.IplImage prevImage = null;
            opencv_core.IplImage diff = null;

            IplImage i = new IplImage();

            CanvasFrame canvasFrame = new CanvasFrame("Some Title");
            canvasFrame.setCanvasSize(640, 480);

            opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();

            Socket skt = new Socket("localhost", 1234);
            InputStream in = skt.getInputStream();
            DataInputStream dis = new DataInputStream(in);
            
            image = opencv_core.IplImage.create(640, 480, IPL_DEPTH_8U, 1);
//            InputStream is = skt.getInputStream();
//            ObjectInputStream ois = new ObjectInputStream(skt.getInputStream());
            System.out.print("Received string: '");

//            while (!is.ready()) {
//            }
            while (true) {
                cvClearMemStorage(storage);
//                cvSmooth(frame, frame, CV_GAUSSIAN, 9, 9, 2, 2);
//                image = opencv_core.IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
//                cvCvtColor(frame, image, CV_RGB2GRAY);
//                is.read(imageBytes);
//                Object o = ois.readObject();

                int len = dis.readInt();
                imageBytes = new byte[len];
                dis.readFully(imageBytes);
                InputStream in1 = new ByteArrayInputStream(imageBytes);
                BufferedImage img = ImageIO.read(in1);
                System.out.println(img + "\t" + imageBytes);
                image.copyFrom(img);
                System.out.println(image);
                canvasFrame.showImage(image);
            }

//            System.out.println(in.readLine()); // Read one line and output it
//            System.out.print("'\n");
//            in.close();
        } catch (Exception e) {
            System.out.print("Whoops! It didn't work!\n");
            java.util.logging.Logger.getLogger(TCPClient.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
        }
    }

}
