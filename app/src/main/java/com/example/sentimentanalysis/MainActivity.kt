package com.example.sentimentanalysis

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(),TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var buttonSpeak: Button? = null
    private var editText: EditText? = null
    private var miFrase: String = ""
    private var goodFoods: ArrayList<String> = ArrayList<String>()
    private var badFoods: ArrayList<String> = ArrayList<String>()
    private var goodWords: ArrayList<String> = ArrayList<String>()
    private var badWords: ArrayList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonSpeak = this.button_speak
        editText = this.edittext_input
        buttonSpeak!!.isEnabled = false;
        tts = TextToSpeech(this, this)

        buttonSpeak!!.setOnClickListener { speakOut() }
        readJSONS()
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            } else {
                buttonSpeak!!.isEnabled = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }

    }

    private fun speakOut() {
        miFrase = editText!!.text.toString()
        tts!!.speak(miFrase, TextToSpeech.QUEUE_FLUSH, null,"")
        analizeMoodandFood(miFrase)
    }

    public override fun onDestroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    private fun readJSONS(){
        var allGoodWords = applicationContext.assets.open("goodWords.txt").bufferedReader().use{
            it.readText()
        }
        var allBadWords = applicationContext.assets.open("badWords.txt").bufferedReader().use{
            it.readText()
        }
        var allBadFood = applicationContext.assets.open("badFoods.txt").bufferedReader().use{
            it.readText()
        }
        var allGoodFood = applicationContext.assets.open("goodFoods.txt").bufferedReader().use{
            it.readText()
        }
        var goodWord:String = ""
        var badWord:String = ""
        var goodFood:String = ""
        var badFood:String = ""
        for(i in allBadWords){
            if(i == '\n'){
                badWords.add(badWord)
                badWord =""
            }else
                badWord+=i
        }
        for(i in allGoodWords){
            if(i == '\n'){
                goodWords.add(goodWord)
                goodWord =""
            }else
                goodWord+=i
        }
        for(i in allGoodFood){
            if(i == '\n'){
                goodFoods.add(goodFood)
                goodFood =""
            }else
                goodFood+=i
        }
        for(i in allBadFood){
            if(i == '\n'){
                badFoods.add(badFood)
                badFood =""
            }else
                badFood +=i
        }

    }

    private fun determineFood(mood: Boolean): String{
        if(mood){
            return goodFoods.random()
        }else{
            return badFoods.random()
        }
    }

    private fun determineMood(frase: List<String>): Boolean{
        var determiner = 0
        for (words in frase){
            if(words in goodWords){
                determiner += 1
            }else if(words in badWords){
                determiner -=1
            }
        }
        return determiner>=0
    }
    private fun analizeMoodandFood(frase: String){
        val fraseWords = frase.split(", ",". "," ")
        tts!!.speak("Based on your mood, I recommend you to eat some${determineFood(determineMood(fraseWords))}", TextToSpeech.QUEUE_FLUSH, null,"")
    }


}
