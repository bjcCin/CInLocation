![CInLocation](https://github.com/bjcCin/CInLocation/blob/master/imagens/logo.jpg?raw=true)
# Testes

Como adicional, alguns testes foram escritos para assegurar que o cadastro dos lembretes no banco estavam funcionando da forma adequada:

![TesteInsert](imagens/teste_insert.png)
- Teste de inserção: Insere um lembrete no banco e depois checa que existe um lembrete com mesmo título no banco de dados (seguro pois o banco estará vazio).

![TesteUpdate](imagens/teste_update.png)
- Teste de atualização: Insere um lembrete no banco e depois altera seu título e checa se existe um lembrete no banco com o novo título.

![TesteDelete](imagens/teste_delete.png)
- Teste de remoção: Insere um lembrete no bancoe guarda o id, remove e checa se nenhuma entidade é retornada quando consultado pelo id.
