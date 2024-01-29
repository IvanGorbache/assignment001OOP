public class King extends ConcretePiece{

    //Constructor with parameters to construct the super class, ConcretePiece
    public King(String newName, int newId, Player newOwner,Position startingPosition)
    {
        //Using the given parameters to construct a ConcretePiece
        super(newName, newId,"♔",newOwner,startingPosition);
    }
}
