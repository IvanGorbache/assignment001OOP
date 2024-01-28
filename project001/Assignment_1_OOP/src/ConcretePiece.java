import java.util.Stack;

public abstract class ConcretePiece implements Piece{

    private final String name;

    private final int id;
    private final String type;
    private final Player owner;
    private int distanceTraveled;
    private final Stack<Position> moveHistory;

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
    @Override
    public Player getOwner() {
        return this.owner;
    }
    @Override
    public String getType() {
        return this.type;
    }
    public String getName() {
        return this.name+this.id;
    }
    public void addMove(Position pos)
    {
        calculateDistance(pos,true);
        this.moveHistory.push(pos);
    }
    //removing the last position from the move history and returning it
    public Position removeLastMove()
    {
        if(!this.moveHistory.isEmpty())
        {
            Position position = moveHistory.peek();
            calculateDistance(this.moveHistory.pop(),false);
            return position;
        }
        return null;
    }

    //Calculating distance between points in the move history.
    private void calculateDistance(Position pos, boolean add)
    {
        Position lastMove = this.moveHistory.peek();
        int newDistance = Math.abs(pos.getX() - lastMove.getX()) + Math.abs(pos.getY() - lastMove.getY());
        this.distanceTraveled += add?newDistance:-newDistance;
    }

    public int getDistanceTraveled() {
        return this.distanceTraveled;
    }

    public int getMoveHistorySize()
    {
        return this.moveHistory.size();
    }

    public Stack<Position> getMoveHistory()
    {
        return this.moveHistory;
    }

    public int getId() {
        return id;
    }
}
