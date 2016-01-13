package com.viewController;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.TextField;
import java.awt.TextArea;
import java.awt.event.KeyEvent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyAdapter;
import java.io.IOException;
import java.net.UnknownHostException;

import com.chat.Client;

@SuppressWarnings("serial")
public class ClientViewController extends Frame {
	
	public final int X = 400;
	public final int Y = 200;
	public final int WIDTH = 360;
	public final int HEIGHT = 480;
	
	private TextArea textContent = null;
	private TextField words = null;
	
	private Client client = null;
	
	public ClientViewController(Client client) {
		this.client = client;
	}
	
	public void lauch() {
		this.setTitle("用户名:" + client.getName());
		this.setBounds(X, Y, WIDTH, HEIGHT) ;
		textContent = new TextArea();
		words = new TextField();
		textContent.setEditable(false);
	 	 words.setEditable(true);
		//		textContent.setEnabled(false);
//		words.setEnabled(true);
	 	 
		this.add(textContent, BorderLayout.NORTH);
		this.add(words, BorderLayout.SOUTH);
		this.pack();
		this.setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
				System.exit(0);
			}
		});
		words.addKeyListener(new MyKeyboardListener());
		words.addTextListener(new MyTextListener());
		
		this.setFontForText(words, textContent, new Font("宋体", Font.LAYOUT_LEFT_TO_RIGHT, 12));
	    connect();
	}
	
	private void connect() {
		try {
			client.connect();
			new Thread(new ClientReceiverThread()).start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(String str) {
		try {
			client.send(str);
		} catch (IOException e) {
			client.close();
			e.printStackTrace();
		}
	}
	
	public void close() {
		client.close();
	}
	
	public String receive() throws IOException {
			return client.receive();
	}
	
	private class MyKeyboardListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				textContent.setText(textContent.getText()  + "I say: " + words.getText() + "\n");
				send(words.getText());
				words.setText("");
				System.out.println("textContent text23: " + textContent.getText());			
			}
		}
	}
	
	private class ClientReceiverThread implements Runnable{
		
		private boolean isGeted = false;
		private String message = "";
		
		@Override
		public void run() {
			isGeted = true;
			try {
				while(isGeted) {
					message =  client.receive();
					textContent.setText(textContent.getText()  + message  + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				client.close();
			}
		}
	}
	
	private class MyTextListener implements TextListener {      
		  
		@Override
		public void textValueChanged(TextEvent e) {
			
		}      
    }
	
	// use Chinese
	public void setFontForText(TextField words, TextArea textContent, Font font) {
		Font[] fList;  
        //初始化Font列表  
        String[] lstr = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fList = new Font[lstr.length];  
        for(int i=0; i<lstr.length; i++) {  
         fList[i]=new Font(lstr[i], font.getStyle(), font.getSize());  
        }  
		  
        String s = "英文";  
        if(font.canDisplayUpTo(s)!=-1) {
        	//发现不能显示字体，则查找可使用的字体  
            for(int i=0;i<fList.length;i++) {  
                if(fList[i].canDisplayUpTo(s)==-1) {  
                    words.setFont(fList[i]);  
                    textContent.setFont(fList[i]);
                    break;  
                }  
            }  
        }
	}
}
