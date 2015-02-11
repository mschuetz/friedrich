grammar lua;

program : assignment*;

assignment : name '=' value;

value: number | string | bool | path | map;

SPACE : [ \t\n\r] -> skip;

name : ID;

number : NUMBER;
bool : BOOL;
string : STRING;
path : PATH;

map : '{' pair (',' pair)* '}'
    | '{' '}';
pair : key '=' value;
key : '[' value ']';

BOOL : 'true' | 'false';
STRING :  '\"' (~["])* '\"' ;
PATH : '/' (~[ \t\n\r{},\[\]])* ;
NUMBER : [0-9]+;

ID : [a-zA-Z] [a-zA-Z0-9:]*;