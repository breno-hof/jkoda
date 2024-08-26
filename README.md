# jKoda
An interpreter of the Koda programming language written in Java

## Scanning
> The first step in any compiler or interpreter is scanning. 
> The scanner takes in raw source code as a series of characters and groups it into a series of chunks we call tokens.
>
> <cite>Robert Nystrom on [Crafting Interpreters](https://www.craftinginterpreters.com/scanning.html)</cite>

### Lexemes and Tokens
> Our job is to scan through the list of characters and group them together into the smallest sequences that still represent something. 
> Each of these blobs of characters is called a <b>lexeme</b>.
> 
> The lexemes are only the raw substrings of the source code.
> However, in the process of grouping character sequences into lexemes, we also stumble upon some other useful information. 
> When we take the lexeme and bundle it together with that other data, the result is a <b>token</b>.
>
> <cite>Robert Nystrom on [Crafting Interpreters](https://www.craftinginterpreters.com/scanning.html)</cite>

## Syntatic grammars
> In the syntactic grammar we’re talking about now, we’re at a different level of granularity.
> Now each “letter” in the alphabet is an entire token and a “string” is a sequence of tokens — an entire expression. 
>
> <cite>Robert Nystrom on [Crafting Interpreters](https://www.craftinginterpreters.com/representing-code.html)</cite>


## Parsing Expressions
>  The only remaining piece is parsing—transmogrifying a sequence of tokens into one of those syntax trees.
>
> <cite>Robert Nystrom on [Crafting Interpreters](https://www.craftinginterpreters.com/parsing-expressions.html)</cite>

### Recursive descent
> Recursive descent is considered a top-down parser because it starts from the top or outermost grammar rule (here expression) and works its way down into the nested subexpressions before finally reaching the leaves of the syntax tree.
>
> <cite>Robert Nystrom on [Crafting Interpreters](https://www.craftinginterpreters.com/parsing-expressions.html#recursive-descent-parsing)</cite>

