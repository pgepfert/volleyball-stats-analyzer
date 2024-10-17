
 


%option noyywrap

%{
   #include <stdlib.h> 
   #include <iostream>
   #include <iomanip>
   #include <string> 
   #include <map>
   #include <stack>
	
	#define debug(x)  std::cout << "(" << __LINE__ << ") " << #x << " == " << (x) << std::endl;
	
   int depth = 0;
   
   
   struct info
	{
	   std::string word;
	   std::size_t strlen, unicode_strlen;
	   std::size_t counter;
	};
   
   std::map<std::string, info> slowa;
	std::stack<YY_BUFFER_STATE> bufory;
	
	std::size_t unicode_strlen (const std::string & s)
	{
	    std::size_t size = 0;
	    for (const auto & c : s)
	       size += ((c & 0xc0) != 0x80);
       return size;
	}
	
	
	
%}


%x BODY ENV_IN_BODY MATH INPUT 

 
%%

"\\begin{document}"     { BEGIN(BODY);    }

<BODY>"\\end{document}"       { BEGIN(INITIAL); }
<BODY>"%".*         

<BODY,ENV_IN_BODY>"\\begin{"(table|figure|comment|verbatim|lstlisting)[*]?"}"    {  BEGIN(ENV_IN_BODY); depth++; }
<ENV_IN_BODY>"\\end{"(table|figure|comment|verbatim|lstlisting)[*]?"}"    { 
                              depth--; 
                              if (depth > 0) 
                                 BEGIN(ENV_IN_BODY);
                              else
                              BEGIN(BODY); 
                          }
<BODY>"$$"                { BEGIN(MATH); }
<BODY>"$"                 { BEGIN(MATH); }

<MATH>"$$"                { BEGIN(BODY); }
<MATH>"$"                 { BEGIN(BODY); }


<BODY>"\\input{"          {  BEGIN(INPUT);}

<INPUT>[^}]+              {  
                             bufory.push(YY_CURRENT_BUFFER);
                             yyin = fopen( yytext, "r" );
                             
                             yy_switch_to_buffer(yy_create_buffer(yyin, YY_BUF_SIZE ));
                          
                             BEGIN(BODY);   
                          }
<BODY><<EOF>>             {   
                              yy_delete_buffer( YY_CURRENT_BUFFER );
                              yy_switch_to_buffer(bufory.top());
                              bufory.pop();
                          }

<BODY>"\\begin{"[a-zA-Z0-9*]+"}"                          
<BODY>"\\end{"[a-zA-Z0-9*]+"}"                          
<BODY>"\\cite{"[^}]+"}"
<BODY>"\\"(eq)?ref"{"[^}]+"}"
<BODY>"\\label{"[^}]+"}"
<BODY>"\\lstinline|"[^|]*"|" 
<BODY>"\\lstinline!"[^!]*"!" 
<BODY>"\\"[a-zA-Z]+             
<BODY>"\\-"
<BODY>[^ \t\r\n~(){}.,':$/0-9!?"\\\[\]=\*]+      { slowa[std::string(yytext)].counter++; }

                                        
<ENV_IN_BODY,MATH,INITIAL,BODY>.
<ENV_IN_BODY,MATH,INITIAL,BODY>\r?\n

%%


int main (int ile, char ** param)
{
   const char * szVERBOSE = "--verbose";
   const char * szHELP    = "--help";
   
   const int GWIAZDKI = 25; // maksymalna liczba gwiazdek

   if (ile > 1 and not strcmp(param[1], szHELP))
   {
      std::cout << "Word count for LaTeX documents" << std::endl;
      std::cout << "usage:" << std::endl;
      std::cout << "The program reads from the standard input and writes a number of words to the standard output." << std::endl;
      std::cout << "  --help              prints this help" << std::endl;
      std::cout << "  --verbose           prints number of words and all words (with counts) in alphabetical order." << std::endl;

      return 0;
   }

   yylex();
   
   int liczba = 0; 
   for (auto s : slowa)
   {
       liczba += s.second.counter;
   } 
 
   if (ile > 1 and not strcmp (param[1], szVERBOSE))
   {
      std::cout << "liczba slow: " << liczba << std::endl << std::endl; 
   
      // najpierw sprawdzam najdluzsze slowo i najwyzsza liczbe:
      int max_liczba = 0;
      int max_dlugosc = 0;
      
      for (auto & s : slowa)
      {
          // tylko po to, zeby bylo ladne formatowanie w konsoli:
          s.second.strlen = s.first.length();
          s.second.unicode_strlen = unicode_strlen(s.first);
          
          auto len = s.second.unicode_strlen; 
          if (max_dlugosc < len)
             max_dlugosc = len;
             
          if (max_liczba < s.second.counter)
             max_liczba = s.second.counter;
      }
      // wyznaczam dlugosc najdluzszej liczby:
      auto max_len = std::to_string(max_liczba).length();
      
      
      // i w koncu wypisuje:
      for (auto s : slowa)
        std::cout << std::left 
                  << std::setw(max_dlugosc + s.second.strlen - s.second.unicode_strlen) 
                  << s.first << "\t" 
                  << std::setw(max_len) << std::right << s.second.counter 
                  << "\t"
                  << std::setfill ('*') << std::setw(s.second.counter * GWIAZDKI / max_liczba)
                  << ""
                  << std::setfill (' ')
                  << std::endl;
   }
   else
      std::cout << liczba << std::endl;    
   return 0;
}

