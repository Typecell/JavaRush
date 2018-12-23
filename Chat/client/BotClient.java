package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BotClient extends Client {

    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    protected String getUserName() {
        double r = Math.random() * 100;
        int x = (int) r;
        return "date_bot_" + x;
    }

    public class BotSocketThread extends SocketThread {

        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            if (message.contains(": ")) {
                String username = message.split(": ")[0];
                String text = message.split(": ")[1];
                Calendar calendar = new GregorianCalendar();
                Date date = calendar.getTime();

                if (text.equals("дата")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.YYYY");
                    sendTextMessage("Информация для "+ username + ": " + dateFormat.format(date));
                }
                else if (text.equals("день")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("d");
                    sendTextMessage("Информация для "+ username + ": " + dateFormat.format(date));
                }
                else if (text.equals("месяц")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
                    sendTextMessage( "Информация для "+ username + ": " + dateFormat.format(date));
                }
                else if (text.equals("год")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY");
                    sendTextMessage( "Информация для "+ username + ": " + dateFormat.format(date));
                }
                else if (text.equals("время")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("H:mm:ss");
                    sendTextMessage( "Информация для "+ username + ": " + dateFormat.format(date));
                }
                else if (text.equals("час")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("H");
                    sendTextMessage( "Информация для "+ username + ": " + dateFormat.format(date));
                }
                else if (text.equals("минуты")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("m");
                    sendTextMessage( "Информация для "+ username + ": " + dateFormat.format(date));
                }
                else if (text.equals("секунды")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("s");
                    sendTextMessage( "Информация для "+ username + ": " + dateFormat.format(date));
                }
            }
        }

    }

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }
}
