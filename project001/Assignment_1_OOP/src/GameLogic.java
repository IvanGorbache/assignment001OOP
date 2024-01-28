import java.net.ConnectException;
import java.util.*;

public class GameLogic implements PlayableLogic{

    //Private ConcretePlayer variables used to store the players
    private final ConcretePlayer playerTwoAttack, playerOneDefend;

    //Private static ConcretePlayer variable used to store the victor of the game for use in the comparators
    private static ConcretePlayer victor;

    //A 2D array of ConcretePiece used to represent the current position of all Pieces on the board
    private ConcretePiece[][] board;

    //A 2D array of Position used to keep track of the number of unique Pieces that stepped on each tile
    private Position[][] uniqueSteps;

    //An ArrayList of ConcretePiece used to gather all pieces for printing the statistics
    private ArrayList<ConcretePiece> allPieces;

    //An ArrayList of position used to gather all positions for printing the statistics
    private ArrayList<Position> allPositions;

    //A stack of 2D array of ConcretePiece used to store information about the location of Pieces and to revive pieces
    private Stack<ConcretePiece[][]> history;

    //A stack of ConcretePiece that managed to score a kill during their turn
    private Stack<ConcretePiece> attackHistory;

    //A stack of ConcretePiece that made a move on the board
    private Stack<ConcretePiece> piecesHistory;

    //A boolean variable used to keep track on who's turn it is.
    private boolean secondPlayerTurn;

    //A boolean used to check if the game is over.
    private boolean isGameOver;

    //A variable used to keep track of the team sizes in case we enter a state in which one of the teams is unable to win due to not having enough pieces
    //The minimum for attacks is 2 because a minimum of 3 is required to capture a king
    private int attackerCount, defenderCount;

    //The constructor takes in no variables and creates all that is needed to start a game
    public GameLogic()
    {
        //Creating our players.
        this.playerOneDefend = new ConcretePlayer(true);
        this.playerTwoAttack = new ConcretePlayer(false);

        //The attacking team always starts first (Which is player two)
        secondPlayerTurn = true;

        //The game can't be over before it has even begun.
        isGameOver = false;

        //Setting the team sizes
        attackerCount = 24;
        defenderCount = 12;

        //Creating the board
        createBoard();
    }

