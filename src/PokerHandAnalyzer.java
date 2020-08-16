import java.util.Arrays;
import java.util.HashSet;

public class PokerHandAnalyzer {

    private Card[] cards; // Cards to analyse in the hand (5 cards)
    private int[] cardsTable; // Distribution of the Card faces present in the hand
                              // each element of this array represent the number of card of that face(from 1
                              // to 13, 1 being ace) present in the hand
    static final int HAND_MAX_LENGHT = 5;

    private int jokers; // how many jokers in the hand
    private int remainingJokers; // how many jokers unused

    private CardGroupe firstGroupee; // to record how many cards are of the same face (first groupe) and their face
    private CardGroupe secondGroupee;// to record how many cards are of the same face (second groupe) and their face

    private boolean HighCard;
    private boolean Pair;
    private boolean TwoPair;
    private boolean ThreeOfAKind;
    private boolean flush;
    private boolean straight;
    private boolean RoyalFlush;
    private boolean FullHouse;
    private boolean FourOfAKind;

    public Hand analyzeHand(final String[] hand) {

        // verification
        if (hand.length != HAND_MAX_LENGHT)
            throw new IllegalArgumentException("Wrong number of cards in hand.");
        if (new HashSet<>(Arrays.asList(hand)).size() != hand.length && !Arrays.asList(hand).contains("JK"))
            throw new IllegalArgumentException("Duplicate cards in hand.");

        // initialisation
        initialiseCards(hand);

        // processing the cards
        createCardTable();
        GroupeCards();
        evaluateCardsTable();

        return calculateResult();
    }

    private int howManyJokers() {
        int jokersCounter = 0;
        for (Card card : cards) {
            if (card.isJoker())
                jokersCounter++;

        }
        return jokersCounter;
    }

    // this method transfers the hand from an array string to an array of cards
    // objects
    private void initialiseCards(final String[] hand) {
        String face;
        String suit;
        cards = new Card[HAND_MAX_LENGHT];
        // for each string card in the hand, add it to the array of Cards
        for (int i = 0; i < hand.length; i++) {
            String card = hand[i];

            face = String.valueOf(card.charAt(0));
            suit = String.valueOf(card.charAt(1));

            Card newcard = new Card(suit, face);
            cards[i] = newcard;
        }

        jokers = howManyJokers(); // count how many jokers we have in our hand

    }

    // this method creates an array of faces present in our hand
    private void createCardTable() {
        // each element of this array represent the number of card of that face(from 1
        // to 13, 1 being ace) present in the hand
        cardsTable = new int[14];

        // initialising the array to 0
        for (int faces : cardsTable) {
            faces = 0;
        }

        for (int i = 0; i <= 4; i++) {
            // increment the number of cards having a certain face accordingly inside
            // cardsTable
            if (!cards[i].isJoker())
                cardsTable[cards[i].getFace()]++;

        }

    }

    // here, i divide same cards in two groupes (small and large), for example: a
    // pair
    // of king and three fives.
    // the first groupe stores the largest amount of same cards, the second stores
    // the smallest.
    private void GroupeCards() {
        firstGroupee = new CardGroupe(); // large groupe
        secondGroupee = new CardGroupe();// small groupe

        for (int i = 13; i >= 1; i--) {
            if (cardsTable[i] > firstGroupee.amount) {// If there is more cards of face i than the number of same cards
                if (firstGroupee.amount != 1)
                // the if statement checks if firstGroupe was previously assigned to
                // something before overwriting it, and if it was, we save it in the second
                // groupe.
                {
                    // the small groupe takes the values of the large before overwriting it with a
                    // new groupe
                    secondGroupee = firstGroupee;
                }
                firstGroupee.amount = cardsTable[i];// set firstGroupe to that number of cards
                firstGroupee.face = i;// and record the face of those cards

            } else if (cardsTable[i] > secondGroupee.amount) {
                secondGroupee.amount = cardsTable[i];
                secondGroupee.face = i;
            }
        }
        // firstGroupe amount starts at 1, so if we find a face of which there are two cards,
        // then we record 2 in firstGroupe
        // and (i) as face. This will work fine if there's a pair, three
        // of a kind, or four of a kind.

    }

