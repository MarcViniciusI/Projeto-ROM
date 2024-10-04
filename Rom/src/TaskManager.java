
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.awt.FontMetrics;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TaskManager extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private ScheduledExecutorService scheduler;

    public TaskManager() {
        setTitle("Gerenciador de Tarefas Semanais");
        setSize(1200, 400); // Aumentei o tamanho para acomodar melhor as colunas
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Criação da tabela com uma coluna para fotos
        String[] columns = {"Foto", "Personagem", "Cake", "Chaos MVP", "Chaos Mini", "ET", "Oracle", "Corredor",
            "Valhalla", "Purgatory", "Thanatos", "Phantom", "Museu", "Ilha", "PSR", "WSA", "POG"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) {
                    return Icon.class; // Coluna da foto como ícone
                }
                return column == 1 ? String.class : Boolean.class; // Coluna de nome como String, e tarefas como checkbox
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 1; // Permitir edição apenas nas colunas de tarefas
            }
        };
        table = new JTable(model);
        table.setRowHeight(94);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Define larguras preferenciais para as colunas
        table.getColumnModel().getColumn(0).setPreferredWidth(103); // Foto
        table.getColumnModel().getColumn(1).setPreferredWidth(90); // Personagem
        for (int i = 2; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(70); // Tarefas
        }

        // Adiciona a tabela ao painel com rolagem
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
                
        // Chame aqui o método para ajustar a largura das colunas
        adjustColumnWidths(table);

        // Chama o método loadData para carregar os dados
        loadData(); // Carrega os dados ao iniciar

        // Programação para resetar toda segunda-feira às 08:00
        scheduleWeeklyReset();

        // Cria e adiciona o JMenuBar
        createMenuBar();

        // Adiciona um listener para encerrar o scheduler quando a janela for fechada
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (scheduler != null && !scheduler.isShutdown()) {
                    scheduler.shutdown();
                }
                super.windowClosing(e);
            }
        });
        // metodo icone aplicação
        IconUtils.loadIcon(this);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu "Personagem"
        JMenu personagemMenu = new JMenu("Personagem");

        // Item "Adicionar Personagem"
        JMenuItem addPersonagemItem = new JMenuItem("Adicionar Personagem");
        addPersonagemItem.addActionListener(e -> addCharacter());

        // Item "Editar Personagem"
        JMenuItem editPersonagemItem = new JMenuItem("Editar Personagem");
        editPersonagemItem.addActionListener(e -> editCharacter());

        // Item "Excluir Personagem"
        JMenuItem deletePersonagemItem = new JMenuItem("Excluir Personagem");
        deletePersonagemItem.addActionListener(e -> deleteCharacter());

        // Adiciona os itens ao menu "Personagem"
        personagemMenu.add(addPersonagemItem);
        personagemMenu.add(editPersonagemItem);
        personagemMenu.add(deletePersonagemItem);

        // Menu "Salvar"
        JMenuItem saveDataItem = new JMenuItem("Salvar Dados");
        saveDataItem.addActionListener(e -> saveData()); // Chama saveData ao clicar

        // Adiciona os itens ao menu
        menuBar.add(personagemMenu);
        menuBar.add(saveDataItem); // Adiciona o item "Salvar Dados" ao menu

        // Define o JMenuBar no JFrame
        setJMenuBar(menuBar);
    }

    private void addCharacter() {
        String characterName = JOptionPane.showInputDialog(this, "Digite o nome do personagem:");
        if (characterName != null && !characterName.trim().isEmpty()) {
            // Selecione a foto do personagem
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Escolha uma foto");
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String photoPath = selectedFile.getAbsolutePath(); // Armazena o caminho absoluto

                // Carrega a imagem original
                ImageIcon originalIcon = new ImageIcon(photoPath);
                Image originalImage = originalIcon.getImage();

                // Calcula a proporção para ajustar a imagem
                int originalWidth = originalIcon.getIconWidth();
                int originalHeight = originalIcon.getIconHeight();
                double widthRatio = 103.0 / originalWidth;
                double heightRatio = 94.0 / originalHeight;
                double scaleFactor = Math.min(widthRatio, heightRatio); // Mantém a proporção

                // Calcula as novas dimensões, mantendo a proporção
                int newWidth = (int) (originalWidth * scaleFactor);
                int newHeight = (int) (originalHeight * scaleFactor);

                // Redimensiona a imagem mantendo a proporção
                Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);

                // Centraliza a imagem dentro do espaço de 103x94
                ImageIcon centeredIcon = createCenteredIcon(scaledIcon, 103, 94);
                centeredIcon.setDescription(photoPath); // Define o caminho da foto

                Object[] newRow = new Object[table.getColumnCount()];
                newRow[0] = centeredIcon; // Adiciona a imagem centralizada à primeira coluna
                newRow[1] = characterName.trim();
                for (int i = 2; i < newRow.length; i++) {
                    newRow[i] = false; // Inicializa as tarefas como não concluídas
                }
                model.addRow(newRow);
            }
        }
    }

    private void saveData() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Para formatar o JSON de forma legível

        // Lista para armazenar as informações dos personagens
        List<CharacterInfo> characterInfos = new ArrayList<>();
        // Lista para armazenar as informações das tarefas
        List<TaskInfo> taskInfos = new ArrayList<>();

        // Percorre cada linha da tabela
        for (int row = 0; row < model.getRowCount(); row++) {
            // Pega o nome do personagem da tabela
            String characterName = (String) model.getValueAt(row, 1);

            // Pega o caminho da foto (para simplificar, vamos usar o caminho no disco)
            Icon icon = (Icon) model.getValueAt(row, 0);
            String photoPath = icon instanceof ImageIcon ? ((ImageIcon) icon).getDescription() : "";

            // Cria um novo objeto CharacterInfo e adiciona à lista
            characterInfos.add(new CharacterInfo(characterName, photoPath));

            // Pega o status das tarefas (checkboxes)
            boolean[] tasks = new boolean[model.getColumnCount() - 2]; // Exclui as colunas de foto e nome
            for (int col = 2; col < model.getColumnCount(); col++) {
                tasks[col - 2] = (boolean) model.getValueAt(row, col);
            }

            // Cria um novo objeto TaskInfo e adiciona à lista
            taskInfos.add(new TaskInfo(characterName, tasks));
        }

        // Salva as informações dos personagens no arquivo personagens.json
        try (FileWriter writer = new FileWriter("personagens.json")) {
            gson.toJson(characterInfos, writer); // Converte a lista de personagens para JSON e salva
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Salva as informações das tarefas no arquivo tarefas.json
        try (FileWriter writer = new FileWriter("tarefas.json")) {
            gson.toJson(taskInfos, writer); // Converte a lista de tarefas para JSON e salva
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Confirmação de salvamento
        JOptionPane.showMessageDialog(this, "Dados salvos com sucesso!", "Confirmação", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadData() {
        Gson gson = new Gson();

        // Carregar personagens
        try (FileReader reader = new FileReader("personagens.json")) {
            CharacterInfo[] characters = gson.fromJson(reader, CharacterInfo[].class);
            for (CharacterInfo character : characters) {
                ImageIcon icon = new ImageIcon(character.getPhotoPath());
                if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
                    JOptionPane.showMessageDialog(this, "Não foi possível carregar a imagem: " + character.getPhotoPath(), "Erro de Imagem", JOptionPane.ERROR_MESSAGE);
                    continue; // Ignora este personagem se a imagem não puder ser carregada
                }
                Object[] rowData = new Object[table.getColumnCount()];
                rowData[0] = icon;
                rowData[1] = character.getName();
                for (int i = 2; i < rowData.length; i++) {
                    rowData[i] = false; // Inicializa tarefas como não concluídas
                }
                model.addRow(rowData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Carregar tarefas
        try (FileReader reader = new FileReader("tarefas.json")) {
            TaskInfo[] tasks = gson.fromJson(reader, TaskInfo[].class);
            for (TaskInfo task : tasks) {
                for (int row = 0; row < model.getRowCount(); row++) {
                    if (model.getValueAt(row, 1).equals(task.getCharacterName())) {
                        for (int col = 2; col < model.getColumnCount(); col++) {
                            model.setValueAt(task.getTasks()[col - 2], row, col);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void adjustColumnWidths(JTable table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            // Obter o título da coluna
            String columnTitle = table.getColumnName(i);

            // Obter o FontMetrics do JTable para medir a largura do título
            FontMetrics metrics = table.getFontMetrics(table.getFont());

            // Medir a largura do título
            int titleWidth = metrics.stringWidth(columnTitle);

            // Adicionar uma margem para que o texto não fique muito próximo das bordas
            int padding = 10;

            // Obter a largura preferida da coluna atual, baseada no conteúdo (se houver)
            int preferredWidth = Math.max(titleWidth + padding, table.getColumnModel().getColumn(i).getPreferredWidth());

            // Definir a largura preferida da coluna
            table.getColumnModel().getColumn(i).setPreferredWidth(preferredWidth);
        }
    }

    /**
     * Centraliza a imagem redimensionada dentro das dimensões especificadas,
     * adicionando espaços vazios conforme necessário.
     *
     * @param icon O ImageIcon redimensionado.
     * @param width A largura total disponível.
     * @param height A altura total disponível.
     * @return Um novo ImageIcon com a imagem centralizada.
     */
    private ImageIcon createCenteredIcon(ImageIcon icon, int width, int height) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufferedImage.createGraphics();

        // Preenche o fundo com transparente
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, width, height);
        g2.setComposite(AlphaComposite.SrcOver);

        // Calcula a posição para centralizar a imagem
        int x = (width - icon.getIconWidth()) / 2;
        int y = (height - icon.getIconHeight()) / 2;

        // Desenha a imagem
        g2.drawImage(icon.getImage(), x, y, null);
        g2.dispose();

        return new ImageIcon(bufferedImage);
    }

    private void editCharacter() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String currentCharacterName = (String) model.getValueAt(selectedRow, 1);
            String newCharacterName = JOptionPane.showInputDialog(this, "Edite o nome do personagem:", currentCharacterName);
            if (newCharacterName != null && !newCharacterName.trim().isEmpty()) {
                model.setValueAt(newCharacterName.trim(), selectedRow, 1); // Atualiza o nome do personagem
            }

            int changePhoto = JOptionPane.showConfirmDialog(this, "Deseja alterar a foto?", "Alterar Foto", JOptionPane.YES_NO_OPTION);
            if (changePhoto == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Escolha uma nova foto");
                int result = fileChooser.showOpenDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String photoPath = selectedFile.getAbsolutePath(); // Armazena o caminho absoluto

                    // Carrega a imagem original
                    ImageIcon originalIcon = new ImageIcon(photoPath);
                    Image originalImage = originalIcon.getImage();

                    // Calcula a proporção para ajustar a imagem
                    int originalWidth = originalIcon.getIconWidth();
                    int originalHeight = originalIcon.getIconHeight();
                    double widthRatio = 103.0 / originalWidth;
                    double heightRatio = 94.0 / originalHeight;
                    double scaleFactor = Math.min(widthRatio, heightRatio); // Mantém a proporção

                    // Calcula as novas dimensões
                    int newWidth = (int) (originalWidth * scaleFactor);
                    int newHeight = (int) (originalHeight * scaleFactor);

                    // Redimensiona a imagem
                    Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);

                    // Centraliza a imagem dentro do espaço de 103x94
                    ImageIcon centeredIcon = createCenteredIcon(scaledIcon, 103, 94);
                    centeredIcon.setDescription(photoPath); // Define o caminho da foto

                    // Atualiza a foto na tabela
                    model.setValueAt(centeredIcon, selectedRow, 0);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um personagem para editar!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void deleteCharacter() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este personagem?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                model.removeRow(selectedRow); // Remove o personagem selecionado
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um personagem para excluir.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void scheduleWeeklyReset() {
        scheduler = Executors.newScheduledThreadPool(1);

        long delay = calculateInitialDelay();
        long period = TimeUnit.DAYS.toMillis(7); // A cada 7 dias

        scheduler.scheduleAtFixedRate(this::resetTasks, delay, period, TimeUnit.MILLISECONDS);
    }

    private long calculateInitialDelay() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nextMondayAtEight = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))
                .withHour(8).withMinute(0).withSecond(0).withNano(0);

        if (now.compareTo(nextMondayAtEight) > 0) {
            nextMondayAtEight = nextMondayAtEight.plusWeeks(1);
        }

        Duration duration = Duration.between(now, nextMondayAtEight);
        return duration.toMillis();
    }

    private void resetTasks() {
        SwingUtilities.invokeLater(() -> {
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 2; col < model.getColumnCount(); col++) {
                    model.setValueAt(false, row, col); // Marca todas as tarefas como não concluídas
                }
            }
            JOptionPane.showMessageDialog(this, "Tarefas resetadas para a semana!", "Reset Completo", JOptionPane.INFORMATION_MESSAGE);
        });
    }

          
    public static void main(String[] args) {
        // Configura o look and feel para o sistema operacional
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Se falhar, mantém o look and feel padrão
        }

        SwingUtilities.invokeLater(() -> {
            TaskManager manager = new TaskManager();
            manager.setVisible(true);
        });
    }
}
