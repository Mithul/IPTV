/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package iptv;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author mithul
 */
public class IPTV {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException {
//        URL u= new URL("file://try.mp4");
//        MediaPanel m = new MediaPanel(u);
        GrabberShow g=new GrabberShow();
        g.run();
        // TODO code application logic here
    }
    
}
