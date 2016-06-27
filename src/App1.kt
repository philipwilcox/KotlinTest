import java.security.SecureRandom

fun main(args: Array<String>) {
    println("Hello, world!")
    val oneDie = Die()
    println("First roll: ${oneDie.roll()}; second roll: ${oneDie.roll()}")
    val attackDice = AttackDice(3)
    for (i in 1..10) {
        val results = attackDice.rollDice()
        println("Result of rolling attack dice: ${results}")
    }
}

class Die {
    val random = SecureRandom()

    fun roll() = random.nextInt(6) + 1
}

open class Dice(diceCount: Int) {
    val dice = (1..diceCount).map { Die() }

    /**
     * Roll this set of dice, returning the value as a list of sorted (largest to smallest) integers.
     */
    fun rollDice(): List<Int> {
        return dice.map { d -> d.roll() }.sortedDescending()
    }
}

class AttackDice(diceCount: Int) : Dice(diceCount) {
    init {
        if (diceCount < 2)
            throw Exception("Must attack with at least 2 dice")
        if (diceCount > 3)
            throw Exception("Cannot attack with more than 3 dice")
    }
}

class DefendDice(diceCount: Int) : Dice(diceCount) {
    init {
        if (diceCount < 1)
            throw Exception("Must defend with at least 1 die")
        if (diceCount > 2)
            throw Exception("Cannot defend with more than 2 dice")
    }
}