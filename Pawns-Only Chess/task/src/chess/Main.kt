package chess

import kotlin.system.exitProcess

fun main() {
    var winner = ""
    val board = MutableList(8) { MutableList(8) { " " } }
    repeat(8) {
        board[1][it] = "W"
        board[6][it] = "B"
    }


    fun printBoard() {
        for (row in 8 downTo 1) {
            println("  +---+---+---+---+---+---+---+---+")
            print("${row} |")
            for (column in 1..8 ) {
                print(" ${board[row - 1][column - 1]} |")
            }
            println()
        }
        println("  +---+---+---+---+---+---+---+---+")
        println("    a   b   c   d   e   f   g   h")
    }


    var turnCounter: Int = 1

    fun whiteToPlay(): Boolean = (turnCounter % 2) == 1

    fun convertMove(move: String): MutableList<Int> = mutableListOf<Int>(
        move[1].code - 49,
        move[0].code - 97,
        move[3].code - 49,
        move[2].code - 97,
        )

    fun colorOfPiece(moveConverted: List<Int>): Boolean {
       if (whiteToPlay()) {
           return board[moveConverted[0]][moveConverted[1]] == "W"
       } else {
           return board[moveConverted[0]][moveConverted[1]] == "B"
       }
    }


    fun makeMove(moveConverted: List<Int>) {
        board[moveConverted[2]][moveConverted[3]] = board[moveConverted[0]][moveConverted[1]]
        board[moveConverted[0]][moveConverted[1]] = " "
        turnCounter++
    }


    fun captureOrMove(moveConverted: List<Int>):Int {
        return(when {
            moveConverted[1] == moveConverted[3] -> 1
            moveConverted[1] == moveConverted[3] - 1 || moveConverted[1] == moveConverted[3] + 1 -> 2
            else -> 3
        })
    }


    fun notWithinBoard(moveConverted: List<Int>): Boolean {
        return !(moveConverted.maxOrNull() in 0..7 && moveConverted.minOrNull() in 0..7)
    }


    fun invalidInputPrint() {
        println("Invalid Input")
    }


    fun enPassant(moveConverted: List<Int>, typeMove: Int, lastMove: List<Int>, lengthLastMove: Int): Boolean {
        return ((whiteToPlay() &&
                moveConverted[0] == 4 &&lastMove[3] == moveConverted[3] &&
                lastMove[2] == moveConverted[2] - 1 &&
                lengthLastMove == 2)
                ||
                (!whiteToPlay() &&
                (moveConverted[0] == 3) && lastMove[3] == moveConverted[3] &&
                lastMove[2] == moveConverted[2] + 1 &&
                lengthLastMove == 2)) &&
                (typeMove == 2)
    }


    fun pieceInSquare(moveConverted: List<Int>): Boolean {
        return if (whiteToPlay() && board[moveConverted[0]][moveConverted[1]] == "W"){
            true
        } else !whiteToPlay() && board[moveConverted[0]][moveConverted[1]] == "B"
    }


    fun validMove(moveConverted: List<Int>, typeMove: Int, moveFirst: Boolean): Boolean {
        val spaceVal = (((moveConverted[0] == (moveConverted[2] - 1) ||
                (moveFirst && moveConverted[0] == moveConverted[2] - 2))&&
            whiteToPlay() &&
            board[moveConverted[2]][moveConverted[3]] == " ")
            ||
                (!whiteToPlay() &&
            (moveConverted[0] == (moveConverted[2] + 1) ||
            (moveFirst && moveConverted[0] == moveConverted[2] + 2)) &&
            board[moveConverted[2]][moveConverted[3]] == " "))

        return spaceVal && typeMove == 1
    }


    fun validCapture(moveConverted: List<Int>, typeMove: Int, enPass: Boolean): Boolean {
        return (whiteToPlay() &&
                typeMove == 2 &&
                ((moveConverted[0] == moveConverted[2] - 1 &&
                board[moveConverted[2]][moveConverted[3]] == "B")
                || (enPass && whiteToPlay())))
                ||
                (!whiteToPlay() &&
                typeMove == 2 &&
                (moveConverted[0] == moveConverted[2] + 1 &&
                board[moveConverted[2]][moveConverted[3]] == "W")
                || (enPass && !whiteToPlay()))


    }


    fun winByLastRow(moveConverted: List<Int>): Boolean {
        return moveConverted[2] == 7 || moveConverted[2] == 0
    }


    fun winnerByTake(): Boolean {
        var counterWhite = 0
        var counterBlack = 0
        repeat(8) {
            if (board[it].contains("W")) counterWhite++
            if (board[it].contains("B")) counterBlack++
        }
        if (counterWhite == 0) {
            winner = "Black"
        } else if (counterBlack == 0) {
            winner = "White"
        }

        return counterWhite * counterBlack == 0
    }


    fun pawnList(): List<List<Int>> {
        var pawnPosition = mutableListOf<List<Int>>()
        if (whiteToPlay()) {
            for (i in 1 until  board.size) {
                for (j in 1 until board[i].size) {
                    if (board[i][j] == "W") {
                        pawnPosition.add(mutableListOf(i, j))
                    }
                }
            }
        } else {
            for (i in 1 until  board.size) {
                for (j in 1 until board[i].size) {
                    if (board[i][j] == "B") {
                        pawnPosition.add(mutableListOf(i, j))
                    }
                }
            }
        }
        return pawnPosition
    }

    fun possibleMoveCheck (pawnPossition: List<List<Int>>, enPassant: Boolean = false): Boolean {
        var movePossible = false
        if (whiteToPlay()) {
            for (pawn in pawnPossition.indices) {
                if (board[pawnPossition[pawn][0] + 1][pawnPossition[pawn][1]] == " " ||
                    (pawnPossition[pawn][1] != 0 && board[pawnPossition[pawn][0] + 1][pawnPossition[pawn][1] - 1] == "B") ||
                    (pawnPossition[pawn][1] != 7 && board[pawnPossition[pawn][0] + 1][pawnPossition[pawn][1] + 1] == "B") ||
                    (pawnPossition[pawn][1] != 0 && enPassant && board[pawnPossition[pawn][0] + 1][pawnPossition[pawn][1] - 2] == "B")||
                    (pawnPossition[pawn][1] != 7 && enPassant && board[pawnPossition[pawn][0] + 1][pawnPossition[pawn][1] + 2] == "B")) {
                    movePossible = true
                }
            }
        }
        if (!whiteToPlay()) {
            for (pawn in pawnPossition.indices) {
                if (board[pawnPossition[pawn][0] - 1][pawnPossition[pawn][1]] == " " ||
                    (pawnPossition[pawn][1] != 0 && board[pawnPossition[pawn][0] - 1][pawnPossition[pawn][1] - 1] == "W") ||
                    (pawnPossition[pawn][1] != 7 && board[pawnPossition[pawn][0] - 1][pawnPossition[pawn][1] + 1] == "W") ||
                    (pawnPossition[pawn][1] != 0 && enPassant && board[pawnPossition[pawn][0] - 1][pawnPossition[pawn][1] - 2] == "W")||
                    (pawnPossition[pawn][1] != 7 && enPassant && board[pawnPossition[pawn][0] - 1][pawnPossition[pawn][1] + 2] == "W")) {
                    movePossible = true
                }
            }
        }
        return movePossible
    }


    fun checkStalemate(lastMove: List<Int>): Boolean {
        var pawnPosition = pawnList()
        var enPassantPossible = lastMove[0] == lastMove[2] - 2 || lastMove[0] == lastMove[2] + 2
        return !possibleMoveCheck(pawnPosition, enPassantPossible)
    }

    println("Pawns-Only Chess")
    println("First Player's name:")
    val white = readln()
    println("Second Player's name:")
    val black = readln()
    var move: String = ""
    var lastMove = mutableListOf<Int>(0,0,0,0)
    var lastMoveLength = 0
    printBoard()
    while (true) {
        if (whiteToPlay()) {
            println("${white}'s turn:")
        } else {
            println("${black}'s turn:")
        }
        move = readln()
        if (move == "exit") {
            break
        }
        val moveNum = convertMove(move)
        //validate move is within board
        if (notWithinBoard(moveNum)) {
            invalidInputPrint()
            continue
        }
        //capture or move
        val moveType = captureOrMove(moveNum)
        if (moveType == 3) {
            invalidInputPrint()
            continue
        }
        //first move of piece
        val firstMove = (whiteToPlay() && moveNum[0] == 1) || (!whiteToPlay() && moveNum[0] == 6) && moveType == 1
        //en passant
        val enPassantVal: Boolean = enPassant(moveNum, moveType, lastMove, lastMoveLength)
        //check if piece in square
        if (!pieceInSquare(moveNum) && whiteToPlay()) {
            println("No white pawn at ${move.substring(0,2)}")
            continue
        } else if ( !pieceInSquare(moveNum) && !whiteToPlay()) {
            println("No black pawn at ${move.substring(0,2)}")
            continue
        }
        //validate move
        if (!validMove(moveNum,moveType,firstMove) && !validCapture(moveNum,moveType, enPassantVal)) {
            invalidInputPrint()
            continue
        }
        if (enPassantVal) {
            board[lastMove[2]][lastMove[3]] = " "
        }
        //make move
        makeMove(moveNum)
        //make move last move
        lastMove = moveNum
        lastMoveLength = Math.abs(moveNum[2] - moveNum[0])
        //print board
        printBoard()
        //check winner by reaching last row
        if (winByLastRow(moveNum)) {
            if (whiteToPlay()) {
                winner = "Black"
            } else {
                winner = "White"
            }
            break
        }
        //check winner by take
        if (winnerByTake()) {
            break
        }
        //check stalemate
        if (checkStalemate(moveNum)) {
            winner = "Stalemate!"
            break
        }
    }
    if (winner == "White" || winner == "Black") {
        println("$winner Wins!")
    } else if (winner == "Stalemate!") {
        println(winner)
    }
    println("Bye!")
}