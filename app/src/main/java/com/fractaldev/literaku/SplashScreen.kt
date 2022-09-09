package com.fractaldev.literaku

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.appindexing.Action
import com.google.firebase.appindexing.FirebaseUserActions
import com.google.firebase.appindexing.builders.AssistActionBuilder
import java.util.*

private const val OPEN_APP_FEATURE = "feature"

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val featureRequested = intent.data?.getQueryParameter("feature")
//        val featureRequested = intent?.extras?.getString(OPEN_APP_FEATURE)

        // check for deeplinks
        intent?.handleIntent()

        //menghilangkan ActionBar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash_screen)
        val handler = Handler()
        handler.postDelayed({
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }, 3000L) //3000 L = 3 detik
    }

    private fun Intent.handleIntent() {
        when (action) {
            // When the action is triggered by a deep-link, Intent.Action_VIEW will be used
            Intent.ACTION_VIEW -> handleDeepLink(data)
            // Otherwise start the app as you would normally do.
            else -> Unit
        }
    }

    private fun handleDeepLink(data: Uri?) {
        // path is normally used to indicate which view should be displayed
        // i.e https://sonique.assistant.test/start?exerciseType="Running" -> path = "start"
        var actionHandled = true
        when (data?.path) {
            DeepLink.OPEN -> {
                val featureRequested = data.getQueryParameter(DeepLink.Params.FEATURE).orEmpty()
                startRequestedFeature(featureRequested)
            }
            else -> {
                actionHandled = false
                Log.w("MainActivity", "DeepLink, path: ${data?.path} not handled")
                Unit
            }
        }

        notifyActionSuccess(actionHandled)
    }

    private fun notifyActionSuccess(succeed: Boolean) {

        intent.getStringExtra(DeepLink.Actions.ACTION_TOKEN_EXTRA)?.let { actionToken ->
            val actionStatus = if (succeed) {
                Action.Builder.STATUS_TYPE_COMPLETED
            } else {
                Action.Builder.STATUS_TYPE_FAILED
            }
            val action = AssistActionBuilder()
                .setActionToken(actionToken)
                .setActionStatus(actionStatus)
                .build()

            // Send the end action to the Firebase app indexing.
            FirebaseUserActions.getInstance(applicationContext).end(action)
        }
    }

    private fun startRequestedFeature(featureRequested: String) {
        when (featureRequested.toLowerCase()) {
            Commands.HOME,
            Commands.MAIN-> {
//                runAll = true
//                onInsertsClicked()
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
            Commands.SELECT  -> {
//                runAll = true
//                onSelectIndexedClicked()
            }
            else -> Unit
        }
    }
}

object DeepLink {
    const val OPEN = "/open"

    /**
     * Parameter types for the deep-links
     */
    object Params {
        const val FEATURE = "feature"
    }

    object Actions {
        const val ACTION_TOKEN_EXTRA = "actions.fulfillment.extra.ACTION_TOKEN"
    }
}

