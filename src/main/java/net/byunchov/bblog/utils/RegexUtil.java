package net.byunchov.bblog.utils;

import java.util.regex.Pattern;

public class RegexUtil {
    // public static String EMAIL_REGEX = "((([\\t ]*\\r\\n)?[\\t ]+)?[-!#-\'*+/-9=?A-Z^-~]+(\\.[-!#-\'*+/-9=?A-Z^-~]+)*(([\\t ]*\\r\\n)?[\\t ]+)?|(([\\t ]*\\r\\n)?[\\t ]+)?\"(((([\\t ]*\\r\\n)?[\\t ]+)?([]!#-[^-~]|(\\\\[\\t -~])))+(([\\t ]*\\r\\n)?[\\t ]+)?|(([\\t ]*\\r\\n)?[\\t ]+)?)\"(([\\t ]*\\r\\n)?[\\t ]+)?)@((([\\t ]*\\r\\n)?[\\t ]+)?[-!#-\'*+/-9=?A-Z^-~]+(\\.[-!#-\'*+/-9=?A-Z^-~]+)*(([\\t ]*\\r\\n)?[\\t ]+)?|(([\\t ]*\\r\\n)?[\\t ]+)?\\[((([\\t ]*\\r\\n)?[\\t ]+)?[!-Z^-~])*(([\\t ]*\\r\\n)?[\\t ]+)?](([\\t ]*\\r\\n)?[\\t ]+)?)";
    public static String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public static boolean patternMatches(String content, String regexPattern) {
        return Pattern.compile(regexPattern)
          .matcher(content)
          .matches();
    }

    public static boolean isValidEmailAddress(String email) {
        return patternMatches(email, EMAIL_REGEX);
    }
}
