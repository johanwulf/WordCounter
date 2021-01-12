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


    // Maybe this method?
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
}

public E remove(int index) {
    if(index < 0 || index > size) {
        throw new IndexOutOfBoundsException();
    }

    if(index == 0) {
        E e = first.element;
        first = first.next;
        return e;
    }

    Node<E> n = first;

    for(int i = 0; i < index-1; i++) {
        n = n.next;
    }

    E e = n.next.element;
    n.next = n.next.next;
    return e;
}

// Invertera vektor
public static <E> void reverse(E[] a) {
    reverse(0, a.length-1, a);
}

private E[] reverse(int start, int end, E[] a) {
    if(start < end) {
        swap(start, end, a);
        reverse(start+1, end-1, a);
    }
}

private E[] swap(int index1, int index2, E[] a) {
    E temp = a[index1];
    a[index1] = a[index2];
    a[index2] = temp;
    return a;
}

// Jämföra med/utan comparator
private int compareElements(E e1, E e2) {
    if (comparator == null) {
        return ((Comparable<E>) e1).compareTo(e2);
    } else {
        return comparator.compare(e1, e2);
    }
}

// Enkellänkad lsita
public void clear() {
    first = null;
    last = null;
    size = 0;
}

public void drop(int n) {
    if (n >= size) {
        clear();
    } else {
        for (int i = 0; i < n; i++) {
            first = first.next;
        }
        size = size - n;
    }
}

// Returnera lista med löv från binärt träd
public List<E> leaves() {
    List<E> list = new ArrayList();
    leaves(root, list);
    return list;
}

public void leaves(Node<E> n, List<E> list) {
    if (n == null) {
        return;
    } else if (n.left == null && n.right == null) {
        list.add(n.element);
    } else {
        leaves(n.left, list);
        leaves(n.right, list);
    }
}

/**
 * 1a) Falskt - eftersom att det minsta talet sätts i roten så kommer inget hamna på vänster sida av trädet vilket gör det obalanserat
 * 1b) Sant - tidskomplexiteten för denna algoritmen är kvadratisk. Det innebär att en ökning med 5 gånger av n gör att tiden blir 5 i kvadrat - alltså 25 gånger längre. 4*25 = 100.
 * 1c) Sant - bara man sätter in elementen sist i vektorn och håller koll på antalet insatta element så är de möjligt
 * 1d) Falskt - när man söker i en enkellänkad lista måste alla element gås genom vilket gör att binär sökning ej passar bra.
 * 1e) Falskt - heapsort delar inte upp problemet i mindre delar och slår ihop resultaten vilket är kännetecknande för en divide and conquer algoritm
 * 1f) Fel svar :(
 * */

// 2a)
private void add(E e) {
    ListNode<E> temp = new ListNode<E>(e);
    ListNode<E> current = first;
    ListNode<E> previous = null;

    if (first == null) {
        first = temp;
    }

    while(e.compareTo(current.element) < 0) {
        previous = current;
        current = current.next;
    }

    previous.next = temp;
    temp.next = current;
}

// 2b)
private E peek() {
    if(first == null) {
        return null;
    } else {
        return first.element;
    }
}

private E poll() {
    if (first == null) {
        return null;
    } else {
        E temp = first.element;
        first = first.next;
        return temp;
    }
}

/**
 * 2c) Minheap, barnen ska vara >= föräldrern (binärt träd). Lagras i en vektor efter plats i trädet. 
 */

// 3a
public class MapSparseVector implements SparseVector {
    int length;
    Map<Integer, Double> map;

    /**
     * Creates a sparse vector with length elements, all with the value zero.
     * @param length number of elements
     */
    public MapSparseVector(int length) {
        this.map = new HashMap<Integer, Double>();
        this.length = length;
    }

    /** Puts val in element with index i.
     * @param i index
     * @param val the new value
     * @throws IllegalArgumentException if i < 0 or >= the length of this vector
     */
    public void put(int i, double val) {
        if(i < 0 || i >= length) {
            throw new IllegalArgumentException();
        }
        if(val == 0) {
            map.remove(i);
        } else {
            map.put(i, val);
        }
    }

    /**
     * Gets the value in the element with index i.
     * @param i index
     * @return the value in the element with index i
     * @throws IllegalArgumentException if i < 0 or >= the length of this vector
     */
    public double get(int i) {
        if(i < 0 || i >= length) {
            throw new IllegalArgumentException();
        }

        if(map.containsKey(i)) {
            return map.get(i);
        } else {
            return 0.0;
        }
    }

    /**
     * Returns the length of this vector.
     * @return the length of this vector
     */
    public int length() {
        return length;
    }

    /**
     * Computes the dot product of this vector and v.
     * @param v the other vector
     * @return the dot product
     * @throws IllegalArgumentException if the two vectors have different length
     */
    public double dot(SparseVector v) {
        if (v.length() != length) {
            throw new IllegalArgumentException();
        }
        
        double sum = 0.0;

        for(int i = 0; i < length; i++) {
            sum += this.get(i)*v.get(i);
        }

        return sum;
    }
}