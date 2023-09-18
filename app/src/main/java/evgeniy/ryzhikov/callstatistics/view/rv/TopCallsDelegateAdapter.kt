package evgeniy.ryzhikov.callstatistics.view.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import evgeniy.ryzhikov.callstatistics.databinding.ItemTopsBinding

class TopCallsDelegateAdapter : AbsListItemAdapterDelegate<TopCalls, TopItemInterface, TopCallsDelegateAdapter.ViewHolder>() {
    class ViewHolder(binding: ItemTopsBinding) : RecyclerView.ViewHolder(binding.root) {
        val contactName = binding.contactName
        val phoneNumber = binding.phoneNumber
        val counterCalls = binding.countCalls
    }

    override fun isForViewType(item: TopItemInterface, items: MutableList<TopItemInterface>, position: Int): Boolean {
        return item is TopCalls
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTopsBinding.inflate(inflater,parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(item: TopCalls, holder: ViewHolder, payloads: MutableList<Any>) {
        println("item.contactName")
        holder.contactName.text = item.contactName
        holder.phoneNumber.text = item.phoneNumber
        holder.counterCalls.text = item.countCalls.toString()
    }

}