package com.android.base.adapter

/**
 * @author Ztiany
 */
interface DataManager<T> {

    fun add(element: T)

    fun addAt(location: Int, element: T)

    fun addItems(elements: List<T>)

    /**
     * 添加元素前会使用 equals 方法进行比较。
     *
     * @param elements 元素
     */
    fun addItemsChecked(elements: List<T>)

    fun addItemsAt(location: Int, elements: List<T>)

    fun replace(oldElement: T, newElement: T)

    fun replaceAt(index: Int, element: T)

    /**
     * 清除之前集合中的数据，然后把 elements 添加到之前的集合中，不会使用 elements 作为数据源。
     *
     * @param elements 元素
     */
    fun replaceAll(elements: List<T>)

    /**
     * 此方法会使用 [newDataSource] 直接替换掉之前的数据源。
     */
    fun setDataSource(newDataSource: MutableList<T>)

    fun swipePosition(fromPosition: Int, toPosition: Int)

    fun remove(element: T): Boolean

    fun removeIf(filter: (T) -> Boolean)

    fun removeAt(index: Int)

    fun removeItems(elements: List<T>)

    fun removeItems(elements: List<T>, isSuccessive: Boolean)

    fun getItem(position: Int): T?

    operator fun contains(element: T): Boolean

    fun clear()

    /**
     * @return -1 if not contains this element.
     */
    fun indexItem(element: T): Int

    fun notifyElementChanged(element: T)

    fun isEmpty(): Boolean

    fun getList(): List<T>

    fun getDataSize(): Int

}

fun <T> DataManager<T>.requireItem(position: Int): T {
    return getItem(position) ?: throw NullPointerException("There is no item for position ${position}.")
}