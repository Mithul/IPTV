/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finished;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import org.bytedeco.javacv.CanvasFrame;

/**
 *
 * @author mithul
 */
public class TCPClient {

    private static byte[] imageBytes;
    private static opencv_core.IplImage image;
    private static CanvasFrame canvasFrame;
    private static Socket skt;
    private static InputStream in;
    private static DataInputStream dis;

    private static void createConnection(String ip, int port) {
        try {
            skt = new Socket(ip, port);
            in = skt.getInputStream();
            dis = new DataInputStream(in);
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Mat getImage() {

        Mat resizeimage = new Mat();
        try {
            int len = dis.readInt();
            imageBytes = new byte[len];

            dis.readFully(imageBytes);

            InputStream in1 = new ByteArrayInputStream(imageBytes);
            BufferedImage img = ImageIO.read(in1);
            System.out.println(img + "\t" + imageBytes);
            image.copyFrom(img);

            System.out.println(image);
            Mat m = new Mat(image);
            Size sz = new Size(640, 480);
            org.bytedeco.javacpp.opencv_imgproc.resize(m, resizeimage, sz);
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resizeimage;
    }

    public static void main(String args[]) {
        try {

            createConnection("localhost",1234);
            
            image = null;

            canvasFrame = new CanvasFrame("Some Title");
            canvasFrame.setCanvasSize(640, 480);

            opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();

            image = opencv_core.IplImage.create(480, 480, IPL_DEPTH_8U, 1);

            while (canvasFrame.isVisible()) {
                cvClearMemStorage(storage);
                
                Mat dispImage = getImage();
                
                canvasFrame.showImage(dispImage);
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
