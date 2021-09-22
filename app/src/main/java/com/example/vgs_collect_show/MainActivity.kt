package com.example.vgs_collect_show

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.vgs_collect_show.databinding.ActivityMainBinding
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var vgsShow: VGSShow
    private lateinit var vgsForm: VGSCollect
    private lateinit var binding: ActivityMainBinding
    private var cardAlias = ""

    companion object {
        const val VAULT_ID = "tntklbx0kmd"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setUpCollect()
        setupShow()

    }

    private fun setUpCollect() {
        vgsForm = VGSCollect(this, VAULT_ID,
            com.verygoodsecurity.vgscollect.core.Environment.SANDBOX
        )
        vgsForm.bindView(binding.cardNumberField)
        binding.submitBtn.setOnClickListener {
            submitData()
        }
        vgsForm.addOnResponseListeners(object : VgsCollectResponseListener{
            override fun onResponse(response: com.verygoodsecurity.vgscollect.core.model.network.VGSResponse?) {
                Log.e("Collect Response=>", response.toString());
               cardAlias=""
               when(response){
                   is com.verygoodsecurity.vgscollect.core.model.network.VGSResponse.SuccessResponse ->{
                       val code = response?.code
                       val body = response?.body
                       cardAlias = JSONObject(response?.body.toString()).get("data")
                           .let { JSONObject(it.toString()).get("card_number") }
                           .toString()
                   }
                   is com.verygoodsecurity.vgscollect.core.model.network.VGSResponse.ErrorResponse ->{
                       val code = response?.errorCode
                       val message = response?.localizeMessage
                       Toast.makeText(this@MainActivity,message.toString(),Toast.LENGTH_SHORT).show()
                   }
               }

                binding.tvCardAlias.text= cardAlias

            }
        })

    }

    private fun setupShow() {
        vgsShow = VGSShow(this, VAULT_ID, VGSEnvironment.Sandbox())
        vgsShow.subscribe(binding.infoField)

        vgsShow.addOnResponseListener(object : VGSOnResponseListener {

            override fun onResponse(response: VGSResponse) {
                Log.e("Show Response=>", response.toString());
                when (response) {
                    is VGSResponse.Success -> {
                        val successCode = response.code
                    }
                    is VGSResponse.Error -> {
                        val errorCode = response.code
                        val message = response.message
                    }
                }
            }
        })
        // vgsShow.requestAsync("/post", VGSHttpMethod.POST,createRequestPayload())

        binding.revealButton.setOnClickListener {
            revealData()
        }
    }

    private fun revealData() {
        vgsShow.requestAsync(
            VGSRequest.Builder("post", VGSHttpMethod.POST).body(
                createRequestPayload()
            ).build()
        )
    }

    private fun submitData() {
        vgsForm.asyncSubmit("/post", HTTPMethod.POST)
    }
    private fun createRequestPayload(): Map<String, Any> {
        return mapOf("payment_card_number" to cardAlias)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this!!.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onDestroy() {
        vgsForm.onDestroy()
        super.onDestroy()
    }

}