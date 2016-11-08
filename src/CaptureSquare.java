
import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 參考來源：
 * https://www.oschina.net/code/snippet_54100_1248 * 
 */

public class CaptureSquare extends JFrame {
	private static final long serialVersionUID = -267804510087895906L;
	private JButton button = null;
	private JLabel imgLabel = null;

	public CaptureSquare() {
		button = new JButton("進入擷取模式(按右鍵取消)");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					
					new ScreenWindow(imgLabel);
				} catch (Exception e1) {
					JOptionPane.showConfirmDialog(null, "出現意外錯誤！", "系統提示",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		JPanel pane = new JPanel();
		pane.setBackground(Color.WHITE);
		imgLabel = new JLabel();
		pane.add(imgLabel);
		JScrollPane spane = new JScrollPane(pane);
		this.getContentPane().add(button, BorderLayout.NORTH);
		this.getContentPane().add(spane);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(300, 200);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		new CaptureSquare();
	}
}

class ScreenWindow extends JFrame {
	private static final long serialVersionUID = -3758062802950480258L;
	private boolean isDrag = false;
	private int x = 0;
	private int y = 0;
	private int xEnd = 0;
	private int yEnd = 0;
	private int dx = 0;
	private int dy = 0;
	private int dxEnd = 0;
	private int dyEnd = 0;

	public ScreenWindow(final JLabel imgLabel) throws AWTException,
			InterruptedException {
		//取得螢幕大小
		Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
		JLabel label = new JLabel(new ImageIcon(ScreenImage.getScreenImage(0,
				0, screenDims.width, screenDims.height)));
		
		label.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		
		label.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					dispose();
				}
			}
			
			public void mousePressed(MouseEvent e) {
				x = e.getX();
				y = e.getY();

			}

			public void mouseReleased(MouseEvent e) {
				Image image = null;
				
				if (isDrag) {
					xEnd = e.getX();
					yEnd = e.getY();
					if (x > xEnd) {
						int temp = x;
						x = xEnd;
						xEnd = temp;
					}
					if (y > yEnd) {
						int temp = y;
						y = yEnd;
						yEnd = temp;
					}
					try {
						int width = xEnd - x;
						int height = yEnd - y;
						image = ScreenImage
								.getScreenImage(x, y, width, height);
						
						imgLabel.setIcon(new ImageIcon(image));
						
						saveImage(image,width,height);
					} catch (Exception ex) {
						JOptionPane.showConfirmDialog(null, "出現意外錯誤！", "系統提示",
								JOptionPane.DEFAULT_OPTION,
								JOptionPane.ERROR_MESSAGE);
					}
					dispose();
				}
				
				
			}
		});
		
		
		label.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				if (!isDrag)
					isDrag = true;
				dx = e.getX();
				dy = e.getY();
			}

			public void mouseMoved(MouseEvent e) {
				
				dxEnd = e.getX();
				dyEnd = e.getY();
			}
		});
			    
		this.setUndecorated(true);
		this.getContentPane().add(label);
		this.setSize(screenDims.width, screenDims.height);
		this.setVisible(true);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		
	}

	public void paint(Graphics g) {  
		if(isDrag){
			int penWidth = 1;
			Graphics2D g2 = (Graphics2D) g;  
	        g2.setStroke(new BasicStroke(1.0f));  
	        super.paint(g2);  
	        g2.setColor(new Color(255, 0, 0));  
	        int height = dyEnd - dy;  
	        int width = dxEnd - dx;  
	        //右下到右上
	        g2.drawLine(dx+penWidth, dy, dx+penWidth, dy + height);  
	        //左下到右下
	        g2.drawLine(dx, dy+penWidth*2, dx + width, dy+penWidth*2);  
	        //左上到右上
	        g2.drawLine(dx, dy + height-penWidth*2, dx + width, dy + height-penWidth*2);  
	        //左下到左上
	        g2.drawLine(dx + width-penWidth*2, dy, dx + width-penWidth*2, dy + height); 
		}
         
        this.repaint();
    } 
	
	
	public void saveImage(Image image,int width,int height){
		BufferedImage bfsource = toBufferedImage(image);
		int[] rgb = new int[bfsource.getWidth() * bfsource.getHeight()];
		bfsource.getRGB(0, 0, width, height, rgb, 0, width);
		
		
		BufferedImage buffImg = null;
		
		//取長或寬較長的一邊當邊長
		int length = width;
		if(height > width)
			length = height;
		
		try  
        {  
            buffImg = new BufferedImage(length, length, BufferedImage.TYPE_INT_RGB);  
            Graphics2D gd = buffImg.createGraphics();  
            //透明  start  
            buffImg = gd.getDeviceConfiguration().createCompatibleImage(length, length, Transparency.TRANSLUCENT);  
            gd=buffImg.createGraphics();  
            //透明  end  

            int xoffset = (length-width)/2;
            int yoffset = (length-height)/2;		
            		
            buffImg.setRGB(xoffset, yoffset, width, height, rgb, 0, width);

            gd.drawImage(image, height, width, this);
            
            
        } catch (Exception e) {  
        	
        }  
		
		 BufferedImage imgMap = buffImg;
        File imgFile=new File(".//temp.png");  
        try  
        {  
            ImageIO.write(imgMap, "PNG", imgFile);  
        } catch (IOException e)  
        {  
            e.printStackTrace();  
        }  
	}
	
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
}

class ScreenImage {
	public static Image getScreenImage(int x, int y, int w, int h)
			throws AWTException, InterruptedException {
		Robot robot = new Robot();
		Image screen = robot.createScreenCapture(new Rectangle(x, y, w, h))
				.getScaledInstance(w, h, Image.SCALE_SMOOTH);
		MediaTracker tracker = new MediaTracker(new Label());
		tracker.addImage(screen, 1);
		tracker.waitForID(0);
		return screen;
	}
}