import kotlin.random.Random
import kotlin.system.exitProcess

val WIN = arrayOf(
    "░██╗░░░░░░░██╗██╗███╗░░██╗██╗",
    "░██║░░██╗░░██║██║████╗░██║██║",
    "░╚██╗████╗██╔╝██║██╔██╗██║██║",
    "░░████╔═████║░██║██║╚████║╚═╝",
    "░░╚██╔╝░╚██╔╝░██║██║░╚███║██╗",
    "░░░╚═╝░░░╚═╝░░╚═╝╚═╝░░╚══╝╚═╝"
)
val LOSS = arrayOf(
    "██╗░░░░░░█████╗░░██████╗░██████╗",
    "██║░░░░░██╔══██╗██╔════╝██╔════╝",
    "██║░░░░░██║░░██║╚█████╗░╚█████╗░",
    "██║░░░░░██║░░██║░╚═══██╗░╚═══██╗",
    "███████╗╚█████╔╝██████╔╝██████╔╝",
    "╚══════╝░╚════╝░╚═════╝░╚═════╝░"
)

val RANDOM_WORDS = arrayOf(
    arrayOf("Hate", "It's an emotion that can be expressed by a person against another person or thing."),
    arrayOf("Like", "It's an emotion that can be expressed by a person against another person or thing."),
    arrayOf("Bike", "It's a vehicle."),
    arrayOf("Car", "It's a vehicle."),
    arrayOf("House", "It's a building."),
    arrayOf("School", "It's a building."),
    arrayOf("Computer", "It's a machine."),
    arrayOf("Phone", "It's a machine."),
    arrayOf("Internet", "It's a making of a 'spider'."),
    arrayOf("Happiness", "It's an emotion that can be expressed by a person."),
    arrayOf("Sadness", "It's an emotion that can be expressed by a person."),
    arrayOf("Anger", "It's an emotion that can be expressed by a person."),
    arrayOf("Fear", "It's an emotion that can be expressed by a person."),
    arrayOf("Love", "It's an emotion that can be expressed by a person."),
    arrayOf("Joy", "It's an emotion that can be expressed by a person."),
    arrayOf("Keyboard", "It's an input device that a user can use."),
    arrayOf("Mouse", "It's an input device that a user can use."),
    arrayOf("Monitor", "It's an output device that a user can use."),
    arrayOf("Printer", "It's an output device that a user can use."),
    arrayOf("Speaker", "It's an output device that a user can use."),
    arrayOf("Tablet", "It's an out/input device that a user can use.")
)

const val FORCE_STOP_GAME_KEYWORD = "__FORCE_STOP_GAME"

fun animation(text: Array<String>, delay: Long = 500) {
    for (element in text) {
        println(element)
        Thread.sleep(delay)
    }
}

fun winAnimation() = animation(WIN)
fun lossAnimation() = animation(LOSS)

enum class GamePlayer {
    USER,
    COMPUTER
}

enum class GameMode(val readableName:String) {
    X_O("X-O"),
    I_GUESS("I guess"),
    HANG_MAN("Hand man");

    @JvmName("getReadableName1")
    fun getReadableName() = readableName
}

fun getPlayers():Array<GamePlayer> {
    val types:String = GamePlayer.values().contentToString()

    println("Player1 $types")
    var player1:GamePlayer? = playerOf(readLine())
    while(player1 == null) {
        println("Please enter a valid player name $types")
        player1 = playerOf(readLine())
    }
    println("Player1: $player1")

    println("Player2 $types")
    var player2:GamePlayer? = playerOf(readLine())
    while(player2 == null) {
        println("Please enter a valid player name $types")
        player2 = playerOf(readLine())
    }
    println("Player2: $player2")

    println("Successfully set players!")

    return arrayOf(player1, player2)
}
private fun playerOf(name:String?):GamePlayer? {
    if(name == null) return null

    return try {
        GamePlayer.valueOf(name)
    } catch (e:IllegalArgumentException) { null }
}

fun getGameMode():GameMode {
    val types:String = GameMode.values().contentToString()

    println("Please select a gamemode: $types")
    var gamemode:GameMode? = gameModeOf(readLine())
    while(gamemode == null) {
        println("Please enter a valid gamemode $types")
        gamemode = gameModeOf(readLine())
    }

    println("Successfully set gamemode! $gamemode")

    return gamemode
}
private fun gameModeOf(name:String?):GameMode? {
    if(name == null) return null

    return try {
        GameMode.valueOf(name)
    } catch (e:IllegalArgumentException) { null }
}

fun main() {
    // Get players
    val players:Array<GamePlayer> = getPlayers()
    val player1 = players[0]
    val player2 = players[1]

    // Get game mode
    val mode = getGameMode()

    // Start game
    println("Starting ${mode.getReadableName()} game!")
    startGame(mode, player1, player2)
}

