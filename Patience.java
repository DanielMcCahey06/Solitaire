import java.util.*;

public class Patience{
    private Deck deckPile;
    private ArrayList<CardPile> tableau;
    private ArrayList<CardPile> foundations;
    private CardPile discardPile;
    private int score;
    private int moves;

    public static final String BLACK = "\u001B[37m";  // Black color
    public static final String RESET = "\u001B[0m";    // Reset color
    public static final String RED = "\u001B[31m";    // Red color

    // Initialize game
    public Patience(){
        tableau = new ArrayList<>();
        deckPile = new Deck();
        discardPile = new CardPile();
        foundations = new ArrayList<>(); // 4 foundation piles for each suit
        score = 0;
        moves = 0;

        initializeTableau();
        initializeFoundations();
    }

    public void initializeTableau(){
        for (int i=0; i<7; i++){
            CardPile pile = new CardPile();
            for (int j=0; j<=i; j++){
                Card acard = deckPile.drawCard();
                if (j==i){
                    acard.flip(); // Last card in pile is face up
                }
                pile.addCards(acard); // Add card from deck pile
            }
            tableau.add(pile); // Add pile to tableau
        }
    }

    public void initializeFoundations(){
        for (int i=0; i<4; i++){
            foundations.add(new CardPile()); // Creates 4 empty cardpiles
        }
    }

    // Method that begins the game
    public void play(){
        boolean gameOver = false;
        Scanner scanner = new Scanner(System.in);
        String input;

        while(true){
            displayGame();
            System.out.println("Enter your command (or Q to quit): ");
            String userInput = scanner.nextLine();
            if(userInput.equalsIgnoreCase("Q")){
                System.out.println("Thanks for playing!");
                break;
            }
            processInput(userInput);
            gameOver = checkGameCompletion(foundations);
            if(gameOver){
                System.out.println("Congratulations! You won! Thank you for playing!");
                break;
            }
        }
        scanner.close();
    }

    public void displayGame(){
        System.out.println();

        // Display top row: draw pile, discard pile, and foundation piles
        deckPile.displayTopCard();
        discardPile.displayTopCard();
        System.out.print("       ");

        for(int i=0; i<foundations.size(); i++){
            if (foundations.get(i).isEmpty()) {
                String suitSymbol;
                String colour;
                switch(i){
                    case 0: suitSymbol = "♦"; colour = Card.RED;  break;
                    case 1: suitSymbol = "♥"; colour = Card.RED; break;
                    case 2: suitSymbol = "♣"; colour = Card.BLACK; break;
                    case 3: suitSymbol = "♠"; colour = Card.BLACK; break;
                    default: suitSymbol = ""; colour = null;
                }
                System.out.print("[" + colour + suitSymbol + Card.RESET + "]" + "  ");
            } else {
                foundations.get(i).displayTopCard();
                System.out.print("  ");
            }
        }
        System.out.println();
        System.out.print("----------------------------------");
        System.out.println();

        int maxLaneSize = getMaxLaneSize();
        for (int row = 0; row < maxLaneSize; row++) {
            for (int col = 0; col < tableau.size(); col++) {
                if (row < tableau.get(col).Size()) {
                    Card card = tableau.get(col).get(row); // Get the card in the current row of this lane
                    System.out.print(card);
                } else {
                    System.out.print("     "); // Empty space if this lane has fewer cards
                }
            }
            System.out.println(); // Move to the next row
        }
        System.out.println("Score: " + score + " Moves: " + moves);
    }

