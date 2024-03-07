package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.Input;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Input inputData = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePath1),
                Input.class);

        ArrayNode output = objectMapper.createArrayNode();

        //TODO add here the entry point to your implementation

        Statistics.resetStatistics();


        for (int gameNumber = 0; gameNumber < inputData.getGames().size(); gameNumber++) {
            //for every game, i create my decks from the input by using deepcopy
            Statistics.setTotalGamesPlayed();
            Game_implementation game_implement = new Game_implementation();

            ArrayList<Cards_class> deckPlayer1 = new ArrayList<>();
            ArrayList<Cards_class> deckPlayer2 = new ArrayList<>();

            int index = 0;
            deckPlayer1 =  game_implement.choseDeckPlayer(inputData, index, gameNumber);
            index++;
            deckPlayer2 = game_implement.choseDeckPlayer(inputData, index, gameNumber);

            int seed = inputData.getGames().get(gameNumber).getStartGame().getShuffleSeed();

            // shuffle both chosen decks using the given seed
            Collections.shuffle(deckPlayer1, new Random(seed));
            Collections.shuffle(deckPlayer2, new Random(seed));

            // start reading comands
            ActionsClass actions = new ActionsClass();
            actions.actions(inputData, deckPlayer1, deckPlayer2, output, gameNumber);
        }



        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }
}
