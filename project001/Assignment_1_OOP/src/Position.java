import java.util.ArrayList;

public class Position {

    //Private integer variable denoting the X coordinate
    private int x;

    //Private integer variable denoting the Y coordinate
    private int y;

    //Private arrayList storing all the unique pieces that stepped on that spot
    private ArrayList<ConcretePiece> uniquePieces;

    //A constructor for Position that gets X and Y coordinates
    public Position(int newX, int newY)
    {
        //Setting up the new coordinates
        this.x = newX;
        this.y = newY;

        //Setting the arraylist of unique pieces to null
        uniquePieces = null;
    }

    //A copy constructor that copies the coordinates of the given newPosition
    public Position(Position newPosition)
    {
        //Copying the coordinates of the given newPosition
        this.x = newPosition.getX();
        this.y = newPosition.getY();

        //Setting the arraylist of unique pieces to null
        uniquePieces = null;
    }

    //A getter for the X coordinates
    public int getX() {
        return x;
    }

    //A setter for the X coordinates
    public void setX(int newX) {
        this.x = newX;
    }

    //A getter for the Y coordinates
    public int getY() {
        return y;
    }

    //A setter for the X coordinates
    public void setY(int newY) {
        this.y = newY;
    }

    //Converting the coordinates to a string
    @Override
    public String toString() {
        return "("+this.x+", "+this.y+")";
    }

    //A getter for the arraylist of uniquePieces
    public ArrayList<ConcretePiece> getUniquePieces() {
        return uniquePieces;
    }

    //Adds a new piece to the arraylist of unique pieces if it doesn't contain it already
    public void addUniquePieces(ConcretePiece piece) {
        //Creating a new arrayList if it doesn't exist already
        if (uniquePieces == null) {
            uniquePieces = new ArrayList<>();
        }
        //Checking the that the given piece isn't null and that the arraylist doesn't contain it already
        if(piece != null && !uniquePieces.contains(piece)) {
            uniquePieces.add(piece);
        }
    }

    //Removing a piece from the arraylist of unique pieces when using the undo button.
    public void removePiece(ConcretePiece piece)
    {
        //Checking that the arraylist isn't null
        if(uniquePieces!=null)
        {
            //Removing the desired piece
            uniquePieces.remove(piece);
        }
    }
}