    public void processInput(String userInput){

        if(userInput.length() < 2) {
            // Uncovering a new card from the draw pile and placing in discard pile
            if (userInput.equalsIgnoreCase("D")) {
                Card newCard = deckPile.drawCard();
                if (newCard != null) {
                    discardPile.addCards(newCard);
                    newCard.flip();
                    System.out.println("New Card Drawn: " + newCard);
                } else {
                    System.out.println("No More Cards to Draw. Re-dealing the draw pile. ");
                    refillDeckFromDiscard(discardPile, deckPile);
                }
            }
            else{
                System.out.println("Invalid Command. Try Again.");
            }
        }

        // User entered at least 2 characters
        else {
            // Extract location to move card(s) from and to
            String fromLabel = userInput.substring(0, 1).toUpperCase();
            String toLabel = userInput.substring(1, 2).toUpperCase();

            // Check the number of cards to move
            int numCardsToMove = (userInput.length() == 2) ? 1 : Integer.parseInt(userInput.substring(2));

            // Move between tableau lanes
            if (Character.isDigit(fromLabel.charAt(0)) && Character.isDigit(toLabel.charAt(0))) {
                int fromLane = Integer.parseInt(fromLabel) - 1;
                int toLane = Integer.parseInt(toLabel) - 1;

                if (fromLane >= 0 && fromLane < tableau.size() && toLane >= 0 && toLane < tableau.size() && fromLane != toLane) {
                    ArrayList<Card> cardsToMove = tableau.get(fromLane).drawCards(numCardsToMove);
                    System.out.println("TESTING: CARDS TO MOVE: " + cardsToMove);
                    Card destinationCard = tableau.get(toLane).getCard();

                    // Check if destination lane is empty. If so only allow a king to be moved
                    if (cardsToMove != null) {
                        if(destinationCard == null){
                            //checkEmptyLane(cardsToMove, toLane, fromLane, numCardsToMove);
                            //Add in some sort of function that can do this.


                            if (cardsToMove.getFirst().getRank() == Rank.KING) {
                                tableau.get(toLane).addCards(cardsToMove.toArray(new Card[0]));
                                score += 5 * numCardsToMove;
                                moves++;
                                System.out.println("Moved " + cardsToMove + " from lane " + (fromLane + 1) + " to Lane " + (toLane + 1));
                                // Flip card that was beneath the moved card, if it exists and is face down
                                if(!tableau.get(fromLane).isEmpty()){
                                    Card newtopCard = tableau.get(fromLane).getCard();
                                    if(!newtopCard.isFaceUp){
                                        tableau.get(fromLane).flipTopCard();
                                    }
                                }
                            } else {
                                System.out.println("Only a King can be moved to an empty tableau lane.");
                                tableau.get(fromLane).addCards(cardsToMove.toArray(new Card[0])); // Return cards to original pile
                            }

                        } else {
                            // Check the card being moved is one rank lower and alternates colours with the top card in destination pile
                            if(cardsToMove.getFirst().isOneRankLower(destinationCard) && cardsToMove.getFirst().isRed() != destinationCard.isRed()){
                                tableau.get(toLane).addCards(cardsToMove.toArray(new Card[0]));
                                score += 5 * numCardsToMove;
                                moves++;
                                System.out.println("Moved " + cardsToMove + " from lane " + (fromLane + 1) + " to Lane " + (toLane + 1));
                                // Flip card that was beneath the moved card, if it exists and is face down
                                if(!tableau.get(fromLane).isEmpty()){
                                    Card newtopCard = tableau.get(fromLane).getCard();
                                    if(!newtopCard.isFaceUp){
                                        tableau.get(fromLane).flipTopCard();
                                    }
                                }
                            }
                            else {
                                System.out.println("Invalid move. Cards must alternate in colour and be one rank apart");
                                tableau.get(fromLane).addCards(cardsToMove.toArray(new Card[0])); // Return card to original pile
                            }
                        }
                    }else{
                        System.out.println("No Card to move from Lane: " + (fromLane + 1));
                    }
                    System.out.println("INVALID COMMAND. TRY AGAIN.");
                }
                else {
                    System.out.println("Invalid Command!!.");
                }
            }

            // Handle move from discard pile to Tableau Lane or Foundation
            if (fromLabel.equals("P")){
                Card cardToMove = discardPile.drawCard();

                if(cardToMove == null){
                    System.out.println("No card to move from discard pile. ");
                    return;
                }

                // Move to tableau
                if(Character.isDigit(toLabel.charAt(0))){
                    int toLane = Integer.parseInt(toLabel) - 1;
                    if(toLane >= 0 && toLane < 7){
                        Card destinationCard = tableau.get(toLane).getCard();

                        // Rule: Only Kings can be moved to empty tableau lane
                        if(destinationCard == null){
                            //checkEmptyLane(cardToMove, toLane, fromLane, numCardsToMove);
                            if(cardToMove.getRank() == Rank.KING){
                                tableau.get(toLane).addCards(cardToMove);
                                moves++;
                                System.out.println("Moved " + cardToMove + " to Lane " + toLabel);
                            } else {
                                System.out.println("Only a king can be moved to an empty tableau lane. ");
                                discardPile.addCards(cardToMove);
                            }
                        } else {
                            // Rule: Alternating colour and descending rank in tableau
                            if(cardToMove.isOneRankLower(destinationCard) && cardToMove.isRed() != destinationCard.isRed()){
                                tableau.get(toLane).addCards(cardToMove);
                                moves++;
                                System.out.println("Moved " + cardToMove + " to Lane " + toLabel);
                            } else {
                                System.out.println("Invalid move. Cards must alternate in colour and be one rank apart.");
                                discardPile.addCards(cardToMove);
                            }
                        }
                    }
                    else {
                        System.out.println("Invalid Command. Please enter a number corresponding to one of the Tableau lanes.");
                        discardPile.addCards(cardToMove);
                    }
                }

                else if(toLabel.charAt(0) == 'S' || toLabel.charAt(0) == 'C' || toLabel.charAt(0) == 'D' || toLabel.charAt(0) == 'H'){
                    int foundationIndex = getFoundationIndex(cardToMove.getSuit());
                    Card topFoundationCard = foundations.get(foundationIndex).getCard();
                    // Rule: Foundation pile must start with an Ace
                    if (topFoundationCard == null){
                        if(cardToMove.getRank() == Rank.ACE){
                            foundations.get(foundationIndex).addCards(cardToMove);
                            score += 10 * numCardsToMove;
                            moves++;
                            System.out.println("Moved " + cardToMove + " to Foundation.");
                        } else {
                            System.out.println("Only an Ace can be placed in an empty foundation pile. How?? ");
                            discardPile.addCards(cardToMove);
                        }
                    } else {
                        // Rule: Cards in foundation must be in ascending order by suit
                        if(cardToMove.isOneRankHigher(topFoundationCard)){
                            foundations.get(foundationIndex).addCards(cardToMove);
                            score += 10;
                            moves++;
                            System.out.println("Moved " + cardToMove + " to Foundation.");
                        } else {
                            System.out.println("Invalid move. Cards must be placed in ascending order by suit.");
                            discardPile.addCards(cardToMove);
                        }
                    }
                }

                else {
                    System.out.println("Invalid Command end. ");
                    discardPile.addCards(cardToMove);
                }
            }

            // Handle move from tableau to foundation
            if (Character.isDigit(fromLabel.charAt(0)) && Character.isLetter(toLabel.charAt(0))) {
                int fromLane = Integer.parseInt(fromLabel) - 1;
                if(fromLane >= 0 && fromLane <= 7 && toLabel.charAt(0) == 'D' || toLabel.charAt(0) == 'H' || toLabel.charAt(0) == 'S' || toLabel.charAt(0) == 'C'){
                    Card cardToMove = tableau.get(fromLane).drawCard();
                    if(cardToMove != null){
                        int foundationIndex = getFoundationIndex(cardToMove.getSuit());
                        Card topFoundationCard = foundations.get(foundationIndex).getCard();

                        // Foundation pile must start with an ace
                        if(topFoundationCard == null){
                            if(cardToMove.getRank() == Rank.ACE){
                                foundations.get(foundationIndex).addCards(cardToMove);
                                score += 20 * numCardsToMove;
                                moves++;
                                System.out.println("Moved " + cardToMove + " to Foundation.");
                                // Flip card that was beneath the moved card, if it exists and is face down
                                if(!tableau.get(fromLane).isEmpty()){
                                    Card newtopCard = tableau.get(fromLane).getCard();
                                    if(!newtopCard.isFaceUp){
                                        tableau.get(fromLane).flipTopCard();
                                    }
                                }

                            } else {
                                System.out.println("Invalid move. Only an Ace in an empty foundation pile. ");
                                tableau.get(fromLane).addCards(cardToMove); // Return card to discard pile
                            }
                        } else { // Foundation pile already has cards on it
                            // Cards must be in ascending order
                            if (cardToMove.isOneRankHigher(topFoundationCard)){
                                foundations.get(foundationIndex).addCards(cardToMove);
                                score += 10 * numCardsToMove;
                                moves++;
                                System.out.println("Moved " + cardToMove + " to Foundation.");
                                // Flip card that was beneath the moved card, if it exists and is face down
                                if(!tableau.get(fromLane).isEmpty()){
                                    Card newtopCard = tableau.get(fromLane).getCard();
                                    if(!newtopCard.isFaceUp){
                                        tableau.get(fromLane).flipTopCard();
                                    }
                                }
                            } else {
                                System.out.println("Invalid move. Cards must be placed in ascending order by suit. ");
                                tableau.get(fromLane).addCards(cardToMove);
                            }
                        }
                    } else {
                        System.out.println("No card to move form lane " + (fromLane+1));
                    }
                }
                else {
                    System.out.println("Invalid Move. Try Again.");
                }
            }
        }
    }

