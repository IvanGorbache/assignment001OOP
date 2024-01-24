import java.util.ArrayList;

public class Position {
    private int x;
    private int y;

    private ArrayList<ConcretePiece> uniquePieces;

    public Position(int newX, int newY)
    {
        this.x = newX;
        this.y = newY;
        uniquePieces = null;
    }

    public Position(Position newPosition)
    {
        this.x = newPosition.getX();
        this.y = newPosition.getY();
        uniquePieces = null;
    }

    public int getX() {
        return x;
    }

    public void setX(int newX) {
        this.x = newX;
    }

    public int getY() {
        return y;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    @Override
    public String toString() {
        return "("+this.x+", "+this.y+")";
    }

    public ArrayList<ConcretePiece> getUniquePieces() {
        return uniquePieces;
    }

    public boolean addUniquePieces(ConcretePiece piece) {
        if (uniquePieces == null)
        {
            uniquePieces = new ArrayList<>();
        }
        if(!uniquePieces.contains(piece))
        {
            uniquePieces.add(piece);
            return true;
        }
        return false;
    }

    public void removePiece()
    {
        if(uniquePieces!=null && !uniquePieces.isEmpty())
        {
            uniquePieces.remove(uniquePieces.get(uniquePieces.size()-1));
        }
    }
}
