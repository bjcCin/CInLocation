![CInLocation](https://github.com/bjcCin/CInLocation/blob/master/imagens/logo.jpg?raw=true)
# Memória

Fizemos alguns esforços afim de dimunir o custo de memória, como foi mostrado no relatório principal. Esses esforços resultaram em alguns bons resultados de uso de memória.

1. Ao abrir o app, é consumido quase **90MB** de memória RAM, divididos como mostra a imagem abaixo. Acreditamos que boa parte desse custo, é na busca e exibição das imagens dos cards do Recycle View.
2. Assim que é estabilizado esse custo de carregamento inicial dos dados, a aplicação parece se comportar com um bom custo de memória, **61MB**.
3. Devido aos efeitos visuais, como o collapse toolbar, e zoom in do Google Maps nossa tela de cadastro é a segunda mais pesada da aplicação. Ela começa consumindo cerca de **190MB** e estabilando em **170MB**. Detalhe é que ao receber a localização setada no maps, a memória volta a ter um pico alto de 190MB, se estabilizando novamente em seguida.
4. Como as telas de cadastro e edição são muito semelhantes, elas possuem consumo de memória parecidos, porém nos nossos testes a tela de edição se estabilizou em 140MB. Acreditamos que isso se deve ao fato dela ser uma Activity a parte enquanto que a de adição é um Fragment com opções compartilhadas com outras telas como Bottom Navegation.
5. O Fragment de lembretes completados é semelhante ao da Home, apenas com a opçao de deletar o lembrete a mais. A variação de consumo de memória entre as duas nos nossos testes foi bem ínfima.
