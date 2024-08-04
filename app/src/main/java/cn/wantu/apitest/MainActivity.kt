package cn.wantu.apitest

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import cn.wantu.apitest.data.ApiTestConfig
import cn.wantu.apitest.ui.activity.ApiTest
import cn.wantu.apitest.ui.dialog.DownloadProgressDialog
import cn.wantu.apitest.ui.dialog.MessageDialog
import cn.wantu.apitest.utils.HttpUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException

class MainActivity : ComponentActivity() {

    private var showUpdateDialog by mutableStateOf(false)
    private var latestVersion by mutableStateOf("")
    private var updateContent by mutableStateOf("")

    private var showProgressDialog by mutableStateOf(false)
    private var downloadTitle by mutableStateOf("")
    private var progress by mutableFloatStateOf(0f)

    private var showInstallDialog by mutableStateOf(false)
    private var installContent by mutableStateOf("")

    companion object {
        lateinit var json: ApiTestConfig
    }

    private val getUnknownAppSourcesPermission =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                startActivity(newInstallIntent())
            } else {
                Toast.makeText(this, "请求权限失败", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        setContent {
            UpdateDialog(coroutineScope)
            ProgressDialog()
            InstallDialog()
        }
        coroutineScope.launch {
            // 获取最新版本
            json = Json.decodeFromString<ApiTestConfig>(
                HttpUtils.get("https://docs.wty5.cn/App/ApiTestConfig.json")
                    .excuteString()!!
            )
            val pi = packageManager.getPackageInfo(packageName, 0)
            @Suppress("DEPRECATION")
            val currentVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pi.longVersionCode
            } else {
                pi.versionCode.toLong()
            }
            if (currentVersion < json.versionCode) {
                // 有新版本
                showUpdateDialog = true // 显示更新对话框
                latestVersion = json.version // 最新版本
                updateContent = json.content // 更新内容
            } else {
                gotoTestPage()
            }
        }
    }

    @Composable
    private fun InstallDialog() {
        MessageDialog(
            showDialog = showInstallDialog,
            title = "下载成功",
            message = installContent,
            confirmText = "安装",
            onConfirm = {
                showInstallDialog = false
                installApk()
            },
            onDismiss = {
                showInstallDialog = false
                gotoTestPage()
            }
        )
    }

    @Composable
    private fun ProgressDialog() {
        DownloadProgressDialog(
            showDialog = showProgressDialog,
            progress = progress,
            title = downloadTitle,
            onCancel = {
            }
        )
    }

    @Composable
    private fun UpdateDialog(coroutineScope: CoroutineScope) {
        MessageDialog(
            showDialog = showUpdateDialog,
            title = "有新版本$latestVersion",
            message = updateContent,
            confirmText = "更新",
            onConfirm = {
                showUpdateDialog = false
                showProgressDialog = true
                coroutineScope.launch {
                    val fileName = "ApiTest_${json.version}.apk"
                    // 创建目标文件
                    val file = File(getExternalFilesDir(null), fileName)
                    downloadTitle = "Downloading $fileName..."
                    showProgressDialog = true
                    HttpUtils.get(json.download)
                        .excute().use { response ->
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
                            // 下载并保存文件
                            response.body?.let { body ->
                                val contentLength = body.contentLength() - json.skip
                                body.source().let { source ->
                                    file.outputStream().use { output ->
                                        source.skip(json.skip.toLong())
                                        val sink = output.sink().buffer()
                                        val buffer = ByteArray(8192)
                                        var totalBytesRead: Long = 0
                                        var bytesRead: Int
                                        while (source.read(buffer)
                                                .also { bytesRead = it } != -1
                                        ) {
                                            sink.write(buffer, 0, bytesRead)
                                            totalBytesRead += bytesRead
                                            progress = totalBytesRead.toFloat() / contentLength
                                            println("进度: $progress, 总读取字节数: $totalBytesRead")
                                        }
                                        sink.flush()
                                    }
                                }
                            }
                            // 下载完成
                            showProgressDialog = false
                            showInstallDialog = true
                            installContent = "File downloaded to: ${file.absolutePath}"
                        }
                }
            },
            onDismiss = {
                // 不更新
                gotoTestPage()
            }
        )
    }

    private fun gotoTestPage() {
        startActivity(Intent(this, ApiTest::class.java))
        finish()
    }

    private fun newInstallIntent() = Intent(Intent.ACTION_VIEW).apply {
        val file = File(getExternalFilesDir(null), "ApiTest_${json.version}.apk")
        val apkUri =
            FileProvider.getUriForFile(this@MainActivity, "${packageName}.fileprovider", file)
                .apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
        setDataAndType(apkUri, "application/vnd.android.package-archive")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    private fun installApk() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!packageManager.canRequestPackageInstalls()) {
                // 跳转到设置页面以请求安装未知应用的权限
                val intent1 = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                intent1.data = Uri.parse("package:${packageName}")
                getUnknownAppSourcesPermission.launch(intent1)
                return
            }
        }
        startActivity(newInstallIntent())
    }

}
