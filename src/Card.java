import java.util.stream.IntStream;
import java.util.Arrays;

//this class represent a card in a poker game
public class Card {
    // index of the card face or suit depending on it's ranking
    private int face, suit;
    private boolean isJoker;

    // possible combinaisons of cards
    private static String[] suits = { "H", "S", "D", "C" }; // for "hearts", "spades", "diamonds", "clubs"
    private static String[] faces = { "null","A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K" };

    // return the face index as a string
    public static String faceToString(int face) {
        return faces[face];
    }

    // Constructor


    // Constructor
    Card(String _suit, String _face) {
        String newCard=_face+_suit;
        this.face = faceToInt(_face);
        this.suit = suitToInt(_suit);
        this.isJoker= newCard.equals("JK");
    }

    private  int faceToInt(String face) {
        return Arrays.asList(faces).indexOf(face);
    }

    private  int suitToInt(String suit) {
        return Arrays.asList(suits).indexOf(suit);
    }

    // getters
    public String getCard() {
        if (isJoker) {
            return "JK";
        }
        return faces[face] + suits[suit];
    }

    public int getFace() {
        return face;
    }

    public int getSuit() {
        return suit;
    }
    
    public boolean isJoker() {
        return isJoker;
    }

}