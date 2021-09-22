package com.example.vgs_collect_show.view.collect

import android.content.Context
import android.util.Log
import android.widget.TextView
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.example.vgs_collect_show.MainActivity
import com.example.vgs_collect_show.R
import com.example.vgs_collect_show.view.BaseFormView
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.json.JSONObject


class CardCollectFormView constructor(context: Context, messenger: BinaryMessenger?, id: Int) :
        BaseFormView(context, messenger, id, R.layout.collect_form_layout) {

    override val viewType: String get() = MainActivity.COLLECT_FORM_VIEW_TYPE

    private val vgsCollect = VGSCollect(context, MainActivity.VAULT_ID, MainActivity.ENVIRONMENT)

    private val cardNumberAlias = rootView.findViewById<TextView>(R.id.tvCardNumberAlias)
    private val cardDateAlias = rootView.findViewById<TextView>(R.id.tvExpDateAlias)
    private val cardHolderNameAlias = rootView.findViewById<TextView>(R.id.tvCardHolderAlias)
    private val cardCvcAlias = rootView.findViewById<TextView>(R.id.tvcardCvcAlias)
    private val cardSsnAlias = rootView.findViewById<TextView>(R.id.tvSSNAlias)

    init {
        vgsCollect.bindView(rootView.findViewById(R.id.etCardNumber))
        vgsCollect.bindView(rootView.findViewById(R.id.etExpDate))
        vgsCollect.bindView(rootView.findViewById(R.id.cardHolderNameField))
        vgsCollect.bindView(rootView.findViewById(R.id.cardCVCField))
        vgsCollect.bindView(rootView.findViewById(R.id.ssnField))
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "redactCard" -> redactCard(result)
        }
    }

    private fun redactCard(result: MethodChannel.Result) {
        runOnBackground {
            with(vgsCollect.submit("post", HTTPMethod.POST)) {
                runOnMain {
                    when (this) {
                        is VGSResponse.SuccessResponse -> handleSuccess(this, result)
                        is VGSResponse.ErrorResponse -> result.error(this.code.toString(), this.localizeMessage, this.body)
                    }
                }
            }
        }
    }

    private fun handleSuccess(successResponse: VGSResponse.SuccessResponse, result: MethodChannel.Result) {
        with(successResponse.body?.toJson()) {
            var cardAlias: String? = null
            var dateAlias: String? = null
            var nameAlias: String? = null
            var cvcAlias: String? = null
            var ssnAlias: String? = null
            try {
                (this?.get("json") as? JSONObject)?.let {
                    cardAlias = it.get("cardNumber").toString()
                    nameAlias = it.get("personName").toString()
                    dateAlias = it.get("expDate").toString()
                    cvcAlias = it.get("card_cvc").toString()
                    ssnAlias = it.get("card_ssn").toString()

                }
            } catch (e: Exception) {
                Log.d("CardCollectFormView", e.toString())
            }
            cardNumberAlias.text = cardAlias.toString()
            cardDateAlias.text = dateAlias.toString()
            cardHolderNameAlias.text = nameAlias.toString()
            cardCvcAlias.text = cvcAlias.toString()
            cardSsnAlias.text = ssnAlias.toString()
            result.success(mapOf(
                    "cardNumber" to cardAlias,
                    "personName" to nameAlias,
                    "expDate" to dateAlias,
                    "card_cvc" to cvcAlias,
                    "card_ssn" to ssnAlias
            ))
        }
    }

    private fun String.toJson(): JSONObject? = try {
        JSONObject(this)
    } catch (e: Exception) {
        null
    }
}