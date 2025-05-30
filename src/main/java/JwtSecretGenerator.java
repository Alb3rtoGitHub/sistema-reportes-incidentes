import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Base64;

public class JwtSecretGenerator {
    public static void main(String[] args) {
        SecretKey secretKey = Jwts.SIG.HS512.key().build();
        String base64Secret = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println("Clave Base64: " + base64Secret);
    }
}
