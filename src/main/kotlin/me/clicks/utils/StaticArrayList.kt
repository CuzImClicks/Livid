package me.clicks.utils

/**
 * @author Clicks
 */
class StaticArrayList<T>(val maxSize: Int) : Iterable<T> {
    private val array: Array<Any?> = Array(maxSize) { null }
    var size = 0
        private set

    fun add(element: T) {
        if (size >= maxSize) throw IndexOutOfBoundsException("Array has reached its max size of $maxSize")
        array[size++] = element
    }

    @Suppress("UNCHECKED_CAST")
    operator fun get(index: Int): T {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException("Index: $index, Size: $size")
        return array[index] as T
    }

    fun removeAt(index: Int): T {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException("Index: $index, Size: $size")
        val element = array[index] as T
        for (i in index until size - 1) {
            array[i] = array[i + 1]
        }
        array[--size] = null
        return element
    }

    fun forEach(action: (T?) -> Unit) {
        for (i in 0 until size) {
            action(array[i] as T)
        }
    }

    fun contains(element: T): Boolean {
        for (i in 0 until size) {
            if (array[i] == element) return true
        }
        return false
    }

    fun indexOf(element: T): Int {
        for (i in 0 until size) {
            if (array[i] == element) return i
        }
        return -1
    }

    fun clear() {
        for (i in 0 until size) {
            array[i] = null
        }
        size = 0
    }

    override fun iterator(): Iterator<T> = object : Iterator<T> {
        private var currentIndex = 0

        override fun hasNext(): Boolean = currentIndex < size

        @Suppress("UNCHECKED_CAST")
        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            return array[currentIndex++] as T
        }
    }
}


fun <T> staticArrayListOf(vararg elements: T): StaticArrayList<T> {
    val list = StaticArrayList<T>(elements.size)
    elements.forEach { list.add(it) }
    return list
}

fun <T> staticArrayListOf(size: Int) = StaticArrayList<T>(size)



