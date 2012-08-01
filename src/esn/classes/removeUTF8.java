package esn.classes;

import java.util.Arrays;

public class removeUTF8 {

        private static char[] SPECIAL_CHARACTERS = { ' '};

        private static char[] REPLACEMENTS = { '_', '\0', '\0', '\0', '\0', '\0',
                        '\0', '_', '\0', '_', '\0', '\0', '\0', '\0', '\0', '\0', '_',
                        '\0', '\0', '\0', '\0', '\0', 'A', 'A', 'A', 'A', 'E', 'E', 'E',
                        'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a', 'a', 'a',
                        'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u', 'y', 'A',
                        'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u', 'A', 'a',
                        'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
                        'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e', 'E', 'e',
                        'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'I',
                        'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
                        'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
                        'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
                        'U', 'u', };

        public static String execute(String s) {
                int maxLength = Math.min(s.length(), 236);
                char[] buffer = new char[maxLength];
                int n = 0;
                for (int i = 0; i < maxLength; i++) {
                        char ch = s.charAt(i);
                        buffer[n] = removeAccent(ch);
                        // skip not printable characters
                        if (buffer[n] > 31) {
                                n++;
                        }
                }
                // skip trailing slashes
                while (n > 0 && buffer[n - 1] == '/') {
                        n--;
                }
                return String.valueOf(buffer, 0, n);
        }

        public static char removeAccent(char ch) {
                int index = Arrays.binarySearch(SPECIAL_CHARACTERS, ch);
                if (index >= 0) {
                        ch = REPLACEMENTS[index];
                }
                return ch;
        }
        
        public static String removeAccent(String s) {
                StringBuilder sb = new StringBuilder(s);
                for (int i = 0; i < sb.length(); i++) {
                        sb.setCharAt(i, removeAccent(sb.charAt(i)));
                }
                return sb.toString();
        }
        
}
