package main;

public final class Table {
    public Table() {
    }

    /** function to place a card on table*/

    public void addCardToTable(final Cards_class[][] gameTable,
                               final int line, final MinionCard card) {
        for (int i = 0; i < MagicNumbers.NR_COLUMNS; i++) {
           if (gameTable[line][i] == null) {
               // if there is a free place on the table, add the card
               gameTable[line][i] = card;
               return;
           }
        }

    }
}
