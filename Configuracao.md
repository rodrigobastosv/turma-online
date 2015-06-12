<h1>BAIXANDO E EXECUTANDO A APLICAÇÃO</h1>

Descreveremos a seguir como baixar o código fonte, instalar o NetBeans IDE e como configurar o ambiente de desenvolvimento para utilizar o projeto Turma Online.

> Várias instalações do NetBeans IDE podem coexistir no mesmo sistema. Mais informações poderão ser obtidas no site netbeans.org. Para utilizar o Netbeans IDE é necessário primeiro ter instalado a versão mais recente do JDK (Java SE Development Kit).

<h3>Instalando o NetBeans</h3>
•	Na página de downloads do NetBeans (netbeans.org/downloads) baixe execute a distribuição JEE, esta opção inclui o Apache Tomcat, mas não é instalado por default. <br />
•	Ao iniciar o instalador marque a caixa de seleção “Apache Tomcat” na página de Boas-Vindas e clique em Próximo. <br />
•	Nas próximas páginas, aceite os termos de licença e clique em próximo até a página de instalação do NetBeans. <br />
•	Na página de instalação do NetBeans informe um diretório e nome sem caracteres de espaço para a pasta NetBeans (ex: c:\java\netbeans-7.2). <br />
•	O mesmo deve ser feito na página de instalação do Tomcat, por exemplo, c:\java\tomcat-7.0.27. Em ambos, o diretório deve estar vazio e com permissões de leitura e gravação. <br />
•	Na página de resumo clique em Instalar para iniciar a instalação. Após a instalação clique em Finalizar e execute o NetBeans. <br />

<h3>Baixando o projeto Turma Online</h3>
Turma Online é um projeto de uma aplicação java web pronto para ser utilizado que contem as dependências (jars) e configurações (web.xml e faces-config.xml) necessárias do framework Entities. Está disponível somente uma versão para a IDE Netbeans (TurmaOnline). Para instalar o projeto TurmaOnline na IDE Netbeans siga os seguintes passos: <br />
•	No NetBeans IDE, selecione Equipe > Subversion > Efetuar Check-out no menu principal. O assistente de Check-out é aberto. <br />
•	No primeiro painel do assistente, insira a URL: https://turma-online.googlecode.com/svn/TurmaOnline. Caso esteja usando um Proxy, certifique-se de clicar no botão Configuração de Proxy e insira as informações solicitadas. Quando tiver certeza de que suas definições de conexão estão corretas, clique em Próximo. <br />
•	No painel “Pastas para Check-out” do assistente, especifique “TurmaOnline” no campo “Pasta(s) do Repositório”. No campo “Pasta Local” informe c:\java, por exemplo, e clique em “Finalizar” para iniciar o download. <br />
•	O IDE exibi os arquivos que estão sendo baixados na janela Saída (Ctrl-4) e a barra de status indica o andamento do download. <br />
•	Após o download do projeto, uma caixa de diálogo será exibida solicitando a abertura do projeto. Clique em “Abrir Projeto”. <br />

<h3>Configurando o Tomcat</h3>
•	Na janela Serviços do NetBeans, clique com o botão direito do mouse no nó “Servidores|Apache Tomcat” e escolha “Propriedades”. Acesse a aba plataforma e digite “-XX:MaxPermSize=512m -Xmx950m” em “Opções da VM” e clique em Fechar. <br />

<h3>Iniciando o servidor Java DB</h3>
•	O projeto Entities-Blank, por conveniência, é pré-configurado para utilizar o banco de dados JavaDB que vem embutido na plataforma Java. Para iniciar o servidor JavaDB na janela Serviços, clique com o botão direito do mouse no nó “Banco de Dados | Java DB” e escolha “Iniciar Servidor”. <br />

<h3>Configurando outros bancos de dados</h3>
•	Para utilizar outro BD basta adicionar o driver JDBC ao projeto, criar apenas o database (sem tabelas,...) e configurar o arquivo META-INF/context.xml. Abaixo temos um exemplo para o banco de dados Postgres. <br />

```
25. <Resource auth="Container" type="javax.sql.DataSource"  <br/>
26. driverClassName="org.postgresql.Driver"  <br/>
27. name="jdbc/ds-blank"  <br/>
28. username="postgres" password="postgres"  <br/>
29. url="jdbc:postgresql://localhost:5433/postgres"/>  <br/>
```


<h3>Configurando múltiplos bancos de dados</h3>
O Entities suporta o uso de múltiplas bases de dados ao mesmo tempo, neste caso, deve-se criar um arquivo de configuração do Hibernate para cada base de dados no pacote default com o mapeamento de suas respectivas classes de entidades. O framework Entities carregará automaticamente todos os arquivos de extensão “.hibernate.cfg.xml” que estejam no pacote padrão.