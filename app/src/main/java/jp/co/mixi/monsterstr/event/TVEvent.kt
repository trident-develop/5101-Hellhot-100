package jp.co.mixi.monsterstr.event

sealed interface TVEvent {
    data object OpenGame : TVEvent
}