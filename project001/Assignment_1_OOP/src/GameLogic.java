import java.net.ConnectException;
import java.util.*;

public class GameLogic implements PlayableLogic{

    private final ConcretePlayer playerTwoAttack, playerOneDefend;
    private static ConcretePlayer victor;
    private ConcretePiece[][] board;
    private Position[][] uniqueSteps;
    private ArrayList<ConcretePiece> allPieces;
    private ArrayList<Position> allPositions;
    private Stack<ConcretePiece[][]> history;
    private Stack<ConcretePiece> attackHistory;
    private Stack<ConcretePiece> piecesHistory;
    private Stack<Position> positionsHistory;
    private boolean secondPlayerTurn;
    private boolean isGameOver;

    private int attackerCount, defenderCount;
    public GameLogic()
    {
        this.playerOneDefend = new ConcretePlayer(true);
        this.playerTwoAttack = new ConcretePlayer(false);
        secondPlayerTurn = true;
        isGameOver = false;
        attackerCount = 24;
        defenderCount = 12;
        createBoard();
    }
    private void createBoard() {
        isGameOver = false;
        history = new Stack<>();
        attackHistory = new Stack<>();
        piecesHistory = new Stack<>();
        positionsHistory = new Stack<>();
        uniqueSteps = new Position[getBoardSize()][getBoardSize()];
        board = new ConcretePiece[getBoardSize()][getBoardSize()];
        board[3][0] = new Pawn("A1", playerTwoAttack, new Position(3,0));
        board[4][0] = new Pawn("A2", playerTwoAttack, new Position(4,0));
        board[5][0] = new Pawn("A3", playerTwoAttack, new Position(5,0));
        board[6][0] = new Pawn("A4", playerTwoAttack, new Position(6,0));
        board[7][0] = new Pawn("A5", playerTwoAttack, new Position(7,0));
        board[5][1] = new Pawn("A6", playerTwoAttack, new Position(5,1));
        board[0][3] = new Pawn("A7", playerTwoAttack, new Position(0,3));
        board[10][3] = new Pawn("A8", playerTwoAttack, new Position(10,3));
        board[0][4] = new Pawn("A9", playerTwoAttack, new Position(0,4));
        board[10][4] = new Pawn("A10", playerTwoAttack, new Position(10,4));
        board[0][5] = new Pawn("A11", playerTwoAttack, new Position(0,5));
        board[1][5] = new Pawn("A12", playerTwoAttack, new Position(1,5));
        board[9][5] = new Pawn("A13", playerTwoAttack, new Position(9,5));
        board[10][5] = new Pawn("A14", playerTwoAttack, new Position(10,5));
        board[0][6] = new Pawn("A15", playerTwoAttack, new Position(0,6));
        board[10][6] = new Pawn("A16", playerTwoAttack, new Position(10,6));
        board[0][7] = new Pawn("A17", playerTwoAttack, new Position(0,7));
        board[10][7] = new Pawn("A18", playerTwoAttack, new Position(10,7));
        board[5][9] = new Pawn("A19", playerTwoAttack, new Position(5,9));
        board[3][10] = new Pawn("A20", playerTwoAttack, new Position(3,10));
        board[4][10] = new Pawn("A21", playerTwoAttack, new Position(4,10));
        board[5][10] = new Pawn("A22", playerTwoAttack, new Position(5,10));
        board[6][10] = new Pawn("A23", playerTwoAttack, new Position(6,10));
        board[7][10] = new Pawn("A24", playerTwoAttack, new Position(7,10));
        board[5][3] = new Pawn("D1", playerOneDefend, new Position(5,3));
        board[4][4] = new Pawn("D2", playerOneDefend, new Position(4,4));
        board[5][4] = new Pawn("D3", playerOneDefend, new Position(5,4));
        board[6][4] = new Pawn("D4", playerOneDefend, new Position(6,4));
        board[3][5] = new Pawn("D5", playerOneDefend, new Position(3,5));
        board[4][5] = new Pawn("D6", playerOneDefend, new Position(4,5));
        board[5][5] = new King("K7", playerOneDefend, new Position(5,5));
        board[6][5] = new Pawn("D8", playerOneDefend, new Position(6,5));
        board[7][5] = new Pawn("D9", playerOneDefend, new Position(7,5));
        board[4][6] = new Pawn("D10", playerOneDefend, new Position(4,6));
        board[5][6] = new Pawn("D11", playerOneDefend, new Position(5,6));
        board[6][6] = new Pawn("D12", playerOneDefend, new Position(6,6));
        board[5][7] = new Pawn("D13", playerOneDefend, new Position(5,7));
        getAllPieces();
        getAllPositionsStart();
    }
    private void getAllPieces()
    {
        this.allPieces = new ArrayList<>();
        for (int i=0;i<getBoardSize();i++)
        {
            for (int j = 0;j<getBoardSize();j++)
            {
                if(board[i][j]!=null)
                {
                    allPieces.add(board[i][j]);
                }
            }
        }
    }
    private void getAllPositions()
    {
        this.allPositions = new ArrayList<>();
        for (int i=0;i<getBoardSize();i++)
        {
            for (int j = 0;j<getBoardSize();j++)
            {
                if(uniqueSteps[i][j]!=null)
                {
                    allPositions.add(uniqueSteps[i][j]);
                }
            }
        }
    }
    private void getAllPositionsStart()
    {
        for (int i=0;i<getBoardSize();i++)
        {
            for (int j = 0;j<getBoardSize();j++)
            {
                if(board[i][j]!=null)
                {
                    uniqueSteps[i][j] = new Position(i,j);
                    uniqueSteps[i][j].addUniquePieces(board[i][j]);
                }
            }
        }
    }
    @Override
    public boolean move(Position a, Position b) {
        if (isMoveLegal(a,b)) {
            updateHistory();
            secondPlayerTurn = !secondPlayerTurn;
            this.board[b.getX()][b.getY()] = this.board[a.getX()][a.getY()];
            this.board[a.getX()][a.getY()] = null;
            this.board[b.getX()][b.getY()].addMove(b);
            this.piecesHistory.add(this.board[b.getX()][b.getY()]);
            attackHistory.add(null);
            if(uniqueSteps[b.getX()][b.getY()]==null)
            {
                uniqueSteps[b.getX()][b.getY()] = new Position(b);
            }
            if(uniqueSteps[b.getX()][b.getY()].addUniquePieces(board[b.getX()][b.getY()]))
            {
                positionsHistory.add(uniqueSteps[b.getX()][b.getY()]);
            }
            else
            {
                positionsHistory.add(null);
            }
            if(getPieceAtPosition(b) instanceof Pawn)
            {
                checkKill(b);
            }
            else
            {
                checkEscape(b);
            }
            if(attackerCount ==0)
            {
                victor = playerOneDefend;
                isGameOver = true;
                printStatistics();
            }
            return true;
        }
        return false;
    }
    private void updateHistory() {
        ConcretePiece[][] copyBoard = new ConcretePiece[getBoardSize()][getBoardSize()];
        for (int i = 0; i < getBoardSize(); i++) {
            System.arraycopy(this.board[i], 0, copyBoard[i], 0, getBoardSize());
        }
        history.push(copyBoard);
    }
    private void checkEscape(Position pos)
    {
        isGameOver = getPieceAtPosition(pos) instanceof King && isEdge(pos);
        if(isGameOver)
        {
            victor = playerOneDefend;
            printStatistics();
        }
    }
    private boolean isMoveLegal(Position a, Position b)
    {
        return canEnter(a,b) && isPieceTurn(a) && isPathLegal(a,b) && isPathClear(a,b) && isPawnCastleCase(a,b);
    }
    private boolean isPawnCastleCase(Position a, Position b)
    {
        return!(getPieceAtPosition(a) instanceof Pawn && isEdge(b));
    }
    private boolean canEnter(Position a, Position b)
    {
        return board[a.getX()][a.getY()] != null && board[b.getX()][b.getY()] == null;
    }
    private boolean isPathLegal(Position a, Position b)
    {
        return a.getX() == b.getX() ^ a.getY() == b.getY();
    }
    private boolean isPieceTurn(Position a)
    {
        return getPieceAtPosition(a).getOwner().isPlayerOne() != secondPlayerTurn;
    }
    private boolean isPathClear(Position a, Position b)
    {
        Position temp;
        int xSign, ySign, xDiff, yDiff;
        temp = new Position(a);
        xDiff = temp.getX() - b.getX();
        yDiff = temp.getY() - b.getY();
        xSign = xDiff != 0 ? -(xDiff) / Math.abs(xDiff) : 0;
        ySign = yDiff != 0 ? -(yDiff) / Math.abs(yDiff) : 0;
        while (temp.getX() != b.getX() || temp.getY() != b.getY()) {
            temp.setX(temp.getX() + xSign);
            temp.setY(temp.getY() + ySign);
            if (getPieceAtPosition(temp) != null) {
                return false;
            }
        }
        return true;
    }
    @Override
    public Piece getPieceAtPosition(Position position) {
        return this.board[position.getX()][position.getY()];
    }
    public boolean isEdge(Position pos)
    {
        Position[] edgeSquare = new Position[]{
                new Position(0, 0),
                new Position(10, 10),
                new Position(0, 10),
                new Position(10, 0)};
        for (Position tempPos:edgeSquare)
        {
            if ((tempPos.getX() == pos.getX())&&(tempPos.getY() == pos.getY()))
            {
                return true;
            }
        }
        return false;
    }
    private void checkKill(Position attacker) {
        Position[] directions = getSurroundingAreas(attacker);
        for (Position target : directions) {
            if (canAttack(attacker,target)) {
                if ((getPieceAtPosition(target) instanceof Pawn) && (isPawnSurrounded(target, attacker))) {
                    ((Pawn)getPieceAtPosition(attacker)).modifyKillCounter(true);
                    attackHistory.add(this.board[attacker.getX()][attacker.getY()]);
                    Position pos = getOppositePosition(attacker, target);
                    if(inMapRange(pos) && getPieceAtPosition(pos)!=null)
                    {
                        ((Pawn)getPieceAtPosition(pos)).modifyKillCounter(true);
                        attackHistory.add(this.board[pos.getX()][pos.getY()]);
                    }
                    if(secondPlayerTurn)
                    {
                        attackerCount--;
                    }
                    else {
                        defenderCount--;
                    }
                    board[target.getX()][target.getY()] = null;
                } else if ((getPieceAtPosition(target) instanceof King)) {
                    isGameOver = isKingSurrounded(target);
                    if(isGameOver)
                    {
                        victor = playerTwoAttack;
                        printStatistics();
                    }
                }
            }
        }

    }
    private Position getOppositePosition(Position attacker, Position target)
    {
        int newX = target.getX() + (target.getX() - attacker.getX());
        int newY = target.getY() + (target.getY() - attacker.getY());
        return new Position(newX, newY);
    }
    private boolean canAttack(Position attacker, Position target)
    {
        return isPositionOccupied(target) && isTargetHostile(attacker,target);
    }
    private boolean isPositionOccupied(Position pos)
    {
        return (inMapRange(pos) && getPieceAtPosition(pos) != null);
    }
    private boolean isTargetHostile(Position attacker, Position target)
    {
        return getPieceAtPosition(target).getOwner() != getPieceAtPosition(attacker).getOwner();
    }
    private Position[] getSurroundingAreas(Position pos)
    {
        return new Position[]{
                new Position(pos.getX() + 1, pos.getY()),
                new Position(pos.getX() - 1, pos.getY()),
                new Position(pos.getX(), pos.getY() + 1),
                new Position(pos.getX(), pos.getY() - 1)
        };
    }
    private boolean inMapRange(Position pos) {
        int mapSize = getBoardSize();
        return (pos.getX() < mapSize && pos.getX() >= 0) && (pos.getY() < mapSize && pos.getY() >= 0);
    }