interface Game {
    fun start()
    fun end()
}

private fun isInt(value:String):Boolean {
    return try {
        value.toInt()
        true
    } catch (e:NumberFormatException) { false }
}

fun tellGameStopSecret() = println("To forcefully stop this game, please type '$FORCE_STOP_GAME_KEYWORD'")
fun switchPlayer(currentPlayer:GamePlayer, player1:GamePlayer, player2:GamePlayer):GamePlayer {
    return when(currentPlayer) {
        player1 -> player2
        player2 -> player1
        else -> throw IllegalArgumentException("Invalid player")
    }
}

// START OF X O GAME CODE

class XO(
    private val player1:GamePlayer,
    private val player2:GamePlayer,
    private val isComputer:Boolean = false):Game {

    private val board = arrayOf(
        "0", "1", "2",
        "3", "4", "5",
        "6", "7", "8"
    )

    private fun slot(index:Int):String {
        return when {
            isInt(board[index]) -> " "
            else -> board[index]
        }
    }
    private fun printBoard() {
        val separator = "---+---+---"

        println(" ${slot(0)} | ${slot(1)} | ${slot(2)} ")
        println(separator)
        println(" ${slot(3)} | ${slot(4)} | ${slot(5)} ")
        println(separator)
        println(" ${slot(6)} | ${slot(7)} | ${slot(8)} ")
    }

    private fun setSymbol(symbol:String, index:Int, currentPlayer:GamePlayer) {
        if(board.all { it == "X" || it == "O" }) {
            println("The board is full!")
            end()
            return
        }
        if(board[index] == "X" || board[index] == "O") {
            println("This field is already taken!")
            return
        }
        board[index] = symbol

        if(tryHard(index)){
            if(currentPlayer == GamePlayer.COMPUTER) {
                lossAnimation()
            }
            else {
                println("The user with the symbol '$symbol' wins!")
                winAnimation()
            }

            exitProcess(0)
        }
    }

    private fun tryHard(index:Int):Boolean {
        val symbol = board[index]

        if(index == 0) {
            if(board[1] == symbol && board[2] == symbol) return true
            if(board[3] == symbol && board[6] == symbol) return true
            if(board[4] == symbol && board[8] == symbol) return true
        }

        if(index == 1) {
            if(board[0] == symbol && board[2] == symbol) return true
            if(board[4] == symbol && board[7] == symbol) return true
        }

        if(index == 2) {
            if(board[0] == symbol && board[1] == symbol) return true
            if(board[5] == symbol && board[8] == symbol) return true
            if(board[4] == symbol && board[6] == symbol) return true
        }

        if(index == 3) {
            if(board[0] == symbol && board[6] == symbol) return true
            if(board[4] == symbol && board[5] == symbol) return true
        }

        if(index == 4) {
            if(board[0] == symbol && board[8] == symbol) return true
            if(board[3] == symbol && board[5] == symbol) return true
            if(board[1] == symbol && board[7] == symbol) return true
        }

        if(index == 5) {
            if(board[2] == symbol && board[8] == symbol) return true
            if(board[3] == symbol && board[4] == symbol) return true
        }

        if(index == 6) {
            if(board[0] == symbol && board[3] == symbol) return true
            if(board[7] == symbol && board[8] == symbol) return true
            if(board[4] == symbol && board[2] == symbol) return true
        }

        if(index == 7) {
            if(board[1] == symbol && board[4] == symbol) return true
            if(board[6] == symbol && board[8] == symbol) return true
        }

        if(index == 8) {
            if(board[2] == symbol && board[5] == symbol) return true
            if(board[6] == symbol && board[7] == symbol) return true
            if(board[4] == symbol && board[0] == symbol) return true
        }

        // This is generated using the GitHub Copilot AI, so no, I didn't lose my life.

        return false
    }

    private fun getRandomOpenSlot():Int? {
        val openSlots = board.filter { it != "X" && it != "O" }
        if(openSlots.isEmpty()) return null
        return openSlots.random().toInt()
    }

    override fun start() {
        if(isComputer) println(
                    "This game contains a 'COMPUTER' as a player" +
                    "\nPlease note that this feature is not yet supported" +
                    "\nAnd the bot will be playing without AI, and it's selecting random empty slots as it's choice.")
        tellGameStopSecret()
        var currentPlayer:GamePlayer = player2
        var currentSymbol = "X"
        while(!board.all { it == "X" || it == "O" }) {
            currentSymbol = if(currentSymbol == "X") "O" else "X"
            currentPlayer = switchPlayer(currentPlayer, player1, player2)
            println("${currentPlayer.name}'s turn:")
            printBoard()
            var index:Int?
            if(currentPlayer == GamePlayer.COMPUTER) {
                index = getRandomOpenSlot()

                if(index == null) {
                    println("The board is full!")
                    return
                }
            }
            else {
                index = getUserInput(this).toIntOrNull()
                while(index == null || index < 0 || index > 8) {
                    println("Please input a valid index format! (0-8)")
                    index = getUserInput(this).toIntOrNull()
                }
            }


            setSymbol(currentSymbol, index, currentPlayer)
        }
    }

    override fun end() {
        exitProcess(0)
    }
}

