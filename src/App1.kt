import java.security.SecureRandom

fun main(args: Array<String>) {
    println("Hello, world!")
    val one_die = Die()
    println("First roll: ${one_die.roll()}; second roll: ${one_die.roll()}")
}

class Die {
    val random = SecureRandom()

    fun roll() = random.nextInt(6) + 1
}

open class Dice(dice_count: Int) {
    val dice_count = dice_count

    fun roll_dice() = {
        // TODO Jun 26, pmw: roll these dice, return sorted descending-order list of the rolls
    }
}

class AttackDice(dice_count: Int) : Dice(dice_count) {
    init {
        if (dice_count < 2)
            throw Exception("Must attack with at least 2 dice")
        if (dice_count > 3)
            throw Exception("Cannot attack with more than 3 dice")
    }
}

class DefendDice(dice_count: Int) : Dice(dice_count) {
    init {
        if (dice_count < 1)
            throw Exception("Must defend with at least 1 die")
        if (dice_count > 2)
            throw Exception("Cannot defend with more than 2 dice")
    }
}

fun roll_attack(attack_dice: Int, defend_dice: Int) {

}