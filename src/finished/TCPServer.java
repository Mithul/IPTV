/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finished;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
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
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

/**
 *
 * @author mithul
 */
public class TCPServer {

    private static byte[] imageBytes;

    public static void main(String args[]) throws IOException, FrameGrabber.Exception {
        String data = "Toobie ornaught toobie";

        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();

        opencv_core.IplImage frame = grabber.grab();
        opencv_core.IplImage image = null;


        CanvasFrame canvasFrame = new CanvasFrame("Some Title");
        canvasFrame.setCanvasSize(frame.width(), frame.height());

        opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();

        ServerSocket srvr = new ServerSocket(1234);
        Socket skt = srvr.accept();
        System.out.print("Server has connected!\n");

        OutputStream out = skt.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);
//        PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
//        OutputStream out1 = skt.getOutputStream();

//        ObjectOutputStream oos = new ObjectOutputStream(skt.getOutputStream());
        while (canvasFrame.isVisible() && (frame = grabber.grab()) != null) {
            cvClearMemStorage(storage);

            cvSmooth(frame, frame, CV_GAUSSIAN, 9, 9, 2, 2);
            image = opencv_core.IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
            cvCvtColor(frame, image, CV_RGB2GRAY);

//            canvasFrame.showImage(frame);

            BufferedImage b = new BufferedImage(frame.width(), frame.height(), BufferedImage.TYPE_INT_RGB);
            image.copyTo(b);
            ByteArrayOutputStream bScrn = new ByteArrayOutputStream();
            ImageIO.write(b, "PNG", bScrn);
            byte[] imgByte = bScrn.toByteArray();

            dos.writeInt(imgByte.length);
            dos.write(imgByte, 0, imgByte.length);

            InputStream in = new ByteArrayInputStream(imgByte);
            BufferedImage img = ImageIO.read(in);
            image.copyFrom(img);

            System.out.println(" imgByte " + b + "\t");
            System.out.println(" imgByte " + img + "\t");

//            oos.writeObject(imgByte);
            System.out.println("Sent\t" + frame + "\t");
//            out1.write(imgByte);
            canvasFrame.showImage(frame);

            // recognize contours
        }

//        System.out.print("Sending string: '" + data + "'\n");
//        out.close();
        skt.close();
        srvr.close();
        System.out.print("Whoops! It didn't work!\n");

        grabber.stop();
        canvasFrame.dispose();
    }

}
