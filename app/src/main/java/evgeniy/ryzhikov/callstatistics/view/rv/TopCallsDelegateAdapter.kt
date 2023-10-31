package evgeniy.ryzhikov.callstatistics.view.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.databinding.ItemTopsBinding

class TopCallsDelegateAdapter : AbsListItemAdapterDelegate<TopItem, TopItemInterface, TopCallsDelegateAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemTopsBinding) : RecyclerView.ViewHolder(binding.root) {
        val contactName = binding.contactName
        val phoneNumber = binding.phoneNumber
        val counterCalls = binding.countCalls
        val cardView = binding.root
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
        holder.contactName.text = item.contactName
        holder.phoneNumber.text = item.phoneNumber
        holder.counterCalls.text = item.value
        holder.binding.itemContainer.setOnClickListener {
            item.clickListener.onClick(item.phoneData)
        }
        holder.cardView.animation = AnimationUtils.loadAnimation(holder.cardView.context, R.anim.rv_anim)
    }

}