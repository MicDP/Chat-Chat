package com.chat;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.TextField;
import java.awt.TextArea;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.model.ChatUser;

@SuppressWarnings("serial")
public class Client extends Frame {
	
	private ChatUser user = null;
	private Socket s = null;
	private DataInputStream in = null;
	private DataOutputStream out = null;

	public Client() { 	}
	
	public Client(Socket s) {
		this.s = s;
			try {
				if(null == out ) out = new DataOutputStream(s.getOutputStream());
				if(null == in ) in = new DataInputStream(s.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void connect() throws UnknownHostException, IOException {
			if(null == s) s = new Socket("127.0.0.1", 8001);
			if(null == out ) out = new DataOutputStream(s.getOutputStream());
			if(null == in ) in = new DataInputStream(s.getInputStream());
	}
	
	public void send(String str) throws IOException {
			out.writeUTF(str);
			out.flush();
	}
	
	public void close() {
		try {
			if(null != in) in.close();
			if(null != out) out.close();
			if(null != s && !s.isClosed()) s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String receive() throws IOException {
			return  in.readUTF();
	}
	
	public ChatUser getUser() {
		return user;
	}
	public void setUser(ChatUser user) {
		this.user = user;
	}
	public void setId(int id) {
		user.setId(id);
	}
	public int getId() {
		return user.getId();
	}
	
	public String getName() {
		return user.getName();
	}

	public void setName(String name) {
		user.setName(name);
	}
	
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
