package com.cbh.jrdp;

import sun.misc.JavaLangAccess;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;

/**
 * Client class
 *
 * @author cbh
 * @date 2019/7/20 08:20
 */
public class Client {

    private static final String SERVER_HOST = "192.168.1.111";
    private static final int SERVER_PORT = 7777;

    private static final String RECEIVED_TMP_PATH = "tmp/jrdp/client/";

    private static Socket socket;
    private static ObjectOutputStream OOS;
    private static ObjectInputStream OIS;

    private static JLabel imag_lab;

    public static void main(String[] args) throws Exception {
        try {
            startConnection(SERVER_HOST, SERVER_PORT);
            showUI();
            while (true){
                receivePic();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OOS.close();
            OIS.close();
            socket.close();
        }
    }


    public static void startConnection(String host, int port) throws Exception {
        socket = new Socket(host, port);
        if (socket.isConnected()) {
            System.out.println("socket connected..." + socket);
        }
        OOS = new ObjectOutputStream(socket.getOutputStream());
        OIS = new ObjectInputStream(socket.getInputStream());
    }


    public static void receivePic() throws Exception {
        Message g = (Message) OIS.readObject();
        FileOutputStream FOS = new FileOutputStream(RECEIVED_TMP_PATH + g.getFileName());
        FOS.write(g.getFileContent(), 0, (int) g.getFileLength());
        FOS.flush();

        FileInputStream FIS = new FileInputStream(RECEIVED_TMP_PATH + g.getFileName());
        BufferedImage BI = ImageIO.read(FIS);
        ImageIcon IIC = new ImageIcon(BI);

        Image img = IIC.getImage();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();

        int w = d.width;
        int h = d.height;
        BufferedImage bi = resize(img, w, h);

        imag_lab.setIcon(new ImageIcon(bi));
        imag_lab.repaint();//销掉以前画的背景
    }

    private static BufferedImage resize(Image img, int newW, int newH) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_BGR);
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }

    public static void showUI() {
        //控制台标题
        JFrame jf = new JFrame("控制台");
        setListener(jf);
        //控制台大小
//        jf.setSize(500, 400);
//        jf.setUndecorated(true);
        GraphicsDevice device = jf.getGraphicsConfiguration().getDevice();
        device.setFullScreenWindow(jf);
        //imag_lab用于存放画面
        imag_lab = new JLabel("加载中...", JLabel.CENTER);
        imag_lab.setLayout(new BorderLayout());
        imag_lab.setOpaque(true);
        imag_lab.setBackground(Color.BLACK);
        imag_lab.setForeground(Color.white);
        imag_lab.setBorder(new BasicBorders.ButtonBorder(Color.white, Color.white, Color.white, Color.white));
//        imag_lab.setIcon(new ImageIcon(Client.class.getResource("/").getPath()+"cursor-fill.png"));
        // imag_lab 范围内隐藏本地的光标
        URL classUrl = Client.class.getResource("");
        Image imageCursor = Toolkit.getDefaultToolkit().getImage(classUrl);
        imag_lab.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                imageCursor,  new Point(0, 0), "cursor"));
        jf.add(imag_lab);
        //设置控制台可见
        jf.setVisible(true);
        //控制台置顶
        jf.setAlwaysOnTop(true);
        jf.setResizable(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void setListener(JFrame frame) {
        //panel设置监听器
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                sendEventObject(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                sendEventObject(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                sendEventObject(e);
            }
        });

        frame.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                sendEventObject(e);
            }
        });

        frame.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                sendEventObject(e);
            }

            public void mouseMoved(MouseEvent e) {
                sendEventObject(e);
            }
        });

        frame.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                sendEventObject(e);
            }

            public void mouseEntered(MouseEvent e) {
                sendEventObject(e);
            }

            public void mouseExited(MouseEvent e) {
                sendEventObject(e);
            }

            public void mousePressed(MouseEvent e) {
                sendEventObject(e);
            }

            public void mouseReleased(MouseEvent e) {
                sendEventObject(e);
            }
        });
    }

    private static void sendEventObject(InputEvent event) {
        try {
            System.out.println("send");
            OOS.writeObject(event);
            OOS.flush();
        } catch (Exception ef) {
            ef.printStackTrace();
        }
    }


}

