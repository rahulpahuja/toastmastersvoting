package myoo.votingapp.Utils

import android.support.v7.util.DiffUtil

class DiffCalculateUtils<T : DiffCalculate<T>>(private val oldList: List<T>, private val newList: List<T>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].areBothSame(newList[newItemPosition])
    }


    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].areContentSame(newList[newItemPosition])
    }

}

class MyStringDiffCalculateUtils<String>(private val oldList: List<kotlin.String>, private val newList: List<kotlin.String>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].equals(newList[newItemPosition])
    }


    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return true
    }

}

interface DiffCalculate<T> {

    fun areBothSame(newItem: T): Boolean
    fun areContentSame(newItem: T): Boolean
}