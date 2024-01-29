public class Pawn extends ConcretePiece{

    //Private integer for storing the number of kills
    private int killCounter;

    //Constructor with parameters to construct the super class, ConcretePiece
    public Pawn(Player newOwner)
    {
        //Using the given parameters to construct a ConcretePiece
        this.setName(newOwner.isPlayerOne()?"D":"A");
        this.setType(newOwner.isPlayerOne()?"♙":"♟");
        this.setOwner(newOwner);

        //Setting the killCounter to 0
        this.killCounter = 0;
    }

    //A getter for the killCounter
    public int getKillCounter() {
        return this.killCounter;
    }

    //Adds or subtracts from the killCounter (Adds 1 when getting a kill. Subtracts 1 when undoing a kill)
    //True - Add 1 kill
    //False - Sub 1 kill
    public void modifyKillCounter(boolean add)
    {
        killCounter+=add?1:-1;
    }
}
