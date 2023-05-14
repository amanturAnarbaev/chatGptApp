package com.example.chatgpt

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.amber.exseption.showToast
import com.example.chatgpt.databinding.FragmentMainBinding
import org.json.JSONObject

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: MessageRvAdapter
    private lateinit var messageList: ArrayList<MessageRvModal>
    private val url = "https://api.openai.com/v1/completions"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        messageList = ArrayList()

        adapter = MessageRvAdapter(messageList)
        binding.recycler.adapter = adapter


        binding.etQuery.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                if (binding.etQuery.text.toString().isNotEmpty()) {
                    messageList.add(MessageRvModal(binding.etQuery.text.toString(), "user"))
                    adapter.notifyDataSetChanged()
                    getResponse(binding.etQuery.text.toString())
                } else {
                    showToast("Please enter your query...")
                }
                return@OnEditorActionListener true
            }
            false
        })

    }

    private fun getResponse(query: String) {
        adapter = binding.recycler.adapter as MessageRvAdapter

        binding.etQuery.setText("")
        val queue: RequestQueue = Volley.newRequestQueue(requireContext())
        val jsonObject: JSONObject? = JSONObject()
        jsonObject?.put("model", "text-davinci-003")
        jsonObject?.put("prompt", query)
        jsonObject?.put("temperature", 0)
        jsonObject?.put("max_tokens", 100)
        jsonObject?.put("top_p", 1)
        jsonObject?.put("frequency_penalty", 0.0)
        jsonObject?.put("presence_penalty", 0.0)

        val postRequest: JsonObjectRequest = @SuppressLint("NotifyDataSetChanged") object :
            JsonObjectRequest(Method.POST, url, jsonObject, Response.Listener { response ->
                val responseMsg: String =
                    response.getJSONArray("choices").getJSONObject(0).getString("text")
                messageList.add(MessageRvModal(responseMsg, "bot"))
                binding.recycler.postDelayed({
                    binding.recycler.smoothScrollToPosition(adapter.itemCount)
                }, 200)
                adapter.notifyDataSetChanged()

            }, Response.ErrorListener {
                showToast("Fail to get response...")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json"
                params["Authorization"] =
                    "Bearer (in this part add your api key from open AI)"
                //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
                return params
            }
        }
        postRequest.retryPolicy = object : RetryPolicy {
            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun retry(error: VolleyError?) {

            }
        }
        queue.add(postRequest)
    }

}
