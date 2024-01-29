public class King extends ConcretePiece{

    //Constructor with parameters to construct the super class, ConcretePiece
    public King(Player newOwner)
    {
        //Using the given parameters to construct a ConcretePiece
        this.setOwner(newOwner);
        this.setType("♔");
        this.setName("K");
    }
}
