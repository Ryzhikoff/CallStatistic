package evgeniy.ryzhikov.callstatistics.ui.type_calls

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.models.TypeCallsContainer
import evgeniy.ryzhikov.callstatistics.ui.MainActivity
import evgeniy.ryzhikov.callstatistics.utils.INCOMING_TYPE

class IncomingFragment: TypeCallsFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.setArguments(bundleOf(Pair(MainActivity.KEY_TYPE_CALL, TypeCallsContainer(INCOMING_TYPE))))
        super.onViewCreated(view, savedInstanceState)
        initFabMenu()
    }

    private fun initFabMenu()  {
        view?.findViewById<FloatingActionMenu>(R.id.fabMenu)?.apply {
            animation = AnimationUtils.loadAnimation(context, R.anim.fall_from_top)
            animate()
        }
        view?.findViewById<FloatingActionButton>(R.id.backup)?.setOnClickListener {
            findNavController().navigate(R.id.action_incomingFragment_to_backupFragment3)
        }

    }
}