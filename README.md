# Meus Gastos — controle de despesas com leitura de cupom fiscal

Projeto Android nativo (Kotlin + Jetpack Compose) para controle completo de gastos pessoais,
com leitura automática de cupons e notas fiscais via OCR no próprio aparelho.

## Como abrir

1. Instale o **Android Studio** (versão Koala ou mais recente).
2. `File > Open` e selecione a pasta `MeusGastos`.
3. Deixe o Gradle sincronizar (baixa as dependências automaticamente).
4. Rode em um emulador ou aparelho físico com Android 8.0 (API 26) ou superior.

## Arquitetura

- **MVVM**: `ExpenseViewModel` expõe `StateFlow`s consumidos direto pelas telas Compose.
- **Room** (`AppDatabase`): banco local SQLite — nenhum dado sai do aparelho. Categorias padrão
  (Alimentação, Transporte, Moradia...) são criadas na primeira execução e podem ser editadas
  livremente; só não podem ser excluídas, para evitar gastos "órfãos".
- **CameraX + ML Kit Text Recognition** (`ReceiptScanner`): a foto do cupom é processada
  inteiramente no aparelho — sem enviar imagem a nenhum servidor, sem custo por leitura e
  funcionando offline. A extração usa expressões regulares ajustadas ao padrão de cupons
  fiscais brasileiros (CF-e/NFC-e): procura por "VALOR TOTAL", datas em `dd/mm/aaaa` e o nome
  do estabelecimento na primeira linha "cheia" do texto.
- **Vico** (`ReportsScreen`): biblioteca de gráficos nativa para Compose — evolução diária em
  barras e distribuição por categoria em lista com barra de progresso.

## Sobre a confiabilidade do OCR

Cupom térmico desbotado ou fotografado em ângulo reduz a precisão do reconhecimento de texto.
Por isso o app **nunca salva automaticamente** o que foi lido: os campos vêm pré-preenchidos,
mas o usuário sempre confere e corrige antes de salvar. Um indicador de confiança (alta/parcial/
baixa) sinaliza quando vale a pena checar com mais atenção. O texto bruto do OCR fica salvo
junto ao gasto (`rawOcrText`), então é possível reprocessar ou conferir depois se algo parecer
errado.

## O que falta implementar (próximos passos sugeridos, em ordem de prioridade)

1. **Captura de foto real**: hoje o botão da câmera está com um placeholder — falta criar o
   arquivo temporário via `FileProvider` e passar a `Uri` para o `TakePicture` launcher.
2. **DatePicker** na tela de novo gasto (campo já existe, só falta o diálogo).
3. **Edição/exclusão de gasto individual** a partir da lista da tela inicial.
4. **Filtro de período nos relatórios** (hoje fixo no mês corrente — trocar por seletor
   semana/mês/personalizado).
5. **Exportação** dos relatórios (CSV ou PDF) — útil para levar dados a uma planilha, já que
   você trabalha bastante com Sheets/Excel nos outros projetos.
6. **Backup/restauração** do banco local (ex.: exportar `.db` para o Google Drive).

## Dependências principais

| Biblioteca | Função |
|---|---|
| Jetpack Compose + Material 3 | Interface |
| Room | Persistência local |
| CameraX | Captura de foto do cupom |
| ML Kit Text Recognition | OCR no aparelho |
| Vico | Gráficos |
| Navigation Compose | Navegação entre telas |
