package com.kingofnothing.mylectures;

public class StringFormatter {
    public static String CapFirstLetter(String word){
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
    public static String TrimEnd(String text){
        return text.substring(0,(text.length()- 1));
    }
    public static String TrimFirst(String text)
    {
        return text.substring(1);
    }
    public static char GetLast(String text){
        return text.charAt(text.length() - 1);
    }
}
