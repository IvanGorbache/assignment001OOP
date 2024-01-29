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
    //Inputs: None
    //Outputs: None
    //Function: Creates the 2D array of ConcretePieces that represents the board along with the stacks that represent historical data
    private void createBoard() {
        //Resetting the Ids of attackers and defenders when starting a new game
        ConcretePiece.resetID();
        //Called here because finishing a game restarts the board
        isGameOver = false;

        //Initializing  all stacks related to historical data.
        history = new Stack<>();
        attackHistory = new Stack<>();
        piecesHistory = new Stack<>();

        //Initializing  a 2D array for keeping track of the number of unique pieces that stepped on each tile
        uniqueSteps = new Position[getBoardSize()][getBoardSize()];

        //Creating an arraylist that contains all pieces
        allPieces = new ArrayList<>();

        //Initializing  the board
        board = new ConcretePiece[getBoardSize()][getBoardSize()];

        //Creating all the pieces and spreading them on the board
        int i = 0;
        int j = 0;

        //A string representing the arrangement of pieces on the board
        //0 - empty space
        //1 - defender pawn
        //2 - attacker pawn
        //3 - king
        String field = """
                00022222000
                00000200000
                00000000000
                20000100002
                20001110002
                22011311022
                20001110002
                20000100002
                00000000000
                00000200000
                00022222000""";
        //Iterating over the String
        for(char c: field.toCharArray())
        {
            switch (c)
            {
                //1 - Defender Pawn
                case '1':
                    createPiece(i,j,playerOneDefend,false);
                    i++;
                    break;
                //2 - Attacker Pawn
                case '2':
                    createPiece(i,j,playerTwoAttack,false);
                    i++;
                    break;
                //3 - King
                case '3':
                    createPiece(i,j,playerOneDefend,true);
                    i++;
                    break;
                 //Moving down a line
                case '\n':
                    //Moving down a line
                    j++;
                    //Resting the position to the start
                    i = 0;
                    break;
                 //Empty space
                default:
                    i++;
                    break;
            }
        }
    }
    //Creates a new piece to add to the board given its starting position, owner and whether it's a king or not
    private void createPiece(int i,int j, ConcretePlayer player, boolean isKing)
    {
        board[i][j] = isKing? new King(player): new Pawn(player);

        allPieces.add(board[i][j]);
        board[i][j].addMove(new Position(i,j));

        uniqueSteps[i][j] = new Position(i,j);

        //Adding the piece in that location to the arraylist of unique piece in that position
        uniqueSteps[i][j].addUniquePieces(board[i][j]);
    }
    //Gathering all the positions that were stepped on to an arraylist for printing statistics
    //Inputs: None
    //Outputs: None
    //Function: Iterates over the 2D array of uniqueSteps and gathers all the positions that aren't null
    private void getAllPositions()
    {
        this.allPositions = new ArrayList<>();

        //Iterating over all the entries in the board
        for (int i=0;i<getBoardSize();i++)
        {
            for (int j = 0;j<getBoardSize();j++)
            {
                //Gathering all the positions that were stepped on
                if(uniqueSteps[i][j]!=null)
                {
                    //Adding it to the arraylist for sorting
                    allPositions.add(uniqueSteps[i][j]);
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
                //If the king escaped, the victor is the defender
                victor = playerOneDefend;
                isGameOver = true;
                //Adding a win to the defending team
                playerOneDefend.addWin();
                //Printing the statistics
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
        //Iterating over all the entries in the board
        for (int i = 0; i < getBoardSize(); i++) {
            //Copying the array to the CopyBoard
            System.arraycopy(this.board[i], 0, copyBoard[i], 0, getBoardSize());
        }
        //Adding CopyBoard to the history
        history.push(copyBoard);
    }

    //checking if the king has reach one of the four corners of the map
    private void checkEscape(Position pos)
    {
        //Checks if the king has reached any of the edges
        isGameOver = getPieceAtPosition(pos) instanceof King && isEdge(pos);

        //Checks if the game is over after the check
        if(isGameOver)
        {
            //If the king escaped, the victor is the defender
            victor = playerOneDefend;
            //Adding a win to the defending team
            playerOneDefend.addWin();
            //Printing the statistics
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

        //Setting the starting point of our check to be a
        temp = new Position(a);

        //Calculating the difference between position a and b
        xDiff = temp.getX() - b.getX();
        yDiff = temp.getY() - b.getY();

        //Calculating whether we should add, subtract or do nothing to the coordinates to reach the destination
        xSign = xDiff != 0 ? -(xDiff) / Math.abs(xDiff) : 0;
        ySign = yDiff != 0 ? -(yDiff) / Math.abs(yDiff) : 0;

        //Changing the position until we enter the destination
        while (temp.getX() != b.getX() || temp.getY() != b.getY()) {

            //Updating the X and Y
            temp.setX(temp.getX() + xSign);
            temp.setY(temp.getY() + ySign);

            //Checking that the position is empty
            if (getPieceAtPosition(temp) != null) {
                //returning false if we encounter a piece between point a and point b
                return false;
            }
        }
        //returning true if the path between point a and point b is clear
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
        //Array of the corners of the map [(0,0), (10,10), (0,10), (10,0)]
        Position[] edgeSquare = new Position[]{
                new Position(0, 0),
                new Position(10, 10),
                new Position(0, 10),
                new Position(10, 0)};
        //Iterating over the array of position to check if our given pos is one of them
        for (Position tempPos:edgeSquare)
        {
            //Comparing our give pos to one of the corners
            if ((tempPos.getX() == pos.getX())&&(tempPos.getY() == pos.getY()))
            {
                //Returning true if the given pos is a corner
                return true;
            }
        }
        //Returning false if the given pos isn't a corner
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
                    //If the king is surrounded
                    if(isGameOver)
                    {
                        //The victor is the attacker
                        victor = playerTwoAttack;

                        //Adding a win to the attacking team
                        playerTwoAttack.addWin();

                        //Printing the statistics
                        printStatistics();
                    }
                }
            }
        }
    }

    //Calculating the position opposite to the attack relative to the taget
    private Position getOppositePosition(Position attacker, Position target)
    {
        //Calculating the coordinates of the opposite position to the attacker relative to the target
        int newX = target.getX() + (target.getX() - attacker.getX());
        int newY = target.getY() + (target.getY() - attacker.getY());

        //returning the result
        return new Position(newX, newY);
    }

    //Checking if an attack is possible
    private boolean canAttack(Position attacker, Position target)
    {
        //Checking if the given target is occupied and hostile to us (of a different team)
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
        //Returning the positions north, south, west and east of our given pos
        return new Position[]{
                new Position(pos.getX() + 1, pos.getY()),
                new Position(pos.getX() - 1, pos.getY()),
                new Position(pos.getX(), pos.getY() + 1),
                new Position(pos.getX(), pos.getY() - 1)
        };
    }

    //Checking if a position is inside the map
    private boolean inMapRange(Position pos) {
        //Getting the size of the board
        int mapSize = getBoardSize();

        //Checking if the give pos falls within the bounds of our board
        return (pos.getX() < mapSize && pos.getX() >= 0) && (pos.getY() < mapSize && pos.getY() >= 0);
    }

    //checking if a pawn is surrounded with another pawn or wall
    private boolean isPawnSurrounded(Position target, Position attacker) {
        //Getting the position opposite to the attack relative to the taget
        Position pos = getOppositePosition(attacker,target);

        //Checking if the pos is an edge, or outside the map, or a hostile pawn
        return isEdge(pos) || (!inMapRange(pos)) || (isPositionOccupied(pos) && isTargetHostile(pos,target) && (!(getPieceAtPosition(pos) instanceof King)));
    }

    //checking if a king a surrounded from all sides
    private boolean isKingSurrounded(Position target) {
        //Getting all the positions around the king
        Position[] directions = getSurroundingAreas(target);

        //Iterating over the positions around the king
        for (Position pos : directions) {
            //If one of the positions is inside the map and either empty or non-hostile
            if ((inMapRange(pos)) && ((getPieceAtPosition(pos) == null) || (!isTargetHostile(pos,target)))){
                return false;
            }
        }
        //If at least three positions are occupied by hostile pawns, the attacking player wins
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
                //Removing the null padding
                attackHistory.pop();
            }
            else
            {
                //reduce the kill counter on the piece
                //a loop is used for cases where a piece might get more than a single kill
                while (attackHistory.peek()!=null)
                {
                    //Removing a kill from the kill counter.
                    //The casting is safe because we only enter pawns to the attack history
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
            //Getting the size of the move history
            size = piece.getMoveHistorySize();

            //Printing only pieces that moved more than once
            if (size>=2)
            {
                //Printing the move history
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

        //Separating with stars
        printStars();

        //Sorting all the pieces based on the number of kills they have
        allPieces.sort(new KillCountComparator().reversed());

        //Printing all the pieces that have at least one kill to their name
        for (ConcretePiece piece: allPieces)
        {
            //Printing only pawns because only they are capable of killing
            if(piece instanceof Pawn)
            {
                //Getting the kill counter of the pawn
                killCount = ((Pawn)piece).getKillCounter();

                //Printing pawns that have at least one kill to their name
                if(killCount>0)
                {
                    System.out.println(piece.getName()+": "+killCount+" kills");
                }
            }
        }

        //Separating with stars
        printStars();

        //Sorting all the pieces based on the distance they traveled
        allPieces.sort(new DistanceComparator().reversed());
        for (ConcretePiece piece: allPieces)
        {
            //getting the distance traveled by the piece
            size = piece.getDistanceTraveled();

            //Only printing pieces that traveled
            if(size>0)
            {
                System.out.println(piece.getName()+": "+size+" squares");
            }
        }

        //Separating with stars
        printStars();

        //Gathering all the positions that were stepped on over the duration of the game
        getAllPositions();

        //Sorting all the positions based on the number of unique pieces that stepped on them
        allPositions.sort(new UniquePiecesComparator());
        for (Position position: allPositions)
        {
            //Only printing positions that had more than one unique piece step on them
            if(position.getUniquePieces().size()>1)
            {
                System.out.println(position.toString()+position.getUniquePieces().size()+" pieces");
            }
        }

        //Separating with stars
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
