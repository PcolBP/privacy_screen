package ca.couver.privacy_screen

import android.app.Activity
import android.content.Context
import android.view.WindowManager.LayoutParams
import androidx.annotation.NonNull
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** PrivacyScreenPlugin */
class PrivacyScreenPlugin : FlutterPlugin, MethodCallHandler, ActivityAware, DefaultLifecycleObserver {

    private lateinit var channel: MethodChannel
    private var activity: Activity? = null
    private lateinit var context: Context

    // Plugin configuration
    private var enableSecure: Boolean = false
    private var autoLockAfterSeconds: Long = -1
    private var timeEnteredBackground: Long = 0

    // -------------------- FlutterPlugin --------------------

    override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        context = binding.applicationContext
        channel = MethodChannel(binding.binaryMessenger, "channel.couver.privacy_screen")
        channel.setMethodCallHandler(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        channel.setMethodCallHandler(null)
    }

    // -------------------- MethodCallHandler --------------------

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "updateConfig" -> {
                enableSecure = call.argument<Boolean>("enableSecureAndroid") ?: false
                autoLockAfterSeconds =
                    call.argument<Number>("autoLockAfterSecondsAndroid")?.toLong() ?: -1

                applySecureFlag() // Apply immediately if activity is available
                result.success(true)
            }
            else -> result.notImplemented()
        }
    }

    private fun applySecureFlag() {
        activity?.window?.let { window ->
            if (enableSecure) {
                window.addFlags(LayoutParams.FLAG_SECURE)
            } else {
                window.clearFlags(LayoutParams.FLAG_SECURE)
            }
        }
    }

    // -------------------- DefaultLifecycleObserver --------------------

    private fun judgeLock() {
        if (autoLockAfterSeconds >= 0 &&
            timeEnteredBackground > 0 &&
            (System.currentTimeMillis() - timeEnteredBackground) / 1000 > autoLockAfterSeconds
        ) {
            channel.invokeMethod("lock", null)
        }
        timeEnteredBackground = 0
    }

    override fun onResume(owner: LifecycleOwner) {
        channel.invokeMethod("onLifeCycle", "onResume")
        judgeLock()
    }

    override fun onPause(owner: LifecycleOwner) {
        channel.invokeMethod("onLifeCycle", "onPause")
        timeEnteredBackground = System.currentTimeMillis()
    }

    override fun onStart(owner: LifecycleOwner) {
        channel.invokeMethod("onLifeCycle", "onStart")
    }

    override fun onStop(owner: LifecycleOwner) {
        channel.invokeMethod("onLifeCycle", "onStop")
    }

    override fun onCreate(owner: LifecycleOwner) {
        channel.invokeMethod("onLifeCycle", "onCreate")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        channel.invokeMethod("onLifeCycle", "onDestroy")
    }

    // -------------------- ActivityAware --------------------

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        applySecureFlag() // Apply FLAG_SECURE as soon as activity is available
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        activity = null
    }
}
