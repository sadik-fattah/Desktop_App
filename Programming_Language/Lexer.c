#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

typedef enum {
    TOKEN_NUMBER,
    TOKEN_IDENTIFIER,
    TOKEN_ASSIGN,
    TOKEN_PRINT,
    TOKEN_PLUS,
    TOKEN_MINUS,
    TOKEN_STAR,
    TOKEN_SLASH,
    TOKEN_SEMICOLON,
    TOKEN_END,
    TOKEN_INVALID
} TokenType;

typedef struct {
    TokenType type;
    char *value;
} Token;

char *input;  // Pointer to the input string
size_t position = 0;

Token create_token(TokenType type, char *value) {
    Token token;
    token.type = type;
    token.value = value;
    return token;
}

void skip_whitespace() {
    while (input[position] != '\0' && isspace(input[position])) {
        position++;
    }
}

Token next_token() {
    skip_whitespace();

    if (input[position] == '\0') {
        return create_token(TOKEN_END, NULL);
    }

    if (isdigit(input[position])) {
        // Number token
        size_t start = position;
        while (isdigit(input[position])) position++;
        size_t len = position - start;
        char *num_str = strndup(input + start, len);
        return create_token(TOKEN_NUMBER, num_str);
    }

    if (isalpha(input[position]) || input[position] == '_') {
        // Identifier (variable name or keyword)
        size_t start = position;
        while (isalnum(input[position]) || input[position] == '_') position++;
        size_t len = position - start;
        char *id_str = strndup(input + start, len);
        if (strcmp(id_str, "print") == 0) {
            return create_token(TOKEN_PRINT, id_str);
        } else {
            return create_token(TOKEN_IDENTIFIER, id_str);
        }
    }

    if (input[position] == '=') {
        position++;
        return create_token(TOKEN_ASSIGN, "=");
    }

    if (input[position] == '+') {
        position++;
        return create_token(TOKEN_PLUS, "+");
    }

    if (input[position] == '-') {
        position++;
        return create_token(TOKEN_MINUS, "-");
    }

    if (input[position] == '*') {
        position++;
        return create_token(TOKEN_STAR, "*");
    }

    if (input[position] == '/') {
        position++;
        return create_token(TOKEN_SLASH, "/");
    }

    if (input[position] == ';') {
        position++;
        return create_token(TOKEN_SEMICOLON, ";");
    }

    return create_token(TOKEN_INVALID, NULL);
}

void print_token(Token token) {
    switch (token.type) {
        case TOKEN_NUMBER: printf("NUMBER(%s)\n", token.value); break;
        case TOKEN_IDENTIFIER: printf("IDENTIFIER(%s)\n", token.value); break;
        case TOKEN_ASSIGN: printf("ASSIGN\n"); break;
        case TOKEN_PRINT: printf("PRINT\n"); break;
        case TOKEN_PLUS: printf("PLUS\n"); break;
        case TOKEN_MINUS: printf("MINUS\n"); break;
        case TOKEN_STAR: printf("STAR\n"); break;
        case TOKEN_SLASH: printf("SLASH\n"); break;
        case TOKEN_SEMICOLON: printf("SEMICOLON\n"); break;
        case TOKEN_END: printf("END\n"); break;
        case TOKEN_INVALID: printf("INVALID\n"); break;
    }
}

