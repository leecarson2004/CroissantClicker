package com.croissant.CroissantClicker;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class TextFieldLimit extends PlainDocument {
    private final int limit;

    TextFieldLimit(int limit) {
        super();
        this.limit = limit;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null){
            return;
        }
        //check if str+existing text exceeds char limit
        if ((getLength() + str.length()) <= limit){

            //allow only chars within ascii (65-90 || 97-12 || 48-57) (numerical and alphabetical)
            StringBuilder editableString = new StringBuilder(str);

            for (int i = 0; i < str.length(); i++){
                char currChar = editableString.charAt(i);
                if (!((currChar >= 65 && currChar <= 90)
                        || (currChar >= 97 && currChar <= 122)
                        || (currChar >= 48 && currChar <= 57)
                        || (currChar == 32)))
                {
                    editableString.deleteCharAt(i);
                }
            }

            str = editableString.toString();
            super.insertString(offset, str, attr);
        }
    }
}
