package main;

public final class Statistics {
    private static int playerOneWins = 0;

    private static int playerTwoWins = 0;

    private static int totalGamesPlayed = 0;

    public static int getPlayerOneWins() {
        return playerOneWins;
    }

    public static int getPlayerTwoWins() {
        return playerTwoWins;
    }

    public static int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    /** function to increase the number of games played*/
    public static void setTotalGamesPlayed() {
        Statistics.totalGamesPlayed++;
    }

    /** function to increase the number of games won by player 1*/

    public static void setPlayerOneWins() {
        Statistics.playerOneWins++;
    }

    /** function to increase the number of games won by player 2*/

    public static void setPlayerTwoWins() {
        Statistics.playerTwoWins++;
    }

    /** function to reset statisics */

    public static void resetStatistics() {
        Statistics.playerOneWins = 0;
        Statistics.playerTwoWins = 0;
        Statistics.totalGamesPlayed = 0;
    }

}
