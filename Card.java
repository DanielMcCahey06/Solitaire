
class Card{

    // Color codes
    public static final String RED = "\u001B[31m";    // Red color
    public static final String BLACK = "\u001B[37m";  // Black color
    public static final String RESET = "\u001B[0m";    // Reset color

    protected Suit suit;
    protected Rank rank;
    protected boolean isFaceUp = false;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
        this.isFaceUp = false;
    }

    // Flip the card
    public void flip(){
        isFaceUp = !isFaceUp;
    }

    // Getter for Suit
    public Suit getSuit(){
        return suit;
    }

    // Getter for Rank
    public Rank getRank(){
        return rank;
    }

    // Check if card is red
    public boolean isRed(){
        return suit == Suit.H || suit == Suit.D; // Hearts and diamonds are red
    }

    // Check if card is black
    public boolean isBlack(){
        return suit == Suit.S || suit == Suit.C; // Suits and clubs are black
    }

    // Check if this card is one rank higher than other
    public boolean isOneRankHigher(Card otherCard){
        return (rank.getValue() == (otherCard.rank.getValue()+1));
    }

    // Check if this card is one rank lower than other
    public boolean isOneRankLower(Card otherCard){
        return (rank.getValue() == (otherCard.rank.getValue()-1));
    }

    public boolean isFaceUp() {
        return isFaceUp;
    }

    @Override
    public String toString(){
        String value;

        // Map ranks to representations
        switch (rank) {
            case ACE: value = "A"; break;
            case JACK: value = "J"; break;
            case QUEEN: value = "Q"; break;
            case KING: value = "K"; break;
            default: value = String.valueOf(rank.getValue());
        }

        String suitSymbol;
        switch(suit){
            case D: suitSymbol = "♦"; break;
            case H: suitSymbol = "♥"; break;
            case C: suitSymbol = "♣"; break;
            case S: suitSymbol = "♠"; break;
            default: suitSymbol = "";
        }

        String colour = isFaceUp ? (isRed() ? RED : BLACK) : RESET;
        //return isFaceUp? background + colour + value + suitSymbol + RESET: "[X]";
        //return isFaceUp ? colour + value + suitSymbol + RESET : "[X]";
        return isFaceUp ? colour + String.format("%-5s", value + suitSymbol) + RESET: String.format("%-5s", "[X]");
    }

}