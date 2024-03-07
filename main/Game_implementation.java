package main;


import fileio.CardInput;
import fileio.Input;

import java.util.ArrayList;

public final class Game_implementation {
    public Game_implementation() {
    }

    // use static variable turns to counter how many turns have been ended
    private static int turns = 0;

    /** function to a turn */
    public static void setTurns() {
        turns += 1;
    }

    public static int getTurns() {
        return turns;
    }

    // use static variable playerTurn to memorise the player that is next
    public static int playerTurn;

    /** function to reset turns */

    public static void resetTurns() {
        turns = 0;
    }

    /** function to et the player turn*/

    public void setPlayerTurn(final int turn) {
        playerTurn = turn;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }


    public void incrementTurn() {
        playerTurn++;
    }

    /** function to create the decks of cards players use*/

    public ArrayList<Cards_class> choseDeckPlayer(final Input input,
                                                    final int playerIdx,
                                                    final int gameNumber) {
        //check for which player to create the deck
        if (playerIdx == 0) {
            //for player 1
            int deckIdx = input.getGames().get(gameNumber).getStartGame().
                    getPlayerOneDeckIdx();
            // create new array of cards
            ArrayList<CardInput> deckAux = input.getPlayerOneDecks().
                    getDecks().get(deckIdx);
            ArrayList<Cards_class> deckReturn = new ArrayList<>();
            for (int i = 0; i < deckAux.size(); i++) {
                String environment = input.getPlayerOneDecks().
                        getDecks().get(deckIdx).get(i).getName();
                if (environment.equals("Winterfell")
                        || environment.equals("Firestorm")
                        || environment.equals("Heart Hound")) {
                    // new environment card
                    Environment_card cardNew = new
                            Environment_card(input.getPlayerOneDecks().
                            getDecks().get(deckIdx).get(i));
                    deckReturn.add(cardNew);
                } else {
                    // new minion card
                    MinionCard cardNew = new
                            MinionCard(input.getPlayerOneDecks().
                            getDecks().get(deckIdx).get(i));
                    deckReturn.add(cardNew);
                }
            }
            return deckReturn;
        } else if (playerIdx == 1) {
            // for player 2
            int deckIdx = input.getGames().get(gameNumber).getStartGame().
                    getPlayerTwoDeckIdx();
            ArrayList<CardInput> deckAux2 = input.getPlayerTwoDecks().
                    getDecks().get(deckIdx);
            ArrayList<Cards_class> deckReturn2 = new ArrayList<>();
            for (int i = 0; i < deckAux2.size(); i++) {
                String environment = input.getPlayerTwoDecks().getDecks().
                        get(deckIdx).get(i).getName();
                if (environment.equals("Winterfell")
                        || environment.equals("Firestorm")
                        || environment.equals("Heart Hound")) {
                    Environment_card cardNew = new
                            Environment_card(input.getPlayerTwoDecks().
                            getDecks().get(deckIdx).get(i));
                    deckReturn2.add(cardNew);
                } else {
                    MinionCard cardNew = new
                            MinionCard(input.getPlayerTwoDecks().
                            getDecks().get(deckIdx).get(i));
                    deckReturn2.add(cardNew);
                }
            }
            // return the new created deck
            return deckReturn2;
        }
        return null;
    }


}
