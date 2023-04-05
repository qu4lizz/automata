package qu4lizz.automata.util;

public class Utils {
    public static String addNumber(String str, int num) {
        int extracted;
        StringBuilder stringBuilder = new StringBuilder();
        for (var chr : str.toCharArray()) {
            if (Character.isDigit(chr))
                stringBuilder.append(chr);
        }
        extracted = Integer.parseInt(stringBuilder.toString()) + num;
        return "q" + extracted;
    }


}