    //Creating the board along with the stacks used to store historical data related to the board.
    private void createBoard() {

        //Called here because finishing a game restarts the board
        isGameOver = false;

        //Initializing  all stacks related to historical data.
        history = new Stack<>();
        attackHistory = new Stack<>();
        piecesHistory = new Stack<>();

        //Initializing  a 2D array for keeping track of the number of unique pieces that stepped on each tile
        uniqueSteps = new Position[getBoardSize()][getBoardSize()];

        //Initializing  the board
        board = new ConcretePiece[getBoardSize()][getBoardSize()];

        //The struggle itself towards the heights is enough to fill a man's heart. One must imagine Sisyphus happy.
        //Creating all the pieces and spreading them on the board
        board[3][0] = new Pawn("A",1, playerTwoAttack, new Position(3,0));
        board[4][0] = new Pawn("A",2,  playerTwoAttack, new Position(4,0));
        board[5][0] = new Pawn("A",3, playerTwoAttack, new Position(5,0));
        board[6][0] = new Pawn("A", 4,playerTwoAttack, new Position(6,0));
        board[7][0] = new Pawn("A", 5,playerTwoAttack, new Position(7,0));
        board[5][1] = new Pawn("A", 6,playerTwoAttack, new Position(5,1));
        board[0][3] = new Pawn("A", 7,playerTwoAttack, new Position(0,3));
        board[10][3] = new Pawn("A", 8,playerTwoAttack, new Position(10,3));
        board[0][4] = new Pawn("A", 9,playerTwoAttack, new Position(0,4));
        board[10][4] = new Pawn("A", 10,playerTwoAttack, new Position(10,4));
        board[0][5] = new Pawn("A", 11,playerTwoAttack, new Position(0,5));
        board[1][5] = new Pawn("A", 12,playerTwoAttack, new Position(1,5));
        board[9][5] = new Pawn("A", 13,playerTwoAttack, new Position(9,5));
        board[10][5] = new Pawn("A", 14,playerTwoAttack, new Position(10,5));
        board[0][6] = new Pawn("A", 15,playerTwoAttack, new Position(0,6));
        board[10][6] = new Pawn("A", 16,playerTwoAttack, new Position(10,6));
        board[0][7] = new Pawn("A", 17,playerTwoAttack, new Position(0,7));
        board[10][7] = new Pawn("A", 18,playerTwoAttack, new Position(10,7));
        board[5][9] = new Pawn("A", 19,playerTwoAttack, new Position(5,9));
        board[3][10] = new Pawn("A", 20,playerTwoAttack, new Position(3,10));
        board[4][10] = new Pawn("A", 21,playerTwoAttack, new Position(4,10));
        board[5][10] = new Pawn("A", 22,playerTwoAttack, new Position(5,10));
        board[6][10] = new Pawn("A", 23,playerTwoAttack, new Position(6,10));
        board[7][10] = new Pawn("A", 24,playerTwoAttack, new Position(7,10));
        board[5][3] = new Pawn("D",1, playerOneDefend, new Position(5,3));
        board[4][4] = new Pawn("D",2, playerOneDefend, new Position(4,4));
        board[5][4] = new Pawn("D", 3,playerOneDefend, new Position(5,4));
        board[6][4] = new Pawn("D", 4,playerOneDefend, new Position(6,4));
        board[3][5] = new Pawn("D", 5,playerOneDefend, new Position(3,5));
        board[4][5] = new Pawn("D", 6,playerOneDefend, new Position(4,5));
        board[5][5] = new King("K", 7,playerOneDefend, new Position(5,5));
        board[6][5] = new Pawn("D", 8,playerOneDefend, new Position(6,5));
        board[7][5] = new Pawn("D", 9,playerOneDefend, new Position(7,5));
        board[4][6] = new Pawn("D", 10,playerOneDefend, new Position(4,6));
        board[5][6] = new Pawn("D", 11,playerOneDefend, new Position(5,6));
        board[6][6] = new Pawn("D", 12,playerOneDefend, new Position(6,6));
        board[5][7] = new Pawn("D", 13,playerOneDefend, new Position(5,7));

        //Gathering all the pieces to an arraylist for printing statistics
        getAllPieces();

        //Initializing all the positions that have a piece in them at the start
        getAllPositionsStart();
    }

    //Gathering all the pieces to an arraylist for printing statistics
    private void getAllPieces()
    {
        this.allPieces = new ArrayList<>();
        for (int i=0;i<getBoardSize();i++)
        {
            for (int j = 0;j<getBoardSize();j++)
            {
                //Only adding pieces from positions on the board that aren't empty
                if(board[i][j]!=null)
                {
                    allPieces.add(board[i][j]);
                }
            }
        }
    }

    //Gathering all the positions that were stepped on to an arraylist for printing statistics
    private void getAllPositions()
    {
        this.allPositions = new ArrayList<>();
        for (int i=0;i<getBoardSize();i++)
        {
            for (int j = 0;j<getBoardSize();j++)
            {
                //Gathering all the positions that were stepped on
                if(uniqueSteps[i][j]!=null)
                {
                    allPositions.add(uniqueSteps[i][j]);
                }
            }
        }
    }

    //Initializing all the positions that have a piece in them at the start
    private void getAllPositionsStart()
    {
        for (int i=0;i<getBoardSize();i++)
        {
            for (int j = 0;j<getBoardSize();j++)
            {
                //Only adding pieces from positions on the board that aren't empty
                if(board[i][j]!=null)
                {
                    uniqueSteps[i][j] = new Position(i,j);
                    uniqueSteps[i][j].addUniquePieces(board[i][j]);
                }
            }
        }
    }

