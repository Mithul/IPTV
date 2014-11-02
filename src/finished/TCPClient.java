/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finished;

import static finished.TCPServer.start;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;

/**
 *
 * @author mithul
 */
public class TCPClient extends SwingWorker<Void, Void> {

    private byte[] imageBytes;
    private opencv_core.IplImage image;
    private CanvasFrame canvasFrame;
    private Socket skt;
    private InputStream in;
    private DataInputStream dis;

    private void createConnection(String ip, int port) {
        try {
            skt = new Socket(ip, port);
            in = skt.getInputStream();
            dis = new DataInputStream(in);
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Mat getImage() {

        Mat resizeimage = new Mat();
        try {
            int len = dis.readInt();
            if (len < 0) {
                return null;
            }
            imageBytes = new byte[len];

            dis.readFully(imageBytes);

            InputStream in1 = new ByteArrayInputStream(imageBytes);
            BufferedImage img = ImageIO.read(in1);
            System.out.println("Received\t" + len);
            if (img == null) {
                return null;
            }
            image.copyFrom(img);

            //System.out.println(image);
            Mat m = new Mat(image);
            Size sz = new Size(640, 480);
            org.bytedeco.javacpp.opencv_imgproc.resize(m, resizeimage, sz);
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!resizeimage.empty()) {
            return resizeimage;
        } else {
            return null;
        }
    }

    TCPClient(String ip) {
        createConnection(ip, 1234);
    }

    @Override
    public Void doInBackground() throws IOException, FrameGrabber.Exception {
        start();
        return null;
    }

    @Override
    public void done() {
    }

    public void start() {
        try {

            image = null;

            canvasFrame = new CanvasFrame("Client");
            canvasFrame.setCanvasSize(640, 480);

            opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();

            image = opencv_core.IplImage.create(480, 480, IPL_DEPTH_8U, 1);

            while (canvasFrame.isVisible()) {
                cvClearMemStorage(storage);

                Mat dispImage = getImage();
                if (dispImage != null) {
                    canvasFrame.showImage(dispImage);
                }
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
