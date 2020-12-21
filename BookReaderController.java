package WordCounter;

import java.awt.*;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.*;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

public class BookReaderController {
    private SortedListModel listModel;
    private JList<SortedListModel> listView;

    JScrollPane scrollPane;
    JFrame frame;
    Container pane;

    public BookReaderController() {
        SwingUtilities.invokeLater(() -> createWindow("BookReader", 1000, 500));
    }

    private void createWindow(String title, int width, int height) {

        frame = new JFrame(title);
        pane = frame.getContentPane();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        pane.add(createRadioButtons(), BorderLayout.CENTER);
        pane.add(createSearchField(frame), BorderLayout.SOUTH);
        

        frame.pack();
        frame.setVisible(true);
    }

    private void createLists(GeneralWordCounter counter, JFrame frame) {
        frame.setVisible(false);
        listModel = new SortedListModel(counter.getWordList());
        listView = new JList<SortedListModel>(listModel);
        listView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        scrollPane = new JScrollPane(listView, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(1000, 500));
        pane.add(scrollPane, BorderLayout.NORTH);

        listView.revalidate();
    }

    private JPanel createRadioButtons() {
        JRadioButton sortAlphabetical = new JRadioButton("Alphabetically sorted");
        JRadioButton sortFrequency = new JRadioButton("Frequency sorted");

        sortAlphabetical.setSelected(true);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sortAlphabetical);
        buttonPanel.add(sortFrequency);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(sortAlphabetical);
        buttonGroup.add(sortFrequency);

        sortAlphabetical.addActionListener(e -> listModel.sort(
                (x, y) -> ((Entry<String, Integer>) x).getKey().compareTo(((Entry<String, Integer>) y).getKey())));
        sortFrequency.addActionListener(e -> listModel
                .sort((x, y) -> -(((Entry<String, Integer>) x).getValue() - ((Entry<String, Integer>) y).getValue())));

        return buttonPanel;
    }

    private JPanel createSearchField(JFrame frame) {
        JButton btnBrowse = new JButton("Browse");
        JButton searchButton = new JButton("Find");
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(500, (int) searchButton.getPreferredSize().getHeight()));

        JPanel searchPanel = new JPanel();
        searchPanel.add(searchButton);
        searchPanel.add(searchField);
        searchPanel.add(btnBrowse);
        frame.getRootPane().setDefaultButton(searchButton);

        searchButton.addActionListener(e -> {
            String searchedKey = searchField.getText().toLowerCase().trim();
            boolean found = false;

            for (int i = 0; i < listModel.getSize(); i++) {
                String currentKey = ((Entry<String, Integer>) listModel.getElementAt(i)).getKey();

                if (currentKey.equals(searchedKey)) {
                    listView.setSelectedIndex(i);
                    listView.ensureIndexIsVisible(i);
                    found = true;
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(frame, "Word not found");
            }
        });

        btnBrowse.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Select the text file whose words you want to count");
            JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView());
            j.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt", "text");
            j.setFileFilter(filter);
            j.showOpenDialog(null);
            String textFile = j.getSelectedFile().getAbsolutePath();
            JOptionPane.showMessageDialog(frame, "Select the text file with words you want to exclude");
            j.showOpenDialog(null);
            String bannedFile = j.getSelectedFile().getAbsolutePath();

            try {
                createWordCounters(textFile, bannedFile, frame);
            } catch (FileNotFoundException e1) {
                JOptionPane.showMessageDialog(frame, "Files deleted or moved");
            }
        });

        return searchPanel;
    }

    private void createWordCounters(String textFile, String bannedFile, JFrame frame) throws FileNotFoundException {
        Scanner s = new Scanner(new File(textFile));
		Scanner s2 = new Scanner(new File(bannedFile));

		Set<String> banned = new HashSet<String>();
		
		while(s2.hasNext()) {
			String word = s2.next().toLowerCase();		
            banned.add(word);
		}
		
		GeneralWordCounter wordCount = new GeneralWordCounter(banned);
		
		s.findWithinHorizon("\uFEFF", 1);
		s.useDelimiter("(\\s|,|\\.|:|;|!|\\?|'|\\\")+");

		while (s.hasNext()) {
			String word = s.next().toLowerCase();	
            wordCount.process(word);
		}

		s.close();
        s2.close();
        
        createLists(wordCount, frame);
    }
}

