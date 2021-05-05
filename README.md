# FULA-Interpreter

## Übersicht

1. [Einleitung](#einleitung)
2. [Aufbau der Sprache](#aufbau-der-sprache)
3. [Code Beispiel](#code-beispiel)
4. [Grammatiken](#grammatiken)
    1. [Grammatik für das Scannen](#grammatik-für-das-scannen)
    2. [Grammatik für das Parsen](#grammatik-für-das-parsen)

## Einleitung

In diesem Projekt habe ich eine eigene funktionale Programmiersprache _FULA_ entwickelt und einen dazugehörigen Interpreter in Java geschrieben. Dieses Projekt ist im Rahmen einer Facharbeit in der Oberstuffe entstanden, wurde jedoch auch noch darüber hinaus weiter bearbeitet.

Da das Projekt im Rahmen einer Facharbeit in der Oberstuffe entstanden ist, waren gewisse vorgaben gegeben, wie z.B. dass ich für Listen die Abiturklassen des Landes NRW anstatt von den Java Listen nutzen musste. Außerdem musste ich Datenstrukturen, wie eine Map selbst implementieren.

Das Ziel dieses Projektes war nicht eine Programmiersprache zu entwickeln, die einen praktischen nutzen hat. Wäre dies der fall gewesen, hätte ich den Interpreter wahrscheinlich auch nicht in Java schreiben geschrieben, da die Effizienz limitiert ist im Vergleich zu Sprachen wie C++. Vielmehr wollte ich durch das entwickeln der Sprache einen tiefen Einblick in die Funktionsweise von Programmiersprachen und deren Interpretern und Compilern gewinnen, was ich definitiv erreicht habe. 

## Aufbau der Sprache

Die Sprache basiert auf der Implementation des Lambda-Kalküls. Somit ist diese Programmiersprache funktional. So wie ich den In- und Output in die Sprache integriert habe ist diese nicht ohne Seiteneffekt. Außerhalb des In- und Outputs kommen allerdings keine Seiteneffekte zur Stande und die Sprache ist rein funktional.

## Code Beispiel

Im folgenden ist ein Programm zur Berechnung der Primzahlen von 0 bis 10 gezeigt. Dies soll die Möglichkeit geben ohne viel Aufwand den Grundaufbau der Sprache zu erkennen.

```
let
    # isN gibt true aus wenn x eine Natürliche Zahl ist
    isN := {x ->
            x%1 == 0 && x > 1
        }

    # sub gibt true zurück wenn sich y durch keine ganze Zahl kleiner oder gleich x Teilen lässt
    sub := {x, y ->
            x<=1 || y%x != 0 && sub(x-1, y)
        }

    # isPrimeNumber gibt true zurück, wenn x eine Primzahl ist
    isPrimeNumber := {x ->
            isN(x) && sub(x-1, x)
        }

    # printPrimes gibt alle Primzahlen von current bis max aus
    printPrimes := {current, max ->
            if current <= max
                printPrimes(
                    if isPrimeNumber(current)
                        print current + 1
                    else
                        current + 1
                    , max
                )
            else
                0
        }
in
    printPrimes(0, 10)
end
```

## Grammatiken

Bevor ich mit der Entwicklung des Interpreters begann, habe ich die Programmiersprache selbst entwickelt und dazu eine Grammatik für das Scannen der Token und anschließend eine Grammatik für das parsen dieser Token entwickelt. Im folgenden sind die entstandenen Grammatiken aufgelistet.

### Grammatik für das Scannen

```
Terminal =
    {a, b, c, d, e, f, g, h, i, j; k, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z}
    ∪ {A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z}
    ∪ {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0}
    ∪ {., ,, {, }, <, >, =, !, (, ), +, -, *, /, %, ^}

Non-Terminal =
    {Token, Bool, Identifier, Float, Z, K, LowerChar, UpperChar, Number}

Startsymbol ist Token

Token ::=
    Bool|Identifier|Float|let|in|where|if|else| input|print|sin|cos|tan|exp|log|sqrt|.| ,|{|}|<|>|=|!|(|)|+|-|*|/|%|^}

Bool ::= true|false

Identifier ::= LowerChar [[LowerChar] [UpperChar] [Number]]*

Float ::= Z|PI|E

Z ::= Number [Z]|.K

K ::= Number [K]

LowerChar ::= a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z

UpperChar ::= A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z

Number ::= 0|1|2|3|4|5|6|7|8|9
```

### Grammatik für das Parsen

```
Terminal =
    {let, in, where, if, else, input, print, sin, cos, tan, exp, log, sqrt}
    ∪ {., ,, {, }, <, >, =, !, (, ), +, -, *, /, %, ^}
    ∪ {Float, Identifier, Bool}

Non-Terminal =
    {Expression, LetExp, IfExp, FunExp, AppExp, Assignment, Term, OrExp, XOrExp, AndExp, EquExp, ComExp, AddExp, MulExp, PowerExp, ValExp}

Startsymbol ist Expression

Expression ::= LetExp
    | IfExp
    | FunExp
    | Term

LetExp ::=
    let
        Assignment
    in
        Expression
    end

IfExp ::=
    if Expression
        Expression
    else
        Expression

FunExp ::= { Identifier [, Identifier]* -> Expression } [AppExp]

Assignment ::= Identifier := Expression [Assignment]

AppExp ::= (EXP [, EXP]*) [AppExp]

Term ::= OrExp [where Assignment]

OrExp ::= AndExp [|| OrExp]

AndExp ::= XOrExp [&& AndExp]


XOrExp ::= EquExp [~ XOr]

EquExp ::= ComExp [!= EquExp]
    |ComExp [== EquExp]

ComExp ::= AddExp [< ComExp]
    |AddExp [<= ComExp]
    |AddExp [> ComExp]
    |AddExp [>= ComExp]

AddExp ::= MulExp [+ AddExp]
    |MulExp [- AddExp]

MulExp ::= PowExp [* MulExp]
    |PowExp [/ MulExp]
    |PowExp [% MulExp]

PowExp ::= ValExp [^ PowExp]

ValExp ::= ( Expression )
    |- ValExp
    |! ValExp
    |sin ValExp| cos ValExp| tan ValExp
    |sqrt ValExp| log ValExp| exp ValExp
    |print ValExp| input
    |Bool
    |Float
    |Identifier [AppExp]
```
