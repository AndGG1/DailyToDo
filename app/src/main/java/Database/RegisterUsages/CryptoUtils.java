package Database.RegisterUsages;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final byte[] key = "1234567890123456".getBytes();


    private final static ExecutorService executorService = Executors.newSingleThreadExecutor();
    public static String encrypt(String input) throws ExecutionException, InterruptedException {

        return executorService.submit(() -> {
            try {
                SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
                return Base64.encodeToString(encrypted, Base64.DEFAULT);
            } catch (Exception e) {
                return "-1";
            }
        }).get();
    }

    public static void decrypt(String input, CallbackC c) {

        executorService.execute(() -> {
            try {
                SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                byte[] decoded = Base64.decode(input, Base64.DEFAULT);
                byte[] decrypted = cipher.doFinal(decoded);
                c.onComplete(new String(decrypted, StandardCharsets.UTF_8));
            } catch (Exception e) {
                c.onComplete("-1");
            }
        });
    }

    public interface CallbackC {
        void onComplete(String res);
    }
}
