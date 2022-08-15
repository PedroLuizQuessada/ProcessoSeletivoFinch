# ProcessoSeletivoFinch
Automação desenvolvida como parte de um processo seletivo da empresa Finch Soluções.

## Requisitos
O computador onde rodará a automação deve possuir os seguintes itens:<br/>
-Java 8 instalado<br/>
-conexão com a internet durante a execução do programa<br/>
-Google Drive instalado

## Funcionamento:
A automação foi desenvolvida com o intuito de garantir que mesmo uma pessoa sem conhecimento técnico de programação seja capaz de configura-la.
Existem 2 pontos de interação entre o usuário e o programa: o Google Drive e o arquivo de configuração application.properties.

### Google Drive:
É por meio do Google Drive que a automação realiza o controle das pendências.
Existe uma pasta que será compartilhada com o usuário chamada "AutomaçãoFinch". Dentro dela existem três outras pastas chamadas "Pendentes", "Processados" e "Resultados". 
Na "Pendentes" o usuário deverá subir arquivos de Excel (.xls ou .xlsx) com apenas uma planilha em cada. Em cada uma dessas planilhas ele deve informar na primeira coluna, iniciando pela primeira linha, o nome dos produtos que serão pesquisados nas lojas.
Após finalizar a pesquisa de todos os produtos daquela planilha, a automação irá move-la da pasta "Pendentes" para a pasta "Processados", concatenando em seu nome a data e hora da execução desse deslocamento seguindo o formato "dd_MM_yyyy_HH_mm_sss".
Já na pasta de resultados a automação irá subir uma segunda planilha para cada planilha processada. Nela, a primeira coluna conterá o nome dos produtos pesquisados (que foram informados na planilha que o usuário subiu na pasta "Pendentes") e nas outras colunas serão informados os preços mais baixos encontrados para aquele produto. Caso o arquivo que o usuário subiu na "Pendentes" não corresponda a um Excel, a planilha de resultado será gerada com apenas uma célula informando sobre o tipo inválido do arquivo. O nome da planilha com o resultado será igual ao arquivo subido na pasta "Pendentes" concatenado a data e hora do momento sa sua geração seguindo o formato "dd_MM_yyyy_HH_mm_sss".

### Application.properties:
Esse arquivo possui uma série de configurações que serão usadas durante a execução da automação. É por meio dos seguintes parâmetros que o usuário deve informar as respectivas informações (os parâmetros não listados aqui que constam no application.properties são informações que devem ser alteradas apenas pelo administrador da automação):
<br/>-rpa.loja: loja onde os produtos serão pesquisados. Necessário preencher com uma das opções assinaladas dentro do próprio application.properties como válidas
<br/>-rpa.minutos-intervalo-execucao: a automação processará todas as pendências presentes na pasta "Pendentes" de uma única vez. Após finalizar a pesquisa de todos os produtos dessas pendências ela irá aguardar o número de minutos indicado através deste parâmetro para depois realizar uma nova busca por pendências
<br/>-rpa.google-drive-path: path para o root do Google Drive
<br/>-rpa.numero-resultados: número de preços que serão levantados por produto pesquisado
<br/>-driver.navegador: o webdriver que será utilizado durante a execução da automação. Necessário preencher com uma das opções assinaladas dentro do próprio application.properties como válidas
<br/>-driver.chrome-driver-path: path do chromedriver instalado
<br/>-driver.firefox-driver-path: path do geckodriver instalado
<br/>-email: endereço de e-mail que receberá o Excel com o resultado de todas as pendências processadas durante aquela iteração. Cada resultado será uma planilha dentro deste Excel. O usuário pode preencher esse parâmetro com "n" caso não queira receber esses e-mails
<br/>-spring.mail.nome: nome usado como remetente dos e-mails enviados

## Tratativa de erros:
Caso aconteça algum imprevisto durante a execução da automação o webdriver aberto será encerrado, a mensagem da falha será impressa no console, o e-mail informado no parâmetro spring.mail.username do application.properties receberá uma mensagem a respeito da falha ocorrida, surgirá um popup informando a falha ocorrida e a execução do programa será interrompida até que este popup seja fechado.
