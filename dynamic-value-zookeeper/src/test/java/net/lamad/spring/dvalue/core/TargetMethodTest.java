package net.lamad.spring.dvalue.core;


import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.lang.annotation.Annotation;

public class TargetMethodTest {

    @Test
    public void should_create_proper_path_from_slash_path_annotation() {
        // Given
        DValue dValue = createDValue("/node/subNode");
        TargetMethod targetMethod = new TargetMethod(null, null, dValue);

        // When
        String asFilePath = targetMethod.getAsFilePath();
        String asPropertyPath = targetMethod.getAsPropertyPath();

        // Then
        Assertions.assertThat(asFilePath).isEqualTo("/node/subNode");
        Assertions.assertThat(asPropertyPath).isEqualTo("node.subNode");
    }

    @Test
    public void should_create_proper_path_from_dot_path_annotation() {
        // Given
        DValue dValue = createDValue("node.subNode");
        TargetMethod targetMethod = new TargetMethod(null, null, dValue);

        // When
        String asFilePath = targetMethod.getAsFilePath();
        String asPropertyPath = targetMethod.getAsPropertyPath();

        // Then
        Assertions.assertThat(asFilePath).isEqualTo("/node/subNode");
        Assertions.assertThat(asPropertyPath).isEqualTo("node.subNode");
    }

    private DValue createDValue(final String path) {
        return new DValue() {
            @Override
            public boolean equals(Object obj) {
                return false;
            }

            @Override
            public int hashCode() {
                return 0;
            }

            @Override
            public String toString() {
                return null;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String path() {
                return path;
            }

            @Override
            public String type() {
                return null;
            }

            @Override
            public String charset() {
                return null;
            }
        };
    }
}