// END OF X O GAME CODE

// START OF I GUESS GAME

class IGuess(
    private val player1:GamePlayer,
    private val player2:GamePlayer,
    private val isComputer:Boolean = false):Game {



    private fun randomWord() = RANDOM_WORDS.random()
    private fun randomRelatedWord(last:Char) = RANDOM_WORDS.filter { (it[0][it[0].length - 1] == last) }.random()
    var currentPlayer = player1

    override fun start() {
        if(isComputer) println(
                    "This game contains a 'COMPUTER' as a player" +
                    "\nPlease note that this feature is not yet supported" +
                    "\nAnd the bot will be playing without AI, and it's selecting random words as it's choice.")

        tellGameStopSecret()
        val word = randomWord()
        val wordName = word[0]
        val wordDescription = word[1]
        println("I'm guessing a word that starts with '${wordName[0]}' and ends with '${wordName[wordName.length - 1]}'")
        println("Word description: $wordDescription")

        fun getInput() = when(currentPlayer) {
            GamePlayer.USER -> getUserInput(this)
            GamePlayer.COMPUTER -> {
                val cWord = randomRelatedWord(wordName[wordName.length - 1])[0]
                println("Computer has guessed the word '${cWord}'")
                cWord
            }
        }

        var playerInput = ""
        while (playerInput != wordName) {
            playerInput = getInput()
            if(playerInput == wordName) continue
            else println("Wrong word, try again!")
            currentPlayer = switchPlayer(currentPlayer, player1, player2)
            println("${currentPlayer}'s turn:")

        }
        if(currentPlayer == GamePlayer.COMPUTER)  lossAnimation()
        else winAnimation()
        println("The word was '${wordName}'")
    }

    override fun end() {
        exitProcess(0)
    }
}

// END OF I GUESS GAME

