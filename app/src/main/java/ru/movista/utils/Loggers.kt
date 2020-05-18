package ru.movista.utils

import android.content.Context
import android.util.Log
import com.crashlytics.android.Crashlytics
import org.jetbrains.anko.doAsync
import ru.movista.data.source.local.FILE_LOG_NAME
import timber.log.Timber
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FileLogTree(val context: Context) : Timber.Tree() {

    companion object {
        private const val TAG_LOG = "file_log_handler"
    }

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        doAsync(
            exceptionHandler = { Timber.tag(TAG_LOG).e(it, "error submitting task") },
            executorService = executorService
        ) {
            writeLogToLocalFile(tag, message)
        }
    }

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return (priority > Log.DEBUG) && TAG_LOG != tag
    }

    private fun writeLogToLocalFile(tag: String?, message: String) {
        val rootPath = context.filesDir
        val file = File(rootPath, FILE_LOG_NAME)

        createNewFileIfAbsent(file)

        try {
            val writer = BufferedWriter(
                FileWriter(
                    file,
                    shouldAppend(file)
                )
            )
            writer.use {
                tag?.let { tag ->
                    writer.append("$tag: $message")
                } ?: writer.append(message)

                writer.newLine()
            }
        } catch (e: Exception) {
            Timber.tag(TAG_LOG).e(e, "error adding log")
        }
    }

    private fun createNewFileIfAbsent(file: File) {
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: Exception) {
                Timber.tag(TAG_LOG).e(e, "error creating the $FILE_LOG_NAME")
            }
        }
    }

    private fun shouldAppend(file: File): Boolean {
        var shouldAppend = false
        try {
            val lastModified = file.lastModified()
            val currentTime = System.currentTimeMillis()
            val difference = currentTime - lastModified
            val diffInHours = difference / 3600000 // мс в часы
            if (diffInHours < 2) shouldAppend = true
        } catch (e: Exception) {
            Timber.tag(TAG_LOG).e(e, "error getting lastModified")
        }
        return shouldAppend
    }


}

class CrashReportingTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.ERROR) {
            if (t != null) {
                Crashlytics.logException(t)
            } else {
                Crashlytics.logException(Throwable(message))
            }
        }
    }
}