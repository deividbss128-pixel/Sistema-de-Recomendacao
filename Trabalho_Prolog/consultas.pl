% REGRAS:
:- [database].
:- dynamic filme/5, avaliacao/3.


% Fase 1: Consultas Básicas (Aquecimento)

% Listar títulos de um gênero:
filmes_por_genero(GeneroRef, Lista) :-
	findall(Titulo,
		(filme(_, Titulo, GeneroFilme, _, _),
			filme_tem_genero(GeneroFilme, GeneroRef)),
		Lista).

% Listar títulos lançados estritamente após um ano:
filmes_de_ano_superior(AnoRef, Lista) :-
	findall(Titulo,
		(filme(_, Titulo, _, _, AnoFilme),
			ano_superior(AnoRef, AnoFilme)),
		Lista).

% Listar apenas as notas (números) dadas por um usuário
notas_do_usuario(UsuarioID, Notas) :-
	findall(Nota,
		(avaliacao(UsuarioID, _, Nota)),
		Notas).

% Fase 2: Lógica e Agregação 

% Calcular a média aritmética das notas de um usuário:
media_avaliacoes(UsuarioID, Media) :-
	findall(Nota,
		avaliacao(UsuarioID, _, Nota),
		Notas),
	length(Notas, Quantidade),
	sum_list(Notas, Soma),
	Media is Soma/Quantidade.

% Verificar se um usario é hater:
usuario_hater(UsuarioID) :-
	findall(Nota,
		avaliacao(UsuarioID, _, 1),
		Notas1),
	findall(Nota,
		avaliacao(UsuarioID, _, 5),
		Notas5),
	length(Notas1, Quant1),
	length(Notas5, Quant5),
	Quant1 > Quant5.
	
% Lista de filmes(ID) que o usuario nunca avaliou:
filmes_nao_avaliados(UsuarioID, Filmes) :-
	findall(ID,
		(filme(ID, _, _, _, _), % todos os filmes do catálogo
			 \+ avaliacao(UsuarioID, ID, _)), % retiro(\+) os filmes que avaliou
		Filmes). % guardo na lista Filmes

% Fase 3: Sistema Colaborativo

% Busca se dois usuários são vizinhos se avaliaram o mesmo filme (ID)
%  e ambos deram nota >= 4:



% ____________________
% CHAMADAS AUXILIARES:

% Quando o filme tem um genero unico:
filme_tem_genero(GeneroFilme, GeneroRef) :-
	% verifica o atomo
atom(GeneroFilme),
	GeneroFilme = GeneroRef.
% Quando o filme tem vários gêneros:
filme_tem_genero(GeneroFilme, GeneroRef) :-
	% verifica a lista
is_list(GeneroFilme),
	member(GeneroRef, GeneroFilme).

%filmes acima do Ano de Referencia:    
ano_superior(AnoRef, AnoFilme) :-
	AnoFilme > AnoRef.