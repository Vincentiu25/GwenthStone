package main;

import fileio.CardInput;
import fileio.Coordinates;

import java.util.ArrayList;

public abstract class Cards_class {
    // class of cards that will be extended

    protected int mana;
    protected String description;
    protected ArrayList<String> colors;
    protected String name;

    public Cards_class() {

    }

    public String toString() {
        return "CardInput{"
                +  "mana="
                + mana
                + description
                + '\''
                + ", colors="
                + colors
                + ", name='"
                +  ""
                + name
                + '\''
                + '}';
    }

}

class MinionCard extends Cards_class {
    // minion class
    private int attackDamage;
    private int health;
    public MinionCard(final CardInput card_input) {
        // copy constructor
        this.mana = card_input.getMana();
        this.attackDamage = card_input.getAttackDamage();
        this.health = card_input.getHealth();
        this.description = card_input.getDescription();
        this.colors = card_input.getColors();
        this.name = card_input.getName();
    }

    public MinionCard(final MinionCard card) {
        // copy constructor
        this.mana = card.getMana();
        this.attackDamage = card.getAttackDamage();
        this.health = card.getHealth();
        this.description = card.getDescription();
        this.colors = card.getColors();
        this.name = card.getName();
    }


    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(final int attackDamage) {
        if (attackDamage <= 0) {
            this.attackDamage = 0;
        } else {
            this.attackDamage = attackDamage;
        }
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(final int health) {
        this.health = health;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void specialAttack(final MinionCard[][] gameTable,
                              final Coordinates coordAttacker,
                              final Coordinates coordAttacked) {
        // the special ability of minions

        if (gameTable[coordAttacker.getX()][coordAttacker.getY()].name.
                equals("Disciple")) {
            gameTable[coordAttacked.getX()][coordAttacked.getY()].
                    setHealth(gameTable[coordAttacked.getX()]
                            [coordAttacked.getY()].getHealth() + 2);
        } else if (gameTable[coordAttacker.getX()][coordAttacker.getY()].name.
                equals("The Cursed One")) {
            int aux = gameTable[coordAttacked.getX()]
                    [coordAttacked.getY()].getHealth();
            gameTable[coordAttacked.getX()][coordAttacked.getY()].
                    setHealth(gameTable[coordAttacked.getX()]
                            [coordAttacked.getY()].getAttackDamage());
            gameTable[coordAttacked.getX()][coordAttacked.getY()].
                    setAttackDamage(aux);
        } else if (gameTable[coordAttacker.getX()][coordAttacker.getY()].name.
                equals("The Ripper")) {
            gameTable[coordAttacked.getX()][coordAttacked.getY()].
                    setAttackDamage(gameTable[coordAttacked.getX()]
                            [coordAttacked.getY()].getAttackDamage() - 2);
        } else if (gameTable[coordAttacker.getX()][coordAttacker.getY()].name.
                equals("Miraj")) {
            int aux = gameTable[coordAttacked.getX()]
            [coordAttacked.getY()].getHealth();
            gameTable[coordAttacked.getX()][coordAttacked.getY()].
                    setHealth(gameTable[coordAttacker.getX()]
                            [coordAttacker.getY()].getHealth());
            gameTable[coordAttacker.getX()][coordAttacker.getY()].
                    setHealth(aux);
        }
    }
}

class Environment_card extends Cards_class {
    //environment card

    public Environment_card(final CardInput card_input) {
        // copy constructor
        this.mana = card_input.getMana();
        this.description = card_input.getDescription();
        this.colors = card_input.getColors();
        this.name = card_input.getName();
    }

    public Environment_card(final Environment_card card) {
        // copy constructor
        this.mana = card.getMana();
        this.description = card.getDescription();
        this.colors = card.getColors();
        this.name = card.getName();
    }

    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int specialAbility(final Environment_card environment,
                              final MinionCard[][] gameTable,
                              final int row) {
        // special ability
        String name = environment.name;
        if (name.equals("Firestorm")) {
            for (int i = 0; i < MagicNumbers.NR_COLUMNS; i++) {
                if (gameTable[row][i] != null) {
                    gameTable[row][i].setHealth(gameTable[row][i].
                            getHealth() - 1);
                }
            }
            return 1;
        } else if (name.equals("Winterfell")) {
            for (int i = 0; i < MagicNumbers.NR_COLUMNS; i++) {
                if (gameTable[row][i] != null) {
                    // freeze an entire row
                    Hand.frozeCards(row, i);
                }
            }
            return 1;
        } else if (name.equals("Heart Hound")) {
            int maxHealth = 0;
            int posHealth = 0;
            for (int i = 0; i < MagicNumbers.NR_COLUMNS; i++) {
                if (gameTable[row][i] != null) {
                    // get the position of the attacked card
                    if (maxHealth < gameTable[row][i].getHealth()) {
                        maxHealth = gameTable[row][i].getHealth();
                        posHealth = i;
                    }
                }
            }
            MinionCard minionAux = new MinionCard(gameTable[row][posHealth]);
            if (gameTable[MagicNumbers.LAST - row][MagicNumbers.NR_ROWS] != null) {
                // if there is not enough room to steal a card
                return 0;
            } else {
                // add the stolen card
                gameTable[row][posHealth] = null;
                gameTable[MagicNumbers.DEFROZE - row][posHealth] = minionAux;
                return 1;
            }
        }
        return 1;
    }

}

class Hero {
    // hero class
    private static int heroAttack[] = {0, 0};
    private int mana;
    private int health;
    private String description;
    private ArrayList<String> colors;
    private String name;

    public Hero(final CardInput card_input) {
        // copy constructor
        this.mana = card_input.getMana();
        this.health = 30;
        this.description = card_input.getDescription();
        this.colors = card_input.getColors();
        this.name = card_input.getName();
    }

    public Hero(final Hero hero) {
        // copy constructor
        this.mana = hero.getMana();
        this.health = hero.getHealth();
        this.description = hero.getDescription();
        this.colors = hero.getColors();
        this.name = hero.getName();
    }

    public static int[] getHeroAttack() {
        return heroAttack;
    }

    /** function to note when the hero has attacked */

    public static void setHeroAttack(final int i) {
        Hero.heroAttack[i]++;
    }

    public static void resetHeroAttack(final int i) {
        Hero.heroAttack[i] = 0;

    }


    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(final int health) {
        this.health = health;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /** function to use special ability of hero */

    public void specialAbility(final Hero hero,
                                final MinionCard[][] gameTable,
                                final int row) {
        // special ability for every hero
        String name = hero.getName();
        if (name.equals("Lord Royce")) {
            int maxAttack = 0;
            int posAttack = 0;
            for (int i = 0; i < MagicNumbers.NR_COLUMNS; i++) {
                if (gameTable[row][i] != null) {
                    if (maxAttack < gameTable[row][i].getAttackDamage()) {
                        maxAttack = gameTable[row][i].getAttackDamage();
                        posAttack = i;
                    }
                }
            }
            Hand.frozeCards(row, posAttack);
        } else if (name.equals("Empress Thorina")) {
            int maxHp = 0;
            int posHp = 0;
            for (int i = 0; i < MagicNumbers.NR_COLUMNS; i++) {
                if (gameTable[row][i] != null) {
                    if (maxHp < gameTable[row][i].getHealth()) {
                        maxHp = gameTable[row][i].getHealth();
                        posHp = i;
                    }
                }
            }
            gameTable[row][posHp].setHealth(0);
        } else if (name.equals("King Mudface")) {
            for (int i = 0; i < MagicNumbers.NR_COLUMNS; i++) {
                if (gameTable[row][i] != null) {
                    gameTable[row][i].setHealth(gameTable[row][i].
                            getHealth() + 1);
                }
            }
        } else if (name.equals("General Kocioraw")) {
            for (int i = 0; i < MagicNumbers.NR_COLUMNS; i++) {
                if (gameTable[row][i] != null) {
                    gameTable[row][i].setAttackDamage(gameTable[row][i].
                            getAttackDamage() + 1);
                }
            }
        }

    }

    public String toString() {
        return "CardInput{"
                +  "mana="
                + mana
                +  ", description='"
                + description
                + '\''
                + ", colors="
                + colors
                + ", name='"
                +  ""
                + name
                + '\''
                + ", health="
                + health
                + '}';
    }
}

class Hand {
    // class that contains data about the game

    //players mana
    private int manaPlayerOne;
    private int manaPlayerTwo;

    // players heroes
    private Hero heroPlayer1;
    private Hero heroPlayer2;


    // a matrix to know if a card is frozen or not
    private static int frozenRows[][] = {{0, 0, 0, 0, 0},
                                        {0, 0, 0, 0, 0},
                                        {0, 0, 0, 0, 0},
                                        {0, 0, 0, 0, 0}};

    public static int[][] getFrozenRows() {
        return frozenRows;
    }

    /** function to freeze a card */

    public static void frozeCards(final int row, final int col) {
        Hand.frozenRows[row][col]++;
    }

    /** function to defroze a card */

    public static void defrozeCards(final int row, final int col) {
        Hand.frozenRows[row][col] = 0;
    }

    public static int[][] getCardsWhoAttacked() {
        return cardsWhoAttacked;
    }


    // a matrix to know if a card has attacked
    private static int cardsWhoAttacked[][] = {{0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}};

    public static void attackCard(final int row, final int col) {
        Hand.cardsWhoAttacked[row][col]++;
    }

    /** function to clean from memory the cards who attacked */

    public static void endAttack() {
        // the minion resets the attack
        for (int i = 0; i < MagicNumbers.NR_ROWS; i++) {
            for (int j = 0; j < MagicNumbers.NR_COLUMNS; j++) {
                Hand.cardsWhoAttacked[i][j] = 0;
            }
        }

    }

    public Hero getHeroPlayer1() {
        return heroPlayer1;
    }

    public Hero getHeroPlayer2() {
        return heroPlayer2;
    }

    public void setHeroPlayer1(final Hero heroPlayer1) {
        this.heroPlayer1 = heroPlayer1;
    }

    public void setHeroPlayer2(final Hero heroPlayer2) {
        this.heroPlayer2 = heroPlayer2;
    }

    private ArrayList<Cards_class> handPlayerOne;
    private ArrayList<Cards_class> handPlayerTwo;

    public Hand() {
        this.manaPlayerOne = 1;
        this.manaPlayerTwo = 1;
        this.handPlayerOne = new ArrayList<Cards_class>();
        this.handPlayerTwo = new ArrayList<Cards_class>();
    }

    public int getManaPlayerOne() {
        return manaPlayerOne;
    }

    public int getManaPlayerTwo() {
        return manaPlayerTwo;
    }

    /** function to set mana of player 1 */

    public void setManaPlayerOne(final int mana) {

        this.manaPlayerOne += mana;

    }
    /** function to increase mana of player 2 */

    public void minusManaPlayerOne(final int mana) {
        this.manaPlayerOne -= mana;
        if (this.manaPlayerOne < 0) {
            this.manaPlayerOne = 0;
        }
    }

    /** function to decrease mana of player 2 */

    public void minusManaPlayerTwo(final int mana) {
        this.manaPlayerTwo -= mana;
        if (this.manaPlayerTwo < 0) {
            this.manaPlayerTwo = 0;
        }
    }
    /** function to add to mana of player 2 */

    public void setManaPlayerTwo(final int mana) {

        this.manaPlayerTwo += mana;
    }

    public ArrayList<Cards_class> getHandPlayerTwo() {
        return handPlayerTwo;
    }

    public ArrayList<Cards_class> getHandPlayerOne() {
        return handPlayerOne;
    }

    /** function to add a card in hand*/
    public void addCardsInHand1(final Hand hands,
                                 final ArrayList<Cards_class> deckPlayer1) {
        // addd a card to the hand of player one
        if (deckPlayer1.size() == 0) {
            return;
        } else {
            // card name
            String cardName = deckPlayer1.get(0).name;
            if (cardName.equals("Winterfell")
                    || cardName.equals("Firestorm")
                    || cardName.equals("Heart Hound")) {
                // environment card
                Environment_card newEnvironmentCard = new Environment_card(
                        (Environment_card) deckPlayer1.get(0));
                hands.getHandPlayerOne().add(newEnvironmentCard);
                deckPlayer1.remove(0);
            } else {
                // minion card
                MinionCard newMinionCard = new MinionCard(
                        (MinionCard) deckPlayer1.get(0));
                hands.getHandPlayerOne().add(newMinionCard);
                deckPlayer1.remove(0);
            }
        }
    }

    /** function to add a card in hand*/

    public void addCardsInHand2(final Hand hands,
                                final ArrayList<Cards_class> deckPlayer2) {
        // addd a card to the hand of player two
        if (deckPlayer2.size() == 0) {
            return;
        } else {
            String cardName = deckPlayer2.get(0).name;
            if (cardName.equals("Winterfell")
                    || cardName.equals("Firestorm")
                    || cardName.equals("Heart Hound")) {
                Environment_card newEnvironmentCard = new Environment_card(
                        (Environment_card) deckPlayer2.get(0));
                hands.getHandPlayerTwo().add(newEnvironmentCard);
                deckPlayer2.remove(0);
            } else {
                MinionCard newMinionCard = new MinionCard(
                        (MinionCard) deckPlayer2.get(0));
                hands.getHandPlayerTwo().add(newMinionCard);
                deckPlayer2.remove(0);
            }
        }

    }

    /** function to remove a card from hand*/
    public Hand removeCardFromHand1(final Hand hand, final int index) {
        hand.getHandPlayerOne().remove(index);
        return hand;
    }

    /** function to remove a card from hand*/
    public Hand removeCardFromHand2(final Hand hand, final int index) {
        hand.getHandPlayerTwo().remove(index);
        return hand;
    }

    /** function to check if any card must be removed*/
    public void checkIfNoHp(final MinionCard[][] gameTable) {
        for (int i = 0; i < MagicNumbers.NR_ROWS; i++) {
            for (int j = 0; j < MagicNumbers.NR_COLUMNS; j++) {
                if (gameTable[i][j] != null) {
                    if (gameTable[i][j].getHealth() <= 0) {
                        // if the minion is dead, it will be deleted from
                        // the table
                        // minions on the right will move one position to
                        // the left
                        for (int k = j; k < MagicNumbers.NR_ROWS; k++) {
                            gameTable[i][k] = gameTable[i][k + 1];
                        }
                        j--;
                        // the last place becomes empty
                        gameTable[i][MagicNumbers.NR_ROWS] = null;
                    }
                }
            }
        }
    }
}
