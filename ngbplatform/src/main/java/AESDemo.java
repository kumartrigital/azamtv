import java.math.BigInteger;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.Days;
import org.joda.time.LocalDate;

public class AESDemo 
{
private static String algorithm = "AES";
//private static byte[] keyValue=new byte[] {'A','S','H','O','K','R','E','D','D','Y','R','E','D','D','Y','A'};// your key
private static byte[] keyValue=new String("hugoadminhugoadm").getBytes();

    // Performs Encryption
    public static String encrypt(String plainText) throws Exception 
    {
    	
    	ArrayList<String> arrayList=new ArrayList<String>();
    	arrayList.add(0, "hi");
    	arrayList.add(1, "hello");
    	arrayList.add(2, "world");
    	arrayList.add(0, "hai");
    	System.out.println(arrayList);
            Key key = generateKey();
            Cipher chiper = Cipher.getInstance(algorithm);
            chiper.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = chiper.doFinal(plainText.getBytes());
            String encryptedValue = new String(Base64.encodeBase64(encVal));
            return encryptedValue;
    }

    // Performs decryption
    public static String decrypt(String encryptedText) throws Exception 
    {
            // generate key new BASE64Decoder().decodeBuffer(encryptedText);
            Key key = generateKey();
            Cipher chiper = Cipher.getInstance(algorithm);
            chiper.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = Base64.decodeBase64(encryptedText);
            byte[] decValue = chiper.doFinal(decordedValue);
            String decryptedValue = new String(decValue);
            return decryptedValue;
    }

//generateKey() is used to generate a secret key for AES algorithm
    private static Key generateKey() throws Exception 
    {
            Key key = new SecretKeySpec(keyValue, algorithm);
            return key;
    }

    // performs encryption & decryption 
    public static void main(String[] args) throws Exception 
    {

            String plainText = "Sapphire Networks=12-12-2999";
    	  //String plainText = "Default Demo Tenant=31-07-2999";
            String encryptedText = AESDemo.encrypt(plainText);
            System.out.println(encryptedText);
           // String decordedValue1 = String.format("%040x", encryptedText.getBytes());//new String(Base64.decodeBase64(encryptedText));
            String  decordedValue1 =  String.format("%040x", new BigInteger(1, encryptedText.getBytes(/*YOUR_CHARSET?*/)));
            String decryptedText = AESDemo.decrypt(encryptedText);

            System.out.println("Plain Text : " + plainText);
            System.out.println("Encrypted Text : " + decordedValue1);
            System.out.println("Decrypted Text : " + decryptedText);
            String[] strings=decryptedText.split("=");
            SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
            System.out.println(dateFormat.format(new Date()));
            
            Date date=dateFormat.parse(strings[1]);
            System.out.println(Days.daysBetween( new LocalDate(), new LocalDate(date)).getDays());
            System.out.println(date.after(new Date()));
    }
}
