package com.kelvin.kelvinTestProjectMaven.regularExpressionTest;
import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kelvin.kelvinTestProjectMaven.standardizePhoneTest.IDMPhoneNumberUtil;
import junit.framework.Assert;

import org.junit.Test;

public class regExpTest {
	
	@Test
	public void testStringMatches(){
		String str = "11";
		System.out.println(str.matches("\\d{1}"));

		str = "123abcasdfadsf";
		
		String regx = "a(bc).*";
		Pattern p = Pattern.compile(regx);
		//str.matches(regExp) return false because the regExp not match all string, only match a part
		assertEquals(false, str.matches(regx));
		assertEquals(1,p.matcher(str).groupCount());

		regx = "abc";
		p = Pattern.compile(regx);				
		assertEquals(false, Pattern.matches(regx ,str));
		assertEquals(false, str.matches(regx));
		assertEquals(0,p.matcher(str).groupCount());
	}
	
	@Test
	public void getMatchedPart(){
		String str = "2018-01-29 21:48:21,365 : <ns:TCRMAdminContEquivBObj><ns:AdminPartyId>8501e85cdbd1aac</ns:AdminPartyId><ns:AdminSystemType>100007</ns:AdminSystemType><ns:TCRMExtension><ns:XAdminContEquivBObjExt><ns:XStatusType>100007</ns:XStatusType></ns:XAdminContEquivBObjExt></ns:TCRMExtension></ns:TCRMAdminContEquivBObj";

		//1. create Regular Expression object: Pattern object
		String regExp = "AdminPartyId>([^<]*)";
		Pattern p = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);

		//2. get Matcher object. Matcher will find the matched parts(group)
		Matcher m = p.matcher(str);
		//str.matches(regExp) return false because the regExp not match all string, only match a part: "AdminPartyId>8501e85cdbd1aac"
		assertEquals(false, str.matches(regExp));

		//3. get matched groups:
		//the group info only can be retrieved after call Matcher.find()
		if(m.find()){
			System.out.println("Whole string: " + str);
			System.out.println("First group(entire match): " + m.group(0));
			System.out.println("Second group(captured by '()'): " + m.group(1));
		}
	}
	
	@Test
	public void getAllMatchedPart(){
		String phoneNumber = "+1(630)-123-1234";
		System.out.println("original string " + phoneNumber);

		//Capture all number to group
		//by calling m.find() in loop
		Pattern p = Pattern.compile("(\\d+)");
		Matcher m = p.matcher(phoneNumber);

		int i = 0;
		while(m.find()){
			i++;
			int totalGroupCount = m.groupCount();
			System.out.println("Match " + i + "th: "
					+ "group count:" + totalGroupCount
					+ " result: " + m.group(0));
		}
	}
	
	@Test
	public void trimLeadingString(){
		String str = "000001233";
		str = str.replaceAll("^0+", "");
		Assert.assertEquals("1233", str);
	}
	
	@Test
	public void twoPhoneNumberMatch(){
		Assert.assertTrue(isTwoPhoneNumberMatch("(630)-123-1234","6301231234"));
		Assert.assertTrue(isTwoPhoneNumberMatch("630-123-1234","6301231234"));
		Assert.assertTrue(isTwoPhoneNumberMatch("+1-630-123-1234","0016301231234"));
		Assert.assertTrue(isTwoPhoneNumberMatch("+0708243036 00000-000000","+0708243036   00000-000000"));
	}
	
	@Test
	public void stripSpecialChars(){
		String str = "Stre # $ % ^ &et123~ ! @ * ( ) _ + ` - = [ ] \\ { } | ; ' , . / : \" < > ?";
		System.out.println(str);
		str = str.replaceAll("~|!|@|#|\\$|%|\\^|\\&|\\*|\\(|\\)|_|\\+|`|-|=|\\[|\\]|\\\\|\\{|\\}|\\||;|'|,|\\.|/|:|\"|<|>|\\?|\\s", "");
		System.out.println(str);
	}

	private String extractAllNumbers(String phoneNumber) {
//		Pattern pattern = Pattern.compile("\\d");
//		Matcher matches = pattern.matcher(phoneNumber);
//		if(matches.find()){
//			System.out.println("groups are:" + matches.group());
//		}

		//replace first + to "00"
		phoneNumber = phoneNumber.replaceAll("^\\+", "00");
		
		//replace all NON-Number characters.
		phoneNumber = phoneNumber.replaceAll("[^\\d]", "");
		return phoneNumber;
	}
	
	public static boolean isTwoPhoneNumberMatch(String phoneNumber1, String phoneNumber2) {
		boolean result = false;
		//first extract all number.
		phoneNumber1 = phoneNumber1.replaceAll("[^\\d]", "");
		phoneNumber2 = phoneNumber2.replaceAll("[^\\d]", "");
		
		//then trim the leading Zero
		phoneNumber1 = phoneNumber1.replaceAll("^0+", "");
		phoneNumber2 = phoneNumber2.replaceAll("^0+", "");
		result = phoneNumber1.equalsIgnoreCase(phoneNumber2);
		return result;
	}
	
	/*
	 * for incident 1728829. those characters needed to stripped out when comparing phone number:
	 * special characters: # ( ) * & ^ - + =
	 * extension mark:  extension, extn, ext, ex
	 */
	@Test
	public void removeSpecialCharacterForPhoneNumber(){
		String phoneNumber = "(630)123-80913413144!@#$%&*()_+`-=[]\\{};',./:\"<>?";
		phoneNumber = phoneNumber.replaceAll("\\s+|\\+|\\(|\\)|\\*|\\&|\\^|-|=|#|%|extension|extn|ext|ex|!|@|\\$|_|`|\\[|\\]|\\\\|\\{|\\}|;|'|,|\\.|\\/|\\:|\"|<|>|\\?", "");
		System.out.println(phoneNumber);
		
	}
	
	@Test
	public void extractPhoneNumberExtension(){
		//String[] ExtStrArray = new String[]{"extension","extn","ext","ex","x","#"};
		System.out.println("01088126666EXT 1234" + ":" + IDMPhoneNumberUtil.getPhoneNumberExtension("01088126666EXT 1234"));
//		System.out.println("01088126666extension1234" + ":" + IDMPhoneNumberUtil.getPhoneNumberExtension("01088126666extension1234"));
//		System.out.println("01088126666extn1234" + ":" + IDMPhoneNumberUtil.getPhoneNumberExtension("01088126666extn1234"));
//		System.out.println("01088126666ex1234" + ":" + IDMPhoneNumberUtil.getPhoneNumberExtension("01088126666ex1234"));
//		System.out.println("01088126666extn1234" + ":" + IDMPhoneNumberUtil.getPhoneNumberExtension("01088126666extn1234"));
//		System.out.println("01088126666#1234" + ":" + IDMPhoneNumberUtil.getPhoneNumberExtension("01088126666#1234"));
	}
}
