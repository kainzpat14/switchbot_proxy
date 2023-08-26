package sh.kainz.switchbotApi

import java.time.Instant
import java.util.*
import javax.crypto.Mac

import javax.crypto.spec.SecretKeySpec

data class SwitchbotSignature(val time : String, val token : String, val nonce : String, val signature : String)

interface ISwitchbotSigner {
    fun sign() : SwitchbotSignature
}

class SwitchbotSigner (private val token : String, private val secret : String) : ISwitchbotSigner {
    //source: https://github.com/OpenWonderLabs/SwitchBotAPI#authentication
    override fun sign() : SwitchbotSignature {
        val nonce = UUID.randomUUID().toString()
        val time = "" + Instant.now().toEpochMilli()
        val data = token + time + nonce
        val secretKeySpec = SecretKeySpec(secret.toByteArray(charset("UTF-8")), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKeySpec)
        val signature = String(Base64.getEncoder().encode(mac.doFinal(data.toByteArray(charset("UTF-8")))))
        return SwitchbotSignature(time,token,nonce,signature)
    }
}