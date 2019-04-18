package com.example.cst2335_finalproject;

import java.util.LinkedList;

public class MWDictWord {
    String word, syllables, pronunciation, wordType;
    LinkedList<String> definitionList;

    public MWDictWord() {
        definitionList = new LinkedList<String>();
    }

    public MWDictWord(String newWord, String newSyllables, String newPronunciation,
                      String newWordType, LinkedList<String> newDefinitionList) {
        word = newWord;
        syllables = newSyllables;
        pronunciation = newPronunciation;
        wordType = newWordType;
        definitionList = newDefinitionList;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setSyllables(String syllables) {
        this.syllables = syllables;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public void setWordType(String wordType) {
        this.wordType = wordType;
    }

    public void setDefinitionList(LinkedList<String> definitionList) {
        this.definitionList = definitionList;
    }

    public String getWord() {
        return word;
    }

    public String getSyllables() {
        return syllables;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public String getWordType() {
        return wordType;
    }

    public LinkedList<String> getDefinitionList() {
        return definitionList;
    }
}
