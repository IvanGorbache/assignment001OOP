public class ConcretePlayer implements Player{

    //A private boolean that determines if the player is the first or second player
    private final boolean isPlayerOne;

    //A private integer that counts the number of wins achieved by the player
    private int winsCounter;

    //Constructor
    public ConcretePlayer(boolean isPlayerOne) {
        this.isPlayerOne = isPlayerOne;
        winsCounter = 0;
    }

    //A getter for isPlayerOne
    @Override
    public boolean isPlayerOne() {
        return isPlayerOne;
    }

    //A getter for the number of wins
    @Override
    public int getWins() {
        return winsCounter;
    }

    //Adds a win to the win counter of the player
    public void addWin()
    {
        this.winsCounter+=1;
    }
}
