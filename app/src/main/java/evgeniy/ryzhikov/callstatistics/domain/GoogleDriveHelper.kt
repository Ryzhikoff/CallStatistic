package evgeniy.ryzhikov.callstatistics.domain

import android.content.Context
import androidx.room.Room
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.InputStreamContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import evgeniy.ryzhikov.callstatistics.data.PhoneDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class GoogleDriveHelper @Inject constructor(
    private val context: Context,
    private val database: PhoneDatabase
) {

    private var coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var driveService: Drive

    fun init(account: GoogleSignInAccount) {
        coroutineScope = CoroutineScope(Dispatchers.IO)
        val credentials = GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_FILE))
        credentials.selectedAccount = account.account
        driveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credentials
        )
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    fun load() {
        coroutineScope.launch {
            val folderId = getFolderIdIfExist()
            if (folderId == null) {
                withContext(Dispatchers.Main) {
                    ActionListener.onBackupNotFound(BackupNotFoundErrorType.FOLDER_NOT_FOUND)
                }
            } else {
                val fileId = getFileIdIfExist(folderId)
                if (fileId == null) {
                    withContext(Dispatchers.Main) {
                        ActionListener.onBackupNotFound(BackupNotFoundErrorType.FILE_NOT_FOUND)
                    }
                } else {
                    loadFromDrive(fileId)
                }
            }
        }

    }

    private suspend fun loadFromDrive(fileId: String) {
        val backupFile = java.io.File(context.filesDir, FILE_NAME) // Путь к вашему файлу базы данных

        // Скачивание бекапа с Google Диска
        downloadBackupFileFromDrive(fileId, backupFile)

        // Добавление данных из бекапа в текущую базу данных
        val temporaryDb = Room.databaseBuilder(context, PhoneDatabase::class.java, backupFile.absolutePath).build()
        val phoneNumberList = temporaryDb.phoneNumberDao().getAll()
        val phoneTalkList = temporaryDb.phoneTalkDao().getAll()

        if (phoneNumberList.isNotEmpty() && phoneTalkList.isNotEmpty()) {
            database.phoneNumberDao().insertAll(phoneNumberList)
            database.phoneTalkDao().insertAll(phoneTalkList)
            println("Data inserted into local database successfully.")

            withContext(Dispatchers.Main) {
                ActionListener.onLoadSuccess()
            }
            // Удаление файла бекапа после успешного добавления данных в текущую базу данных
            backupFile.delete()
        } else {
            withContext(Dispatchers.Main) {
                ActionListener.onLoadError("Failed to read data from backup file.")
            }
        }
    }

    private suspend fun downloadBackupFileFromDrive(fileId: String, destinationFile: java.io.File) {
        withContext(Dispatchers.IO) {
            try {
                val outputStream = FileOutputStream(destinationFile)
                driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream)
                outputStream.close()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    ActionListener.onLoadError(e.toString())
                }
            }
        }
    }

    fun save() {
        coroutineScope.launch {
            val folderId = getFolderId()
            val fileId = getFileIdIfExist(folderId)

            if (fileId != null) {
                val isOverwrite = async {
                    withContext(Dispatchers.Main) {
                        ActionListener.onFileExist()
                    }
                }.await()


                if (isOverwrite) {
                    copyDatabase(folderId, fileId)
                }

            } else {
                copyDatabase(folderId)
            }
        }
    }


    private suspend fun getFolderId(): String {
        val existingFolderId = getFolderIdIfExist()
        return existingFolderId ?: createFolder()
    }

    private suspend fun getFolderIdIfExist(): String? {
        try {
            // Создаем запрос для Google Drive API
            val query = "'root' in parents and mimeType = 'application/vnd.google-apps.folder' and name = '$FOLDER_NAME'"
            val request = driveService.files().list().setQ(query)

            // Запускаем запрос и получаем список файлов
            val result = request.execute()
            val files = result.files

            // Если список файлов не пустой, значит папка существует, возвращаем ее идентификатор
            if (files != null && files.isNotEmpty()) {
                return files[0].id
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                ActionListener.onSaveError(e.toString())
            }
        }

        // Если папка не найдена, возвращаем null
        return null
    }

    private suspend fun createFolder(): String {
        return withContext(Dispatchers.IO) {
            try {
                // Создаем метаданные для новой папки
                val folderMetadata = File()
                folderMetadata.name = FOLDER_NAME
                folderMetadata.mimeType = "application/vnd.google-apps.folder"

                // Создаем запрос для Google Drive API
                val request = driveService.files().create(folderMetadata)

                // Запускаем запрос на создание папки
                val folder = request.execute()

                // Возвращаем идентификатор созданной папки
                return@withContext folder.id
            } catch (e: Exception) {
                // Обрабатываем ошибки
                withContext(Dispatchers.Main) {
                    ActionListener.onSaveError(e.toString())
                }
                return@withContext ""
            }
        }
    }

    private suspend fun getFileIdIfExist(folderId: String): String? {
        try {
            // Создаем запрос для Google Drive API
            val query = "'$folderId' in parents and name = '$FILE_NAME'"
            val request = driveService.files().list().setQ(query)

            // Запускаем запрос и получаем список файлов
            val result = request.execute()
            val files = result.files

            // Если список файлов не пустой, значит файл с заданным именем найден, возвращаем его идентификатор
            if (files != null && files.isNotEmpty()) {
                files.forEach {
                    println("fileName - ${it.name}")
                }
                return files[0].id
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                ActionListener.onSaveError(e.toString())
            }
        }

        // Если файл не найден, возвращаем null
        return null
    }

    private suspend fun copyDatabase(folderId: String, fileId: String? = null) {
        val dbHelper = database.openHelper

        val dbFile = dbHelper.writableDatabase.path
        val exportFile = java.io.File(context.cacheDir, FILE_NAME)

        try {
            if (dbFile != null) {
                java.io.File(dbFile).copyTo(exportFile, overwrite = true)
            } else {
                ActionListener.onSaveError("Error save database")
            }
        } catch (e: IOException) {
            withContext(Dispatchers.Main) {
                ActionListener.onSaveError(e.toString())
            }
        }

        if (folderId.isNotEmpty()) {
            // Загрузка файла на Google Диск
            copyFileToDrive(exportFile, folderId, fileId)
        } else {
            // Ошибка при создании папки
            withContext(Dispatchers.Main) {
                ActionListener.onSaveError("Failed to create folder.")
            }
        }
    }

    private suspend fun copyFileToDrive(fileToUpload: java.io.File, folderId: String, fileId: String? = null) {
        withContext(Dispatchers.IO) {
            try {
                // Открываем поток для чтения файла
                val inputStream: InputStream = FileInputStream(fileToUpload)

                // Создаем файл на Google Диске
                val fileMetadata = File()
                fileMetadata.name = fileToUpload.name
                fileMetadata.parents = listOf(folderId) // Идентификатор папки, в которую вы хотите загрузить файл

                // Создаем запрос для Google Drive API
                val mediaContent = InputStreamContent("application/octet-stream", inputStream)

                // Запускаем запрос на загрузку файла
                val request = driveService.files().create(fileMetadata, mediaContent)
                val newFile = request.execute()

                if (newFile.id != null) {
                    //Удаляем кэш и старый бекап с диска
                    fileToUpload.delete()
                    fileId?.let {
                        driveService.files().delete(fileId).execute()
                    }
                    withContext(Dispatchers.Main) {
                        ActionListener.onSaveSuccess()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        ActionListener.onSaveError("Error saved file to disk")
                    }
                }
            } catch (e: GoogleJsonResponseException) {
                if (e.statusCode == 404) {
                    // Ошибка 404: файл не найден
                    withContext(Dispatchers.Main) {
                        ActionListener.onSaveError("GoogleDrive File not found. Make sure the folderId is correct.")
                    }
                } else {
                    // Другие ошибки Google Drive API
                    withContext(Dispatchers.Main) {
                        ActionListener.onSaveError(e.toString())
                    }
                }
            } catch (e: Exception) {
                // Обрабатываем другие ошибки
                withContext(Dispatchers.Main) {
                    ActionListener.onSaveError(e.toString())
                }
            }
        }
    }

    fun clear() {
        if (coroutineScope.isActive) {
            coroutineScope.cancel()
        }
    }

    /**
    Listener is called when a file with a backup has already been saved

    Takes a Boolean as a response, whether to overwrite the file
     */
    suspend fun setOnFileExistListener(callback: suspend () -> Boolean) {
        ActionListener.OnFileExist {
            callback()
        }
    }

    /**
    The listener is called when saving the backup failed.

    It also returns the error text.
     */
    fun setOnSaveErrorListener(callback: (String) -> Unit) {
        ActionListener.OnSaveError { errorBody ->
            callback(errorBody)
        }
    }

    fun setOnSaveSuccessListener(callback: () -> Unit) {
        ActionListener.OnSaveSuccess {
            callback()
        }
    }

    fun setOnBackupNotFoundListener(callback: (BackupNotFoundErrorType) -> Unit) {
        ActionListener.OnBackupNotFound {
            callback(it)
        }
    }

    fun setOnLoadSuccessListener(callback: () -> Unit) {
        ActionListener.OnLoadSuccess {
            callback()
        }
    }

    fun setOnLoadErrorListener(callback: (String) -> Unit) {
        ActionListener.OnLoadError {
            callback(it)
        }
    }

    private object ActionListener {

        private open class BaseListener<T>(var execute: T) {
            operator fun invoke(execute: T): BaseListener<T> {
                this.execute = execute
                return this
            }
        }

        object OnFileExist : BaseListener<suspend () -> Boolean>({ true })

        suspend fun onFileExist(): Boolean {
            return OnFileExist.execute()
        }

        object OnSaveSuccess : BaseListener<() -> Unit>({})

        fun onSaveSuccess() {
            OnSaveSuccess.execute()
        }

        object OnSaveError : BaseListener<(String) -> Unit>({})

        fun onSaveError(errorBody: String) {
            OnSaveError.execute(errorBody)
        }

        object OnLoadSuccess : BaseListener<() -> Unit>({})

        fun onLoadSuccess() {
            OnLoadSuccess.execute()
        }

        object OnLoadError : BaseListener<(String) -> Unit>({})

        fun onLoadError(errorBody: String) {
            OnLoadError.execute(errorBody)
        }

        object OnBackupNotFound : BaseListener<(BackupNotFoundErrorType) -> Unit>({})

        fun onBackupNotFound(type: BackupNotFoundErrorType) {
            OnBackupNotFound.execute(type)
        }

    }


    enum class BackupNotFoundErrorType {
        FILE_NOT_FOUND,
        FOLDER_NOT_FOUND
    }

    private companion object {
        const val APPLICATION_NAME = "Call Statistics"
        const val FOLDER_NAME = "call_statistics"
        const val FILE_NAME = "backup_call_statistics_database.db"
    }
}
