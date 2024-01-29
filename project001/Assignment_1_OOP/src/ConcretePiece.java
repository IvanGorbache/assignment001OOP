import java.util.Stack;

public abstract class ConcretePiece implements Piece{

    //The name of the piece. (A - Attacker, D - Defender, K - King)
    private final String name;

    //The id of the piece
    private final int id;

    //The type of the piece (♔ ♙ ♟)
    private final String type;

    //The player that owns the piece
    private final Player owner;

    //The total distance that the piece has traveled throughout the duration of the game
    private int distanceTraveled;

    //A stack used to store the positions the piece has traveled to
    private final Stack<Position> moveHistory;

    //Constructor
    public ConcretePiece(String newName, int newId, String newType, Player newOwner, Position startingPos)
    {
        this.name = newName;
        this.id =newId;
        this.type = newType;
        this.owner = newOwner;
        this.moveHistory = new Stack<>();
        this.moveHistory.add(startingPos);
        this.distanceTraveled = 0;
    }

    //A getter for the owner of the piece
    @Override
    public Player getOwner() {
        return this.owner;
    }

    //A getter for the type of the piece
    @Override
    public String getType() {
        return this.type;
    }

    //A getter for the full name of the piece (name + id)
    public String getName() {
        return this.name+this.id;
    }

    //Adds a new move to the move history of the piece
    public void addMove(Position pos)
    {
        calculateDistance(pos,true);
        this.moveHistory.push(pos);
    }

    //removing the last position from the move history and returning it
    public Position removeLastMove()
    {
        //checking that the move history isn't empty
        if(!this.moveHistory.isEmpty())
        {
            //Getting the last move so that we may return it
            Position position = moveHistory.peek();

            //Removing the last move and precalculating the total distance
            calculateDistance(this.moveHistory.pop(),false);

            //Returning the position
            return position;
        }
        return null;
    }

    //Calculating distance between points in the move history.
    //True - add the new distance to the total distance
    //False - sub the new distance from the total distance (used for undo)
    private void calculateDistance(Position pos, boolean add)
    {
        Position lastMove = this.moveHistory.peek();
        int newDistance = Math.abs(pos.getX() - lastMove.getX()) + Math.abs(pos.getY() - lastMove.getY());
        this.distanceTraveled += add?newDistance:-newDistance;
    }

    //A getter for the total distanceTraveled by the piece
    public int getDistanceTraveled() {
        return this.distanceTraveled;
    }

    //A getter fot the size of the moveHistory
    public int getMoveHistorySize()
    {
        return this.moveHistory.size();
    }

    //A getter for the moveHistory
    public Stack<Position> getMoveHistory()
    {
        return this.moveHistory;
    }

    //A getter for the id of the piece
    public int getId() {
        return id;
    }
}
