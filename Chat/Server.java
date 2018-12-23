package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ConsoleHelper.writeMessage("Введите номер порта");
        int port = ConsoleHelper.readInt();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ConsoleHelper.writeMessage("Сервер запущен");
            for (;;) {
                Socket socket = serverSocket.accept();
                Handler handler = new Handler(socket);
                handler.start();
            }
        } catch (Exception e) {
            ConsoleHelper.writeMessage("Ошибка соединения");
        }
    }

    public static void sendBroadcastMessage(Message message) {
        try {
            for (Connection connection : connectionMap.values())
                connection.send(message);
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Сообщение не отправлено");
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            String clientName;
            while(true) {
                boolean isFound = false;
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message answer = connection.receive();
                if (answer.getType().equals(MessageType.USER_NAME) && !answer.getData().isEmpty()) {

                    if (connectionMap.containsKey(answer.getData()))
                        isFound = true;
                    if (isFound)
                        continue;

                    connectionMap.put(answer.getData(), connection);
                    connection.send(new Message(MessageType.NAME_ACCEPTED));
                    clientName = answer.getData();
                    break;
                }
            }
            return clientName;
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for (String name : connectionMap.keySet()) {
                if (name.equals(userName))
                    continue;
                connection.send(new Message(MessageType.USER_ADDED, name));
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    message = new Message(MessageType.TEXT, userName + ": " + message.getData());
                    sendBroadcastMessage(message);
                }
                else
                    ConsoleHelper.writeMessage("Ошибка");
            }
        }
        
        public void run() {
            String address = socket.getRemoteSocketAddress().toString();
            ConsoleHelper.writeMessage("Установлено новое соединение с удаленным адресом " + address);
            
            try (Connection connection = new Connection(socket)) {
                String userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                sendListOfUsers(connection, userName);
                serverMainLoop(connection, userName);
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
                ConsoleHelper.writeMessage("Соединение с удаленным адресом закрыто");
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Ппроизошла ошибка при обмене данными с удаленным адресом");
            }
        }
    }
}
