package com.spaceclub;

import org.junit.jupiter.api.DisplayNameGenerator.Standard;

import java.lang.reflect.Method;

public class SpaceClubCustomDisplayNameGenerator {

    public static class ReplaceUnderscores extends Standard {

        public static final char CHAR_CHANGE_FROM = '_';
        public static final char CHAR_CHANGE_TO = ' ';

        public ReplaceUnderscores() {
            super();
        }

        @Override
        public String generateDisplayNameForClass(Class<?> testClass) {
            return replaceUnderscores(super.generateDisplayNameForClass(testClass));
        }

        @Override
        public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
            return replaceUnderscores(super.generateDisplayNameForNestedClass(nestedClass));
        }

        @Override
        public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
            return replaceUnderscores(testMethod.getName());
        }

        private static String replaceUnderscores(String name) {
            return name.replace(CHAR_CHANGE_FROM, CHAR_CHANGE_TO);
        }

    }

}
