package jp.co.mixi.monsterstr.game

import kotlin.random.Random

const val REEL_COLUMNS = 4
const val REEL_ROWS = 5

private const val MATCH_THRESHOLD = 5

data class SpinResult(
    val grid: List<List<Symbol>>,
    val matches: List<MatchInfo>,
    val wildCells: List<Pair<Int, Int>>,
    val wildBonus: Int,
    val totalScore: Int,
) {
    val isBigWin: Boolean get() = totalScore >= 400
    val winningCells: Set<Pair<Int, Int>> get() =
        matches.flatMap { it.cells }.toSet() + wildCells.toSet()
}

data class MatchInfo(
    val symbol: Symbol,
    val effectiveCount: Int,
    val score: Int,
    val cells: List<Pair<Int, Int>>,
)

object SlotEngine {

    private val totalWeight: Int = Symbol.values().sumOf { it.weight }

    fun spin(random: Random = Random.Default): SpinResult {
        val grid: List<List<Symbol>> = List(REEL_ROWS) {
            List(REEL_COLUMNS) { randomWeightedSymbol(random) }
        }
        return scoreGrid(grid)
    }

    fun scoreGrid(grid: List<List<Symbol>>): SpinResult {
        val cellsBySymbol: Map<Symbol, List<Pair<Int, Int>>> = buildMap {
            for (r in grid.indices) {
                for (c in grid[r].indices) {
                    val s = grid[r][c]
                    getOrPut(s) { mutableListOf() }
                    (get(s) as MutableList).add(r to c)
                }
            }
        }

        val wildCells: List<Pair<Int, Int>> =
            cellsBySymbol[Symbol.WILD_JOKER].orEmpty()
        val wildCount: Int = wildCells.size

        val matches = mutableListOf<MatchInfo>()
        for ((symbol, cells) in cellsBySymbol) {
            if (symbol.isWild) continue
            val count = cells.size
            if (count >= MATCH_THRESHOLD) {
                val multiplier = when (count) {
                    5 -> 2
                    6 -> 4
                    7 -> 7
                    8 -> 11
                    9 -> 16
                    else -> 22
                }
                val score = symbol.baseValue * multiplier
                matches += MatchInfo(
                    symbol = symbol,
                    effectiveCount = count,
                    score = score,
                    cells = cells,
                )
            }
        }

        val wildBonus = wildCount * 50
        val total = matches.sumOf { it.score } + wildBonus
        return SpinResult(
            grid = grid,
            matches = matches.sortedByDescending { it.score },
            wildCells = wildCells,
            wildBonus = wildBonus,
            totalScore = total,
        )
    }

    fun randomWeightedSymbol(random: Random = Random.Default): Symbol {
        var roll = random.nextInt(totalWeight)
        for (s in Symbol.values()) {
            if (roll < s.weight) return s
            roll -= s.weight
        }
        return Symbol.CHERRY
    }
}
