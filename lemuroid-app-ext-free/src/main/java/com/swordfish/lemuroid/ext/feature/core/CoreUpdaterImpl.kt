/*
 * CoreUpdaterImpl.kt
 *
 * Downloads libretro cores from the official Libretro nightly buildbot:
 * https://buildbot.libretro.com/nightly/android/latest/
 */

package com.swordfish.lemuroid.ext.feature.core

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.swordfish.lemuroid.common.files.safeDelete
import com.swordfish.lemuroid.lib.core.CoreUpdater
import com.swordfish.lemuroid.lib.library.CoreID
import com.swordfish.lemuroid.lib.preferences.SharedPreferencesHelper
import com.swordfish.lemuroid.lib.storage.DirectoriesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import timber.log.Timber
import java.io.File

class CoreUpdaterImpl(
    private val directoriesManager: DirectoriesManager,
    retrofit: Retrofit,
) : CoreUpdater {

    private val api = retrofit.create(CoreUpdater.CoreManagerApi::class.java)

    override suspend fun downloadCores(
        context: Context,
        coreIDs: List<CoreID>,
    ) {
        val sharedPreferences = SharedPreferencesHelper.getSharedPreferences(context.applicationContext)
        coreIDs.asFlow()
            .onEach { retrieveAssets(it, sharedPreferences) }
            .onEach { downloadCoreIfNeeded(it) }
            .collect()
    }

    private suspend fun retrieveAssets(
        coreID: CoreID,
        sharedPreferences: SharedPreferences,
    ) {
        CoreID.getAssetManager(coreID)
            .retrieveAssetsIfNeeded(api, directoriesManager, sharedPreferences)
    }

    private suspend fun downloadCoreIfNeeded(coreID: CoreID) {
        val destFile = getCoreFile(coreID)
        if (destFile.exists()) {
            Timber.d("Core already present: ${coreID.libretroFileName}")
            return
        }
        downloadCoreFromBuildbot(coreID, destFile)
    }

    private suspend fun downloadCoreFromBuildbot(coreID: CoreID, destFile: File) {
        val abi = Build.SUPPORTED_ABIS.first()
        val zipFileName = "${coreID.libretroFileName}.zip"
        val url = "$BUILDBOT_BASE_URL/$abi/$zipFileName"

        Timber.i("Downloading core ${coreID.libretroFileName} from buildbot: $url")

        try {
            val response = api.downloadZip(url)
            if (!response.isSuccessful) {
                throw Exception("HTTP ${response.code()}: ${response.errorBody()?.string()}")
            }

            withContext(Dispatchers.IO) {
                response.body()?.use { zipInputStream ->
                    val entry = zipInputStream.nextEntry
                        ?: throw Exception("Empty zip for ${coreID.libretroFileName}")
                    Timber.d("Extracting zip entry: ${entry.name}")
                    destFile.parentFile?.mkdirs()
                    zipInputStream.copyTo(destFile.outputStream())
                }
            }
            Timber.i("Core downloaded successfully: ${destFile.absolutePath}")
        } catch (e: Throwable) {
            destFile.safeDelete()
            Timber.e(e, "Failed to download core ${coreID.libretroFileName}")
            throw e
        }
    }

    private suspend fun getCoreFile(coreID: CoreID): File =
        withContext(Dispatchers.IO) {
            val coresDir = directoriesManager.getCoresDirectory()
            File(coresDir, coreID.libretroFileName)
        }

    companion object {
        const val BUILDBOT_BASE_URL =
            "https://buildbot.libretro.com/nightly/android/latest"
    }
}
