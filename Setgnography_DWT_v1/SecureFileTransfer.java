import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;
import javax.swing.filechooser.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import java.io.File;
// import java.io.FileInputStream;
import javax.imageio.ImageIO;
import java.nio.ByteBuffer;
// import javax.swing.ImageIcon;

class GuiSecureFileTransfer extends JFrame implements ActionListener {
	JButton open;
	JButton send;
	Socket socket_var;
	DataOutputStream outputStream;
	BufferedImage sourceImage = null;
	BufferedImage embeddedImage = null;
	JTextField plain_text;
	JLabel sourceImage_label;
	JLabel l;
	String path;
	JLabel embeddedImage_label;
	JButton embed;

	public GuiSecureFileTransfer() {
		try {
			socket_var = new Socket(InetAddress.getByName("127.0.0.1"), 3636);
			outputStream = new DataOutputStream(socket_var.getOutputStream());
		} catch (Exception ex) {
			System.out.println(ex);
		}
		open = new JButton("Open");
		send = new JButton("Send");
		embed = new JButton("Embed");
		l = new JLabel();
		sourceImage_label = new JLabel();
		sourceImage_label.setBounds(50, 10, 20, 20);
		embeddedImage_label = new JLabel();
		embeddedImage_label.setBounds(50, 10, 10, 10);
		plain_text = new JTextField(10);
		send.addActionListener(this);
		open.addActionListener(this);
		embed.addActionListener(this);
		add(plain_text);
		add(open);
		add(send);
		add(embed);
		add(l);
		add(sourceImage_label);
		add(embeddedImage_label);
		setLayout(new FlowLayout());
		setVisible(true);
		setSize(500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void actionPerformed(ActionEvent ae) {
		JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		if (ae.getSource() == open) {
			int response = fc.showOpenDialog(null);
			if (response == JFileChooser.APPROVE_OPTION) {
				l.setText(fc.getSelectedFile().getAbsolutePath());
				path = fc.getSelectedFile().getAbsolutePath();
			} else {
				l.setText("the user cancelled the operation");
			}
			try {
				sourceImage = ImageIO.read(new File(path));
				sourceImage_label.setIcon(new ImageIcon(sourceImage));
			} catch (Exception exce) {
				System.out.println("Exception");
			}
		}
		if (ae.getSource() == embed) {
			String mess = plain_text.getText();
			if (mess.equals("") || sourceImage.getHeight() == 0) {
				JOptionPane.showMessageDialog(this, "Please Select Image and Enter the Text First!");
				return;
			}
			embeddedImage = sourceImage.getSubimage(0, 0, sourceImage.getWidth(), sourceImage.getHeight());
			embeddedImage = embedMessage(embeddedImage, mess);
			embeddedImage_label.setIcon(new ImageIcon(embeddedImage));
			System.out.println(embeddedImage.getHeight());
			this.validate();
			try {
				ImageIO.write(embeddedImage, "png", new File("C:\\Users\\kandi\\Desktop\\Setgnography_DWT_v1\\facebook_logo.png"));
			} catch (Exception ex) {
				System.out.println("Exception");
			}
		}
		if (ae.getSource() == send) {
			try {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ImageIO.write(embeddedImage, "png", byteArrayOutputStream);
				byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
				outputStream.write(size);
				outputStream.write(byteArrayOutputStream.toByteArray());
				System.out.println();

				outputStream.flush();
			} catch (Exception ex) {
				System.out.println(ex);
			}
		}
	}

	private BufferedImage embedMessage(BufferedImage img, String mess) {
		int messageLength = mess.length();
		int imageWidth = img.getWidth(), imageHeight = img.getHeight(), imageSize = imageWidth * imageHeight;
		if (messageLength * 8 + 32 > imageSize) {
			JOptionPane.showMessageDialog(this, "Message is too long for the chosen image", "Message too long!",
					JOptionPane.ERROR_MESSAGE);
			return img;
		}
		DWT2 obj = new DWT2();
		return obj.embedText(img, mess);
	}

}

class SecureFileTransfer {
	public static void main(String[] args) throws Exception {
		GuiSecureFileTransfer gft = new GuiSecureFileTransfer();
	}
}
