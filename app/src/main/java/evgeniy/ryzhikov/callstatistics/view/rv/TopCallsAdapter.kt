package evgeniy.ryzhikov.callstatistics.view.rv

import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class TopCallsAdapter : ListDelegationAdapter<List<TopItemInterface>>() {
    private var list = ArrayList<TopItemInterface>()
    init {
        delegatesManager.addDelegate(TopCallsDelegateAdapter())
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
    override fun setItems(items: List<TopItemInterface>?) {
        super.setItems(items)
        notifyDataSetChanged()
    }
}