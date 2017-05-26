package com.fyx.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.fyx.andr.R
import com.fyx.utils.KotlinUtils
import com.fyx.utils.Utils

class KotlinActivity : AppCompatActivity() {

    var editText1:TextView?=null
    var editText2:TextView?=null
    var editTextsum:TextView?=null
    var btnAdd:Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
        editText1 = findViewById(R.id.editText1) as TextView?
        editText2 = findViewById(R.id.editText2) as TextView?
        editTextsum = findViewById(R.id.editTextsum) as TextView?
        btnAdd = findViewById(R.id.btn_add) as Button?

        var testKotlin:KotlinUtils? = KotlinUtils()

        var width = Utils.getScreenWidth(this)

        btnAdd?.setOnClickListener {
            v: View? ->
        }

        btnAdd?.setOnClickListener {
            var first= editText1!!.getText().toString()
            var second = editText2!!.getText().toString()
            if(TextUtils.isEmpty(first)||TextUtils.isEmpty(second)){
                Toast.makeText(this,"数据不能为空",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            var sum = testKotlin?.sum(first as Int, second as Int)
            editTextsum?.setText(sum.toString())
        }
    }


}
