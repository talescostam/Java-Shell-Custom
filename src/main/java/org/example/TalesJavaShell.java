package org.example;

import java.io.*;
import java.util.*;

public class TalesJavaShell {
    public static void main(String[] args) throws java.io.IOException {
        String commandLine;
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        List<String> history = new ArrayList<>();
        File currentDir = new File(System.getProperty("user.dir"));

        while (true) {
            System.out.print("Diretório atual: " + currentDir.getAbsolutePath() + "> ");
            commandLine = console.readLine();

            if (commandLine.equals("")) continue;

            history.add(commandLine);
            String[] tokens = commandLine.split(" ");
            List<String> command = new ArrayList<>(Arrays.asList(tokens));

            if (command.get(0).equals("cd")) {
                if (command.size() > 1) {
                    String path = command.get(1);
                    if (path.equals("..")) {
                        currentDir = currentDir.getParentFile();
                    } else {
                        File newDir = new File(currentDir, command.get(1));
                        if (newDir.exists() && newDir.isDirectory()) {
                            currentDir = newDir;
                        } else {
                            System.err.println("Diretório inválido: " + command.get(1));
                        }
                    }
                } else {
                    currentDir = new File(System.getProperty("user.home"));
                }
                continue;
            }

            if (command.get(0).equals("ls")) {
                System.out.println("Diretório atual: " + currentDir.getAbsolutePath());
                File[] files = currentDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        System.out.println((file.isDirectory() ? "Pasta " : "Arquivo ") + file.getName());
                    }
                }
                continue;
            }

            if (command.get(0).equals("history")) {
                for (int i = 0; i < history.size(); i++) {
                    System.out.println(i + " " + history.get(i));
                }
                continue;
            }

            if (command.get(0).equals("!!")) {
                if (history.size() < 2) {
                    System.err.println("Nenhum comando anterior no histórico.");
                    continue;
                }
                commandLine = history.get(history.size() - 2);
                tokens = commandLine.split(" ");
                command = new ArrayList<>(Arrays.asList(tokens));
            } else if (command.get(0).startsWith("!")) {
                try {
                    int index = Integer.parseInt(command.get(0).substring(1));
                    if (index < 0 || index >= history.size()) {
                        System.err.println("Índice do histórico inválido.");
                        continue;
                    }
                    commandLine = history.get(index);
                    tokens = commandLine.split(" ");
                    command = new ArrayList<>(Arrays.asList(tokens));
                } catch (NumberFormatException e) {
                    System.err.println("Formato de comando de histórico inválido.");
                    continue;
                }
            }

            try {
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.directory(currentDir);
                Process process = pb.start();

                InputStream is = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                System.err.println("Erro ao executar comando: " + e.getMessage());
            }
        }
    }
}