class HangMan(
    private val player1:GamePlayer,
    private val player2:GamePlayer,
    private val isComputer:Boolean = false):Game {


    private val HANGMAN_PROCESSES = arrayOf(
                "\n0\n",
        // ----------
                "\n0\n" +
                "|\n",
        // ----------
                "\n0\n" +
                "|\n" +
                "|\n",
        // ----------
                "\n0\n" +
                "|\\\n" +
                "|\n",
        // ----------
                "\n 0\n" +
                "/|\\\n" +
                " |\n",
        // ----------
                "\n 0\n" +
                "/|\\\n" +
                " |\n" +
                "/ \n",
        // ----------
                "\n 0\n" +
                "/|\\\n" +
                " |\n" +
                "/ \\\n",
        // ----------
                "\n |\n" +
                " O\n" +
                "/|\\\n" +
                " |\n" +
                "/ \\\n",
        // ----------
                "\n \\\n" +
                " |\n" +
                " O\n" +
                "/|\\\n" +
                " |\n" +
                "/ \\\n",
        // ----------
                "\n\\\n" +
                " \\\n" +
                " |\n" +
                " O\n" +
                "/|\\\n" +
                " |\n" +
                "/ \\\n",
        // ----------
                "\n-- \n" +
                "  \\\n" +
                "   \\\n" +
                "   |\n" +
                "   O\n" +
                "  /|\\\n" +
                "   |\n" +
                "  / \\\n",
        // ----------
                "\n---- \n" +
                "    \\\n" +
                "     \\\n" +
                "     |\n" +
                "     O\n" +
                "    /|\\\n" +
                "     |\n" +
                "    / \\\n",
        // ----------
                "\n+---- \n" +
                "     \\\n" +
                "      \\\n" +
                "      |\n" +
                "      O\n" +
                "     /|\\\n" +
                "      |\n" +
                "     / \\\n",
        // ----------
                "\n+---- \n" +
                "|    \\\n" +
                "      \\\n" +
                "      |\n" +
                "      O\n" +
                "     /|\\\n" +
                "      |\n" +
                "     / \\\n",
        // ----------
                "\n+---- \n" +
                "|    \\\n" +
                "|     \\\n" +
                "      |\n" +
                "      O\n" +
                "     /|\\\n" +
                "      |\n" +
                "     / \\\n",
        // ----------
                "\n+---- \n" +
                "|    \\\n" +
                "|     \\\n" +
                "|     |\n" +
                "      O\n" +
                "     /|\\\n" +
                "      |\n" +
                "     / \\\n",
        // ----------
                "\n+---- \n" +
                "|    \\\n" +
                "|     \\\n" +
                "|     |\n" +
                "|     O\n" +
                "     /|\\\n" +
                "      |\n" +
                "     / \\\n",
        // ----------
                "\n+---- \n" +
                "|    \\\n" +
                "|     \\\n" +
                "|     |\n" +
                "|     O\n" +
                "|    /|\\\n" +
                "      |\n" +
                "     / \\\n",
        // ----------
                "\n+---- \n" +
                "|    \\\n" +
                "|     \\\n" +
                "|     |\n" +
                "|     O\n" +
                "|    /|\\\n" +
                "|     |\n" +
                "     / \\\n",
        // ----------
                "\n+---- \n" +
                "|    \\\n" +
                "|     \\\n" +
                "|     |\n" +
                "|     O\n" +
                "|    /|\\\n" +
                "|     |\n" +
                "+    / \\\n",
        // ----------
                "\n +---- \n" +
                " |    \\\n" +
                " |     \\\n" +
                " |     |\n" +
                " |     O\n" +
                " |    /|\\\n" +
                " |     |\n" +
                "_+    / \\\n",
        // ----------
                "\n +---- \n" +
                " |    \\\n" +
                " |     \\\n" +
                " |     |\n" +
                " |     O\n" +
                " |    /|\\\n" +
                " |     |\n" +
                "_+_   / \\\n",
    )

    override fun start() {
        if(isComputer) {
            println("For your own safety, the 'COMPUTER' feature will not work in this gamemode!")
            println("If we were to enable this to work, the COMPUTER will kill the man for no reason.")
            println("^_^ Sorry!")
        }

        tellGameStopSecret()
        println("You have ${HANGMAN_PROCESSES.size} chances to guess the word")
        val word = RANDOM_WORDS.random()[0]
        var wordForPlayer = replaceRandomCharsInString(word, '*', word.length / 2)
        var currentHangManStatus = HANGMAN_PROCESSES[0]
        while(wordForPlayer != word) {
            println("The word is '${wordForPlayer}'")
            println("Guess the first letter that has '*'!")
            val letterToGuess = wordForPlayer.indexOf('*')
            var letter = getUserInput(this)[0]
            while(wordForPlayer[letterToGuess] != word[letterToGuess]) {
                val wrongLetter = "Wrong letter!, try again:"
                println(wrongLetter)
                letter = try {getUserInput(this)[0]} catch(e:StringIndexOutOfBoundsException) {' '}
                if(HANGMAN_PROCESSES.indexOf(currentHangManStatus) == HANGMAN_PROCESSES.size - 1) {
                    lossAnimation()
                    println("The word was '${word}'")
                    end()
                }

                val chars = wordForPlayer.toCharArray()
                chars[letterToGuess] = letter
                wordForPlayer = String(chars)
                if(wordForPlayer[letterToGuess] == word[letterToGuess]) break
                else {
                    currentHangManStatus = HANGMAN_PROCESSES[HANGMAN_PROCESSES.indexOf(currentHangManStatus) + 1]
                    println(currentHangManStatus.split('\n').joinToString("\n" + " ".repeat(wrongLetter.length / 2)))
                }
            }

            println("You guessed a letter of the word, which is '${letter}'")
            if(wordForPlayer == word) {
                winAnimation()
                end()
            }
        }

        winAnimation()
        println("Congratulations! You guessed the word '${word}'")
    }

    override fun end() {
        exitProcess(0)
    }
}

fun replaceRandomCharsInString(string:String, replacement:Char, charsAmount:Int):String {
    var output:String = string
    repeat(charsAmount) { output = output.replace(output.random(), replacement) }
    return output
}

fun getUserInput(game:Game):String {
    return when(val input:String = readLine()?: "") {
        FORCE_STOP_GAME_KEYWORD -> {
            println("Stopping the game!")
            game.end()
            return FORCE_STOP_GAME_KEYWORD
        }
        else -> input
    }
}

fun startGame(gameMode:GameMode, player1:GamePlayer, player2:GamePlayer) {
    val isComputer:Boolean = when(GamePlayer.COMPUTER) {
        player1 -> true
        player2 -> true
        else -> false
    }

    val game:Game = when(gameMode) {
        GameMode.X_O -> XO(player1, player2, isComputer)
        GameMode.I_GUESS -> IGuess(player1, player2, isComputer)
        GameMode.HANG_MAN -> HangMan(player1, player2, isComputer)
    }

    game.start()
}
