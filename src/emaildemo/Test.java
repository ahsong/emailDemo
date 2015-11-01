import java.awt.FlowLayout;
    import java.awt.Graphics2D;
    import java.awt.Image;
    import java.awt.event.ActionEvent;
    import java.awt.event.WindowAdapter;
    import java.awt.event.WindowEvent;
    import java.awt.image.BufferedImage;
    import java.io.File;
    import java.io.IOException;
    import java.lang.reflect.Array;
    import java.util.Vector;
    
    import javax.imageio.ImageIO;
    import javax.media.Buffer;
    import javax.media.CaptureDeviceInfo;
    import javax.media.CaptureDeviceManager;
    import javax.media.ControllerAdapter;
    import javax.media.ControllerEvent;
    import javax.media.Format;
    import javax.media.Manager;
    import javax.media.NoDataSourceException;
    import javax.media.Player;
    import javax.media.RealizeCompleteEvent;
    import javax.media.control.FrameGrabbingControl;
    import javax.media.format.VideoFormat;
    import javax.media.protocol.DataSource;
    import javax.media.util.BufferToImage;
    import javax.swing.ImageIcon;
    import javax.swing.JFrame;
    import javax.swing.JLabel;
    import javax.swing.JPanel;
    
    public class Test {
    
      static JPanel             panel       = new JPanel();
      static JFrame             myFrame     = new JFrame();
      static Player             player      = null;
    
      static CaptureDeviceInfo  videoDevice = null;
      static VideoFormat        videoFormat = null;
    
      public void actionPerformed(ActionEvent ae) { }
    
      public static void main(String[] argv) throws NoDataSourceException, Exception {
        
        //PANEL.           
        panel = new JPanel();
        panel.setLayout(new FlowLayout());      
        
        //CREATE FRAME.
        myFrame = new JFrame();
        myFrame.setVisible(true);
        myFrame.setSize(300,300);
        myFrame.getContentPane().add(panel);     
        myFrame.addWindowListener(
          new WindowAdapter(){
            public void windowClosing(WindowEvent event){  
              player.close();
              myFrame.dispose();
            }
          }
        );                         
    
        //GET ALL MEDIA DEVICES.
        Vector deviceListVector = CaptureDeviceManager.getDeviceList(null);
    
        //CHOOSE AUDIO DEVICES & FORMAT.
        for (int x = 0; x < deviceListVector.size(); x++)    {
    
          CaptureDeviceInfo device         = (CaptureDeviceInfo) deviceListVector.elementAt(x);
          String            deviceName     = device.getName();           
          Format            deviceFormat[] = device.getFormats();
          
          for (int y = 0; y < deviceFormat.length; y++)      {                      
            if (videoDevice == null && deviceFormat[y] instanceof VideoFormat) {
              videoFormat = (VideoFormat) deviceFormat[y];
              if(videoFormat.toString().indexOf("640")!=-1) {
                videoDevice = device;
                System.out.println(videoFormat);
              }
            }
    
          }
        }       
        
        //VIDEO DATA SOURCE.
        DataSource videoDataSource = Manager.createDataSource(videoDevice.getLocator());
        DeviceInfo.setFormat(videoDataSource, videoFormat);
    
        //CREATE PLAYER.
        player = Manager.createPlayer(videoDataSource);
        player.addControllerListener(
            new ControllerAdapter(){
              public void controllerUpdate(ControllerEvent event){  
                if (event instanceof RealizeCompleteEvent) {
                  panel.add(player.getVisualComponent());
                  panel.add(player.getControlPanelComponent());
                  myFrame.validate();
                }
              }
            }
        );        
        player.start();     
        
        //GRAB IMAGE.
        Thread.sleep(5000);
        FrameGrabbingControl fgc  = (FrameGrabbingControl) player.getControl("javax.media.control.FrameGrabbingControl");  
        Buffer               buf  = fgc.grabFrame();
        BufferToImage        btoi = new BufferToImage((VideoFormat)buf.getFormat());    
        Image                img  = btoi.createImage(buf);
        saveImagetoFile(img,"Dots.jpg");
        panel.add(new JLabel(new ImageIcon(img)));  //Expand window to see the image.
        panel.validate();
      } 
      
      static public void saveImagetoFile(Image img, String fileName) throws IOException {
        int           w = img.getWidth(null);
        int           h = img.getHeight(null);
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D    g2 = bi.createGraphics();
                      g2.drawImage(img, 0, 0, null);
                      g2.dispose();
        String fileType = fileName.substring(fileName.indexOf('.')+1);
        ImageIO.write(bi, fileType, new File(fileName));
      } 
    
    }