    private int getMaxLaneSize() {
        int max = 0; // Initialize a variable to keep track of the maximum size
        for (CardPile pile : tableau) { // Iterate through each tableau pile
            if (pile.Size() > max) { // Check if the current pile's size is greater than the current max
                max = pile.Size(); // Update max if the current pile is larger
            }
        }
        return max; // Return the maximum lane size found
    }

    public void refillDeckFromDiscard(CardPile discardPile, CardPile deckPile){
        ArrayList<Card> reversedPile = new ArrayList<>();

        for(int i=discardPile.Size()-1 ; i >= 0 ; i--){
            Card card = discardPile.drawCard();
            card.flip();
            reversedPile.add(card);
        }

        for(Card card : reversedPile){
            deckPile.addCards(card);
        }
    }


    public void checkEmptyLane(ArrayList<Card> cardsToMove, int toLane, int fromLane, int numCardsToMove){

        if (cardsToMove.getFirst().getRank() == Rank.KING) {
            tableau.get(toLane).addCards(cardsToMove.toArray(new Card[0]));
            score += 5 * numCardsToMove;
            moves++;
            System.out.println("Moved " + cardsToMove + " from lane " + (fromLane + 1) + " to Lane " + (toLane + 1));
            // Flip card that was beneath the moved card, if it exists and is face down
            if(!tableau.get(fromLane).isEmpty()){
                Card newtopCard = tableau.get(fromLane).getCard();
                if(!newtopCard.isFaceUp){
                    tableau.get(fromLane).flipTopCard();
                }
            }
        } else {
            System.out.println("Only a King can be moved to an empty tableau lane.");
            tableau.get(fromLane).addCards(cardsToMove.toArray(new Card[0])); // Return cards to original pile
        }
    }

    // Method to get the index of the foundation pile based on suit
    private int getFoundationIndex(Suit suit) {
        switch (suit) {
            case D: return 0; // Diamonds
            case H: return 1; // Hearts
            case C: return 2; // Clubs
            case S: return 3; // Spades
            default: return -1; // Should not happen
        }
    }

    // Method to check game completion
    public boolean checkGameCompletion(ArrayList<CardPile> foundations){
        for (CardPile foundation : foundations){
            // Check if each foundation is full
            if(foundation.Size() != 13){
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args){
        Patience game = new Patience();
        game.play();
    }
}
