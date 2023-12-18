package evgeniy.ryzhikov.callstatistics.ui.type_calls.rv

import android.annotation.SuppressLint
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class TopCallsAdapter : ListDelegationAdapter<List<TopItemInterface>>() {
    private var list = ArrayList<TopItemInterface>()
    init {
        delegatesManager.addDelegate(TopCallsDelegateAdapter())
        delegatesManager.addDelegate(HeaderDelegateAdapter())
    }

    fun addItem(item : TopItemInterface) {
        list.add(item)
    }

    fun update() {
        setItems(list)
    }

    fun clear() {
        list.clear()
        update()
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun setItems(items: List<TopItemInterface>?) {
        super.setItems(items)
        notifyDataSetChanged()
    }
}