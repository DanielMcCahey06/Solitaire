
import java.util.*;

public class CardPile implements Iterable<Card>{
    protected ArrayList<Card> pile;

    // Constructor
    public CardPile(){
        pile = new ArrayList<Card>();
    }

    // Add a card to this pile
    public void addCards(Card... cards){
        for(Card card : cards){
            pile.add(card);
        }
    }

    // Remove multiple cards from the pile
    public ArrayList<Card> drawCards(int numCards) {
        ArrayList<Card> drawnCards = new ArrayList<>();
        for (int i = 0; i < numCards; i++) {
            Card card = drawCard();
            if (card != null) {
                drawnCards.add(0, card);
            } else {
                break; // Stop if there are no more cards to draw
            }
        }
        return drawnCards; // Return the list of drawn cards
    }

    // Remove top card from pile
    public Card drawCard(){
        if(!pile.isEmpty()){
            return pile.removeLast();
        }
        else{
            return null;
        }
    }

    // Implementing the iterator method
    @Override
    public Iterator<Card> iterator() {
        return pile.iterator();  // Return the iterator for the cards list
    }

    // Flip top card
    public void flipTopCard(){
        if(!pile.isEmpty()){
            Card topCard = pile.get(pile.size()-1);
            topCard.flip();
            System.out.println("TOP CARD FLIPPED");
        }
    }

    // See the top card of the pile
    public Card getCard(){
        if(!pile.isEmpty()){
            return pile.get(pile.size()-1);
        }
        else{
            return null;
        }
    }

    // Check if pile is empty
    public boolean isEmpty(){
        return pile.isEmpty();
    }

    // Get pile size
    public int Size(){
        return pile.size();
    }

    public void display(){
        for(Card card : pile){
            System.out.print(card + " ");
        }
        System.out.println();
    }

    public Card get(int index) {
        return pile.get(index); // Ensure this method exists and is public
    }

    public void displayTopCard(){
        if(!pile.isEmpty()){
            Card topCard = pile.get(pile.size()-1);
            System.out.print(topCard);
        }else {
            System.out.print("[ ]  "); // Change this message if you want
        }
    }
}