public class Pawn extends ConcretePiece{
    private int killCounter;

    public Pawn(String newName, int newId ,Player newOwner, Position startingPosition)
    {
        super(newName, newId ,newOwner.isPlayerOne()?"♙":"♟",newOwner,startingPosition);
        this.killCounter = 0;
    }

    public int getKillCounter() {
        return this.killCounter;
    }

    public void modifyKillCounter(boolean add)
    {
        killCounter+=add?1:-1;
    }
}
