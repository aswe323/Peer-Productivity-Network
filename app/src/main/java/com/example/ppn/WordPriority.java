package com.example.ppn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.BiFunction;

/***
 * A static class ment to hold words. both bucketwords and priority words.
 *
 */
 public class WordPriority {


    private Map<String,Integer> priorityWords;
    private Map<String,TimePack> bucketWords;
    private static WordPriority instance;
    private static boolean created = false;


    private WordPriority(Map<String, Integer> priorityWords, Map<String, TimePack> bucketWords) {
        this.priorityWords = priorityWords;
        this.bucketWords = bucketWords;
    }

    public static WordPriority getInstance(Map<String, Integer> priorityWords, Map<String, TimePack> bucketWords) {
        if(!created) instance = new WordPriority(priorityWords,bucketWords);
        created = true;
        return instance;
    }

    //region priorityWords c.r.u.d
    public int priorityWordsSize() {
        return priorityWords.size();
    }

    public boolean isPriorityWordsEmpty() {
        return priorityWords.isEmpty();
    }

    public boolean isPriorityWordsContainsKey(@Nullable @org.jetbrains.annotations.Nullable Object key) {
        return priorityWords.containsKey(key);
    }

    public boolean isPriorityWordsContainsValue(@Nullable @org.jetbrains.annotations.Nullable Object value) {
        return priorityWords.containsValue(value);
    }

    public Integer getFromPriorityWords(@Nullable @org.jetbrains.annotations.Nullable Object key) {
        return priorityWords.get(key);
    }

    public Integer putInPriorityWords(String key, Integer value) {
        return priorityWords.put(key, value);
    }

    public void putAllInPriorityWords(@NonNull @org.jetbrains.annotations.NotNull Map<? extends String, ? extends Integer> m) {
        priorityWords.putAll(m);
    }

    public boolean replaceInPriorityWords(String key, @Nullable @org.jetbrains.annotations.Nullable Integer oldValue, Integer newValue) {
        return priorityWords.replace(key, oldValue, newValue);
    }

    public Integer replaceInPriorityWords(String key, Integer value) {
        return priorityWords.replace(key, value);
    }

    public Integer mergePriorityWords(String key, @NonNull @org.jetbrains.annotations.NotNull Integer value, @NonNull @org.jetbrains.annotations.NotNull BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        return priorityWords.merge(key, value, remappingFunction);
    }
    //endregion

    //region BucketWords c.r.u.d
    public int sizeBucketWords() {
        return bucketWords.size();
    }

    public boolean isBucketWordsEmpty() {
        return bucketWords.isEmpty();
    }

    public boolean IsBucketWordscontainsKey(@Nullable @org.jetbrains.annotations.Nullable Object key) {
        return bucketWords.containsKey(key);
    }

    public boolean IsBucketWordscontainsValue(@Nullable @org.jetbrains.annotations.Nullable Object value) {
        return bucketWords.containsValue(value);
    }

    public TimePack getFromBucketWords(@Nullable @org.jetbrains.annotations.Nullable Object key) {
        return bucketWords.get(key);
    }

    public TimePack putInBucketWords(String key, TimePack value) {
        return bucketWords.put(key, value);
    }

    public TimePack removeFromBucketWords(@Nullable @org.jetbrains.annotations.Nullable Object key) {
        return bucketWords.remove(key);
    }

    public void putAllInBucketWords(@NonNull @NotNull Map<? extends String, ? extends TimePack> m) {
        bucketWords.putAll(m);
    }

    public void replaceAllInBucketWords(@NonNull @NotNull BiFunction<? super String, ? super TimePack, ? extends TimePack> function) {
        bucketWords.replaceAll(function);
    }

    public boolean removeFromBucketWords(@Nullable @org.jetbrains.annotations.Nullable Object key, @Nullable @org.jetbrains.annotations.Nullable Object value) {
        return bucketWords.remove(key, value);
    }

    public boolean replaceInBucketWords(String key, @Nullable @org.jetbrains.annotations.Nullable TimePack oldValue, TimePack newValue) {
        return bucketWords.replace(key, oldValue, newValue);
    }

    public TimePack replaceInBucketWords(String key, TimePack value) {
        return bucketWords.replace(key, value);
    }
    //endregion


}
