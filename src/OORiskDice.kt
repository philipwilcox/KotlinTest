import java.security.SecureRandom

/**
 * This is the main entry point for a somewhat object-oriented approach to evaluating Risk battles.
 *
 * It models each die, and even set of die per roll, as a class, which is fairly high overhead, but
 * handy for getting familiar with some basic OO modeling in Kotlin.
 *
 * TODO: a fun future thing would be to move to have a "dice pool" like how the game itself only comes
 * with so many physical dice that get reused.
 *
 * TODO: add some tests, see how that works in Kotlin
 */
fun main(args: Array<String>) {
    println("Hello, world!")
    val oneDie = Die()
    println("First roll: ${oneDie.roll()}; second roll: ${oneDie.roll()}")
    val attackDice = AttackDice(3)
    val defendDice = DefendDice(1)
    val basicBattle = Battle(attackDice, defendDice)
    for (i in 1..10) {
        val results = basicBattle.doBattle()
        println("Result of attack $i: $results")
    }

    println("--Test Invasions--")
    val random = SecureRandom()
    for (i in 1..20) {
        val attackerArmies = random.nextInt(8) + 2
        val defenderArmies = random.nextInt(8) + 1
        val result = invadeCountry(attackerArmies, defenderArmies)
        println("Result of attacking with $attackerArmies against $defenderArmies: $result")
    }
}

/**
 * A POJO for storing the result of an invasion attempt.
 */
data class InvasionResult(val attackerWins: Boolean, val attackerArmies: Int, val defenderArmies: Int)

/**
 * Top-level action supported by this program: compute the winner of an invasion attempt that started with the given
 * number of armies for attacker and defender and continued until either the defender was defeated or the attacker
 * could no longer attack.
 */
fun invadeCountry(attackerArmies: Int, defenderArmies: Int): InvasionResult {
    if (attackerArmies < 2)
        throw Exception("Must have at least 2 armies to attack from a country")
    if (defenderArmies < 1)
        throw Exception("A country cannot be empty!")
    var invasionResolved = false
    var currentAttackerArmies = attackerArmies
    var currentDefenderArmies = defenderArmies
    // Do battle until either the defender is vanquished or the attacker is out of spare armies
    while (!invasionResolved) {
        val battle = Battle(AttackDice(diceCountFromArmyCount(currentAttackerArmies, true)),
                DefendDice(diceCountFromArmyCount(currentDefenderArmies, false)))
        //println("  Attacker has $currentAttackerArmies armies, defender has $currentDefenderArmies, Will do battle: $battle")
        val result = battle.doBattle()
        currentAttackerArmies -= result.attackerLosses
        currentDefenderArmies -= result.defenderLosses
        if (currentDefenderArmies < 1)
            invasionResolved = true
        else if (currentAttackerArmies < 2)
            invasionResolved = true
    }
    if (currentDefenderArmies < 1)
        return InvasionResult(true, currentAttackerArmies, currentDefenderArmies)
    else
        return InvasionResult(false, currentAttackerArmies, currentDefenderArmies)
}

/** Helper function for determining how many dice to use based on the role of the player and the number of armies they
 * have.
 *
 * Don't like this organization, reconsider later.
 */
private fun diceCountFromArmyCount(armyCount: Int, attacking: Boolean): Int {
    if (attacking) {
        if (armyCount < 2)
            throw Exception("Must have at least 2 armies to attack from a country")
        else if (armyCount <= 3)
            return armyCount - 1
        else
            return 3 // Max number of attacker armies
    } else {
        if (armyCount < 1)
            throw Exception("A country cannot be empty!")
        else if (armyCount == 1)
            return armyCount
        else
            return 2 // Max number of defender armies
    }
}

/**
 * This is a POJO for storing a tuple of how many armies the attacker and defenders lose in a battle
 * (i.e. a roll of the dice).
 */
data class BattleResult(val attackerLosses: Int, val defenderLosses: Int)

/**
 * This class represents a single battle between a set of attacking dice and a set of defending dice.
 *
 * It has a doBattle function that returns a BattleResult tuple object.
 *
 * This structure could let me re-use the dice, but I don't like this sort of pattern, I'd rather have something
 * that drew from the dice pool each time rather than just re-use the same set of dice only when the same numbers were
 * involved as in the last battle.
 */
class Battle(attackDice: AttackDice, defendDice: DefendDice) {
    val attackDice = attackDice
    val defendDice = defendDice

    fun doBattle(): BattleResult {
        val attackRolls = attackDice.rollDice()
        val defendRolls = defendDice.rollDice()
        var attackLosses = 0
        var defendLosses = 0
        // Attacker can roll more than defender, but neither attacker nor defender can lose more than rolled
        for (i in 0 until attackDice.size) {
            if (i >= defendDice.size)
                break;
            if (attackRolls[i] > defendRolls[i])
                defendLosses++
            else
                attackLosses++
        }
        return BattleResult(attackLosses, defendLosses)
    }

    /** For debugging */
    override fun toString() = "Battle(${attackDice.size} attacking ${defendDice.size} dice)"
}

class Die {
    val random = SecureRandom()

    fun roll() = random.nextInt(6) + 1
}

open class Dice(diceCount: Int) {
    val dice = (1..diceCount).map { Die() }
    val size = diceCount

    /**
     * Roll this set of dice, returning the value as a list of sorted (largest to smallest) integers.
     */
    fun rollDice(): List<Int> {
        return dice.map { d -> d.roll() }.sortedDescending()
    }
}

class AttackDice(diceCount: Int) : Dice(diceCount) {
    init {
        if (diceCount < 1)
            throw Exception("Must attack with at least 1 die")
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