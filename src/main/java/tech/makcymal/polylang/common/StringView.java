package tech.makcymal.polylang.common;

import java.util.function.Predicate;

import static tech.makcymal.polylang.common.CommonUtils.findFirst;
import static tech.makcymal.polylang.common.CommonUtils.findNext;

public class StringView {

    private static final Predicate<Character> PASS_ALL_FILTER = c -> true;

    private final String str;
    private Predicate<Character> filter = PASS_ALL_FILTER;

    private StringView(String str) {
        this.str = str;
    }

    public static StringView of(String str) {
        return new StringView(str);
    }

    public StringView passAlphaNumeric() {
        if (filter == PASS_ALL_FILTER) {
            filter = c -> Character.isAlphabetic(c) || Character.isDigit(c);
        } else {
            filter = c -> filter.test(c) && (Character.isAlphabetic(c) || Character.isDigit(c));
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        StringView sv;
        if (o instanceof String) {
            sv = new StringView((String) o);
        } else if (o instanceof StringView) {
            sv = (StringView) o;
        } else {
            return false;
        }

        int i = findFirst(str, filter);
        int j = findFirst(sv.str, sv.filter);
        while (0 <= i && i < str.length() && 0 <= j && j < sv.str.length()) {
            if (Character.toLowerCase(str.charAt(i)) != Character.toLowerCase(sv.str.charAt(j))) {
                return false;
            }
            i = findNext(str, filter, i);
            j = findNext(sv.str, sv.filter, j);
        }

        return i == -1 && j == -1;
    }

}
