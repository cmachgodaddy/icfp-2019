package icfp2019

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import icfp2019.model.BoardNodeStates
import icfp2019.model.BoardCells
import icfp2019.model.GameState

class Cache<T, R>(filler: (T) -> R) {
    companion object {
        fun <R> forGameState(filler: (GameState) -> R): Cache<GameState, R> {
            return Cache(filler)
        }

        fun <R> forBoard(filler: (BoardCells) -> R): Cache<BoardCells, R> {
            return Cache(filler)
        }

        fun <R> forBoardState(filler: (BoardNodeStates) -> R): Cache<BoardNodeStates, R> {
            return Cache(filler)
        }
    }

    private val storage = CacheBuilder.newBuilder()
        .build(object : CacheLoader<T, R>() {
            override fun load(key: T): R {
                return filler(key)
            }
        })

    operator fun invoke(key: T): R {
        return storage.getUnchecked(key)
    }
}
