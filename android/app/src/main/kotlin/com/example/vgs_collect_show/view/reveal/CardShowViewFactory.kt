package com.example.vgs_collect_show.view.reveal

import android.content.Context
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class CardShowViewFactory constructor(private val messenger: BinaryMessenger? = null) :
        PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    override fun create(context: Context, id: Int, args: Any?): PlatformView {
        return CardShowFormView(context, messenger, id)
    }
}