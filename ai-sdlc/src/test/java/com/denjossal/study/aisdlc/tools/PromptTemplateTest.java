package com.denjossal.study.aisdlc.tools;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PromptTemplateTest {

    @Test
    void shouldRenderTemplate() {
        var template = new PromptTemplate("Hello {{name}}, welcome to {{project}}!");
        var result = template.set("name", "Dennis").set("project", "AI-SDLC").render();

        assertThat(result).isEqualTo("Hello Dennis, welcome to AI-SDLC!");
    }

    @Test
    void shouldExtractVariables() {
        var template = new PromptTemplate("{{language}} code in {{file}} at line {{line}}");
        assertThat(template.extractVariables()).containsExactly("language", "file", "line");
    }

    @Test
    void shouldDetectMissingVariables() {
        var template = new PromptTemplate("{{name}} wrote {{code}}");
        template.set("name", "Alice");

        assertThat(template.findMissingVariables()).containsExactly("code");
    }

    @Test
    void shouldThrowOnMissingVariables() {
        var template = new PromptTemplate("Hello {{name}}");

        assertThatThrownBy(template::render)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Missing variables");
    }

    @Test
    void shouldBuildCodeReviewPrompt() {
        var messages = PromptTemplate.buildCodeReviewPrompt(
                "public void foo() {}", "java", "production service"
        );

        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).role()).isEqualTo(PromptTemplate.Role.SYSTEM);
        assertThat(messages.get(1).content()).contains("java");
    }

    @Test
    void shouldBuildTestGenerationPrompt() {
        var messages = PromptTemplate.buildTestGenerationPrompt("class Foo {}", "JUnit 5");

        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).content()).contains("JUnit 5");
        assertThat(messages.get(0).content()).contains("Edge cases");
    }
}