    private boolean isPawnSurrounded(Position target, Position attacker) {
        Position pos = new Position(target.getX() + (target.getX() - attacker.getX()), target.getY() + (target.getY() - attacker.getY()));
        return isEdge(pos) || (!inMapRange(pos)) || (isPositionOccupied(pos) && isTargetHostile(pos,target) && (!(getPieceAtPosition(pos) instanceof King)));
    }
    private boolean isKingSurrounded(Position target) {
        Position[] directions = getSurroundingAreas(target);
        for (Position pos : directions) {
            if ((inMapRange(pos)) && ((getPieceAtPosition(pos) == null) || (!isTargetHostile(pos,target)))){
                return false;
            }
        }
        playerTwoAttack.addWin();
        return true;
    }

    @Override
    public Player getFirstPlayer() {
        return this.playerOneDefend;
    }

    @Override
    public Player getSecondPlayer() {
        return this.playerTwoAttack;
    }

    @Override
    public boolean isGameFinished() {
        return this.isGameOver;
    }

    @Override
    public boolean isSecondPlayerTurn() {
        return this.secondPlayerTurn;
    }

    @Override
    public void reset() {
        this.secondPlayerTurn = true;
        createBoard();
    }

    @Override
    public void undoLastMove() {
        if(!history.isEmpty())
        {
            board = history.pop();
            secondPlayerTurn = !secondPlayerTurn;
        }
        if(!attackHistory.isEmpty())
        {
            if(attackHistory.peek()==null)
            {
                attackHistory.pop();
            }
            else
            {
                while (attackHistory.peek()!=null)
                {
                    ((Pawn)attackHistory.pop()).modifyKillCounter(false);
                }
            }
        }
        if(!piecesHistory.isEmpty())
        {
            piecesHistory.pop().removeLastMove();
        }
        if(!positionsHistory.isEmpty())
        {
            if(positionsHistory.peek() != null)
            {
                positionsHistory.pop().removePiece();
            }
        }
    }
    private void printStatistics()
    {
        allPieces.sort(new StepsComparator());
        int size, killCount;
        for (ConcretePiece piece: allPieces)
        {
            size = piece.getMoveHistorySize();
            if (size>=2)
            {
                System.out.print(piece.getName()+": [");
                for (Position position: piece.getMoveHistory())
                {
                    System.out.print(position.toString());
                    if((size--)>1)
                    {
                        System.out.print(", ");
                    }
                }
                System.out.println("]");
            }
        }
        printStars();
        allPieces.sort(new KillCountComparator().reversed());
        for (ConcretePiece piece: allPieces)
        {
            if(piece instanceof Pawn)
            {
                killCount = ((Pawn)piece).getKillCounter();
                if(killCount>0)
                {
                    System.out.println(piece.getName()+": "+killCount+" kills");
                }
            }
        }
        printStars();
        allPieces.sort(new DistanceComparator().reversed());
        for (ConcretePiece piece: allPieces)
        {
            size = piece.getDistanceTraveled();
            if(size>0)
            {
                System.out.println(piece.getName()+": "+size+" squares");
            }
        }
        printStars();
        getAllPositions();
        allPositions.sort(new UniquePiecesComparator());
        for (Position position: allPositions)
        {
            if(position.getUniquePieces().size()>1)
            {
                System.out.println(position.toString()+position.getUniquePieces().size()+" pieces");
            }
        }
        printStars();
    }
    static class StepsComparator implements Comparator<ConcretePiece>
    {
        public int compare(ConcretePiece obj1, ConcretePiece obj2)
        {
            if(obj1.getOwner()!=obj2.getOwner())
            {
                return obj1.getOwner()==victor?-1:1;
            }
            else if(obj1.getMoveHistorySize() == obj2.getMoveHistorySize())
            {
                if(obj1 instanceof King)
                {
                    return -1;
                }
                if(obj2 instanceof King)
                {
                    return 1;
                }
                return obj1.getName().compareTo(obj2.getName());
            }
            return obj1.getMoveHistorySize() - obj2.getMoveHistorySize();
        }
    }
    static class DistanceComparator implements Comparator<ConcretePiece>{
        public int compare(ConcretePiece obj1, ConcretePiece obj2) {
            if((obj1).getDistanceTraveled() == (obj2).getDistanceTraveled())
            {
                if(obj1.getOwner()!=obj2.getOwner())
                {
                    return obj1.getOwner()==victor?-1:1;
                }
                else{
                    if(obj1 instanceof King)
                    {
                        return 1;
                    }
                    if(obj2 instanceof King)
                    {
                        return -1;
                    }
                    return obj2.getName().compareTo(obj1.getName());
                }
            }
            return ((obj1).getDistanceTraveled() - (obj2).getDistanceTraveled());
        }
    }
    static class KillCountComparator implements Comparator<ConcretePiece>{
        public int compare(ConcretePiece obj1, ConcretePiece obj2) {
            if(obj1 instanceof King)
            {
                return -1;
            }
            if(obj2 instanceof King)
            {
                return 1;
            }
            if(((Pawn)obj1).getKillCounter() == ((Pawn)obj2).getKillCounter())
            {
                if(obj1.getOwner()!=obj2.getOwner())
                {
                    return obj1.getOwner()==victor?1:-1;
                }
                else{
                    return obj1.getName().compareTo(obj2.getName());
                }
            }
            return ((Pawn)obj1).getKillCounter() - ((Pawn)obj2).getKillCounter();
        }
    }
    static class UniquePiecesComparator implements Comparator<Position> {
        public int compare(Position obj1, Position obj2) {
            if (obj1.getUniquePieces().size() == obj2.getUniquePieces().size())
            {
                if(obj1.getX()== obj2.getX())
                {
                    return obj1.getY()-obj2.getY();
                }
                return obj1.getX()-obj2.getX();
            }
            return obj2.getUniquePieces().size()-obj1.getUniquePieces().size();
        }
    }
    private void printStars()
    {
        for (int i = 0;i<75;i++)
        {
            System.out.print("*");
        }
        System.out.println();
    }
    @Override
    public int getBoardSize() {
        return 11;
    }
}
