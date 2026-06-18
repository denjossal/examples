package com.denjossal.study.aisdlc.tools;

import java.util.*;
import java.util.regex.*;

/**
 * Prompt template engine for AI-assisted development.
 *
 * Demonstrates key patterns for effective AI prompting:
 * - Structured templates with variable substitution
 * - System/user/assistant message roles
 * - Few-shot examples for consistent output
 * - Output format constraints (JSON schema, markdown)
 */
public class PromptTemplate {

    public enum Role { SYSTEM, USER, ASSISTANT }

    public record Message(Role role, String content) {}

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");

    private final String template;
    private final Map<String, String> variables;

    public PromptTemplate(String template) {
        this.template = template;
        this.variables = new HashMap<>();
    }

    public PromptTemplate set(String key, String value) {
        variables.put(key, value);
        return this;
    }

    public String render() {
        var missing = findMissingVariables();
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Missing variables: " + missing);
        }
        return VARIABLE_PATTERN.matcher(template)
                .replaceAll(match -> Matcher.quoteReplacement(variables.get(match.group(1))));
    }

    public Set<String> findMissingVariables() {
        var required = extractVariables();
        required.removeAll(variables.keySet());
        return required;
    }

    public Set<String> extractVariables() {
        var vars = new LinkedHashSet<String>();
        var matcher = VARIABLE_PATTERN.matcher(template);
        while (matcher.find()) {
            vars.add(matcher.group(1));
        }
        return vars;
    }

    /**
     * Builds a multi-turn prompt with system context, few-shot examples, and user query.
     */
    public static List<Message> buildCodeReviewPrompt(String code, String language, String context) {
        return List.of(
                new Message(Role.SYSTEM, """
                        You are a senior code reviewer. Review the code for:
                        1. Correctness bugs
                        2. Security vulnerabilities (OWASP Top 10)
                        3. Performance issues
                        4. Readability and maintainability

                        Output format: JSON array of findings with severity (critical/high/medium/low),
                        line number, description, and suggested fix.
                        """),
                new Message(Role.USER, """
                        Language: %s
                        Context: %s

                        ```%s
                        %s
                        ```
                        """.formatted(language, context, language, code))
        );
    }

    /**
     * Builds a prompt for generating unit tests from implementation code.
     */
    public static List<Message> buildTestGenerationPrompt(String code, String testFramework) {
        return List.of(
                new Message(Role.SYSTEM, """
                        Generate comprehensive unit tests for the provided code.
                        Use %s. Cover:
                        - Happy path
                        - Edge cases (null, empty, boundary values)
                        - Error conditions

                        Follow the AAA pattern (Arrange, Act, Assert).
                        Use descriptive test method names: should[Expected]When[Condition].
                        """.formatted(testFramework)),
                new Message(Role.USER, code)
        );
    }
}
