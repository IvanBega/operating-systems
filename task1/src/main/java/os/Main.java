package os;

import java.io.*;

/*
Java, процеси (напр. ProcessBuilder),
перенаправлення вводу-виводу для передачі результатів обчислень, блокуючий ввод-вивід
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Manager manager = new Manager();
        manager.start();
    }
}
