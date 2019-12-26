package cz.neumimto.rpg.common.utils;

/*
 * CODE TAKEN FROM ApacheUtils
 * Removed not needed lines (previously 9k long class)
 *
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class StringUtils {

    public static final int INDEX_NOT_FOUND = -1;

    private StringUtils() {
        super();
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static String replace(final String text, final String searchString, final String replacement) {
        return replace(text, searchString, replacement, -1);
    }

    public static String replace(final String text, final String searchString, final String replacement, final int max) {
        return replace(text, searchString, replacement, max, false);
    }

    private static String replace(final String text, String searchString, final String replacement, int max, final boolean ignoreCase) {
        if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
            return text;
        }
        String searchText = text;
        if (ignoreCase) {
            searchText = text.toLowerCase();
            searchString = searchString.toLowerCase();
        }
        int start = 0;
        int end = searchText.indexOf(searchString, start);
        if (end == INDEX_NOT_FOUND) {
            return text;
        }
        final int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = increase < 0 ? 0 : increase;
        increase *= max < 0 ? 16 : max > 64 ? 64 : max;
        final StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != INDEX_NOT_FOUND) {
            buf.append(text, start, end).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = searchText.indexOf(searchString, start);
        }
        buf.append(text, start, text.length());
        return buf.toString();
    }


    public static String replaceEach(final String text, final String[] searchList, final String[] replacementList) {
        return replaceEach(text, searchList, replacementList, false, 0);
    }

    private static String replaceEach(
            final String text, final String[] searchList, final String[] replacementList, final boolean repeat, final int timeToLive) {

        // mchyzer Performance note: This creates very few new objects (one major goal)
        // let me know if there are performance requests, we can create a harness to measure

        if (text == null || text.isEmpty() || searchList == null ||
                searchList.length == 0 || replacementList == null || replacementList.length == 0) {
            return text;
        }

        // if recursing, this shouldn't be less than 0
        if (timeToLive < 0) {
            throw new IllegalStateException("Aborting to protect against StackOverflowError - " +
                    "output of one loop is the input of another");
        }

        final int searchLength = searchList.length;
        final int replacementLength = replacementList.length;

        // make sure lengths are ok, these need to be equal
        if (searchLength != replacementLength) {
            throw new IllegalArgumentException("Search and Replace array lengths don't match: "
                    + searchLength
                    + " vs "
                    + replacementLength);
        }

        // keep track of which still have matches
        final boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];

        // index on index that the match was found
        int textIndex = -1;
        int replaceIndex = -1;
        int tempIndex = -1;

        // index of replace array that will replace the search string found
        // NOTE: logic duplicated below START
        for (int i = 0; i < searchLength; i++) {
            if (noMoreMatchesForReplIndex[i] || searchList[i] == null ||
                    searchList[i].isEmpty() || replacementList[i] == null) {
                continue;
            }
            tempIndex = text.indexOf(searchList[i]);

            // see if we need to keep searching for this
            if (tempIndex == -1) {
                noMoreMatchesForReplIndex[i] = true;
            } else {
                if (textIndex == -1 || tempIndex < textIndex) {
                    textIndex = tempIndex;
                    replaceIndex = i;
                }
            }
        }
        // NOTE: logic mostly below END

        // no search strings found, we are done
        if (textIndex == -1) {
            return text;
        }

        int start = 0;

        // get a good guess on the size of the result buffer so it doesn't have to double if it goes over a bit
        int increase = 0;

        // count the replacement text elements that are larger than their corresponding text being replaced
        for (int i = 0; i < searchList.length; i++) {
            if (searchList[i] == null || replacementList[i] == null) {
                continue;
            }
            final int greater = replacementList[i].length() - searchList[i].length();
            if (greater > 0) {
                increase += 3 * greater; // assume 3 matches
            }
        }
        // have upper-bound at 20% increase, then let Java take over
        increase = Math.min(increase, text.length() / 5);

        final StringBuilder buf = new StringBuilder(text.length() + increase);

        while (textIndex != -1) {

            for (int i = start; i < textIndex; i++) {
                buf.append(text.charAt(i));
            }
            buf.append(replacementList[replaceIndex]);

            start = textIndex + searchList[replaceIndex].length();

            textIndex = -1;
            replaceIndex = -1;
            tempIndex = -1;
            // find the next earliest match
            // NOTE: logic mostly duplicated above START
            for (int i = 0; i < searchLength; i++) {
                if (noMoreMatchesForReplIndex[i] || searchList[i] == null ||
                        searchList[i].isEmpty() || replacementList[i] == null) {
                    continue;
                }
                tempIndex = text.indexOf(searchList[i], start);

                // see if we need to keep searching for this
                if (tempIndex == -1) {
                    noMoreMatchesForReplIndex[i] = true;
                } else {
                    if (textIndex == -1 || tempIndex < textIndex) {
                        textIndex = tempIndex;
                        replaceIndex = i;
                    }
                }
            }
            // NOTE: logic duplicated above END

        }
        final int textLength = text.length();
        for (int i = start; i < textLength; i++) {
            buf.append(text.charAt(i));
        }
        final String result = buf.toString();
        if (!repeat) {
            return result;
        }

        return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
    }

    public static String capitalizeFirst(String str) {
        return (Character.toUpperCase(str.charAt(0)) + str.substring(1)).replaceAll("_", " ");
    }
}

