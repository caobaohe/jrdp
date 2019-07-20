package com.cbh.jrdp;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server class
 *
 * @author cbh
 * @date 2019/7/20 08:26
 */
public class Server {

    private static final int PORT = 7777;
    private static final String IMAGE_PATH = "tmp/jrdp/server/";

    private static Socket socket;
    private static ObjectOutputStream OOS;
    private static ObjectInputStream OIS;

    private static Robot robot;
    private static BufferedImage cursorImg;

    public static void main(String[] args) throws Exception {
        try {
            openServer(PORT);
            String cursorPath = Server.class.getResource("/").getPath();
            cursorImg = ImageIO.read(new File(cursorPath + "cursor-fill.png"));
            robot = new Robot();
            Thread thread = new Thread(new ActionThread(OIS));
            thread.start();
            while (true) {
                capturePic();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OOS.close();
            OIS.close();
            socket.close();
        }
    }

    public static void openServer(int port) throws Exception {
        System.out.println("ServerStart.....");
        ServerSocket server = new ServerSocket(port);
        socket = server.accept();
        System.out.println("连接上...\n" + socket);
        OIS = new ObjectInputStream(socket.getInputStream());
        OOS = new ObjectOutputStream(socket.getOutputStream());
    }


    public static void capturePic() throws Exception {
//        robot = new Robot();
        Message msg = null;
        Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
        java.awt.Dimension dm = tk.getScreenSize();
        java.awt.Robot robot = new java.awt.Robot();
        for (int i = 0; i < 6; i++) {
            //截取指定大小的屏幕区域
            Rectangle rec = new Rectangle(0, 0, (int) dm.getWidth(), (int) dm
                    .getHeight());
            BufferedImage bimage = robot.createScreenCapture(rec);
            // 获取鼠标的位置
            Point p = MouseInfo.getPointerInfo().getLocation();
            // 在截屏图片上绘制鼠标
            bimage.createGraphics().drawImage(cursorImg, p.x, p.y, null);
            //将图片保存到文件中
            String filePath = IMAGE_PATH + i + ".jpeg";
            FileOutputStream fops = new FileOutputStream(filePath);
            javax.imageio.ImageIO.write(bimage, "jpeg", fops);
            fops.flush();
            fops.close();
            msg = new Message(filePath);

//            System.out.println("send:" + msg.getFileName());
            OOS.writeObject(msg);
            OOS.flush();
        }
    }


}

class ActionThread implements Runnable {
    private ObjectInputStream OIS;

    public ActionThread(ObjectInputStream ois) {
        this.OIS = ois;
    }

    public void run() {
        try {
            action();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void action() throws Exception {
        Robot robot = new Robot();
        while (true) {
            InputEvent e = (InputEvent) OIS.readObject();
            if (e != null) {
                handleEvents(robot, e);
            }
        }

    }

    public static void handleEvents(Robot action, InputEvent event) {
        MouseEvent mevent = null; //鼠标事件
        MouseWheelEvent mwevent = null;//鼠标滚动事件
        KeyEvent kevent = null; //键盘事件
        int mousebuttonmask = -100; //鼠标按键
        switch (event.getID()) {
            case MouseEvent.MOUSE_MOVED:   //鼠标移动
                System.out.println(event.getID() + ":MOUSE_MOVED");
                mevent = (MouseEvent) event;
                action.mouseMove(mevent.getX(), mevent.getY());
                break;
            case MouseEvent.MOUSE_PRESSED:   //鼠标键按下
                System.out.println(event.getID() + ":MOUSE_PRESSED");
                mevent = (MouseEvent) event;
                action.mouseMove(mevent.getX(), mevent.getY());
                mousebuttonmask = getMouseClick(mevent.getButton());
                if (mousebuttonmask != -100) {
                    action.mousePress(mousebuttonmask);
                }
                break;
            case MouseEvent.MOUSE_RELEASED:  //鼠标键松开
                System.out.println(event.getID() + ":MOUSE_RELEASED");
                mevent = (MouseEvent) event;
                action.mouseMove(mevent.getX(), mevent.getY());
                mousebuttonmask = getMouseClick(mevent.getButton());//取得鼠标按键
                if (mousebuttonmask != -100) {
                    action.mouseRelease(mousebuttonmask);
                }
                break;
            case MouseEvent.MOUSE_WHEEL:   //鼠标滚动
                System.out.println(event.getID() + ":MOUSE_WHEEL");
                mwevent = (MouseWheelEvent) event;
                action.mouseWheel(mwevent.getWheelRotation());
                break;
            case MouseEvent.MOUSE_DRAGGED:   //鼠标拖拽
                System.out.println(event.getID() + ":MOUSE_DRAGGED");
                mevent = (MouseEvent) event;
                action.mouseMove(mevent.getX(), mevent.getY());
                break;
            case KeyEvent.KEY_PRESSED:   //按键
                System.out.println(event.getID() + ":KEY_PRESSED");
                kevent = (KeyEvent) event;
                action.keyPress(kevent.getKeyCode());
                break;
            case KeyEvent.KEY_RELEASED:   //松键
                System.out.println(event.getID() + ":KEY_RELEASED");
                kevent = (KeyEvent) event;
                action.keyRelease(kevent.getKeyCode());
                break;
            default:
                System.out.println(event.getID() + ":default");
                break;
        }
    }

    private static int getMouseClick(int button) { //取得鼠标按键
        if (button == MouseEvent.BUTTON1) {//左键 ,中间键为BUTTON2
            return InputEvent.BUTTON1_MASK;
        }
        if (button == MouseEvent.BUTTON3) {//右键
            return InputEvent.BUTTON3_MASK;
        }
        return -100;
    }
}
