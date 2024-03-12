package evgeniy.ryzhikov.callstatistics.ui.backup


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.services.drive.DriveScopes
import evgeniy.ryzhikov.callstatistics.App
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.data.PhoneDatabase
import evgeniy.ryzhikov.callstatistics.databinding.FragmentBackupBinding
import evgeniy.ryzhikov.callstatistics.domain.GoogleDriveHelper
import evgeniy.ryzhikov.callstatistics.ui.customview.PopUpDialog
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import javax.inject.Inject


class BackupFragment : Fragment(R.layout.fragment_backup) {
    private var _binding: FragmentBackupBinding? = null
    private val binding: FragmentBackupBinding get() = _binding!!

    private lateinit var googleSignInClient: GoogleSignInClient
    private var account: GoogleSignInAccount? = null


    private val signInResultContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            onRegisterCallback(result)
        }

    private lateinit var onPermissionListener: OnPermissionListener
    private lateinit var onSignInListener: OnSignInListener
    private val permissionResultContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            onPermissionCallback(result)
        }

    @Inject
    lateinit var database: PhoneDatabase

    @Inject
    lateinit var googleDriveHelper: GoogleDriveHelper


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBackupBinding.bind(view)
        App.instance.appComponent.inject(this)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        setClickListeners()
        driveListeners()
    }

    private fun setClickListeners() = with(binding) {
        save.setOnClickListener {
            signAndPermissionCheckAndRunCommand { googleDriveHelper.save() }
        }

        load.setOnClickListener {
            signAndPermissionCheckAndRunCommand { googleDriveHelper.load() }
        }

        back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun signAndPermissionCheckAndRunCommand(driveCommand: () -> Unit) {
        enableProgressBar(true)
        account = GoogleSignIn.getLastSignedInAccount(requireContext())

        onSignInSuccess {
            onPermissionSuccess {
                account?.let {
                    googleDriveHelper.init(it)
                    driveCommand.invoke()
                }
            }
            checkPermission()
        }
        if (account != null) {
            onSignInListener.onSuccess()
        } else {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        signInResultContract.launch(signInIntent)
    }

    private fun onRegisterCallback(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (result.data != null) {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)
                account = task.result
                onSignInListener.onSuccess()
            } else {
                onSignInListener.onError()
            }
        } else {
            onSignInListener.onError()
        }
    }

    private fun checkPermission() {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (GoogleSignIn.hasPermissions(account, Scope(DriveScopes.DRIVE_FILE))) {
            onPermissionListener.onSuccess()
        } else {
            val signInIntent = googleSignInClient.signInIntent
            permissionResultContract.launch(signInIntent)
        }
    }

    private fun onPermissionCallback(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            onPermissionListener.onSuccess()
        } else {
            onPermissionListener.onError()
        }
    }

    private fun driveListeners() = with(googleDriveHelper) {
        lifecycleScope.launch {
            setOnFileExistListener {
                val deferred = CompletableDeferred<Boolean>()
                showOverwriteFileDialog { userResponse ->
                    deferred.complete(userResponse)
                }

                deferred.await()
            }
        }

        setOnSaveSuccessListener {
            showOnResultDialog(
                title = getString(R.string.pop_up_saved_success_title),
                content = getString(R.string.pop_up_saved_success_content)
            )
        }

        setOnSaveErrorListener { errorBody ->
            val errorContent = "${getString(R.string.pop_up_saved_error_content)}\n $errorBody"
            showOnResultDialog(
                title = getString(R.string.pop_up_saved_error_title),
                content = errorContent
            )
        }

        setOnBackupNotFoundListener { type ->
            val title = getString(R.string.pop_up_backup_error_title)
            val content = when (type) {
                GoogleDriveHelper.BackupNotFoundErrorType.FILE_NOT_FOUND -> getString(R.string.pop_up_backup_error_content_file)
                GoogleDriveHelper.BackupNotFoundErrorType.FOLDER_NOT_FOUND -> getString(R.string.pop_up_backup_error_content_folder)
            }
            showOnResultDialog(title, content)
        }

        setOnLoadSuccessListener {
            showOnResultDialog(
                title = getString(R.string.pop_up_load_success_title),
                content = getString(R.string.pop_up_load_success_content)
            )
        }

        setOnLoadErrorListener {
            showOnResultDialog(
                title = getString(R.string.pop_up_load_error_title),
                content = "${getString(R.string.pop_up_load_error_content)}\n $it"
            )
        }

    }

    private fun showOverwriteFileDialog(overwrite: (Boolean) -> Unit) {
        val leftButtonListener = object : PopUpDialog.PopUpDialogClickListener {
            override fun onClick(popUpDialog: PopUpDialog) {
                popUpDialog.dismiss()
                overwrite(true)
            }

        }

        val rightButtonListener = object : PopUpDialog.PopUpDialogClickListener {
            override fun onClick(popUpDialog: PopUpDialog) {
                popUpDialog.dismiss()
                overwrite(false)
                enableProgressBar(false)
            }
        }

        val dialog = PopUpDialog.Builder()
            .title(getString(R.string.pop_up_launch_title))
            .content(getString(R.string.pop_up_overwrite_content))
            .leftButtonText(getString(R.string.ok))
            .rightButtonText(getString(R.string.no))
            .leftButtonListener(leftButtonListener)
            .rightButtonListener(rightButtonListener)
            .animationType(PopUpDialog.AnimationType.SPLASH)
            .build()

        if (!childFragmentManager.isStateSaved) {
            dialog.show(childFragmentManager, PopUpDialog.TAG)
        }
        dialog.setOnCanceledListener {
            overwrite(false)
            enableProgressBar(false)
        }

    }

    private fun showOnResultDialog(
        title: String,
        content: String
    ) {
        val leftButtonListener = object : PopUpDialog.PopUpDialogClickListener {
            override fun onClick(popUpDialog: PopUpDialog) {
                popUpDialog.dismiss()
            }

        }
        val dialog = PopUpDialog.Builder()
            .title(title)
            .content(content)
            .leftButtonText(getString(R.string.ok))
            .leftButtonListener(leftButtonListener)
            .animationType(PopUpDialog.AnimationType.SPLASH)
            .build()


        if (!childFragmentManager.isStateSaved) {
            dialog.show(childFragmentManager, PopUpDialog.TAG)
            enableProgressBar(false)
        }

    }

    private fun enableProgressBar(value: Boolean) {
        binding.progressBar.isVisible = value
    }

    override fun onDestroyView() {
        super.onDestroyView()
        googleDriveHelper.clear()
        _binding = null
    }

    private inline fun onPermissionSuccess(crossinline callback: () -> Unit) {
        onPermissionListener = object : OnPermissionListener {
            override fun onSuccess() {
                callback()
            }

            override fun onError() {}
        }
    }

    private inline fun onSignInSuccess(crossinline callback: () -> Unit) {
        onSignInListener = object : OnSignInListener {
            override fun onSuccess() {
                callback()
            }

            override fun onError() {}
        }
    }

    private interface OnPermissionListener {
        fun onSuccess()
        fun onError()
    }

    private interface OnSignInListener {
        fun onSuccess()
        fun onError()
    }

}