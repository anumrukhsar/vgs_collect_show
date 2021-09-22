package com.example.vgs_collect_show.view.reveal

import android.content.Context
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.example.vgs_collect_show.MainActivity
import com.example.vgs_collect_show.R
import com.example.vgs_collect_show.view.BaseFormView
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class CardShowFormView constructor(context: Context, messenger: BinaryMessenger?, id: Int) :
        BaseFormView(context, messenger, id, R.layout.show_form_layout) {

    override val viewType: String get() = MainActivity.SHOW_FORM_VIEW_TYPE

    private val vgsShow = VGSShow(context, MainActivity.VAULT_ID, MainActivity.ENVIRONMENT)

    init {

        vgsShow.subscribe(rootView.findViewById(R.id.tvCardNumber))
        vgsShow.subscribe(rootView.findViewById(R.id.tvCardExpiration))
        vgsShow.subscribe(rootView.findViewById(R.id.tvCardHolderName))
        vgsShow.subscribe(rootView.findViewById(R.id.tvCardCvc))
        vgsShow.subscribe(rootView.findViewById(R.id.tvCardSSn))
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "revealCard" -> revealCard(call)
        }
    }

    private fun revealCard(call: MethodCall) {
        val data = call.arguments as ArrayList<*>
        runOnBackground {
            vgsShow.request("post", VGSHttpMethod.POST, mapOf(
                    "payment_card_number" to data[0],
                    "payment_person_name" to data[1],
                    "payment_card_expiration_date" to data[2],
                    "payment_card_cvc" to data[3],
                    "payment_card_ssn" to data[4]
            ))
        }
    }
}