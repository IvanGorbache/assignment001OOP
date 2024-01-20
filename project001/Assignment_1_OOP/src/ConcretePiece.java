import java.util.Stack;

public abstract class ConcretePiece implements Piece{

    private final String name;
    private final String type;
    private final Player owner;
    private int distanceTraveled;
    private final Stack<Position> moveHistory;

    public ConcretePiece(String newName, String newType, Player newOwner, Position startingPos)
    {
        this.name = newName;
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
        return this.name;
    }
    public void addMove(Position pos)
    {
        calculateDistance(pos,true);
        this.moveHistory.push(pos);
    }
    public void removeLastMove()
    {
        if(!this.moveHistory.isEmpty())
        {
            calculateDistance(this.moveHistory.pop(),false);
        }
    }

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

}
