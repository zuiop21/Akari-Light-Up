package akari.lightup

import android.content.Context
import java.io.File

class FileManager(private val context: Context) {

    fun createJSONFile() {
        val jsonContent = context.getString(R.string.inittime).trimIndent()

        val fileName = context.getString(R.string.scores_json)
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            file.createNewFile()
            try {
                file.writeText(jsonContent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}