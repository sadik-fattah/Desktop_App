typedef struct ASTNode {
    enum { NODE_ASSIGNMENT, NODE_PRINT, NODE_BINOP, NODE_NUMBER, NODE_IDENTIFIER } type;
    union {
        struct { char *var_name; struct ASTNode *expr; } assignment;
        struct { struct ASTNode *expr; } print;
        struct { struct ASTNode *left; Token op; struct ASTNode *right; } binop;
        char *var_name;
        int value;
    };
} ASTNode;

ASTNode* parse_expression();
ASTNode* parse_term();
ASTNode* parse_factor();

ASTNode* parse_assignment() {
    Token var_token = next_token();
    if (var_token.type != TOKEN_IDENTIFIER) {
        printf("Syntax Error: Expected variable name.\n");
        exit(1);
    }

    Token assign_token = next_token();
    if (assign_token.type != TOKEN_ASSIGN) {
        printf("Syntax Error: Expected '='.\n");
        exit(1);
    }

    ASTNode *expr = parse_expression();

    ASTNode *node = malloc(sizeof(ASTNode));
    node->type = NODE_ASSIGNMENT;
    node->assignment.var_name = var_token.value;
    node->assignment.expr = expr;
    return node;
}

ASTNode* parse_print() {
    next_token();  // Skip "print"
    ASTNode *expr = parse_expression();
    
    ASTNode *node = malloc(sizeof(ASTNode));
    node->type = NODE_PRINT;
    node->print.expr = expr;
    return node;
}

ASTNode* parse_expression() {
    ASTNode *left = parse_term();
    Token op = next_token();
    
    if (op.type == TOKEN_PLUS || op.type == TOKEN_MINUS) {
        ASTNode *right = parse_term();
        
        ASTNode *node = malloc(sizeof(ASTNode));
        node->type = NODE_BINOP;
        node->binop.left = left;
        node->binop.op = op;
        node->binop.right = right;
        return node;
    }
    return left;
}

ASTNode* parse_term() {
    ASTNode *left = parse_factor();
    Token op = next_token();
    
    if (op.type == TOKEN_STAR || op.type == TOKEN_SLASH) {
        ASTNode *right = parse_factor();
        
        ASTNode *node = malloc(sizeof(ASTNode));
        node->type = NODE_BINOP;
        node->binop.left = left;
        node->binop.op = op;
        node->binop.right = right;
        return node;
    }
    return left;
}

ASTNode* parse_factor() {
    Token token = next_token();
    if (token.type == TOKEN_NUMBER) {
        ASTNode *node = malloc(sizeof(ASTNode));
        node->type = NODE_NUMBER;
        node->value = atoi(token.value);
        return node;
    }

    if (token.type == TOKEN_IDENTIFIER) {
        ASTNode *node = malloc(sizeof(ASTNode));
        node->type = NODE_IDENTIFIER;
        node->var_name = token.value;
        return node;
    }
    
    printf("Syntax Error: Unexpected token.\n");
    exit(1);
}

ASTNode* parse() {
    Token token = next_token();
    if (token.type == TOKEN_IDENTIFIER) {
        return parse_assignment();
    }
    if (token.type == TOKEN_PRINT) {
        return parse_print();
    }
    printf("Syntax Error: Unexpected token.\n");
    exit(1);
}

