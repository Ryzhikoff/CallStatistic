package evgeniy.ryzhikov.callstatistics.ui.type_calls.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import evgeniy.ryzhikov.callstatistics.databinding.ItemTopsBinding

class TopCallsDelegateAdapter : AbsListItemAdapterDelegate<TopItem, TopItemInterface, TopCallsDelegateAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemTopsBinding) : RecyclerView.ViewHolder(binding.root) {
        val contactName = binding.contactName
        val phoneNumber = binding.phoneNumber
        val counterCalls = binding.countCalls
    }

    override fun isForViewType(item: TopItemInterface, items: MutableList<TopItemInterface>, position: Int): Boolean {
        return item is TopItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTopsBinding.inflate(inflater,parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(item: TopItem, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.contactName.setText(item.contactName)
        holder.phoneNumber.text = item.phoneNumber
        holder.counterCalls.text = item.value
        holder.binding.itemContainer.setOnClickListener {
            item.clickListener.onClick(item.phoneData)
        }
    }

}