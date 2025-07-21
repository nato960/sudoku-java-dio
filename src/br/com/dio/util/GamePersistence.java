package br.com.dio.util;

import br.com.dio.model.Board;
import br.com.dio.model.Space;

import java.io.*;

import static br.com.dio.model.GameStatusEnum.INCOMPLETE;


public class GamePersistence {

    private static final String SAVE_FILE = "data/sudoku-save.txt";
    private static final String BASE_FILE = "data/sudoku-base-game.txt";

    static {
        File dir = new File("data");

        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) System.out.println("Diretório 'data/' criado com sucesso.");
        }
    }

    public static void saveGame(Board board) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    Space s = board.getSpaces().get(row).get(col);
                    if (s.getActual() != null) {
                        writer.write(col + "," + row + ";" + s.getActual() + "," + s.isFixed() + " ");
                    }
                }
            }

            writer.newLine();
            System.out.println("Jogo salvo com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao salvar o jogo: " + e.getMessage());
        }
    }

    public static Board loadGame() {
        if (!new File(SAVE_FILE).exists()) {
            System.out.println("Arquivo de save não encontrado: " + SAVE_FILE);
            return null;
        }

        int[][] values = new int[9][9];
        boolean[][] fixed = new boolean[9][9];

        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            String[] entries = reader.readLine().trim().split(" ");
            for (String entry : entries) {
                String[] coordAndValue = entry.split(";");
                String[] coord = coordAndValue[0].split(",");
                String[] value = coordAndValue[1].split(",");

                if ("null".equals(value[0])) continue;

                int col = Integer.parseInt(coord[0]);
                int row = Integer.parseInt(coord[1]);
                int val = Integer.parseInt(value[0]);
                boolean isFixed = Boolean.parseBoolean(value[1]);

                values[row][col] = val;
                fixed[row][col] = isFixed;
            }

            System.out.println("Jogo carregado com sucesso!");
            Board board = new Board(values, fixed);
            board.setStatus(INCOMPLETE);
            return board;

        } catch (IOException e) {
            System.out.println("Erro ao carregar o jogo: " + e.getMessage());
            return null;
        }
    }


    public static int[][] readBaseBoard(boolean[][] fixedPositions) {
        int[][] board = new int[9][9];
        File file = new File(BASE_FILE);

        if (!file.exists()) {
            System.out.println("Arquivo de base não encontrado: " + BASE_FILE);
            return board;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String[] entries = reader.readLine().trim().split(" ");

            for (String entry : entries) {
                String[] coordAndValue = entry.split(";");
                String[] coord = coordAndValue[0].split(",");
                String[] value = coordAndValue[1].split(",");

                int col = Integer.parseInt(coord[0]);
                int row = Integer.parseInt(coord[1]);
                int val = Integer.parseInt(value[0]);
                boolean fixed = Boolean.parseBoolean(value[1]);

                if (fixed && val != 0) {
                    board[row][col] = val;
                    fixedPositions[row][col] = true;
                }
            }

            System.out.println("Tabuleiro base carregado com sucesso!");

        } catch (IOException e) {
            System.out.println("Erro ao carregar tabuleiro base: " + e.getMessage());
        }
        return board;
    }
}

