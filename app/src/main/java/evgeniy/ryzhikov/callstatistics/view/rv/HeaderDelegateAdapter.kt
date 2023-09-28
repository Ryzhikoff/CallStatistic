package evgeniy.ryzhikov.callstatistics.view.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import evgeniy.ryzhikov.callstatistics.databinding.ItemHeaderBinding

class HeaderDelegateAdapter : AbsListItemAdapterDelegate<Header, TopItemInterface, HeaderDelegateAdapter.ViewHolder>() {
    class ViewHolder(binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        val header = binding.header
    }

    override fun isForViewType(item: TopItemInterface, items: MutableList<TopItemInterface>, position: Int): Boolean {
        return item is Header
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHeaderBinding.inflate(inflater,parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(item: Header, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.header.text = item.header
    }
}