package icfp2019

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import icfp2019.model.GameState

class Cache<T, R>(filler: (T) -> R) {
    companion object {
        fun <R> forGameState(filler: (GameState) -> R): Cache<GameState, R> {
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
