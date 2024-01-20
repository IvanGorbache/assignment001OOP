public class ConcretePlayer implements Player{

    private final boolean isPlayerOne;
    private int winsCounter;
    public ConcretePlayer(boolean isPlayerOne) {
        this.isPlayerOne = isPlayerOne;
        winsCounter = 0;
    }
    @Override
    public boolean isPlayerOne() {
        return isPlayerOne;
    }

    @Override
    public int getWins() {
        return winsCounter;
    }

    public void addWin()
    {
        this.winsCounter+=1;
    }
}