    //The quintessential move function that gets a starting position a and a finish position b
    @Override
    public boolean move(Position a, Position b) {
        //checking if moving a piece from a to b is even legal
        if (isMoveLegal(a,b)) {
            //Updating history to the state before the move was executed
            updateHistory();

            //Changing turns
            secondPlayerTurn = !secondPlayerTurn;

            //Actually moving the piece
            this.board[b.getX()][b.getY()] = this.board[a.getX()][a.getY()];
            this.board[a.getX()][a.getY()] = null;

            //Adding the move to the piece
            this.board[b.getX()][b.getY()].addMove(b);

            //Adding the piece to the history
            this.piecesHistory.add(this.board[b.getX()][b.getY()]);

            //Adding a null to the stack of pieces tha attacked to operate them
            attackHistory.add(null);

            //Updating the unique step counter on the position
            //Creating one if there isn't one already
            if(uniqueSteps[b.getX()][b.getY()]==null)
            {
                uniqueSteps[b.getX()][b.getY()] = new Position(b);
            }
            uniqueSteps[b.getX()][b.getY()].addUniquePieces(board[b.getX()][b.getY()]);

            //Checking if the piece is a pawn so that we may perform an attack with it
            if(getPieceAtPosition(b) instanceof Pawn)
            {
                //performing an attack
                checkKill(b);
            }
            else
            {
                //if it's not a pawn, it's obviously a king. We check if it managed to escape.
                checkEscape(b);
            }

            //For the special case where the attacking team is too small to capture the king
            if(attackerCount == 2)
            {
                //In that case the defender wins
                victor = playerOneDefend;
                isGameOver = true;
                printStatistics();
            }

            //returning true if the move is successful
            return true;
        }
        //returning false if the move is illegal
        return false;
    }

    //Used to copy the current state of the board and push it to a stack
    private void updateHistory() {
        ConcretePiece[][] copyBoard = new ConcretePiece[getBoardSize()][getBoardSize()];
        for (int i = 0; i < getBoardSize(); i++) {
            System.arraycopy(this.board[i], 0, copyBoard[i], 0, getBoardSize());
        }
        history.push(copyBoard);
    }

    //checking if the king has reach one of the four corners of the map
    private void checkEscape(Position pos)
    {
        isGameOver = getPieceAtPosition(pos) instanceof King && isEdge(pos);
        if(isGameOver)
        {
            victor = playerOneDefend;
            printStatistics();
        }
    }

    //Combines multiple checks to check if a move is legal
    private boolean isMoveLegal(Position a, Position b)
    {
        return canEnter(a,b) && isPieceTurn(a) && isPathLegal(a,b) && isPathClear(a,b) && isPawnCastleCase(a,b);
    }

    //Checks for the specific case where a pawn tries to enter a corner
    private boolean isPawnCastleCase(Position a, Position b)
    {
        return!(getPieceAtPosition(a) instanceof Pawn && isEdge(b));
    }

    //Checks if the starting position has a piece in it and that the finish position is empty
    private boolean canEnter(Position a, Position b)
    {
        return board[a.getX()][a.getY()] != null && board[b.getX()][b.getY()] == null;
    }

    //Checks if the movement is only horizontal or vertical and that we don't stay in the same place
    private boolean isPathLegal(Position a, Position b)
    {
        return a.getX() == b.getX() ^ a.getY() == b.getY();
    }

    //Checks if it's the turn of the pieces owner
    private boolean isPieceTurn(Position a)
    {
        return getPieceAtPosition(a).getOwner().isPlayerOne() != secondPlayerTurn;
    }

    //Checks if the path from a to b is unobstructed
    private boolean isPathClear(Position a, Position b)
    {
        //Temporary position that stores the position we're currently checking
        Position temp;

        //used to calculate the difference in position between a and b and whether we need to add or subtract to reach the desired position
        int xSign, ySign, xDiff, yDiff;
        temp = new Position(a);
        xDiff = temp.getX() - b.getX();
        yDiff = temp.getY() - b.getY();
        xSign = xDiff != 0 ? -(xDiff) / Math.abs(xDiff) : 0;
        ySign = yDiff != 0 ? -(yDiff) / Math.abs(yDiff) : 0;

        //Changing the position until we enter the destination
        while (temp.getX() != b.getX() || temp.getY() != b.getY()) {

            //Updating the X and Y
            temp.setX(temp.getX() + xSign);
            temp.setY(temp.getY() + ySign);

            //Checking that the position is empty
            if (getPieceAtPosition(temp) != null) {
                return false;
            }
        }
        return true;
    }

    //Getting the piece at the position through the 2D array
    @Override
    public Piece getPieceAtPosition(Position position) {
        return this.board[position.getX()][position.getY()];
    }

    //Checking if the position is one of the four corners of the board
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

