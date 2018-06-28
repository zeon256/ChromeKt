import java.sql.ResultSet

data class ChromeAccount(val website: String,
                         val username: String,
                         val clearTextPassword: String) {
    constructor(rs: ResultSet) : this(
            website = rs.getString(1),
            username = rs.getString(2),
            clearTextPassword = getWin32Password(rs.getBytes(3))
    )
}