    // evaluate hand
    private void evaluateCardsTable() {

        if (firstGroupee.amount == 1) {// if we have no pair...
            HighCard = true;
        }
        if ((firstGroupee.amount == 2 && secondGroupee.amount == 1) || (firstGroupee.amount == 1 && jokers == 1)) { // if
                                                                                                                    // 1
                                                                                                                    // pair
            Pair = true;
        }

        if (firstGroupee.amount == 2 && secondGroupee.amount == 2
                || (firstGroupee.amount == 2 && secondGroupee.amount == 1 && jokers == 1)) {// two
            // pair
            TwoPair = true;
        }
        if (firstGroupee.amount == 3 && secondGroupee.amount != 2) {// three of a kind (not full house)
            ThreeOfAKind = true;
        }

        // check flush
        // if two cards are not the same suit, then there's no flush
        flush = true; // assume there is a flush
        // "8S JK 6S JK 4S"
        for (int x = 0; x < 4; x++) {
            if (cards[x].getSuit() != cards[x + 1].getSuit())

                if (!cards[x].isJoker() && !cards[x + 1].isJoker()) { // if those two cards are not jokers
                    flush = false;
                }

        }

        // --------------------------------------------------------------------------------------------------
        // check straight
        // To figure out if there's a straight, we need to know if there are five cards
        // in a row.
        // if there is one card in five consecutive ranks, we have a straight.
        straight = false; // assume no straight
        for (int i = 1; i <= 9; i++) // can't have straight with lowest value of more than 10
        {
            remainingJokers = jokers;
            if (cardsTable[i] == 1 || useJoker()) {
                if (cardsTable[i + 1] == 1 || useJoker()) {
                    if (cardsTable[i + 2] == 1 || useJoker()) {
                        if (cardsTable[i + 3] == 1 || useJoker()) {
                            if (cardsTable[i + 4] == 1 || useJoker()) {
                                straight = true;
                                break;

                            }

                        }

                    }
                }

            }
        }
        // edge case for RoyalFlush
        if (cardsTable[10] == 1 && cardsTable[11] == 1 && cardsTable[12] == 1 && cardsTable[13] == 1
                && cardsTable[1] == 1) // ace
        {
            RoyalFlush = true;
        }

        if (firstGroupee.amount == 3 && secondGroupee.amount == 2) {// full house
            FullHouse = true;
        }
        if (firstGroupee.amount == 4) { // four of a kind
            FourOfAKind = true;
        }

        // --------------------------------------------------------------------------------------------------

    }

    //if there is a joker left, return true and decrement it
    private boolean useJoker() {
        int remaining = remainingJokers;
        remainingJokers--;
        return remaining > 0;
    }

    private Hand calculateResult() {
        Hand result = Hand.HighCard;
        // evaluate hand
        if (RoyalFlush) {
            result = Hand.RoyalFlush;
        }
        if (HighCard) {// if we have no pair...
            result = Hand.HighCard;
        }
        if (Pair) { // if 1 pair
            result = Hand.Pair;
        }
        if (TwoPair) {// two pair
            result = Hand.TwoPair;
        }
        if (ThreeOfAKind) {// three of a kind (not full house)
            result = Hand.ThreeOfAKind;
        }
        if (straight) {
            result = Hand.Straight;
        }
        if (flush) {
            result = Hand.Flush;
        }
        if (FullHouse) {// full house
            result = Hand.FullHouse;
        }
        if (FourOfAKind) { // four of a kind
            result = Hand.FourOfAKind;
        }
        if (straight && flush) {
            result = Hand.StraightFlush;
        }
        if (RoyalFlush) {
            result = Hand.RoyalFlush;
        }
        return result;

    }


}