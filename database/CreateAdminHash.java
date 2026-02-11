import org.mindrot.jbcrypt.BCrypt;

public class CreateAdminHash {
    public static void main(String[] args) {
        String password = "admin123";
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("Hash for 'admin123': " + hashed);
        
        // Test verification
        boolean check = BCrypt.checkpw(password, hashed);
        System.out.println("Verification: " + check);
    }
}
