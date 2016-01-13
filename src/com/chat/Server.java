package com.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.model.ChatUser;

public class Server {
	
	private ServerSocket server = null;
	private Socket s = null;
	private boolean isStarted = false;
	private List< Client> clientList = null;

	private List<ChatUser> users = null;
	
	public static void main(String[] args)  {
		new Server().start();
	}
	
	public Server() {
		clientList = new ArrayList<Client>();
		testData();
	}
	
	public void start() {
		try {
			server = new ServerSocket(8001);
			System.out.println("the server start!");
			isStarted = true;
			while(isStarted) {
				s = server.accept();
				Client client = new Client(s);
				new Thread(new HandleClientThread(client, clientList)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	private void close() {
		if(!server.isClosed()) {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public ChatUser validate(String message) {
		ChatUser user = null;
		for(ChatUser u : users) {
			if(message.equals("[{name: " + u.getId() + ", password: " + u.getPassword() + "}]")) {
				user = u;
				break;
			}
		}
		return user;
	}
	
	public void testData() {
		users = new ArrayList<ChatUser>();
		ChatUser user1 = new ChatUser();
		user1.setId(135);
		user1.setName("wang");
		user1.setPassword("123");
		users.add(user1);
		ChatUser user2 = new ChatUser();
		user2.setId(182);
		user2.setName("Xiong");
		user2.setPassword("1234");
		users.add(user2);
		ChatUser user3 = new ChatUser();
		user3.setId(136);
		user3.setName("gong");
		user3.setPassword("12345");
		users.add(user3);
	}
	
	private class HandleClientThread implements Runnable {
		
		private Client client = null;
		private boolean isConnected = false;
		private String message =  "";
		
		private List<Client> clientList = null;
		
		public HandleClientThread(Client client, List<Client> clientList) {
			this.client = client;
			this.clientList = clientList;
		}
		@Override
		public void run() {
			try {
				message = client.receive();
System.out.println("message: " + message);
				if(null != validate(message)) {
					client.setUser(validate(message));
					clientList.add(client);
System.out.println("a Client: " + client.getId() + " connected");
					// send success to client
					client.send(client.getName());
					for(int i=0; i<clientList.size(); i++) {
						Client c = clientList.get(i);
						if(c != client) {
							c.send("\t\t " + client.getName() + "  加入聊天室.");
							System.out.println(message);
						}
					}
					isConnected = true;
				} else {
					client.send("failed");
					isConnected = false;
				}
			} catch (IOException e1) {
				client.close();
				isConnected = false;
				e1.printStackTrace();
			}			
			try {
				while(isConnected) {
					message = client.receive();
						if(null !=message && !"".equals(message)) {
							for(int i=0; i<clientList.size(); i++) {
								Client c = clientList.get(i);
								if(c != client) {
									c.send(client.getName() + "  says: " + message);
									System.out.println(message);
								}
							}
						}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
					for(int i=0; i<clientList.size(); i++) {
						Client c = clientList.get(i);
						if(c == client) {
							clientList.remove(i);
						}
					}
					for(int i=0; i<clientList.size(); i++) {
						Client c = clientList.get(i);
						if(c != client) {
							try {
								c.send("\t\t " + client.getName() + "  退出聊天室.");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				client.close();
			}
		}
	}
}
