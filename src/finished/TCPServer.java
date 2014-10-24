/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finished;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static opencv_core.IplImage image;
    private static CanvasFrame canvasFrame;
    private static OutputStream out;
    private static DataOutputStream dos[];
    private static int noClients;

    private static DataOutputStream createConnection(ServerSocket srvr) {
        DataOutputStream dos = null;
        try {
            Socket skt = srvr.accept();
            System.out.print("Server has connected!\n");

            out = skt.getOutputStream();
            dos = new DataOutputStream(out);
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dos;
    }

    private static void sendImage(DataOutputStream dos, opencv_core.IplImage frame) {
        try {
            BufferedImage b = new BufferedImage(frame.width(), frame.height(), BufferedImage.TYPE_INT_RGB);
            image.copyTo(b);
            ByteArrayOutputStream bScrn = new ByteArrayOutputStream();
            ImageIO.write(b, "PNG", bScrn);
            byte[] imgByte = bScrn.toByteArray();

            dos.writeInt(imgByte.length);
            dos.write(imgByte, 0, imgByte.length);
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void clientService(ServerSocket srvr, int noClients) {
        TCPServer.noClients = noClients;
        dos = new DataOutputStream[noClients];
        for (int i = 0; i < noClients; i++) {
            dos[i] = createConnection(srvr);
        }

    }

    public static void main(String args[]) throws IOException, FrameGrabber.Exception {

        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();

        opencv_core.IplImage frame = grabber.grab();
        image = null;

        canvasFrame = new CanvasFrame("Some Title");
        canvasFrame.setCanvasSize(frame.width(), frame.height());

        opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();

        ServerSocket srvr = new ServerSocket(1234);
        System.out.print("Server has created Socket\n");

        clientService(srvr, 2);

        while (canvasFrame.isVisible() && (frame = grabber.grab()) != null) {
            cvClearMemStorage(storage);

            cvSmooth(frame, frame, CV_GAUSSIAN, 9, 9, 2, 2);
            image = opencv_core.IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
            cvCvtColor(frame, image, CV_RGB2GRAY);

            for (int i = 0; i < noClients; i++) {
                sendImage(dos[i], frame);
            }

            /*InputStream in = new ByteArrayInputStream(imgByte);
             BufferedImage img = ImageIO.read(in);
             image.copyFrom(img);

             System.out.println(" imgByte " + b + "\t");
             System.out.println(" imgByte " + img + "\t");
             */
            System.out.println("Sent\t" + frame + "\t");
            canvasFrame.showImage(frame);

        }

        srvr.close();
        System.out.print("Whoops! It didn't work!\n");

        grabber.stop();
        canvasFrame.dispose();
    }

}
