
import java.util.*;

public class Deck extends CardPile {
    // Constructor to initialise deck of cards
    public Deck() {
        super();
        initializeDeck();
        shuffle();
    }

    // Initialise the Deck
    public void initializeDeck(){
        for(Suit suit : Suit.values()){
            for(Rank rank : Rank.values()){
                pile.add(new Card(suit, rank));
            }
        }
    }

    // Shuffle the deck
    public void shuffle(){
        Collections.shuffle(pile);
    }
}