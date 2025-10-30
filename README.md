# Economy

É um plugin que cuida do marketplace do servidor usando [PostgreSQL](https://www.postgresql.org/) 
como database principal

## Marketplace
O marketplace funciona como: um jogador cria um anúncio, e coloca o preço em cima de uma quantidade de itens,
o valor dos itens é de livre escolha do jogador dono da `Listing` e outro jogador vai poder comprar dessa `Listing`
usando dinheiro ganho em eventos e guerras.

O mercado muda conforme a **oferta e demanda**, a database salva todas as transações na tabela `transactions`.

## Tabelas
No PostgreSQL temos as tabelas: 
- `itens` onde fica o id serial de cada item do mine,
    **os itens são adicionados a essa tabela conforme a demanda para diminuir a carga na database**
- `players` onde fica o player, **o id dessa table é o UUID do jogador no servidor**
- `transactions` guarda as transações com dinheiro dentro do mercado de itens,
**salva data/hora de quando aconteceu cada transação e faz um historico de tudo ja vendido e anunciado**
- `market_transaction_details` uma entidade fraca da `transactions` que salva qual anuncio que foi comprado e outras coisas.
- `listings` aqui fica guardado os anúncios de itens do mercado, ele vai ser lido e mostrado para o jogador no jogo. 