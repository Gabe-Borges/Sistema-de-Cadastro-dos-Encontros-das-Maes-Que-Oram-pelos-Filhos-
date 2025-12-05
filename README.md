# Sistema de Cadastro dos Encontros das Mães Que Oram pelos Filhos 
Repositório do trabalho de Linguagem de Programação


Descrição Geral:

"Desenvolva um sistema desktop em Java para auxiliar na organização dos encontros das Mães Que 
Oram pelos Filhos, de uma igreja. 
O sistema deve permitir o cadastro das mães participantes, o gerenciamento dos encontros e serviços, e 
a emissão de relatórios sobre as atividades realizadas."

Projeto feito por Gabriel Borges de Toledo 2°ADS

# Diagrama Conceitual do Banco de Dados
<img width="1733" height="790" alt="Untitled diagram-2025-12-05-012810" src="https://github.com/user-attachments/assets/e6e6e515-e5f8-4a0e-9e71-d24c55f1b95a" />


# Diagrama de Classes UML
<img width="1402" height="1068" alt="Untitled diagram-2025-12-05-012121" src="https://github.com/user-attachments/assets/fc4fb447-26b3-4ffa-93a7-811d50ec29b3" />


# Diagrama Lógico do Banco de Dados 

<img width="2170" height="1188" alt="Untitled diagram-2025-12-05-012328" src="https://github.com/user-attachments/assets/85ea81ca-daed-4a3c-b03a-e568d73f628b" />


# Banco MySQL
```
CREATE DATABASE IF NOT EXISTS maes_que_oram_db;

USE maes_que_oram_db;

CREATE TABLE IF NOT EXISTS MAE (
    id_mae INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(15),
    endereco VARCHAR(200),
    data_aniversario DATE NOT NULL 
);

CREATE TABLE IF NOT EXISTS SERVICO_FIXO (
    id_servico_fixo INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS ENCONTRO (
    id_encontro INT PRIMARY KEY AUTO_INCREMENT,
    data_encontro DATE NOT NULL UNIQUE,
    excluido_logico BOOLEAN DEFAULT FALSE 
);

CREATE TABLE IF NOT EXISTS SERVICO_ENCONTRO (
    id_servico_encontro INT PRIMARY KEY AUTO_INCREMENT,
    id_encontro INT NOT NULL,
    id_servico_fixo INT NOT NULL,
    id_mae_responsavel INT, 
    descricao_atividade VARCHAR(255),
    
    FOREIGN KEY (id_encontro) REFERENCES ENCONTRO(id_encontro),
    FOREIGN KEY (id_servico_fixo) REFERENCES SERVICO_FIXO(id_servico_fixo),
    FOREIGN KEY (id_mae_responsavel) REFERENCES MAE(id_mae),

    UNIQUE KEY uk_encontro_servico (id_encontro, id_servico_fixo)
);

INSERT IGNORE INTO SERVICO_FIXO (nome) VALUES
('MÚSICA'), ('RECEPÇÃO DE MÃES'), ('ACOLHIDA'), ('TERÇO'),
('FORMAÇÃO'), ('MOMENTO ORACIONAL'), ('PROCLAMAÇÃO DA VITÓRIA'),
('SORTEIO DAS FLORES'), ('ENCERRAMENTO'), ('ARRUMAÇÃO CAPELA'),
('QUEIMA DOS PEDIDOS'), ('COMPRAS FLORES');
``` 


# Exemplo de .txt gerado no Relatório de Encontro
```
Relatório do Encontro: 25/12
--------------------------------------------------------------------
Status do Encontro: ATIVO

ESCALA DE SERVIÇOS:
MÚSICA:                   Mirian - Descrição: Samba
RECEPÇÃO DE MÃES:         Mirian
ACOLHIDA:                 Joana
TERÇO:                    Mirian
FORMAÇÃO:                 Marcia
MOMENTO ORACIONAL:        Joana
PROCLAMAÇÃO DA VITÓRIA:   Marcia
SORTEIO DAS FLORES:       Joana - Descrição: 21h30
ENCERRAMENTO:             Joana
ARRUMAÇÃO CAPELA:         Marcia
QUEIMA DOS PEDIDOS:       Mirian
COMPRAS FLORES:           Mirian - Descrição: Rosas
```
