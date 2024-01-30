package evgeniy.ryzhikov.callstatistics.ui.type_calls

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import evgeniy.ryzhikov.callstatistics.models.TypeCallsContainer
import evgeniy.ryzhikov.callstatistics.ui.MainActivity
import evgeniy.ryzhikov.callstatistics.utils.INCOMING_TYPE

class IncomingFragment: TypeCallsFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.setArguments(bundleOf(Pair(MainActivity.KEY_TYPE_CALL, TypeCallsContainer(INCOMING_TYPE))))
        super.onViewCreated(view, savedInstanceState)
    }
}