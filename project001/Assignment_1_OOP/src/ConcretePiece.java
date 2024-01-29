import java.util.Stack;

public abstract class ConcretePiece implements Piece{

    //The name of the piece. (A - Attacker, D - Defender, K - King)
    private String name;

    //The id of the piece
    private int id;

    //The type of the piece (♔ ♙ ♟)
    private String type;

    //The player that owns the piece
    private Player owner;

    //The total distance that the piece has traveled throughout the duration of the game
    private int distanceTraveled;

    //A stack used to store the positions the piece has traveled to
    private final Stack<Position> moveHistory;

    private static int attackerID = 1;
    private static int defenderID = 1;

    //Constructor
    public ConcretePiece()
    {
        this.moveHistory = new Stack<>();
        this.distanceTraveled = 0;
    }

    //Setting the id of the owner
    public void setId()
    {
        this.id = this.owner.isPlayerOne()?defenderID++:attackerID++;
    }

    //Setting the owner of the piece
    public void setOwner(Player newOwner)
    {
        this.owner = newOwner;

        //Setting the id only after we get a new owner
        setId();
    }

    //Setting the name of the piece
    public void setName(String newName)
    {
        this.name = newName;
    }

    //Setting the type of the piece
    public void setType(String newType)
    {
        this.type = newType;
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
        if(!moveHistory.isEmpty())
        {
            Position lastMove = this.moveHistory.peek();
            int newDistance = Math.abs(pos.getX() - lastMove.getX()) + Math.abs(pos.getY() - lastMove.getY());
            this.distanceTraveled += add?newDistance:-newDistance;
        }
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

    public static void resetID()
    {
        defenderID = 1;
        attackerID = 1;
    }
}
