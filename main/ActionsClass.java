package main;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.Coordinates;
import fileio.Input;
import fileio.ActionsInput;

import java.util.ArrayList;


public final class ActionsClass {

    public ActionsClass() {
    }

    /** function to get every command*/
    public void actions(final Input input, final ArrayList<Cards_class> deckPlayer1,
                        final ArrayList<Cards_class> deckPlayer2,
                        final ArrayNode output, final int numberGame) {
        ObjectMapper objectMapper = new ObjectMapper();

        // create my game table, which can only have Minions
        MinionCard[][] gameTable = new MinionCard[MagicNumbers.NR_ROWS][MagicNumbers.NR_COLUMNS];

        // create player hands, player mana
        Hand hands = new Hand();
        Game_implementation gameImplement = new Game_implementation();

        // add the first card in every players hand
        hands.addCardsInHand1(hands, deckPlayer1);
        hands.addCardsInHand2(hands, deckPlayer2);
        Hero playerOneHero = new Hero(input.getGames().get(numberGame).
                getStartGame().getPlayerOneHero());
        Hero playerTwoHero = new Hero(input.getGames().get(numberGame).
                getStartGame().getPlayerTwoHero());

        // save the heroes for both players
        hands.setHeroPlayer1(playerOneHero);
        hands.setHeroPlayer2(playerTwoHero);

        // set the starting player
        Game_implementation.playerTurn = input.getGames().get(numberGame).
                getStartGame().getStartingPlayer();
        Game_implementation.resetTurns();

        ArrayList<ActionsInput> actions = new ArrayList<>();
        actions = input.getGames().get(numberGame).getActions();

        for (ActionsInput actionsArr : actions) {

            if (actionsArr.getCommand().equals("getPlayerDeck")) {
                // add to output the deck of the player chosen
                int playerIdx = actionsArr.getPlayerIdx();
                if (playerIdx == 1) {
                    ObjectNode node = objectMapper.createObjectNode();
                    node.put("command", actionsArr.getCommand());
                    node.put("playerIdx", playerIdx);
                    node.putPOJO("output", deckPlayer1.toArray());
                    output.add(node);
                } else if (playerIdx == 2) {
                    ObjectNode node = objectMapper.createObjectNode();
                    node.put("command", actionsArr.getCommand());
                    node.put("playerIdx", playerIdx);
                    node.putPOJO("output", deckPlayer2.toArray());
                    output.add(node);
                }
            } else if (actionsArr.getCommand().equals("getPlayerHero")) {
                // gt the hero of the chosen player
                int playerIdx = actionsArr.getPlayerIdx();
                if (playerIdx == 1) {
                    Hero heroAux = new Hero(hands.getHeroPlayer1());
                    ObjectNode node = objectMapper.createObjectNode();
                    node.put("command", actionsArr.getCommand());
                    node.put("playerIdx", playerIdx);
                    node.putPOJO("output", heroAux);
                    output.add(node);
                } else if (playerIdx == 2) {
                    Hero heroAux = new Hero(hands.getHeroPlayer2());
                    ObjectNode node = objectMapper.createObjectNode();
                    node.put("command", actionsArr.getCommand());
                    node.put("playerIdx", playerIdx);
                    node.putPOJO("output", heroAux);
                    output.add(node);
                }

            } else if (actionsArr.getCommand().equals("getPlayerTurn")) {
                //get thhe player that is currently at turn
                ObjectNode node = objectMapper.createObjectNode();
                node.put("command", actionsArr.getCommand());
                node.put("output", Game_implementation.playerTurn);
                output.add(node);
            } else if (actionsArr.getCommand().equals("placeCard")) {
                int handIdx = actionsArr.getHandIdx();
                if (Game_implementation.playerTurn == 1) {
                    if (handIdx >= hands.getHandPlayerOne().size()) {
                        continue;
                    }
                    if (hands.getHandPlayerOne().isEmpty()) {
                        continue;
                    } else if (hands.getHandPlayerOne().size() != 0) {
                        // get the name of the card that must be placed
                        String environment = hands.getHandPlayerOne().get(handIdx).name;

                        if (environment.equals("Firestorm")
                                || environment.equals("Winterfell")
                                || environment.equals("Heart Hound")) {
                            // in case it is enbvironment
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("command", actionsArr.getCommand());
                            node.put("handIdx", handIdx);
                            node.put("error", "Cannot place environment card on table.");
                            output.add(node);
                            continue;
                        } else {
                            int manaAux = hands.getManaPlayerOne();
                            if (manaAux < hands.getHandPlayerOne().get(handIdx).mana) {
                                // if the player does not have enough mana
                                ObjectNode node = objectMapper.createObjectNode();
                                node.put("command", actionsArr.getCommand());
                                node.put("handIdx", handIdx);
                                node.put("error", "Not enough mana to place card on table.");
                                output.add(node);
                                continue;
                            } else {
                                String nameCard = hands.getHandPlayerOne().get(handIdx).name;
                                if (nameCard.equals("The Ripper")
                                        || nameCard.equals("Miraj")
                                        || nameCard.equals("Goliath")
                                        || nameCard.equals("Warden")) {
                                    // place the card where it belongs
                                    int line = 2;
                                    Table table = new Table();
                                    //create a new card to place on table
                                    MinionCard cardAux = new MinionCard((MinionCard) hands.
                                            getHandPlayerOne().get(handIdx));
                                    // check if last place of a row is empty, which means there is
                                    // room for the card
                                    if (gameTable[line][MagicNumbers.NR_ROWS] != null) {
                                        // if the last place is not null, the row is full
                                        ObjectNode node = objectMapper.createObjectNode();
                                        node.put("command", actionsArr.getCommand());
                                        node.put("handIdx", handIdx);
                                        node.put("error", "Cannot place "
                                                + "card on table since row is full.");
                                        output.add(node);
                                    } else {
                                        // add the card to the table
                                        table.addCardToTable(gameTable,
                                                line, cardAux);
                                        // decrease the players mana
                                        hands.minusManaPlayerOne(hands.
                                                getHandPlayerOne().
                                                get(handIdx).mana);
                                        // remove the card from the players hand
                                        hands.getHandPlayerOne().
                                                remove(handIdx);
                                    }

                                } else if (nameCard.equals("Sentinel")
                                        || nameCard.equals("Berserker")
                                        || nameCard.equals("The Cursed One")
                                        || nameCard.equals("Disciple")) {
                                    // the back row
                                    int line = MagicNumbers.LAST;
                                    Table table = new Table();
                                    MinionCard cardAux = new
                                            MinionCard((MinionCard) hands.
                                            getHandPlayerOne().get(handIdx));
                                    if (gameTable[line][MagicNumbers.NR_ROWS]
                                            != null) {
                                        ObjectNode node = objectMapper.
                                                createObjectNode();
                                        node.put("command", actionsArr.
                                                getCommand());
                                        node.put("handIdx", handIdx);
                                        node.put("error", "Cannot place "
                                              + "card on table since row is full.");
                                        output.add(node);
                                    } else {
                                        table.addCardToTable(gameTable,
                                                line, cardAux);
                                        hands.minusManaPlayerOne(cardAux.mana);
                                        hands.getHandPlayerOne().
                                                remove(handIdx);
                                    }
                                }
                            }
                        }
                    }

                } else if (Game_implementation.playerTurn == 2) {
                    if (hands.getHandPlayerTwo().isEmpty()) {
                        continue;
                    } else if (hands.getHandPlayerTwo().size() != 0) {
                        String environment = hands.getHandPlayerTwo().
                                get(handIdx).name;
                        if (environment.equals("Firestorm")
                                || environment.equals("Winterfell")
                                || environment.equals("Heart Hound")) {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("command", actionsArr.getCommand());
                            node.put("handIdx", handIdx);
                            node.put("error", "Cannot place "
                                    + "environment card on table.");
                            output.add(node);
                            continue;
                        } else {
                            if (hands.getHandPlayerTwo().get(handIdx).mana
                                    > hands.getManaPlayerTwo()) {
                                ObjectNode node = objectMapper.createObjectNode();
                                node.put("command", actionsArr.getCommand());
                                node.put("handIdx", handIdx);
                                node.put("error", "Not enough mana "
                                        + "to place card on table");
                                output.add(node);
                                continue;
                            } else {
                                if (handIdx >= hands.getHandPlayerTwo().size()) {
                                    ObjectNode node = objectMapper.createObjectNode();
                                    node.put("command", actionsArr.getCommand());
                                    node.put("handIdx", handIdx);
                                    node.put("error", "Cannot place card "
                                            + "on table since row is full");
                                    output.add(node);
                                    continue;
                                }
                                String nameCard = hands.getHandPlayerTwo().get(handIdx).name;
                                if (nameCard.equals("The Ripper")
                                        || nameCard.equals("Miraj")
                                        || nameCard.equals("Goliath")
                                        || nameCard.equals("Warden")) {
                                    // front row
                                    int line = 1;
                                    Table table = new Table();
                                    MinionCard cardAux = new MinionCard((MinionCard) hands.
                                            getHandPlayerTwo().get(handIdx));
                                    if (gameTable[line][MagicNumbers.NR_ROWS] != null) {
                                        ObjectNode node = objectMapper.createObjectNode();
                                        node.put("command", actionsArr.getCommand());
                                        node.put("handIdx", handIdx);
                                        node.put("error", "Cannot place card "
                                                + "on table since row is full.");
                                        output.add(node);
                                    } else {
                                        table.addCardToTable(gameTable, line, cardAux);
                                        hands.minusManaPlayerTwo(hands.
                                                getHandPlayerTwo().get(handIdx).mana);
                                        hands.getHandPlayerTwo().remove(handIdx);
                                    }
                                } else if (nameCard.equals("Sentinel")
                                        || nameCard.equals("Berserker")
                                        || nameCard.equals("The Cursed One")
                                        || nameCard.equals("Disciple")) {
                                    //the back row
                                    int line = 0;
                                    Table table = new Table();
                                    MinionCard cardAux = new MinionCard((MinionCard) hands.
                                            getHandPlayerTwo().get(handIdx));
                                    if (gameTable[line][MagicNumbers.NR_ROWS] != null) {
                                        ObjectNode node = objectMapper.createObjectNode();
                                        node.put("command", actionsArr.getCommand());
                                        node.put("handIdx", handIdx);
                                        node.put("error", "Cannot place card "
                                                + "on table since row is full.");
                                        output.add(node);
                                    } else {
                                        table.addCardToTable(gameTable, line, cardAux);
                                        hands.minusManaPlayerTwo(hands.
                                                getHandPlayerTwo().get(handIdx).mana);
                                        hands.getHandPlayerTwo().remove(handIdx);
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (actionsArr.getCommand().equals("endPlayerTurn")) {
                // when an player ends his turn, the frozen index of cards
                // becomes 2, next turn it will get unfrozen
                for (int i = 0; i < MagicNumbers.NR_ROWS; i++) {
                    for (int j = 0; j < MagicNumbers.NR_COLUMNS; j++) {
                        if (Hand.getFrozenRows()[i][j] != 0) {
                            Hand.frozeCards(i, j);
                        }
                    }
                }
                // if the hero has attacked during the turn
                for (int i = 0; i < 2; i++) {
                    if (Hero.getHeroAttack()[i] != 0) {
                        Hero.setHeroAttack(i);
                    }
                }

                if (Game_implementation.getTurns() % 2 == 0) {
                    // if a round is not over
                    // change player turn
                    if (Game_implementation.playerTurn == 2) {
                        Game_implementation.playerTurn = 1;
                    } else {
                        Game_implementation.playerTurn = 2;
                    }
                } else if (Game_implementation.getTurns() % 2 == 1) {
                    // if the round is over
                    // give mana
                    if ((Game_implementation.getTurns() + 1) / 2 >= MagicNumbers.TURNS) {
                        hands.setManaPlayerOne(MagicNumbers.TURNS);
                        hands.setManaPlayerTwo(MagicNumbers.TURNS);
                    } else {
                        int nrAux = Game_implementation.getTurns();
                        hands.setManaPlayerOne((nrAux + MagicNumbers.
                                ADD) / 2);
                        hands.setManaPlayerTwo((nrAux + MagicNumbers.
                                ADD) / 2);
                    }
                    //change turns
                    if (Game_implementation.playerTurn == 2) {
                        Game_implementation.playerTurn = 1;
                    } else {
                        Game_implementation.playerTurn = 2;
                    }
                    hands.addCardsInHand1(hands, deckPlayer1);
                    hands.addCardsInHand2(hands, deckPlayer2);
                }
                Game_implementation.setTurns();
                // defroze the card if neccesary
                for (int i = 0; i < MagicNumbers.NR_ROWS; i++) {
                    for (int j = 0; j < MagicNumbers.NR_COLUMNS; j++) {
                        if (Hand.getFrozenRows()[i][j] == MagicNumbers.
                                DEFROZE) {
                            Hand.defrozeCards(i, j);
                        }
                    }

                }
                Hand.endAttack();

                // reset hero attack
                for (int i = 0; i < 2; i++) {
                    if (Hero.getHeroAttack()[i] == MagicNumbers.DEFROZE) {
                        Hero.resetHeroAttack(i);
                    }
                }

            } else if (actionsArr.getCommand().equals("getCardsInHand")) {
                int playerIndex = actionsArr.getPlayerIdx();
                if (playerIndex == 1) {

                    // i create a new list of cards
                    ArrayList<Cards_class> copyCardHand = new ArrayList<>();
                    for (int i = 0; i < hands.getHandPlayerOne().size(); i++) {
                        String nameCard = hands.getHandPlayerOne().get(i).name;
                        // depending on the name, i create an environment or a
                        // minion card, copy every card from the hand and
                        // add the new card to the new list
                        if (nameCard.equals("Firestorm")
                                || nameCard.equals("Winterfell")
                                || nameCard.equals("Heart Hound")) {
                            Environment_card copyEnvironment = new
                                    Environment_card((Environment_card) hands.
                                    getHandPlayerOne().get(i));
                            copyCardHand.add(copyEnvironment);
                        } else {
                            MinionCard copyMinion = new
                                    MinionCard((MinionCard) hands.
                                    getHandPlayerOne().get(i));
                            copyCardHand.add(copyMinion);
                        }
                    }
                    ObjectNode node = objectMapper.createObjectNode();
                    node.put("command", actionsArr.getCommand());
                    node.put("playerIdx", playerIndex);
                    node.putPOJO("output", copyCardHand.toArray());
                    output.add(node);
                } else if (playerIndex == 2) {
                    ArrayList<Cards_class> copyCardHand = new ArrayList<>();
                    for (int i = 0; i < hands.getHandPlayerTwo().size(); i++) {
                        String nameCard = hands.getHandPlayerTwo().get(i).name;
                        if (nameCard.equals("Firestorm")
                                || nameCard.equals("Winterfell")
                                || nameCard.equals("Heart Hound")) {
                            Environment_card copyEnvironment = new
                                    Environment_card((Environment_card) hands.
                                    getHandPlayerTwo().get(i));
                            copyCardHand.add(copyEnvironment);
                        } else {
                            MinionCard copyMinion = new
                                    MinionCard((MinionCard) hands.
                                    getHandPlayerTwo().get(i));
                            copyCardHand.add(copyMinion);
                        }
                    }
                    ObjectNode node = objectMapper.createObjectNode();
                    node.put("command", actionsArr.getCommand());
                    node.put("playerIdx", playerIndex);
                    node.putPOJO("output", copyCardHand.toArray());
                    output.add(node);
                }
            } else if (actionsArr.getCommand().equals("getCardsOnTable")) {
                // create an array of arrays which stores the cards from the table

                // first array
                ArrayList<Cards_class> matrixAux1 = new ArrayList<>();

                ObjectNode node = objectMapper.createObjectNode();
                node.put("command", actionsArr.getCommand());
                int i = 0;
                for (int j = 0; j < MagicNumbers.NR_COLUMNS; j++) {
                    if (gameTable[i][j] != null) {
                        MinionCard newMinion = new MinionCard(gameTable[i][j]);
                        matrixAux1.add(newMinion);
                    }
                }
                i++;
                // second array
                ArrayList<Cards_class> matrixAux2 = new ArrayList<>();
                for (int j = 0; j < MagicNumbers.NR_COLUMNS; j++) {
                    if (gameTable[i][j] != null) {
                        MinionCard newMinion = new MinionCard(gameTable[i][j]);
                        matrixAux2.add(newMinion);
                    }
                }
                i++;
                // third array
                ArrayList<Cards_class> matrixAux3 = new ArrayList<>();
                for (int j = 0; j < MagicNumbers.NR_COLUMNS; j++) {
                    if (gameTable[i][j] != null) {
                        MinionCard newMinion = new MinionCard(gameTable[i][j]);
                        matrixAux3.add(newMinion);
                    }
                }
                i++;
                // fourth array
                ArrayList<Cards_class> matrixAux4 = new ArrayList<>();
                for (int j = 0; j < MagicNumbers.NR_COLUMNS; j++) {
                    if (gameTable[i][j] != null) {
                        MinionCard newMinion = new MinionCard(gameTable[i][j]);
                        matrixAux4.add(newMinion);
                    }
                }
                //create the array of arrays
                ArrayList<ArrayList<Cards_class>> matrixOut = new ArrayList<>();
                // add the four arrays
                matrixOut.add(matrixAux1);
                matrixOut.add(matrixAux2);
                matrixOut.add(matrixAux3);
                matrixOut.add(matrixAux4);
                // print the array of arrays
                node.putPOJO("output", matrixOut);

                output.add(node);
            } else if (actionsArr.getCommand().equals("getPlayerMana")) {
                int playerIndex = actionsArr.getPlayerIdx();
                if (playerIndex == 1) {
                    // mana player 1
                    ObjectNode node = objectMapper.createObjectNode();
                    node.put("command", actionsArr.getCommand());
                    node.put("playerIdx", playerIndex);
                    node.put("output", hands.getManaPlayerOne());
                    output.add(node);
                } else if (playerIndex == 2) {
                    //mana player 2
                    ObjectNode node = objectMapper.createObjectNode();
                    node.put("command", actionsArr.getCommand());
                    node.put("playerIdx", playerIndex);
                    node.put("output", hands.getManaPlayerTwo());
                    output.add(node);
                }
            } else if (actionsArr.getCommand().equals("useEnvironmentCard")) {
                // card index
                int handIdx = actionsArr.getHandIdx();
                // row to attack
                int row = actionsArr.getAffectedRow();
                if  (Game_implementation.playerTurn == 1) {
                    // get name of the card
                    String nameCard = hands.getHandPlayerOne().
                            get(handIdx).name;
                    if (nameCard.equals("Firestorm")
                            || nameCard.equals("Winterfell")
                            || nameCard.equals("Heart Hound")) {
                        int manaAux = hands.getManaPlayerOne();
                        if (manaAux < hands.getHandPlayerOne().
                                get(handIdx).mana) {
                            // if there is not enough mana
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("affectedRow", row);
                            node.put("command", actionsArr.getCommand());
                            node.put("handIdx", handIdx);
                            node.put("error", "Not enough mana "
                                    + "to use environment card.");
                            output.add(node);
                            continue;
                        }
                        if (row == 2 || row == MagicNumbers.LAST) {
                            // if the chosen row does not belong to the enemy
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("affectedRow", row);
                            node.put("command", actionsArr.getCommand());
                            node.put("handIdx", handIdx);
                            node.put("error", "Chosen row does "
                                    + "not belong to the enemy.");
                            output.add(node);
                            continue;
                        }
                        // verify if the ability can be used
                        Environment_card environment = new
                                Environment_card((Environment_card) hands.
                                getHandPlayerOne().get(handIdx));
                        int verify = environment.specialAbility(environment,
                                gameTable, row);
                        if (verify == 1) {
                            // if the ability can be used, decrease mana
                            hands.minusManaPlayerOne(hands.getHandPlayerOne().
                                    get(handIdx).mana);
                            //remove the card
                            hands.getHandPlayerOne().remove(handIdx);
                            // check if there is no minion that needs to be deleted
                            hands.checkIfNoHp(gameTable);
                        } else {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("affectedRow", row);
                            node.put("command", actionsArr.getCommand());
                            node.put("handIdx", handIdx);
                            node.put("error", "Cannot steal enemy card "
                                    + "since the player's row is full.");
                            output.add(node);
                        }

                    } else {
                        // if the chosen card is of type environment
                        ObjectNode node = objectMapper.createObjectNode();
                        node.put("affectedRow", row);
                        node.put("command", actionsArr.getCommand());
                        node.put("handIdx", handIdx);
                        node.put("error", "Chosen card is not "
                                + "of type environment.");
                        output.add(node);
                    }
                } else if (Game_implementation.playerTurn == 2) {
                    // if the current player is number 2
                    String nameCard = hands.getHandPlayerTwo().get(handIdx).name;
                    if (nameCard.equals("Firestorm")
                            || nameCard.equals("Winterfell")
                            || nameCard.equals("Heart Hound")) {
                        int manaAux = hands.getManaPlayerTwo();
                        if (manaAux < hands.getHandPlayerTwo().get(handIdx).mana) {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("affectedRow", row);
                            node.put("command", actionsArr.getCommand());
                            node.put("handIdx", handIdx);
                            node.put("error", "Not enough mana "
                                    + "to use environment card.");
                            output.add(node);
                            continue;
                        }
                        if (row == 0 || row == 1) {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("affectedRow", row);
                            node.put("command", actionsArr.getCommand());
                            node.put("handIdx", handIdx);
                            node.put("error", "Chosen row does not "
                                    + "belong to the enemy.");
                            output.add(node);
                            continue;
                        }
                        Environment_card environment = new
                                Environment_card((Environment_card) hands.
                                getHandPlayerTwo().get(handIdx));
                        int verify = environment.specialAbility(environment, gameTable, row);
                        if (verify == 1) {
                            hands.minusManaPlayerTwo(hands.getHandPlayerTwo().
                                    get(handIdx).mana);
                            hands.getHandPlayerTwo().remove(handIdx);
                            hands.checkIfNoHp(gameTable);
                        } else {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("affectedRow", row);
                            node.put("command", actionsArr.getCommand());
                            node.put("handIdx", handIdx);
                            node.put("error", "Cannot steal enemy "
                                    + "card since the player's row is full.");
                            output.add(node);
                        }
                    } else {
                        ObjectNode node = objectMapper.createObjectNode();
                        node.put("affectedRow", row);
                        node.put("command", actionsArr.getCommand());
                        node.put("handIdx", handIdx);
                        node.put("error", "Chosen card "
                                + "is not of type environment.");
                        output.add(node);
                    }
                }
            } else if (actionsArr.getCommand().
                    equals("getEnvironmentCardsInHand")) {
                int playerIndex = actionsArr.getPlayerIdx();
                ArrayList<Environment_card> environmentArray = new ArrayList<>();
                if (playerIndex == 1) {
                    for (int  i = 0; i < hands.getHandPlayerOne().size(); i++) {
                        String nameCard = hands.getHandPlayerOne().get(i).name;
                        if (nameCard.equals("Firestorm")
                                || nameCard.equals("Winterfell")
                                || nameCard.equals("Heart Hound")) {
                            Environment_card environmentCard = new
                                    Environment_card((Environment_card) hands.
                                    getHandPlayerOne().get(i));
                            environmentArray.add(environmentCard);
                        }
                    }
                } else if (playerIndex == 2) {
                    for (int  i = 0; i < hands.getHandPlayerTwo().size(); i++) {
                        String nameCard = hands.getHandPlayerTwo().get(i).name;
                        if (nameCard.equals("Firestorm")
                                || nameCard.equals("Winterfell")
                                || nameCard.equals("Heart Hound")) {
                            Environment_card environmentCard = new
                                    Environment_card((Environment_card) hands.
                                    getHandPlayerTwo().get(i));
                            environmentArray.add(environmentCard);
                        }
                    }
                }
                ObjectNode node = objectMapper.createObjectNode();
                node.put("command", actionsArr.getCommand());
                node.put("playerIdx", playerIndex);
                node.putPOJO("output", environmentArray.toArray());
                output.add(node);
            } else if (actionsArr.getCommand().equals("getCardAtPosition")) {
                int xCoord = actionsArr.getX();
                int yCoord = actionsArr.getY();

                if (gameTable[xCoord][yCoord] != null) {
                    MinionCard minionAux = new MinionCard(gameTable
                            [xCoord][yCoord]);
                    ObjectNode node = objectMapper.createObjectNode();
                    node.put("command", actionsArr.getCommand());
                    node.putPOJO("output", minionAux);
                    node.put("x", xCoord);
                    node.put("y", yCoord);
                    output.add(node);
                } else {
                    ObjectNode node = objectMapper.createObjectNode();
                    node.put("command", actionsArr.getCommand());
                    node.put("output", "No card available "
                            + "at that position.");
                    node.put("x", xCoord);
                    node.put("y", yCoord);
                    output.add(node);
                }

            } else if (actionsArr.getCommand().
                    equals("getFrozenCardsOnTable")) {
                // create an array to store frozen cards
                ArrayList<MinionCard> frozenCards = new ArrayList<>();
                for (int i = 0; i < MagicNumbers.NR_ROWS; i++) {
                    for (int j = 0; j < MagicNumbers.NR_COLUMNS; j++) {
                        if (gameTable[i][j] != null
                                && Hand.getFrozenRows()[i][j] != 0) {
                            // if the card is not null and it is frozen
                            MinionCard aux = new MinionCard(gameTable[i][j]);
                            frozenCards.add(aux);
                        }
                    }
                }
                ObjectNode node = objectMapper.createObjectNode();
                node.put("command", actionsArr.getCommand());
                node.putPOJO("output", frozenCards);
                output.add(node);
            } else if (actionsArr.getCommand().equals("cardUsesAttack")) {
                // coordinates of attacker
                int xAttacker = actionsArr.getCardAttacker().getX();
                int yAttacker = actionsArr.getCardAttacker().getY();
                // coordinates of attacked
                int xAttacked = actionsArr.getCardAttacked().getX();
                int yAttacked = actionsArr.getCardAttacked().getY();

                if (Game_implementation.playerTurn == 2) {
                    if (xAttacked == 0 || xAttacked == 1) {
                        // if the card attacked does not belong to the enemy
                        ObjectNode node = objectMapper.createObjectNode();
                        node.put("command", actionsArr.getCommand());
                        Coordinates coord1 = actionsArr.getCardAttacker();
                        Coordinates coord2 = actionsArr.getCardAttacked();
                        node.putPOJO("cardAttacker", coord1);
                        node.putPOJO("cardAttacked", coord2);
                        node.put("error", "Attacked card does not "
                                + "belong to the enemy.");
                        output.add(node);
                        continue;
                    }
                } else if (Game_implementation.playerTurn == 1) {
                    if (xAttacked == 2 || xAttacked == MagicNumbers.LAST) {
                        // if the card attacked does not belong to the enemy
                        ObjectNode node = objectMapper.createObjectNode();
                        node.put("command", actionsArr.getCommand());
                        Coordinates coord1 = actionsArr.getCardAttacker();
                        Coordinates coord2 = actionsArr.getCardAttacked();
                        node.putPOJO("cardAttacker", coord1);
                        node.putPOJO("cardAttacked", coord2);
                        node.put("error", "Attacked card does "
                                + "not belong to the enemy.");
                        output.add(node);
                        continue;
                    }
                }

                if (Hand.getFrozenRows()[xAttacker][yAttacker] != 0) {
                    // if the attacker card is frozen
                    ObjectNode node = objectMapper.createObjectNode();
                    node.put("command", actionsArr.getCommand());
                    Coordinates coord1 = actionsArr.getCardAttacker();
                    Coordinates coord2 = actionsArr.getCardAttacked();
                    node.putPOJO("cardAttacker", coord1);
                    node.putPOJO("cardAttacked", coord2);
                    node.put("error", "Attacker card is frozen.");
                    output.add(node);
                    continue;
                } else {
                    if (Hand.getCardsWhoAttacked()[xAttacker][yAttacker] != 0) {
                        // if the card has already attacked
                        ObjectNode node = objectMapper.createObjectNode();
                        node.put("command", actionsArr.getCommand());
                        //get my coordinates
                        Coordinates coordAttacker = new Coordinates();
                        coordAttacker.setX(actionsArr.getCardAttacker().getX());
                        coordAttacker.setY(actionsArr.getCardAttacker().getY());
                        Coordinates coordAttacked = new Coordinates();
                        coordAttacked.setX(actionsArr.getCardAttacked().getX());
                        coordAttacked.setY(actionsArr.getCardAttacked().getY());

                        node.putPOJO("cardAttacker", coordAttacker);
                        node.putPOJO("cardAttacked", coordAttacked);
                        node.put("error", "Attacker card has already"
                                + " attacked this turn.");
                        output.add(node);
                        continue;
                    }
                    int isTank = 0;
                    int haveTank = 0;
                    if (Game_implementation.playerTurn == 1) {
                        for (int i = 0; i < MagicNumbers.NR_COLUMNS; i++) {
                            if (gameTable[1][i] != null) {
                                if (gameTable[1][i].name.equals("Warden")
                                        || gameTable[1][i].name.
                                        equals("Goliath")) {
                                    //check if there is a tank on the line
                                    haveTank = 1;
                                }
                            }
                        }
                        if (xAttacked == 0 && haveTank == 1) {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("command", actionsArr.getCommand());
                            node.putPOJO("cardAttacker", actionsArr.
                                    getCardAttacker());
                            node.putPOJO("cardAttacked", actionsArr.
                                    getCardAttacked());
                            node.put("error", "Attacked card is not "
                                    + "of type 'Tank'.");
                            output.add(node);
                            continue;
                        } else {
                            if (gameTable[xAttacked][yAttacked] == null) {
                                continue;
                            }
                            if (gameTable[xAttacked][yAttacked].name.
                                    equals("Warden")
                                    || gameTable[xAttacked][yAttacked].name.
                                    equals("Goliath")) {
                                // check if the card is tank
                                isTank = 1;
                            }
                            if (isTank == 1 || haveTank == 0) {
                                // if there is no tank or the card is a tank
                                //attack the card
                                gameTable[xAttacked][yAttacked].
                                        setHealth(gameTable[xAttacked][yAttacked].
                                                getHealth()
                                                - gameTable[xAttacker][yAttacker].
                                                        getAttackDamage());
                                Hand.attackCard(xAttacker, yAttacker);
                                // check rhe health of attacked card
                                hands.checkIfNoHp(gameTable);
                            } else if (isTank == 0 && haveTank == 1) {
                                // there is a tank
                                ObjectNode node = objectMapper.createObjectNode();
                                node.put("command", actionsArr.getCommand());
                                node.putPOJO("cardAttacker", actionsArr.
                                        getCardAttacker());
                                node.putPOJO("cardAttacked", actionsArr.
                                        getCardAttacked());
                                node.put("error", "Attacked card is not of "
                                        + "type 'Tank'.");
                                output.add(node);
                                continue;
                            }
                        }
                    } else if (Game_implementation.playerTurn == 2) {
                        for (int i = 0; i < MagicNumbers.NR_COLUMNS; i++) {
                            if (gameTable[2][i] != null) {
                                if (gameTable[2][i].name.equals("Warden")
                                        || gameTable[2][i].name.
                                        equals("Goliath")) {
                                    haveTank = 1;
                                }
                            }
                        }
                        if (xAttacked == MagicNumbers.LAST  && haveTank == 1) {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("command", actionsArr.getCommand());
                            node.putPOJO("cardAttacker", actionsArr.
                                    getCardAttacker());
                            node.putPOJO("cardAttacked", actionsArr.
                                    getCardAttacked());
                            node.put("error", "Attacked card is not "
                                    + "of type 'Tank'.");
                            output.add(node);
                            isTank = 0;
                            continue;
                        } else {
                            if (gameTable[xAttacked][yAttacked] == null) {
                                continue;
                            }
                            if (gameTable[xAttacked][yAttacked].name.
                                    equals("Warden")
                                    || gameTable[xAttacked][yAttacked].name.
                                    equals("Goliath")) {
                                isTank = 1;
                            }
                            if (isTank == 1 || haveTank == 0) {
                                gameTable[xAttacked][yAttacked].setHealth(
                                        gameTable[xAttacked][yAttacked]
                                                .getHealth() - gameTable
                                                [xAttacker][yAttacker].
                                                getAttackDamage());
                                Hand.attackCard(xAttacker, yAttacker);
                                hands.checkIfNoHp(gameTable);
                            } else if (isTank == 0 && haveTank == 1) {
                                ObjectNode node = objectMapper.createObjectNode();
                                node.put("command", actionsArr.getCommand());
                                node.putPOJO("cardAttacker", actionsArr.
                                        getCardAttacker());
                                node.putPOJO("cardAttacked", actionsArr.
                                        getCardAttacked());
                                node.put("error", "Attacked card is not of "
                                        + "type 'Tank'.");
                                output.add(node);
                                continue;
                            }
                        }
                    }
                }

            } else if (actionsArr.getCommand().equals("cardUsesAbility")) {

                // get coordinates
                Coordinates coordAttacker = actionsArr.getCardAttacker();
                Coordinates coordAttacked = actionsArr.getCardAttacked();

                if (Hand.getFrozenRows()[coordAttacker.getX()]
                        [coordAttacker.getY()] != 0) {
                    // if the card is frozen
                    ObjectNode node = objectMapper.createObjectNode();
                    node.put("command", actionsArr.getCommand());
                    Coordinates coord1 = actionsArr.getCardAttacker();
                    Coordinates coord2 = actionsArr.getCardAttacked();
                    node.putPOJO("cardAttacker", coord1);
                    node.putPOJO("cardAttacked", coord2);
                    node.put("error", "Attacker card is frozen.");
                    output.add(node);
                    continue;
                }
                if (Hand.getCardsWhoAttacked()[coordAttacker.getX()]
                        [coordAttacker.getY()] != 0) {
                    // if the card has already attacked
                    ObjectNode node = objectMapper.createObjectNode();
                    node.put("command", actionsArr.getCommand());
                    node.putPOJO("cardAttacker", coordAttacker);
                    node.putPOJO("cardAttacked", coordAttacked);
                    node.put("error", "Attacker card has already attacked "
                            + "this turn.");
                    output.add(node);
                    continue;
                }
                if (gameTable[coordAttacker.getX()][coordAttacker.getY()].
                        name.equals("Disciple")) {
                    // check if the chosen row is valid
                    if (Game_implementation.playerTurn == 2) {

                        if (coordAttacked.getX() == 2
                                || coordAttacked.getX() == MagicNumbers.LAST) {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("command", actionsArr.getCommand());
                            Coordinates coord1 = actionsArr.getCardAttacker();
                            Coordinates coord2 = actionsArr.getCardAttacked();
                            node.putPOJO("cardAttacker", coord1);
                            node.putPOJO("cardAttacked", coord2);
                            node.put("error", "Attacked card does not "
                                    + "belong to the current player.");
                            output.add(node);
                            continue;
                        }
                    } else if (Game_implementation.playerTurn == 1) {
                        if (coordAttacked.getX() == 0
                                || coordAttacked.getY() == 1) {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("command", actionsArr.getCommand());
                            Coordinates coord1 = actionsArr.getCardAttacker();
                            Coordinates coord2 = actionsArr.getCardAttacked();
                            node.putPOJO("cardAttacker", coord1);
                            node.putPOJO("cardAttacked", coord2);
                            node.put("error", "Attacked card does not "
                                    + "belong to the current player.");
                            output.add(node);
                            continue;
                        }
                    }
                    // if it is valid, use ability
                    gameTable[coordAttacker.getX()][coordAttacker.getY()].
                            specialAttack(gameTable, coordAttacker,
                                    coordAttacked);
                    continue;
                }
                if (gameTable[coordAttacker.getX()][coordAttacker.getY()].
                        name.equals("The Ripper")
                        || gameTable[coordAttacker.getX()][coordAttacker.getY()].
                        name.equals("Miraj")
                        || gameTable[coordAttacker.getX()][coordAttacker.getY()].
                        name.equals("The Cursed One")) {
                    // check if the chosen row is valid
                    if (Game_implementation.playerTurn == 2) {
                        if (coordAttacked.getX() == 0 || coordAttacked.getX() == 1) {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("command", actionsArr.getCommand());
                            Coordinates coord1 = actionsArr.getCardAttacker();
                            Coordinates coord2 = actionsArr.getCardAttacked();
                            node.putPOJO("cardAttacker", coord1);
                            node.putPOJO("cardAttacked", coord2);
                            node.put("error", "Attacked card does not "
                                    + "belong to the enemy.");
                            output.add(node);
                            continue;
                        }
                        // check for tanks
                        int isTank = 0;
                        int haveTank = 0;

                        for (int i = 0; i < MagicNumbers.NR_COLUMNS; i++) {
                            if (gameTable[2][i] != null) {
                                if (gameTable[2][i].name.equals("Warden")
                                        || gameTable[2][i].name.
                                        equals("Goliath")) {
                                    haveTank = 1;
                                }
                            }
                        }
                        if (coordAttacked.getX() == MagicNumbers.LAST
                                && haveTank == 1) {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("command", actionsArr.getCommand());
                            node.putPOJO("cardAttacker", actionsArr.
                                    getCardAttacker());
                            node.putPOJO("cardAttacked", actionsArr.
                                    getCardAttacked());
                            node.put("error", "Attacked card is not of "
                                    + "type 'Tank'.");
                            output.add(node);
                            continue;
                        } else {
                            if (gameTable[coordAttacked.getX()]
                                    [coordAttacked.getY()] == null) {
                                continue;
                            }
                            if (gameTable[coordAttacked.getX()]
                                    [coordAttacked.getY()].name.
                                    equals("Warden")
                                    || gameTable[coordAttacked.getX()]
                                    [coordAttacked.getY()].name.
                                    equals("Goliath")) {
                                isTank = 1;
                            }
                            if (isTank == 1 || haveTank == 0) {
                                // attack if there is no other tank
                                gameTable[coordAttacker.getX()]
                                        [coordAttacker.getY()].specialAttack(
                                                gameTable, coordAttacker,
                                                coordAttacked);
                                Hand.attackCard(coordAttacker.getX(),
                                        coordAttacker.getY());
                                hands.checkIfNoHp(gameTable);

                            } else if (isTank == 0 && haveTank == 1) {
                                ObjectNode node = objectMapper.createObjectNode();
                                node.put("command", actionsArr.getCommand());
                                node.putPOJO("cardAttacker", actionsArr.
                                        getCardAttacker());
                                node.putPOJO("cardAttacked", actionsArr.
                                        getCardAttacked());
                                node.put("error", "Attacked card is not of "
                                        + "type 'Tank'.");
                                output.add(node);
                                continue;
                            }
                        }
                    } else if (Game_implementation.playerTurn == 1) {
                        if (coordAttacked.getX() == 2
                                || coordAttacked.getY() == MagicNumbers.LAST) {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("command", actionsArr.getCommand());
                            Coordinates coord1 = actionsArr.getCardAttacker();
                            Coordinates coord2 = actionsArr.getCardAttacked();
                            node.putPOJO("cardAttacker", coord1);
                            node.putPOJO("cardAttacked", coord2);
                            node.put("error", "Attacked card does not "
                                    + "belong to the enemy.");
                            output.add(node);
                            continue;
                        }
                        int isTank = 0;
                        int haveTank = 0;

                        for (int i = 0; i < MagicNumbers.NR_COLUMNS; i++) {
                            if (gameTable[1][i] != null) {
                                if (gameTable[1][i].name.equals("Warden")
                                        || gameTable[1][i].name.equals("Goliath")) {
                                    haveTank = 1;
                                }
                            }
                        }
                        if (coordAttacked.getX() == 0 && haveTank == 1) {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("command", actionsArr.getCommand());
                            node.putPOJO("cardAttacker", actionsArr.
                                    getCardAttacker());
                            node.putPOJO("cardAttacked", actionsArr.
                                    getCardAttacked());
                            node.put("error", "Attacked card is not of"
                                    + " type 'Tank'.");
                            output.add(node);
                            continue;
                        } else {
                            if (gameTable[coordAttacked.getX()]
                                    [coordAttacked.getY()] == null) {
                                continue;
                            }
                            if (gameTable[coordAttacked.getX()]
                                    [coordAttacked.getY()].name.
                                    equals("Warden")
                                    || gameTable[coordAttacked.getX()]
                                    [coordAttacked.getY()].name.
                                    equals("Goliath")) {
                                isTank = 1;
                            }
                            if (isTank == 1 || haveTank == 0) {
                                gameTable[coordAttacker.getX()]
                                        [coordAttacker.getY()].
                                        specialAttack(gameTable,
                                                coordAttacker, coordAttacked);
                                Hand.attackCard(coordAttacker.getX(),
                                        coordAttacker.getY());
                                hands.checkIfNoHp(gameTable);

                            } else if (isTank == 0 && haveTank == 1) {
                                ObjectNode node = objectMapper.createObjectNode();
                                node.put("command", actionsArr.getCommand());
                                node.putPOJO("cardAttacker", actionsArr.
                                        getCardAttacker());
                                node.putPOJO("cardAttacked", actionsArr.
                                        getCardAttacked());
                                node.put("error", "Attacked card is not of "
                                        + "type 'Tank'.");
                                output.add(node);
                                continue;
                            }
                        }
                    }
                }

            } else if (actionsArr.getCommand().equals("useAttackHero")) {
                // get coordinates
                Coordinates coordAttacker = actionsArr.getCardAttacker();

                if (Hand.getFrozenRows()[coordAttacker.getX()]
                        [coordAttacker.getY()] != 0) {
                    ObjectNode node = objectMapper.createObjectNode();
                    node.put("command", actionsArr.getCommand());
                    Coordinates coord1 = actionsArr.getCardAttacker();
                    Coordinates coord2 = actionsArr.getCardAttacked();
                    node.putPOJO("cardAttacker", coordAttacker);
                    node.put("error", "Attacker card is frozen.");
                    output.add(node);
                    continue;
                }

                if (Hand.getCardsWhoAttacked()[coordAttacker.getX()]
                        [coordAttacker.getY()] != 0) {
                    // if the card has already attacked
                    ObjectNode node = objectMapper.createObjectNode();
                    node.put("command", actionsArr.getCommand());
                    node.putPOJO("cardAttacker", coordAttacker);
                    node.put("error", "Attacker card has "
                            + "already attacked this turn.");
                    output.add(node);
                    continue;
                }
                // check for tanks
                int isTank = 0;
                int haveTank = 0;
                if (Game_implementation.playerTurn == 1) {

                    for (int i = 0; i < MagicNumbers.NR_COLUMNS; i++) {
                        if (gameTable[1][i] != null) {
                            if (gameTable[1][i].name.equals("Warden")
                                    || gameTable[1][i].name.
                                    equals("Goliath")) {
                                haveTank = 1;
                            }
                        }
                    }
                    if (haveTank == 1) {
                        ObjectNode node = objectMapper.createObjectNode();
                        node.put("command", actionsArr.getCommand());
                        node.putPOJO("cardAttacker", actionsArr.
                                getCardAttacker());
                        node.put("error", "Attacked card is not of "
                                + "type 'Tank'.");
                        output.add(node);
                        continue;
                    } else {
                        // if the crad can attack the hero

                        //decrease hero health
                        hands.getHeroPlayer2().setHealth(hands.getHeroPlayer2()
                                .getHealth() - gameTable[coordAttacker.getX()]
                                [coordAttacker.getY()].getAttackDamage());
                        Hand.attackCard(coordAttacker.getX(),
                                coordAttacker.getY());
                        if (hands.getHeroPlayer2().getHealth() <= 0) {
                            // if the player dies, the game ends
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("gameEnded", "Player one killed the "
                                    + "enemy hero.");
                            // set statistics
                            Statistics.setPlayerOneWins();
                            output.add(node);
                        }
                    }

                } else if (Game_implementation.playerTurn == 2) {

                    for (int i = 0; i < MagicNumbers.NR_COLUMNS; i++) {
                        if (gameTable[2][i] != null) {
                            if (gameTable[2][i].name.equals("Warden")
                                    || gameTable[2][i].name.
                                    equals("Goliath")) {
                                haveTank = 1;
                            }
                        }
                    }
                    if (haveTank == 1) {
                        ObjectNode node = objectMapper.createObjectNode();
                        node.put("command", actionsArr.getCommand());
                        node.putPOJO("cardAttacker", actionsArr.
                                getCardAttacker());
                        node.put("error", "Attacked card is not "
                                + "of type 'Tank'.");
                        output.add(node);
                        continue;
                    } else {
                        hands.getHeroPlayer1().setHealth(hands.
                                getHeroPlayer1().getHealth() - gameTable
                                [coordAttacker.getX()][coordAttacker.getY()]
                                .getAttackDamage());
                        Hand.attackCard(coordAttacker.getX(), coordAttacker.getY());
                        if (hands.getHeroPlayer1().getHealth() <= 0) {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("gameEnded", "Player two killed "
                                    + "the enemy hero.");
                            Statistics.setPlayerTwoWins();
                            output.add(node);
                            continue;
                        }
                    }
                }
            } else if (actionsArr.getCommand().equals("useHeroAbility")) {
                int row = actionsArr.getAffectedRow();

                if (Game_implementation.playerTurn == 1) {
                    // check if there is enough mana
                    int manaAux = hands.getManaPlayerOne();
                    if (manaAux < hands.getHeroPlayer1().getMana()) {
                        ObjectNode node = objectMapper.createObjectNode();
                        node.put("command", actionsArr.getCommand());
                        node.put("affectedRow", row);
                        node.put("error", "Not enough mana to use "
                                + "hero's ability.");
                        output.add(node);
                        continue;
                    }

                    if (Hero.getHeroAttack()[0] != 0) {
                        // check if the hero has not alread attacked
                        ObjectNode node = objectMapper.createObjectNode();
                        node.put("command", actionsArr.getCommand());
                        node.put("affectedRow", row);
                        node.put("error", "Hero has already "
                                + "attacked this turn.");
                        output.add(node);
                        continue;
                    }

                    if (hands.getHeroPlayer1().getName().equals("Lord Royce")
                            || hands.getHeroPlayer1().getName().
                            equals("Empress Thorina")) {
                        if (row == 2 || row == MagicNumbers.LAST) {
                            // check the given row
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("command", actionsArr.getCommand());
                            Coordinates coord1 = actionsArr.getCardAttacker();
                            Coordinates coord2 = actionsArr.getCardAttacked();
                            node.put("affectedRow", row);
                            node.put("error", "Selected row does "
                                    + "not belong to the enemy.");
                            output.add(node);
                            continue;
                        } else {
                            //use ability
                            hands.getHeroPlayer1().specialAbility(hands.
                                    getHeroPlayer1(), gameTable, row);
                            hands.minusManaPlayerOne(hands.getHeroPlayer1().
                                    getMana());
                            // set the hero to have attacked
                            Hero.setHeroAttack(Game_implementation.
                                    playerTurn - 1);
                            // check if there are dead minions
                            hands.checkIfNoHp(gameTable);
                        }
                    } else if (hands.getHeroPlayer1().getName().
                            equals("General Kocioraw")
                            || hands.getHeroPlayer1().getName().
                            equals("King Mudface")) {
                        if (row == 0 || row == 1) {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("command", actionsArr.getCommand());
                            Coordinates coord1 = actionsArr.getCardAttacker();
                            Coordinates coord2 = actionsArr.getCardAttacked();
                            node.put("affectedRow", row);
                            node.put("error", "Selected row does not belong "
                                    + "to the current player.");
                            output.add(node);
                            continue;
                        } else {
                            hands.getHeroPlayer1().specialAbility(hands.
                                    getHeroPlayer1(), gameTable, row);
                            hands.minusManaPlayerOne(hands.
                                    getHeroPlayer1().getMana());
                            Hero.setHeroAttack(Game_implementation.
                                    playerTurn - 1);
                            hands.checkIfNoHp(gameTable);
                        }
                    }


                } else if (Game_implementation.playerTurn == 2) {
                    int manaAux = hands.getManaPlayerTwo();
                    if (manaAux < hands.getHeroPlayer2().getMana()) {
                        ObjectNode node = objectMapper.createObjectNode();
                        node.put("command", actionsArr.getCommand());
                        node.put("affectedRow", row);
                        node.put("error", "Not enough mana to use "
                                + "hero's ability.");
                        output.add(node);
                        continue;
                    }

                    if (Hero.getHeroAttack()[1] != 0) {
                        ObjectNode node = objectMapper.createObjectNode();
                        node.put("command", actionsArr.getCommand());
                        node.put("affectedRow", row);
                        node.put("error", "Hero has already attacked "
                                + "this turn.");
                        output.add(node);
                        continue;
                    }

                    if (hands.getHeroPlayer2().getName().
                            equals("Lord Royce")
                            || hands.getHeroPlayer2().getName().
                            equals("Empress Thorina")) {
                        if (row == 0 || row == 1) {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("command", actionsArr.getCommand());
                            Coordinates coord1 = actionsArr.getCardAttacker();
                            Coordinates coord2 = actionsArr.getCardAttacked();
                            node.put("affectedRow", row);
                            node.put("error", "Selected row does not "
                                    + "belong to the enemy.");
                            output.add(node);
                            continue;
                        } else {
                            hands.getHeroPlayer2().specialAbility(hands.
                                    getHeroPlayer2(), gameTable, row);
                            hands.minusManaPlayerTwo(hands.
                                    getHeroPlayer2().getMana());
                            Hero.setHeroAttack(Game_implementation.
                                    playerTurn - 1);
                            hands.checkIfNoHp(gameTable);
                        }
                    } else if (hands.getHeroPlayer2().getName().
                            equals("General Kocioraw")
                            || hands.getHeroPlayer2().getName().
                            equals("King Mudface")) {
                        if (row == 2 || row == MagicNumbers.LAST) {
                            ObjectNode node = objectMapper.createObjectNode();
                            node.put("command", actionsArr.getCommand());
                            Coordinates coord1 = actionsArr.getCardAttacker();
                            Coordinates coord2 = actionsArr.getCardAttacked();
                            node.put("affectedRow", row);
                            node.put("error", "Selected row does not belong "
                                    + "to the current player.");
                            output.add(node);
                            continue;
                        } else {
                            hands.getHeroPlayer2().specialAbility(hands.
                                    getHeroPlayer2(), gameTable, row);
                            hands.minusManaPlayerTwo(hands.getHeroPlayer2().getMana());
                            Hero.setHeroAttack(Game_implementation.
                                    playerTurn - 1);
                            hands.checkIfNoHp(gameTable);
                        }
                    }
                }
            } else if (actionsArr.getCommand().equals("getTotalGamesPlayed")) {
                // total number of games played
                ObjectNode node = objectMapper.createObjectNode();
                node.put("command", actionsArr.getCommand());
                int gamesPlayed = Statistics.getTotalGamesPlayed();
                node.put("output", gamesPlayed);
                output.add(node);

            } else if (actionsArr.getCommand().equals("getPlayerOneWins")) {
                // games won by player 1
                ObjectNode node = objectMapper.createObjectNode();
                node.put("command", actionsArr.getCommand());
                int gamesWon = Statistics.getPlayerOneWins();
                node.put("output", gamesWon);
                output.add(node);

            } else if (actionsArr.getCommand().equals("getPlayerTwoWins")) {
                // games won by player 2
                ObjectNode node = objectMapper.createObjectNode();
                node.put("command", actionsArr.getCommand());
                int gamesWon = Statistics.getPlayerTwoWins();
                node.put("output", gamesWon);
                output.add(node);
            }
        }
    }
}