    //checking if a kill has been performed successfully
    private void checkKill(Position attacker) {
        //Gathering the positions surrounding our attacker
        Position[] directions = getSurroundingAreas(attacker);

        //Iterating over the positions
        for (Position target : directions) {
            //checking if the kill can be performed
            if (canAttack(attacker,target)) {
                //checking if we killed a pawn
                if ((getPieceAtPosition(target) instanceof Pawn) && (isPawnSurrounded(target, attacker))) {

                    //Updating the attacker's kill count
                    ((Pawn)getPieceAtPosition(attacker)).modifyKillCounter(true);

                    //adding our attacker to the stack of pieces that managed to attack
                    attackHistory.add(this.board[attacker.getX()][attacker.getY()]);

                    //updating team sizes
                    if(secondPlayerTurn)
                    {
                        attackerCount--;
                    }
                    else {
                        defenderCount--;
                    }

                    //Removing the attacked pawn from the board
                    board[target.getX()][target.getY()] = null;

                    //checking if we attacked a king
                } else if ((getPieceAtPosition(target) instanceof King)) {
                    //checking if the king is surrounded
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

    //Calculating the position opposite to the attack relative to the taget
    private Position getOppositePosition(Position attacker, Position target)
    {
        int newX = target.getX() + (target.getX() - attacker.getX());
        int newY = target.getY() + (target.getY() - attacker.getY());
        return new Position(newX, newY);
    }

    //Checking if an attack is possible
    private boolean canAttack(Position attacker, Position target)
    {
        return isPositionOccupied(target) && isTargetHostile(attacker,target);
    }

    //checking if a position is inside the map and is occupied
    private boolean isPositionOccupied(Position pos)
    {
        return (inMapRange(pos) && getPieceAtPosition(pos) != null);
    }

    //checking if the pieces are hostile to each other (have different owners)
    private boolean isTargetHostile(Position attacker, Position target)
    {
        return getPieceAtPosition(target).getOwner() != getPieceAtPosition(attacker).getOwner();
    }

    //Gathering all the positions surrounding a position
    private Position[] getSurroundingAreas(Position pos)
    {
        return new Position[]{
                new Position(pos.getX() + 1, pos.getY()),
                new Position(pos.getX() - 1, pos.getY()),
                new Position(pos.getX(), pos.getY() + 1),
                new Position(pos.getX(), pos.getY() - 1)
        };
    }

    //Checking if a position is inside the map
    private boolean inMapRange(Position pos) {
        int mapSize = getBoardSize();
        return (pos.getX() < mapSize && pos.getX() >= 0) && (pos.getY() < mapSize && pos.getY() >= 0);
    }

    //checking if a pawn is surrounded with another pawn or wall
    private boolean isPawnSurrounded(Position target, Position attacker) {
        Position pos = getOppositePosition(attacker,target);
        return isEdge(pos) || (!inMapRange(pos)) || (isPositionOccupied(pos) && isTargetHostile(pos,target) && (!(getPieceAtPosition(pos) instanceof King)));
    }

    //checking if a king a surrounded from all sides
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

    //returning the first player
    @Override
    public Player getFirstPlayer() {
        return this.playerOneDefend;
    }

    //returning the second player
    @Override
    public Player getSecondPlayer() {
        return this.playerTwoAttack;
    }

    //checking if a game is over
    @Override
    public boolean isGameFinished() {
        return this.isGameOver;
    }

    //Checking if the second player's turn to play.
    @Override
    public boolean isSecondPlayerTurn() {
        return this.secondPlayerTurn;
    }

    //Resting the game
    @Override
    public void reset() {
        //The second player starts first
        this.secondPlayerTurn = true;

        //Recreating the board from the ground up
        createBoard();
    }

    //undoing the last move and all the history related to it
    @Override
    public void undoLastMove() {
        if(!piecesHistory.isEmpty())
        {
            //Removing the last step of a piece
            Position p = piecesHistory.peek().removeLastMove();

            //Removing the piece from the unique step counter of the tile it was on
            uniqueSteps[p.getX()][p.getY()].removePiece(piecesHistory.pop());
        }

        //checking if the history is empty
        if(!history.isEmpty())
        {
            //Setting the board to a previous step
            board = history.pop();

            //setting the player turn
            secondPlayerTurn = !secondPlayerTurn;
        }

        //checking if the history of attacks is empty
        if(!attackHistory.isEmpty())
        {
            //if it's a separation, we pop it
            if(attackHistory.peek()==null)
            {
                attackHistory.pop();
            }
            else
            {
                //reduce the kill counter on the piece
                //a loop is used for cases where a piece might get more than a single kill
                while (attackHistory.peek()!=null)
                {
                    ((Pawn)attackHistory.pop()).modifyKillCounter(false);
                }
            }
        }
    }

    //Printing all the statistics needed for the second part of the project
    private void printStatistics()
    {
        //Sorting all pieces based on the number of moves they made
        allPieces.sort(new StepsComparator());
        int size, killCount;

        //Printing all the pieces that made at least 2 steps
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

        //Sorting all the pieces based on the number of kills they have
        allPieces.sort(new KillCountComparator().reversed());

        //Printing all the pieces that have at least one kill to their name
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

        //Sorting all the pieces based on the distance they traveled
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

        //Sorting all the positions based on the number of unique pieces that stepped on them
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
            //checking if they are on the same team
            if(obj1.getOwner()!=obj2.getOwner())
            {
                //The winning team always goes first in the sort
                return obj1.getOwner()==victor?-1:1;
            }
            //comparing the number of steps each took
            else if(obj1.getMoveHistorySize() == obj2.getMoveHistorySize())
            {
                //sorting by their ID if the amount is identical
                return obj1.getId() - obj2.getId();
            }
            //sorting based on number of steps
            return obj1.getMoveHistorySize() - obj2.getMoveHistorySize();
        }
    }
    static class DistanceComparator implements Comparator<ConcretePiece>{
        public int compare(ConcretePiece obj1, ConcretePiece obj2) {
            //checking if they traveled the same distance
            if((obj1).getDistanceTraveled() == (obj2).getDistanceTraveled())
            {
                //checking if their id match
                if(obj1.getId()==obj2.getId())
                {
                    //checking if ob1 belongs to the wining team (If the id are the same, they are probably opposite teams)
                    return obj1.getOwner()==victor?1:-1;
                }
                else{
                    //sorting based on id
                    return obj2.getId() - obj1.getId();
                }
            }
            //sorting by distance traveled
            return ((obj1).getDistanceTraveled() - (obj2).getDistanceTraveled());
        }
    }
    static class KillCountComparator implements Comparator<ConcretePiece>{
        public int compare(ConcretePiece obj1, ConcretePiece obj2) {
            //avoiding casting the king as a pawn
            if(obj1 instanceof King)
            {
                return -1;
            }
            if(obj2 instanceof King)
            {
                return 1;
            }
            //checking if the amount of kills is the same
            if(((Pawn)obj1).getKillCounter() == ((Pawn)obj2).getKillCounter())
            {
                //checking the id are the same
                if(obj1.getId()==obj2.getId())
                {
                    //checking if ob1 belongs to the wining team (If the id are the same, they are probably opposite teams)
                    return obj1.getOwner()==victor?1:-1;
                }
                else{
                    //sorting by id
                    return obj2.getId() - obj1.getId();
                }
            }
            //sorting by number of kills
            return ((Pawn)obj1).getKillCounter() - ((Pawn)obj2).getKillCounter();
        }
    }
    static class UniquePiecesComparator implements Comparator<Position> {
        public int compare(Position obj1, Position obj2) {
            //checking if the number unique pieces is the same
            if (obj1.getUniquePieces().size() == obj2.getUniquePieces().size())
            {
                //checking if the x cords are the same
                if(obj1.getX()== obj2.getX())
                {
                    //sorting by Y
                    return obj1.getY()-obj2.getY();
                }
                //sorting by X
                return obj1.getX()-obj2.getX();
            }
            //sorting by number of Unique pieces
            return obj2.getUniquePieces().size()-obj1.getUniquePieces().size();
        }
    }
    //printing 75 stars
    private void printStars()
    {
        for (int i = 0;i<75;i++)
        {
            System.out.print("*");
        }
        System.out.println();
    }

    //getting the size of the board (11)
    @Override
    public int getBoardSize() {
        return 11;
    }
}
