package com.example.lifeafterdorm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lifeafterdorm.databinding.FragmentEmailVerifyBinding
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import java.util.Properties
import kotlin.random.Random


class EmailVerifyFragment : Fragment() {
    private lateinit var binding: FragmentEmailVerifyBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmailVerifyBinding.inflate(inflater, container, false)
        val view = binding.root
        val pageTitleTextView = view.findViewById<TextView>(R.id.pageTitle)
        if (pageTitleTextView != null) {
            pageTitleTextView.text = "Email Verification"
        }

        view.findViewById<ImageView>(R.id.viewback).setOnClickListener{
            findNavController().navigateUp()
        }
        return view
    }

    private fun generateRandomCode(): String {
        // 生成一个6位随机验证码
        val random = Random
        val code = StringBuilder()
        repeat(6) {
            val digit = random.nextInt(10)
            code.append(digit)
        }
        return code.toString()
    }

    private fun sendEmail(recipientEmail: String, code: String) {
        val username = "your_email@example.com" // 你的发送邮件地址
        val password = "your_email_password" // 你的发送邮件密码

        val props = Properties()
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = "smtp.example.com" // 你的 SMTP 服务器地址
        props["mail.smtp.port"] = "587" // SMTP 服务器端口号

        val session = Session.getInstance(props,
            object : javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(username, password)
                }
            })

        Thread(Runnable {
            try {
                val message = MimeMessage(session)
                message.setFrom(InternetAddress(username))
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                message.subject = "Verification Code"
                message.setText("Your verification code is: $code")

                Transport.send(message)

                println("Verification email sent to $recipientEmail")
            } catch (e: MessagingException) {
                e.printStackTrace()
            }
        }).start()
    }
}
