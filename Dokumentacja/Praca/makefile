
.PHONEY :   clear


__start__: latex-word-count
# print number of words and all words with counts in alphabetical order
	cat praca.tex | ./latex-word-count --verbose 
# print only number of words
	cat praca.tex | ./latex-word-count 


latex-word-count: lex.yy.c  
	g++ -std=c++11 lex.yy.c -o latex-word-count

lex.yy.c: lekser.l  
	flex lekser.l 
	
clear : 
	rm latex-word-count *.c *.h   

