package com.example.vgs_collect_show

import com.example.vgs_collect_show.view.collect.CardCollectViewFactory
import com.example.vgs_collect_show.view.reveal.CardShowViewFactory
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine

class MainActivity : FlutterActivity() {

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        val registry = flutterEngine.platformViewsController.registry
        registry.registerViewFactory(COLLECT_FORM_VIEW_TYPE, CardCollectViewFactory(flutterEngine.dartExecutor.binaryMessenger))
        registry.registerViewFactory(SHOW_FORM_VIEW_TYPE, CardShowViewFactory(flutterEngine.dartExecutor.binaryMessenger))
    }

    companion object {

        const val VAULT_ID = "tntklbx0kmd"
        const val ENVIRONMENT = "sandbox"

        const val COLLECT_FORM_VIEW_TYPE = "card-collect-form-view";
        const val SHOW_FORM_VIEW_TYPE = "card-show-form-view";
    }
}
