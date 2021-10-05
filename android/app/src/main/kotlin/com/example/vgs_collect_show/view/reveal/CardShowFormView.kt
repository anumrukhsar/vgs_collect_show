package com.example.vgs_collect_show.view.reveal

import android.content.Context
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.example.vgs_collect_show.MainActivity
import com.example.vgs_collect_show.R
import com.example.vgs_collect_show.view.BaseFormView
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
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
//private fun revealCard(call: MethodCall) {
//    val data = call.arguments as ArrayList<*>
//    val headerName = "Authorization";
//    val headerval = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJCaVRrRjlMbTA4d2hWM2x1QXFfQks1WHpTbVFoVVhGOTBJV3o3XzJWS1dBIn0.eyJleHAiOjE2MzI3MjI2NDYsImlhdCI6MTYzMjcyMjM0NiwianRpIjoiZTFiYjk2MjctMGVlNS00NTQyLWIwNDUtMTIzN2ZlMzM4YWIyIiwiaXNzIjoiaHR0cHM6Ly9hdXRoLWRldi5jb250YXN3YXAuaW8vYXV0aC9yZWFsbXMvc29raW4iLCJhdWQiOlsicmVhbG0tbWFuYWdlbWVudCIsImFjY291bnQiXSwic3ViIjoiZTRjZmUzZDEtYWNhNC00ODc4LWEzZmEtYzNiYzI5OWQ1MzUyIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic29raW4iLCJzZXNzaW9uX3N0YXRlIjoiMWU5OTE0MjMtMzUzNi00NjRlLTkwZjQtOTM2NmFkYjk1OTg2IiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJtYW5hZ2UtdXNlcnMiXX0sInNva2luIjp7InJvbGVzIjpbInVtYV9wcm90ZWN0aW9uIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6IiIsImluc3RpdHV0aW9uX3JlZiI6IjcyNjczQjlBOUIwOUI1NTZBNDlFOTRGRDY0Rjk2ODBCIiwiY2xpZW50SWQiOiJzb2tpbiIsImNsaWVudEhvc3QiOiI1Mi4yLjE4My44NSIsImNsaWVudEFkZHJlc3MiOiI1Mi4yLjE4My44NSJ9.h7dOS6I2K7P3UMnT8kKW3wNEMH4fw9IjjAFqSfdXr7gcrRR55yTgvY5mBf9PFpF5afy5tp5VGOXU7MSAVf6D8sfLS3Byy1qxYRBbCPjbRiNLvCHC_1Eg1dnKkUYq580eRYWhejepkJhf04vdzwsPSIbRqltzheYG7jshauOda4LKHGHkYiX-6gBFUaYIJ7FEh0EpZVC30AmUuCzBXr8cmK2stCKdB8NlN4ZSVIQuX_fV-0Dkfe7zNigPDY5u8gYW-htVFUaTH8g1RsEYBzdLb-AB5X0AfNrFgFPx-9CltRTxJfOdZXtyfcfFHZC0L2-FOR5q6OHHJ1Pk7bSwdCIW7w";
//    runOnBackground {
//        val request = VGSRequest.Builder("/beta_v3/virtual_cards/1", VGSHttpMethod.GET)
//            .headers(mapOf(headerName to headerval))
////            .setHostname("swapcards.com.br")
//            .build()
//
//        vgsShow.requestAsync(request)
//        vgsShow.addOnResponseListener(object :VGSOnResponseListener{
//            override fun onResponse(response: VGSResponse) {
//                when (response) {
//                    is VGSResponse.Success -> {
//                        val successCode = response.code
//                    }
//                    is VGSResponse.Error -> {
//                        val errorCode = response.code
//                        val message = response.message
//                    }
//                }            }
//        }
//        )
//    }
//}

}