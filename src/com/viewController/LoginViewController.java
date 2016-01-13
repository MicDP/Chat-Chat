package com.viewController;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.ConnectException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.chat.Client;
import com.model.ChatUser;

public class LoginViewController {
	
	public final int X = 400;
	public final int Y = 100;
	public final int WIDTH = 400;
	public final int HEIGHT = 500;
	
	private JFrame frame = null;
	private JLabel nameLabel = null;
	private JTextField nameField = null;
	private JLabel passwordLabel = null;
	private JPasswordField passwordField = null;
	private JButton loginBtn = null;
	private JButton registerBtn = null;
	
	public LoginViewController() {
		frame = new JFrame();
		frame.setTitle("a sample chat system");
		frame.setBounds(X, Y, WIDTH, HEIGHT);
		frame.setLayout(null);
		
		// 1. title
		JLabel titleLabel = new JLabel("Chat Chat");
		titleLabel.setBounds(WIDTH/2-50, 60, 100, 30);
		titleLabel.setBackground(Color.BLACK);
		frame.add(titleLabel);
		
		// 2. name
		 JPanel namePanel = new JPanel();
		 namePanel.setBounds(0, titleLabel.getY()+titleLabel.getHeight()+20, WIDTH, 40);
		 namePanel.setLayout(new FlowLayout());
		nameLabel = new JLabel("name: ");
		nameField = new JTextField(14);
		namePanel.add(nameLabel);
		namePanel.add(nameField);
		frame.add(namePanel);
		
		// 3. password
		JPanel passwordPanel = new JPanel();
		passwordPanel.setBounds(0, namePanel.getY()+namePanel.getHeight(), WIDTH, 40);
		passwordPanel.setLayout(new FlowLayout());
		passwordLabel = new JLabel("password: ");
		passwordField = new JPasswordField(14);
		passwordField.setEchoChar('*');
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordField);
		frame.add(passwordPanel);
		
		// 4. login
		JPanel btnPanel = new JPanel();
        btnPanel.setBounds(0, passwordPanel.getY()+passwordPanel.getHeight(), WIDTH, 40);
        btnPanel.setLayout(new FlowLayout());
		loginBtn = new JButton("login");
		registerBtn = new JButton("register");
		btnPanel.add(loginBtn);
		btnPanel.add(registerBtn);
		frame.add(btnPanel);
		
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		loginBtn.addActionListener(new BtnListenter());
	}
	
	private class BtnListenter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Client client = new Client();
			try {
				client.connect();
				client.send("[{name: " + nameField.getText() + ", password: " + new String(passwordField.getPassword()) +"}]");
				String result = client.receive();
System.out.println(result);
				if("failed".equals(result)) {
					client.close();
					// 5. fail to login
					JPanel failLoginPanel = new JPanel();
					failLoginPanel.setBounds(0, HEIGHT-100, WIDTH, 40);
					JLabel failLoginLabel = new JLabel("<html>fail to login, please check your name and password <br/>to login again!</html>");
					failLoginPanel.setLayout(new FlowLayout());
					failLoginPanel.add(failLoginLabel);
					frame.add(failLoginPanel);
					frame.setVisible(true);
				} else {
					// success to login
					frame.dispose();
					ChatUser user = new ChatUser();
					user.setId(Integer.parseInt(nameField.getText()));
					user.setName(result);
					client.setUser(user);
					new ClientViewController(client).lauch();
				}
			} catch(ConnectException e1) {
				
				e1.printStackTrace();
			} catch(UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				client.close();
				// 5. fail to login
				JPanel failLoginPanel = new JPanel();
				failLoginPanel.setBounds(0, HEIGHT-100, WIDTH, 40);
				JLabel failLoginLabel = new JLabel("<html>fail to login, please check your network <br/>to login again!<html>");
				failLoginPanel.setLayout(new FlowLayout());
				failLoginPanel.add(failLoginLabel);
				frame.add(failLoginPanel);
				frame.setVisible(true);
				e1.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
		new LoginViewController();
	}
}
